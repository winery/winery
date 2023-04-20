/*******************************************************************************
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

package org.eclipse.winery.model.adaptation.enhance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.DeploymentTechnologyDescriptor;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElementWithTags;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.OpenToscaInterfaces;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.common.ListUtils.listIsNotNullOrEmpty;

/**
 * This class exposes utility functions which enhance a given topology. It also provides some semantic utilities as,
 * e.g., returning the hostedOn relation of a NodeTemplate.
 */
public class EnhancementUtils {

    static final String GENERATED_NS_SUFFIX = "/generated";
    private static final Logger logger = LoggerFactory.getLogger(EnhancementUtils.class);

    // region ******************** Freeze and Defrost ********************
    public static TTopologyTemplate determineStatefulComponents(TTopologyTemplate topology) {
        Map<QName, TNodeType> nodeTypes = RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class);

        topology.getNodeTemplates().stream()
            .filter(nodeTemplate -> {
                TNodeType type = nodeTypes.get(nodeTemplate.getType());
                return RepositoryFactory.getRepository().getParentsAndChild(type)
                    .stream()
                    .map(TExtensibleElementWithTags::getTags)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .anyMatch(tag ->
                        "stateful".equalsIgnoreCase(tag.getName())
                            || "isStateful".equalsIgnoreCase(tag.getName()));
            })
            // avoid duplicate annotations
            .filter(node -> !ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.statefulComponentPolicyType))
            .forEach(node ->
                ModelUtilities.addPolicy(node, OpenToscaBaseTypes.statefulComponentPolicyType, "stateful")
            );

        return topology;
    }

    public static TopologyAndErrorList determineFreezableComponents(TTopologyTemplate topology) {
        Map<QName, TNodeType> nodeTypes = RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class);

        ArrayList<String> errorList = new ArrayList<>();
        topology.getNodeTemplates().stream()
            // only iterate over all stateful components
            .filter(node -> ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.statefulComponentPolicyType))
            // avoid duplicate annotations
            .filter(node -> !ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.freezableComponentPolicyType))
            .forEach(node -> {
                TNodeType type = nodeTypes.get(node.getType());
                if (ModelUtilities.nodeTypeHasInterface(type, OpenToscaInterfaces.stateInterface)) {
                    ModelUtilities.addPolicy(node, OpenToscaBaseTypes.freezableComponentPolicyType, "freezable");
                } else {
                    TRelationshipTemplate relationshipTemplate;
                    boolean isFreezable = false;
                    do {
                        relationshipTemplate = getHostedOnRelationship(topology, node);

                        if (Objects.nonNull(relationshipTemplate)) {
                            TNodeTemplate host = (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
                            TNodeType hostType = nodeTypes.get(host.getType());
                            if (ModelUtilities.nodeTypeHasInterface(hostType, OpenToscaInterfaces.stateInterface)) {
                                ModelUtilities.addPolicy(host, OpenToscaBaseTypes.freezableComponentPolicyType, "freezable");
                                isFreezable = true;
                            }
                        }
                    } while (!isFreezable && Objects.nonNull(relationshipTemplate));

                    if (!isFreezable) {
                        errorList.add(node.getId());
                    }
                }
            });

        TopologyAndErrorList topologyAndErrorList = new TopologyAndErrorList();
        topologyAndErrorList.errorList = errorList;
        topologyAndErrorList.topologyTemplate = topology;

        return topologyAndErrorList;
    }

    public static TTopologyTemplate cleanFreezableComponents(TTopologyTemplate topology) {
        topology.getNodeTemplates().stream()
            // only iterate over all freezable components
            .filter(node -> ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.freezableComponentPolicyType))
            .forEach(node -> {
                TRelationshipTemplate hostedOnRelationship = getHostedOnRelationship(topology, node);
                while (Objects.nonNull(hostedOnRelationship)) {
                    TNodeTemplate host = (TNodeTemplate) hostedOnRelationship.getTargetElement().getRef();
                    if (node.getPolicies() != null
                        && ModelUtilities.containsPolicyType(host, OpenToscaBaseTypes.freezableComponentPolicyType)) {
                        node.getPolicies()
                            .removeIf(policy -> policy.getPolicyType().equals(OpenToscaBaseTypes.freezableComponentPolicyType));
                        hostedOnRelationship = null;
                    } else {
                        hostedOnRelationship = getHostedOnRelationship(topology, host);
                    }
                }
            });

        return topology;
    }
    // endregion

    /**
     * This method returns the <em>hostedOn</em> RelationshipTemplate (see
     * {@link ToscaBaseTypes#hostedOnRelationshipType}) of the given NodeTemplate in the given Topology. Note: It is
     * assumed that there is only <b>one</b> hostedOn relation.
     *
     * @param topology The topology in which the given node is in.
     * @param node     The node to return the hostedOn relation.
     */
    private static TRelationshipTemplate getHostedOnRelationship(TTopologyTemplate topology, TNodeTemplate node) {
        Map<QName, TRelationshipType> relationshipTypes = RepositoryFactory.getRepository().getQNameToElementMapping(RelationshipTypeId.class);
        List<TRelationshipTemplate> outgoingRelationshipTemplates = ModelUtilities.getOutgoingRelationshipTemplates(topology, node);
        return outgoingRelationshipTemplates.stream()
            .filter(relation -> ModelUtilities.isOfType(ToscaBaseTypes.hostedOnRelationshipType, relation.getType(), relationshipTypes))
            .findFirst()
            .orElse(null);
    }

    private static String generateNewGeneratedNamespace(QName qName) {
        NamespaceManager namespaceManager = RepositoryFactory.getRepository().getNamespaceManager();
        String namespace = qName.getNamespaceURI().concat(GENERATED_NS_SUFFIX);
        NamespaceProperties namespaceProperties = namespaceManager.getNamespaceProperties(namespace);
        namespaceProperties.setGeneratedNamespace(true);
        namespaceManager.setNamespaceProperties(namespace, namespaceProperties);
        return namespace;
    }

    private static void addAllDAsAndIAsToImplementation(TNodeTypeImplementation target, TNodeTypeImplementation source) {
        if (Objects.nonNull(source.getDeploymentArtifacts())) {
            if (target.getDeploymentArtifacts() == null) {
                target.setDeploymentArtifacts(new ArrayList<>());
            }
            target.getDeploymentArtifacts().addAll(
                source.getDeploymentArtifacts()
            );
        }

        if (Objects.nonNull(source.getImplementationArtifacts())) {
            if (target.getImplementationArtifacts() == null) {
                target.setImplementationArtifacts(new ArrayList<>());
            }
            target.getImplementationArtifacts().addAll(
                source.getImplementationArtifacts()
            );
        }
    }

    // region ******************** Add Management Features ******************** 

    /**
     * Gathers all feature NodeTypes available for the given topology.
     *
     * If the underlying implementation of the feature does not matter, use <code>null</code>.
     *
     * <p>
     * Note: If feature NodeTypes are used in the topology, they cannot be enhanced with more features.
     * </p>
     *
     * @param topology               The topology to update.
     * @param deploymentTechnologies Deployment technology descriptors contained in the service template
     */
    public static Map<String, Map<QName, String>> getAvailableFeaturesForTopology(TTopologyTemplate topology, List<DeploymentTechnologyDescriptor> deploymentTechnologies) {
        IRepository repository = RepositoryFactory.getRepository();

        Map<String, Map<QName, String>> availableFeatures = new HashMap<>();
        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);

        topology.getNodeTemplates().forEach(node -> {
            List<String> nodeDeploymentTechnologies = deploymentTechnologies.stream()
                .filter(deploymentTechnologyDescriptor -> deploymentTechnologyDescriptor.getManagedIds()
                    .contains(node.getId()))
                .map(DeploymentTechnologyDescriptor::getTechnologyId)
                .collect(Collectors.toList());

            Map<TNodeType, String> featureChildren = getAvailableFeaturesOfType(node.getType(), nodeTypes, nodeDeploymentTechnologies);
            Map<QName, String> applicableFeatures = new HashMap<>();

            // Check requirements
            featureChildren.forEach((featureType, value) -> {
                if (listIsNotNullOrEmpty(featureType.getRequirementDefinitions())) {
                    List<TRequirementDefinition> requirements = featureType.getRequirementDefinitions().stream()
                        .filter(req -> req.getRequirementType().equals(OpenToscaBaseTypes.managementFeatureRequirement))
                        .collect(Collectors.toList());

                    requirements.forEach(req -> {
                        boolean applicable = ModelUtilities.getHostedOnSuccessors(topology, node).stream()
                            .anyMatch(hosts -> {
                                WineryVersion reqVersion = VersionUtils.getVersion(req.getName());
                                String reqName = VersionUtils.getNameWithoutVersion(req.getName());

                                String type = hosts.getType().getLocalPart();
                                if (VersionUtils.getNameWithoutVersion(type).equals(reqName)) {
                                    return reqVersion.getComponentVersion().isEmpty()
                                        || reqVersion.getComponentVersion().equals(VersionUtils.getVersion(type).getComponentVersion());
                                }

                                return false;
                            });

                        if (applicable) {
                            applicableFeatures.put(featureType.getQName(), value);
                        }
                    });
                } else {
                    applicableFeatures.put(featureType.getQName(), value);
                }
            });

            if (featureChildren.size() > 0) {
                availableFeatures.put(node.getId(), applicableFeatures);
            }
        });

        return availableFeatures;
    }

    /**
     * Retrieve the available types of the <code>givenType</code> and filter them according to their implementation
     * based on the underlying <code>deploymentTechnology</code>. If the filtering by the
     * <code>deploymentTechnology</code> is not required, <code>null</code> should be passed.
     *
     * @param givenType              The QName of the type to be investigated.
     * @param elements               The set of Types available.
     * @param deploymentTechnologies The underlying deployment technology, the features must comply to.
     * @param <T>                    The type of the Elements
     * @return The set of applicable features.
     */
    public static <T extends TEntityType> Map<T, String> getAvailableFeaturesOfType(
        QName givenType, Map<QName, T> elements,
        List<String> deploymentTechnologies) {
        HashMap<T, String> features = new HashMap<>();

        TEntityType entityType = elements.get(givenType);
        for (TEntityType type : RepositoryFactory.getRepository().getParentsAndChild(entityType)) {
            // Only the direct Children can define features.
            ModelUtilities.getDirectChildrenOf(type.getQName(), elements).forEach((qName, nodeType) -> {
                if (Objects.nonNull(nodeType.getTags())) {
                    List<TTag> list = nodeType.getTags();

                    // To enable the usage of "technology" and "technologies", we only check for "technolog"
                    String supportedDeploymentTechnologies = list.stream()
                        .filter(tag -> tag.getName().toLowerCase().contains("deploymentTechnolog".toLowerCase()))
                        .map(TTag::getValue)
                        .collect(
                            Collectors.joining(" "));

                    if (StringUtils.isBlank(supportedDeploymentTechnologies)
                        || "*".equals(supportedDeploymentTechnologies) || deploymentTechnologies.stream()
                        .anyMatch(s -> supportedDeploymentTechnologies.toLowerCase().contains(s.toLowerCase()))) {
                        list.stream()
                            .filter(tag -> "feature".equalsIgnoreCase(tag.getName()))
                            .findFirst()
                            .ifPresent(tTag -> features.put(elements.get(qName), tTag.getValue()));
                    }
                }
            });
        }
        return features;
    }

    /**
     * This method applies selected features to the given topology. Hereby the <code>featureMap</code> as generated by
     * {@link #getAvailableFeaturesForTopology(TTopologyTemplate, List)} is expected. However, the list may differ from
     * the originally generated one, since a user may want to have only a specific feature, i.e., all of the specified
     * features in the given list are applied.
     *
     * @param topology   The topology, the features will be applied to. It must be the same topology which was passed to
     *                   the {@link #getAvailableFeaturesForTopology(TTopologyTemplate, List)}.
     * @param featureMap The list of features to apply to the topology.
     * @return The updated topology in which all matching NodeTypes will be replaced with the corresponding feature
     * NodeTypes.
     */
    public static TTopologyTemplate applyFeaturesForTopology(TTopologyTemplate topology, Map<String, Map<QName, String>> featureMap) {
        topology.getNodeTemplates().stream()
            .filter(nodeTemplate -> Objects.nonNull(featureMap.get(nodeTemplate.getId())))
            .forEach(nodeTemplate -> {
                TNodeType generatedNodeType = createFeatureNodeType(nodeTemplate, featureMap.get(nodeTemplate.getId()));
                nodeTemplate.setType(generatedNodeType.getQName());
                if (Objects.nonNull(generatedNodeType.getWinerysPropertiesDefinition())) {
                    List<PropertyDefinitionKV> definedProperties = generatedNodeType.getWinerysPropertiesDefinition()
                        .getPropertyDefinitions();

                    LinkedHashMap<String, String> propertiesKV = ModelUtilities.getPropertiesKV(nodeTemplate);
                    final LinkedHashMap<String, String> kvProperties = propertiesKV == null
                        ? new LinkedHashMap<>()
                        : propertiesKV;
                    if (kvProperties.isEmpty()) {
                        definedProperties.stream().map(PropertyDefinitionKV::getKey)
                            .forEach(k -> kvProperties.put(k, ""));
                    } else {
                        definedProperties.forEach(propertyDefinition -> {
                            if (Objects.isNull(kvProperties.get(propertyDefinition.getKey()))) {
                                kvProperties.put(propertyDefinition.getKey(), "");
                            }
                        });
                    }
                    // TODO:
                    // We need to set new Properties because the {@link TEntityTemplate#setProperties} is implemented
                    // badly and does not add new properties. Due to time constraints we do it that way for now.
                    if (!kvProperties.isEmpty()) {
                        ModelUtilities.setPropertiesKV(nodeTemplate, kvProperties);
                    }
                }
            });

        return topology;
    }

    /**
     * This method merges the Basic-NodeType of the given nodeTemplate with the selected Feature-NodeTypes and generates
     * respective implementations.
     *
     * @param nodeTemplate The NodeTemplate that is updated with the selected features.
     * @param featureTypes The list of selected features as generated by
     *                     {@link #getAvailableFeaturesForTopology(TTopologyTemplate, List}.
     * @return The mapping of the generated merged NodeType and the QName of the NodeType it replaces.
     */
    public static TNodeType createFeatureNodeType(TNodeTemplate nodeTemplate, Map<QName, String> featureTypes) {
        IRepository repository = RepositoryFactory.getRepository();
        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        Map<QName, TNodeTypeImplementation> nodeTypeImplementations = repository.getQNameToElementMapping(NodeTypeImplementationId.class);

        StringBuilder featureNames = new StringBuilder();
        featureTypes.values().forEach(featureName -> {
            if (!featureNames.toString().isEmpty()) {
                featureNames.append("-");
            }
            featureNames.append(featureName.replaceAll("\\s", "-"));
        });

        // Ensure that we do not run into reference hell and load a new instance that we can change... 
        TNodeType featureEnrichedNodeType = repository.getType(nodeTemplate);

        // merge type
        String namespace = generateNewGeneratedNamespace(nodeTemplate.getType());
        featureEnrichedNodeType.setTargetNamespace(namespace);
        featureEnrichedNodeType.setName(
            nodeTemplate.getType().getLocalPart() + "-" + nodeTemplate.getId() + "-" + featureNames
                + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + "1"
        );
        TEntityType.DerivedFrom derivedFrom = new TEntityType.DerivedFrom();
        derivedFrom.setType(nodeTemplate.getType());
        featureEnrichedNodeType.setDerivedFrom(derivedFrom);

        // prepare Properties
        if (Objects.isNull(featureEnrichedNodeType.getWinerysPropertiesDefinition())) {
            WinerysPropertiesDefinition props = new WinerysPropertiesDefinition();
            props.setPropertyDefinitions(new ArrayList<>());
            featureEnrichedNodeType.setProperties(props);
        }
        List<PropertyDefinitionKV> baseProperties = featureEnrichedNodeType.getWinerysPropertiesDefinition()
            .getPropertyDefinitions();

        // prepare Interfaces
        if (Objects.isNull(featureEnrichedNodeType.getInterfaces())) {
            featureEnrichedNodeType.setInterfaces(new ArrayList<>());
        }

        List<TInterface> baseInterfaces = featureEnrichedNodeType.getInterfaces();

        // merge impl accordingly
        TNodeTypeImplementation generatedImplementation = new TNodeTypeImplementation.Builder(
            featureEnrichedNodeType.getName() + "_Impl"
                + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + "1",
            featureEnrichedNodeType.getQName()
        ).build();

        // ensure that the lists are initialized
        generatedImplementation.setImplementationArtifacts(new ArrayList<>());
        generatedImplementation.setDeploymentArtifacts(new ArrayList<>());

        Collection<NodeTypeImplementationId> baseTypeImplementations =
            repository.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, nodeTemplate.getType());

        if (baseTypeImplementations.size() > 0) {
            for (NodeTypeImplementationId id : baseTypeImplementations) {
                if (Objects.isNull(generatedImplementation.getTargetNamespace())) {
                    generatedImplementation.setTargetNamespace(generateNewGeneratedNamespace(id.getQName()));
                }
                addAllDAsAndIAsToImplementation(generatedImplementation, nodeTypeImplementations.get(id.getQName()));
            }
        } else {
            // This should never be the case. However, we implement it as a valid fallback. 
            generatedImplementation.setTargetNamespace(
                namespace.replace("nodetypes", "nodetypeimplementations")
            );
        }

        featureTypes.keySet().forEach(featureTypeQName -> {
            TNodeType nodeType = nodeTypes.get(featureTypeQName);

            // merge Properties
            if (Objects.nonNull(nodeType.getWinerysPropertiesDefinition())) {
                List<PropertyDefinitionKV> kvList = nodeType.getWinerysPropertiesDefinition().getPropertyDefinitions();
                if (Objects.nonNull(kvList) && !kvList.isEmpty()) {
                    for (PropertyDefinitionKV kv : kvList) {
                        boolean listContainsProperty = baseProperties.stream()
                            .anyMatch(property -> property.getKey().equals(kv.getKey()));
                        if (!listContainsProperty) {
                            baseProperties.add(kv);
                        }
                    }
                }
            }

            // merge Interfaces
            if (Objects.nonNull(nodeType.getInterfaces()) && !nodeType.getInterfaces().isEmpty()) {
                for (TInterface anInterface : nodeType.getInterfaces()) {
                    Optional<TInterface> existingInterface = baseInterfaces.stream()
                        .filter(iface -> anInterface.getName().equals(iface.getName()))
                        .findFirst();
                    if (existingInterface.isPresent()) {
                        TInterface tInterface = existingInterface.get();
                        anInterface.getOperations().stream()
                            .filter(anOp -> tInterface.getOperations().stream().noneMatch(op -> op.getName().equals(anOp.getName())))
                            .forEach(anOp -> tInterface.getOperations().add(anOp));
                    } else {
                        baseInterfaces.add(anInterface);
                    }
                }
            }

            // merge implementations
            repository.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, featureTypeQName)
                .forEach(id ->
                    addAllDAsAndIAsToImplementation(
                        generatedImplementation,
                        nodeTypeImplementations.get(id.getQName())
                    )
                );
        });

        // In the case that neither the basic type, nor the feature types define properties,
        // remove them from the type to ensure a compliant XML.
        if (Objects.nonNull(featureEnrichedNodeType.getWinerysPropertiesDefinition())
            && Objects.nonNull(featureEnrichedNodeType.getWinerysPropertiesDefinition().getPropertyDefinitions())
            && featureEnrichedNodeType.getWinerysPropertiesDefinition().getPropertyDefinitions().isEmpty()) {
            ModelUtilities.removeWinerysPropertiesDefinition(featureEnrichedNodeType);
        }

        try {
            repository.setElement(new NodeTypeId(featureEnrichedNodeType.getQName()), featureEnrichedNodeType);
            repository.setElement(new NodeTypeImplementationId(generatedImplementation.getQName()), generatedImplementation);
        } catch (IOException e) {
            logger.error("Error while saving generated definitions.", e);
        }

        return featureEnrichedNodeType;
    }
    // endregion
}
