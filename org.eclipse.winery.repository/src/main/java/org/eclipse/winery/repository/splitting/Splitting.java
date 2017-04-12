/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Karoline Saatkamp - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.winery.repository.splitting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import org.slf4j.LoggerFactory;

public class Splitting {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Splitting.class);

	// Required variables for the following computation of the transitive closure of a given topology
	private Map<TNodeTemplate, Set<TNodeTemplate>> initDirectSuccessors = new HashMap<>();
	private Map<TNodeTemplate, Boolean> visitedNodeTemplates = new HashMap<>();
	private Map<TNodeTemplate, Set<TNodeTemplate>> transitiveAndDirectSuccessors = new HashMap<>();

	/**
	 * Splits the topology template of the given service template.
	 * Creates a new service template with "-split" suffix as id.
	 * Any existing "-split" service template will be deleted.
	 * Matches the split topology template to the cloud providers according to the target labels.
	 * Creates a new service template with "-matched" suffix as id.
	 * Any existing "-matched" service template will be deleted.
	 *
	 * @param id of the ServiceTemplate switch should be split and matched to cloud providers
	 * @return id of the ServiceTemplate which contains the matched topology
	 * @throws SplittingException
	 * @throws IOException
	 */
	public ServiceTemplateId splitTopologyOfServiceTemplate(ServiceTemplateId id) throws SplittingException, IOException {

		long start = System.currentTimeMillis();
		ServiceTemplateResource serviceTempateResource =
				(ServiceTemplateResource) AbstractComponentsResource.getComponentInstaceResource(id);

		// create wrapper service template
		ServiceTemplateId splitServiceTemplateId =
				new ServiceTemplateId(id.getNamespace().getDecoded(),
						id.getXmlId().getDecoded() + "-split", false);
		Repository.INSTANCE.forceDelete(splitServiceTemplateId);
		Repository.INSTANCE.flagAsExisting(splitServiceTemplateId);
		ServiceTemplateResource splitServiceTempateResource =
				(ServiceTemplateResource) AbstractComponentsResource.getComponentInstaceResource(splitServiceTemplateId);

		TTopologyTemplate splitTopologyTemplate = split(serviceTempateResource.getServiceTemplate().getTopologyTemplate());
		splitServiceTempateResource.getServiceTemplate().setTopologyTemplate(splitTopologyTemplate);
		LOGGER.debug("Persisting...");
		splitServiceTempateResource.persist();
		LOGGER.debug("Persisted.");

		// create wrapper service template
		ServiceTemplateId matchedServiceTemplateId =
				new ServiceTemplateId(id.getNamespace().getDecoded(),
						id.getXmlId().getDecoded() + "-split-matched", false);
		Repository.INSTANCE.forceDelete(matchedServiceTemplateId);
		Repository.INSTANCE.flagAsExisting(matchedServiceTemplateId);
		ServiceTemplateResource matchedTemplateResource =
				(ServiceTemplateResource) AbstractComponentsResource.getComponentInstaceResource(matchedServiceTemplateId);

		TTopologyTemplate matchedTopologyTemplate = matchingWithDefaultHostSelection(splitTopologyTemplate);
		matchedTemplateResource.getServiceTemplate().setTopologyTemplate(matchedTopologyTemplate);
		LOGGER.debug("Persisting...");
		matchedTemplateResource.persist();
		LOGGER.debug("Persisted.");

		long duration = System.currentTimeMillis() - start;
		LOGGER.debug("Execution Time in millisec: " + duration + "ms");

		return matchedServiceTemplateId;
	}

    /*
	 * Checks if a topology template is valid.
	 * The topology is valid if (1) all highest node templates have target labels assigned and
	 * (2) all successor nodes connected by hostedOn relationships have no other target labels then the predecessors.
	 *
	 * @param topologyTemplate the topology template which should be checked
	 * @return true if the topology template is valid and false if it is not
	 */
	public boolean checkValidTopology (TTopologyTemplate topologyTemplate) {
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
						&& !ModelUtilities.getTargetLabel(node).get().toLowerCase().equals(ModelUtilities.getTargetLabel(successor).get().toLowerCase())) {
							return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Splits a topology template according to the attached target labels.
	 * The target labels attached to nodes determine at which target the nodes should be deployed.
	 * The result is a topology template containing for each target the required nodes.
	 * Duplicates nodes which host nodes with different target labels.
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

			for (TNodeTemplate currentNode: nodeTemplatesWhichPredecessorsHasNoPredecessors) {

				List<TNodeTemplate> predecessors = getHostedOnPredecessorsOfNodeTemplate(topologyTemplateCopy, currentNode);
				Set<String> predecessorsTargetLabel = new HashSet<>();

				for (TNodeTemplate predecessor: predecessors) {
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
					for (String targetLabel: predecessorsTargetLabel) {
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
							if (sourceElementIncommingRel instanceof TNodeTemplate
									&& ((ModelUtilities.getTargetLabel((TNodeTemplate) sourceElementIncommingRel).get().toLowerCase()
									.equals(ModelUtilities.getTargetLabel(duplicatedNode).get().toLowerCase())
									&& incomingRelationship.getType().getLocalPart().toLowerCase().contains("hostedon"))
									|| !predecessors.contains(sourceElementIncommingRel))) {

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
						ModelUtilities.getAllRelationshipTemplates(topologyTemplateCopy).stream()
						.filter (rt -> rt.getSourceElement().getRef() instanceof TNodeTemplate
								|| rt.getTargetElement().getRef() instanceof TNodeTemplate)
						.filter(rt -> predecessors.contains(rt.getSourceElement().getRef())
								|| predecessors.contains(rt.getTargetElement().getRef()))
						.collect(Collectors.toList());

				topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().removeAll(removingRelationships);
			}
			nodeTemplatesWhichPredecessorsHasNoPredecessors.clear();
			nodeTemplatesWhichPredecessorsHasNoPredecessors.addAll(getNodeTemplatesWhichPredecessorsHasNoPredecessors(topologyTemplateCopy));
		}

		return topologyTemplate;
	}

	/**
	 * This method returns the possible hosts for each lowest level node. Before a suitable node is found nodes may be removed.
	 * @param topologyTemplate which should be mapped to the cloud provider according to the attached target labels
	 *                            - target labels have to be attached to each node
	 * @return map with a list of possible hosts for each lowest level node
	 */
	public Map<String, List<TNodeTemplate>> getInjectionOptions(TTopologyTemplate topologyTemplate) throws SplittingException {
		ProviderRepository repository = new ProviderRepository();
		Map<String, List<TNodeTemplate>> injectionOptions = new HashMap<>();
		// Contains all nodes for which at least one cloud provider node is found to host them
		List<TNodeTemplate> nodesForWhichHostsFound = new ArrayList<>();
		nodesForWhichHostsFound.clear();

		List <TNodeTemplate> needHostNodeTemplateCandidates = getNodeTemplatesWithoutOutgoingHostedOnRelationships(topologyTemplate);
		List<TNodeTemplate> nodesToCheck = new ArrayList<>();

		//Find lowest level nodes with open requirements which means they can be hosted by an other component
		for (int i = 0; i <= needHostNodeTemplateCandidates.size() - 1; i++) {
			if (needHostNodeTemplateCandidates.get(i).getRequirements() != null) {
				nodesToCheck.add(needHostNodeTemplateCandidates.get(i));
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
				}

				//noinspection OptionalGetWithoutIsPresent
				String targetLabel = ModelUtilities.getTargetLabel(needHostNode).get();

				List<TNodeTemplate> compatibleNodeTemplates = repository
						.getAllNodeTemplatesForLocationAndOfferingCapability(targetLabel, needHostNode.getRequirements().getRequirement());

				//Add compatible nodes to the injectionOptions to host the considered lowest level node
				if (!compatibleNodeTemplates.isEmpty()) {
					injectionOptions.put(needHostNode.getId(), compatibleNodeTemplates);
					nodesForWhichHostsFound.add(needHostNode);
				}
			}
		}

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
				}

				//noinspection OptionalGetWithoutIsPresent
				String targetLabel = ModelUtilities.getTargetLabel(replacementCandidate).get();

				// For each replacement candidate the predecessors are considered
				for (TNodeTemplate predecessor : predecessorsOfReplacementCandidate) {
					// Check if a compatible node for the predecessor from the right provider is available
					List<TNodeTemplate> compatibleNodeTemplates = repository
							.getAllNodeTemplatesForLocationAndOfferingCapability(targetLabel, predecessor.getRequirements().getRequirement());
					//Add compatible nodes to the injectionOptions to host the considered lowest level node
					if (!compatibleNodeTemplates.isEmpty()) {
						injectionOptions.put(predecessor.getId(), compatibleNodeTemplates);
						nodesForWhichHostsFound.add(predecessor);
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
		List <TNodeTemplate> checkListAllNodesMatched = ModelUtilities.getAllNodeTemplates(topologyTemplate)
				.stream()
				.filter(z -> getNodeTemplatesWithoutOutgoingHostedOnRelationships(topologyTemplate).contains(z))
				.filter(y -> !nodesForWhichHostsFound.contains(y))
				.collect(Collectors.toList());

		if (!checkListAllNodesMatched.isEmpty()) {
			throw new SplittingException("No matching possible");
		}
		return injectionOptions;
	}

	/**
	 *
	 * @param topologyTemplate which has to be labeled with the default label '*' which includes all available
	 *                            provider repositories and then it is searched for matching options
	 * @return map with the host options for each lowest node of the topology
	 * @throws SplittingException
	 */
	public Map<String, List<TNodeTemplate>> getMatchingOptionsWithDefaultLabeling (TTopologyTemplate topologyTemplate) throws SplittingException {
		ModelUtilities.getAllNodeTemplates(topologyTemplate).forEach(t -> ModelUtilities.setTargetLabel(t, "*"));
		return getInjectionOptions(topologyTemplate);
	}

	public TTopologyTemplate matchingWithDefaultHostSelection (TTopologyTemplate topologyTemplate) throws SplittingException {
		TTopologyTemplate newTopologyTemplate = BackendUtils.clone(topologyTemplate);
		Map<String, List<TNodeTemplate>> matchingOptions = getInjectionOptions(newTopologyTemplate);
		Map<String, TNodeTemplate> defaultHostSelection = new HashMap<>();
		matchingOptions.entrySet().stream()
				.forEach(entry -> defaultHostSelection.put(entry.getKey(), entry.getValue().get(0)));

		TTopologyTemplate finishTopologyTemplate = injectNodeTemplates(topologyTemplate, defaultHostSelection);

		return finishTopologyTemplate;
	}

	/**
	 * Replaces the host of each key by the value of the map.
	 * Adds new relationships between the nodes and their new hosts
	 *
	 * @param topologyTemplate original topology for which the Node Templates shall be replaced
	 * @param injectNodes map with the Nodes to replace as key and the replacement as value
	 * @return modified topology with the replaced Node Templates
	 */
	public TTopologyTemplate injectNodeTemplates (TTopologyTemplate topologyTemplate, Map<String, TNodeTemplate> injectNodes) {
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
			TNodeTemplate newMatchingNodeTemplate;
			TNodeTemplate newHostNodeTemplate = injectNodes.get(predecessorOfNewHostId);

			boolean matchingFound = matching.stream()
					.anyMatch(nt -> ModelUtilities.getTargetLabel(nt).get().toLowerCase()
							.equals(ModelUtilities.getTargetLabel(newHostNodeTemplate).get().toLowerCase())
					&& nt.getId().equals(Util.makeNCName(newHostNodeTemplate.getId() + "-" + ModelUtilities.getTargetLabel(newHostNodeTemplate).get())));

			//Check if the chosen replace node is already in the matching
			if (!matchingFound) {
				newMatchingNodeTemplate = injectNodes.get(predecessorOfNewHostId);
				newMatchingNodeTemplate.setId(Util.makeNCName(newMatchingNodeTemplate.getId() + "-" + ModelUtilities.getTargetLabel(newMatchingNodeTemplate).get()));
				newMatchingNodeTemplate.setName(Util.makeNCName(newMatchingNodeTemplate.getName() + "-" + ModelUtilities.getTargetLabel(newMatchingNodeTemplate).get()));
				topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(newMatchingNodeTemplate);
				matching.add(newMatchingNodeTemplate);
			} else {
				newMatchingNodeTemplate = matching.stream().filter(nt -> ModelUtilities.getTargetLabel(nt).get().toLowerCase()
						.equals(ModelUtilities.getTargetLabel(newHostNodeTemplate).get().toLowerCase())
						&& nt.getId().equals(Util.makeNCName(newHostNodeTemplate.getId() + "-" + ModelUtilities.getTargetLabel(newHostNodeTemplate).get()))).findAny().get();
			}

			//In case the predecessor was a lowest node a new hostedOn relationship has to be added
			if (originHostSuccessors.isEmpty()) {
				TRelationshipTemplate newHostedOnRelationship = new TRelationshipTemplate();
				//TODO Fix naming of new Relationships (Util.makeNCName ?)
				newHostedOnRelationship.setId(UUID.randomUUID().toString());
				newHostedOnRelationship.setName(UUID.randomUUID().toString());
				TRelationshipTemplate.SourceElement sourceElement = new TRelationshipTemplate.SourceElement();
				TRelationshipTemplate.TargetElement targetElement = new TRelationshipTemplate.TargetElement();
				sourceElement.setRef(predecessorOfNewHost);
				targetElement.setRef(newMatchingNodeTemplate);
				newHostedOnRelationship.setSourceElement(sourceElement);
				newHostedOnRelationship.setTargetElement(targetElement);

				// Take the hostedOn relationship type from the repository
				Repository.INSTANCE.getAllTOSCAComponentIds(RelationshipTypeId.class)
						.stream()
						.filter(c -> c.getQName().getLocalPart().toLowerCase().contains("hostedon"))
						.findAny()
						.ifPresent(c -> newHostedOnRelationship.setType(c.getQName()));

				topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(newHostedOnRelationship);

			} else {
				//Assupmtion: Only one hostedOn Successor possible
				TNodeTemplate originHost = originHostSuccessors.get(0);
				List<TRelationshipTemplate> incomingRelationshipsOfReplacementCandidate =
						ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, originHost);

				//The incoming Relationships not from the predecessors have to be copied
				List<TRelationshipTemplate> incomingRelationshipsNotHostedOn =
						incomingRelationshipsOfReplacementCandidate
								.stream()
								.filter(rt -> rt.getSourceElement().getRef() instanceof TNodeTemplate)
								.filter(r -> !getHostedOnPredecessorsOfNodeTemplate(topologyTemplate, originHost).contains(r.getSourceElement().getRef()))
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

		Map<TNodeTemplate, Set<TNodeTemplate>> transitiveAndDirectSuccessors = computeTransitiveClosure(topologyTemplate);

		// Delete all replaced Nodes and their direct and transitive hostedOn successors
		for (TNodeTemplate deleteOriginNode : replacedNodeTemplatesToDelete) {
			if (!transitiveAndDirectSuccessors.get(deleteOriginNode).isEmpty()) {
				for (TNodeTemplate successor : transitiveAndDirectSuccessors.get(deleteOriginNode) ) {
					topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, successor));
					topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, successor));
				}
			}

			topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(transitiveAndDirectSuccessors.get(deleteOriginNode));
			topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, deleteOriginNode));
			topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, deleteOriginNode));

		}
		topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(replacedNodeTemplatesToDelete);

		return topologyTemplate;
	}



	/**
	 * Switch the source of a relationship to a new node template
	 * @param outgoingRel all relationships which has to be switched to a new source element
	 * @param newSource the new source element
	 * @return list of the reassigned relationships
	 */
	private List<TRelationshipTemplate> reassignOutgoingRelationships(List<TRelationshipTemplate> outgoingRel, TNodeTemplate newSource) {

		List<TRelationshipTemplate> newOutgoingRel = new ArrayList<>();
		for (TRelationshipTemplate outgoingRelationship : outgoingRel) {
			TRelationshipTemplate newOutgoingRelationship = BackendUtils.clone(outgoingRelationship);
			TRelationshipTemplate.SourceElement sourceElementNew = new TRelationshipTemplate.SourceElement();
			sourceElementNew.setRef(newSource);
			newOutgoingRelationship.setSourceElement(sourceElementNew);
			newOutgoingRelationship.setId(Util.makeNCName(outgoingRelationship.getId() + "-" + ModelUtilities.getTargetLabel(newSource).get()));
			newOutgoingRelationship.setName(Util.makeNCName(outgoingRelationship.getName() + "-" + ModelUtilities.getTargetLabel(newSource).get()));
			newOutgoingRel.add(newOutgoingRelationship);
		}
		return newOutgoingRel;
	}

	/**
	 * Switch the target of a relationship to a new node template
	 * @param incomingRel all relationships which has to be switched to a new target element
	 * @param newTarget the new target element
	 * @return list of the reassigned relationships
	 */
	private List<TRelationshipTemplate> reassignIncomingRelationships(List<TRelationshipTemplate> incomingRel, TNodeTemplate newTarget) {
		List<TRelationshipTemplate> newIncomingRel = new ArrayList<>();
		for (TRelationshipTemplate incomingRelationship : incomingRel) {
			TRelationshipTemplate newIncomingRelationship = BackendUtils.clone(incomingRelationship);
			TRelationshipTemplate.TargetElement targetElementNew = new TRelationshipTemplate.TargetElement();
			targetElementNew.setRef(newTarget);
			newIncomingRelationship.setTargetElement(targetElementNew);
			newIncomingRelationship.setId(Util.makeNCName(incomingRelationship.getId() + "-" + ModelUtilities.getTargetLabel(newTarget).get()));
			newIncomingRelationship.setName(Util.makeNCName(incomingRelationship.getName() + "-" + ModelUtilities.getTargetLabel(newTarget).get()));

			newIncomingRel.add(newIncomingRelationship);
		}
		return newIncomingRel;
	}

	/**
	 * Find all lowest level nodes which are not application-specific nodes which are not contained in the matchingNodeTemplates list
	 * @param topologyTemplate topology template
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
	 * @param topologyTemplate
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
	 * @param topologyTemplate
	 * @return list of node templates
	 */
	protected List<TNodeTemplate> getNodeTemplatesWithoutOutgoingHostedOnRelationships(TTopologyTemplate topologyTemplate) {

		return ModelUtilities.getAllNodeTemplates(topologyTemplate)
				.stream()
				.filter(nt -> getHostedOnSuccessorsOfNodeTemplate(topologyTemplate, nt).isEmpty())
				.collect(Collectors.toList());
	}


	/**
	 * Find all node templates which predecessors has no further predecessors
	 * @param topologyTemplate
	 * @return list of nodes
	 */
	protected List<TNodeTemplate> getNodeTemplatesWhichPredecessorsHasNoPredecessors(TTopologyTemplate topologyTemplate) {
		List<TNodeTemplate> nodeTemplates = ModelUtilities.getAllNodeTemplates(topologyTemplate);

		List<TNodeTemplate> predecessorsOfpredecessors = new ArrayList<>();
		predecessorsOfpredecessors.clear();
		List<TNodeTemplate> candidates = new ArrayList<>();
		for (TNodeTemplate nodeTemplate: nodeTemplates) {
			List<TNodeTemplate> allPredecessors = getHostedOnPredecessorsOfNodeTemplate(topologyTemplate, nodeTemplate);
			if (!allPredecessors.isEmpty()) {
				predecessorsOfpredecessors.clear();
				for (TNodeTemplate predecessor: allPredecessors) {
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
	 * Find all successors of a node template. the successor is the source of a hostedOn relationship to the nodeTemplate
	 * @param topologyTemplate
	 * @param nodeTemplate for which all successors should be found
	 * @return list of successors (node templates)
	 */
	protected List<TNodeTemplate> getHostedOnSuccessorsOfNodeTemplate(TTopologyTemplate topologyTemplate, TNodeTemplate nodeTemplate) {
		List<TNodeTemplate> successorNodeTemplates = new ArrayList<>();
		for (TRelationshipTemplate relationshipTemplate: ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate)) {
			if (relationshipTemplate.getTargetElement().getRef() instanceof TNodeTemplate
					&& relationshipTemplate.getType().getLocalPart().toLowerCase().contains("hostedon")) {
				successorNodeTemplates.add((TNodeTemplate) relationshipTemplate.getTargetElement().getRef());
			}
		}
		return successorNodeTemplates;
	}

	/**
	 * Find all predecessors of a node template. the predecessor is the target of a hostedOn relationship to the nodeTemplate
	 * @param topologyTemplate
	 * @param nodeTemplate for which all predecessors should be found
	 * @return list of predecessors
	 */
	protected List<TNodeTemplate> getHostedOnPredecessorsOfNodeTemplate(TTopologyTemplate topologyTemplate, TNodeTemplate nodeTemplate) {
		List<TNodeTemplate> predecessorNodeTemplates = new ArrayList<>();
		predecessorNodeTemplates.clear();
		List<TRelationshipTemplate> incomingRelationships = ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, nodeTemplate);
		for (TRelationshipTemplate relationshipTemplate: incomingRelationships) {
			if (relationshipTemplate.getSourceElement().getRef() instanceof TNodeTemplate
					&& relationshipTemplate.getType().getLocalPart().toLowerCase().contains("hostedon")) {
				predecessorNodeTemplates.add((TNodeTemplate) relationshipTemplate.getSourceElement().getRef());
			}
		}
		return predecessorNodeTemplates;
	}


	/**
	 * Compute transitive closure of a given topology template based on the hostedOn relationships
	 * @param topologyTemplate
	 * @return
	 */

	protected Map<TNodeTemplate, Set<TNodeTemplate>> computeTransitiveClosure (TTopologyTemplate topologyTemplate) {
		List<TNodeTemplate> nodeTemplates = new ArrayList<>(ModelUtilities.getAllNodeTemplates(topologyTemplate));

		for (TNodeTemplate node : nodeTemplates) {
			initDirectSuccessors.put(node, new HashSet<>(getHostedOnSuccessorsOfNodeTemplate(topologyTemplate, node)));
			visitedNodeTemplates.put(node, false);
			transitiveAndDirectSuccessors.put(node, new HashSet<>());
		}
		for (TNodeTemplate node: nodeTemplates) {
			if (!visitedNodeTemplates.get(node)) {
				computeNodeForTransitiveClosure(node);
			}
		}

		return transitiveAndDirectSuccessors;
	}

	/**
	 * Helper method to compute the transitive closure
	 * @param nodeTemplate
	 * @return
	 */
	private void computeNodeForTransitiveClosure (TNodeTemplate nodeTemplate) {
		visitedNodeTemplates.put(nodeTemplate, true);
		Set<TNodeTemplate> successorsToCheck;
		successorsToCheck = initDirectSuccessors.get(nodeTemplate);
		successorsToCheck.removeAll(transitiveAndDirectSuccessors.get(nodeTemplate));

		for (TNodeTemplate successorToCheck : successorsToCheck ) {
			if (!visitedNodeTemplates.get(successorToCheck)) {
				computeNodeForTransitiveClosure(successorToCheck);
			}
			transitiveAndDirectSuccessors.get(nodeTemplate).add(successorToCheck);
			transitiveAndDirectSuccessors.get(nodeTemplate).addAll(transitiveAndDirectSuccessors.get(successorToCheck));
		}
	}
}
