/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.yaml.common.writer.xml.support;

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
