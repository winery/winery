/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *     ZTE - support of more gateways
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {

	private String id;
	private String name;
	private String type;
	private List<String> connections = new ArrayList<>();

	public List<String> getConnections() {
		
		return connections;
	}

	public void setConnections(List<String> connections) {
		this.connections = connections;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
