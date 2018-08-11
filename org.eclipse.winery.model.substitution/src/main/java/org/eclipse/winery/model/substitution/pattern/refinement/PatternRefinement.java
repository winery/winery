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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.winery.common.ids.definitions.PatternRefinementModelId;
import org.eclipse.winery.model.substitution.AbstractSubstitution;
import org.eclipse.winery.model.substitution.SubstitutionUtils;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TRelationDirection;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaTypeMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import com.google.common.collect.Iterators;
import org.jgrapht.GraphMapping;

public class PatternRefinement extends AbstractSubstitution {

    private List<TPatternRefinementModel> patternRefinementModels;

    public PatternRefinement() {
        this.patternRefinementModels = this.repository.getAllDefinitionsChildIds(PatternRefinementModelId.class)
            .stream()
            .map(repository::getElement)
            .collect(Collectors.toList());
    }

    public void refineTopology(TTopologyTemplate topology) {
        ToscaIsomorphismMatcher isomorphismMatcher = new ToscaIsomorphismMatcher();

        while (SubstitutionUtils.containsPatterns(topology.getNodeTemplates(), this.nodeTypes)) {
            ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);

            List<PatternRefinementCandidate> candidates = new ArrayList<>();
            this.patternRefinementModels
                .forEach(prm -> {
                    ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(prm.getDetector());
                    Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = isomorphismMatcher.findMatches(detectorGraph, topologyGraph, new ToscaTypeMatcher());

                    if (matches.hasNext()) {
                        List<GraphMapping<ToscaNode, ToscaEdge>> graphMappings = new ArrayList<>();
                        Iterators.addAll(graphMappings, matches);

                        PatternRefinementCandidate candidate = new PatternRefinementCandidate(prm, graphMappings, detectorGraph);

                        if (isApplicable(candidate, topology)) {
                            candidates.add(candidate);
                        }
                    }
                });

            PatternRefinementCandidate refinement = choosePatternRefinement(candidates);

            applyRefinement(refinement, topology);
        }
    }

    private PatternRefinementCandidate choosePatternRefinement(List<PatternRefinementCandidate> candidates) {
        return candidates.get(0);
    }

    public void applyRefinement(PatternRefinementCandidate refinement, TTopologyTemplate topology) {

    }

    public boolean isApplicable(PatternRefinementCandidate candidate, TTopologyTemplate topology) {
        return candidate.getGraphMapping()
            .stream()
            .allMatch(mapping -> {
                TNodeTemplate matchingNode = mapping.getVertexCorrespondence(candidate.getDetectorGraph().getReferenceNode(), false).getNodeTemplate();
                return topology.getRelationshipTemplates().stream()
                    .filter(relationship ->
                        // all relationships which have the matchingNode as source or target
                        // -> \pi_1(rm_x) = \pi_2(sgm_i)
                        matchingNode.getId().equals(relationship.getSourceElement().getRef().getId()) ||
                            matchingNode.getId().equals(relationship.getTargetElement().getRef().getId())
                    ).filter(relationship -> {
                        // ignore all relationships which are part of the sub-graph
                        // \nexists sgm_y \in sgms : \pi_1(sgm_y) = r_j
                        return candidate.getGraphMapping().stream()
                            .noneMatch(edgeMapping -> {
                                ToscaEdge edgeCorrespondence = edgeMapping.getEdgeCorrespondence(candidate.getDetectorGraph().getReferenceEdge(), false);
                                return edgeCorrespondence.getTemplate().equals(relationship);
                            });
                    }).allMatch(relationship ->
                        // do the actual applicable check: can the relationship be mapped? 
                        candidate.getPatternRefinementModel().getRelationshipMappings().getRelationMapping().stream()
                            .anyMatch(relationMapping -> {
                                // TODO: check if any supertype of the relationship matches
                                if (relationMapping.getDirection() == TRelationDirection.INGOING) {
                                    return relationship.getType().equals(relationMapping.getRelationType())
                                        && relationship.getSourceElement().getRef().getType().equals(relationMapping.getValidSourceOrTarget());
                                } else {
                                    return relationship.getType().equals(relationMapping.getRelationType())
                                        && relationship.getTargetElement().getRef().getType().equals(relationMapping.getValidSourceOrTarget());
                                }
                            })
                    );
            });
    }
}
