/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.instance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaPropertyMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jgrapht.GraphMapping;

public abstract class InstanceModelRefinementPlugin {

    protected Set<String> requiredInputs;
    protected RefineableSubgraph matchToBeRefined;

    private final ToscaIsomorphismMatcher isomorphismMatcher;
    private final String id;
    private ArrayList<RefineableSubgraph> subGraphs;

    public InstanceModelRefinementPlugin(String id) {
        this.id = id;
        this.isomorphismMatcher = new ToscaIsomorphismMatcher();
    }

    /**
     * Apply the changes for the nodes identified by the matchToBeRefined.
     *
     * @param template the topology template to be refined
     * @return the refined topology template
     */
    public abstract TTopologyTemplate apply(TTopologyTemplate template);

    public abstract Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced);

    public boolean isApplicable(TTopologyTemplate template, ToscaGraph topologyGraph) {
        TTopologyTemplate detector = getDetectorGraph();
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(detector);
        IToscaMatcher matcher = new ToscaPropertyMatcher();

        Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = this.isomorphismMatcher
            .findMatches(detectorGraph, topologyGraph, matcher);

        this.subGraphs = new ArrayList<>();
        int[] ids = {0};
        matches.forEachRemaining(match -> {
            ArrayList<String> nodeIdsToBeReplaced = new ArrayList<>();
            detectorGraph.vertexSet().forEach(toscaNode ->
                nodeIdsToBeReplaced.add(match.getVertexCorrespondence(toscaNode, false).getTemplate().getId())
            );

            Set<String> additionalInputs = this.determineAdditionalInputs(template, nodeIdsToBeReplaced);

            this.subGraphs.add(new RefineableSubgraph(match, detectorGraph, nodeIdsToBeReplaced, additionalInputs, ids[0]++));
        });

        return !this.subGraphs.isEmpty();
    }

    protected abstract TTopologyTemplate getDetectorGraph();

    public String getId() {
        return id;
    }

    public void setUserInputs(Map<String, String> userInputs) {
    }

    public Set<String> getRequiredInputs() {
        return this.requiredInputs;
    }

    public void setSelectedMatchId(int matchId) {
        if (subGraphs != null) {
            this.matchToBeRefined = subGraphs.get(matchId);
        }
    }

    public static class RefineableSubgraph {

        int id;
        ArrayList<String> nodeIdsToBeReplaced;
        Set<String> additionalInputs;

        @JsonIgnore
        private final GraphMapping<ToscaNode, ToscaEdge> graphMapping;
        @JsonIgnore
        private final ToscaGraph detectorGraph;

        public RefineableSubgraph(GraphMapping<ToscaNode, ToscaEdge> graphMapping, ToscaGraph detectorGraph,
                                  ArrayList<String> nodeIdsToBeReplaced, Set<String> additionalInputs, int id) {
            this.graphMapping = graphMapping;
            this.detectorGraph = detectorGraph;
            this.nodeIdsToBeReplaced = nodeIdsToBeReplaced;
            this.additionalInputs = additionalInputs;
            this.id = id;
        }
    }
}
