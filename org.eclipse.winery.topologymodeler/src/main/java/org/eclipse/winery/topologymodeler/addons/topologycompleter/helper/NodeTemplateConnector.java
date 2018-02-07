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

package org.eclipse.winery.topologymodeler.addons.topologycompleter.helper;

import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.TOSCAAnalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * This class searches a {@link TRelationshipType} which is able to connect two given {@link TNodeTemplate}s.
 */
public class NodeTemplateConnector {

    /**
     * Searches a compatible {@link TRelationshipType} to connect two {@link TNodeTemplate}s.
     *
     * @param source        the source {@link TNodeTemplate}
     * @param target        the target {@link TNodeTemplate}
     * @param toscaAnalyzer the {@link TOSCAAnalyzer} object to access the data model
     * @param requirement   the {@link TRequirement} of the source {@link TNodeTemplate}
     * @return a list of suitable {@link TRelationshipType}s
     */
    public static List<TRelationshipType> findRelationshipType(TNodeTemplate source, TNodeTemplate target, TOSCAAnalyzer toscaAnalyzer, TRequirement requirement) {

        List<TRelationshipType> suitableRelationshipTypes = new ArrayList<TRelationshipType>();
        List<TRelationshipType> allRelationshipTypes = toscaAnalyzer.getRelationshipTypes();

        // in case the connection to a placeholder is searched, no requirement exists
        if (requirement != null) {

            List<TCapability> capabilities = target.getCapabilities().getCapability();

            // check if a RelationshipType can connect a requirement of the source NodeTemplate to a capability of the target NodeTemplate
            for (TRelationshipType relationshipType : allRelationshipTypes) {
                if (relationshipType.getValidSource() != null && relationshipType.getValidTarget() != null) {
                    for (TCapability capability : capabilities) {
                        if ((relationshipType.getValidSource().getTypeRef().equals(requirement.getType()) && relationshipType.getValidTarget().getTypeRef().equals(capability.getType()))) {
                            suitableRelationshipTypes.add(relationshipType);
                        }
                    }
                }
            }
        }

        // to extend the selection check if a RelationshipType can connect the type of the source NodeTemplate to the type of the target NodeTemplate
        for (TRelationshipType rt : allRelationshipTypes) {
            if (rt.getValidSource() != null && rt.getValidTarget() != null) {
                if ((rt.getValidSource().getTypeRef().equals(source.getType()) && rt.getValidTarget().getTypeRef().equals(target.getType()))) {
                    suitableRelationshipTypes.add(rt);
                }
            }
        }

        // in case no suitable relationship type could be found, search for generic types without the optional ValidSource / ValidTarget elements.
        if (suitableRelationshipTypes.isEmpty()) {
            for (TRelationshipType rt : allRelationshipTypes) {
                if (rt.getValidSource() == null && rt.getValidTarget() == null) {
                    suitableRelationshipTypes.add(rt);
                }
            }
        }

        return suitableRelationshipTypes;
    }
}
