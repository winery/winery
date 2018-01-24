/**
 * Copyright (c) 2017 Marvin Wohlfarth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.patterndetection.model;

import org.jgrapht.DirectedGraph;

public class PatternPosition {

	private String patternName;
	private DirectedGraph<TNodeTemplateExtended, RelationshipEdge> nodesOfOriginGraph;

	public PatternPosition(String patternName, DirectedGraph<TNodeTemplateExtended, RelationshipEdge> nodesOfOriginGraph) {
		this.patternName = patternName;
		this.nodesOfOriginGraph = nodesOfOriginGraph;
	}

	public String getPatternName() {
		return patternName;
	}

	public DirectedGraph<TNodeTemplateExtended, RelationshipEdge> getNodesOfOriginGraph() {
		return nodesOfOriginGraph;
	}
}
