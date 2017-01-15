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

package org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.DeferredAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.PlaceHolderAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.RequirementAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.TOSCAAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion.completer.DeferredCompleter;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion.completer.PlaceHolderCompleter;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion.completer.RequirementCompleter;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion.completer.StepByStepCompleter;

/**
 * This class manages the completion of a TOSCA {@link TTopologyTemplate}.
 */
public class CompletionManager {

	private static final Logger LOGGER = Logger.getLogger(CompletionManager.class.getName());

	/**
	 * {@link TOSCAAnalyzer} object to access the JAXB data model
	 */
	private final TOSCAAnalyzer toscaAnalyzer;

	/**
	 * Map containing the topology solutions.
	 *
	 * The first parameter of the map is an index used to traverse the map easily. The second parameter of the solutions map
	 * is another map containing a possible topology solution and a boolean value that determines if the topology is complete.
	 * When all topologies of the solution map are complete, it will be returned to Winery.
	 */
	private final Map<Integer, Map<TTopologyTemplate, Boolean>> solutions;

	/**
	 * Whether a step-by-step or an one-step approach is conducted
	 */
	private final boolean stepByStep;

	/**
	 * Map containing {@link TNodeTemplate}s and {@link TRelationshipTemplate}s to be chosen by the user in the step-by-step approach.
	 */
	private Map<TNodeTemplate, Map<TNodeTemplate, List<TEntityTemplate>>> templateChoices;

	/**
	 * Whether a user interaction for choosing inserted {@link TNodeTemplate}s and {@link TRelationshipTemplate}s is necessary or not.
	 */
	private boolean nodeTemplateUserInteraction = false;

	/**
	 * List containing {@link TRelationshipTemplate} to be chosen by the user.
	 */
	private List<TEntityTemplate> choices;

	/**
	 * Whether a user interaction for choosing inserted {@link TRelationshipTemplate}s is necessary or not.
	 */
	private boolean userInteraction = false;

	/**
	 * The index of the topology solutions map.
	 */
	private int index;

	/**
	 * The class constructor.
	 *
	 * @param toscaAnalyzer
	 *            the {@link TOSCAAnalyzer} object to access the data model
	 * @param stepByStep
	 *            whether the topology completion is processed step-by-step or not
	 */
	public CompletionManager(TOSCAAnalyzer toscaAnalyzer, boolean stepByStep) {
		this.toscaAnalyzer = toscaAnalyzer;
		this.stepByStep = stepByStep;
		this.index = 0;

		// instantiate the solution map
		solutions = new HashMap<Integer, Map<TTopologyTemplate, Boolean>>();
	}

