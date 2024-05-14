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

package org.eclipse.winery.model.adaptation.substitution.refinement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jdt.annotation.NonNull;
import org.jgrapht.GraphMapping;

public class RefinementCandidate {

    private final OTRefinementModel refinementModel;
    @JsonIgnore
    private final GraphMapping<ToscaNode, ToscaEdge> graphMapping;
    @JsonIgnore
    private final ToscaGraph detectorGraph;
    private final int id;

    private final Map<String, List<String>> warnings = new HashMap<>();

    public RefinementCandidate(OTRefinementModel refinementModel, GraphMapping<ToscaNode, ToscaEdge> graphMapping,
                               ToscaGraph detectorGraph, int id) {
        this(refinementModel, graphMapping, detectorGraph, id, new HashMap<>());
    }

    public RefinementCandidate(OTRefinementModel refinementModel, GraphMapping<ToscaNode, ToscaEdge> graphMapping,
                               ToscaGraph detectorGraph, int id, Map<String, List<String>> warnings) {
        this.refinementModel = Objects.requireNonNull(refinementModel);
        this.graphMapping = Objects.requireNonNull(graphMapping);
        this.detectorGraph = Objects.requireNonNull(detectorGraph);
        this.id = id;
        this.getNodeIdsToBeReplaced().forEach(nodeId -> {
            if (warnings.containsKey(nodeId)) {
                this.warnings.put(nodeId, warnings.get(nodeId));
            }
        });
        this.getRelIdsToBeReplaced().forEach(relationId -> {
            if (warnings.containsKey(relationId)) {
                this.warnings.put(relationId, warnings.get(relationId));
            }
        });
    }

    @NonNull
    public OTRefinementModel getRefinementModel() {
        return refinementModel;
    }

    public GraphMapping<ToscaNode, ToscaEdge> getGraphMapping() {
        return graphMapping;
    }

    @NonNull
    public ToscaGraph getDetectorGraph() {
        return detectorGraph;
    }

    public int getId() {
        return id;
    }

    public Map<String, List<String>> getWarnings() {
        return warnings;
    }

    public ArrayList<String> getNodeIdsToBeReplaced() {
        ArrayList<String> ids = new ArrayList<>();

        this.detectorGraph.vertexSet().forEach(toscaNode ->
            ids.add(graphMapping.getVertexCorrespondence(toscaNode, false).getTemplate().getId())
        );

        return ids;
    }

    public ArrayList<String> getRelIdsToBeReplaced() {
        ArrayList<String> ids = new ArrayList<>();

        this.detectorGraph.edgeSet().forEach(toscaEdge ->
            ids.add(graphMapping.getEdgeCorrespondence(toscaEdge, false).getTemplate().getId())
        );

        return ids;
    }
}
