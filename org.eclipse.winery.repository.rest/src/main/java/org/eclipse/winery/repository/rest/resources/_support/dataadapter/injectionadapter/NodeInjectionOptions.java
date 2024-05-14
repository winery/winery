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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

public class NodeInjectionOptions {
    @XmlElement(name = "NodeID")
    protected String nodeID;

    @XmlElementWrapper(name = "InjectionOptions")
    protected List<QName> injectionOptions;

    public NodeInjectionOptions() {
    }

    public NodeInjectionOptions(String nodeID, List<QName> injectionOptions) {
        this.nodeID = nodeID;
        this.injectionOptions = injectionOptions;
    }

    public void addInjectionOption(QName injectionOption) {
        if (this.injectionOptions == null) {
            this.injectionOptions = new ArrayList<>();
        }
        injectionOptions.add(injectionOption);
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public List<QName> getInjectionOptions() {
        return injectionOptions;
    }

    public void setInjectionOptions(List<QName> injectionOptions) {
        this.injectionOptions = injectionOptions;
    }
}
