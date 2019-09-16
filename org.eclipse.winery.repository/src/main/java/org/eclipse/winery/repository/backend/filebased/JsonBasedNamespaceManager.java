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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.repository.backend.AbstractNamespaceManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonBasedNamespaceManager extends AbstractNamespaceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonBasedNamespaceManager.class);
    private final File file;
    private final ObjectMapper objectMapper;

    private Map<String, NamespaceProperties> namespaceProperties;

    public JsonBasedNamespaceManager(File file) {
        this(file, true);
    }

    public JsonBasedNamespaceManager(File file, boolean local) {
        Objects.requireNonNull(file);
        this.file = file;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.namespaceProperties = this.loadNamespacePropertiesFromFile();

        if (local) {
            this.namespaceProperties.put(Namespaces.TOSCA_NAMESPACE,
                new NamespaceProperties(Namespaces.TOSCA_NAMESPACE, "tosca", "Predefined TOSCA elements", "", false)
            );
            this.namespaceProperties.put(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE,
                new NamespaceProperties(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "winery", "TOSCA extension by Eclipse Winery", "", false)
            );
            this.namespaceProperties.put(Namespaces.W3C_NAMESPACE_URI,
                new NamespaceProperties(Namespaces.W3C_NAMESPACE_URI, "xmlns", "W3C XML namespace", "", false)
            );
            this.namespaceProperties.put(Namespaces.W3C_XML_SCHEMA_NS_URI,
                new NamespaceProperties(Namespaces.W3C_XML_SCHEMA_NS_URI, "xsd", "W3C XML schema namespace", "", false)
            );
            this.namespaceProperties.put(Namespaces.EXAMPLE_NAMESPACE_URI,
                new NamespaceProperties(Namespaces.EXAMPLE_NAMESPACE_URI, "ex", "Namespace for creating examples", "", false)
            );
        }
    }

    @Override
    protected Set<String> getAllPrefixes(String namespace) {
        return this.namespaceProperties.values().stream()
            .map(NamespaceProperties::getPrefix)
            .collect(Collectors.toSet());
    }

    @Override
    public String getPrefix(String namespace) {
        return getNamespaceProperties(Objects.isNull(namespace) ? "" : namespace).getPrefix();
    }

    @NonNull
    public NamespaceProperties getNamespaceProperties(String namespace) {
        NamespaceProperties properties = this.namespaceProperties.get(namespace);

        if (Objects.isNull(properties)) {
            properties = new NamespaceProperties(namespace, generatePrefix(namespace));
        }

        return properties;
    }

    private void save() {
        try {
            if (!this.file.exists()) {
                if (this.file.getParentFile().mkdirs() || this.file.createNewFile()) {
                    LOGGER.debug("Created new namespace file at {}", this.file);
                } else {
                    LOGGER.error("Could not create namespace file at {}", this.file);
                }
            }

            this.objectMapper.writeValue(this.file, this.namespaceProperties);
        } catch (IOException e) {
            LOGGER.debug("Could not save namespace to json file!", e);
        }
    }

    @Override
    public boolean hasPermanentProperties(String namespace) {
        Map<String, NamespaceProperties> map = this.loadNamespacePropertiesFromFile();

        return map.containsKey(namespace)
            && Objects.nonNull(map.get(namespace).getPrefix())
            && !map.get(namespace).getPrefix().isEmpty();
    }

    @Override
    public void removeNamespaceProperties(String namespace) {
        this.namespaceProperties.remove(namespace);
        this.save();
    }

    @Override
    public void setNamespaceProperties(String namespace, NamespaceProperties properties) {
        this.namespaceProperties.put(namespace, properties);
        this.save();
    }

    @Override
    public Map<String, NamespaceProperties> getAllNamespaces() {
        return this.namespaceProperties;
    }

    @Override
    public void clear() {
        this.namespaceProperties.clear();
        this.save();
    }

    private Map<String, NamespaceProperties> loadNamespacePropertiesFromFile() {
        Map<String, NamespaceProperties> nsProps = new HashMap<>();

        try {
            if (this.file.exists()) {
                TypeReference<HashMap<String, NamespaceProperties>> hashMapTypeReference =
                    new TypeReference<HashMap<String, NamespaceProperties>>() {
                    };
                nsProps = objectMapper.readValue(file, hashMapTypeReference);
            }
        } catch (IOException e) {
            LOGGER.debug("Error while loading the namespace file.", e);
            throw new RuntimeException();
        }

        return nsProps;
    }

    @Override
    public void addAllPermanent(Collection<NamespaceProperties> properties) {
        properties.forEach(prop -> this.namespaceProperties.put(prop.getNamespace(), prop));
        this.save();
    }

    @Override
    public void replaceAll(Map<String, NamespaceProperties> map) {
        this.namespaceProperties = map;
        this.save();
    }

    @Override
    public boolean isPatternNamespace(String namespace) {
        NamespaceProperties properties = this.namespaceProperties.get(namespace);

        if (Objects.nonNull(properties)) {
            return properties.isPatternCollection();
        } else {
            return false;
        }
    }

    @Override
    public boolean isSecureCollection(String namespace) {
        NamespaceProperties properties = this.namespaceProperties.get(namespace);

        if (Objects.nonNull(properties)) {
            return properties.isSecureCollection();
        } else {
            return false;
        }
    }

    @Override
    public boolean isGeneratedNamespace(String namespace) {
        NamespaceProperties properties = this.namespaceProperties.get(namespace);

        if (Objects.nonNull(properties)) {
            return properties.isGeneratedNamespace();
        } else {
            return false;
        }
    }
}
