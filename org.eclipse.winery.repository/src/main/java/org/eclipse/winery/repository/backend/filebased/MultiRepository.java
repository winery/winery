/********************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.elements.ToscaElementId;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

import org.apache.commons.configuration2.Configuration;
import org.apache.tika.mime.MediaType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Layer that manages the local repositories
 */
public class MultiRepository extends GitBasedRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiRepository.class);

    private final Map<FilebasedRepository, Set<String>> repositoryGlobal = new HashMap<>();
    private final Map<FilebasedRepository, Set<Namespace>> repositoryCommonNamespace = new HashMap<>();

    private GitBasedRepository localRepository;

    public MultiRepository(GitBasedRepositoryConfiguration configuration) throws IOException, GitAPIException {
        super(configuration);

        try {
            LOGGER.debug("Trying to initialize local repository...");
            File localRepoPath = new File(FilebasedRepository.getActiveRepositoryFilePath(), Constants.DEFAULT_LOCAL_REPO_NAME);
            FileBasedRepositoryConfiguration localRepoConfig = new FileBasedRepositoryConfiguration(localRepoPath.toPath());
            GitBasedRepositoryConfiguration gitConfig = new GitBasedRepositoryConfiguration(false, localRepoConfig);
            this.localRepository = new GitBasedRepository(gitConfig);
            LOGGER.debug("Local repo has been initialized at {}", localRepoPath.getAbsolutePath());
        } catch (IOException | GitAPIException e) {
            LOGGER.error("Error while initializing local repository of the Multi Repository!", e);
            throw e;
        }

        RepositoryConfigurationManager.initialize(this);
        RepositoryUtils.checkGitIgnore(this);
        repositoryGlobal.put(localRepository, new HashSet<>());
        updateNamespaces();
    }

    @Override
    Path generateWorkingRepositoryRoot() {
        return this.repositoryDep;
    }

    protected FilebasedRepository getLocalRepository() {
        return localRepository;
    }

    protected Map<FilebasedRepository, Set<String>> getRepositoriesMap() {
        return repositoryGlobal;
    }

    protected Map<FilebasedRepository, Set<Namespace>> getRepositoriesCommonNamespace() {
        return repositoryCommonNamespace;
    }

    protected Collection<FilebasedRepository> getRepositories() {
        return repositoryGlobal.keySet();
    }

    protected void addRepository(FilebasedRepository repository) {
        registerRepository(repository);
    }

    protected void removeRepository(String urlToRepository) {
        for (FilebasedRepository repo : repositoryGlobal.keySet()) {
            if (((GitBasedRepository) repo).getRepositoryUrl().equals(urlToRepository)) {
                unregisterRepository(repo);
                break;
            }
        }
    }

    protected void removeRepository(FilebasedRepository repository) {
        unregisterRepository(repository);
    }

    private void addNamespacesToRepository(FilebasedRepository repository, GenericId id) {
        if (id instanceof DefinitionsChildId) {
            Namespace namespace = ((DefinitionsChildId) id).getNamespace();
            String ns = namespace.getDecoded();

            Set<String> set = repositoryGlobal.get(repository);
            set.add(ns);
            repositoryGlobal.put(repository, set);

            String pns;
            try {
                pns = namespace.getEncoded().substring(0, namespace.getEncoded()
                    .lastIndexOf(RepositoryUtils.getUrlSeparatorEncoded()));
            } catch (UnsupportedEncodingException ex) {
                LOGGER.error("Error when generating the namespace", ex);
                return;
            }

            Set<Namespace> setPre = repositoryCommonNamespace.get(repository);
            setPre.add(new Namespace(pns, true));
            repositoryCommonNamespace.put(repository, setPre);
        }
    }

    private void addNamespacesToRepository(FilebasedRepository repository, RepositoryFileReference ref) {
        addNamespacesToRepository(repository, ref.getParent());
    }

    protected void updateNamespaces() {
        Map<FilebasedRepository, Set<String>> tempMap = new HashMap<>(repositoryGlobal);
        tempMap.keySet().forEach(repository -> {
            Collection<String> repositoryNamespaces = repository.getNamespaceManager().getAllNamespaces().keySet();
            repositoryGlobal.put(repository, new HashSet<>(repositoryNamespaces));
            repositoryCommonNamespace.put(repository, generateCommonNamespace(repositoryNamespaces));
        });
    }

    private static HashSet<Namespace> generateCommonNamespace(Collection<String> repositoryNamespaces) {
        HashSet<Namespace> setNS = new HashSet<>();

        repositoryNamespaces.forEach(ns -> {
            Namespace namespace = new Namespace(ns, false);
            if (namespace.getEncoded().contains("%2F")) {
                String pns = namespace.getEncoded();
                if (namespace.getEncoded().lastIndexOf("%2F") > 15) {
                    pns = namespace.getEncoded().substring(0, namespace.getEncoded().lastIndexOf("%2F"));
                }
                setNS.add(new Namespace(pns, true));
            }
        });

        return setNS;
    }

    private void registerRepository(FilebasedRepository repository) {
        if (repositoryGlobal.get(repository) != null) {
            LOGGER.debug("The repository is probably already registered.");
            return;
        }

        repositoryGlobal.put(repository, new HashSet<>());
        updateNamespaces();
    }

    private void unregisterRepository(FilebasedRepository repository) {
        repositoryGlobal.remove(repository);
        repositoryCommonNamespace.remove(repository);
    }

    @Override
    public boolean exists(GenericId id) {
        return RepositoryUtils.getRepositoryById(id, this).exists(id);
    }

    @Override
    public boolean exists(RepositoryFileReference ref) {
        return RepositoryUtils.getRepositoryByRef(ref, this).exists(ref);
    }

    @Override
    public Configuration getConfiguration(RepositoryFileReference ref) {
        return RepositoryUtils.getRepositoryByRef(ref, this).getConfiguration(ref);
    }

    @Override
    public long getSize(RepositoryFileReference ref) throws IOException {
        return RepositoryUtils.getRepositoryByRef(ref, this).getSize(ref);
    }

    @Override
    public FileTime getLastModifiedTime(RepositoryFileReference ref) throws IOException {
        return RepositoryUtils.getRepositoryByRef(ref, this).getLastModifiedTime(ref);
    }

    @Override
    public InputStream newInputStream(RepositoryFileReference ref) throws IOException {
        return RepositoryUtils.getRepositoryByRef(ref, this).newInputStream(ref);
    }

    @Override
    public boolean flagAsExisting(GenericId id) {
        return RepositoryUtils.getRepositoryById(id, this).flagAsExisting(id);
    }

    @Override
    public Date getLastUpdate(RepositoryFileReference ref) {
        return RepositoryUtils.getRepositoryByRef(ref, this).getLastUpdate(ref);
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, String content, MediaType mediaType) throws IOException {
        FilebasedRepository repository = RepositoryUtils.getRepositoryByRef(ref, this);
        repository.putContentToFile(ref, content, mediaType);
        addNamespacesToRepository(repository, ref);
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException {
        FilebasedRepository repository = RepositoryUtils.getRepositoryByRef(ref, this);
        repository.putContentToFile(ref, inputStream, mediaType);
        addNamespacesToRepository(repository, ref);
    }

    @Override
    public SortedSet<RepositoryFileReference> getContainedFiles(GenericId id) {
        return RepositoryUtils.getRepositoryById(id, this).getContainedFiles(id);
    }

    @Override
    public Collection<Namespace> getUsedNamespaces() {
        Collection<Namespace> result = new HashSet<>();
        getRepositories().forEach(repository -> result.addAll(repository.getUsedNamespaces()));
        return result;
    }

    @Override
    public Collection<Namespace> getComponentsNamespaces(Class<? extends DefinitionsChildId> clazz) {
        Collection<Namespace> result = new HashSet<>();
        getRepositories().forEach(repository -> result.addAll(repository.getComponentsNamespaces(clazz)));
        return result;
    }

    @Override
    public Collection<? extends DefinitionsChildId> getAllIdsInNamespace(Class<? extends DefinitionsChildId> clazz, Namespace namespace) {
        Collection<DefinitionsChildId> result = new HashSet<>();
        getRepositories().forEach(repository -> result.addAll(repository.getAllIdsInNamespace(clazz, namespace)));
        return result;
    }

    @Override
    public <T extends DefinitionsChildId> SortedSet<T> getAllDefinitionsChildIds(Class<T> idClass) {
        SortedSet<T> result = new TreeSet<>();
        getRepositories().forEach(repository -> result.addAll(repository.getAllDefinitionsChildIds(idClass)));
        return result;
    }

    @Override
    public <T extends DefinitionsChildId> SortedSet<T> getStableDefinitionsChildIdsOnly(Class<T> idClass) {
        SortedSet<T> result = new TreeSet<>();
        getRepositories().forEach(repository -> result.addAll(repository.getStableDefinitionsChildIdsOnly(idClass)));
        return result;
    }

    @Override
    public <T extends ToscaElementId> SortedSet<T> getNestedIds(GenericId id, Class<T> idClass) {
        return RepositoryUtils.getRepositoryById(id, this).getNestedIds(id, idClass);
    }

    @Override
    public NamespaceManager getNamespaceManager() {
        return JsonBasedMultiNamespaceManager.getInstance(this);
    }

    @Override
    public void forceDelete(GenericId id) {
        RepositoryUtils.getRepositoryById(id, this).forceDelete(id);
    }

    @Override
    public void forceDelete(Class<? extends DefinitionsChildId> definitionsChildIdClazz, Namespace namespace) {
        getRepositories().forEach(repository -> repository.forceDelete(definitionsChildIdClazz, namespace));
    }

    @Override
    public void getZippedContents(final GenericId id, OutputStream out) throws WineryRepositoryException {
        RepositoryUtils.getRepositoryById(id, this).getZippedContents(id, out);
    }

    @Override
    public void doImport(InputStream in) {
        getLocalRepository().doImport(in);
    }

    @Override
    public void rename(DefinitionsChildId oldId, DefinitionsChildId newId) throws IOException {
        RepositoryUtils.getRepositoryById(oldId, this).rename(oldId, newId);
    }

    @Override
    public void duplicate(DefinitionsChildId from, DefinitionsChildId newId) throws IOException {
        RepositoryUtils.getRepositoryById(from, this).duplicate(from, newId);
    }

    @Override
    public void forceDelete(RepositoryFileReference ref) throws IOException {
        RepositoryUtils.getRepositoryByRef(ref, this).forceDelete(ref);
    }
}
