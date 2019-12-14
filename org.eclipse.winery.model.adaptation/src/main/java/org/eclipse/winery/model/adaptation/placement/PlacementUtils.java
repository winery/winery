/********************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

package org.eclipse.winery.model.adaptation.placement;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.model.version.VersionSupport;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.splitting.Splitting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.model.adaptation.placement.Constants.NODE_TEMPLATE_PROPERTY_DATA_FACTOR;
import static org.eclipse.winery.model.adaptation.placement.Constants.NODE_TEMPLATE_PROPERTY_DATA_SIZE;
import static org.eclipse.winery.model.adaptation.placement.Constants.RELATIONSHIP_TEMPLATE_TRANSFER_TYPE_PULL;
import static org.eclipse.winery.model.adaptation.placement.Constants.SERVICE_TEMPLATE_GROUP;
import static org.eclipse.winery.model.adaptation.placement.Constants.TAG_NAME_LOCATION;
import static org.eclipse.winery.model.adaptation.placement.Constants.TAG_NAME_PROVIDER;
import static org.eclipse.winery.model.tosca.utils.ModelUtilities.QNAME_LOCATION;
import static org.eclipse.winery.repository.targetallocation.util.AllocationUtils.deepcopy;

/**
 * This class exposes utility functions which group components of an incomplete topology together based on the data flow
 * between them and assign them to a suited provider using requirements like privacy.
 */
public class PlacementUtils {

    private static final Predicate<TNodeTemplate> hasLocation =
        nodeTemplate -> Objects.nonNull(nodeTemplate.getOtherAttributes().get(ModelUtilities.NODE_TEMPLATE_REGION));

    private static final Predicate<TRelationshipTemplate> notPullingRelation =
        relation -> Objects
            .isNull(relation.getOtherAttributes().get(ModelUtilities.RELATIONSHIP_TEMPLATE_TRANSFER_TYPE))
            || !relation.getOtherAttributes().get(ModelUtilities.RELATIONSHIP_TEMPLATE_TRANSFER_TYPE)
            .equals(RELATIONSHIP_TEMPLATE_TRANSFER_TYPE_PULL);

    private static final Predicate<TRelationshipTemplate> pullingRelation =
        relation -> Objects
            .nonNull(relation.getOtherAttributes().get(ModelUtilities.RELATIONSHIP_TEMPLATE_TRANSFER_TYPE))
            && relation.getOtherAttributes().get(ModelUtilities.RELATIONSHIP_TEMPLATE_TRANSFER_TYPE)
            .equals(RELATIONSHIP_TEMPLATE_TRANSFER_TYPE_PULL);

    private static final Logger LOGGER = LoggerFactory.getLogger(PlacementUtils.class);

    /**
     * This method groups the components of the given incomplete topology together based on the data flow between them,
     * the data size of the contained data sources and the data factor of the processing components. Afterwards, they
     * are placed on a concrete provider based on defined requirements and the topology is completed for later
     * deployment.
     *
     * @param serviceTemplateId the ID of the ServiceTemplate containing this TopologyTemplate
     * @param topology          the TopologyTemplate to place and complete
     * @return the complete topology with an improved data flow between the components
     */
    public static TTopologyTemplate groupAndPlaceComponents(ServiceTemplateId serviceTemplateId,
                                                            TTopologyTemplate topology) {
        // input TopologyTemplate to reset if the completion fails
        TTopologyTemplate topologyBackup = deepcopy(topology, false);

        // initialize the black list for each NodeTemplate of the TopologyTemplate
        Map<String, List<String>> blackList = new HashMap<>();
        for (TNodeTemplate node : topology.getNodeTemplates()) {
            blackList.put(node.getId(), new ArrayList<>());
        }

        TTopologyTemplate completedTopology = null;
        while (Objects.isNull(completedTopology)) {
            topology = deepcopy(topologyBackup, false);
            assignToLocation(topology, blackList);
            assignToProviders(topology, blackList);
            completedTopology = completeModel(serviceTemplateId, topology, blackList);
        }
        return completedTopology;
    }

