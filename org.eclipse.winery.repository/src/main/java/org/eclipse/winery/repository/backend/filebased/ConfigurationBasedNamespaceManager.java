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
package org.eclipse.winery.repository.backend.filebased;

import org.apache.commons.configuration.Configuration;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.repository.backend.NamespaceManager;

import java.util.*;

public class ConfigurationBasedNamespaceManager implements NamespaceManager {

    private Configuration configuration;

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
        this.configuration.setProperty(Namespaces.URI_OPENTOSCA_NODETYPE, "otnt");
    }

    @Override
    public String getPrefix(String namespace) {
        Objects.requireNonNull(namespace);
        String prefix = configuration.getString(namespace);
        if (prefix == null) {
            prefix = this.generatePrefix(namespace);
            this.configuration.setProperty(namespace, prefix);
        }
        return prefix;
    }

    @Override
    public boolean hasPrefix(String namespace) {
        return this.configuration.containsKey(namespace);
    }

    @Override
    public void remove(String namespace) {
        this.configuration.clearProperty(namespace);
    }

    @Override
    public void setPrefix(String namespace, String prefix) {
        if (!this.getAllPrefixes().contains(prefix)) {
            this.configuration.setProperty(namespace, prefix);
        }
    }

    /**
     * Tries to generate a prefix based on the last part of the URL
     */
    private String generatePrefixProposal(String namespace, int round) {
        Objects.requireNonNull(namespace);
        String[] split = namespace.split("/");
        if (split.length == 0) {
            return String.format("ns%d", round);
        } else {
            String result;
            result = split[split.length - 1].replaceAll("[^A-Za-z]+", "");
            if (result.isEmpty()) {
                return String.format("ns%d", round);
            } else {
                if (round == 0) {
                    return result;
                } else {
                    return String.format("%s%d", result, round);
                }
            }
        }
    }

    private String generatePrefix(String namespace) {
        Objects.requireNonNull(namespace);
        String prefix;
        Collection<String> allPrefixes = this.getAllPrefixes();

        int round = 0;
        do {
            prefix = generatePrefixProposal(namespace, round);
            round++;
        } while (allPrefixes.contains(prefix));
        return prefix;
    }

    public Collection<String> getAllPrefixes() {
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
    public Collection<String> getAllNamespaces() {
        Iterator<String> keys = this.configuration.getKeys();
        Set<String> res = new HashSet<>();
        while (keys.hasNext()) {
            res.add(keys.next());
        }
        return res;
    }

    @Override
    public void clear() {
        this.configuration.clear();
    }
}
