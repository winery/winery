/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.splitting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.version.VersionSupport;
import org.eclipse.winery.repository.common.Util;
import org.eclipse.winery.model.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.driverspecificationandinjection.DASpecification;
import org.eclipse.winery.repository.driverspecificationandinjection.DriverInjection;

import org.slf4j.LoggerFactory;

public class Splitting {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Splitting.class);

    // counter for relationships starts at 100 because all TRelationshipTemplate should have a 3 digit number in their id
    private static int newRelationshipIdCounter = 100;
    private static int nodeTemplateIdCounter = 1;
    private static int IdCounter = 1;

    // Required variables for the following computation of the transitive closure of a given topology
    private Map<TNodeTemplate, Set<TNodeTemplate>> initDirectSuccessors = new HashMap<>();
    private Map<TNodeTemplate, Boolean> visitedNodeTemplates = new HashMap<>();
    private Map<TNodeTemplate, Set<TNodeTemplate>> transitiveAndDirectSuccessors = new HashMap<>();

    /**
     * Splits the topology template of the given service template. Creates a new service template with "-split" suffix
     * as id. Any existing "-split" service template will be deleted. Matches the split topology template to the cloud
     * providers according to the target labels. Creates a new service template with "-matched" suffix as id. Any
     * existing "-matched" service template will be deleted.
     *
     * @param id of the ServiceTemplate switch should be split and matched to cloud providers
     * @return id of the ServiceTemplate which contains the matched topology
     */
    public ServiceTemplateId splitTopologyOfServiceTemplate(ServiceTemplateId id) throws SplittingException, IOException {
        long start = System.currentTimeMillis();
        IRepository repository = RepositoryFactory.getRepository();
        TServiceTemplate serviceTemplate = repository.getElement(id);

        // create wrapper service template
        ServiceTemplateId splitServiceTemplateId = new ServiceTemplateId(
            id.getNamespace().getDecoded(),
            VersionSupport.getNewComponentVersionId(id, "split"),
            false);

        repository.forceDelete(splitServiceTemplateId);
        repository.flagAsExisting(splitServiceTemplateId);
        TServiceTemplate splitServiceTemplate = new TServiceTemplate();
        splitServiceTemplate.setName(splitServiceTemplateId.getXmlId().getDecoded());
        splitServiceTemplate.setId(splitServiceTemplate.getName());
        splitServiceTemplate.setTargetNamespace(id.getNamespace().getDecoded());
        TTopologyTemplate splitTopologyTemplate = split(serviceTemplate.getTopologyTemplate());
        splitServiceTemplate.setTopologyTemplate(splitTopologyTemplate);

        LOGGER.debug("Persisting...");
        repository.setElement(splitServiceTemplateId, splitServiceTemplate);
        LOGGER.debug("Persisted.");

        // create wrapper service template
        ServiceTemplateId matchedServiceTemplateId = new ServiceTemplateId(
            id.getNamespace().getDecoded(),
            VersionSupport.getNewComponentVersionId(id, "split-matched"),
            false);

        repository.forceDelete(matchedServiceTemplateId);
        repository.flagAsExisting(matchedServiceTemplateId);
        TServiceTemplate matchedServiceTemplate = new TServiceTemplate();
        matchedServiceTemplate.setName(matchedServiceTemplateId.getXmlId().getDecoded());
        matchedServiceTemplate.setId(matchedServiceTemplate.getName());
        matchedServiceTemplate.setTargetNamespace(id.getNamespace().getDecoded());
        TTopologyTemplate matchedTopologyTemplate = hostMatchingWithDefaultHostSelection(splitTopologyTemplate);
        matchedServiceTemplate.setTopologyTemplate(matchedTopologyTemplate);
        LOGGER.debug("Persisting...");
        repository.setElement(matchedServiceTemplateId, matchedServiceTemplate);
        LOGGER.debug("Persisted.");

        long duration = System.currentTimeMillis() - start;
        LOGGER.debug("Execution Time in millisec: " + duration + "ms");

        return matchedServiceTemplateId;
    }

    /**
     * Splits the topology template of the given service template. Creates a new service template with "-split" suffix
     * as id. Any existing "-split" service template will be deleted. Matches the split topology template to the cloud
     * providers according to the target labels. Creates a new service template with "-matched" suffix as id. Any
     * existing "-matched" service template will be deleted.
     *
     * @param id of the ServiceTemplate switch should be split and matched to cloud providers
     * @return id of the ServiceTemplate which contains the matched topology
     */
    public ServiceTemplateId matchTopologyOfServiceTemplate(ServiceTemplateId id) throws Exception {
        long start = System.currentTimeMillis();
        IRepository repository = RepositoryFactory.getRepository();

        TServiceTemplate serviceTemplate = repository.getElement(id);
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();

		/*
        Get all open requirements and the basis type of the required capability type
		Two different basis types are distinguished:
			"Container" which means a hostedOn injection is required
			"Endpoint" which means a connectsTo injection is required
		 */
        Map<TRequirement, String> requirementsAndMatchingBasisCapabilityTypes =
            getOpenRequirementsAndMatchingBasisCapabilityTypeNames(topologyTemplate);
        // Output check
        LOGGER.debug("Matching for ServiceTemplate with ID: {}", id.getQName());
        for (TRequirement req : requirementsAndMatchingBasisCapabilityTypes.keySet()) {
            LOGGER.debug("Open Requirement: {}", req.getId());
            LOGGER.debug("Matching basis type: {}", requirementsAndMatchingBasisCapabilityTypes.get(req));
        }

        TTopologyTemplate matchedConnectedTopologyTemplate;
        if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Container")) {
            // set default target labels if they are not yet set
            if (!hasTargetLabels(topologyTemplate)) {
                LOGGER.debug("Target labels are not set for all NodeTemplates. Using default target labels.");
                topologyTemplate.getNodeTemplates().forEach(t -> ModelUtilities.setTargetLabel(t, "*"));
            }
            TTopologyTemplate matchedHostsTopologyTemplate = hostMatchingWithDefaultHostSelection(topologyTemplate);

            if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Endpoint")) {
                matchedConnectedTopologyTemplate = connectionMatchingWithDefaultConnectorSelection(matchedHostsTopologyTemplate);
            } else {
                matchedConnectedTopologyTemplate = matchedHostsTopologyTemplate;
            }
        } else if (requirementsAndMatchingBasisCapabilityTypes.containsValue("Endpoint")) {
            matchedConnectedTopologyTemplate = connectionMatchingWithDefaultConnectorSelection(topologyTemplate);
        } else {
            throw new SplittingException("No open Requirements which can be matched");
        }

        TTopologyTemplate daSpecifiedTopology = matchedConnectedTopologyTemplate;

        //Start additional functionality Driver Injection
        if (!DASpecification.getNodeTemplatesWithAbstractDAs(matchedConnectedTopologyTemplate).isEmpty() &&
            DASpecification.getNodeTemplatesWithAbstractDAs(matchedConnectedTopologyTemplate) != null) {
            daSpecifiedTopology = DriverInjection.injectDriver(matchedConnectedTopologyTemplate);
        }
        //End additional functionality Driver Injection

        // create wrapper service template
        ServiceTemplateId matchedServiceTemplateId = new ServiceTemplateId(
            id.getNamespace().getDecoded(),
            VersionSupport.getNewComponentVersionId(id, "matched"),
            false);

        RepositoryFactory.getRepository().forceDelete(matchedServiceTemplateId);
        RepositoryFactory.getRepository().flagAsExisting(matchedServiceTemplateId);
        repository.flagAsExisting(matchedServiceTemplateId);
        TServiceTemplate matchedServiceTemplate = new TServiceTemplate();
        matchedServiceTemplate.setName(matchedServiceTemplateId.getXmlId().getDecoded());
        matchedServiceTemplate.setId(matchedServiceTemplate.getName());
        matchedServiceTemplate.setTargetNamespace(id.getNamespace().getDecoded());
        matchedServiceTemplate.setTopologyTemplate(daSpecifiedTopology);
        LOGGER.debug("Persisting...");
        repository.setElement(matchedServiceTemplateId, matchedServiceTemplate);
        LOGGER.debug("Persisted.");

        long duration = System.currentTimeMillis() - start;
        LOGGER.debug("Execution Time in millisec: " + duration + "ms");

        return matchedServiceTemplateId;
    }

    /**
     * s Check if the TopologyTemplate contains NodeTemplates without a set target label.
     *
     * @param topologyTemplate the TopologyTemplate to check
     * @return <code>true</code> if all contained NodeTemplates have a target label set, <code>false</code> otherwise
     */
    private boolean hasTargetLabels(TTopologyTemplate topologyTemplate) {
        return topologyTemplate.getNodeTemplates().stream()
            .allMatch(node -> Objects.nonNull(node.getOtherAttributes())
                && Objects.nonNull(node.getOtherAttributes().get(ModelUtilities.QNAME_LOCATION)));
    }

    /**
     *
     */
    public ServiceTemplateId composeServiceTemplates(String composedSolutionServiceTemplateID, List<ServiceTemplateId> serviceTemplateIds) throws IOException, SplittingException {
        IRepository repository = RepositoryFactory.getRepository();
        String solutionNamespace = "http://www.opentosca.org/solutions/";

        // create composed service template
        ServiceTemplateId composedServiceTemplateId =
            new ServiceTemplateId(solutionNamespace, composedSolutionServiceTemplateID, false);
        repository.forceDelete(composedServiceTemplateId);
        repository.flagAsExisting(composedServiceTemplateId);
        TServiceTemplate composedServiceTemplate = new TServiceTemplate();
        composedServiceTemplate.setName(composedServiceTemplateId.getXmlId().getDecoded());
        composedServiceTemplate.setId(composedServiceTemplate.getName());
        composedServiceTemplate.setTargetNamespace(solutionNamespace);
        TTopologyTemplate composedTopologyTemplate = new TTopologyTemplate();
        composedServiceTemplate.setTopologyTemplate(composedTopologyTemplate);
        repository.setElement(composedServiceTemplateId, composedServiceTemplate);
        //add all node and relationship templates from the solution fragements to the composed topology template
        for (ServiceTemplateId id : serviceTemplateIds) {
            BackendUtils.mergeTopologyTemplateAinTopologyTemplateB(id, composedServiceTemplateId);
        }
        composedServiceTemplate = repository.getElement(composedServiceTemplateId);
        composedTopologyTemplate = composedServiceTemplate.getTopologyTemplate();
        List<TRequirement> openRequirements = getOpenRequirements(composedTopologyTemplate);
        for (TRequirement requirement : openRequirements) {
            QName requiredCapabilityTypeQName = getRequiredCapabilityTypeQNameOfRequirement(requirement);

            TNodeTemplate nodeWithOpenCapability = composedTopologyTemplate.getNodeTemplates().stream()
                .filter(nt -> nt.getCapabilities() != null)
                .filter(nt -> nt.getCapabilities().getCapability().stream()
                    .anyMatch(c -> c.getType().equals(requiredCapabilityTypeQName))).findFirst().orElse(null);

            if (nodeWithOpenCapability != null) {
                TCapability matchingCapability = nodeWithOpenCapability.getCapabilities().getCapability()
                    .stream().filter(c -> c.getType().equals(requiredCapabilityTypeQName)).findFirst().get();
                TRelationshipType matchingRelationshipType =
                    getMatchingRelationshipType(requirement, matchingCapability);
                if (matchingRelationshipType != null) {
                    addMatchingRelationshipTemplateToTopologyTemplate(composedTopologyTemplate, matchingRelationshipType, requirement, matchingCapability);
                } else {
                    throw new SplittingException("No suitable relationship type found for matching");
                }
            }
        }
        LOGGER.debug("Persisting...");
        repository.setElement(composedServiceTemplateId, composedServiceTemplate);
        LOGGER.debug("Persisted.");

        return composedServiceTemplateId;
    }

    /**
     *
     * @param serviceTemplateId
     * @throws SplittingException
     */
    public void resolveTopologyTemplate(ServiceTemplateId serviceTemplateId) throws SplittingException, IOException {
        IRepository repository = RepositoryFactory.getRepository();
        TServiceTemplate serviceTemplate = repository.getElement(serviceTemplateId);
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();

        List<TRequirement> openRequirements = getOpenRequirements(topologyTemplate);

        for (TRequirement requirement : openRequirements) {
            QName requiredCapTypeQName = getRequiredCapabilityTypeQNameOfRequirement(requirement);
            List<TNodeTemplate> nodesWithMatchingCapability = topologyTemplate.getNodeTemplates().stream()
                .filter(nt -> nt.getCapabilities() != null)
                .filter(nt -> nt.getCapabilities().getCapability().stream()
                    .anyMatch(c -> c.getType().equals(requiredCapTypeQName)))
                .collect(Collectors.toList());

            if (!nodesWithMatchingCapability.isEmpty() && nodesWithMatchingCapability.size() == 1) {
                TCapability matchingCapability = nodesWithMatchingCapability.get(0).getCapabilities().getCapability()
                    .stream().filter(c -> c.getType().equals(requiredCapTypeQName)).findFirst().get();
                TRelationshipType matchingRelationshipType =
                    getMatchingRelationshipType(requirement, matchingCapability);
                if (matchingRelationshipType != null) {
                    addMatchingRelationshipTemplateToTopologyTemplate(topologyTemplate, matchingRelationshipType, requirement, matchingCapability);
                } else {
                    throw new SplittingException("No suitable relationship type found for matching");
                }
            }
        }
        repository.setElement(serviceTemplateId, serviceTemplate);
    }

    /*
     * Checks if a topology template is valid.
     * The topology is valid if (1) all highest node templates have target labels assigned and
     * (2) all successor nodes connected by hostedOn relationships have no other target labels then the predecessors.
     *
     * @param topologyTemplate the topology template which should be checked
     * @return true if the topology template is valid and false if it is not
     */
    public boolean checkValidTopology(TTopologyTemplate topologyTemplate) {
        Map<TNodeTemplate, Set<TNodeTemplate>> transitiveAndDirectSuccessors = computeTransitiveClosure(topologyTemplate);

        //check if the highest level node templates have target labels assigned
        for (TNodeTemplate node : getNodeTemplatesWithoutIncomingHostedOnRelationships(topologyTemplate)) {
            if (!ModelUtilities.getTargetLabel(node).isPresent()) {
                return false;
            }
        }

        //check the transitive closure for each node if nodes with different labels are reachable by hostedOn relationships
        for (TNodeTemplate node : transitiveAndDirectSuccessors.keySet()) {
            if (!transitiveAndDirectSuccessors.get(node).isEmpty()) {
                for (TNodeTemplate successor : transitiveAndDirectSuccessors.get(node)) {
                    if (ModelUtilities.getTargetLabel(successor).isPresent() && ModelUtilities.getTargetLabel(node).isPresent()
                        && !ModelUtilities.getTargetLabel(node).get().equalsIgnoreCase(ModelUtilities.getTargetLabel(successor).get())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Splits a topology template according to the attached target labels. The target labels attached to nodes determine
     * at which target the nodes should be deployed. The result is a topology template containing for each target the
     * required nodes. Duplicates nodes which host nodes with different target labels.
     *
     * @param topologyTemplate the topology template which should be split
     * @return split topologyTemplate
     */
    public TTopologyTemplate split(TTopologyTemplate topologyTemplate) throws SplittingException {
        if (!checkValidTopology(topologyTemplate)) {
            throw new SplittingException("Topology is not valid");
        }

        // Copy for incremental removal of the processed nodes
        TTopologyTemplate topologyTemplateCopy = BackendUtils.clone(topologyTemplate);

        HashSet<TNodeTemplate> nodeTemplatesWhichPredecessorsHasNoPredecessors
            = new HashSet<>(getNodeTemplatesWhichPredecessorsHasNoPredecessors(topologyTemplateCopy));

        // Consider each node which hostedOn-predecessor nodes have no further hostedOn-predecessors
        while (!nodeTemplatesWhichPredecessorsHasNoPredecessors.isEmpty()) {

            for (TNodeTemplate currentNode : nodeTemplatesWhichPredecessorsHasNoPredecessors) {

                List<TNodeTemplate> predecessors = getHostedOnPredecessorsOfNodeTemplate(topologyTemplateCopy, currentNode);
                Set<String> predecessorsTargetLabel = new HashSet<>();

                for (TNodeTemplate predecessor : predecessors) {
                    Optional<String> targetLabel = ModelUtilities.getTargetLabel(predecessor);
                    if (!targetLabel.isPresent()) {
                        LOGGER.error("No target label present");
                        LOGGER.error("id " + predecessor.getId());
                    }

                    //noinspection OptionalGetWithoutIsPresent
                    predecessorsTargetLabel.add(targetLabel.get().toLowerCase());
                }
                // If all predecessors have the same target label assign this label to the considered node
                if (predecessorsTargetLabel.size() == 1) {
                    //noinspection OptionalGetWithoutIsPresent
                    ModelUtilities.setTargetLabel(currentNode, ModelUtilities.getTargetLabel(predecessors.get(0)).get());
                } else {

                    List<TRelationshipTemplate> incomingRelationships
                        = ModelUtilities.getIncomingRelationshipTemplates(topologyTemplateCopy, currentNode);
                    List<TRelationshipTemplate> outgoingRelationships
                        = ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplateCopy, currentNode);

                    // Otherwise, duplicate the considered node for each target label
                    for (String targetLabel : predecessorsTargetLabel) {
                        TNodeTemplate duplicatedNode = BackendUtils.clone(currentNode);
                        duplicatedNode.setId(Util.makeNCName(currentNode.getId() + "-" + targetLabel));
                        duplicatedNode.setName(Util.makeNCName(currentNode.getName() + "-" + targetLabel));
                        topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(duplicatedNode);
                        topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().add(duplicatedNode);
                        ModelUtilities.setTargetLabel(duplicatedNode, targetLabel);

                        for (TRelationshipTemplate incomingRelationship : incomingRelationships) {
                            Object sourceElementIncommingRel = incomingRelationship.getSourceElement().getRef();

                            /*
                             * incoming hostedOn relationships from predecessors with the same label and not hostedOn
                             * relationships (e.g. conntectsTo) are assigned to the duplicated node.
                             * The origin relationships are duplicated
                             */
                            TNodeTemplate sourceNodeTemplate = ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(topologyTemplateCopy, incomingRelationship);
                            if (((ModelUtilities.getTargetLabel(sourceNodeTemplate).get()
                                .equalsIgnoreCase(ModelUtilities.getTargetLabel(duplicatedNode).get())
                                && getBasisRelationshipType(incomingRelationship.getType()).getValidTarget().getTypeRef().getLocalPart().equalsIgnoreCase("Container"))
                                || !predecessors.contains(sourceNodeTemplate))) {

                                List<TRelationshipTemplate> reassignRelationship = new ArrayList<>();
                                reassignRelationship.add(incomingRelationship);
                                //Reassign incoming relationships
                                List<TRelationshipTemplate> reassignedRelationship
                                    = reassignIncomingRelationships(reassignRelationship, duplicatedNode);
                                topologyTemplate.getNodeTemplateOrRelationshipTemplate().addAll(reassignedRelationship);
                                topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().addAll(reassignedRelationship);
                            }
                        }

                        /*
                         * Reassign outgoing relationships. No difference between the relationship types.
                         * Origin outgoing relationships are duplicated and added to the duplicated node as source
                         */
                        List<TRelationshipTemplate> newOutgoingRelationships
                            = reassignOutgoingRelationships(outgoingRelationships, duplicatedNode);
                        topologyTemplate.getNodeTemplateOrRelationshipTemplate().addAll(newOutgoingRelationships);
                        topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().addAll(newOutgoingRelationships);
                    }

                    // Remove the original node and its relations from the origin topology template and the copy
                    topologyTemplate.getNodeTemplateOrRelationshipTemplate().remove(currentNode);
                    topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(outgoingRelationships);
                    topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(incomingRelationships);
                    topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().remove(currentNode);
                    topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().removeAll(outgoingRelationships);
                    topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().removeAll(incomingRelationships);
                }

                // Remove the hostedOn-predecessors of the considered node and their relations in the working copy
                topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().removeAll(predecessors);
                List<TRelationshipTemplate> removingRelationships =
                    topologyTemplateCopy.getRelationshipTemplates().stream()
                        .filter(rt -> predecessors.contains(ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(topologyTemplateCopy, rt))
                            || predecessors.contains(ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(topologyTemplateCopy, rt)))
                        .collect(Collectors.toList());

                topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().removeAll(removingRelationships);
            }
            nodeTemplatesWhichPredecessorsHasNoPredecessors.clear();
            nodeTemplatesWhichPredecessorsHasNoPredecessors.addAll(getNodeTemplatesWhichPredecessorsHasNoPredecessors(topologyTemplateCopy));
        }

        return topologyTemplate;
    }

    /**
     * This method returns the possible hosts for each lowest level node. Before a suitable node is found nodes may be
     * removed.
     *
     * @param topologyTemplate which should be mapped to the cloud provider according to the attached target labels -
     *                         target labels have to be attached to each node
     * @return map with a list of possible hosts for each lowest level node
     */
    public Map<String, List<TTopologyTemplate>> getHostingInjectionOptions(TTopologyTemplate topologyTemplate) throws SplittingException {
        ProviderRepository repository = new ProviderRepository();
        Map<String, List<TTopologyTemplate>> injectionOptions = new HashMap<>();
        // Contains all nodes for which at least one cloud provider node is found to host them
        List<TNodeTemplate> nodesForWhichHostsFound = new ArrayList<>();
        nodesForWhichHostsFound.clear();

        List<TNodeTemplate> needHostNodeTemplateCandidates = getNodeTemplatesWithoutOutgoingHostedOnRelationships(topologyTemplate);
        List<TNodeTemplate> nodesToCheck = new ArrayList<>();

        //Find lowest level nodes with open requirements which means they can be hosted by an other component
        for (TNodeTemplate nodeTemplateCandidate : needHostNodeTemplateCandidates) {
            if (hasNodeOpenRequirement(topologyTemplate, nodeTemplateCandidate)) {
                if (nodeTemplateCandidate.getRequirements().getRequirement().stream()
                    .anyMatch(req -> getBasisCapabilityType(getRequiredCapabilityTypeQNameOfRequirement(req)).getName().equalsIgnoreCase("Container"))) {
                    nodesToCheck.add(nodeTemplateCandidate);
                }
            }
        }

        LOGGER.debug("Start...");
        if (!nodesToCheck.isEmpty()) {
            //Check all lowest level nodes with open requirements if a compatible node is available
            for (TNodeTemplate needHostNode : nodesToCheck) {
                Optional<String> label = ModelUtilities.getTargetLabel(needHostNode);
                if (!label.isPresent()) {
                    LOGGER.error("No target label present");
                    LOGGER.error("id " + needHostNode.getId());
                    throw new SplittingException("No target label present for Node Template " + needHostNode.getId());
                }

                //noinspection OptionalGetWithoutIsPresent
                String targetLabel = ModelUtilities.getTargetLabel(needHostNode).get();

                List<TRequirement> openHostedOnRequirements = needHostNode.getRequirements().getRequirement().stream()
                    .filter(req -> getBasisCapabilityType(getRequiredCapabilityTypeQNameOfRequirement(req)).getName().equalsIgnoreCase("Container")).collect(Collectors.toList());

                List<TTopologyTemplate> compatibleTopologyFragments = repository
                    .getAllTopologyFragmentsForLocationAndOfferingCapability(targetLabel, openHostedOnRequirements.get(0));

                LOGGER.debug("Found {} compatible topology fragments for NodeTemplate {}",
                    compatibleTopologyFragments.size(), needHostNode.getId());

                //Add compatible nodes to the injectionOptions to host the considered lowest level node
                if (!compatibleTopologyFragments.isEmpty()) {
                    injectionOptions.put(needHostNode.getId(), compatibleTopologyFragments);
                    nodesForWhichHostsFound.add(needHostNode);
                }
            }
        }

        LOGGER.debug("Nodes to check: {}; Nodes with found host: {}", nodesToCheck.size(),
            nodesForWhichHostsFound.size());

        /*
         * Only the lowest components which are not in the matching list and which have still hostedOn-predecessors
         * are candidates which can be replaced by an other node
         */
        List<TNodeTemplate> replacementNodeTemplateCandidates = getReplacementNodeTemplateCandidatesForMatching(topologyTemplate, nodesForWhichHostsFound);

        while (!replacementNodeTemplateCandidates.isEmpty()) {
            for (TNodeTemplate replacementCandidate : replacementNodeTemplateCandidates) {
                List<TNodeTemplate> predecessorsOfReplacementCandidate = getHostedOnPredecessorsOfNodeTemplate(topologyTemplate, replacementCandidate);
                Optional<String> label = ModelUtilities.getTargetLabel(replacementCandidate);
                if (!label.isPresent()) {
                    LOGGER.error("No target label present");
                    LOGGER.error("id " + replacementCandidate.getId());
                    throw new SplittingException("No target label present for Node Template " + replacementCandidate.getId());
                }

                //noinspection OptionalGetWithoutIsPresent
                String targetLabel = ModelUtilities.getTargetLabel(replacementCandidate).get();

                // For each replacement candidate the predecessors are considered
                for (TNodeTemplate predecessor : predecessorsOfReplacementCandidate) {
                    // Check if a compatible node for the predecessor from the right provider is available
                    if (predecessor.getRequirements() == null) {
                        nodesForWhichHostsFound.add(predecessor);
                        //throw new SplittingException("The Node Template with the ID " + predecessor.getId() + " has no requirement assigned and the injected can't be processed");
                    } else {
                        List<TRequirement> openHostedOnRequirements = predecessor.getRequirements().getRequirement().stream()
                            .filter(req -> getBasisCapabilityType(getRequiredCapabilityTypeQNameOfRequirement(req)).getName().equalsIgnoreCase("Container")).collect(Collectors.toList());

                        List<TTopologyTemplate> compatibleTopologyFragments = repository
                            .getAllTopologyFragmentsForLocationAndOfferingCapability(targetLabel, openHostedOnRequirements.get(0));
                        //Add compatible nodes to the injectionOptions to host the considered lowest level node
                        if (!compatibleTopologyFragments.isEmpty()) {
                            injectionOptions.put(predecessor.getId(), compatibleTopologyFragments);
                            nodesForWhichHostsFound.add(predecessor);
                        }
                    }
                }
            }
            // Delete all replacement candidates and their relationships.
            topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(replacementNodeTemplateCandidates);
            List<TRelationshipTemplate> removingIncomingRelationships = ModelUtilities.getAllRelationshipTemplates(topologyTemplate)
                .stream()
                .filter(ir -> replacementNodeTemplateCandidates.contains(ir.getTargetElement().getRef()))
                .collect(Collectors.toList());

            topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(removingIncomingRelationships);
            List<TRelationshipTemplate> removingOutgoingRelationships = ModelUtilities.getAllRelationshipTemplates(topologyTemplate)
                .stream()
                .filter(ir -> replacementNodeTemplateCandidates.contains(ir.getSourceElement().getRef()))
                .collect(Collectors.toList());

            topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(removingIncomingRelationships);
            topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(removingOutgoingRelationships);
            //The former predecessors now are either matched by a new host or they are new replacement candidates
            replacementNodeTemplateCandidates.clear();
            replacementNodeTemplateCandidates.addAll(getReplacementNodeTemplateCandidatesForMatching(topologyTemplate, nodesForWhichHostsFound));
        }

        /*
         * Check if all lowest level nodes are contained in the nodesForWhichHostsFound, i.e., at least one
         * cloud provider node is found to host them.
         * The application-specific nodes must not be replacement candidates!
         */
        List<TNodeTemplate> checkListAllNodesMatched = topologyTemplate.getNodeTemplates().stream()
            .filter(z -> getNodeTemplatesWithoutOutgoingHostedOnRelationships(topologyTemplate).contains(z))
            .filter(node -> hasNodeOpenRequirement(topologyTemplate, node))
            .filter(y -> !nodesForWhichHostsFound.contains(y))
            .collect(Collectors.toList());

        LOGGER.debug("{} nodes without matching:", checkListAllNodesMatched.size());
        for (TNodeTemplate node : checkListAllNodesMatched) {
            LOGGER.debug(node.getId());
        }
        if (!checkListAllNodesMatched.isEmpty()) {
            throw new SplittingException("No matching possible");
        }
        return injectionOptions;
    }

    /**
     * Check if the given NodeTemplate has an open requirement.
     *
     * @param topology the topology containing the NodeTemplate
     * @param node     the NodeTemplate to check
     * @return <code>true</code> if an open requirement is found, <code>false</code> otherwise
     */
    private boolean hasNodeOpenRequirement(TTopologyTemplate topology, TNodeTemplate node) {
        if (Objects.isNull(node.getRequirements())) {
            return false;
        }
        List<TRequirement> openRequirements = getOpenRequirements(topology);
        return node.getRequirements().getRequirement().stream().anyMatch(openRequirements::contains);
    }

    /**
     * @param topologyTemplate which has to be labeled with the default label '*' which includes all available provider
     *                         repositories and then it is searched for matching options
     * @return map with the host options for each lowest node of the topology
     */
    public Map<String, List<TTopologyTemplate>> getHostingMatchingOptionsWithDefaultLabeling(TTopologyTemplate topologyTemplate) throws SplittingException {
        ModelUtilities.getAllNodeTemplates(topologyTemplate).forEach(t -> ModelUtilities.setTargetLabel(t, "*"));
        return getHostingInjectionOptions(topologyTemplate);
    }

    /**
     * Selects default the first entry of the list of possible hosts for each node, which requires a new host
     *
     * @param topologyTemplate which need new hosts added
     * @return topologyTemplate with randomly chosen hosts
     */
    public TTopologyTemplate hostMatchingWithDefaultHostSelection(TTopologyTemplate topologyTemplate) throws SplittingException {
        TTopologyTemplate newTopologyTemplate = BackendUtils.clone(topologyTemplate);
        Map<String, List<TTopologyTemplate>> matchingOptions = getHostingInjectionOptions(newTopologyTemplate);
        Map<String, TTopologyTemplate> defaultHostSelection = new HashMap<>();
        matchingOptions.entrySet().forEach(entry -> defaultHostSelection.put(entry.getKey(), entry.getValue().get(0)));

        return injectNodeTemplates(topologyTemplate, defaultHostSelection, InjectRemoval.REMOVE_REPLACED_AND_SUCCESSORS);
    }

    public TTopologyTemplate connectionMatchingWithDefaultConnectorSelection(TTopologyTemplate topologyTemplate) throws SplittingException {
        Map<String, List<TTopologyTemplate>> connectionInjectionOptions = getConnectionInjectionOptions(topologyTemplate);
        Map<String, TTopologyTemplate> defaultConnectorSelection = new HashMap<>();
        connectionInjectionOptions.entrySet().forEach(entry -> defaultConnectorSelection.put(entry.getKey(), entry.getValue().get(0)));

        return injectConnectionNodeTemplates(topologyTemplate, defaultConnectorSelection);
    }

    /**
     * Replaces the host of each key by the value of the map. Adds new relationships between the nodes and their new
     * hosts
     *
     * @param topologyTemplate original topology for which the Node Templates shall be replaced
     * @param injectNodes      map with the Nodes to replace as key and the replacement as value
     * @param removal          remove nothing, only replaced nt or replaced nt and all successors
     * @return modified topology with the replaced Node Templates
     */
    public TTopologyTemplate injectNodeTemplates(TTopologyTemplate topologyTemplate, Map<String, TTopologyTemplate> injectNodes, InjectRemoval removal) throws SplittingException {
        String id;

        // Matching contains all cloud provider nodes matched to the topology
        List<TNodeTemplate> matching = new ArrayList<>();
        matching.clear();
        LOGGER.debug("Start Matching Method");

        Set<TNodeTemplate> replacedNodeTemplatesToDelete = new HashSet<>();

        for (String predecessorOfNewHostId : injectNodes.keySet()) {
            TNodeTemplate predecessorOfNewHost = ModelUtilities.getAllNodeTemplates(topologyTemplate).stream()
                .filter(nt -> nt.getId().equals(predecessorOfNewHostId))
                .findFirst().orElse(null);
            LOGGER.debug("Predecessor which get a new host " + predecessorOfNewHost.getId());

            List<TNodeTemplate> originHostSuccessors = new ArrayList<>();
            originHostSuccessors.clear();
            originHostSuccessors = getHostedOnSuccessorsOfNodeTemplate(topologyTemplate, predecessorOfNewHost);
            TRequirement openHostedOnRequirement = predecessorOfNewHost.getRequirements().getRequirement().stream()
                .filter(req -> getBasisCapabilityType(getRequiredCapabilityTypeQNameOfRequirement(req)).getName().equals("Container"))
                .findAny().get();
            TNodeTemplate newMatchingNodeTemplate;
            TTopologyTemplate matchingTopologyFragment = injectNodes.get(predecessorOfNewHostId);
            //Highest Node Template to which the HostedOn Relationship has to be connected
            TNodeTemplate newHostNodeTemplate = matchingTopologyFragment.getNodeTemplates().stream()
                .filter(nt -> nt.getCapabilities() != null)
                .filter(nt -> nt.getCapabilities().getCapability().stream().anyMatch(cap -> cap.getType().equals(getRequiredCapabilityTypeQNameOfRequirement(openHostedOnRequirement))))
                .findFirst().get();
            LOGGER.debug("New host NodeTemplate: {}", newHostNodeTemplate.getId());

            //Check if the chosen replace node is already in the matching
            if (topologyTemplate.getNodeTemplates().stream()
                .anyMatch(nt -> equalsWithDifferentId(nt, newHostNodeTemplate))) {
                newMatchingNodeTemplate = topologyTemplate.getNodeTemplates().stream()
                    .filter(nt -> equalsWithDifferentId(nt, newHostNodeTemplate)).findAny().get();
            } else {
                newMatchingNodeTemplate = newHostNodeTemplate;
                matchingTopologyFragment.getNodeTemplateOrRelationshipTemplate().stream()
                    .filter(et -> topologyTemplate.getNodeTemplateOrRelationshipTemplate().stream().anyMatch(tet -> tet.getId().equals(et.getId())))
                    .forEach(et -> et.setId(et.getId() + "_" + IdCounter++));

                // rename capabilities and requirements
                matchingTopologyFragment.getNodeTemplates().stream().forEach(node -> {
                    TNodeTemplate.Capabilities caps = node.getCapabilities();
                    if (Objects.nonNull(caps)) {
                        caps.getCapability().stream()
                            .filter(et -> topologyTemplate.getNodeTemplates().stream().filter(nt -> Objects.nonNull(nt.getCapabilities()))
                                .flatMap(nt -> nt.getCapabilities().getCapability().stream()).anyMatch(cap -> cap.getId().equals(et.getId())))
                            .forEach(et -> et.setId(et.getId() + "_" + IdCounter++));
                    }

                    TNodeTemplate.Requirements reqs = node.getRequirements();
                    if (Objects.nonNull(reqs)) {
                        reqs.getRequirement().stream()
                            .filter(et -> topologyTemplate.getNodeTemplates().stream().filter(nt -> Objects.nonNull(nt.getRequirements()))
                                .flatMap(nt -> nt.getRequirements().getRequirement().stream()).anyMatch(req -> req.getId().equals(et.getId())))
                            .forEach(et -> et.setId(et.getId() + "_" + IdCounter++));
                    }
                });

                LOGGER.debug("Add {} NodeTemplate(s)",
                    matchingTopologyFragment.getNodeTemplateOrRelationshipTemplate().size());
                topologyTemplate.getNodeTemplateOrRelationshipTemplate().addAll(matchingTopologyFragment.getNodeTemplateOrRelationshipTemplate());
                matching.add(newMatchingNodeTemplate);
            }

            //In case the predecessor was a lowest node a new hostedOn relationship has to be added
            if (originHostSuccessors.isEmpty()) {
                TRelationshipTemplate newHostedOnRelationship = new TRelationshipTemplate();
                List<String> ids = new ArrayList<>();
                List<TRelationshipTemplate> tRelationshipTemplates = ModelUtilities.getAllRelationshipTemplates(topologyTemplate);
                tRelationshipTemplates.forEach(rt -> ids.add(rt.getId()));
                //Check if counter is already set in another Id, if yes -> increase newRelationshipCounter +1
                boolean uniqueID = false;
                id = "0";
                while (!uniqueID) {
                    if (!ids.contains("con" + newRelationshipIdCounter)) {
                        id = "con_" + newRelationshipIdCounter;
                        newRelationshipIdCounter++;
                        uniqueID = true;
                    } else {
                        newRelationshipIdCounter++;
                    }
                }
                newHostedOnRelationship.setId(id);
                newHostedOnRelationship.setName(id);

                TRelationshipTemplate.SourceOrTargetElement sourceElement = new TRelationshipTemplate.SourceOrTargetElement();
                TRelationshipTemplate.SourceOrTargetElement targetElement = new TRelationshipTemplate.SourceOrTargetElement();
                sourceElement.setRef(predecessorOfNewHost);
                targetElement.setRef(newMatchingNodeTemplate);
                newHostedOnRelationship.setSourceElement(sourceElement);
                newHostedOnRelationship.setTargetElement(targetElement);

                TRequirement requiredRequirement = null;
                List<TRequirement> openRequirements = predecessorOfNewHost.getRequirements().getRequirement();
                for (TRequirement requirement : openRequirements) {
                    QName requiredCapabilityTypeQName = getRequiredCapabilityTypeQNameOfRequirement(requirement);
                    TCapabilityType matchingBasisCapabilityType = getBasisCapabilityType(requiredCapabilityTypeQName);

                    if (matchingBasisCapabilityType.getName().equalsIgnoreCase("Container")) {
                        requiredRequirement = requirement;
                    }
                }

                TCapability requiredCapability = null;
                List<TCapability> openCapabilities = newMatchingNodeTemplate.getCapabilities().getCapability();
                for (TCapability capability : openCapabilities) {
                    TCapabilityType basisCapabilityType = getBasisCapabilityType(capability.getType());
                    if (basisCapabilityType.getName().equalsIgnoreCase("Container")) {
                        requiredCapability = capability;
                    }
                }
                if (requiredRequirement == null || requiredCapability == null) {
                    throw new SplittingException("The predecessor or new host node has matching which requires a hostedOn relationship");
                } else {
                    TRelationshipType relationshipType = getMatchingRelationshipType(requiredRequirement, requiredCapability);
                    if (relationshipType != null) {
                        QName relationshipTypeQName = new QName(relationshipType.getTargetNamespace(), relationshipType.getName());
                        newHostedOnRelationship.setType(relationshipTypeQName);
                    } else {
                        throw new SplittingException("No suitable relationship type available for hosting the node template with the ID "
                            + predecessorOfNewHost.getId());
                    }
                }

                topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(newHostedOnRelationship);
            } else {
                LOGGER.debug("Predecessor has successor NodeTemplates...");
                //Assupmtion: Only one hostedOn Successor possible
                TNodeTemplate originHost = originHostSuccessors.get(0);
                List<TRelationshipTemplate> incomingRelationshipsOfReplacementCandidate =
                    ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, originHost);

                //The incoming Relationships not from the predecessors have to be copied
                List<TRelationshipTemplate> incomingRelationshipsNotHostedOn =
                    incomingRelationshipsOfReplacementCandidate.stream()
                        .filter(r -> !getHostedOnPredecessorsOfNodeTemplate(topologyTemplate, originHost)
                            .contains(ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(topologyTemplate, r)))
                        .collect(Collectors.toList());

                topologyTemplate.getNodeTemplateOrRelationshipTemplate()
                    .addAll(reassignIncomingRelationships(incomingRelationshipsNotHostedOn, newMatchingNodeTemplate));

                //The incoming Relationships from the currently considered predecessor should be switched
                incomingRelationshipsOfReplacementCandidate.stream()
                    .filter(rt -> rt.getSourceElement().getRef().equals(predecessorOfNewHost))
                    .forEach(rt -> rt.getTargetElement().setRef(newMatchingNodeTemplate));

                // All outgoing rel of the origin hosts are copied and reassigned to the new host as source
                List<TRelationshipTemplate> outgoingRelationshipsOfReplacementCandidateNotHostedOn =
                    ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, originHost)
                        .stream()
                        .filter(r -> !getHostedOnSuccessorsOfNodeTemplate(topologyTemplate, originHost).contains(r.getTargetElement().getRef()))
                        .collect(Collectors.toList());

                topologyTemplate.getNodeTemplateOrRelationshipTemplate()
                    .addAll(reassignOutgoingRelationships(outgoingRelationshipsOfReplacementCandidateNotHostedOn, newMatchingNodeTemplate));

                replacedNodeTemplatesToDelete.add(originHost);
            }
        }

        switch (removal) {
            case REMOVE_NOTHING:
                return topologyTemplate;
            case REMOVE_REPLACED:
                for (TNodeTemplate deleteOriginNode : replacedNodeTemplatesToDelete) {
                    topologyTemplate.getNodeTemplateOrRelationshipTemplate()
                        .removeAll(ModelUtilities
                            .getIncomingRelationshipTemplates(topologyTemplate, deleteOriginNode));
                    topologyTemplate.getNodeTemplateOrRelationshipTemplate()
                        .removeAll(ModelUtilities
                            .getOutgoingRelationshipTemplates(topologyTemplate, deleteOriginNode));
                }
                topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(replacedNodeTemplatesToDelete);
                return topologyTemplate;
            default:
                break;
        }

        Map<TNodeTemplate, Set<TNodeTemplate>> transitiveAndDirectSuccessors = computeTransitiveClosure(topologyTemplate);
        // Delete all replaced Nodes and their direct and transitive hostedOn successors
        for (TNodeTemplate deleteOriginNode : replacedNodeTemplatesToDelete) {
            if (!transitiveAndDirectSuccessors.get(deleteOriginNode).isEmpty()) {
                for (TNodeTemplate successor : transitiveAndDirectSuccessors.get(deleteOriginNode)) {
                    topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, successor));
                    topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, successor));
                }
            }
            topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(transitiveAndDirectSuccessors.get(deleteOriginNode));
            topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, deleteOriginNode));
            topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, deleteOriginNode));
        }
        topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(replacedNodeTemplatesToDelete);

        LOGGER.debug("Resulting topology has {} NodeTemplates...", topologyTemplate.getNodeTemplates().size());
        return topologyTemplate;
    }

    /**
     *
     *
     * @param topologyTemplate
     * @return
     * @throws SplittingException
     */
    public Map<String, List<TTopologyTemplate>> getConnectionInjectionOptions(TTopologyTemplate topologyTemplate) throws SplittingException {
        ProviderRepository providerRepository = new ProviderRepository();
        Map<String, List<TTopologyTemplate>> connectionInjectionOptions = new HashMap<>();
        List<TNodeTemplate> nodeTemplates = ModelUtilities.getAllNodeTemplates(topologyTemplate);
        List<TNodeTemplate> nodeTemplatesWithConnectionRequirement = nodeTemplates.stream()
            .filter(nt -> nt.getRequirements() != null)
            .filter(nt -> nt.getRequirements().getRequirement().stream()
                .anyMatch(req -> getBasisCapabilityType(getRequiredCapabilityTypeQNameOfRequirement(req)).getName().equalsIgnoreCase("Endpoint")))
            .collect(Collectors.toList());

        if (!nodeTemplatesWithConnectionRequirement.isEmpty()) {
            for (TNodeTemplate nodeWithOpenConnectionRequirement : nodeTemplatesWithConnectionRequirement) {
                List<TRequirement> requirements = nodeWithOpenConnectionRequirement.getRequirements().getRequirement().stream()
                    .filter(req -> getBasisCapabilityType(getRequiredCapabilityTypeQNameOfRequirement(req)).getName().equalsIgnoreCase("Endpoint"))
                    .filter(req -> getOpenRequirementsAndMatchingBasisCapabilityTypeNames(topologyTemplate).keySet().contains(req))
                    .collect(Collectors.toList());

                for (TRequirement openRequirement : requirements) {

                    List<TTopologyTemplate> matchingTopologyFragments = providerRepository.getAllTopologyFragmentsForLocationAndOfferingCapability("*",
                        openRequirement);

                    if (!matchingTopologyFragments.isEmpty()) {
                        // instead of the Node Template ID the Requirement ID is added because one Node Template can occur multiple times for connection injection
                        connectionInjectionOptions.put(openRequirement.getId(), matchingTopologyFragments);
                    } else {
                        throw new SplittingException("No matching found");
                    }
                }
            }
        } else {
            connectionInjectionOptions = null;
            LOGGER.debug("No open requirements found");
        }
        return connectionInjectionOptions;
    }

    /**
     *
     * @param topologyTemplate
     * @param selectedConnectionFragments
     * @return
     * @throws SplittingException
     */

    public TTopologyTemplate injectConnectionNodeTemplates(TTopologyTemplate topologyTemplate, Map<String, TTopologyTemplate> selectedConnectionFragments)
        throws SplittingException {
        List<TNodeTemplate> nodeTemplates = ModelUtilities.getAllNodeTemplates(topologyTemplate);
        for (String openRequirementId : selectedConnectionFragments.keySet()) {
            TNodeTemplate nodeTemplateWithThisOpenReq = nodeTemplates.stream()
                .filter(nt -> nt.getRequirements() != null)
                .filter(nt -> nt.getRequirements().getRequirement().stream().anyMatch(req -> req.getId().equals(openRequirementId)))
                .findFirst().get();

            TRequirement openRequirement = nodeTemplateWithThisOpenReq.getRequirements().getRequirement().stream()
                .filter(req -> req.getId().equals(openRequirementId)).findFirst().get();

            QName requiredCapabilityTypeQName = getRequiredCapabilityTypeQNameOfRequirement(openRequirement);

            selectedConnectionFragments.get(openRequirementId).getNodeTemplateOrRelationshipTemplate().stream()
                .filter(et -> topologyTemplate.getNodeTemplateOrRelationshipTemplate().stream().anyMatch(tet -> tet.getId().equals(et.getId())))
                .forEach(et -> et.setId(et.getId() + "_" + IdCounter++));

            topologyTemplate.getNodeTemplateOrRelationshipTemplate()
                .addAll(selectedConnectionFragments.get(openRequirementId).getNodeTemplateOrRelationshipTemplate());
            nodeTemplates.addAll(ModelUtilities.getAllNodeTemplates(selectedConnectionFragments.get(openRequirementId)));

            TNodeTemplate nodeWithOpenCapability = nodeTemplates.stream()
                .filter(nt -> nt.getCapabilities() != null)
                .filter(nt -> nt.getCapabilities().getCapability().stream()
                    .anyMatch(c -> c.getType().equals(requiredCapabilityTypeQName))).findFirst().get();
            TCapability matchingCapability = nodeWithOpenCapability.getCapabilities().getCapability()
                .stream().filter(c -> c.getType().equals(requiredCapabilityTypeQName)).findFirst().get();

            TRelationshipType matchingRelationshipType =
                getMatchingRelationshipType(openRequirement, matchingCapability);

            if (matchingRelationshipType != null) {

                addMatchingRelationshipTemplateToTopologyTemplate(topologyTemplate, matchingRelationshipType, openRequirement, matchingCapability);
            } else {
                throw new SplittingException("No suitable relationship type found for matching");
            }
        }
        return topologyTemplate;
    }

    /**
     *
     * @param requirement
     * @param capability
     * @return
     */
    private TRelationshipType getMatchingRelationshipType(TRequirement requirement, TCapability capability) {

        TRelationshipType matchingRelationshipType = null;

        SortedSet<RelationshipTypeId> relTypeIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(RelationshipTypeId.class);
        List<TRelationshipType> relationshipTypes = new ArrayList<>();
        for (RelationshipTypeId id : relTypeIds) {
            relationshipTypes.add(RepositoryFactory.getRepository().getElement(id));
        }

        Map<String, String> requirementProperties = ModelUtilities.getPropertiesKV(requirement);
        Map<String, String> capabilityProperties = ModelUtilities.getPropertiesKV(capability);

		/* If the property "requiredRelationshipType" is defined for the requirement and the capability this relationship type
           has to be taken - if the specified relationship type is not available, no relationship type is chosen */
        if (requirementProperties != null && capabilityProperties != null &&
            requirementProperties.containsKey("requiredRelationshipType") && capabilityProperties.containsKey("requiredRelationshipType")
            && requirementProperties.get("requiredRelationshipType").equals(capabilityProperties.get("requiredRelationshipType"))
            && requirementProperties.get("requiredRelationshipType") != null) {
            QName referencedRelationshipType = QName.valueOf(requirementProperties.get("requiredRelationshipType"));
            RelationshipTypeId relTypeId = new RelationshipTypeId(referencedRelationshipType);
            if (relTypeIds.stream().anyMatch(rti -> rti.equals(relTypeId))) {
                return RepositoryFactory.getRepository().getElement(relTypeId);
            }
        } else {
            QName requirementTypeQName = requirement.getType();
            RequirementTypeId reqTypeId = new RequirementTypeId(requirement.getType());
            TRequirementType requirementType = RepositoryFactory.getRepository().getElement(reqTypeId);

            QName capabilityTypeQName = capability.getType();
            CapabilityTypeId capTypeId = new CapabilityTypeId(capability.getType());
            TCapabilityType capabilityType = RepositoryFactory.getRepository().getElement(capTypeId);

            List<TRelationshipType> availableMatchingRelationshipTypes = new ArrayList<>();
            availableMatchingRelationshipTypes.clear();

            while (requirementType != null && capabilityType != null) {

                //relationship type with valid source origin requirement type or empty and valid target origin capability type
                for (TRelationshipType rt : relationshipTypes) {
                    if ((rt.getValidSource() == null || rt.getValidSource().getTypeRef().equals(requirementTypeQName)) && (rt.getValidTarget() != null && rt.getValidTarget().getTypeRef().equals(capabilityTypeQName))) {
                        availableMatchingRelationshipTypes.add(rt);
                    }
                }

                if (!availableMatchingRelationshipTypes.isEmpty() && availableMatchingRelationshipTypes.size() == 1) {
                    return availableMatchingRelationshipTypes.get(0);
                } else if (!availableMatchingRelationshipTypes.isEmpty() && availableMatchingRelationshipTypes.size() > 1) {
                    return null;
                } else if (requirementType.getDerivedFrom() != null || capabilityType.getDerivedFrom() != null) {

                    TCapabilityType derivedFromCapabilityType = null;
                    TRequirementType derivedFromRequirementType = null;

                    availableMatchingRelationshipTypes.clear();
                    List<TRelationshipType> additionalMatchingRelationshipTypes = new ArrayList<>();

                    if (capabilityType.getDerivedFrom() != null) {
                        QName derivedFromCapabilityTypeRef = capabilityType.getDerivedFrom().getTypeRef();
                        CapabilityTypeId derivedFromCapTypeId = new CapabilityTypeId(derivedFromCapabilityTypeRef);
                        derivedFromCapabilityType = RepositoryFactory.getRepository().getElement(derivedFromCapTypeId);

                        for (TRelationshipType rt : relationshipTypes) {
                            if ((rt.getValidSource() == null || rt.getValidSource().getTypeRef().equals(requirementTypeQName)) && (rt.getValidTarget() != null && rt.getValidTarget().getTypeRef().equals(derivedFromCapabilityTypeRef))) {
                                availableMatchingRelationshipTypes.add(rt);
                            }
                        }
                    }
                    if (requirementType.getDerivedFrom() != null) {
                        QName derivedFromRequirementTypeRef = requirementType.getDerivedFrom().getTypeRef();
                        RequirementTypeId derivedFromReqTypeId = new RequirementTypeId(derivedFromRequirementTypeRef);
                        derivedFromRequirementType = RepositoryFactory.getRepository().getElement(derivedFromReqTypeId);

                        for (TRelationshipType rt : relationshipTypes) {
                            if ((rt.getValidSource() != null && rt.getValidSource().getTypeRef().equals(derivedFromRequirementTypeRef)) && (rt.getValidTarget() != null && rt.getValidTarget().getTypeRef().equals(capabilityTypeQName))) {
                                additionalMatchingRelationshipTypes.add(rt);
                            }
                        }
                    }

                    availableMatchingRelationshipTypes.addAll(additionalMatchingRelationshipTypes);

                    if (!availableMatchingRelationshipTypes.isEmpty() && availableMatchingRelationshipTypes.size() == 1) {
                        return availableMatchingRelationshipTypes.get(0);
                    } else if (!availableMatchingRelationshipTypes.isEmpty() && availableMatchingRelationshipTypes.size() > 1) {
                        return null;
                    }

                    requirementType = derivedFromRequirementType;
                    capabilityType = derivedFromCapabilityType;
                }
            }

            TCapabilityType basisCapabilityType = getBasisCapabilityType(capability.getType());

            for (TRelationshipType relationshipType : relationshipTypes) {
                if (basisCapabilityType != null && basisCapabilityType.getName().equalsIgnoreCase("container")
                    && relationshipType.getName().equalsIgnoreCase("hostedon") && relationshipType.getValidSource() == null
                    && (relationshipType.getValidTarget() == null || relationshipType.getValidTarget().getTypeRef().getLocalPart().equalsIgnoreCase("container"))) {
                    return relationshipType;
                }
                if (basisCapabilityType != null && basisCapabilityType.getName().equalsIgnoreCase("endpoint")
                    && relationshipType.getName().equalsIgnoreCase("connectsto") && relationshipType.getValidSource() == null
                    && (relationshipType.getValidTarget() == null || relationshipType.getValidTarget().getTypeRef().getLocalPart().equalsIgnoreCase("endpoint"))) {
                    return relationshipType;
                }
            }
        }
        return matchingRelationshipType;
    }

    /**
     * Switch the source of a relationship to a new node template
     *
     * @param outgoingRel all relationships which has to be switched to a new source element
     * @param newSource   the new source element
     * @return list of the reassigned relationships
     */
    private List<TRelationshipTemplate> reassignOutgoingRelationships(List<TRelationshipTemplate> outgoingRel, TNodeTemplate newSource) {

        List<TRelationshipTemplate> newOutgoingRel = new ArrayList<>();
        for (TRelationshipTemplate outgoingRelationship : outgoingRel) {
            TRelationshipTemplate newOutgoingRelationship = BackendUtils.clone(outgoingRelationship);
            TRelationshipTemplate.SourceOrTargetElement sourceElementNew = new TRelationshipTemplate.SourceOrTargetElement();
            sourceElementNew.setRef(newSource);
            newOutgoingRelationship.setSourceElement(sourceElementNew);

            //TargetLable is only appended if not done yet
            if (outgoingRelationship.getId().contains(ModelUtilities.getTargetLabel(newSource).get())) {
                newOutgoingRelationship.setId(Util.makeNCName(outgoingRelationship.getId()));
                newOutgoingRelationship.setName(Util.makeNCName(outgoingRelationship.getName()));
            } else {
                newOutgoingRelationship.setId(Util.makeNCName(outgoingRelationship.getId() + "-" + ModelUtilities.getTargetLabel(newSource).get()));
                newOutgoingRelationship.setName(Util.makeNCName(outgoingRelationship.getName() + "-" + ModelUtilities.getTargetLabel(newSource).get()));
            }
            newOutgoingRel.add(newOutgoingRelationship);
        }
        return newOutgoingRel;
    }

    /**
     * Switch the target of a relationship to a new node template
     *
     * @param incomingRel all relationships which has to be switched to a new target element
     * @param newTarget   the new target element
     * @return list of the reassigned relationships
     */
    private List<TRelationshipTemplate> reassignIncomingRelationships(List<TRelationshipTemplate> incomingRel, TNodeTemplate newTarget) {
        List<TRelationshipTemplate> newIncomingRel = new ArrayList<>();
        for (TRelationshipTemplate incomingRelationship : incomingRel) {
            TRelationshipTemplate newIncomingRelationship = BackendUtils.clone(incomingRelationship);
            TRelationshipTemplate.SourceOrTargetElement targetElementNew = new TRelationshipTemplate.SourceOrTargetElement();
            targetElementNew.setRef(newTarget);
            newIncomingRelationship.setTargetElement(targetElementNew);

            //TargetLable is only appended if not done yet
            if (incomingRelationship.getId().contains(ModelUtilities.getTargetLabel(newTarget).get())) {
                newIncomingRelationship.setId(Util.makeNCName(incomingRelationship.getId()));
                newIncomingRelationship.setName(Util.makeNCName(incomingRelationship.getName()));
            } else {
                newIncomingRelationship.setId(Util.makeNCName(incomingRelationship.getId() + "-" + ModelUtilities.getTargetLabel(newTarget).get()));
                newIncomingRelationship.setName(Util.makeNCName(incomingRelationship.getName() + "-" + ModelUtilities.getTargetLabel(newTarget).get()));
            }
            newIncomingRel.add(newIncomingRelationship);
        }
        return newIncomingRel;
    }

    /**
     * Find all lowest level nodes which are not application-specific nodes which are not contained in the
     * matchingNodeTemplates list
     *
     * @param topologyTemplate      topology template
     * @param matchingNodeTemplates lowest level nodes contained in this list are not considered as candidates
     * @return list of all node templates which are candidates for replacement
     */
    protected List<TNodeTemplate> getReplacementNodeTemplateCandidatesForMatching(TTopologyTemplate topologyTemplate, List<TNodeTemplate> matchingNodeTemplates) {

        return ModelUtilities.getAllNodeTemplates(topologyTemplate)
            .stream()
            .filter(y -> !matchingNodeTemplates.contains(y))
            .filter(y -> !getNodeTemplatesWithoutIncomingHostedOnRelationships(topologyTemplate).contains(y))
            .filter(z -> getNodeTemplatesWithoutOutgoingHostedOnRelationships(topologyTemplate).contains(z))
            .collect(Collectors.toList());
    }

    /**
     * Find all node templates which has no incoming hostedOn relationships (highest level nodes)
     *
     * @return list of node templates
     */
    protected List<TNodeTemplate> getNodeTemplatesWithoutIncomingHostedOnRelationships(TTopologyTemplate topologyTemplate) {

        return ModelUtilities.getAllNodeTemplates(topologyTemplate)
            .stream()
            .filter(nt -> getHostedOnPredecessorsOfNodeTemplate(topologyTemplate, nt).isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * Find all node templates which has no outgoing hostedOn relationships (lowest level nodes)
     *
     * @return list of node templates
     */
    protected List<TNodeTemplate> getNodeTemplatesWithoutOutgoingHostedOnRelationships(TTopologyTemplate topologyTemplate) {

        return topologyTemplate.getNodeTemplates().stream()
            .filter(nt -> getHostedOnSuccessorsOfNodeTemplate(topologyTemplate, nt).isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * Find all node templates which predecessors has no further predecessors
     *
     * @return list of nodes
     */
    protected List<TNodeTemplate> getNodeTemplatesWhichPredecessorsHasNoPredecessors(TTopologyTemplate topologyTemplate) {
        List<TNodeTemplate> nodeTemplates = ModelUtilities.getAllNodeTemplates(topologyTemplate);

        List<TNodeTemplate> predecessorsOfpredecessors = new ArrayList<>();
        predecessorsOfpredecessors.clear();
        List<TNodeTemplate> candidates = new ArrayList<>();
        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            List<TNodeTemplate> allPredecessors = getHostedOnPredecessorsOfNodeTemplate(topologyTemplate, nodeTemplate);
            if (!allPredecessors.isEmpty()) {
                predecessorsOfpredecessors.clear();
                for (TNodeTemplate predecessor : allPredecessors) {
                    predecessorsOfpredecessors.addAll(getHostedOnPredecessorsOfNodeTemplate(topologyTemplate, predecessor));
                }
                if (predecessorsOfpredecessors.isEmpty()) {
                    candidates.add(nodeTemplate);
                }
            }
        }
        return candidates;
    }

    /**
     * Find all successors of a node template. the successor is the source of a hostedOn relationship to the
     * nodeTemplate
     *
     * @param nodeTemplate for which all successors should be found
     * @return list of successors (node templates)
     */
    protected List<TNodeTemplate> getHostedOnSuccessorsOfNodeTemplate(TTopologyTemplate topologyTemplate, TNodeTemplate nodeTemplate) {
        List<TNodeTemplate> successorNodeTemplates = new ArrayList<>();
        for (TRelationshipTemplate relationshipTemplate : ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate)) {
            if (getBasisRelationshipType(relationshipTemplate.getType()).getValidTarget() != null &&
                getBasisRelationshipType(relationshipTemplate.getType()).getValidTarget().getTypeRef().getLocalPart().equalsIgnoreCase("Container")) {
                successorNodeTemplates.add(ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(topologyTemplate, relationshipTemplate));
            }
        }
        return successorNodeTemplates;
    }

    /**
     * Find all predecessors of a node template. the predecessor is the target of a hostedOn relationship to the
     * nodeTemplate
     *
     * @param nodeTemplate for which all predecessors should be found
     * @return list of predecessors
     */
    protected List<TNodeTemplate> getHostedOnPredecessorsOfNodeTemplate(TTopologyTemplate topologyTemplate, TNodeTemplate nodeTemplate) {
        List<TNodeTemplate> predecessorNodeTemplates = new ArrayList<>();
        predecessorNodeTemplates.clear();
        List<TRelationshipTemplate> incomingRelationships = ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, nodeTemplate);
        for (TRelationshipTemplate relationshipTemplate : incomingRelationships) {
            if (getBasisRelationshipType(relationshipTemplate.getType()).getValidTarget() != null &&
                getBasisRelationshipType(relationshipTemplate.getType()).getValidTarget().getTypeRef().getLocalPart().equalsIgnoreCase("Container")) {
                predecessorNodeTemplates.add(ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(topologyTemplate, relationshipTemplate));
            }
        }
        return predecessorNodeTemplates;
    }

    /**
     * Compute transitive closure of a given topology template based on the hostedOn relationships
     */

    public Map<TNodeTemplate, Set<TNodeTemplate>> computeTransitiveClosure(TTopologyTemplate topologyTemplate) {
        List<TNodeTemplate> nodeTemplates = new ArrayList<>(topologyTemplate.getNodeTemplates());

        for (TNodeTemplate node : nodeTemplates) {
            initDirectSuccessors.put(node, new HashSet<>(getHostedOnSuccessorsOfNodeTemplate(topologyTemplate, node)));
            visitedNodeTemplates.put(node, false);
            transitiveAndDirectSuccessors.put(node, new HashSet<>());
        }
        for (TNodeTemplate node : nodeTemplates) {
            if (!visitedNodeTemplates.get(node)) {
                computeNodeForTransitiveClosure(node);
            }
        }

        return transitiveAndDirectSuccessors;
    }

    /**
     * Helper method to compute the transitive closure
     */
    private void computeNodeForTransitiveClosure(TNodeTemplate nodeTemplate) {
        visitedNodeTemplates.put(nodeTemplate, true);
        Set<TNodeTemplate> successorsToCheck;
        successorsToCheck = initDirectSuccessors.get(nodeTemplate);
        successorsToCheck.removeAll(transitiveAndDirectSuccessors.get(nodeTemplate));

        for (TNodeTemplate successorToCheck : successorsToCheck) {
            if (!visitedNodeTemplates.get(successorToCheck)) {
                computeNodeForTransitiveClosure(successorToCheck);
            }
            transitiveAndDirectSuccessors.get(nodeTemplate).add(successorToCheck);
            transitiveAndDirectSuccessors.get(nodeTemplate).addAll(transitiveAndDirectSuccessors.get(successorToCheck));
        }
    }

    public Map<TRequirement, String> getOpenRequirementsAndMatchingBasisCapabilityTypeNames(TTopologyTemplate topologyTemplate) {
        Map<TRequirement, String> openRequirementsAndMatchingBaseCapability = new HashMap<>();
        List<TRequirement> openRequirements = getOpenRequirements(topologyTemplate);
        for (TRequirement requirement : openRequirements) {
            QName requiredCapabilityTypeQName = getRequiredCapabilityTypeQNameOfRequirement(requirement);
            TCapabilityType matchingBasisCapabilityType = getBasisCapabilityType(requiredCapabilityTypeQName);
            openRequirementsAndMatchingBaseCapability.put(requirement, matchingBasisCapabilityType.getName());
        }
        return openRequirementsAndMatchingBaseCapability;
    }

    /**
     *
     * @param topologyTemplate
     * @return
     */
    public List<TRequirement> getOpenRequirements(TTopologyTemplate topologyTemplate) {
        List<TRequirement> openRequirements = new ArrayList<>();
        List<TNodeTemplate> nodeTemplates = ModelUtilities.getAllNodeTemplates(topologyTemplate);

        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            if (nodeTemplate.getRequirements() != null) {
                List<TRequirement> containedRequirements = nodeTemplate.getRequirements().getRequirement();
                List<TNodeTemplate> successorsOfNodeTemplate = new ArrayList<>();
                List<TRelationshipTemplate> outgoingRelationships = ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate);
                if (outgoingRelationships != null && !outgoingRelationships.isEmpty()) {
                    for (TRelationshipTemplate relationshipTemplate : outgoingRelationships) {
                        if (relationshipTemplate.getSourceElement().getRef() instanceof TNodeTemplate) {
                            successorsOfNodeTemplate.add((TNodeTemplate) relationshipTemplate.getTargetElement().getRef());
                        } else {
                            TCapability targetElement = (TCapability) relationshipTemplate.getTargetElement().getRef();
                            successorsOfNodeTemplate.add(nodeTemplates.stream()
                                .filter(nt -> nt.getCapabilities() != null)
                                .filter(nt -> nt.getCapabilities().getCapability().stream().anyMatch(c -> c.getId().equals(targetElement.getId()))).findAny().get());
                        }
                    }
                }
                for (TRequirement requirement : containedRequirements) {
                    QName requiredCapabilityTypeQName = getRequiredCapabilityTypeQNameOfRequirement(requirement);

                    if (!successorsOfNodeTemplate.isEmpty()) {
                        boolean existingCap = successorsOfNodeTemplate.stream()
                            .filter(x -> x.getCapabilities() != null)
                            .anyMatch(x -> x.getCapabilities().getCapability().stream().anyMatch(y -> y.getType().equals(requiredCapabilityTypeQName)));
                        if (!existingCap) {
                            openRequirements.add(requirement);
                        }
                    } else {
                        openRequirements.add(requirement);
                    }
                }
            }
        }
        return openRequirements;
    }

    private TCapabilityType getBasisCapabilityType(QName capabilityTypeQName) {
        CapabilityTypeId parentCapTypeId = new CapabilityTypeId(capabilityTypeQName);
        TCapabilityType parentCapabilityType = RepositoryFactory.getRepository().getElement(parentCapTypeId);
        TCapabilityType basisCapabilityType = parentCapabilityType;

        while (parentCapabilityType != null) {
            basisCapabilityType = parentCapabilityType;

            if (parentCapabilityType.getDerivedFrom() != null) {
                capabilityTypeQName = parentCapabilityType.getDerivedFrom().getTypeRef();
                parentCapTypeId = new CapabilityTypeId(capabilityTypeQName);
                parentCapabilityType = RepositoryFactory.getRepository().getElement(parentCapTypeId);
            } else {
                parentCapabilityType = null;
            }
        }

        return basisCapabilityType;
    }

    private QName getRequiredCapabilityTypeQNameOfRequirement(TRequirement requirement) {
        QName reqTypeQName = requirement.getType();
        RequirementTypeId reqTypeId = new RequirementTypeId(reqTypeQName);
        TRequirementType requirementType = RepositoryFactory.getRepository().getElement(reqTypeId);
        return requirementType.getRequiredCapabilityType();
    }

    public TRelationshipType getBasisRelationshipType(QName relationshipTypeQName) {
        RelationshipTypeId parentRelationshipTypeId = new RelationshipTypeId(relationshipTypeQName);
        TRelationshipType parentRelationshipType = RepositoryFactory.getRepository().getElement(parentRelationshipTypeId);
        TRelationshipType basisRelationshipType = parentRelationshipType;

        while (parentRelationshipType != null) {
            basisRelationshipType = parentRelationshipType;

            if (parentRelationshipType.getDerivedFrom() != null) {
                relationshipTypeQName = parentRelationshipType.getDerivedFrom().getTypeRef();
                parentRelationshipTypeId = new RelationshipTypeId(relationshipTypeQName);
                parentRelationshipType = RepositoryFactory.getRepository().getElement(parentRelationshipTypeId);
            } else {
                parentRelationshipType = null;
            }
        }
        return basisRelationshipType;
    }

    private void addMatchingRelationshipTemplateToTopologyTemplate(TTopologyTemplate topologyTemplate, TRelationshipType relationshipType, TRequirement requirement, TCapability capability) {

        TRelationshipTemplate matchingRelationshipTemplate = new TRelationshipTemplate();

        QName relationshipTypeQName = new QName(relationshipType.getTargetNamespace(), relationshipType.getName());
        LOGGER.debug("The QName of the matchingRelationshipType for ReqCap Matching", relationshipTypeQName);
        List<String> ids = new ArrayList<>();
        List<TRelationshipTemplate> tRelationshipTemplates = ModelUtilities.getAllRelationshipTemplates(topologyTemplate);
        tRelationshipTemplates.forEach(rt -> ids.add(rt.getId()));
        //Check if counter is already set in another Id, if yes -> increase newRelationshipCounter +1
        boolean uniqueID = false;
        String id = "0";
        while (!uniqueID) {
            if (!ids.contains("con" + newRelationshipIdCounter)) {
                id = "con_" + newRelationshipIdCounter;
                newRelationshipIdCounter++;
                uniqueID = true;
            } else {
                newRelationshipIdCounter++;
            }
        }
        matchingRelationshipTemplate.setId(id);
        matchingRelationshipTemplate.setName(id);
        matchingRelationshipTemplate.setType(relationshipTypeQName);
        TRelationshipTemplate.SourceOrTargetElement sourceElement = new TRelationshipTemplate.SourceOrTargetElement();
        TRelationshipTemplate.SourceOrTargetElement targetElement = new TRelationshipTemplate.SourceOrTargetElement();
        sourceElement.setRef(requirement);
        targetElement.setRef(capability);
        matchingRelationshipTemplate.setSourceElement(sourceElement);
        matchingRelationshipTemplate.setTargetElement(targetElement);
        topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(matchingRelationshipTemplate);
    }

    /**
     * Check if the two given NodeTemplates are considered equal in terms of matching, which means that this
     * NodeTemplate is only injected once and used as a hostedOn predecessor for multiple other NodeTemplates. The
     * equality exists if the two NodeTemplates have the same target label and are equal with regard to all attributes
     * except the Id as the Id is replaced during matching.
     *
     * @param node1 the first NodeTemplate to compare
     * @param node2 the second NodeTemplate to compare
     * @return <code>true</code> if the two NodeTemplates are considered equal, <code>false</code> otherwise
     */
    private boolean equalsWithDifferentId(TNodeTemplate node1, TNodeTemplate node2) {
        if (node1 == node2) return true;

        // check if the two NodeTemplates have the same target label defined
        if (!node1.getOtherAttributes().get(ModelUtilities.QNAME_LOCATION)
            .equalsIgnoreCase(node2.getOtherAttributes().get(ModelUtilities.QNAME_LOCATION))) {
            return false;
        }

        // check properties if they are defined (just equals on properties does not seem to work)
        if (Objects.nonNull(node1.getProperties()) && Objects.nonNull(node2.getProperties())
            && Objects.nonNull(node1.getProperties().getKVProperties())
            && Objects.nonNull(node2.getProperties().getKVProperties())) {
            LinkedHashMap<String, String> properties1 = node1.getProperties().getKVProperties();
            LinkedHashMap<String, String> properties2 = node1.getProperties().getKVProperties();
            if (!properties1.equals(properties2)) {
                return false;
            }
        }

        // check if the NodeTemplates are equal (except Id which is replaced while matching)
        return Objects.equals(node1.getPropertyConstraints(), node2.getPropertyConstraints()) &&
            Objects.equals(node1.getType(), node2.getType()) &&
            Objects.equals(node1.getRequirements(), node2.getRequirements()) &&
            Objects.equals(node1.getCapabilities(), node2.getCapabilities()) &&
            Objects.equals(node1.getPolicies(), node2.getPolicies()) &&
            Objects.equals(node1.getDeploymentArtifacts(), node2.getDeploymentArtifacts()) &&
            Objects.equals(node1.getName(), node2.getName()) &&
            Objects.equals(node1.getMinInstances(), node2.getMinInstances()) &&
            Objects.equals(node1.getMaxInstances(), node2.getMaxInstances());
    }
}