    /**
     * Completes the model based on the defined provider assignments and returns a completed ServiceTemplate if the
     * completion was successful. Otherwise, the ServiceTemplate with cleaned location and provider assignments is
     * returned. Additionally, the providers that led to the error are added to the black lists of the components.
     *
     * @param serviceTemplateId the ID of the ServiceTemplate to complete
     * @param topology          the incomplete TopologyTemplate to complete
     * @param blackList         the black list containing the NodeTemplates with the providers that are not usable for
     *                          them
     * @return the completed TopologyTemplate if completion is successful, the cleared and blacklisted TopologyTemplate
     * otherwise
     */
    private static TTopologyTemplate completeModel(ServiceTemplateId serviceTemplateId, TTopologyTemplate topology,
                                                   Map<String, List<String>> blackList) {
        Splitting splitting = new Splitting();
        IRepository repo = RepositoryFactory.getRepository();

        try {
            // create new temporary ServiceTemplate as working copy
            ServiceTemplateId placementId = new ServiceTemplateId(serviceTemplateId.getNamespace().getDecoded(),
                VersionSupport.getNewComponentVersionId(serviceTemplateId, "placement"), false);
            repo.forceDelete(placementId);
            TServiceTemplate placementServiceTemplate = new TServiceTemplate();
            placementServiceTemplate.setTargetNamespace(serviceTemplateId.getNamespace().getDecoded());
            placementServiceTemplate.setTopologyTemplate(topology);
            placementServiceTemplate.setName(placementId.getXmlId().getDecoded());
            placementServiceTemplate.setId(placementServiceTemplate.getName());

            // resolve open requirements until the topology is completed
            while (!splitting.getOpenRequirements(topology).isEmpty()) {
                // add a target label to the topology based on the provider and location assignment
                assignNodesToTargetLabels(topology);
                placementServiceTemplate.setTopologyTemplate(topology);
                repo.setElement(placementId, placementServiceTemplate);

                // complete next level of requirements
                ServiceTemplateId newServiceTemplateId = splitting.matchTopologyOfServiceTemplate(placementId);
                topology = repo.getElement(newServiceTemplateId).getTopologyTemplate();

                // delete intermediate result to avoid cluttering
                repo.forceDelete(placementId);
                placementId = newServiceTemplateId;
            }
            repo.forceDelete(placementId);

            // returned completed topology
            return topology;
        } catch (Exception e) {
            LOGGER.debug("Exception while completing topology: {}", e.getMessage());
            return topology;
        }
    }

    /**
     * Sets a target label for the NodeTemplates of the given TopologyTemplate based on the assigned providers and
     * locations. The infrastructure components that are connected by hostedOn RelationshipTemplates are resolved
     * recursively and assigned to the same target label as the top level component.
     *
     * @param topology the TopologyTemplate to set target labels
     */
    private static void assignNodesToTargetLabels(TTopologyTemplate topology) {
        for (TNodeTemplate node : getNodeTemplatesWithLocation(topology)) {
            String targetLabel = getProvider(node) + "/" + getLocation(node);
            node.getOtherAttributes().put(QNAME_LOCATION, targetLabel);
            assignTargetLabelsRecursively(topology, node, targetLabel);
        }
    }

    /**
     * Assign all NodeTemplates which are transitively connected to the given NodeTemplates by hostedOn
     * RelationshipTemplates to the given target label.
     *
     * @param topology    the TopologyTemplate containing the NodeTemplates to assign
     * @param node        the NodeTemplate which is the starting point to assign all transitively 'hostedOn'-connected
     *                    NodeTemplates the given target label
     * @param targetLabel the target label to assign
     */
    private static void assignTargetLabelsRecursively(TTopologyTemplate topology, TNodeTemplate node, String targetLabel) {
        for (TRelationshipTemplate relation : ModelUtilities.getOutgoingRelationshipTemplates(topology, node)) {
            if (relation.getType().equals(ToscaBaseTypes.hostedOnRelationshipType)) {
                TNodeTemplate hostedOnNode = ModelUtilities
                    .getTargetNodeTemplateOfRelationshipTemplate(topology, relation);
                hostedOnNode.getOtherAttributes().put(QNAME_LOCATION, targetLabel);
                assignTargetLabelsRecursively(topology, hostedOnNode, targetLabel);
            }
        }
    }

