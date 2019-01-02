/*******************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.substitution.refinement;

import java.util.ArrayList;
import java.util.Objects;

import org.eclipse.winery.model.tosca.TRefinementModel;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jdt.annotation.NonNull;
import org.jgrapht.GraphMapping;

public class RefinementCandidate {

    private final TRefinementModel refinementModel;
    @JsonIgnore
    private final GraphMapping<ToscaNode, ToscaEdge> graphMapping;
    @JsonIgnore
    private final ToscaGraph detectorGraph;
    private final int id;

    public RefinementCandidate(TRefinementModel refinementModel, GraphMapping<ToscaNode, ToscaEdge> graphMapping,
                               ToscaGraph detectorGraph, int id) {
        this.refinementModel = Objects.requireNonNull(refinementModel);
        this.graphMapping = Objects.requireNonNull(graphMapping);
        this.detectorGraph = Objects.requireNonNull(detectorGraph);
        this.id = id;
    }

    @NonNull
    public TRefinementModel getRefinementModel() {
        return refinementModel;
    }

    public GraphMapping<ToscaNode, ToscaEdge> getGraphMapping() {
        return graphMapping;
    }

    @NonNull
    public ToscaGraph getDetectorGraph() {
        return detectorGraph;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public ArrayList<String> getNodeIdsToBeReplaced() {
        ArrayList<String> ids = new ArrayList<>();

        this.detectorGraph.vertexSet().forEach(toscaNode ->
            ids.add(graphMapping.getVertexCorrespondence(toscaNode, false).getTemplate().getId())
        );

        return ids;
    }
}
