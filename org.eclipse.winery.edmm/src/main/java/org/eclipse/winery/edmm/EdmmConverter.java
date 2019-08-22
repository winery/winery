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
import java.util.Objects;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
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
import io.github.edmm.core.parser.SequenceEntity;
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
        EntityId sourceComponentEntityId = EntityGraph.COMPONENTS.extend(relationship.getSourceElement().getRef().getId());
        // the entity will always be in the graph since we first transform the NodeTemplates 
        entityGraph.getEntity(sourceComponentEntityId).ifPresent(entity -> {
            EntityId relationTypeEntityId = createRelationType(relationship.getType(), entityGraph);

            EntityId relationsCollectionEntityId = sourceComponentEntityId.extend(DefaultKeys.RELATIONS);
            if (!entityGraph.getEntity(relationsCollectionEntityId).isPresent()) {
                entityGraph.addEntity(new SequenceEntity(relationsCollectionEntityId, entityGraph));
            }

            EntityId relationEntityId = relationsCollectionEntityId.extend(relationTypeEntityId.getName());

            if (Objects.nonNull(relationship.getProperties()) && Objects.nonNull(relationship.getProperties().getKVProperties())) {
                entityGraph.addEntity(new MappingEntity(relationEntityId, entityGraph));
                createProperties(relationship, relationEntityId, entityGraph);
            } else {
                String targetComponent = relationship.getTargetElement().getRef().getId();
                entityGraph.addEntity(new ScalarEntity(targetComponent, relationEntityId, entityGraph));
            }
        });
    }

    private void createNode(TNodeTemplate nodeTemplate, List<TRelationshipTemplate> relationshipTemplates, EntityGraph entityGraph) {
        // create the component inside the topology.
        EntityId componentNodeId = EntityGraph.COMPONENTS.extend(nodeTemplate.getId());
        entityGraph.addEntity(new MappingEntity(componentNodeId, entityGraph));

        // add the type to the model
        EntityId nodeTypeEntityId = createNodeType(nodeTemplate.getType(), entityGraph);
        entityGraph.addEntity(new ScalarEntity(nodeTypeEntityId.getName(), componentNodeId.extend(DefaultKeys.TYPE), entityGraph));

        createProperties(nodeTemplate, componentNodeId, entityGraph);
    }

    private void createProperties(TEntityTemplate toscaTemplate, EntityId componentNodeId, EntityGraph entityGraph) {
        if (Objects.nonNull(toscaTemplate.getProperties()) && Objects.nonNull(toscaTemplate.getProperties().getKVProperties())) {
            EntityId propertiesEntityId = componentNodeId.extend(DefaultKeys.PROPERTIES);
            entityGraph.addEntity(new MappingEntity(propertiesEntityId, entityGraph));

            toscaTemplate.getProperties().getKVProperties()
                .forEach((key, value) -> {
                    EntityId propertyEntityId = propertiesEntityId.extend(key);
                    entityGraph.addEntity(new ScalarEntity(value, propertyEntityId, entityGraph));
                });
        }
    }

    private EntityId createNodeType(QName type, EntityGraph entityGraph) {
        if (!entityGraph.getEntity(EntityGraph.COMPONENT_TYPES).isPresent()) {
            entityGraph.addEntity(new MappingEntity(EntityGraph.COMPONENT_TYPES, entityGraph));
        }

        EntityId nodeTypeEntityId = EntityGraph.COMPONENT_TYPES.extend(normalizeQName(type));

        createTypeDefinition(nodeTypes.get(type), nodeTypeEntityId, entityGraph);

        return nodeTypeEntityId;
    }

    private EntityId createRelationType(QName type, EntityGraph entityGraph) {
        if (!entityGraph.getEntity(EntityGraph.RELATION_TYPES).isPresent()) {
            entityGraph.addEntity(new MappingEntity(EntityGraph.RELATION_TYPES, entityGraph));
        }

        EntityId relationTypeEntityId = EntityGraph.RELATION_TYPES.extend(normalizeQName(type));
        createTypeDefinition(relationshipTypes.get(type), relationTypeEntityId, entityGraph);

        return relationTypeEntityId;
    }

    private void createTypeDefinition(TEntityType toscaType, EntityId typeEntityId, EntityGraph entityGraph) {
        Optional<Entity> entity = entityGraph.getEntity(typeEntityId);
        if (!entity.isPresent()) {
            entityGraph.addEntity(new MappingEntity(typeEntityId, entityGraph));

            if (Objects.nonNull(toscaType.getDerivedFrom())) {
                EntityId baseTypeEntityId = createNodeType(toscaType.getDerivedFrom().getType(), entityGraph);
                entityGraph.addEntity(new ScalarEntity(baseTypeEntityId.getName(), typeEntityId.extend(DefaultKeys.EXTENDS), entityGraph));
            } else if (toscaType instanceof TNodeType) {
                entityGraph.addEntity(new ScalarEntity("base", typeEntityId.extend(DefaultKeys.EXTENDS), entityGraph));
            } else if (toscaType instanceof TRelationshipType) {
                entityGraph.addEntity(new ScalarEntity(null, typeEntityId.extend(DefaultKeys.EXTENDS), entityGraph));
            }

            this.createPropertiesDefinition(toscaType, typeEntityId, entityGraph);
        }
    }

    private void createPropertiesDefinition(TEntityType toscaType, EntityId typeEntityId, EntityGraph entityGraph) {
        if (Objects.nonNull(toscaType.getWinerysPropertiesDefinition())) {
            EntityId propertiesEntityId = typeEntityId.extend(DefaultKeys.PROPERTIES);
            entityGraph.addEntity(new MappingEntity(propertiesEntityId, entityGraph));

            toscaType.getWinerysPropertiesDefinition().getPropertyDefinitionKVList().getPropertyDefinitionKVs()
                .forEach(propertyDef -> {
                    EntityId propertyEntityId = propertiesEntityId.extend(propertyDef.getKey());
                    entityGraph.addEntity(new MappingEntity(propertyEntityId, entityGraph));

                    String normalizedType = propertyDef.getType().replace("xsd:", "");
                    EntityId propertyTypeEntityId = propertyEntityId.extend(DefaultKeys.TYPE);
                    entityGraph.addEntity(new ScalarEntity(normalizedType, propertyTypeEntityId, entityGraph));
                });
        }
    }

    private static String normalizeQName(QName qName) {
        return qName.toString().substring(1)
            .replace("}", "__")
            .replace("/", "")
            .replace(':', '_');
    }
}
