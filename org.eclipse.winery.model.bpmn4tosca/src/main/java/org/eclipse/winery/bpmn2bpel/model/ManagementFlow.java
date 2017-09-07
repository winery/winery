/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel.model;

import java.util.Collection;
import java.util.Iterator;

import org.jgrapht.graph.SimpleDirectedGraph;

@SuppressWarnings("serial")
public class ManagementFlow extends SimpleDirectedGraph<Node, Link> {

	public ManagementFlow() {
		super(Link.class);
	}

//	public void addNode(Node node) {
//		addVertex(node);
//	}
//
//	public Set<Node> getNodes() {
//		return vertexSet();
//	}
//
//	public void addLink(Node src, Node target) {
//		addEdge(src, target);
//	}

	/**
	 * @param id
	 * @return The node with the given id or <code>null</code> if a node with this id cannot be found
	 */
	public Node getNode(String id) {
		return findNodeById(id);
	}

	protected Node findNodeById(String id) {
		Iterator<Node> iter = vertexSet().iterator();
		while (iter.hasNext()) {
			Node node = (Node) iter.next();
			if (node.getId().equals(id))
				return node;
		}
		return null;
	}

	@Override
	protected String toStringFromSets(Collection<? extends Node> vertexSet, Collection<? extends Link> edgeSet,
			boolean directed) {
		// TODO Auto-generated method stub
		return super.toStringFromSets(vertexSet, edgeSet, directed);
	}

}