	/**
	 * This recursive method analyzes and completes a TOSCA {@link TTopologyTemplate}.
	 *
	 * @param topology
	 *            the TOSCA {@link TTopologyTemplate} to be completed
	 *
	 * @return the complete TOSCA {@link TTopologyTemplate} object
	 */
	public List<TTopologyTemplate> manageCompletion(TTopologyTemplate topology) {

		// -------------------------
		// Analyze topology template
		// -------------------------

		// the data model must be cleared before analyzing the topology
		toscaAnalyzer.clear();

		// analyze the content of the topology template
		toscaAnalyzer.analyzeTOSCATopology(topology);

		// Note: The TOSCAAnalyzer object is used for the analysis to access the NodeTemplates or RelationshipTemplates directly,
		// so no cast from the parent type TEntityTemplate is required

		// --------------------------------
		// Analyze unfulfilled requirements
		// --------------------------------
		Map<TRequirement, TNodeTemplate> unfulfilledRequirements = RequirementAnalyzer.analyzeRequirements(toscaAnalyzer);

		// ---------------------------------------
		// Analyze the occurrence of place holders
		// ---------------------------------------
		List<TNodeTemplate> placeHolders = PlaceHolderAnalyzer.analyzePlaceHolders(toscaAnalyzer);

		// --------------------------------------------------------
		// Analyze the occurrence of deferred RelationshipTemplates
		// --------------------------------------------------------
		List<TRelationshipTemplate> deferredRelations = DeferredAnalyzer.analyzeDeferredRelations(toscaAnalyzer);

		// ---------------------
		// Complete the topology
		// ---------------------

		// with the step by step approach, a user interaction is always necessary. So the topology and
		// the choices will be returned in every step. A combination of the step-by-step approach and a deferred topology is not
		// possible because every path of the depth search can lead to a dead end.
		if (stepByStep && deferredRelations.isEmpty()) {
			LOGGER.info("Completing topology step by step.");

			List<TTopologyTemplate> solutionList = new ArrayList<TTopologyTemplate>();

			if (!unfulfilledRequirements.isEmpty() && placeHolders.isEmpty()) {

				// complete a topology containing requirements step by step using the StepByStepCompleter class
				StepByStepCompleter stepByStepCompleter = new StepByStepCompleter(topology);
				stepByStepCompleter.completeTopologyStepByStep(unfulfilledRequirements, toscaAnalyzer);

				// get the NodeTemplate choices for the user
				templateChoices = stepByStepCompleter.getTemplateChoices();
				nodeTemplateUserInteraction = true;

				solutionList.add(topology);

				LOGGER.info("Returning topology for user interaction");

				return solutionList;

			} else if (unfulfilledRequirements.isEmpty() && !placeHolders.isEmpty()) {

				// complete a topology containing place holders step by step using the StepByStepCompleter class
				StepByStepCompleter stepByStepCompleter = new StepByStepCompleter(topology);
				TRelationshipTemplate genericRelationship = stepByStepCompleter.completeWildcardTopologyStepByStep(placeHolders, toscaAnalyzer);

				// get the NodeTemplate selection for the user to choose
				templateChoices = stepByStepCompleter.getTemplateChoices();
				nodeTemplateUserInteraction = true;

				TNodeTemplate toBeRemoved = stepByStepCompleter.getPlaceHolder();
				topology.getNodeTemplateOrRelationshipTemplate().remove(toBeRemoved);
				topology.getNodeTemplateOrRelationshipTemplate().remove(genericRelationship);

				solutionList.add(topology);
				LOGGER.info("Returning topology for user interaction");

				return solutionList;
			} else if (unfulfilledRequirements.isEmpty() && placeHolders.isEmpty() && deferredRelations.isEmpty()) {

				// the topology is complete, return it to Winery

				LOGGER.info("The topology is complete.");

				nodeTemplateUserInteraction = false;
				solutionList.add(topology);
				return solutionList;
			}
		} else {
			// the one-step approach is chosen or the topology contains deferred-RelationshipTemplates
			if (unfulfilledRequirements.isEmpty() && placeHolders.isEmpty() && deferredRelations.isEmpty()) {
				// the topology does not contain any elements that have to completed, it can be defined as complete
				if (solutions.isEmpty()) {
					// no topology solutions found, topology could not be completed due to missing types.
					// Return an empty list, an error message will be shown in Winery.
					return new ArrayList<TTopologyTemplate>();
				} else {
					// this topology is complete, set its boolean value in the map to true
					for (Integer i : solutions.keySet()) {
						for (TTopologyTemplate t : solutions.get(i).keySet()) {
							if (t.equals(topology)) {
								solutions.get(i).put(topology, true);
							}
						}
					}

					// check if the map still contains any incomplete topologies. If this is the case, the recursion will continue.
					// Otherwise the solutions map is returned.
					if (!solutions.values().contains(false)) {
						LOGGER.info("The topology is complete.");
						List<TTopologyTemplate> sol = new ArrayList<TTopologyTemplate>();

						for (Integer i : solutions.keySet()) {
							sol.addAll(solutions.get(i).keySet());
						}
						return sol;
					}
				}
			} else if (!unfulfilledRequirements.isEmpty() && placeHolders.isEmpty() && deferredRelations.isEmpty()) {

				LOGGER.info("The topology contains Requirements, but no Place Holders.");

				// complete a topology containing Requirements in one step using the RequirementCompleter class
				RequirementCompleter requirementCompleter = new RequirementCompleter(topology);

				List<TTopologyTemplate> completeTopology = requirementCompleter.completeRequirementTopology(unfulfilledRequirements, toscaAnalyzer);

				for (TTopologyTemplate topologySolution : completeTopology) {
					Map<TTopologyTemplate, Boolean> topologyMap = new HashMap<TTopologyTemplate, Boolean>();
					topologyMap.put(topologySolution, false);
					solutions.put(index, topologyMap);
				}

				// complete all topology solutions recursively
				for (TTopologyTemplate topologySolution : completeTopology) {
					manageCompletion(topologySolution);
					index++;
				}

			} else if (unfulfilledRequirements.isEmpty() && !placeHolders.isEmpty() || !unfulfilledRequirements.isEmpty() && !placeHolders.isEmpty()) {

				LOGGER.info("The topology contains one or more PlaceHolders.");

				// complete a topology containing place holders in one step using the PlaceHolderCompleter class
				PlaceHolderCompleter placeHolderCompleter = new PlaceHolderCompleter(topology);

				List<TTopologyTemplate> completeTopology = placeHolderCompleter.completePlaceholderTopology(placeHolders, toscaAnalyzer);

				if (placeHolderCompleter.getUserInteraction()) {
					choices = placeHolderCompleter.getChoices();
					userInteraction = true;

					// user interaction is necessary to choose a inserted Relationship Template, return the topology to winery
					List<TTopologyTemplate> intermediateSolutions = new ArrayList<>();
					TRelationshipTemplate toBeRemoved = null;
					for (TEntityTemplate entityTemplate : topology.getNodeTemplateOrRelationshipTemplate()) {
						if (entityTemplate instanceof TRelationshipTemplate) {
							TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) entityTemplate;
							if (relationshipTemplate.getTargetElement().getRef().equals(placeHolderCompleter.getPlaceHolder())) {
								toBeRemoved = relationshipTemplate;
							}
						}
					}

					topology.getNodeTemplateOrRelationshipTemplate().remove(toBeRemoved);
					topology.getNodeTemplateOrRelationshipTemplate().remove(placeHolderCompleter.getPlaceHolder());

					intermediateSolutions.add(topology);
					return intermediateSolutions;
				}

				int i = 0;

				for (TTopologyTemplate topologySolution : completeTopology) {
					Map<TTopologyTemplate, Boolean> topologyMap = new HashMap<TTopologyTemplate, Boolean>();
					topologyMap.put(topologySolution, false);
					solutions.put(i, topologyMap);
					i++;
				}

				for (TTopologyTemplate topologySolution : completeTopology) {
					manageCompletion(topologySolution);
				}
			} else if (!deferredRelations.isEmpty()) {

				LOGGER.info("The topology contains deferred RelationshipTemplates.");

				// complete a topology containing deferred Relationship Templates in one step using the DeferredCompleter class
				DeferredCompleter deferredCompleter = new DeferredCompleter(topology);
				List<TTopologyTemplate> completeTopology = deferredCompleter.completeDeferredTopology(deferredRelations.get(0), toscaAnalyzer);

				int i = 0;
				for (TTopologyTemplate solutionTemplate : completeTopology) {
					Map<TTopologyTemplate, Boolean> topologyMap = new HashMap<TTopologyTemplate, Boolean>();
					topologyMap.put(solutionTemplate, false);
					solutions.put(i, topologyMap);
					i++;
				}

				for (TTopologyTemplate topologySolution : completeTopology) {
					manageCompletion(topologySolution);
				}
			}
			List<TTopologyTemplate> sol = new ArrayList<TTopologyTemplate>();
			for (Integer i : solutions.keySet()) {
				sol.addAll(solutions.get(i).keySet());
			}
			return sol;
		}

		return new ArrayList<TTopologyTemplate>();
	}

	/**
	 * Returns whether an user interaction is necessary or not
	 *
	 * @return the field userInteraction
	 */
	public boolean getUserInteraction() {
		return userInteraction;
	}

	/**
	 * The possible {@link TRelationshipTemplate} choices
	 *
	 * @return the field choices
	 */
	public List<TEntityTemplate> getChoices() {
		return choices;
	}

	/**
	 * A map of {@link TNodeTemplate}s and {@link TRelationshipTemplate}s when completing a topology step by step
	 *
	 * @return the field nodeTemplateChoices
	 */
	public Map<TNodeTemplate, Map<TNodeTemplate, List<TEntityTemplate>>> getTemplateChoices() {
		return templateChoices;
	}

	/**
	 * Returns whether user interaction by choosing {@link TNodeTemplate}s and {@link TRelationshipTemplate}s is necessary or not
	 *
	 * @return the field nodeTemplateUserInteraction
	 */
	public boolean getNodeTemplateUserInteraction() {
		return nodeTemplateUserInteraction;
	}

}
