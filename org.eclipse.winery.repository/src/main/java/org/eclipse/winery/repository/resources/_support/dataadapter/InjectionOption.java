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

package org.eclipse.winery.repository.resources._support.dataadapter;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.eclipse.winery.common.constants.Namespaces;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

public class InjectionOption {
	@XmlElement(name = "NodeID")
	protected String nodeID;

	@XmlElementWrapper (name = "InjectionOptions")
	@XmlElement(namespace = Namespaces.TOSCA_NAMESPACE, name = "TopologyTemplate")
	protected List<TTopologyTemplate> injectionOptions;

	public InjectionOption() {
	}

	public InjectionOption(String nodeID, List<TTopologyTemplate> injectionOptions) {
		this.nodeID = nodeID;
		this.injectionOptions = injectionOptions;
	}
}
