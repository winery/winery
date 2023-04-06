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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.tosca.DiscoveryPluginDescriptor;
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
     * @return the ids of the nodes that have been altered by the plugin
     */
    public abstract Set<String> apply(TTopologyTemplate template);

    public abstract Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced);

    public boolean isApplicable(TTopologyTemplate template, ToscaGraph topologyGraph, List<DiscoveryPluginDescriptor> discoveryPluginDescriptors) {
        List<TTopologyTemplate> detectors = getDetectorGraphs();
        this.subGraphs = new ArrayList<>();
        IToscaMatcher matcher = getToscaMatcher();

        detectors.forEach(detector -> {
            ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(detector);

            Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = this.isomorphismMatcher
                .findMatches(detectorGraph, topologyGraph, matcher);

            int[] ids = {0};
            matches.forEachRemaining(match -> {
                ArrayList<String> nodeIdsToBeReplaced = new ArrayList<>();
                detectorGraph.vertexSet().forEach(toscaNode ->
                    nodeIdsToBeReplaced.add(match.getVertexCorrespondence(toscaNode, false).getTemplate().getId())
                );

                if (!nodeIdsToBeReplaced.isEmpty()) {
                    // We want to avoid multiple executions of a plugin for the same nodes
                    if (nodeIdsToBeReplaced.stream().noneMatch(
                        nodeToBeRefined -> discoveryPluginDescriptors.stream()
                            .filter(executedPlugin -> executedPlugin.getId().equals(this.id))
                            .flatMap(discoveryPluginDescriptor -> discoveryPluginDescriptor.getDiscoveredIds().stream())
                            .anyMatch(node -> node.equals(nodeToBeRefined)))
                    ) {
                        Set<String> additionalInputs = this.determineAdditionalInputs(template, nodeIdsToBeReplaced);

                        this.subGraphs.add(new RefineableSubgraph(match, detectorGraph, nodeIdsToBeReplaced, additionalInputs, ids[0]++));
                    }
                }
            });
        });

        return !this.subGraphs.isEmpty();
    }

    @JsonIgnore
    protected abstract List<TTopologyTemplate> getDetectorGraphs();

    /**
     * <p>Returns the {@code IToscaMatcher} to use for finding refineable sub graphs.</p>
     * <p>
     * There is a default Matcher provided, however every plugin implmentation may choose a different matcher, according
     * to their needs. An alternative would be to use a matcher that receives the plugin as an argument and calls the
     * plugin where needed. However the approach of providing a custom matcher allows for more flexibility.
     * </p>
     */
    @JsonIgnore
    protected IToscaMatcher getToscaMatcher() {
        return new ToscaPropertyMatcher(false, true);
    }

    public String getId() {
        return id;
    }

    public void setUserInputs(Map<String, String> userInputs, TTopologyTemplate template, int matchId) {
        RefineableSubgraph refineableSubgraph = this.subGraphs.get(matchId);
        InstanceModelUtils.setUserInputs(userInputs, template, refineableSubgraph.nodeIdsToBeReplaced);
    }

    public void setSelectedMatchId(int matchId) {
        if (subGraphs != null) {
            this.matchToBeRefined = subGraphs.get(matchId);
        }
    }

    public ArrayList<RefineableSubgraph> getSubGraphs() {
        return subGraphs;
    }

    public static class RefineableSubgraph {

        public int id;
        public List<String> nodeIdsToBeReplaced;
        public Set<String> additionalInputs;

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
