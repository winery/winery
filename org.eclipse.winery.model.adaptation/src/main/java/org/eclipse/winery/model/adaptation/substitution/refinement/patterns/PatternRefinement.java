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
package org.eclipse.winery.model.adaptation.substitution.refinement.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.winery.common.ids.definitions.PatternRefinementModelId;
import org.eclipse.winery.model.adaptation.substitution.SubstitutionUtils;
import org.eclipse.winery.model.adaptation.substitution.refinement.AbstractRefinement;
import org.eclipse.winery.model.adaptation.substitution.refinement.DefaultRefinementChooser;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementChooser;
import org.eclipse.winery.model.tosca.AttributeMapping;
import org.eclipse.winery.model.tosca.TAttributeMappingType;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TPrmMapping;
import org.eclipse.winery.model.tosca.TRefinementModel;
import org.eclipse.winery.model.tosca.TRelationDirection;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TStayMapping;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaPrmPropertyMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.jgrapht.GraphMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternRefinement extends AbstractRefinement {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatternRefinement.class);

    public PatternRefinement(RefinementChooser refinementChooser) {
        super(refinementChooser, PatternRefinementModelId.class, "refined");
    }

    public PatternRefinement() {
        this(new DefaultRefinementChooser());
    }

    public boolean isApplicable(RefinementCandidate candidate, TTopologyTemplate topology) {
        return candidate.getDetectorGraph().vertexSet()
            .stream()
            .allMatch(vertex -> {
                TNodeTemplate matchingNode = candidate.getGraphMapping().getVertexCorrespondence(vertex, false).getTemplate();
                return this.canRedirectExternalRelations(candidate, vertex.getTemplate(), matchingNode, topology);
            });
    }

    public void applyRefinement(RefinementCandidate refinement, TTopologyTemplate topology) {
        if (!(refinement.getRefinementModel() instanceof TPatternRefinementModel)) {
            throw new UnsupportedOperationException("The refinement candidate is not a PRM!");
        }

        // determine the elements that are staying
        TPatternRefinementModel prm = (TPatternRefinementModel) refinement.getRefinementModel();
        List<TEntityTemplate> stayingRefinementElements = prm.getStayMappings() == null ? new ArrayList<>() :
            prm.getStayMappings().stream()
                .map(TPrmMapping::getRefinementNode)
                .collect(Collectors.toList());

        // import the refinement structure
        Map<String, String> idMapping = BackendUtils.mergeTopologyTemplateAinTopologyTemplateB(
            refinement.getRefinementModel().getRefinementTopology(),
            topology,
            stayingRefinementElements
        );

        // only for UI: position the imported nodes next to the nodes to be refined
        Map<String, Map<String, Integer>> coordinates = calculateNewPositions(
            refinement.getDetectorGraph(),
            refinement.getGraphMapping(),
            refinement.getRefinementModel().getRefinementTopology()
        );
        refinement.getRefinementModel().getRefinementTopology().getNodeTemplates().stream()
            .filter(element -> !stayingRefinementElements.contains(element))
            .forEach(node -> {
                    Map<String, Integer> newCoordinates = coordinates.get(node.getId());
                    TNodeTemplate nodeTemplate = topology.getNodeTemplate(idMapping.get(node.getId()));
                    nodeTemplate.setX(newCoordinates.get("x").toString());
                    nodeTemplate.setY(newCoordinates.get("y").toString());
                }
            );

        // iterate over the detector nodes
        refinement.getDetectorGraph().vertexSet()
            .forEach(vertex -> {
                // get the matching node in the topology
                TNodeTemplate matchingNode = refinement.getGraphMapping().getVertexCorrespondence(vertex, false).getTemplate();

                this.redirectInternalRelations(prm, vertex.getTemplate(), matchingNode, topology);
                this.redirectExternalRelations(refinement, vertex.getTemplate(), matchingNode, topology, idMapping);

                this.applyPropertyMappings(refinement, vertex.getId(), matchingNode, topology, idMapping);

                if (!getStayMappingsOfCurrentElement(prm, vertex.getTemplate()).findFirst().isPresent()) {
                    topology.getNodeTemplateOrRelationshipTemplate()
                        .remove(matchingNode);
                } else if (vertex.getTemplate().getPolicies() != null && matchingNode.getPolicies() != null) {
                    vertex.getTemplate().getPolicies().getPolicy()
                        .forEach(detectorPolicy -> {
                            matchingNode.getPolicies().getPolicy()
                                .removeIf(matchingPolicy -> matchingPolicy.getPolicyType().equals(detectorPolicy.getPolicyType()));
                        });
                }
            });
        refinement.getDetectorGraph().edgeSet()
            .forEach(edge -> {
                TRelationshipTemplate relationshipTemplate = refinement.getGraphMapping().getEdgeCorrespondence(edge, false).getTemplate();

                this.applyPropertyMappings(refinement, edge.getId(), relationshipTemplate, topology, idMapping);

                if (!getStayMappingsOfCurrentElement(prm, edge.getTemplate()).findFirst().isPresent()) {
                    topology.getNodeTemplateOrRelationshipTemplate()
                        .remove(relationshipTemplate);
                }
            });
    }

    public void applyPropertyMappings(RefinementCandidate refinement, String detectorNodeId, TEntityTemplate matchingEntity,
                                      TTopologyTemplate topology, Map<String, String> idMapping) {
        List<AttributeMapping> propertyMappings = ((TPatternRefinementModel) refinement.getRefinementModel()).getAttributeMappings();
        if (Objects.nonNull(propertyMappings)) {
            propertyMappings.stream()
                .filter(mapping -> mapping.getDetectorNode().getId().equals(detectorNodeId))
                .forEach(mapping -> {
                    Map<String, String> sourceProperties = matchingEntity.getProperties().getKVProperties();
                    TEntityTemplate.Properties properties = topology.getNodeTemplateOrRelationshipTemplate()
                        .stream()
                        .filter(element -> element.getId().equals(idMapping.get(mapping.getRefinementNode().getId())))
                        .findFirst()
                        .get()
                        .getProperties();
                    Map<String, String> targetProperties = properties.getKVProperties();

                    if (Objects.nonNull(matchingEntity.getProperties()) && Objects.nonNull(sourceProperties) && !sourceProperties.isEmpty()
                        && Objects.nonNull(targetProperties)) {
                        if (mapping.getType() == TAttributeMappingType.ALL) {
                            sourceProperties.forEach(targetProperties::replace);
                        } else {
                            // TPrmPropertyMappingType.SELECTIVE
                            String sourceValue = sourceProperties.get(mapping.getDetectorProperty());
                            targetProperties.put(mapping.getRefinementProperty(), sourceValue);
                        }
                        // because of the dynamical generation of the KV properties, we must set them again to persist them...
                        properties.setKVProperties(targetProperties);
                    }
                });
        }
    }

    private boolean canRedirectExternalRelations(RefinementCandidate refinement, TNodeTemplate detectorNode, TNodeTemplate matchingNode, TTopologyTemplate topology) {
        return this.redirectExternalRelations(refinement, detectorNode, matchingNode, topology, null);
    }

    private boolean redirectExternalRelations(RefinementCandidate refinement, TNodeTemplate detectorNode, TNodeTemplate matchingNode,
                                              TTopologyTemplate topology, Map<String, String> idMapping) {
        // if the current element is a staying element, the external elements do not need to be redirected
        return this.getStayMappingsOfCurrentElement((TPatternRefinementModel) refinement.getRefinementModel(), detectorNode)
            .findFirst().isPresent()
            ||
            this.getExternalRelations(matchingNode, refinement, topology)
                .allMatch(relationship ->
                    refinement.getRefinementModel().getRelationMappings()
                        .stream()
                        // use anyMatch to reduce runtime
                        .anyMatch(relationMapping -> {
                            if (ModelUtilities.isOfType(relationMapping.getRelationType(), relationship.getType(), this.relationshipTypes)) {
                                if (relationMapping.getDirection() == TRelationDirection.INGOING
                                    && (Objects.isNull(relationMapping.getValidSourceOrTarget())
                                    || relationship.getSourceElement().getRef().getType().equals(relationMapping.getValidSourceOrTarget()))
                                ) {
                                    // change the source element to the new source defined in the relation mapping
                                    if (Objects.nonNull(idMapping)) {
                                        String id = idMapping.get(relationMapping.getRefinementNode().getId());
                                        relationship.setTargetNodeTemplate(topology.getNodeTemplate(id));
                                    }
                                    return true;
                                } else if (Objects.isNull(relationMapping.getValidSourceOrTarget())
                                    || relationship.getTargetElement().getRef().getType().equals(relationMapping.getValidSourceOrTarget())) {
                                    if (Objects.nonNull(idMapping)) {
                                        String id = idMapping.get(relationMapping.getRefinementNode().getId());
                                        relationship.setSourceNodeTemplate(topology.getNodeTemplate(id));
                                    }
                                    return true;
                                }
                            }
                            return false;
                        })
                );
    }

    private void redirectInternalRelations(TPatternRefinementModel prm, TNodeTemplate currentDetectorNode,
                                           TNodeTemplate matchingNodeInTopology, TTopologyTemplate topology) {
        if (prm.getStayMappings() != null) {
            topology.getRelationshipTemplates()
                .forEach(relationship ->
                    // get all relationships that are either the source or the target of the current node that is staying
                    this.getStayMappingsOfCurrentElement(prm, currentDetectorNode)
                        .forEach(staying -> {
                                String targetId = relationship.getTargetElement().getRef().getId();
                                String sourceId = relationship.getSourceElement().getRef().getId();

                                String idInRefinementStructure = staying.getRefinementNode().getId();

                                if (targetId.equals(idInRefinementStructure)) {
                                    LOGGER.debug("Redirecting target of {} to {}", relationship.getId(), matchingNodeInTopology.getId());
                                    relationship.getTargetElement().setRef(matchingNodeInTopology);
                                } else if (sourceId.equals(idInRefinementStructure)) {
                                    LOGGER.debug("Redirecting source of {} to {}", relationship.getId(), matchingNodeInTopology.getId());
                                    relationship.getSourceElement().setRef(matchingNodeInTopology);
                                }
                            }
                        )
                );
        }
    }

    public Stream<TRelationshipTemplate> getExternalRelations(TNodeTemplate matchingNode, RefinementCandidate candidate, TTopologyTemplate topology) {
        return topology.getRelationshipTemplates().stream()
            .filter(relationship ->
                // all relationships which have the matchingNode as source or target
                // -> \pi_1(rm_x) = \pi_2(sgm_i)
                matchingNode.getId().equals(relationship.getSourceElement().getRef().getId()) ||
                    matchingNode.getId().equals(relationship.getTargetElement().getRef().getId())
            ).filter(relationship -> {
                // ignore all relationships which are part of the sub-graph
                // \nexists sgm_y \in sgms : \pi_1(sgm_y) = r_j
                return candidate.getDetectorGraph().edgeSet()
                    .stream()
                    .noneMatch(toscaEdge -> {
                        ToscaEdge edgeCorrespondence = candidate.getGraphMapping().getEdgeCorrespondence(toscaEdge, false);
                        return edgeCorrespondence.getTemplate().equals(relationship);
                    });
            });
    }

    @Override
    public boolean getLoopCondition(TTopologyTemplate topology) {
        return SubstitutionUtils.containsPatterns(topology.getNodeTemplates(), this.nodeTypes);
    }

    @Override
    public IToscaMatcher getMatcher(TRefinementModel prm) {
        return new ToscaPrmPropertyMatcher(prm.getDetector().getNodeTemplateOrRelationshipTemplate(), repository.getNamespaceManager());
    }

    private Map<String, Map<String, Integer>> calculateNewPositions(ToscaGraph detectorGraph, GraphMapping<ToscaNode, ToscaEdge> mapping, TTopologyTemplate refinementStructure) {
        HashMap<String, Map<String, Integer>> coordinates = new HashMap<>();
        int[] topLeftOriginal = {-1, -1};
        int[] topLeftReplacement = {-1, -1};

        detectorGraph.vertexSet().forEach(toscaNode -> {
            ToscaNode node = mapping.getVertexCorrespondence(toscaNode, false);
            getTopLeft(node.getTemplate().getX(), node.getTemplate().getY(), topLeftOriginal);
        });
        refinementStructure.getNodeTemplates().forEach(
            tNodeTemplate -> getTopLeft(tNodeTemplate.getX(), tNodeTemplate.getY(), topLeftReplacement)
        );

        refinementStructure.getNodeTemplates().forEach(nodeTemplate -> {
            int x = Integer.parseInt(nodeTemplate.getX());
            int y = Integer.parseInt(nodeTemplate.getY());

            HashMap<String, Integer> newCoordinates = new HashMap<>();
            newCoordinates.put("x", (x - topLeftReplacement[0]) + topLeftOriginal[0]);
            newCoordinates.put("y", (y - topLeftReplacement[1]) + topLeftOriginal[1]);

            coordinates.put(nodeTemplate.getId(), newCoordinates);
        });

        return coordinates;
    }

    private void getTopLeft(String stringX, String stringY, int[] topLeft) {
        int x = Integer.parseInt(stringX);
        int y = Integer.parseInt(stringY);

        if (topLeft[0] > x || topLeft[0] == -1) {
            topLeft[0] = x;
        }
        if (topLeft[1] > y || topLeft[1] == -1) {
            topLeft[1] = y;
        }
    }

    private Stream<TStayMapping> getStayMappingsOfCurrentElement(TPatternRefinementModel prm, TEntityTemplate currentDetectorNode) {
        return prm.getStayMappings() == null ? Stream.of() :
            prm.getStayMappings().stream()
                .filter(stayMapping -> stayMapping.getDetectorNode().getId().equals(currentDetectorNode.getId()));
    }
}