    /**
     * Assigns a provider to each component of the given TopologyTemplate based on requirements like privacy. As many
     * components as possible are assigned to the same provider in a location to profit from fast connections in the
     * same data center or possible discounts.
     *
     * @param topology  the incomplete TopologyTemplate to assign providers to the components
     * @param blackList the black list containing the NodeTemplates with the providers that are not usable for them
     */
    private static void assignToProviders(TTopologyTemplate topology, Map<String, List<String>> blackList) {

        Set<String> locations = getAllLocations(topology);
        for (String location : locations) {
            LOGGER.debug("Trying to assign components for location {} to suited provider.", location);

            // maps providers to the NodeTemplates that are supported
            HashMap<String, List<TNodeTemplate>> providers = new HashMap<>();

            // all nodes that are assigned to the current location and are not yet assigned to a provider
            List<TNodeTemplate> nodesForLocation = getNodeTemplatesWithLocation(topology).stream()
                .filter(node -> getLocation(node).equalsIgnoreCase(location))
                .filter(node -> Objects.isNull(getProvider(node)))
                .collect(Collectors.toList());

            for (TNodeTemplate node : nodesForLocation) {
                LOGGER.debug("Trying to assign NodeTemplate {} to provider in location {}", node.getId(), location);

                List<String> viableProviders = getViableProviders(node, location, blackList);
                if (viableProviders.isEmpty()) {
                    throw new InvalidParameterException("No viable provider found for NodeTemplate " + node.getId()
                        + ". Aborting placement!");
                }

                // add NodeTemplate to the list of supported components of all viable providers
                for (String provider : viableProviders) {
                    List<TNodeTemplate> supportedComps;
                    if (providers.containsKey(provider)) {
                        supportedComps = providers.get(provider);
                    } else {
                        supportedComps = new ArrayList<>();
                    }
                    supportedComps.add(node);
                    providers.put(provider, supportedComps);
                }
            }

            // add the data sources to the providers to which they are already assigned
            List<TNodeTemplate> dataSourcesForLocation = getNodeTemplatesWithLocation(topology).stream()
                .filter(node -> getLocation(node).equalsIgnoreCase(location))
                .filter(node -> Objects.nonNull(getProvider(node)))
                .collect(Collectors.toList());
            for (TNodeTemplate dataSource : dataSourcesForLocation) {
                String provider = getProvider(dataSource);
                if (providers.containsKey(provider)) {
                    List<TNodeTemplate> supportedComps = providers.get(provider);
                    supportedComps.add(dataSource);
                    providers.put(provider, supportedComps);
                }
            }

            // assign all NodeTemplates with the current location to one of the providers
            while (!nodesForLocation.isEmpty()) {
                // get the next provider that supports most of the unassigned NodeTemplates
                String nextProv = getNextProvider(providers);
                LOGGER
                    .debug("Assigning {} NodeTemplates to next provider: {}", providers.get(nextProv).size(), nextProv);

                for (TNodeTemplate node : providers.get(nextProv)) {
                    // assign the component to the provider and remove it from the set of to be assigned NodeTemplates
                    node.getOtherAttributes().put(ModelUtilities.NODE_TEMPLATE_PROVIDER, nextProv);
                    nodesForLocation.remove(node);
                    providers.remove(nextProv);

                    // remove assigned NodeTemplate from the list of all supporting providers 
                    for (String provider : providers.keySet()) {
                        providers.get(provider).remove(node);
                    }
                }
            }
        }
    }

    /**
     * Assigns a location to each component of the given TopologyTemplate based on the data flow, data factors of the
     * processing components, and data sizes of the data sources to minimize the required data transmission between
     * locations.
     *
     * @param topology  the incomplete TopologyTemplate to assign locations to the components
     * @param blackList the black list containing the NodeTemplates with the providers that are not usable for them
     */
    private static void assignToLocation(TTopologyTemplate topology, Map<String, List<String>> blackList) {

        List<TNodeTemplate> nodesWithLocation = getNodeTemplatesWithLocation(topology);
        List<TNodeTemplate> nodesWithoutLocation = getNodeTemplatesWithoutLocation(topology);

        if (nodesWithLocation.isEmpty()) {
            throw new InvalidParameterException("At least one filter needs an assigned location for the placement!");
        }

        // assign all components to the same location if only one location is set
        if (nodesWithLocation.size() < 2) {
            String targetLocation = getLocation(nodesWithLocation.get(0));
            for (TNodeTemplate node : nodesWithoutLocation) {
                node.getOtherAttributes().put(ModelUtilities.NODE_TEMPLATE_REGION, targetLocation);
            }
            return;
        }

        // assign the next component until all components are assigned
        while (!nodesWithoutLocation.isEmpty()) {
            LOGGER.debug("{} nodes have currently no defined location!", nodesWithoutLocation.size());
            for (TNodeTemplate nodeToAssign : nodesWithoutLocation) {
                assignComponent(topology, nodeToAssign, blackList);
            }
            nodesWithoutLocation = getNodeTemplatesWithoutLocation(topology);
        }
    }

