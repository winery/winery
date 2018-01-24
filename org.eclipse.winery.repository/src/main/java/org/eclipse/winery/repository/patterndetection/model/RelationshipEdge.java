/**
 * Copyright (c) 2017 Marvin Wohlfarth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.patterndetection.model;

import org.jgrapht.graph.DefaultEdge;

public class RelationshipEdge<V> extends DefaultEdge {
	private V v1;
	private V v2;
	private String label;

	public RelationshipEdge(V v1, V v2, String label) {
		this.v1 = v1;
		this.v2 = v2;
		this.label = label;
	}

	public V getV1() {
		return v1;
	}

	public V getV2() {
		return v2;
	}

	public String toString() {
		return label;
	}
}
