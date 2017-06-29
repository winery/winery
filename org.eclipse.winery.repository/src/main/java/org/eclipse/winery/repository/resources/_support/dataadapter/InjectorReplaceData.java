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

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.winery.model.tosca.TTopologyTemplate;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "InjectorReplaceData")
public class InjectorReplaceData {

	@XmlJavaTypeAdapter(value = InjectionDataMapAdapter.class)
    public Map<String, TTopologyTemplate> hostInjections;

	@XmlJavaTypeAdapter(value = InjectionOptionsMapAdapter.class)
	public Map<String, TTopologyTemplate> connectionInjections;

	public void setHostInjections (Map<String, TTopologyTemplate> hostInjections) {
		this.hostInjections = hostInjections;
	}

	public void setConnectionInjections (Map<String, TTopologyTemplate> connectionInjections) {
		this.connectionInjections = connectionInjections;
	}

}
