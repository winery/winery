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
package org.eclipse.winery.compliance.model;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;

public class TOSCAEdge extends TOSCAEntity {

	private TOSCANode targetNode;
	private TOSCANode sourceNode;
	private TRelationshipTemplate template;

	public TOSCAEdge(TOSCANode sourceNode, TOSCANode targetNode) {
		super();
		this.targetNode = targetNode;
		this.sourceNode = sourceNode;
	}

	public List<TRelationshipType> getRelationshipTypes() {
		return getTypes().stream().map(tEntityType -> (TRelationshipType) tEntityType).collect(Collectors.toList());
	}

	public TOSCANode getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(TOSCANode targetNode) {
		this.targetNode = targetNode;
	}

	public TOSCANode getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(TOSCANode sourceNode) {
		this.sourceNode = sourceNode;
	}

	public TRelationshipTemplate getTemplate() {
		return template;
	}

	public void setTemplate(TRelationshipTemplate template) {
		this.template = template;
		this.setId(template.getId());
	}
}
