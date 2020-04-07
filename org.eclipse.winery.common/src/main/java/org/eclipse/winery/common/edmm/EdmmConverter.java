/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.common.edmm;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
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
    private final Map<QName, TNodeTypeImplementation> nodeTypeImplementations;
    private final Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations;
    private final Map<QName, TArtifactTemplate> artifactTemplates;
    private final Map<QName, EdmmType> edmmTypeMappings;
    private final Map<QName, EdmmType> oneToOneMappings;
    private final boolean useAbsolutePaths;

    public EdmmConverter(Map<QName, TNodeType> nodeTypes, Map<QName, TRelationshipType> relationshipTypes,
                         Map<QName, TNodeTypeImplementation> nodeTypeImplementations,
                         Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations,
                         Map<QName, TArtifactTemplate> artifactTemplates,
                         Map<QName, EdmmType> edmmTypeMappings, Map<QName, EdmmType> oneToOneMappings) {
        this(nodeTypes, relationshipTypes, nodeTypeImplementations, relationshipTypeImplementations, artifactTemplates,
            edmmTypeMappings, oneToOneMappings, true);
    }

    public EdmmConverter(Map<QName, TNodeType> nodeTypes, Map<QName, TRelationshipType> relationshipTypes,
                         Map<QName, TNodeTypeImplementation> nodeTypeImplementations,
                         Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations,
                         Map<QName, TArtifactTemplate> artifactTemplates, Map<QName, EdmmType> edmmTypeMappings,
                         Map<QName, EdmmType> oneToOneMappings, boolean useAbsolutePaths) {
        this.nodeTypes = nodeTypes;
        this.relationshipTypes = relationshipTypes;
        this.nodeTypeImplementations = nodeTypeImplementations;
        this.relationshipTypeImplementations = relationshipTypeImplementations;
        this.artifactTemplates = artifactTemplates;
        this.edmmTypeMappings = edmmTypeMappings;
        this.useAbsolutePaths = useAbsolutePaths;
        this.oneToOneMappings = oneToOneMappings;
    }

    public EntityGraph transform(TServiceTemplate serviceTemplate) {
        EntityGraph entityGraph = new EntityGraph();

        List<TNodeTemplate> nodeTemplates = serviceTemplate.getTopologyTemplate().getNodeTemplates();
        List<TRelationshipTemplate> relationshipTemplates = serviceTemplate.getTopologyTemplate().getRelationshipTemplates();
        if (!nodeTemplates.isEmpty()) {
            entityGraph.addEntity(new MappingEntity(EntityGraph.COMPONENTS, entityGraph));
        }

        nodeTemplates.forEach(nodeTemplate -> createNode(nodeTemplate, entityGraph));
        relationshipTemplates.forEach(relationship -> createRelation(relationship, entityGraph));

        return entityGraph;
    }

    private void createRelation(TRelationshipTemplate relationship, EntityGraph entityGraph) {
        EntityId sourceComponentEntityId = EntityGraph.COMPONENTS.extend(relationship.getSourceElement().getRef().getName());
        // the entity will always be in the graph since we first transform the NodeTemplates 
        entityGraph.getEntity(sourceComponentEntityId).ifPresent(entity -> {
            EntityId relationTypeEntityId = createType(
                relationshipTypes.get(relationship.getType()),
                EntityGraph.RELATION_TYPES,
                entityGraph
            );

            EntityId relationsCollectionEntityId = sourceComponentEntityId.extend(DefaultKeys.RELATIONS);
            if (!entityGraph.getEntity(relationsCollectionEntityId).isPresent()) {
                entityGraph.addEntity(new SequenceEntity(relationsCollectionEntityId, entityGraph));
            }

            EntityId relationEntityId = relationsCollectionEntityId.extend(relationTypeEntityId.getName());

            if (Objects.nonNull(relationship.getProperties()) && Objects.nonNull(relationship.getProperties().getKVProperties())) {
                entityGraph.addEntity(new MappingEntity(relationEntityId, entityGraph));
                createProperties(relationship, relationEntityId, entityGraph);
            } else {
                String targetComponent = relationship.getTargetElement().getRef().getName();
                entityGraph.addEntity(new ScalarEntity(targetComponent, relationEntityId, entityGraph));
            }
        });
    }

    private void createNode(TNodeTemplate nodeTemplate, EntityGraph entityGraph) {
        // create the component inside the topology.
        EntityId componentNodeId = EntityGraph.COMPONENTS.extend(nodeTemplate.getName());
        entityGraph.addEntity(new MappingEntity(componentNodeId, entityGraph));

        // add the type to the model
        EntityId nodeTypeEntityId = this.createType(
            nodeTypes.get(nodeTemplate.getType()),
            EntityGraph.COMPONENT_TYPES,
            entityGraph
        );
        entityGraph.addEntity(new ScalarEntity(nodeTypeEntityId.getName(), componentNodeId.extend(DefaultKeys.TYPE), entityGraph));

        createProperties(nodeTemplate, componentNodeId, entityGraph);
        createArtifact(nodeTemplate, componentNodeId, entityGraph);
        createOperations(nodeTypes.get(nodeTemplate.getType()), componentNodeId, entityGraph);
    }

    private void createArtifact(TNodeTemplate nodeTemplate, EntityId componentNodeId, EntityGraph entityGraph) {
        if (nodeTemplate.getDeploymentArtifacts() != null
            && nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact().size() > 0) {
            EntityId artifactsEntityId = componentNodeId.extend(DefaultKeys.ARTIFACTS);
            entityGraph.addEntity(new SequenceEntity(artifactsEntityId, entityGraph));

            for (TDeploymentArtifact artifact : nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact()) {
                String path = null;

                TArtifactTemplate artifactTemplate = artifactTemplates.get(artifact.getArtifactRef());
                if (artifactTemplate != null && artifactTemplate.getArtifactReferences().getArtifactReference().size() > 0) {
                    path = artifactTemplate.getArtifactReferences().getArtifactReference().get(0).getReference();
                }

                EntityId artifactEntityId = artifactsEntityId.extend(
                    artifact.getArtifactType().getLocalPart().toLowerCase()
                );
                
                createPathReferenceEntity(entityGraph, path, artifactEntityId);
                
                entityGraph.addEntity(new ScalarEntity(
                    path != null && this.useAbsolutePaths ? Environments.getInstance().getRepositoryConfig().getRepositoryRoot() + "/" + path : path,
                    artifactEntityId,
                    entityGraph
                ));
            }
        }
    }

    private void createPathReferenceEntity(EntityGraph entityGraph, String givenPath, EntityId entityId) {
        String path = givenPath;
        if (givenPath != null) {
            try {
                path = URLDecoder.decode(this.useAbsolutePaths
                        ? Environments.getInstance().getRepositoryConfig().getRepositoryRoot() + "/" + givenPath
                        : givenPath,
                    "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        entityGraph.addEntity(
            new ScalarEntity(path, entityId, entityGraph)
        );
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

    private EntityId createType(TEntityType toscaType, EntityId parentEntityId, EntityGraph entityGraph) {
        if (!entityGraph.getEntity(parentEntityId).isPresent()) {
            entityGraph.addEntity(new MappingEntity(parentEntityId, entityGraph));
        }

        EntityId typeEntityId = parentEntityId.extend(this.normalizeQName(toscaType.getQName()));
        EdmmType edmmType = oneToOneMappings.get(toscaType.getQName());

        if (edmmType != null) {
            typeEntityId = parentEntityId.extend(edmmType.getValue());
            entityGraph.addEntity(new MappingEntity(typeEntityId, entityGraph));
            EdmmTypeProperties.getDefaultConfiguration(edmmType, entityGraph);

            this.createPropertiesDefinition(toscaType, typeEntityId, entityGraph);

            if (Objects.nonNull(toscaType.getDerivedFrom())) {
                QName inheritsFrom = toscaType.getDerivedFrom().getType();
                TEntityType parent = toscaType instanceof TNodeType
                    ? nodeTypes.get(inheritsFrom)
                    : relationshipTypes.get(inheritsFrom);
                createType(parent, parentEntityId, entityGraph);
            }
        } else {
            Optional<Entity> entity = entityGraph.getEntity(typeEntityId);
            if (!entity.isPresent()) {
                entityGraph.addEntity(new MappingEntity(typeEntityId, entityGraph));

                if (Objects.nonNull(toscaType.getDerivedFrom())) {
                    QName inheritsFrom = toscaType.getDerivedFrom().getType();
                    TEntityType parent = toscaType instanceof TNodeType
                        ? nodeTypes.get(inheritsFrom)
                        : relationshipTypes.get(inheritsFrom);

                    EntityId baseTypeEntityId = createType(parent, parentEntityId, entityGraph);
                    entityGraph.addEntity(
                        new ScalarEntity(baseTypeEntityId.getName(), typeEntityId.extend(DefaultKeys.EXTENDS), entityGraph)
                    );
                } else {
                    String parentElement = "base";

                    edmmType = edmmTypeMappings.get(toscaType.getQName());
                    if (edmmType != null) {
                        parentElement = edmmType.getValue();
                        EdmmTypeProperties.getDefaultConfiguration(edmmType, entityGraph);
                    } else if (toscaType instanceof TRelationshipType) {
                        parentElement = EdmmType.DEPENDS_ON.getValue();
                        EdmmTypeProperties.getDefaultConfiguration(EdmmType.DEPENDS_ON, entityGraph);
                    }

                    entityGraph.addEntity(
                        new ScalarEntity(parentElement, typeEntityId.extend(DefaultKeys.EXTENDS), entityGraph)
                    );
                }

                this.createPropertiesDefinition(toscaType, typeEntityId, entityGraph);
            }
        }

        return typeEntityId;
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

    private void createOperations(TEntityType type, EntityId nodeTypeEntityId, EntityGraph entityGraph) {
        if (type instanceof TNodeType && Objects.nonNull(((TNodeType) type).getInterfaces())) {
            TInterfaces interfaces = ((TNodeType) type).getInterfaces();
            interfaces.getInterface().forEach(anInterface -> {
                anInterface.getOperation().forEach(operation -> {
                    EntityId operationsEntityId = nodeTypeEntityId.extend(DefaultKeys.OPERATIONS);
                    entityGraph.addEntity(new MappingEntity(operationsEntityId, entityGraph));

                    TNodeTypeImplementation implementation = nodeTypeImplementations.values().stream()
                        .filter(impl -> impl.getNodeType().equals(type.getQName()))
                        .findFirst()
                        .orElse(null);
                    String path = getImplementationForOperation(implementation, anInterface.getName(), operation.getName());

                    EntityId operationId = operationsEntityId.extend(operation.getName());
                    createPathReferenceEntity(entityGraph, path, operationId);
                });
            });
        }
    }

    private String getImplementationForOperation(TEntityTypeImplementation implementation,
                                                 String interfaceName, String operationName) {
        if (implementation != null && implementation.getImplementationArtifacts() != null) {
            List<TImplementationArtifacts.ImplementationArtifact> artifacts = implementation.getImplementationArtifacts()
                .getImplementationArtifact().stream()
                .filter(artifact -> artifact.getInterfaceName().equals(interfaceName))
                .collect(Collectors.toList());

            if (artifacts.size() == 1 && artifacts.get(0).getArtifactRef() != null) {
                TArtifactTemplate artifactTemplate = artifactTemplates.get(artifacts.get(0).getArtifactRef());
                if (artifactTemplate.getArtifactReferences().getArtifactReference().size() > 0) {
                    return artifactTemplate.getArtifactReferences().getArtifactReference().get(0).getReference();
                }
            }

            for (TImplementationArtifacts.ImplementationArtifact artifact : artifacts) {
                if (artifact.getOperationName().equals(operationName)) {
                    TArtifactTemplate artifactTemplate = artifactTemplates.get(artifact.getArtifactRef());
                    if (artifactTemplate != null && artifactTemplate.getArtifactReferences().getArtifactReference().size() > 0) {
                        return artifactTemplate.getArtifactReferences().getArtifactReference().get(0).getReference();
                    }
                }
            }
        }
        return null;
    }

    private String normalizeQName(QName qName) {
        return qName.toString()
            .replace("{", "")
            .replace("}", "__")
            .replace("/", "")
            .replace(':', '_');
    }
}
