/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.repository.backend.AbstractNamespaceManager;

import org.apache.commons.configuration2.Configuration;

@Deprecated
public class ConfigurationBasedNamespaceManager extends AbstractNamespaceManager {

    private Configuration configuration;
    private Map<String, String> namespaceToPrefixMap = new HashMap<>();

    /**
     * @param configuration The configuration to read from and store data into
     */
    public ConfigurationBasedNamespaceManager(Configuration configuration) {
        this.configuration = configuration;

        // globally set prefixes

        // if that behavior is not desired, the code has to be moved to "generatePrefix" which checks for existence, ...
        this.configuration.setProperty(Namespaces.TOSCA_NAMESPACE, "tosca");
        this.configuration.setProperty(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "winery");
        this.configuration.setProperty(Namespaces.W3C_XML_SCHEMA_NS_URI, "xsd");
        this.configuration.setProperty(Namespaces.W3C_NAMESPACE_URI, "xmlns");

        // example namespaces opened for users to create new types
        this.configuration.setProperty(Namespaces.EXAMPLE_NAMESPACE_URI, "ex");
    }

    @Override
    public String getPrefix(String namespace) {
        if (namespace == null) {
            namespace = "";
        }

        // configuration stores the permanent mapping
        // this has precedence
        String prefix = configuration.getString(namespace);
        if (prefix == null || prefix.isEmpty()) {
            // in case no permanent mapping is found - or the prefix is invalid, check the in-memory ones
            prefix = this.namespaceToPrefixMap.get(namespace);
            if (prefix == null) {
                prefix = this.generatePrefix(namespace);
                this.namespaceToPrefixMap.put(namespace, prefix);
            }
        }
        return prefix;
    }

    @Override
    public boolean hasPermanentProperties(String namespace) {
        return this.configuration.containsKey(namespace);
    }

    @Override
    public void removeNamespaceProperties(String namespace) {
        this.configuration.clearProperty(namespace);
        // ensure that in-memory mapping also does not have the key any more
        this.namespaceToPrefixMap.remove(namespace);
    }

    @Override
    public void setNamespaceProperties(String namespace, NamespaceProperties properties) {
        if (Objects.isNull(namespace) || namespace.isEmpty() ||
            Objects.isNull(properties) || Objects.isNull(properties.getPrefix()) || properties.getPrefix().isEmpty()) {
            return;
        }

        if (!this.getAllPermanentPrefixes().contains(properties.getPrefix())) {
            this.configuration.setProperty(namespace, properties.getPrefix());
            // ensure that in-memory mapping does not have the key any more
            this.namespaceToPrefixMap.remove(namespace);
        }
    }

    private Collection<String> getAllPermanentPrefixes() {
        Iterator<String> keys = this.configuration.getKeys();
        Set<String> res = new HashSet<>();
        while (keys.hasNext()) {
            String key = keys.next();
            String prefix = this.configuration.getString(key);
            res.add(prefix);
        }
        return res;
    }

    @Override
    public Map<String, NamespaceProperties> getAllNamespaces() {
        Iterator<String> keys = this.configuration.getKeys();
        Map<String, NamespaceProperties> map = new HashMap<>();
        while (keys.hasNext()) {
            String next = keys.next();
            map.put(next, new NamespaceProperties(next, this.configuration.getString(next)));
        }
        return map;
    }

    @Override
    public void clear() {
        this.configuration.clear();
    }

    @Override
    protected Set<String> getAllPrefixes(String namespace) {
        Set<String> allPrefixes = new HashSet<>();
        allPrefixes.addAll(this.getAllPermanentPrefixes());
        allPrefixes.addAll(this.namespaceToPrefixMap.values());

        return allPrefixes;
    }

    @Override
    public void addAllPermanent(Collection<NamespaceProperties> properties) {
        this.clear();
        for (NamespaceProperties nsp : properties) {
            this.setNamespaceProperties(nsp.getNamespace(), nsp);
        }
    }

    @Override
    public void replaceAll(Map<String, NamespaceProperties> map) {
        this.clear();
        map.forEach(this::setNamespaceProperties);
    }

    @Override
    public NamespaceProperties getNamespaceProperties(String namespace) {
        String prefix = this.getPrefix(namespace);
        return new NamespaceProperties(namespace, prefix);
    }

    @Override
    public boolean isPatternNamespace(String namespace) {
        return false;
    }

    @Override
    public boolean isSecureCollection(String namespace) {
        return false;
    }

    @Override
    public boolean isGeneratedNamespace(String namespace) {
        return false;
    }
}
