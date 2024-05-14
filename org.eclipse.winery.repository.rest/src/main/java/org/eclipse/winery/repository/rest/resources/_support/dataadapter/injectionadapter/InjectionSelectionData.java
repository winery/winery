/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement
public class InjectionSelectionData {
    
    @XmlElement(name = "hostInjections")
    public List<Injection> hostInjections;
    @XmlElement(name = "connectionInjections")
    public List<Injection> connectionInjections;

    public InjectionSelectionData() {
    }

    public InjectionSelectionData(List<Injection> hostInjections, List<Injection> connectionInjections) {
        this.hostInjections = hostInjections;
        this.connectionInjections = connectionInjections;
    }

    public List<Injection> getHostInjections() {
        return hostInjections;
    }

    public void setHostInjections(List<Injection> hostInjections) {
        this.hostInjections = hostInjections;
    }

    public List<Injection> getConnectionInjections() {
        return connectionInjections;
    }

    public void setConnectionInjections(List<Injection> connectionInjections) {
        this.connectionInjections = connectionInjections;
    }
}
