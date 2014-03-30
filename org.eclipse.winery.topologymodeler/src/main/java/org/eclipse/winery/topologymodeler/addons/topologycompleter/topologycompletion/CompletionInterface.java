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

import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.client.IWineryRepositoryClient;
import org.eclipse.winery.repository.client.WineryRepositoryClientFactory;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.DeferredAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.PlaceHolderAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.RequirementAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.TOSCAAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.Constants;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.JAXBHelper;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.RESTHelper;
import org.slf4j.LoggerFactory;

/**
 * This class is the entry point of the TOSCA topology completion which is called by the Winery Topology Modeler.
 * It receives an incomplete {@link TTopologyTemplate} from Winery.
 * The completion of the incomplete {@link TTopologyTemplate} is managed by this class.
 */
public class CompletionInterface {

	/**
	 * Logger for debug reasons.
	 */
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CompletionInterface.class.getName());

	/**
	 * This global variable is returned to the Winery Topology Modelerer via getCurrentTopology() to display intermediate results when an user interaction is necessary.
	 */
	private TTopologyTemplate currentTopology;

	/**
	 * This list contains {@link TTopologyTemplate}s to be chosen by the user when the topology solution isn't unique.
	 */
	private List<TTopologyTemplate> topologyTemplateChoices;

	/**
	 * This List contains {@link TRelationshipTemplate}s to be chosen by the user.
	 */
	private List<TEntityTemplate> relationshipTemplateChoices;

	/**
	 * This Map contains {@link TNodeTemplate}s and {@link TRelationshipTemplate}s to be chosen by the user during the step-by-step approach.
	 */
	private Map<TNodeTemplate, Map<TNodeTemplate, List<TEntityTemplate>>> nodeTemplateChoices;

	/**
	 * String containing an error message to be displayed in Winery if necessary.
	 */
	private String errorMessage = "";

	/**
	 * This method receives an incomplete {@link TTopologyTemplate} and the repository content from Winery. After analyzing the {@link TTopologyTemplate}, the topology is completed. This method will
	 * return a message after the completion whether the completion was successful, has failed or the user has to interact.
	 *
	 * @param topology
	 *            (XMLString) the {@link TTopologyTemplate} to be completed as XMLString
	 * @param serviceTemplateName
	 *            the name of the ServiceTemplate for REST calls
	 * @param topologyTemplateURL
	 *            the URL where the template is saved to
	 * @param overwriteTopology
	 *            determines in which way the {@link TTopologyTemplate} is saved. The current {@link TTopologyTemplate} can either be overwritten or a new topology can be created.
	 * @param topologyName
	 *            the name of the {@link TTopologyTemplate} when a new {@link TTopologyTemplate} shall be created
	 * @param topologyNamespace
	 *            the namespace of the {@link TTopologyTemplate} when a new {@link TTopologyTemplate} shall be created
	 * @param repositoryURL
	 *            the URL to the repository to receive and write TOSCA specific information
	 * @param stepByStep
	 *            whether the topology completion is processed step-by-step or not
	 * @param restarted
	 *            whether the topology completion is restarted or started for the first time
	 *
	 * @return a message to Winery that contains information whether the topology is complete, the user has to interact or an error occurred.
	 */
	public String complete(String topology, String serviceTemplateName, String topologyTemplateURL, Boolean overwriteTopology,
			String topologyName, String topologyNamespace, String repositoryURL, boolean stepByStep, boolean restarted) {

		logger.info("Starting completion...");

		////////////////////////////////////////
		// STEP 1: Receive topology from Winery
		////////////////////////////////////////

		logger.info("Saving to: " + topologyTemplateURL);

		logger.info("The service template to be completed is: " + serviceTemplateName);

		// receive types from repository
		IWineryRepositoryClient client = WineryRepositoryClientFactory.getWineryRepositoryClient();
		client.addRepository(repositoryURL);

		List<TNodeType> nodeTypeList = (List<TNodeType>) client.getAllTypes(TNodeType.class);
		List<TRelationshipType> relationshipTypeList = (List<TRelationshipType>) client.getAllTypes(TRelationshipType.class);
		List<TRequirementType> requirementTypeList = (List<TRequirementType>) client.getAllTypes(TRequirementType.class);

		/////////////////////////////////////
		// Step 2: Analyze topology content
		/////////////////////////////////////

		logger.info("The modelled topology as XML: " + topology);

		TTopologyTemplate topologyTemplate = JAXBHelper.getTopologyAsJaxBObject(topology);

		logger.info("Analyzing topology...");

		// analyze the received topology
		TOSCAAnalyzer toscaAnalyzer = new TOSCAAnalyzer();
		toscaAnalyzer.analyzeTOSCATopology(topologyTemplate);
		toscaAnalyzer.setTypes(nodeTypeList, relationshipTypeList, requirementTypeList);

		// if the topology is already complete, a message is displayed
		if (checkCompletnessOfTopology(toscaAnalyzer) && !restarted) {
			return Constants.CompletionMessages.TOPOLOGYCOMPLETE.toString();
		} else {

			/////////////////////////////////////////
			// Step 3: Invoke the topology completion
			/////////////////////////////////////////
			logger.info("Invoking Topology Completion...");

			CompletionManager completionManager = new CompletionManager(toscaAnalyzer, stepByStep);
			List<TTopologyTemplate> completedTopology = completionManager.manageCompletion(topologyTemplate);

			// the user has to interact by choosing a RelationshipTemplate, send message to Winery which will display a dialog
			if (completionManager.getUserInteraction() && !stepByStep) {
				currentTopology = completedTopology.get(0);
				relationshipTemplateChoices = completionManager.getChoices();

				return Constants.CompletionMessages.USERINTERACTION.toString();

			} else if (completionManager.getNodeTemplateUserInteraction() && stepByStep) {
				// the topology completion is processed Step-by-Step, the user has to choose Node and RelationshipTemplates to be inserted
				currentTopology = completedTopology.get(0);
				nodeTemplateChoices = completionManager.getTemplateChoices();

				for (TNodeTemplate nodeTemplate : nodeTemplateChoices.keySet()) {
					Map<TNodeTemplate, List<TEntityTemplate>> entityTemplates = nodeTemplateChoices.get(nodeTemplate);

					for (TNodeTemplate entity : entityTemplates.keySet()) {
						for (TEntityTemplate relationshipTemplate : entityTemplates.get(entity)) {
							// remove entity that has to be chosen next
							if (currentTopology.getNodeTemplateOrRelationshipTemplate().contains(relationshipTemplate)) {
								currentTopology.getNodeTemplateOrRelationshipTemplate().remove(relationshipTemplate);
							} else if (currentTopology.getNodeTemplateOrRelationshipTemplate().contains(entity)) {
								currentTopology.getNodeTemplateOrRelationshipTemplate().remove(entity);
							}
						}
					}
				}

				return Constants.CompletionMessages.STEPBYSTEP.toString();
			}

			logger.info("Completion successful!");

			if (completedTopology.size() == 1) {
				// solution is unique, save the topology
				RESTHelper.saveCompleteTopology(completedTopology.get(0), topologyTemplateURL, overwriteTopology, topologyName, topologyNamespace, repositoryURL);
				return Constants.CompletionMessages.SUCCESS.toString();
			} else if (completedTopology.size() > 1) {
				// if there are several topology solutions, let the user choose
				this.topologyTemplateChoices = completedTopology;
				return Constants.CompletionMessages.USERTOPOLOGYSELECTION.toString();
			} else {
				// an error occurred
				errorMessage = "Error: No suitable NodeTemplate could be found for a Requirement or PlaceHolder.";
				return Constants.CompletionMessages.FAILURE.toString();
			}
		}
	}

	/**
	 * This method checks if the topology is already complete. It will be called before executing the topology completion but
	 * only in case the topology completion isn't restarted after a user selection.
	 *
	 * @param toscaAnalyzer
	 *            the topology to be checked
	 * @return whether the topology is complete or not
	 */
	public boolean checkCompletnessOfTopology(TOSCAAnalyzer toscaAnalyzer) {

		if (RequirementAnalyzer.analyzeRequirements(toscaAnalyzer).isEmpty() && PlaceHolderAnalyzer.analyzePlaceHolders(toscaAnalyzer).isEmpty()
				&& DeferredAnalyzer.analyzeDeferredRelations(toscaAnalyzer).isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the current state of the completion.
	 *
	 * @return the current {@link TTopologyTemplate}
	 */
	public TTopologyTemplate getCurrentTopology() {
		return currentTopology;
	}

	/**
	 * Returns the choices whenever there are several possible complete {@link TTopologyTemplate}s. They will be displayed in Winery and chosen by the user.
	 *
	 * @return the possible {@link TTopologyTemplate} choices as a list.
	 */
	public List<TTopologyTemplate> getTopologyTemplateChoices() {
		return topologyTemplateChoices;
	}

	/**
	 * Returns the {@link TRelationshipTemplate} choices
	 *
	 * @return the {@link TRelationshipTemplate}s to be chosen
	 */
	public List<TEntityTemplate> getRelationshipTemplateChoices() {
		return relationshipTemplateChoices;
	}

	/**
	 * Returns several {@link TNodeTemplate} and {@link TRelationshipTemplate} choices when the user selected the step-by-step approach.
	 *
	 * @return the {@link TNodeTemplate} choices
	 */
	public Map<TNodeTemplate, Map<TNodeTemplate, List<TEntityTemplate>>> getNodeTemplateChoices() {
		return nodeTemplateChoices;
	}

	/**
	 * Returns a message when an error occurred during the completion.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
