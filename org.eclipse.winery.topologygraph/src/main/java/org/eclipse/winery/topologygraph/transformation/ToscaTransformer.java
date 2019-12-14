/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.topologygraph.transformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEntity;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.eclipse.jdt.annotation.NonNull;

public class ToscaTransformer {

    public static ToscaGraph createTOSCAGraph(TTopologyTemplate topologyTemplate) {
        ToscaGraph graph = new ToscaGraph();
        @NonNull List<TRelationshipTemplate> relationshipTemplates = topologyTemplate.getRelationshipTemplates();
        @NonNull List<TNodeTemplate> nodeTemplates = topologyTemplate.getNodeTemplates();
        Map<TNodeTemplate, ToscaNode> nodes = new HashMap<>();
        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            ToscaNode node = createAndInitializeTOSCANode(nodeTemplate);
            nodes.put(nodeTemplate, node);
            graph.addVertex(node);
        }
        for (TRelationshipTemplate tRelationshipTemplate : relationshipTemplates) {
            ToscaNode source = nodes.get(tRelationshipTemplate.getSourceElement().getRef());
            ToscaNode target = nodes.get(tRelationshipTemplate.getTargetElement().getRef());
            ToscaEdge edge = new ToscaEdge(source, target);
            graph.addEdge(source, target, edge);
            initializeTOSCAEdge(tRelationshipTemplate, edge);
        }
        return graph;
    }

    protected static void initializeTOSCAEdge(TRelationshipTemplate tRelationshipTemplate, ToscaEdge edge) {
        edge.setTemplate(tRelationshipTemplate);
        addTEntityTypes(tRelationshipTemplate.getType(), edge, TRelationshipType.class);
    }

    protected static ToscaNode createAndInitializeTOSCANode(TNodeTemplate nodeTemplate) {
        ToscaNode node = new ToscaNode();
        node.setNodeTemplate(nodeTemplate);
        addTEntityTypes(nodeTemplate.getType(), node, TNodeType.class);
        return node;
    }

    public static void addTEntityTypes(QName nodeTypeQName, ToscaEntity entity, Class<? extends TEntityType> tEntityTypeClass) {
        TEntityType entityType = getEntityType(nodeTypeQName, tEntityTypeClass);
        entity.addTEntityType(entityType);

        Optional.of(entityType).map(TEntityType::getDerivedFrom)
            .map(TEntityType.DerivedFrom::getTypeRef)
            .ifPresent(qName -> addTEntityTypes(qName, entity, tEntityTypeClass));
    }

    protected static TEntityType getEntityType(QName qName, Class<? extends TEntityType> tEntityTypeClass) {
        if (tEntityTypeClass.isAssignableFrom(TNodeType.class)) {
            return RepositoryFactory.getRepository().getElement(new NodeTypeId(qName));
        }
        if (tEntityTypeClass.isAssignableFrom(TRelationshipType.class)) {
            return RepositoryFactory.getRepository().getElement(new RelationshipTypeId(qName));
        }
        return null;
    }
}
