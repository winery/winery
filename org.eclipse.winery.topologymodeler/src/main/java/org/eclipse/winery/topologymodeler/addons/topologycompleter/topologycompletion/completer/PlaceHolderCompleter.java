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
import java.util.List;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.TOSCAAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.NodeTemplateConnector;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.placeholderhandling.PlaceHolderHandler;

/**
 * This class completes a {@link TTopologyTemplate} containing place holders.
 */
public class PlaceHolderCompleter {

	/**
	 * The {@link TTopologyTemplate} to be completed
	 */
	TTopologyTemplate topology;

	/**
	 * List containing user choices for {@link TRelationshipTemplate}s
	 */
	List<TEntityTemplate> choices;

	/**
	 * Whether an user interaction is necessary or not
	 */
	boolean userInteraction;

	/**
	 * The last inserted place holder
	 */
	TNodeTemplate placeHolder;

	/**
	 * The constructor of the class PlaceHolderCompleter.
	 *
	 * @param topology the {@link TTopologyTemplate} to be completed
	 */
	public PlaceHolderCompleter(TTopologyTemplate topology) {
		this.topology = topology;
		userInteraction = false;
	}

	/**
	 * This method completes a {@link TTopologyTemplate} containing place holders.
	 *
	 * @param placeHolders  the contained place holders
	 * @param toscaAnalyzer the {@link TOSCAAnalyzer} object to access the data model
	 * @return the complete {@link TTopologyTemplate}
	 */
	public List<TTopologyTemplate> completePlaceholderTopology(List<TNodeTemplate> placeHolders, TOSCAAnalyzer toscaAnalyzer) {

		List<TTopologyTemplate> solutions = new ArrayList<TTopologyTemplate>();

		for (TNodeTemplate placeHolder : placeHolders) {

			List<TNodeType> suitableNodeTypes = PlaceHolderHandler.getSuitableNodeTypes(placeHolder, toscaAnalyzer);

			// if there are more than one solution for an inserted NodeTemplate,
			// create copies of the topology. The user can choose from them after the completion.
			TTopologyTemplate topologyCopy = null;

			for (TNodeType suitableNodeType : suitableNodeTypes) {
				topologyCopy = new TTopologyTemplate();
				topologyCopy.getNodeTemplateOrRelationshipTemplate().addAll(topology.getNodeTemplateOrRelationshipTemplate());

				TNodeTemplate nodeTemplate = ModelUtilities.instantiateNodeTemplate(suitableNodeType);

				List<TNodeTemplate> sourceTemplates = new ArrayList<>();

				// contains RelationshipTemplates connecting to a place holder.
				// These Templates are generic and have to be replaced with
				// concrete ones.
				List<TRelationshipTemplate> placeholderConnections = new ArrayList<>();

				TRelationshipTemplate foundTarget = null;
				for (TEntityTemplate entity : topology.getNodeTemplateOrRelationshipTemplate()) {
					if (entity instanceof TRelationshipTemplate) {
						TRelationshipTemplate rt = (TRelationshipTemplate) entity;
						if (((TNodeTemplate) rt.getTargetElement().getRef()).getId().equals(placeHolder.getId())) {
							TRelationshipTemplate placeHolderConnection = (TRelationshipTemplate) entity;
							placeholderConnections.add(placeHolderConnection);
							sourceTemplates.add((TNodeTemplate) placeHolderConnection.getSourceElement().getRef());
						} else if (((TNodeTemplate) rt.getSourceElement().getRef()).getId().equals(placeHolder.getId())) {
							foundTarget = (TRelationshipTemplate) entity;
						}
					}
				}

				// collect all possible RelationshipTemplates that can be used to connect to the placeholder
				choices = new ArrayList<>();

				for (TNodeTemplate sourceTemplate : sourceTemplates) {
					// find matching RelationshipTypes to connect the Node Templates
					List<TRelationshipType> suitableRTs = NodeTemplateConnector.findRelationshipType(sourceTemplate, nodeTemplate, toscaAnalyzer, null);
					for (TRelationshipType rt : suitableRTs) {
						TRelationshipTemplate relationship = ModelUtilities.instantiateRelationshipTemplate(rt, sourceTemplate, nodeTemplate);
						choices.add(relationship);
					}
				}

				// set the source elements of the RelationshipTemplates connecting from the replaced placeholder to other NodeTemplates
				for (TEntityTemplate entityTemplate : topologyCopy.getNodeTemplateOrRelationshipTemplate()) {
					if (entityTemplate instanceof TRelationshipTemplate) {
						TRelationshipTemplate relationshipTemplate = (TRelationshipTemplate) entityTemplate;
						if (relationshipTemplate.equals(foundTarget)) {
							foundTarget.getSourceElement().setRef(nodeTemplate);
						}
					}
				}

				// remove the generic connections to the place holder
				topologyCopy.getNodeTemplateOrRelationshipTemplate().removeAll(placeholderConnections);

				// there are more than one possible Relationship Templates to connect to the inserted NodeTemplate(s), so
				// interrupt the completion and ask the user which one to insert
				if (choices.size() > 1 && sourceTemplates.size() == 1) {

					choices.add(sourceTemplates.get(0));
					choices.add(nodeTemplate);
					topologyCopy.getNodeTemplateOrRelationshipTemplate().remove(placeHolder);

					userInteraction = true;
					this.placeHolder = placeHolder;
					break;
				} else if (choices.size() == 1 || sourceTemplates.size() > 1) {
					// replace the place holder with an actual NodeTemplate
					topologyCopy.getNodeTemplateOrRelationshipTemplate().addAll(choices);
					topologyCopy.getNodeTemplateOrRelationshipTemplate().add(nodeTemplate);
					topologyCopy.getNodeTemplateOrRelationshipTemplate().remove(placeHolder);
				}
				solutions.add(topologyCopy);
			}
		}
		return solutions;
	}

	/**
	 * Returns the replaced place holder.
	 *
	 * @return the place holder
	 */
	public TNodeTemplate getPlaceHolder() {
		return placeHolder;
	}

	/**
	 * Returns whether an user interaction is necessary or not.
	 *
	 * @return the field userInteraction
	 */
	public boolean getUserInteraction() {
		return userInteraction;
	}

	/**
	 * Possible Relationship Template choices.
	 *
	 * @return the field choices
	 */
	public List<TEntityTemplate> getChoices() {
		return choices;
	}
}
