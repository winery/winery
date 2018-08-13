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

import java.util.List;
import java.util.Objects;

import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.eclipse.jdt.annotation.NonNull;
import org.jgrapht.GraphMapping;

public class PatternRefinementCandidate {

    private final TPatternRefinementModel patternRefinementModel;
    private final List<GraphMapping<ToscaNode, ToscaEdge>> graphMapping;
    private final ToscaGraph detectorGraph;

    public PatternRefinementCandidate(TPatternRefinementModel patternRefinementModel, List<GraphMapping<ToscaNode, ToscaEdge>> graphMapping, ToscaGraph detectorGraph) {
        this.patternRefinementModel = Objects.requireNonNull(patternRefinementModel);
        this.graphMapping = Objects.requireNonNull(graphMapping);
        this.detectorGraph = Objects.requireNonNull(detectorGraph);
    }

    @NonNull
    public TPatternRefinementModel getPatternRefinementModel() {
        return patternRefinementModel;
    }

    @NonNull
    public List<GraphMapping<ToscaNode, ToscaEdge>> getGraphMapping() {
        return graphMapping;
    }

    @NonNull
    public ToscaGraph getDetectorGraph() {
        return detectorGraph;
    }
}
