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

package org.eclipse.winery.repository.rest.resources._support.dataadapter.injectionadapter;

import org.eclipse.winery.model.tosca.TTopologyTemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "InjectorReplaceData")
public class InjectorReplaceData {

    @XmlJavaTypeAdapter(value = InjectionDataMapAdapter.class)
    public Map<String, TTopologyTemplate> hostInjections;

    @XmlJavaTypeAdapter(value = InjectionOptionsMapAdapter.class)
    public Map<String, TTopologyTemplate> connectionInjections;

    public void setHostInjections(Map<String, TTopologyTemplate> hostInjections) {
        this.hostInjections = hostInjections;
    }

    public void setConnectionInjections(Map<String, TTopologyTemplate> connectionInjections) {
        this.connectionInjections = connectionInjections;
    }

}
