/*******************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.backend.filebased;

import java.io.Serializable;

public class NamespaceProperties implements Serializable, Comparable<NamespaceProperties> {

    private static final long serialVersionUID = -6867642303902116547L;

    private String namespace;
    private String prefix;
    private String readableName = "";

    /**
     * Placeholder for future work: support multiple repositories in the backend
     */
    private String upstreamRepository = "";
    private boolean isPatternCollection = false;
    private boolean isSecureCollection = false;
    private boolean isGeneratedNamespace = false;

    /**
     * This constructor must not be used. It is only required for serialization.
     */
    public NamespaceProperties() {
    }

    public NamespaceProperties(String namespace, String prefix) {
        this.namespace = namespace;
        this.prefix = prefix;
    }

    public NamespaceProperties(String namespace, String prefix, String readableName, String upstreamRepository, boolean isPatternCollection) {
        this.namespace = namespace;
        this.prefix = prefix;
        this.readableName = readableName;
        this.upstreamRepository = upstreamRepository;
        this.isPatternCollection = isPatternCollection;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getReadableName() {
        return readableName;
    }

    public void setReadableName(String readableName) {
        this.readableName = readableName;
    }

    public String getUpstreamRepository() {
        return upstreamRepository;
    }

    public void setUpstreamRepository(String upstreamRepository) {
        this.upstreamRepository = upstreamRepository;
    }

    public boolean isPatternCollection() {
        return isPatternCollection;
    }

    public void setPatternCollection(boolean patternCollection) {
        isPatternCollection = patternCollection;
    }

    public boolean isSecureCollection() {
        return isSecureCollection;
    }

    public void setSecureCollection(boolean secureCollection) {
        isSecureCollection = secureCollection;
    }

    public boolean isGeneratedNamespace() {
        return isGeneratedNamespace;
    }

    public void setGeneratedNamespace(boolean generatedNamespace) {
        isGeneratedNamespace = generatedNamespace;
    }

    @Override
    public int compareTo(NamespaceProperties o) {
        int compareTo = this.prefix.compareTo(o.prefix);

        if (compareTo == 0) {
            return this.namespace.compareTo(o.namespace);
        }

        return compareTo;
    }
}
