/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.writer.support;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Properties")
public class PropertiesList {
    private List<JAXBElement> entries = new ArrayList<>();

    private String namespace;

    @XmlAnyElement
    public List<JAXBElement> getEntries() {
        return entries;
    }

    public PropertiesList setEntries(List<JAXBElement> entries) {
        this.entries = entries;
        return this;
    }

    @XmlAttribute(name = "xmlns")
    public String getNamespace() {
        return namespace;
    }

    public PropertiesList setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }
}
