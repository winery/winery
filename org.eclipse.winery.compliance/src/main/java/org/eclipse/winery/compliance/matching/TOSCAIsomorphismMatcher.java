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
package org.eclipse.winery.compliance.matching;

import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.winery.compliance.model.TOSCAEdge;
import org.eclipse.winery.compliance.model.TOSCAGraph;
import org.eclipse.winery.compliance.model.TOSCANode;

import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;

public class TOSCAIsomorphismMatcher {

	public Iterator<GraphMapping<TOSCANode, TOSCAEdge>> findMatches(TOSCAGraph queryGraph, TOSCAGraph toSearchIn, ITOSCAMatcher comp) {

		// NEVER CHANGE THE ORDERING OF THE PARAMETERS. THE SWITCH (RIGHT, LEFT) IS DELIBERATE!
		Comparator<TOSCANode> vertexComparator = (TOSCANode left, TOSCANode right) -> new TOSCAComparatorProxy(comp).compareTypeCompatible(right, left);
		Comparator<TOSCAEdge> edgeComparator = (TOSCAEdge left, TOSCAEdge right) -> new TOSCAComparatorProxy(comp).compareTypeCompatible(right, left);

		VF2SubgraphIsomorphismInspector<TOSCANode, TOSCAEdge> inspector = new VF2SubgraphIsomorphismInspector<>(toSearchIn, queryGraph, vertexComparator, edgeComparator);
		return inspector.getMappings();
	}
}
