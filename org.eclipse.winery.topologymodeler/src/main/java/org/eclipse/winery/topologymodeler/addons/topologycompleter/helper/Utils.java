/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologymodeler.addons.topologycompleter.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.TOSCAAnalyzer;

import org.slf4j.LoggerFactory;

/**
 * Contains methods to match requirements and capabilities and find elements in the {@link TTopologyTemplate}
 */
public class Utils {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Utils.class.getName());

    /**
     * This method searches {@link TNodeType}s in the repository that match a requirement.
     *
     * @param requirement   the requirement to be matched
     * @param toscaAnalyzer the {@link TOSCAAnalyzer} object to access the data model
     * @return a list of the matched {@link TNodeType}s
     */
    public static List<TNodeType> matchRequirementAndCapability(TRequirement requirement, TOSCAAnalyzer toscaAnalyzer) {

        List<TNodeType> possibleNodeTypes = new ArrayList<TNodeType>();

        // find all matching Node Types for a requirement by the "requiredCapabilityType" attribute of its type
        for (TRequirementType requirementType : toscaAnalyzer.getRequirementTypes()) {
            if (requirementType.getName().equals(requirement.getType().getLocalPart())) {

                QName requiredCapabilityType = requirementType.getRequiredCapabilityType();
                for (TNodeType nodeType : toscaAnalyzer.getNodeTypes()) {
                    if (nodeType.getCapabilityDefinitions() != null) {
                        for (TCapabilityDefinition cd : nodeType.getCapabilityDefinitions().getCapabilityDefinition()) {
                            if (cd.getCapabilityType().getLocalPart().equals(requiredCapabilityType.getLocalPart())) {
                                possibleNodeTypes.add(nodeType);
                            }
                        }
                    }
                }
            }
        }

        return possibleNodeTypes;
    }

    /**
     * Generates a random {@link UUID} exclusively used by the {@link TemplateBuilder}.
     *
     * @return the generated {@link UUID} id
     */
    public static String createRandomID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns a {@link TNodeType} for a given Id.
     *
     * @param nodeTypes All the {@link TNodeType} in the {@link TTopologyTemplate}
     * @param id        The id of the {@link TNodeType} to be searched
     * @return the {@link TNodeType} or null if not found
     */
    public static TNodeType getNodeTypeForId(List<TNodeType> nodeTypes, QName id) {

        for (TNodeType nodeType : nodeTypes) {
            if (nodeType.getName().equals(id.getLocalPart()) && nodeType.getTargetNamespace().equals(id.getNamespaceURI())) {
                return nodeType;
            }
        }

        LOGGER.error("No NodeType with " + id + " exists");

        // no type could be found for the given ID, this case cannot occur if the topology was modelled in the Winery Topology Modeler
        return null;
    }
}
