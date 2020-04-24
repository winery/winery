/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.patterndetection.model;

import org.jgrapht.graph.SimpleDirectedGraph;

public class PatternPosition {

    private String patternName;
    private SimpleDirectedGraph<TNodeTemplateExtended, RelationshipEdge> nodesOfOriginGraph;

    public PatternPosition(String patternName, SimpleDirectedGraph<TNodeTemplateExtended, RelationshipEdge> nodesOfOriginGraph) {
        this.patternName = patternName;
        this.nodesOfOriginGraph = nodesOfOriginGraph;
    }

    public String getPatternName() {
        return patternName;
    }

    public SimpleDirectedGraph<TNodeTemplateExtended, RelationshipEdge> getNodesOfOriginGraph() {
        return nodesOfOriginGraph;
    }
}
