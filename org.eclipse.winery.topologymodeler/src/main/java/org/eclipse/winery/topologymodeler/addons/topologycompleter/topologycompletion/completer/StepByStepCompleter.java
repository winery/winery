/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion.completer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.TOSCAAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.NodeTemplateConnector;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.Utils;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.placeholderhandling.PlaceHolderHandler;

/**
 * This class handles topologies that are completed step by step
 */
public class StepByStepCompleter {

	/**
	 * the topology to be completed
	 */
	TTopologyTemplate topology;

	/**
	 * the Node and RelationshipTemplates chosen by the user in every step
	 */
	Map<TNodeTemplate, Map<TNodeTemplate, List<TEntityTemplate>>> templateChoices;

	/**
	 * the last inserted place holder to be deleted
	 */
	private TNodeTemplate placeHolder;

	/**
	 * The constructor of the class.
	 *
	 * @param topology
	 *            the {@link TTopologyTemplate} to be completed
	 */
	public StepByStepCompleter(TTopologyTemplate topology) {
		this.topology = topology;
	}

	/**
	 * This method is called when a topology containing {@link TRequirement}s is completed step by step.
	 *
	 * @param unfulfilledRequirements
	 *            a list of unfulfilled requirements
	 * @param placeHolders
	 *            a list of place holders to be fulfilled
	 * @param toscaAnalyzer
	 * 			  the {@link TOSCAAnalyzer} object to access the data model
	 */
	public void completeTopologyStepByStep(Map<TRequirement, TNodeTemplate> unfulfilledRequirements, TOSCAAnalyzer toscaAnalyzer) {

		Set<TRequirement> requirements = unfulfilledRequirements.keySet();

		TNodeTemplate nodeTemplate = null;

		for (TRequirement requirement : requirements) {
			// remove the requirement from the NodeTemplate
			TNodeTemplate requirementTemplate = unfulfilledRequirements.get(requirement);
			for (TEntityTemplate element : topology.getNodeTemplateOrRelationshipTemplate()) {
				if (requirementTemplate.getId().equals(element.getId())) {
					((TNodeTemplate) element).getRequirements().getRequirement().remove(requirement);
				}
			}

			List<TNodeType> possibleNodeTypes = Utils.matchRequirementAndCapability(requirement, toscaAnalyzer);

			// create a NodeTemplate for each matching node type
			List<TNodeTemplate> possibleTemplates = new ArrayList<>();
			for (TNodeType possibleType : possibleNodeTypes) {
				nodeTemplate = ModelUtilities.instantiateNodeTemplate(possibleType);
				possibleTemplates.add(nodeTemplate);
			}

			TNodeTemplate correspondingNodeTemplate = unfulfilledRequirements.get(requirement);

			Map<TNodeTemplate, List<TEntityTemplate>> entityTemplates = new HashMap<>();

			// add all possible choices to a list and return it to the user
			for (TNodeTemplate possibleTemplate : possibleTemplates) {
				List<TEntityTemplate> choices = new ArrayList<TEntityTemplate>();
				List<TRelationshipType> suitableRTs = NodeTemplateConnector.findRelationshipType(correspondingNodeTemplate, possibleTemplate, toscaAnalyzer, requirement);
				for (TRelationshipType rt : suitableRTs) {
					TRelationshipTemplate relationship = ModelUtilities.instantiateRelationshipTemplate(rt, correspondingNodeTemplate, possibleTemplate);
					choices.add(relationship);
				}
				entityTemplates.put(possibleTemplate, choices);
			}

			templateChoices = new HashMap<TNodeTemplate, Map<TNodeTemplate, List<TEntityTemplate>>>();
			templateChoices.put(correspondingNodeTemplate, entityTemplates);

			// let the user decide which template shall be inserted
			break;
		}

	}

	/**
	 * Completes a place holder {@link TTopologyTemplate} step by step.
	 *
	 * @param placeHolders
	 *            the place holders of the topology
	 * @param toscaAnalyzer
	 *            the {@link TOSCAAnalyzer} object to access the data model.
	 * @return the generic {@link TRelationshipTemplate} which connects to the place holder.
	 */
	public TRelationshipTemplate completeWildcardTopologyStepByStep(List<TNodeTemplate> placeHolders, TOSCAAnalyzer toscaAnalyzer) {

		// take the first place holder, the order doesn't matter in the step by step approach
		TNodeTemplate placeHolder = placeHolders.get(0);

		// get suitable NodeTypes for a placeholder and instantiate NodeTemplates
		List<TNodeType> suitableNodeTypes = PlaceHolderHandler.getSuitableNodeTypes(placeHolder, toscaAnalyzer);
		List<TNodeTemplate> suitableNodeTemplates = new ArrayList<TNodeTemplate>();
		for (TNodeType suitableNodeType : suitableNodeTypes) {
			TNodeTemplate nodeTemplate = ModelUtilities.instantiateNodeTemplate(suitableNodeType);
			suitableNodeTemplates.add(nodeTemplate);
		}

		/**
		 * map containing the choices for the user selection
		 */
		Map<TNodeTemplate, List<TEntityTemplate>> entityTemplates = new HashMap<>();

		TNodeTemplate sourceTemplate = null;

		// the RelationshipTemplate connecting to the placeholder
		TRelationshipTemplate connectingRelationshipTemplate = null;

		for (TEntityTemplate entity : topology.getNodeTemplateOrRelationshipTemplate()) {
			if (entity instanceof TRelationshipTemplate) {
				TRelationshipTemplate rt = (TRelationshipTemplate) entity;
				if (((TNodeTemplate) rt.getTargetElement().getRef()).getId().equals(placeHolder.getId())) {
					connectingRelationshipTemplate = (TRelationshipTemplate) entity;
					sourceTemplate = (TNodeTemplate) connectingRelationshipTemplate.getSourceElement().getRef();
				}
			}
		}

		for (TNodeTemplate nodeTemplate : suitableNodeTemplates) {

			List<TEntityTemplate> choices = new ArrayList<>();

			// find matching RelationshipTypes to connect the Node Templates
			List<TRelationshipType> suitableRTs = NodeTemplateConnector.findRelationshipType(sourceTemplate, nodeTemplate, toscaAnalyzer, null);
			for (TRelationshipType rt : suitableRTs) {
				TRelationshipTemplate relationship = ModelUtilities.instantiateRelationshipTemplate(rt, sourceTemplate, nodeTemplate);
				choices.add(relationship);
			}
			entityTemplates.put(nodeTemplate, choices);
		}

		templateChoices = new HashMap<TNodeTemplate, Map<TNodeTemplate, List<TEntityTemplate>>>();
		templateChoices.put(sourceTemplate, entityTemplates);

		this.placeHolder = placeHolder;

		return connectingRelationshipTemplate;

	}

	/**
	 * Returns a map containing the choices for the user selection when the topology is completed step by step.
	 *
	 * @return the field ntChoices
	 */
	public Map<TNodeTemplate, Map<TNodeTemplate, List<TEntityTemplate>>> getTemplateChoices() {
		return templateChoices;
	}

	/**
	 * Returns the replaced place holder to remove it from the topology.
	 *
	 * @return the place holder
	 */
	public TNodeTemplate getPlaceHolder() {
		return placeHolder;
	}
}
