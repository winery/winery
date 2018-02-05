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

package org.eclipse.winery.repository.rest.resources._support.dataadapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.Namespaces;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "InjectorReplaceOptions")
public class InjectorReplaceOptions {

	@XmlElement(namespace = Namespaces.TOSCA_NAMESPACE, name = "TopologyTemplate")
	public TTopologyTemplate topologyTemplate;

	@XmlJavaTypeAdapter(value = InjectionOptionsMapAdapter.class)
	public Map<String, List<TTopologyTemplate>> hostInjections = new HashMap<>();

	@XmlJavaTypeAdapter(value = InjectionOptionsMapAdapter.class)
	public Map<String, List<TTopologyTemplate>> connectionInjections = new HashMap<>();

	public void setTopologyTemplate(TTopologyTemplate topologyTemplate) {
		this.topologyTemplate = topologyTemplate;
	}

	public void setHostInjectionOptions(Map<String, List<TTopologyTemplate>> hostInjectionOptions) {
		this.hostInjections = hostInjectionOptions;
	}

	public void setConnectionInjectionOptions(Map<String, List<TTopologyTemplate>> connectionInjectionOptions) {
		this.connectionInjections = connectionInjectionOptions;
	}
}
