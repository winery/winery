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
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.TOSCAAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.NodeTemplateConnector;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.Utils;

/**
 * This class serves the completion of a topology containing Deferred {@link TRelationshipTemplate}s.
 */
public class DeferredCompleter {

	/**
	 * The TOSCA {@link TTopologyTemplate} document
	 */
	private TTopologyTemplate topology;

	/**
	 * A Map containing the requirements removed during the algorithm and their corresponding {@link TNodeTemplate}.
	 */
	private Map<TRequirement, TNodeTemplate> removedRequirements;

	/**
	 * Constructor of the class.
	 *
	 * @param topology
	 *            the {@link TTopologyTemplate} to be completed
	 */
	public DeferredCompleter(TTopologyTemplate topology) {
		this.topology = topology;
		removedRequirements = new HashMap<TRequirement, TNodeTemplate>();
	}

	/**
	 * Completes a {@link TTopologyTemplate} that contains deferred {@link TRelationshipTemplate}s with a depth search algorithm. A deferred {@link TRelationshipTemplate} serves as place holder for a
	 * number of {@link TNodeTemplate}s and {@link TRelationshipTemplate}s.
	 *
	 * @param deferredRelation
	 *            all found deferred {@link TRelationshipTemplate}s in the topology
	 * @param toscaAnalyzer
	 * 			  the {@link TOSCAAnalyzer} object to access the data model
	 *
	 * @return the completed topology
	 */
	public List<TTopologyTemplate> completeDeferredTopology(TRelationshipTemplate deferredRelation, TOSCAAnalyzer toscaAnalyzer) {

		List<TTopologyTemplate> solutions = new ArrayList<TTopologyTemplate>();

		TNodeTemplate source = (TNodeTemplate) deferredRelation.getSourceElement().getRef();
		TNodeTemplate target = (TNodeTemplate) deferredRelation.getTargetElement().getRef();

		// TODO Remove this "if clause" after the Provisioning-API is implemented. At the moment Deferred RelationshipTemplates can't be completed
		// without the existence of Requirements
		if (source.getRequirements() != null && !source.getRequirements().getRequirement().isEmpty()) {
			topology.getNodeTemplateOrRelationshipTemplate().remove(deferredRelation);
			runDepthFirstSearch(source, target, new ArrayList<TEntityTemplate>(), solutions, toscaAnalyzer);
		}

		/**
		 * Note: This code adds Requirements to NodeTemplates that has been removed during the algorithm but could not
		 * be used to replace the Deferred-RelationshipTemplates. If this step is not done, requirements could get lost.
		 *
		 * Therefore all removed Requirements are checked for fulfillment in the topology. If they have not been fulfilled
		 * they are re-added to the topology.
		 */
		Set<TRequirement> keySet = removedRequirements.keySet();

		for (TTopologyTemplate topologyTemplate: solutions) {
			boolean fulfilled = false;
			for (TRequirement requirement: keySet) {
				for (TEntityTemplate entity: topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
					if (entity instanceof TNodeTemplate) {
						TNodeTemplate nodeTemplate = (TNodeTemplate) entity;
						if (nodeTemplate.getCapabilities() != null) {
							for (TCapability capability: nodeTemplate.getCapabilities().getCapability()) {
								String reqCapaType = "";
								for (TRequirementType reqType: toscaAnalyzer.getRequirementTypes()) {
									if (reqType.getName().equals(requirement.getType().getLocalPart())) {
										reqCapaType = reqType.getRequiredCapabilityType().getLocalPart();
									}
								}
								if (capability.getName().equals(reqCapaType)) {
									fulfilled = true;
								}
							}
						}
					}
				}
				if (!fulfilled) {
					for (TEntityTemplate entity: topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
						if (entity.equals(removedRequirements.get(requirement))) {
							TNodeTemplate foundNT = (TNodeTemplate) entity;
							foundNT.getRequirements().getRequirement().add(requirement);
						}
					}
				}
			}
		}

		return solutions;
	}

