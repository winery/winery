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

package org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.Utils;

/**
 * This class analyzes the occurrence of TOSCA {@link TRequirement}s in a {@link TTopologyTemplate} and checks whether they
 * are already fulfilled or not.
 */
public class RequirementAnalyzer {

	/**
	 * This method checks if {@link TNodeTemplate}s contain {@link TRequirement}s and adds them to a {@link Map}.
	 *
	 * @param toscaAnalyzer
	 *            the {@link TOSCAAnalyzer} object to access the data model
	 *
	 * @return a map containing {@link TNodeTemplate}s and their {@link TRequirement}s
	 */
	public static Map<TRequirement, TNodeTemplate> analyzeRequirements(TOSCAAnalyzer toscaAnalyzer) {

		// map containing entries for a Requirement and its corresponding NodeTemplate
		Map<TRequirement, TNodeTemplate> unfulfilledRequirements = new HashMap<TRequirement, TNodeTemplate>();

		for (TNodeTemplate nodeTemplate : toscaAnalyzer.getNodeTemplates()) {

			List<TRequirement> requirements = new ArrayList<>();

			TNodeType nodeType = Utils.getNodeTypeForId(toscaAnalyzer.getNodeTypes(), nodeTemplate.getType());

			if (nodeType.getRequirementDefinitions() != null && !nodeType.getRequirementDefinitions().getRequirementDefinition().isEmpty()) {

				List<TRequirementDefinition> requirementDefinitions = nodeType.getRequirementDefinitions().getRequirementDefinition();

				// check the requirements of the type of the used NodeTemplate
				for (TRequirementDefinition requirementDefinition: requirementDefinitions) {
					TRequirement requirement = new TRequirement();
					requirement.setType(requirementDefinition.getRequirementType());
					requirement.setName(requirementDefinition.getName());
					requirement.setId(Utils.createRandomID());
				}
			}

			if (nodeTemplate.getRequirements() != null && !nodeTemplate.getRequirements().getRequirement().isEmpty()) {
				requirements.addAll(nodeTemplate.getRequirements().getRequirement());
			}

			if (!requirements.isEmpty()) {
				// list containing the RelationshipTemplates connecting to the NodeTemplate
				List<TRelationshipTemplate> connectors = new ArrayList<TRelationshipTemplate>();

				// add the connected RelationshipTemplates
				for (TRelationshipTemplate connector : toscaAnalyzer.getRelationshipTemplates()) {
					if (connector.getSourceElement().getRef().equals(nodeTemplate)) {
						connectors.add(connector);
					}
				}

				// add requirements of unconnected NodeTemplates to the map because they can't be fulfilled
				if (connectors.size() == 0) {
					for (TRequirement requirement : requirements) {
						unfulfilledRequirements.put(requirement, nodeTemplate);
					}
				} else {
					boolean fulfilled = false;

					// check if one of the connected NodeTemplates already fulfill the requirement
					for (TRequirement requirement : requirements) {
						for (TRelationshipTemplate connector : connectors) {
							TNodeTemplate connectedNodeTemplate = (TNodeTemplate) connector.getTargetElement().getRef();
							if (connectedNodeTemplate.getCapabilities() != null) {
								for (TCapability capa : connectedNodeTemplate.getCapabilities().getCapability()) {
									for (TRequirementType reqType : toscaAnalyzer.getRequirementTypes()) {
										if (requirement.getType().getLocalPart().equals(reqType.getName())) {
											if (reqType.getRequiredCapabilityType().getLocalPart().equals(capa.getType().getLocalPart())
													&& reqType.getRequiredCapabilityType().getNamespaceURI().equals(capa.getType().getNamespaceURI())) {
												fulfilled = true;
											}
										}
									}
								}
							}
						}
						if (!fulfilled) {
							unfulfilledRequirements.put(requirement, nodeTemplate);
						}
					}
				}
			}
		}
		return unfulfilledRequirements;
	}
}
