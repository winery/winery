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

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;

public class TOSCANode extends TOSCAEntity {

	private TNodeTemplate nodeTemplate;

	public List<TNodeType> getNodeTypes() {
		return getTypes().stream().map(TNodeType.class::cast).collect(Collectors.toList());
	}

	public TNodeTemplate getNodeTemplate() {
		return nodeTemplate;
	}

	public void setNodeTemplate(TNodeTemplate nodeTemplate) {
		this.nodeTemplate = nodeTemplate;
		this.setId(nodeTemplate.getId());
	}
}

	

	
	


	