    /**
     * Try to assign the given NodeTemplate to a location based on the data flow. No assignment is performed if other
     * NodeTemplates from which data has to be transmitted to this NodeTemplate are not yet assigned to a location.
     *
     * @param topology     the incomplete topology containing the NodeTemplate
     * @param nodeToAssign the NodeTemplate which should be assigned to a location
     * @param blackList    the black list containing the NodeTemplates with the providers that are not usable for them
     */
    private static void assignComponent(TTopologyTemplate topology, TNodeTemplate nodeToAssign,
                                        Map<String, List<String>> blackList) {
        LOGGER.debug("Trying to assign {}", nodeToAssign.getId());

        // maps locations to the sum of data that needs to be transferred from the locations to this NodeTemplate
        HashMap<String, Double> locations = new HashMap<>();
        for (String location : getAllLocations(topology)) {
            locations.put(location, 0.0);
        }

        for (TRelationshipTemplate relation : ModelUtilities.getIncomingRelationshipTemplates(topology, nodeToAssign)) {
            // only check incoming connectsTos which are not 'pulling'
            if (notPullingRelation.test(relation)) {
                TNodeTemplate source = ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(topology, relation);
                if (!hasLocation.test(source)) {
                    LOGGER.debug("Assignment of NodeTemplate {} currently not possible because {} is not assigned yet" +
                        ".", nodeToAssign.getId(), source.getId());
                    return;
                }

                // calculate data flow size over this relation and add to data size sum from the location of the source
                String sourceLocation = getLocation(source);
                double dataSize = getDataSizeForRelation(topology, relation, false);
                locations.put(sourceLocation, locations.get(sourceLocation) + dataSize);
            }
        }

        for (TRelationshipTemplate relation : ModelUtilities.getOutgoingRelationshipTemplates(topology, nodeToAssign)) {
            // only check outgoing connectsTos which are 'pulling'
            if (pullingRelation.test(relation)) {
                TNodeTemplate target = ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(topology, relation);
                if (!hasLocation.test(target)) {
                    LOGGER.debug("Assignment of NodeTemplate {} currently not possible because {} is not assigned yet.",
                        nodeToAssign.getId(), target.getId());
                    return;
                }

                // calculate data flow size over this relation and add to data size sum from the location of the target
                String targetLocation = getLocation(target);
                double dataSize = getDataSizeForRelation(topology, relation, true);
                if (locations.containsKey(targetLocation)) {
                    locations.put(targetLocation, locations.get(targetLocation) + dataSize);
                } else {
                    locations.put(targetLocation, dataSize);
                }
            }
        }

        // assign location with maximum data size that is not forbidden due to the black list
        for (Map.Entry<String, Double> nextLocation : entriesSortedByValues(locations)) {
            LOGGER.debug("Next location to assign: {} with data size: {}", nextLocation.getKey(), nextLocation
                .getValue());

            if (locationIsBlackListed(nodeToAssign, nextLocation.getKey(), blackList)) {
                LOGGER.debug("Skipping location due to black list.");
                continue;
            }
            nodeToAssign.getOtherAttributes().put(ModelUtilities.NODE_TEMPLATE_REGION, nextLocation.getKey());
            return;
        }

        throw new InvalidParameterException("Unable to assign location to " + nodeToAssign.getId() + " because all " +
            "provider of all locations are blacklisted. Topology must be refined manually!");
    }

    /**
     * Calculates the data size that must be transmitted over the given relation. For this, the transitively connected
     * data sources are determined and the data size is multiplied by the data factors of the processing components in
     * between.
     *
     * @param topology the TopologyTemplate containing the RelationshipTemplate
     * @param relation the RelationshipTemplate to calculate the data size
     * @param pulling  <code>true</code> if the source NodeTemplate pulls over this RelationshipTemplate,
     *                 <code>false</code> otherwise
     * @return the data size that needs to be transmitted over the given RelationshipTemplate
     */
    private static double getDataSizeForRelation(TTopologyTemplate topology, TRelationshipTemplate relation,
                                                 boolean pulling) {
        TNodeTemplate template;
        if (pulling) {
            template = ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(topology, relation);
        } else {
            template = ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(topology, relation);
        }

        // return data size directly if the NodeTemplate is a data source
        if (isDataSource(topology, template)) {
            return getDataSourceSize(template);
        }

        // sum up the data size of all relations over which data flows into the template 
        double dataSize = 0;

        for (TRelationshipTemplate incomingRelation : ModelUtilities
            .getIncomingRelationshipTemplates(topology, template)) {
            if (notPullingRelation.test(incomingRelation)) {
                dataSize += getDataSizeForRelation(topology, incomingRelation, false);
            }
        }

        for (TRelationshipTemplate outgoingRelation :
            ModelUtilities.getOutgoingRelationshipTemplates(topology, template)) {
            if (pullingRelation.test(outgoingRelation)) {
                dataSize += getDataSizeForRelation(topology, outgoingRelation, true);
            }
        }

        return dataSize * getDataFactor(template);
    }

