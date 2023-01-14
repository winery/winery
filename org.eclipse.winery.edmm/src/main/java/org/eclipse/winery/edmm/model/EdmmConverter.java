/*******************************************************************************
 * Copyright (c) 2019-2023 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.edmm.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTParticipant;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.SequenceEntity;
import io.github.edmm.core.parser.support.DefaultKeys;

import static org.eclipse.winery.model.tosca.constants.Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE;

public class EdmmConverter {

    private final Map<QName, TNodeType> nodeTypes;
    private final Map<QName, TRelationshipType> relationshipTypes;
    private final Map<QName, TNodeTypeImplementation> nodeTypeImplementations;
    private final Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations;
    private final Map<QName, TArtifactTemplate> artifactTemplates;
    private final Map<QName, EdmmType> oneToOneMappings;
    private final boolean useAbsolutePaths;

    public EdmmConverter(Map<QName, TNodeType> nodeTypes, Map<QName, TRelationshipType> relationshipTypes,
                         Map<QName, TNodeTypeImplementation> nodeTypeImplementations,
                         Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations,
                         Map<QName, TArtifactTemplate> artifactTemplates,
                         Map<QName, EdmmType> oneToOneMappings) {
        this(nodeTypes, relationshipTypes, nodeTypeImplementations, relationshipTypeImplementations, artifactTemplates,
            oneToOneMappings, true);
    }

    public EdmmConverter(Map<QName, TNodeType> nodeTypes, Map<QName, TRelationshipType> relationshipTypes,
                         Map<QName, TNodeTypeImplementation> nodeTypeImplementations,
                         Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations,
                         Map<QName, TArtifactTemplate> artifactTemplates,
                         Map<QName, EdmmType> oneToOneMappings, boolean useAbsolutePaths) {
        this.nodeTypes = nodeTypes;
        this.relationshipTypes = relationshipTypes;
        this.nodeTypeImplementations = nodeTypeImplementations;
        this.relationshipTypeImplementations = relationshipTypeImplementations;
        this.artifactTemplates = artifactTemplates;
        this.useAbsolutePaths = useAbsolutePaths;
        this.oneToOneMappings = oneToOneMappings;
    }

    public EntityGraph transform(TServiceTemplate serviceTemplate) {
        assert serviceTemplate.getTopologyTemplate() != null;
        return this.transform(serviceTemplate.getTopologyTemplate(), ModelUtilities.getOwnerParticipantOfServiceTemplate(serviceTemplate));
    }

    public EntityGraph transform(TTopologyTemplate topology, String ownerParticipant) {
        EntityGraph entityGraph = new EntityGraph();
        setMetadata(entityGraph);
        List<TNodeTemplate> nodeTemplates = topology.getNodeTemplates();
        List<TRelationshipTemplate> relationshipTemplates = topology.getRelationshipTemplates();

        if (!nodeTemplates.isEmpty()) {
            entityGraph.addEntity(new MappingEntity(EntityGraph.COMPONENTS, entityGraph));
        }

        nodeTemplates.forEach(nodeTemplate -> createNode(nodeTemplate, entityGraph));
        relationshipTemplates.forEach(relationship -> createRelation(relationship, entityGraph));
        List<OTParticipant> participants = topology.getParticipants();

        if (participants != null && !participants.isEmpty() && ownerParticipant != null) {
            entityGraph.addEntity(new ScalarEntity(ownerParticipant, EntityGraph.OWNER, entityGraph));
            entityGraph.addEntity(new MappingEntity(EntityGraph.PARTICIPANTS, entityGraph));
            participants.forEach(participant -> createParticipant(participant, nodeTemplates, entityGraph));
        }

        createTechnologyMapping(nodeTemplates, entityGraph);

        return entityGraph;
    }

    private void setMetadata(EntityGraph entityGraph) {
        entityGraph.addEntity(new ScalarEntity("edm_1_0", EntityGraph.VERSION, entityGraph));
        entityGraph.addEntity(new ScalarEntity("12345", EntityGraph.MULTI_ID, entityGraph));
    }

    private void createTechnologyMapping(List<TNodeTemplate> nodeTemplates, EntityGraph entityGraph) {

        Map<String, List<TNodeTemplate>> deploymentTechnologyMapping = new HashMap<>();
        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            Map<QName, String> attributes = nodeTemplate.getOtherAttributes();
            String key = attributes.get(new QName(TOSCA_WINERY_EXTENSIONS_NAMESPACE, "deployment-technology"));
            if (key != null) {
                deploymentTechnologyMapping.computeIfAbsent(key, k -> new ArrayList<>());
                deploymentTechnologyMapping.get(key).add(nodeTemplate);
            }
        }

        if (!deploymentTechnologyMapping.isEmpty()) {
            entityGraph.addEntity(new MappingEntity(EntityGraph.ORCHESTRATION_TECHNOLOGY, entityGraph));

            deploymentTechnologyMapping.forEach((key, nodes) -> {
                EntityId entity = EntityGraph.ORCHESTRATION_TECHNOLOGY.extend(key);
                entityGraph.addEntity(new SequenceEntity(entity, entityGraph));
                for (TNodeTemplate nodeTemplate : nodes) {
                    EntityId valueEntity = entity.extend(nodeTemplate.getId());
                    entityGraph.addEntity(new ScalarEntity(nodeTemplate.getId(), valueEntity, entityGraph));
                }
            });
        }
    }

    private void createParticipant(OTParticipant participant, List<TNodeTemplate> nodeTemplates, EntityGraph entityGraph) {

        EntityId participantEntity = EntityGraph.PARTICIPANTS.extend(participant.getName());
        entityGraph.addEntity(new MappingEntity(participantEntity, entityGraph));

        EntityId endpointEntityId = participantEntity.extend(DefaultKeys.ENDPOINT);
        entityGraph.addEntity(new ScalarEntity(participant.getUrl(), endpointEntityId, entityGraph));

        EntityId componentsEntityId = participantEntity.extend(DefaultKeys.COMPONENTS);
        entityGraph.addEntity(new SequenceEntity(componentsEntityId, entityGraph));

        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            Map<QName, String> attributes = nodeTemplate.getOtherAttributes();
            String name = attributes.get(new QName(TOSCA_WINERY_EXTENSIONS_NAMESPACE, "participant"));
            if (participant.getName().equals(name)) {
                EntityId valueEntity = componentsEntityId.extend(nodeTemplate.getId());
                entityGraph.addEntity(new ScalarEntity(nodeTemplate.getId(), valueEntity, entityGraph));
            }
        }
    }

    private void createRelation(TRelationshipTemplate relationship, EntityGraph entityGraph) {
        EntityId sourceComponentEntityId = EntityGraph.COMPONENTS.extend(relationship.getSourceElement().getRef().getId());
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

            if (Objects.nonNull(relationship.getProperties()) && Objects.nonNull(ModelUtilities.getPropertiesKV(relationship))) {
                entityGraph.addEntity(new MappingEntity(relationEntityId, entityGraph));
                createProperties(relationship, relationEntityId, entityGraph);
            } else {
                String targetComponent = relationship.getTargetElement().getRef().getId();
                entityGraph.addEntity(new ScalarEntity(targetComponent, relationEntityId, entityGraph));
            }
        });
    }

    private void createNode(TNodeTemplate nodeTemplate, EntityGraph entityGraph) {
        // create the component inside the topology.
        EntityId componentNodeId = EntityGraph.COMPONENTS.extend(nodeTemplate.getId());
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
            && nodeTemplate.getDeploymentArtifacts().size() > 0) {
            EntityId artifactsEntityId = componentNodeId.extend(DefaultKeys.ARTIFACTS);
            entityGraph.addEntity(new SequenceEntity(artifactsEntityId, entityGraph));

            for (TDeploymentArtifact artifact : nodeTemplate.getDeploymentArtifacts()) {
                String path = null;

                TArtifactTemplate artifactTemplate = artifactTemplates.get(artifact.getArtifactRef());
                if (artifactTemplate != null && artifactTemplate.getArtifactReferences().size() > 0) {
                    path = artifactTemplate.getArtifactReferences().get(0).getReference();
                }

                EntityId artifactEntityId = artifactsEntityId.extend(
                    artifact.getArtifactType().getLocalPart().toLowerCase()
                );

                createPathReferenceEntity(entityGraph, path, artifactEntityId);
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
        EntityId propertiesEntityId = componentNodeId.extend(DefaultKeys.PROPERTIES);
        entityGraph.addEntity(new MappingEntity(propertiesEntityId, entityGraph));

        if (Objects.nonNull(toscaTemplate.getProperties()) && Objects.nonNull(ModelUtilities.getPropertiesKV(toscaTemplate))) {

            ModelUtilities.getPropertiesKV(toscaTemplate)
                .forEach((key, value) -> {
                    EntityId propertyEntityId = propertiesEntityId.extend(key);
                    entityGraph.addEntity(new ScalarEntity(value, propertyEntityId, entityGraph));
                });
        }

        // add name as property
        String name = toscaTemplate.getName();
        if (name == null) {
            name = toscaTemplate.getId();
        }
        EntityId propertyEntityId = propertiesEntityId.extend("name");
        entityGraph.addEntity(new ScalarEntity(name, propertyEntityId, entityGraph));
    }

    private EntityId createType(TEntityType toscaType, EntityId typeRoot, EntityGraph entityGraph) {
        if (!entityGraph.getEntity(typeRoot).isPresent()) {
            entityGraph.addEntity(new MappingEntity(typeRoot, entityGraph));
        }

        EntityId typeEntityId;
        EdmmType edmmType = oneToOneMappings.get(toscaType.getQName());
        typeEntityId = edmmType != null ?
            typeRoot.extend(edmmType.getValue()) :
            typeRoot.extend(this.normalizeQName(toscaType.getQName()));

        // the type we are trying to create is already there!
        if (entityGraph.getEntity(typeEntityId).isPresent()) {
            return typeEntityId;
        }

        entityGraph.addEntity(new MappingEntity(typeEntityId, entityGraph));
        EntityId parentEntityId;
        String parentType;

        if (Objects.nonNull(toscaType.getDerivedFrom())) {
            QName inheritsFrom = toscaType.getDerivedFrom().getType();
            TEntityType parent = toscaType instanceof TNodeType
                ? nodeTypes.get(inheritsFrom)
                : relationshipTypes.get(inheritsFrom);
            parentEntityId = createType(parent, typeRoot, entityGraph);
            MappingEntity parentEntity = (MappingEntity) entityGraph.getEntity(parentEntityId).get();
            parentType = parentEntity.getName();
        } else {
            parentType = null;
        }

        entityGraph.addEntity(new ScalarEntity(parentType, typeEntityId.extend(DefaultKeys.EXTENDS), entityGraph));
        this.createPropertiesDefinition(toscaType, typeEntityId, entityGraph);

        return typeEntityId;
    }

    private void createPropertiesDefinition(TEntityType toscaType, EntityId typeEntityId, EntityGraph entityGraph) {
        if (Objects.nonNull(toscaType.getWinerysPropertiesDefinition())) {
            EntityId propertiesEntityId = typeEntityId.extend(DefaultKeys.PROPERTIES);
            entityGraph.addEntity(new MappingEntity(propertiesEntityId, entityGraph));

            toscaType.getWinerysPropertiesDefinition().getPropertyDefinitions()
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
            List<TInterface> interfaces = ((TNodeType) type).getInterfaces();
            interfaces.forEach(anInterface -> {
                anInterface.getOperations().forEach(operation -> {
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
            List<TImplementationArtifact> artifacts = implementation.getImplementationArtifacts()
                .stream()
                .filter(artifact -> artifact.getInterfaceName() != null)
                .filter(artifact -> artifact.getInterfaceName().equals(interfaceName))
                .collect(Collectors.toList());

            if (artifacts.size() == 1 && artifacts.get(0).getArtifactRef() != null) {
                TArtifactTemplate artifactTemplate = artifactTemplates.get(artifacts.get(0).getArtifactRef());
                if (artifactTemplate.getArtifactReferences() != null &&
                    artifactTemplate.getArtifactReferences().size() > 0) {
                    return artifactTemplate.getArtifactReferences().get(0).getReference();
                }
            }

            for (TImplementationArtifact artifact : artifacts) {
                if (artifact.getOperationName() != null && artifact.getOperationName().equals(operationName)) {
                    TArtifactTemplate artifactTemplate = artifactTemplates.get(artifact.getArtifactRef());
                    if (artifactTemplate != null &&
                        artifactTemplate.getArtifactReferences() != null &&
                        artifactTemplate.getArtifactReferences().size() > 0) {
                        return artifactTemplate.getArtifactReferences().get(0).getReference();
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
