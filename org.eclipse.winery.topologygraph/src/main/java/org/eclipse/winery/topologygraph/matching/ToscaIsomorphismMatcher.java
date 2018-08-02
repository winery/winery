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
package org.eclipse.winery.topologygraph.matching;

import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;

public class ToscaIsomorphismMatcher {

    public Iterator<GraphMapping<ToscaNode, ToscaEdge>> findMatches(ToscaGraph queryGraph, ToscaGraph toSearchIn, IToscaMatcher comp) {

        // NEVER CHANGE THE ORDERING OF THE PARAMETERS. THE SWITCH (RIGHT, LEFT) IS DELIBERATE!
        Comparator<ToscaNode> vertexComparator = (ToscaNode left, ToscaNode right) -> new ToscaComparatorProxy(comp).compareTypeCompatible(right, left);
        Comparator<ToscaEdge> edgeComparator = (ToscaEdge left, ToscaEdge right) -> new ToscaComparatorProxy(comp).compareTypeCompatible(right, left);

        VF2SubgraphIsomorphismInspector<ToscaNode, ToscaEdge> inspector = new VF2SubgraphIsomorphismInspector<>(toSearchIn, queryGraph, vertexComparator, edgeComparator);
        return inspector.getMappings();
    }
}