    private static List<TNodeTemplate> getNodeTemplatesWithLocation(TTopologyTemplate topology) {
        return topology.getNodeTemplates().stream()
            .filter(hasLocation)
            .collect(Collectors.toList());
    }

    private static List<TNodeTemplate> getNodeTemplatesWithoutLocation(TTopologyTemplate topology) {
        Predicate<TNodeTemplate> onlyIncomingConnectsToRelations = nodeTemplate ->
            ModelUtilities.getIncomingRelationshipTemplates(topology, nodeTemplate).stream()
                .allMatch(relation -> relation.getType().equals(ToscaBaseTypes.connectsToRelationshipType));

        return topology.getNodeTemplates().stream()
            .filter(nodeTemplate -> !hasLocation.test(nodeTemplate))
            .filter(onlyIncomingConnectsToRelations)
            .collect(Collectors.toList());
    }

    private static String getProvider(TNodeTemplate node) {
        return node.getOtherAttributes().get(ModelUtilities.NODE_TEMPLATE_PROVIDER);
    }

    private static String getLocation(TNodeTemplate node) {
        return node.getOtherAttributes().get(ModelUtilities.NODE_TEMPLATE_REGION);
    }

    private static double getDataSourceSize(TNodeTemplate node) {
        String dataSize = getKVProperties(node).get(NODE_TEMPLATE_PROPERTY_DATA_SIZE);
        if (Objects.isNull(dataSize)) {
            throw new InvalidParameterException("NodeTemplate " + node.getId() + " has no data size property although" +
                " its a data source in the data flow model!");
        }
        return Double.parseDouble(dataSize);
    }

    private static double getDataFactor(TNodeTemplate node) {
        String dataFactor = getKVProperties(node).get(NODE_TEMPLATE_PROPERTY_DATA_FACTOR);
        if (Objects.isNull(dataFactor)) {
            throw new InvalidParameterException("NodeTemplate " + node.getId() + " has no data factor property " +
                "although its a data processor in the data flow model!");
        }
        return Double.parseDouble(dataFactor);
    }

    private static LinkedHashMap<String, String> getKVProperties(TNodeTemplate node) {
        TEntityTemplate.Properties props = node.getProperties();
        if (Objects.isNull(props) || Objects.isNull(props.getKVProperties())) {
            throw new InvalidParameterException("NodeTemplate " + node.getId() + " has no properties defined but all " +
                "NodeTemplates corresponding to filters of the data flow model need either a DataFactor or a DataSize" +
                " property!");
        }
        return props.getKVProperties();
    }

    /**
     * Check if there is a provider supporting the given location that is not on the black list of the given
     * NodeTemplate.
     *
     * @param nodeToAssign the NodeTemplate to check
     * @param location     the location to check
     * @param blackList    the black list containing the NodeTemplates with the providers that are not usable for them
     * @return <code>true</code> if at least one provider supporting the given location exists that is not on the
     * black list of the component, <code>false</code> otherwise
     */
    private static boolean locationIsBlackListed(TNodeTemplate nodeToAssign, String location,
                                                 Map<String, List<String>> blackList) {
        return getViableProviders(nodeToAssign, location, blackList).isEmpty();
    }

    /**
     * Check if the given NodeTemplate corresponds to a data source in the data flow model. This is the case if the
     * NodeTemplate has only incoming connectsTos which are pulling and no outgoing connectsTos, which means the
     * NodeTemplate is passive and does not receive any data from another NodeTemplate.
     *
     * @param topology the TopologyTemplate containing the NodeTemplate
     * @param node     the NodeTemplate to check if it´s a data source
     * @return <code>true</code> if the given NodeTemplate corresponds to a data source, <code>false</code> otherwise
     */
    private static boolean isDataSource(TTopologyTemplate topology, TNodeTemplate node) {
        for (TRelationshipTemplate incomingRelation : ModelUtilities.getIncomingRelationshipTemplates(topology, node)) {
            if (notPullingRelation.test(incomingRelation)) {
                return false;
            }
        }

        List<TRelationshipTemplate> outgoingConnectsTo = ModelUtilities
            .getOutgoingRelationshipTemplates(topology, node).stream()
            .filter(relation -> relation.getType().equals(ToscaBaseTypes.connectsToRelationshipType))
            .collect(Collectors.toList());
        return outgoingConnectsTo.isEmpty();
    }

