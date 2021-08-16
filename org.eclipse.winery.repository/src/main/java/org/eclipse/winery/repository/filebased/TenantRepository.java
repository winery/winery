/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.filebased;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.elements.ToscaElementId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.IWrappingRepository;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

import org.apache.commons.configuration2.Configuration;
import org.apache.tika.mime.MediaType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantRepository implements IWrappingRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantRepository.class);

    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();
    private static final ConcurrentHashMap<String, IRepository> repositories = new ConcurrentHashMap<>();

    private static final String defaultRepositoryFolder = "default";

    private final IRepository defaultRepository;
    private final Path repositoryRoot;

    public TenantRepository(Path repositoryRoot) throws IOException, GitAPIException {
        this.repositoryRoot = repositoryRoot;

        FileBasedRepositoryConfiguration fileBasedConfig = new FileBasedRepositoryConfiguration(repositoryRoot.resolve(defaultRepositoryFolder));

        this.defaultRepository = this.createRepo(
            new GitBasedRepositoryConfiguration(false, fileBasedConfig)
        );
    }

    public TenantRepository(Path repositoryRoot, List<String> tenants) throws IOException, GitAPIException {
        this(repositoryRoot);

        for (String tenant : tenants) {
            initTenantRepository(tenant);
        }
    }

    public void useTenant(String tenantName) {
        TENANT.set(tenantName);
    }

    public IRepository initTenantRepository(String tenantName) throws GitAPIException, IOException {
        FileBasedRepositoryConfiguration fileBasedConfig = new FileBasedRepositoryConfiguration(repositoryRoot.resolve(tenantName));
        IRepository tenantRepo = this.createRepo(
            new GitBasedRepositoryConfiguration(false, fileBasedConfig)
        );
        repositories.put(tenantName, tenantRepo);
        return tenantRepo;
    }

    @Override
    public void serialize(TDefinitions definitions, OutputStream target) throws IOException {
        getRepository().serialize(definitions, target);
    }

    @Override
    public boolean flagAsExisting(GenericId id) {
        return getRepository().flagAsExisting(id);
    }

    @Override
    public boolean exists(GenericId id) {
        return getRepository().exists(id);
    }

    @Override
    public void forceDelete(RepositoryFileReference ref) throws IOException {
        getRepository().forceDelete(ref);
    }

    @Override
    public boolean exists(RepositoryFileReference ref) {
        return getRepository().exists(ref);
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, String content, MediaType mediaType) throws IOException {
        getRepository().putContentToFile(ref, content, mediaType);
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException {
        getRepository().putContentToFile(ref, inputStream, mediaType);
    }

    @Override
    public void putDefinition(RepositoryFileReference ref, TDefinitions content) throws IOException {
        getRepository().putDefinition(ref, content);
    }

    @Override
    public InputStream newInputStream(RepositoryFileReference ref) throws IOException {
        return getRepository().newInputStream(ref);
    }

    @Override
    public TDefinitions definitionsFromRef(RepositoryFileReference ref) throws IOException {
        return getRepository().definitionsFromRef(ref);
    }

    @Override
    public void getZippedContents(GenericId id, OutputStream out) throws WineryRepositoryException {
        getRepository().getZippedContents(id, out);
    }

    @Override
    public long getSize(RepositoryFileReference ref) throws IOException {
        return getRepository().getSize(ref);
    }

    @Override
    public FileTime getLastModifiedTime(RepositoryFileReference ref) throws IOException {
        return getRepository().getLastModifiedTime(ref);
    }

    @Override
    public void doDump(OutputStream out) throws IOException {
        getRepository().doDump(out);
    }

    @Override
    public void doClear() {
        getRepository().doClear();
    }

    @Override
    public void doImport(InputStream in) {
        getRepository().doImport(in);
    }

    @Override
    public Configuration getConfiguration(RepositoryFileReference ref) {
        return getRepository().getConfiguration(ref);
    }

    @Override
    public Date getLastUpdate(RepositoryFileReference ref) {
        return getRepository().getLastUpdate(ref);
    }

    @Override
    public <T extends DefinitionsChildId> SortedSet<T> getAllDefinitionsChildIds(Class<T> idClass) {
        return getRepository().getAllDefinitionsChildIds(idClass);
    }

    @Override
    public <T extends DefinitionsChildId> SortedSet<T> getStableDefinitionsChildIdsOnly(Class<T> idClass) {
        return getRepository().getStableDefinitionsChildIdsOnly(idClass);
    }

    @Override
    public <T extends ToscaElementId> SortedSet<T> getNestedIds(GenericId ref, Class<T> idClass) {
        return getRepository().getNestedIds(ref, idClass);
    }

    @Override
    public SortedSet<RepositoryFileReference> getContainedFiles(GenericId id) {
        return getRepository().getContainedFiles(id);
    }

    @Override
    public Collection<Namespace> getUsedNamespaces() {
        return getRepository().getUsedNamespaces();
    }

    @Override
    public Collection<Namespace> getComponentsNamespaces(Class<? extends DefinitionsChildId> clazz) {
        return getRepository().getComponentsNamespaces(clazz);
    }

    @Override
    public void forceDelete(GenericId id) throws IOException {
        getRepository().forceDelete(id);
    }

    @Override
    public void duplicate(DefinitionsChildId from, DefinitionsChildId newId) throws IOException {
        getRepository().duplicate(from, newId);
    }

    @Override
    public void forceDelete(Class<? extends DefinitionsChildId> definitionsChildIdClazz, Namespace namespace) throws IOException {
        getRepository().forceDelete(definitionsChildIdClazz, namespace);
    }

    @Override
    public NamespaceManager getNamespaceManager() {
        return getRepository().getNamespaceManager();
    }

    @Override
    public Collection<? extends DefinitionsChildId> getAllIdsInNamespace(Class<? extends DefinitionsChildId> clazz, Namespace namespace) {
        return getRepository().getAllIdsInNamespace(clazz, namespace);
    }

    @Override
    public Path ref2AbsolutePath(RepositoryFileReference ref) {
        return getRepository().ref2AbsolutePath(ref);
    }

    @Override
    public Path id2RelativePath(GenericId id) {
        return getRepository().id2RelativePath(id);
    }

    @Override
    public Path id2AbsolutePath(GenericId id) {
        return getRepository().id2AbsolutePath(id);
    }

    @Override
    public Path makeAbsolute(Path relativePath) {
        return getRepository().makeAbsolute(relativePath);
    }

    @Override
    public Path getRepositoryRoot() {
        return getRepository().getRepositoryRoot();
    }

    @Override
    public IRepository getRepository() {
        String tenantName = TENANT.get();
        if (tenantName != null) {
            IRepository repo = repositories.get(tenantName);

            if (repo == null) {
                try {
                    repo = this.initTenantRepository(tenantName);
                } catch (GitAPIException | IOException e) {
                    LOGGER.error("Error while initializing repo for tenant {}", tenantName, e);
                }
            }
            return repo;
        }

        return defaultRepository;
    }

    private IRepository createRepo(GitBasedRepositoryConfiguration configuration) throws IOException, GitAPIException {
        if (RepositoryFactory.repositoryContainsMultiRepositoryConfiguration(configuration)) {
            return new MultiRepository(configuration.getRepositoryPath().get());
        }

        // if a repository root is specified, use it instead of the root specified in the config
        AbstractFileBasedRepository localRepository = configuration.getRepositoryPath().isPresent()
            ? RepositoryFactory.createXmlOrYamlRepository(configuration, configuration.getRepositoryPath().get())
            : RepositoryFactory.createXmlOrYamlRepository(configuration, Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot()));
        return new GitBasedRepository(configuration, localRepository);
    }
}
