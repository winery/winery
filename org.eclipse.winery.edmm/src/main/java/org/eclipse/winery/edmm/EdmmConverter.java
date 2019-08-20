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

package org.eclipse.winery.edmm;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.support.DefaultKeys;

public class EdmmConverter {

    private final Map<QName, TNodeType> nodeTypes;
    private final Map<QName, TRelationshipType> relationshipTypes;

    public EdmmConverter(Map<QName, TNodeType> nodeTypes, Map<QName, TRelationshipType> relationshipTypes) {
        this.nodeTypes = nodeTypes;
        this.relationshipTypes = relationshipTypes;
    }

    public EntityGraph transform(TServiceTemplate serviceTemplate) {
        EntityGraph entityGraph = new EntityGraph();

        List<TNodeTemplate> nodeTemplates = serviceTemplate.getTopologyTemplate().getNodeTemplates();
        List<TRelationshipTemplate> relationshipTemplates = serviceTemplate.getTopologyTemplate().getRelationshipTemplates();
        if (!nodeTemplates.isEmpty()) {
            entityGraph.addEntity(new MappingEntity(EntityGraph.COMPONENTS, entityGraph));
        }

        nodeTemplates.forEach(nodeTemplate -> createNode(nodeTemplate, relationshipTemplates, entityGraph));
        relationshipTemplates.forEach(relationship -> createRelation(relationship, entityGraph));

        return entityGraph;
    }

    private void createRelation(TRelationshipTemplate relationship, EntityGraph entityGraph) {

    }

    private void createNode(TNodeTemplate nodeTemplate, List<TRelationshipTemplate> relationshipTemplates, EntityGraph entityGraph) {
        // create the component inside the topology.
        EntityId componentNodeId = EntityGraph.COMPONENTS.extend(nodeTemplate.getId());
        MappingEntity componentEntity = new MappingEntity(componentNodeId, entityGraph);
        entityGraph.addEntity(componentEntity);

        // add the type to the model
        EntityId nodeTypeEntityId = createNodeType(nodeTemplate.getType(), entityGraph);
        entityGraph.addEntity(new ScalarEntity(nodeTypeEntityId.getName(), componentNodeId.extend(DefaultKeys.TYPE), entityGraph));
    }

    private EntityId createNodeType(QName type, EntityGraph entityGraph) {
        if (!entityGraph.getEntity(EntityGraph.COMPONENT_TYPES).isPresent()) {
            entityGraph.addEntity(new MappingEntity(EntityGraph.COMPONENT_TYPES, entityGraph));
        }

        EntityId nodeTypeId = EntityGraph.COMPONENT_TYPES.extend(normalizeQName(type));
        Optional<Entity> entity = entityGraph.getEntity(nodeTypeId);

        if (!entity.isPresent()) {
            MappingEntity nodeTypeMappingEntity = new MappingEntity(nodeTypeId, entityGraph);
            entityGraph.addEntity(nodeTypeMappingEntity);

            TNodeType nodeType = this.nodeTypes.get(type);
        }

        return nodeTypeId;
    }

    private static String normalizeQName(QName qName) {
        return qName.toString().substring(1)
            .replace("}", "__")
            .replace("/", "")
            .replace(':', '_');
    }
}
