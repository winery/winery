/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.repository.rest.datatypes;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.common.version.WineryVersion;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComponentId {

    private String id;
    private String name;
    private String namespace;
    private QName qName;
    private Definitions full;
    private WineryVersion version;

    public ComponentId(String id, String name, String namespace, QName qName, Definitions full, WineryVersion version) {
        this.id = id;
        this.name = name;
        this.namespace = namespace;
        this.qName = qName;
        this.full = full;
        this.version = version;
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public QName getqName() {
        return qName;
    }

    public Definitions getFull() {
        return full;
    }

    public WineryVersion getVersion() {
        return version;
    }
}
