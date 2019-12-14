/********************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.admin.NamespacesId;
import org.eclipse.winery.repository.backend.AbstractNamespaceManager;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A NamespaceManager that manages the JsonBasedNamespaceManager of each Repository Manager of a manager. What's his
 * salary?
 */
public class JsonBasedMultiNamespaceManager extends AbstractNamespaceManager {

    private static JsonBasedMultiNamespaceManager INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonBasedNamespaceManager.class);

    private final MultiRepository repository;
    private final Map<IRepository, Map<String, NamespaceProperties>> namespaceProperties;

    private JsonBasedMultiNamespaceManager(MultiRepository repository) {
        this.repository = repository;
        this.namespaceProperties = this.loadNamespacePropertiesFromFile();
    }

    public static JsonBasedMultiNamespaceManager getInstance(MultiRepository repository) {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new JsonBasedMultiNamespaceManager(repository);
        return INSTANCE;
    }

    private Map<IRepository, Map<String, NamespaceProperties>> loadNamespacePropertiesFromFile() {
        Map<IRepository, Map<String, NamespaceProperties>> result = new HashMap<>();
        Map<String, NamespaceProperties> nsProps = new HashMap<>();

        for (IRepository repo : this.repository.getRepositories()) {
            RepositoryFileReference ref = BackendUtils.getRefOfJsonConfiguration(new NamespacesId());
            File file = repo.ref2AbsolutePath(ref).toFile();
            try {
                if (file.exists()) {
                    TypeReference<HashMap<String, NamespaceProperties>> hashMapTypeReference =
                        new TypeReference<HashMap<String, NamespaceProperties>>() {
                        };
                    nsProps = JacksonProvider.mapper.readValue(file, hashMapTypeReference);
                }
            } catch (IOException e) {
                LOGGER.debug("Error while loading the namespace file.", e);
                throw new RuntimeException();
            }
            result.put(repo, nsProps);
        }

        return result;
    }

    @Override
    public void addAllPermanent(Collection<NamespaceProperties> properties) {
        properties.forEach(prop -> {
            IRepository repository = RepositoryUtils.getRepositoryByNamespace(prop.getNamespace(), this.repository);

            if (prop.getUpstreamRepository().isEmpty() && repository instanceof GitBasedRepository) {
                prop.setUpstreamRepository(((GitBasedRepository) repository).getRepositoryUrl());
            }

            repository.getNamespaceManager().addAllPermanent(Collections.singletonList(prop));
        });
        this.repository.updateNamespaces();
    }

    @Override
    public void replaceAll(Map<String, NamespaceProperties> map) {
        map.forEach((namespace, properties) -> {
            IRepository repository = RepositoryUtils.getRepositoryByNamespace(namespace, this.repository);
            repository.getNamespaceManager().replaceAll(map);
        });
        this.repository.updateNamespaces();
    }

    @Override
    public void clear() {
        for (IRepository repo : this.repository.getRepositories()) {
            repo.getNamespaceManager().clear();
        }
    }

    @Override
    public Map<String, NamespaceProperties> getAllNamespaces() {
        Map<String, NamespaceProperties> result = new HashMap<>();
        for (IRepository repo : this.repository.getRepositories()) {
            result.putAll(repo.getNamespaceManager().getAllNamespaces());
        }
        return result;
    }

    @Override
    protected Set<String> getAllPrefixes(String namespace) {
        IRepository nsRepository = RepositoryUtils.getRepositoryByNamespace(namespace, this.repository);
        return this.namespaceProperties.get(nsRepository).values().stream()
            .map(NamespaceProperties::getPrefix)
            .collect(Collectors.toSet());
    }

    @Override
    public String getPrefix(String namespace) {
        return RepositoryUtils.getRepositoryByNamespace(namespace, this.repository)
            .getNamespaceManager()
            .getPrefix(namespace);
    }

    @Override
    public boolean hasPermanentProperties(String namespace) {
        return RepositoryUtils.getRepositoryByNamespace(namespace, this.repository)
            .getNamespaceManager()
            .hasPermanentProperties(namespace);
    }

    @Override
    public void removeNamespaceProperties(String namespace) {
        RepositoryUtils.getRepositoryByNamespace(namespace, this.repository)
            .getNamespaceManager()
            .removeNamespaceProperties(namespace);
    }

    @Override
    public @NonNull NamespaceProperties getNamespaceProperties(String namespace) {
        return RepositoryUtils.getRepositoryByNamespace(namespace, this.repository)
            .getNamespaceManager()
            .getNamespaceProperties(namespace);
    }

    @Override
    public void setNamespaceProperties(String namespace, NamespaceProperties properties) {
        RepositoryUtils.getRepositoryByNamespace(namespace, this.repository)
            .getNamespaceManager()
            .setNamespaceProperties(namespace, properties);
    }

    @Override
    public boolean isPatternNamespace(String namespace) {
        return RepositoryUtils.getRepositoryByNamespace(namespace, this.repository)
            .getNamespaceManager()
            .isPatternNamespace(namespace);
    }

    @Override
    public boolean isSecureCollection(String namespace) {
        return RepositoryUtils.getRepositoryByNamespace(namespace, this.repository)
            .getNamespaceManager()
            .isSecureCollection(namespace);
    }

    @Override
    public boolean isGeneratedNamespace(String namespace) {
        return false;
    }
}
