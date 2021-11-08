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
 ********************************************************************************/
package org.eclipse.winery.topologygraph.model;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.graph.SimpleDirectedGraph;

public class ToscaGraph extends SimpleDirectedGraph<ToscaNode, ToscaEdge> {

    private static final long serialVersionUID = 1L;

    public ToscaGraph() {
        super(ToscaEdge.class);
    }

    public ToscaNode getNode(String id) {
        return vertexSet().stream().filter(n -> StringUtils.equals(id, n.getId())).findFirst().orElse(null);
    }

    public ToscaNode getReferenceNode() {
        return vertexSet().stream().findAny().orElse(null);
    }

    public Set<ToscaEntity> vertexAndEdgeSet() {
        return Stream.concat(
            vertexSet().stream().map(v -> (ToscaEntity) v),
            edgeSet().stream().map(e -> (ToscaEntity) e)
        ).collect(Collectors.toSet());
    }

    public Optional<ToscaEntity> getVertexOrEdge(String id) {
        return vertexAndEdgeSet().stream()
            .filter(e -> e.getId().equals(id))
            .findFirst();
    }
}
