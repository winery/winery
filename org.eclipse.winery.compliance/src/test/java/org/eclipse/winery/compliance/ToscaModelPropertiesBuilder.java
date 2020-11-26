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
package org.eclipse.winery.compliance;

import java.util.LinkedHashMap;

import org.eclipse.winery.model.tosca.TEntityTemplate;

import org.eclipse.jdt.annotation.NonNull;

public class ToscaModelPropertiesBuilder {

    LinkedHashMap<String, String> properties = new LinkedHashMap<>();
    @NonNull
    public static String namespaceURI;
    @NonNull
    public static String prefix = "rnd";
    @NonNull
    public static String localName;

    public ToscaModelPropertiesBuilder(@NonNull String namespaceURI, @NonNull String localName) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
    }

    public ToscaModelPropertiesBuilder addProperty(@NonNull String key, @NonNull String value) {
        properties.put(key, value);
        return this;
    }

    public TEntityTemplate.Properties build() {
        TEntityTemplate.WineryKVProperties result = new TEntityTemplate.WineryKVProperties();
        result.setNamespace(namespaceURI);
        result.setElementName(localName);
        result.setKVProperties(properties);
        return result;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }
}