	/**
	 * Runs a recursive depth search to find the path to the target NodeTemplate.
	 *
	 * @param source
	 *            the source node of a given {@link TRelationshipTemplate}
	 * @param target
	 *            the target node of a given {@link TRelationshipTemplate}
	 * @param path
	 *            the current path to the target (can be incomplete)
	 * @param solutions
	 *			  list containing all possible solutions of the completion
	 * @param toscaAnalyzer
	 *  		  the {@link TOSCAAnalyzer} object to access the data model
	 *
	 * @return the path to the target NodeTemplate
	 */
	private void runDepthFirstSearch(TNodeTemplate source, TNodeTemplate target, List<TEntityTemplate> path, List<TTopologyTemplate> solutions, TOSCAAnalyzer toscaAnalyzer) {

		List<TNodeType> matchingNodeTypes = new ArrayList<TNodeType>();

		if (source.getRequirements() != null) {

			List<TRequirement> requirementsOfTemplate = new ArrayList<>();
			for (TRequirement requirement : source.getRequirements().getRequirement()) {
				requirementsOfTemplate.add(requirement);
			}

			for (TRequirement requirement : requirementsOfTemplate) {

				// save the requirement to a list to avoid losing requirements (see line 83)
				TRequirement sourceRequirement = new TRequirement();
				sourceRequirement.setId(requirement.getId());
				sourceRequirement.setName(requirement.getName());
				sourceRequirement.setType(requirement.getType());

				// Remember the removed requirements. In case a requirement
				// can't be used for completing the deferred RelationshipTemplate it has to be re-added to the topology.
				removedRequirements.put(sourceRequirement, source);

				// search for matching NodeTypes for the requirement
				matchingNodeTypes.addAll(Utils.matchRequirementAndCapability(requirement, toscaAnalyzer));

				// remove the requirement so it is not handled again during the algorithm
				source.getRequirements().getRequirement().remove(requirement);
			}
		}
		for (TNodeType match : matchingNodeTypes) {

			if (match.getName().equals(target.getType().getLocalPart()) && match.getTargetNamespace().equals(target.getType().getNamespaceURI())) {
				// the search was successful connect the target
				List<TRelationshipType> suitableRTs = NodeTemplateConnector.findRelationshipType(source, target, toscaAnalyzer, null);

				for (TRelationshipType rt : suitableRTs) {
					TRelationshipTemplate relationship = ModelUtilities.instantiateRelationshipTemplate(rt, source, target);
					path.add(relationship);
				}

				TTopologyTemplate possiblePath = new TTopologyTemplate();
				possiblePath.getNodeTemplateOrRelationshipTemplate().addAll(topology.getNodeTemplateOrRelationshipTemplate());

				// add the path to the topology
				for (TEntityTemplate pathTemplate : path) {
					possiblePath.getNodeTemplateOrRelationshipTemplate().add(pathTemplate);
				}

				possiblePath.getNodeTemplateOrRelationshipTemplate().remove(target);

				// this is no good style, however the target has to be the last item in the list for a proper stack layouting
				possiblePath.getNodeTemplateOrRelationshipTemplate().add(target);
				solutions.add(possiblePath);
				path.clear();
			} else {

				// the end of the path is not reached, add the found NodeTemplate and continue the depth search
				TNodeTemplate instantiatedNodeTemplate = ModelUtilities.instantiateNodeTemplate(match);

				List<TRelationshipType> suitableRTs = NodeTemplateConnector.findRelationshipType(source, instantiatedNodeTemplate, toscaAnalyzer, null);

				for (TRelationshipType rt : suitableRTs) {
					TRelationshipTemplate relationship = ModelUtilities.instantiateRelationshipTemplate(rt, source, instantiatedNodeTemplate);
					path.add(relationship);
				}
				path.add(instantiatedNodeTemplate);
				runDepthFirstSearch(instantiatedNodeTemplate, target, path, solutions, toscaAnalyzer);

			}
		}
	}
}
