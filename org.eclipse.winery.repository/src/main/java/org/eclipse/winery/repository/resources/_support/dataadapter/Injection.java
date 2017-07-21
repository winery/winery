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

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.winery.common.constants.Namespaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;

//@XmlType
public class Injection {
    @XmlElement(name = "hostedNodeID")
    protected String hostedNodeID;
    @XmlElement(namespace = Namespaces.TOSCA_NAMESPACE, name = "NodeTemplate")
    protected TNodeTemplate hostNodeTemplate;

    public Injection() {
    }

    public Injection(String hostedNodeID, TNodeTemplate hostNodeTemplate) {
        this.hostedNodeID = hostedNodeID;
        this.hostNodeTemplate = hostNodeTemplate;
    }

}

