/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.substitution.refinement.placeholder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.substitution.AbstractSubstitution;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.WineryVersionUtils;
import org.eclipse.winery.repository.splitting.Splitting;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaPropertyMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaceholderSubstitution extends AbstractSubstitution {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceholderSubstitution.class);
    private static int newRelationshipIdCounter = 100;
    private static int IdCounter = 1;

    private ServiceTemplateId serviceTemplateId;
    private TTopologyTemplate topologyTemplate;
    private SubstitutionChooser substitutionChooser;

    private ServiceTemplateId substitutionServiceTemplateId;

    public PlaceholderSubstitution(ServiceTemplateId serviceTemplateId, SubstitutionChooser substitutionChooser) {
        this.serviceTemplateId = serviceTemplateId;
        this.substitutionChooser = substitutionChooser;
        substitutionServiceTemplateId = this.getSubstitutionServiceTemplateId(this.serviceTemplateId);
        this.topologyTemplate = RepositoryFactory.getRepository().getElement(serviceTemplateId).getTopologyTemplate();
    }

    private boolean isApplicable(PlaceholderSubstitutionCandidate candidate) {
        return candidate.getDetectorGraph().vertexSet().stream().allMatch(v -> {
            try {
                TNodeTemplate placeholder = getPlaceholder(v.getTemplate());
                Set<QName> placeholderCapabilityTypesQNames = new HashSet<>();
                Set<String> placeholderPropertyNames = new HashSet<>();
                if (ModelUtilities.getPropertiesKV(placeholder) != null) {
                    placeholderPropertyNames = ModelUtilities.getPropertiesKV(placeholder).keySet();
                }

                if (placeholder.getCapabilities() != null) {
                    List<TCapability> capabilities = placeholder.getCapabilities();
                    for (TCapability capability : capabilities) {
                        placeholderCapabilityTypesQNames.add(capability.getType());
                    }
                }

                if (candidate.getServiceTemplateCandidate().getTopologyTemplate().getNodeTemplates().stream()
                    .anyMatch(nt -> nt.getType().getNamespaceURI().equals(OpenToscaBaseTypes.placeholderTypeNamespace))) {
                    return false;
                }

                TNodeTemplate correspondingNode = candidate.getServiceTemplateCandidate().getTopologyTemplate()
                    .getNodeTemplate(candidate.getGraphMapping().getVertexCorrespondence(v, false).getTemplate().getId());
                HostingStackCharacteristics hostingStackcharacteristics =
                    getHostingStackCharacteristics(candidate.getServiceTemplateCandidate().getTopologyTemplate(), correspondingNode);
                if (!hostingStackcharacteristics.getHostingStackCapabilityTypes().containsAll(placeholderCapabilityTypesQNames) ||
                    !hostingStackcharacteristics.getHostingStackKVProperties().containsAll(placeholderPropertyNames)) {
                    return false;
                }
            } catch (PlaceholderSubstitutionException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    public void applySubstitution(PlaceholderSubstitutionCandidate substitution) {
        TTopologyTemplate substitutionTemplate = substitution.getServiceTemplateCandidate().getTopologyTemplate();
        Set<TNodeTemplate> hostedOnSuccessors = new HashSet<>();
        for (String id : substitution.getNodeIdsOfMatchingNodesInCandidate()) {
            hostedOnSuccessors.addAll(ModelUtilities.getHostedOnSuccessors(substitutionTemplate, id));
        }
        Set<TRelationshipTemplate> hostedOnRelations = new HashSet<>();
        for (TNodeTemplate successor : hostedOnSuccessors) {
            //Get all outgoing hostedOn Relationships
            hostedOnRelations.addAll(ModelUtilities.getOutgoingRelationshipTemplates(substitutionTemplate, successor).stream()
                .filter(r -> Splitting.getBaseRelationshipType(r.getType()).getQName().equals(ToscaBaseTypes.hostedOnRelationshipType))
                .collect(Collectors.toSet()));
        }

        TServiceTemplate serviceTemplate = RepositoryFactory.getRepository().getElement(serviceTemplateId);

        //Add nodes and relationships
        hostedOnSuccessors.forEach(n -> {
            n.setId(n.getId() + IdCounter++);
            if (ModelUtilities.getOwnerParticipantOfServiceTemplate(serviceTemplate) != null) {
                ModelUtilities.setParticipant(n, ModelUtilities.getOwnerParticipantOfServiceTemplate(serviceTemplate));
            }
            this.topologyTemplate.addNodeTemplate(n);
        });
        hostedOnRelations.forEach(r -> {
            r.setId(r.getId() + newRelationshipIdCounter++);
            this.topologyTemplate.addRelationshipTemplate(r);
        });

        substitution.getDetectorGraph().vertexSet()
            .forEach(v -> {
                try {
                    TNodeTemplate placeholder = getPlaceholder(v.getTemplate());
                    TRelationshipTemplate incomingHostedOnRelation = ModelUtilities.getIncomingRelationshipTemplates(this.topologyTemplate, placeholder)
                        .stream()
                        .filter(r -> Splitting.getBaseRelationshipType(r.getType()).getQName().equals(ToscaBaseTypes.hostedOnRelationshipType))
                        .findFirst().get();

                    TNodeTemplate correspondingNode = substitution.getServiceTemplateCandidate().getTopologyTemplate()
                        .getNodeTemplate(substitution.getGraphMapping().getVertexCorrespondence(v, false).getTemplate().getId());
                    TRelationshipTemplate outgoingRelationship = ModelUtilities.getOutgoingRelationshipTemplates(substitutionTemplate, correspondingNode)
                        .stream()
                        .filter(r -> Splitting.getBaseRelationshipType(r.getType()).getQName().equals(ToscaBaseTypes.hostedOnRelationshipType))
                        .findFirst().get();
                    TNodeTemplate directHostedOnSuccessor = ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(substitutionTemplate, outgoingRelationship);

                    incomingHostedOnRelation.setTargetNodeTemplate(directHostedOnSuccessor);
                    this.topologyTemplate.getNodeTemplateOrRelationshipTemplate().remove(placeholder);
                } catch (PlaceholderSubstitutionException e) {
                    e.printStackTrace();
                }
            });
    }

    public ServiceTemplateId substituteServiceTemplate(TTopologyTemplate subGraphDetector) {
        TServiceTemplate element = this.repository.getElement(substitutionServiceTemplateId);

        this.substitutePlaceholders(subGraphDetector);
        element.setTopologyTemplate(topologyTemplate);
        try {
            this.repository.setElement(substitutionServiceTemplateId, element);
        } catch (IOException e) {
            LOGGER.error("Error while saving refined topology", e);
        }

        return substitutionServiceTemplateId;
    }

    public void substitutePlaceholders(TTopologyTemplate subGraphDetector) {
        ToscaIsomorphismMatcher isomorphismMatcher = new ToscaIsomorphismMatcher();
        int[] id = new int[1];

        //Detector is the subgraph of application-specific components of the origin topology
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(subGraphDetector);

        Map<QName, TServiceTemplate> serviceTemplateCandidates = getServiceTemplateCandidates();
        List<PlaceholderSubstitutionCandidate> matchingCandidates = new ArrayList<>();
        serviceTemplateCandidates.forEach((qName, st) -> {
            ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(st.getTopologyTemplate());
            IToscaMatcher matcher = new ToscaPropertyMatcher();
            Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = isomorphismMatcher.findMatches(detectorGraph, topologyGraph, matcher);

            matches.forEachRemaining(mapping -> {
                PlaceholderSubstitutionCandidate candidate = new PlaceholderSubstitutionCandidate(qName.getNamespaceURI(), qName.getLocalPart(), st, mapping, detectorGraph, id[0]++);
                if (isApplicable(candidate)) {
                    matchingCandidates.add(candidate);
                }
            });
        });

        if (matchingCandidates.size() == 0) {
            return;
        }

        PlaceholderSubstitutionCandidate substitution = this.substitutionChooser.chooseSubstitution(matchingCandidates, this.substitutionServiceTemplateId, this.topologyTemplate);

        if (Objects.isNull(substitution)) {
            return;
        }

        applySubstitution(substitution);
    }

    private Map<QName, TServiceTemplate> getServiceTemplateCandidates() {
        Map<QName, TServiceTemplate> serviceTemplates = repository.getQNameToElementMapping(ServiceTemplateId.class);
        Set<ServiceTemplateId> versionsOfConsideredServiceTemplate = (Set<ServiceTemplateId>) WineryVersionUtils.getOtherVersionDefinitionsFromDefinition(serviceTemplateId, repository);
        Set<QName> versionQNames = new HashSet<>();
        versionsOfConsideredServiceTemplate.stream().forEach(st -> versionQNames.add(st.getQName()));

        return serviceTemplates.entrySet()
            .stream()
            .filter(entry -> !entry.getKey().equals(this.serviceTemplateId.getQName()))
            .filter(entry -> !entry.getKey().equals(this.substitutionServiceTemplateId.getQName()))
            .filter(entry -> !versionQNames.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private TNodeTemplate getPlaceholder(TNodeTemplate nodeTemplate) throws PlaceholderSubstitutionException {
        TNodeTemplate lastHostedOnSuccessor;
        Optional<TRelationshipTemplate> hostedOn;

        List<TRelationshipTemplate> outgoingRelationshipTemplates = ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate);
        hostedOn = outgoingRelationshipTemplates.stream()
            .filter(relation -> Splitting.getBaseRelationshipType(relation.getType()).getQName().equals(ToscaBaseTypes.hostedOnRelationshipType))
            .findFirst();
        if (hostedOn.isPresent()) {
            lastHostedOnSuccessor = ModelUtilities.getNodeTemplateFromRelationshipSourceOrTarget(topologyTemplate, hostedOn.get().getTargetElement().getRef());
            return getPlaceholder(lastHostedOnSuccessor);
        } else {
            return nodeTemplate;
        }
    }

    private HostingStackCharacteristics getHostingStackCharacteristics(TTopologyTemplate topologyTemplate, TNodeTemplate nodeTemplate) {
        HostingStackCharacteristics hostingStackCharacteristics = new HostingStackCharacteristics(nodeTemplate);
        List<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topologyTemplate, nodeTemplate);
        TNodeTemplate successor;
        while (!hostedOnSuccessors.isEmpty()) {
            successor = hostedOnSuccessors.remove(0);
            LinkedHashMap<String, String> propertiesKV = ModelUtilities.getPropertiesKV(successor);
            if (propertiesKV != null) {
                hostingStackCharacteristics.addKVPropertyToProperties(propertiesKV.keySet());
            }
            if (successor.getCapabilities() != null) {
                List<TCapability> capabilities = successor.getCapabilities();
                for (TCapability capability : capabilities) {
                    hostingStackCharacteristics.addCapability(capability.getType());
                }
            }
        }
        return hostingStackCharacteristics;
    }
}
