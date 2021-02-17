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

package org.eclipse.winery.model.adaptation.substitution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TCapabilityRef;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Substitution extends AbstractSubstitution {

    private static final Logger LOGGER = LoggerFactory.getLogger(Substitution.class);

    private final Map<QName, TServiceTemplate> nodeTypeSubstitutableWithServiceTemplate = new HashMap<>();
    private final Map<QName, TRequirementType> requirementTypes = new HashMap<>();
    private final Map<QName, TCapabilityType> capabilityTypes = new HashMap<>();

    public ServiceTemplateId substituteTopologyOfServiceTemplate(final ServiceTemplateId serviceTemplateId) {
        // 0. Create a new version of the Service Template
        ServiceTemplateId substitutedServiceTemplateId = getSubstitutionServiceTemplateId(serviceTemplateId);
        TServiceTemplate serviceTemplate = repository.getElement(substitutedServiceTemplateId);
        TTopologyTemplate topology = Objects.requireNonNull(serviceTemplate.getTopologyTemplate());

        loadAllRequiredDefinitionsForTopologySubstitution();

        // 1. Step: retrieve all Node Templates which must be substituted
        Map<TNodeTemplate, List<Subtypes<TNodeType>>> substitutableNodeTemplates =
            SubstitutionUtils.collectSubstitutableTemplates(topology.getNodeTemplates(), this.nodeTypes);

        // 2. Step: retrieve all Relationship Templates which must be substituted
        Map<TRelationshipTemplate, List<Subtypes<TRelationshipType>>> substitutableRelationshipTemplates =
            SubstitutionUtils.collectSubstitutableTemplates(topology.getRelationshipTemplates(), this.relationshipTypes);

        // 3. Step: select concrete type to be substituted
        Map<TNodeTemplate, TNodeType> nodeTemplateReplacementMap =
            new FindFirstSubstitutionStrategy<TNodeTemplate, TNodeType>()
                .getReplacementMap(substitutableNodeTemplates);
        Map<TRelationshipTemplate, TRelationshipType> relationshipTemplateReplacementMap =
            new FindFirstSubstitutionStrategy<TRelationshipTemplate, TRelationshipType>()
                .getReplacementMap(substitutableRelationshipTemplates);

        // 4. Step: update the topology
        updateTopology(topology, nodeTemplateReplacementMap, relationshipTemplateReplacementMap);

        try {
            BackendUtils.persist(repository, substitutedServiceTemplateId, serviceTemplate);
        } catch (IOException e) {
            LOGGER.debug("Could not persist Service Template", e);
        }

        return substitutedServiceTemplateId;
    }

    private void updateTopology(TTopologyTemplate topologyTemplate, Map<TNodeTemplate, TNodeType> nodeTemplateReplacementMap,
                                Map<TRelationshipTemplate, TRelationshipType> relationshipTemplateReplacementMap) {
        Map<TNodeTemplate, TServiceTemplate> nodeTemplateToBeSubstitutedWithTopology = new HashMap<>();

        topologyTemplate.getNodeTemplates()
            .forEach(tNodeTemplate -> {
                TServiceTemplate serviceTemplate = this.nodeTypeSubstitutableWithServiceTemplate.get(tNodeTemplate.getType());
                if (Objects.nonNull(serviceTemplate)) {
                    // We need to replace the Node Template with the serviceTemplate but we cannot do that here
                    // -> save it for later processing
                    nodeTemplateToBeSubstitutedWithTopology.put(tNodeTemplate, serviceTemplate);
                } else {
                    // In case of simple NodeTemplate substitution
                    // -> everything is inherited -> there is no need to change anything else
                    TNodeType replacementType = nodeTemplateReplacementMap.get(tNodeTemplate);
                    if (Objects.nonNull(replacementType)) {
                        QName qName = new QName(replacementType.getTargetNamespace(), replacementType.getIdFromIdOrNameField());
                        tNodeTemplate.setType(qName);
                    }
                }
            });

        topologyTemplate.getRelationshipTemplates()
            .forEach(tRelationshipTemplate -> {
                TRelationshipType tRelationshipType = relationshipTemplateReplacementMap.get(tRelationshipTemplate);
                if (Objects.nonNull(tRelationshipType)) {
                    QName qName = new QName(tRelationshipType.getTargetNamespace(), tRelationshipType.getIdFromIdOrNameField());
                    tRelationshipTemplate.setType(qName);
                }
            });

        replaceNodeTemplateWithServiceTemplate(topologyTemplate, nodeTemplateToBeSubstitutedWithTopology);
    }

    private void replaceNodeTemplateWithServiceTemplate(TTopologyTemplate topologyTemplate, Map<TNodeTemplate, TServiceTemplate> nodeTemplateToBeSubstitutedWithTopology) {
        nodeTemplateToBeSubstitutedWithTopology.forEach((substitutableNodeTemplate, stSubstitutingTheNodeTemplate) -> {
            if (Objects.nonNull(stSubstitutingTheNodeTemplate.getBoundaryDefinitions())) {
                TTopologyTemplate topologyToImport = stSubstitutingTheNodeTemplate.getTopologyTemplate();

                // 1. get all references of the Node Template
                // 1.1 Relationships
                // TODO 1.2 Boundary definitions
                List<TRelationshipTemplate> ingoingRelations = new ArrayList<>();
                List<TRelationshipTemplate> outgoingRelations = new ArrayList<>();

                topologyTemplate.getRelationshipTemplates()
                    .forEach(tRelationshipTemplate -> {
                        if (substitutableNodeTemplate.getId().equals(tRelationshipTemplate.getSourceElement().getRef().getId())) {
                            outgoingRelations.add(tRelationshipTemplate);
                        }

                        if (substitutableNodeTemplate.getId().equals(tRelationshipTemplate.getTargetElement().getRef().getId())) {
                            ingoingRelations.add(tRelationshipTemplate);
                        }
                    });

                // 2. import the topology in the Service Template
                BackendUtils.mergeTopologyTemplateAinTopologyTemplateB(topologyToImport, topologyTemplate);

                // 3. update the references accordingly
                if (ingoingRelations.size() > 0) {
                    if (Objects.nonNull(stSubstitutingTheNodeTemplate.getBoundaryDefinitions().getCapabilities())) {
                        List<TCapabilityRef> capabilities = stSubstitutingTheNodeTemplate.getBoundaryDefinitions().getCapabilities().getCapability();
                        ingoingRelations.forEach(ingoing -> {
                            capabilities.forEach(tCapabilityRef -> {
                                topologyToImport.getNodeTemplates().stream()
                                    .filter(tNodeTemplate ->
                                        // find the node template which defines the capability in the boundaries
                                        Objects.nonNull(tNodeTemplate.getCapabilities()) && tNodeTemplate.getCapabilities().getCapability()
                                            .stream()
                                            .anyMatch(tCapability -> tCapability.equals(tCapabilityRef.getRef()))
                                    )
                                    .findFirst()
                                    .ifPresent(tNodeTemplate -> {
                                        ingoing.getTargetElement().setRef(tNodeTemplate);
                                    });
                            });
                        });
                    } else {
                        throw new UnsupportedOperationException("Mapping without Reqs/Caps is currently not supported");
                    }
                }

                if (outgoingRelations.size() > 0) {
                    if (Objects.nonNull(stSubstitutingTheNodeTemplate.getBoundaryDefinitions().getRequirements())) {
                        List<TRequirementRef> requirements = stSubstitutingTheNodeTemplate.getBoundaryDefinitions().getRequirements().getRequirement();
                        outgoingRelations.forEach(outgoing -> {
                            requirements.forEach(requirementRef -> {
                                topologyToImport.getNodeTemplates().stream()
                                    .filter(tNodeTemplate ->
                                        Objects.nonNull(tNodeTemplate.getRequirements()) &&
                                            tNodeTemplate.getRequirements().getRequirement()
                                                .stream()
                                                .anyMatch(tRequirement -> tRequirement.equals(requirementRef.getRef()))
                                    )
                                    .findFirst()
                                    .ifPresent(tNodeTemplate -> {
                                        outgoing.getSourceElement().setRef(tNodeTemplate);
                                    });
                            });
                        });
                    } else {
                        throw new UnsupportedOperationException("Mapping without Reqs/Caps is currently not supported");
                    }
                }

                // TODO: propagate property mappings

                topologyTemplate.getNodeTemplateOrRelationshipTemplate().remove(substitutableNodeTemplate);
            }
        });
    }

    private void loadAllRequiredDefinitionsForTopologySubstitution() {
        this.repository.getAllDefinitionsChildIds(ServiceTemplateId.class)
            .stream()
            .map(repository::getElement)
            .filter(element -> Objects.nonNull(element.getSubstitutableNodeType()))
            .forEach(tServiceTemplate ->
                this.nodeTypeSubstitutableWithServiceTemplate.put(tServiceTemplate.getSubstitutableNodeType(), tServiceTemplate)
            );

        this.repository.getAllDefinitionsChildIds(RequirementTypeId.class)
            .forEach(id ->
                this.requirementTypes.put(id.getQName(), this.repository.getElement(id))
            );

        this.repository.getAllDefinitionsChildIds(CapabilityTypeId.class)
            .forEach(id ->
                this.capabilityTypes.put(id.getQName(), this.repository.getElement(id))
            );
    }
}