    private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                int res = e2.getValue().compareTo(e1.getValue());
                return res != 0 ? res : 1;
            }
        }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    /**
     * Get all ServiceTemplates in the repo with specified tags
     *
     * @return a list with all found ServiceTemplates
     */
    private static List<TServiceTemplate> getServiceTemplatesWithTags() {
        IRepository repo = RepositoryFactory.getRepository();
        return repo.getAllDefinitionsChildIds().stream()
            .filter(id -> id.getGroup().equals(SERVICE_TEMPLATE_GROUP) && id instanceof ServiceTemplateId)
            .map(id -> repo.getElement((ServiceTemplateId) id))
            .filter(st -> Objects.nonNull(st.getTags()))
            .collect(Collectors.toList());
    }

    /**
     * Check if the given ServiceTemplate is a suited provider for the given location and with respect to the black list
     * of the given NodeTemplate
     *
     * @param template  the ServiceTemplate representing a possible provider
     * @param node      the NodeTemplate to check the provider for
     * @param location  the location that needs to be supported by the provider
     * @param blackList the black list of the NodeTemplate restricting possible providers
     * @return <code>true</code> if the provider is suited, <code>false</code> otherwise
     */
    private static boolean isUnsuitedProvider(TServiceTemplate template, TNodeTemplate node, String location,
                                              Map<String, List<String>> blackList) {
        List<TTag> tags = template.getTags().getTag();
        TTag providerTag = getTag(tags, TAG_NAME_PROVIDER);
        TTag locationTag = getTag(tags, TAG_NAME_LOCATION);

        if (Objects.isNull(providerTag) || Objects.isNull(locationTag) || !locationTag.getValue().equals(location)) {
            return true;
        }

        return blackList.get(node.getId()).stream()
            .anyMatch(blockedProvider -> providerTag.getValue().equals(blockedProvider));
    }

    private static TTag getTag(List<TTag> tags, String name) {
        return tags.stream().filter(tag -> tag.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    /**
     * Get all locations which are defined by one of the NodeTemplates.
     *
     * @param topology the topology containing the NodeTemplates
     * @return a list of locations that are defined at one of the NodeTemplates
     */
    private static Set<String> getAllLocations(TTopologyTemplate topology) {
        Set<String> locations = new HashSet<>();
        for (TNodeTemplate node : getNodeTemplatesWithLocation(topology)) {
            locations.add(getLocation(node));
        }
        return locations;
    }

    /**
     * Get all provider from the repo which support the given location and the policies of the given NodeTemplate.
     *
     * @param node      the NodeTemplate with the policies
     * @param location  the location for the provider
     * @param blackList the black list of the NodeTemplate restricting possible providers
     * @return a list with all found providers
     */
    private static List<String> getViableProviders(TNodeTemplate node, String location, Map<String, List<String>> blackList) {
        List<String> providers = new ArrayList<>();
        template:
        for (TServiceTemplate template : getServiceTemplatesWithTags()) {
            // the provider needs the corresponding tags and is not allowed to be on the blacklist
            if (isUnsuitedProvider(template, node, location, blackList)) {
                continue;
            }

            // the provider has to support all defined policies
            if (Objects.nonNull(node.getPolicies())) {
                for (TPolicy policy : node.getPolicies().getPolicy()) {
                    // check the tags of the provider for the policy name
                    if (Objects.isNull(getTag(template.getTags().getTag(), policy.getPolicyType().toString()))) {
                        continue template;
                    }
                }
            }

            providers.add(getTag(template.getTags().getTag(), TAG_NAME_PROVIDER).getValue());
        }
        return providers;
    }

    /**
     * Get the provider from the given list of providers that supports the most NodeTemplates.
     *
     * @param providers the map of providers with the list of supported NodeTemplates
     * @return the name of the provider
     */
    private static String getNextProvider(HashMap<String, List<TNodeTemplate>> providers) {
        int supportedCompCount = 0;
        String bestProv = null;
        for (String provider : providers.keySet()) {
            if (Objects.isNull(bestProv) || providers.get(provider).size() > supportedCompCount) {
                bestProv = provider;
                supportedCompCount = providers.get(provider).size();
            }
        }
        return bestProv;
    }
}
