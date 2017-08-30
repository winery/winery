/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Karoline Saatkamp - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.rest.resources._support.dataadapter;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.Namespaces;

//@XmlType
public class Injection {
	@XmlElement(name = "NodeID")
	protected String nodeID;
	@XmlElement(namespace = Namespaces.TOSCA_NAMESPACE, name = "TopologyTemplate")
	protected TTopologyTemplate injectedTopologyFragment;

	public Injection() {
	}

	public Injection(String hostedNodeID, TTopologyTemplate injectedTopologyFragment) {
		this.nodeID = hostedNodeID;
		this.injectedTopologyFragment = injectedTopologyFragment;
	}
}

