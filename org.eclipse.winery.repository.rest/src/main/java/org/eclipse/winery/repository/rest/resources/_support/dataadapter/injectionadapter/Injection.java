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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

//@XmlType
public class Injection {
    @XmlElement(name = "nodeID")
    protected String nodeID;
    @XmlElement(name = "injection")
    protected QName injection;

    public Injection(String hostedNodeID, QName injectedTopologyFragment) {
        this.nodeID = hostedNodeID;
        this.injection = injectedTopologyFragment;
    }

    public Injection() {
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public QName getInjection() {
        return injection;
    }

    public void setInjection(QName injection) {
        this.injection = injection;
    }
}

