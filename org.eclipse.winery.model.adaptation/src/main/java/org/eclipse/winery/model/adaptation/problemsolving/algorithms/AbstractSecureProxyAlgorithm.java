/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.problemsolving.algorithms;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.adaptation.problemsolving.SolutionInputData;
import org.eclipse.winery.model.adaptation.problemsolving.SolutionStrategy;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;

/**
 * For this algorithm to work, the following Relationship Types must have been used to model hostedOn and connectsTo relations:
 * <ul>
 *     <li>{@link ToscaBaseTypes#connectsToRelationshipType }</li>
 *     <li>{@link ToscaBaseTypes#hostedOnRelationshipType }</li>
 * </ul>
 */
public abstract class AbstractSecureProxyAlgorithm implements SolutionStrategy {

    @Override
    public boolean applySolution(TTopologyTemplate topology, SolutionInputData inputData) {
        if (Objects.isNull(inputData.getFindings()) || inputData.getFindings().size() < 2) {
            return false;
        }

        String firstComponentId = inputData.getFindings().get(0).getComponentId();
        TNodeTemplate firstComponent = topology.getNodeTemplate(firstComponentId);
        String secondComponentId = inputData.getFindings().get(1).getComponentId();
        TNodeTemplate secondComponent = topology.getNodeTemplate(secondComponentId);

        Optional<TRelationshipTemplate> connectsToRelation = ModelUtilities.getOutgoingRelationshipTemplates(topology, firstComponent).stream()
            .filter(relationship -> relationship.getType().equals(ToscaBaseTypes.connectsToRelationshipType))
            .filter(relationship -> relationship.getTargetElement().getRef().getId().equals(secondComponentId))
            .findFirst();

        if (!connectsToRelation.isPresent()) {
            connectsToRelation = ModelUtilities.getOutgoingRelationshipTemplates(topology, secondComponent).stream()
                .filter(relationship -> relationship.getType().equals(ToscaBaseTypes.connectsToRelationshipType))
                .filter(relationship -> relationship.getTargetElement().getRef().getId().equals(firstComponentId))
                .findFirst();

            return connectsToRelation.isPresent()
                && insertProxy(secondComponent, firstComponent, connectsToRelation.get(), topology);
        }

        return insertProxy(firstComponent, secondComponent, connectsToRelation.get(), topology);
    }

    private boolean insertProxy(TNodeTemplate sourceNode, TNodeTemplate targetNode, TRelationshipTemplate oldConnection, TTopologyTemplate topology) {
        topology.getNodeTemplateOrRelationshipTemplate().remove(oldConnection);

        TNodeTemplate sourceNodeProxy = createProxy(sourceNode);
        topology.addNodeTemplate(sourceNodeProxy);
        TNodeTemplate targetNodeProxy = createProxy(targetNode);
        topology.addNodeTemplate(targetNodeProxy);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNode, sourceNodeProxy,
            ToscaBaseTypes.connectsToRelationshipType, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNodeProxy, targetNodeProxy,
            ToscaBaseTypes.connectsToRelationshipType, "securely-connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(targetNodeProxy, targetNode,
            ToscaBaseTypes.connectsToRelationshipType, "connectsTo", topology);

        return addProxyHost(topology, sourceNode, sourceNodeProxy) && addProxyHost(topology, targetNode, targetNodeProxy);
    }

    private boolean addProxyHost(TTopologyTemplate topology, TNodeTemplate neighbour, TNodeTemplate proxy) {
        Map<QName, TNodeType> nodeTypes = RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class);
        ArrayList<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topology, neighbour);

        TNodeTemplate hostedOnComponent = null;
        for (TNodeTemplate hostedOn : hostedOnSuccessors) {
            if (ModelUtilities.isOfType(hostedOn.getType(), getHostComponentType(), nodeTypes)) {
                hostedOnComponent = hostedOn;
            }
        }

        if (Objects.isNull(hostedOnComponent)) {
            return false;
        }

        ModelUtilities.createRelationshipTemplateAndAddToTopology(proxy, hostedOnComponent,
            ToscaBaseTypes.hostedOnRelationshipType, "hostedOn", topology);

        return true;
    }

    protected abstract QName getHostComponentType();

    protected abstract TNodeTemplate createProxy(TNodeTemplate sourceNode);
}
