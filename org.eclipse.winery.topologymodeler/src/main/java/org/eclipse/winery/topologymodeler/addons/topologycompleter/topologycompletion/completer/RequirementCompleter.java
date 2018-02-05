/*******************************************************************************
 * Copyright (c) 2013 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion.completer;

import org.eclipse.winery.model.tosca.*;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.TOSCAAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.NodeTemplateConnector;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequirementCompleter {

    /**
     * The TOSCA {@link TTopologyTemplate} document.
     */
    TTopologyTemplate topology;

    /**
     * The constructor the class.
     *
     * @param topology the topology to be completed
     */
    public RequirementCompleter(TTopologyTemplate topology) {
        this.topology = topology;
    }

    /**
     * This method completes a topology containing {@link TRequirement}s in one step (without user interaction).
     *
     * @param unfulfilledRequirements all the unfulfilled requirements that has been found in the topology
     * @param toscaAnalyzer           the {@link TOSCAAnalyzer} object to access the data model
     * @return the complete topology
     */
    public List<TTopologyTemplate> completeRequirementTopology(Map<TRequirement, TNodeTemplate> unfulfilledRequirements, TOSCAAnalyzer toscaAnalyzer) {

        List<TTopologyTemplate> solutions = new ArrayList<TTopologyTemplate>();

        Set<TRequirement> requirements = unfulfilledRequirements.keySet();

        TNodeTemplate instantiatedNodeTemplate = null;

        // fulfill the Requirements
        for (TRequirement requirement : requirements) {

            // remove the requirement from the NodeTemplate
            TNodeTemplate requirementTemplate = unfulfilledRequirements.get(requirement);
            for (TEntityTemplate element : topology.getNodeTemplateOrRelationshipTemplate()) {
                if (requirementTemplate.getId().equals(element.getId())) {
                    ((TNodeTemplate) element).getRequirements().getRequirement().remove(requirement);
                }
            }

            List<TNodeType> possibleNodeTypes = Utils.matchRequirementAndCapability(requirement, toscaAnalyzer);

            // create a NodeTemplate for every matching Type, insert it into the topology and create a topology copy for each possible inserted NodeTemplate
            TTopologyTemplate topologyCopy = null;
            for (TNodeType possibleType : possibleNodeTypes) {

                topologyCopy = new TTopologyTemplate();
                topologyCopy.getNodeTemplateOrRelationshipTemplate().addAll(topology.getNodeTemplateOrRelationshipTemplate());

                // instantiate the template
                instantiatedNodeTemplate = ModelUtilities.instantiateNodeTemplate(possibleType);
                topologyCopy.getNodeTemplateOrRelationshipTemplate().add(instantiatedNodeTemplate);

                TNodeTemplate correspondingNodeTemplate = unfulfilledRequirements.get(requirement);

                // find matching RelationshipTypes, instantiate RelationshipTemplates and connect the Node Templates
                List<TRelationshipType> suitableRTs = NodeTemplateConnector.findRelationshipType(correspondingNodeTemplate, instantiatedNodeTemplate, toscaAnalyzer, requirement);
                for (TRelationshipType rt : suitableRTs) {
                    TRelationshipTemplate relationship = ModelUtilities.instantiateRelationshipTemplate(rt, correspondingNodeTemplate, instantiatedNodeTemplate);
                    topologyCopy.getNodeTemplateOrRelationshipTemplate().add(relationship);
                }

                solutions.add(topologyCopy);
            }
            if (solutions.size() > 1) {
                break;
            }
        }
        return solutions;
    }
}
