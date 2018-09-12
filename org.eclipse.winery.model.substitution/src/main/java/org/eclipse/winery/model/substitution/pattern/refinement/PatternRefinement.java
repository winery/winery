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
 *******************************************************************************/
package org.eclipse.winery.model.substitution.pattern.refinement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.PatternRefinementModelId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.substitution.AbstractSubstitution;
import org.eclipse.winery.model.substitution.SubstitutionUtils;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TRelationDirection;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaTypeMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternRefinement extends AbstractSubstitution {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatternRefinement.class);

    private List<TPatternRefinementModel> patternRefinementModels;
    private PatternRefinementChooser refinementChooser;
    private ServiceTemplateId refinementServiceTemplateId;

    public PatternRefinement(PatternRefinementChooser refinementChooser) {
        this.refinementChooser = refinementChooser;
        this.patternRefinementModels = this.repository.getAllDefinitionsChildIds(PatternRefinementModelId.class)
            .stream()
            .map(repository::getElement)
            .collect(Collectors.toList());
        super.versionAppendix = "refined";
    }

    public PatternRefinement() {
        this(new DefaultPatternRefinementChooser());
    }

    public ServiceTemplateId refineServiceTemplate(ServiceTemplateId id) {
        refinementServiceTemplateId = this.getSubstitutionServiceTemplateId(id);
        TServiceTemplate element = this.repository.getElement(refinementServiceTemplateId);

        this.refineTopology(element.getTopologyTemplate());
        try {
            this.repository.setElement(refinementServiceTemplateId, element);
        } catch (IOException e) {
            LOGGER.error("Error while saving refined topology", e);
        }

        return refinementServiceTemplateId;
    }

    public void refineTopology(TTopologyTemplate topology) {
        ToscaIsomorphismMatcher isomorphismMatcher = new ToscaIsomorphismMatcher();
        int id[] = new int[1];

        while (SubstitutionUtils.containsPatterns(topology.getNodeTemplates(), this.nodeTypes)) {
            ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);

            List<PatternRefinementCandidate> candidates = new ArrayList<>();
            this.patternRefinementModels
                .forEach(prm -> {
                    ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(prm.getDetector());
                    Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = isomorphismMatcher.findMatches(detectorGraph, topologyGraph, new ToscaTypeMatcher());

                    matches.forEachRemaining(mapping -> {
                        PatternRefinementCandidate candidate = new PatternRefinementCandidate(prm, mapping, detectorGraph, id[0]++);

                        if (isApplicable(candidate, topology)) {
                            candidates.add(candidate);
                        }
                    });
                });

            if (candidates.size() == 0) {
                break;
            }

            PatternRefinementCandidate refinement = this.refinementChooser.choosePatternRefinement(candidates, this.refinementServiceTemplateId, topology);

            if (Objects.isNull(refinement)) {
                break;
            }

            applyRefinement(refinement, topology);
        }
    }

    public void applyRefinement(PatternRefinementCandidate refinement, TTopologyTemplate topology) {
        Map<String, String> idMapping = BackendUtils.mergeTopologyTemplateAinTopologyTemplateB(
            refinement.getPatternRefinementModel().getRefinementStructure(),
            topology
        );

        Map<String, Map<String, Integer>> coordinates = calculateNewPositions(
            refinement.getDetectorGraph(),
            refinement.getGraphMapping(),
            refinement.getPatternRefinementModel().getRefinementStructure()
        );
        refinement.getPatternRefinementModel().getRefinementStructure().getNodeTemplates()
            .forEach(node -> {
                    Map<String, Integer> newCoordinates = coordinates.get(node.getId());
                    TNodeTemplate nodeTemplate = topology.getNodeTemplate(idMapping.get(node.getId()));
                    nodeTemplate.setX(newCoordinates.get("x").toString());
                    nodeTemplate.setY(newCoordinates.get("y").toString());
                }
            );

        refinement.getDetectorGraph().vertexSet()
            .forEach(vertex -> {
                TNodeTemplate matchingNode = refinement.getGraphMapping().getVertexCorrespondence(vertex, false).getNodeTemplate();
                this.getExternalRelations(matchingNode, refinement, topology)
                    .forEach(relationship -> {
                        refinement.getPatternRefinementModel().getRelationMappings().getRelationMapping()
                            .stream()
                            // use anyMatch to reduce runtime
                            .anyMatch(relationMapping -> {
                                if (isOfType(relationMapping.getRelationType(), relationship.getType(), this.relationshipTypes)) {
                                    if (relationMapping.getDirection() == TRelationDirection.INGOING
                                        && (Objects.isNull(relationMapping.getValidSourceOrTarget())
                                        || relationship.getSourceElement().getRef().getType().equals(relationMapping.getValidSourceOrTarget()))
                                    ) {
                                        // change the source element to the new source defined in the relation mapping
                                        String id = idMapping.get(relationMapping.getRefinementNode().getId());
                                        relationship.setTargetNodeTemplate(topology.getNodeTemplate(id));
                                        return true;
                                    } else if (Objects.isNull(relationMapping.getValidSourceOrTarget())
                                        || relationship.getTargetElement().getRef().getType().equals(relationMapping.getValidSourceOrTarget())) {
                                        String id = idMapping.get(relationMapping.getRefinementNode().getId());
                                        relationship.setSourceNodeTemplate(topology.getNodeTemplate(id));
                                        return true;
                                    }
                                }
                                return false;
                            });
                    });
                topology.getNodeTemplateOrRelationshipTemplate()
                    .remove(matchingNode);
            });
        refinement.getDetectorGraph().edgeSet()
            .forEach(edge -> {
                TRelationshipTemplate tRelationshipTemplate = refinement.getGraphMapping().getEdgeCorrespondence(edge, false).getTemplate();
                topology.getNodeTemplateOrRelationshipTemplate()
                    .remove(tRelationshipTemplate);
            });
    }

    private Map<String, Map<String, Integer>> calculateNewPositions(ToscaGraph detectorGraph, GraphMapping<ToscaNode, ToscaEdge> mapping, TTopologyTemplate refinementStructure) {
        HashMap<String, Map<String, Integer>> coordinates = new HashMap<>();
        int[] topLeftOriginal = {-1, -1};
        int[] topLeftReplacement = {-1, -1};

        detectorGraph.vertexSet().forEach(toscaNode -> {
            ToscaNode node = mapping.getVertexCorrespondence(toscaNode, false);
            getTopLeft(node.getNodeTemplate().getX(), node.getNodeTemplate().getY(), topLeftOriginal);
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

    public boolean isApplicable(PatternRefinementCandidate candidate, TTopologyTemplate topology) {
        return candidate.getDetectorGraph().vertexSet()
            .stream()
            .allMatch(vertex -> {
                TNodeTemplate matchingNode = candidate.getGraphMapping().getVertexCorrespondence(vertex, false).getNodeTemplate();
                return this.getExternalRelations(matchingNode, candidate, topology)
                    .allMatch(relationship ->
                        // do the actual applicable check: can the relationship be mapped?
                        Objects.nonNull(candidate.getPatternRefinementModel().getRelationMappings()) &&
                            candidate.getPatternRefinementModel().getRelationMappings().getRelationMapping()
                                .stream()
                                .anyMatch(relationMapping -> {
                                    if (isOfType(relationMapping.getRelationType(), relationship.getType(), this.relationshipTypes)) {
                                        if (relationMapping.getDirection() == TRelationDirection.INGOING) {
                                            return Objects.isNull(relationMapping.getValidSourceOrTarget())
                                                || relationship.getSourceElement().getRef().getType().equals(relationMapping.getValidSourceOrTarget());
                                        } else {
                                            return Objects.isNull(relationMapping.getValidSourceOrTarget())
                                                || relationship.getTargetElement().getRef().getType().equals(relationMapping.getValidSourceOrTarget());
                                        }
                                    }
                                    return false;
                                })
                    );
            });
    }

    public Stream<TRelationshipTemplate> getExternalRelations(TNodeTemplate matchingNode, PatternRefinementCandidate candidate, TTopologyTemplate topology) {
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

    public static boolean isOfType(QName requiredType, QName givenType, Map<QName, ? extends TEntityType> elements) {
        if (!givenType.equals(requiredType)) {
            TEntityType entityType = elements.get(givenType);
            if (Objects.isNull(entityType) || Objects.isNull(entityType.getDerivedFrom())) {
                return false;
            } else {
                return isOfType(requiredType, entityType.getDerivedFrom().getTypeAsQName(), elements);
            }
        }
        return true;
    }
}
