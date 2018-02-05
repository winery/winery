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

package org.eclipse.winery.bpmn2bpel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a sub path of a gateway node
 * 
 * it contains two properties, id is the first following node's id; nodeList is the nodes of the
 * branch order by flow sequence.
 *
 */
public class GatewayBranch {

	private String id;
	private List<Node> nodeList = new ArrayList<Node>();

	public GatewayBranch(String id) {
		super();
		this.id = id;
	}

	public List<Node> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<Node> nodeList) {
		this.nodeList = nodeList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
