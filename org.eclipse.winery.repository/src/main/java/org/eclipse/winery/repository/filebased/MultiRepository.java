/*******************************************************************************
 * Copyright (c) 2020-2021 Contributors to the Eclipse Foundation
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.eclipse.winery.common.Constants;
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
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;
import org.eclipse.winery.repository.backend.filebased.RepositoryProperties;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;
import org.eclipse.winery.repository.filebased.management.IRepositoryResolver;
import org.eclipse.winery.repository.filebased.management.RepositoryResolverFactory;
import org.eclipse.winery.repository.xml.XmlRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.eventbus.EventBus;
import org.apache.commons.configuration2.Configuration;
import org.apache.tika.mime.MediaType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Layer that manages the local repositories
 */
public class MultiRepository implements IWrappingRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiRepository.class);

    private final Map<IRepository, Set<String>> repositoryGlobal = new HashMap<>();
    private final Map<IRepository, Set<Namespace>> repositoryCommonNamespace = new HashMap<>();
    private File dependantRepositories;
    private List<RepositoryProperties> repositoriesList = new ArrayList<>();
    private final GitBasedRepository localRepository;
    private final Path repositoryRoot;
    private final EventBus eventBus;

    /**
     * Initializes a local Git Repository at repositoryRoot/workspace. The repository root points to the directory of
     * the MultiRepository. The repositoryConfiguration points to the repositories.json file. This file contains the
     * repositories that are part of the MultiRepository.
     */
    public MultiRepository(Path repositoryRoot) throws IOException, GitAPIException {
        this.repositoryRoot = repositoryRoot;
        this.eventBus = new EventBus();
        try {
            LOGGER.debug("Trying to initialize local repository...");

            File localRepoPath = new File(repositoryRoot.toString(), Constants.DEFAULT_LOCAL_REPO_NAME);
            this.dependantRepositories = new File(repositoryRoot.toString(), Filename.FILENAME_JSON_MUTLI_REPOSITORIES);
            readRepositoriesConfig();

            GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration = new GitBasedRepositoryConfiguration(
                false,
                new FileBasedRepositoryConfiguration(localRepoPath.toPath())
            );
            this.localRepository = new GitBasedRepository(
                gitBasedRepositoryConfiguration,
                RepositoryFactory.createXmlOrYamlRepository(gitBasedRepositoryConfiguration, localRepoPath.toPath())
            );

            LOGGER.debug("Local repo has been initialized at {}", localRepoPath.getAbsolutePath());
        } catch (IOException | GitAPIException e) {
            LOGGER.error("Error while initializing local repository of the Multi Repository!", e);
            throw e;
        }
        repositoryGlobal.put(localRepository, new HashSet<>());
        updateNamespaces();
    }

    @Override
    public IRepository getRepository() {
        if (localRepository != null) {
            return localRepository.getRepository();
        }
        return null;
    }

    Map<IRepository, Set<String>> getRepositoriesMap() {
        return repositoryGlobal;
    }

    Map<IRepository, Set<Namespace>> getRepositoriesCommonNamespace() {
        return repositoryCommonNamespace;
    }

    public Set<IRepository> getRepositories() {
        return repositoryGlobal.keySet();
    }

    private void addRepository(IRepository repository) {
        registerRepository(repository);
    }

    public void removeRepository(String urlToRepository) {
        for (IRepository repo : repositoryGlobal.keySet()) {
            if (((GitBasedRepository) repo).getRepositoryUrl() != null) {
                if (((GitBasedRepository) repo).getRepositoryUrl().equals(urlToRepository)) {

                    //[TODO] Check if any other Repository is dependent on this Repository, if so, can't remove Repository (overwrite allowed with warning)

                    unregisterRepository(repo);
                    break;
                }
            }
        }
    }

    protected void removeRepository(AbstractFileBasedRepository repository) {
        unregisterRepository(repository);
    }

    /**
     * In case of a Git-based repository we have to check the inner IRepository reference to determine the correct
     * referenced maintained in repositoryGlobal.
     */
    private IRepository determineRepositoryRef(IRepository repository) {
        for (IRepository r : repositoryGlobal.keySet()) {
            IRepository inner = r;
            if (r instanceof GitBasedRepository) {
                inner = ((GitBasedRepository) r).getRepository();
            }
            if (repository.equals(inner)) {
                return r;
            }
        }
        return repository;
    }

    private void addNamespacesToRepository(IRepository repository, GenericId id) {
        if (id instanceof DefinitionsChildId) {
            Namespace namespace = ((DefinitionsChildId) id).getNamespace();
            String ns = namespace.getDecoded();

            IRepository r = determineRepositoryRef(repository);
            Set<String> set = repositoryGlobal.get(r);
            set.add(ns);
            repositoryGlobal.put(r, set);

            String pns;
            try {
                if (this.localRepository.getRepository() instanceof XmlRepository) {
                    pns = namespace.getEncoded().substring(0, namespace.getEncoded()
                        .lastIndexOf(RepositoryUtils.getUrlSeparatorEncoded()));
                } else {
                    pns = namespace.getEncoded();
                }
            } catch (UnsupportedEncodingException ex) {
                LOGGER.error("Error when generating the namespace", ex);
                return;
            }

            Set<Namespace> setPre = repositoryCommonNamespace.get(r);
            setPre.add(new Namespace(pns, true));
            repositoryCommonNamespace.put(r, setPre);
        }
    }

    private void addNamespacesToRepository(IRepository repository, RepositoryFileReference ref) {
        addNamespacesToRepository(repository, ref.getParent());
    }

    void updateNamespaces() {
        Map<IRepository, Set<String>> tempMap = new HashMap<>(repositoryGlobal);
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

    private void registerRepository(IRepository repository) {
        if (repositoryGlobal.get(repository) != null) {
            LOGGER.debug("The repository is probably already registered.");
            return;
        }

        repositoryGlobal.put(repository, new HashSet<>());
        updateNamespaces();
    }

    private void unregisterRepository(IRepository repository) {
        repositoryGlobal.remove(repository);
        repositoryCommonNamespace.remove(repository);
    }

    /**
     * Clones the repositories specified by repositories into the MultiRepository
     *
     * @param repositories the set of repositories that should be cloned into the MultiRepository.
     */
    void addRepositoryToFile(List<RepositoryProperties> repositories) {
        this.repositoriesList = repositories;
        saveConfiguration();
        loadRepositoriesByList(this.repositoriesList);
    }

    /**
     * Returns the repositoryList, if the repositories.json exist, the repositoriesList is first loaded with the
     * repositories from the json.
     */
    List<RepositoryProperties> getRepositoriesFromFile() {
        if (repoContainsConfigFile()) {
            try {
                readRepositoriesConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return repositoriesList;
    }

    /**
     * Loads the repositories from the repositories.json file into the repositories list. Then clones the repositories
     * from the repositories list.
     */
    private void readRepositoriesConfig() throws IOException {
        if (repoContainsConfigFile()) {
            LOGGER.info("Found Repositories file");
            this.repositoriesList = loadConfiguration(this.dependantRepositories);
            MultiRepositoryManager multiRepositoryManager = new MultiRepositoryManager();

            if (!multiRepositoryManager.isMultiRepositoryFileStructureEstablished(this.repositoryRoot)) {
                multiRepositoryManager.createMultiRepositoryFileStructure(
                    this.repositoryRoot,
                    this.repositoryRoot.resolve(Constants.DEFAULT_LOCAL_REPO_NAME)
                );
            }
            loadRepositoriesByList(this.repositoriesList);
        } else {
            createConfigFileAndSetFactoryToMultiRepository();
        }
    }

    /**
     * Writes the content of the repositoriesList into the repositories.json of the MultiRepository.
     */
    private void saveConfiguration() {
        if (!repoContainsConfigFile()) {
            createConfigFileAndSetFactoryToMultiRepository();
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                objectMapper.writeValue(dependantRepositories, repositoriesList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return True whenever there is a repositories.json file in the root folder of the MultiRepository
     */
    private boolean repoContainsConfigFile() {
        File repo = new File(this.getRepositoryRoot().toString(), Filename.FILENAME_JSON_MUTLI_REPOSITORIES);
        return repo.exists();
    }

    /**
     * Reads the dependencies file into the repositoriesList.
     *
     * @param dependency The path to the dependencies file
     */
    private List<RepositoryProperties> loadConfiguration(File dependency) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader reader = objectMapper.readerFor(new TypeReference<List<RepositoryProperties>>() {
        });
        return reader.readValue(dependency);
    }

    /**
     * Clones all repositories of the MultiRepository into the file system. Does not clone duplicates. Also clones all
     * dependencies of the repositories
     *
     * @param repositoryProperties the list of repositories to load
     */
    private void loadRepositoriesByList(List<RepositoryProperties> repositoryProperties) {
        for (RepositoryProperties repository : repositoryProperties) {
            createRepository(repository.getUrl(), repository.getBranch());
        }
    }

    private void createConfigFileAndSetFactoryToMultiRepository() {
        MultiRepositoryManager multiRepositoryManager = new MultiRepositoryManager();
        multiRepositoryManager.initializeRepositoryListForMultiRepositoryAndReconfigureFactory(repositoriesList);
        try {
            RepositoryFactory.reconfigure();
        } catch (Exception e) {
            LOGGER.error("Error while reconfiguring the repository!", e);
        }
    }

    /**
     * This method clones a repository into the file system. If the cloned repository contains dependencies in the form
     * of a repositories.json file, the dependencies will be cloned recursively if they are not already in the
     * MultiRepository. The subrepositories are GitbasedRepositories and are added to the list of repositories to the
     * MultiRepository. It the subrepositories have dependencies, they are initialized as MultiRepos
     *
     * @param url    of the repository
     * @param branch which should be cloned
     */
    private void createRepository(String url, String branch) {
        IRepositoryResolver resolver = null;
        Optional<IRepositoryResolver> resolverOptional = RepositoryResolverFactory.getResolver(url, branch);
        if (resolverOptional.isPresent()) {
            resolver = resolverOptional.get();
        }

        if (resolver != null && !RepositoryUtils.checkRepositoryDuplicate(url, this)) {
            try {
                String ownerDirectory = URLEncoder.encode(resolver.getRepositoryMaintainerUrl(), "UTF-8");
                Path ownerRoot = this.repositoryRoot.resolve(ownerDirectory);
                if (!ownerRoot.toFile().exists()) {
                    Files.createDirectories(ownerRoot);
                }

                Path repositoryLocation = ownerRoot.resolve(resolver.getRepositoryName());
                IRepository newSubRepository = resolver.createRepository(repositoryLocation.toFile());
                this.addRepository(newSubRepository);

                File configurationFile = newSubRepository.getRepositoryRoot()
                    .resolve(Filename.FILENAME_JSON_MUTLI_REPOSITORIES).toFile();
                if (configurationFile.exists()) {
                    loadRepositoriesByList(loadConfiguration(configurationFile));
                }
                fixNamespaces(newSubRepository);
            } catch (IOException | GitAPIException e) {
                LOGGER.error("Error while creating the repository structure", e);
            }
        }
    }

    private void fixNamespaces(IRepository repository) {
        SortedSet<DefinitionsChildId> defChildren = repository.getAllDefinitionsChildIds();
        Collection<NamespaceProperties> namespaceProperties = new ArrayList<>();
        for (DefinitionsChildId value : defChildren) {
            namespaceProperties.add(
                new NamespaceProperties(value.getNamespace().getDecoded(),
                    value.getNamespace().getDecoded().replace(".", ""), "",
                    false)
            );
        }
        repository.getNamespaceManager().addAllPermanent(namespaceProperties);
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
    public TDefinitions definitionsFromRef(RepositoryFileReference ref) throws IOException {
        return RepositoryUtils.getRepositoryByRef(ref, this).definitionsFromRef(ref);
    }

    @Override
    public void doDump(OutputStream out) throws IOException {
        localRepository.doDump(out);
    }

    @Override
    public void doClear() {
        localRepository.doClear();
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
        IRepository repository = RepositoryUtils.getRepositoryByRef(ref, this);
        repository.putContentToFile(ref, content, mediaType);
        addNamespacesToRepository(repository, ref);
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException {
        IRepository repository = RepositoryUtils.getRepositoryByRef(ref, this);
        repository.putContentToFile(ref, inputStream, mediaType);
        addNamespacesToRepository(repository, ref);
    }

    @Override
    public Stream<Path> getAllDirsAndFiles(RepositoryFileReference ref, int depth) throws IOException {
        IRepository repository = RepositoryUtils.getRepositoryByRef(ref, this);
        return repository.getAllDirsAndFiles(ref, depth);
    }

    @Override
    public void createDir(RepositoryFileReference ref) throws IOException {
        IRepository repository = RepositoryUtils.getRepositoryByRef(ref, this);
        repository.createDir(ref);
    }

    @Override
    public Path move(RepositoryFileReference sourceRef, RepositoryFileReference targetRef) throws IOException {
        IRepository repository = RepositoryUtils.getRepositoryByRef(sourceRef, this);
        return repository.move(sourceRef, targetRef);
    }

    @Override
    public void putDefinition(RepositoryFileReference ref, TDefinitions content) throws IOException {
        IRepository repository = RepositoryUtils.getRepositoryByRef(ref, this);
        repository.putDefinition(ref, content);
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
    public Path ref2AbsolutePath(RepositoryFileReference ref) {
        return RepositoryUtils.getRepositoryByRef(ref, this).ref2AbsolutePath(ref);
    }

    @Override
    public Path id2RelativePath(GenericId id) {
        return RepositoryUtils.getRepositoryById(id, this).id2RelativePath(id);
    }

    @Override
    public Path id2AbsolutePath(GenericId id) {
        return RepositoryUtils.getRepositoryById(id, this).id2AbsolutePath(id);
    }

    @Override
    public Path makeAbsolute(Path relativePath) {
        return this.getRepositoryRoot().resolve(relativePath);
    }

    @Override
    public Path getRepositoryRoot() {
        return this.repositoryRoot;
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
        try {
            RepositoryUtils.getRepositoryById(id, this).forceDelete(id);
        } catch (IOException e) {
            LOGGER.debug("Error while force deleting definition child.", e);
        }
    }

    @Override
    public void forceDelete(Class<? extends DefinitionsChildId> definitionsChildIdClazz, Namespace namespace) {
        getRepositories().forEach(repository -> {
            try {
                repository.forceDelete(definitionsChildIdClazz, namespace);
            } catch (IOException e) {
                LOGGER.debug("Error while force deleting definition child.", e);
            }
        });
    }

    @Override
    public void getZippedContents(final GenericId id, OutputStream out) throws WineryRepositoryException {
        RepositoryUtils.getRepositoryById(id, this).getZippedContents(id, out);
    }

    @Override
    public void doImport(InputStream in) {
        getRepository().doImport(in);
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

    /**
     * This method registers an Object on the repositories {@link EventBus}
     *
     * @param eventListener an objects that contains methods annotated with the @{@link com.google.common.eventbus.Subscribe}
     */
    public void registerForEvents(Object eventListener) {
        this.eventBus.register(eventListener);
    }

    /**
     * This method unregisters an Object on the repositories {@link EventBus}
     *
     * @param eventListener an objects that contains methods annotated with the @{@link com.google.common.eventbus.Subscribe}
     */
    public void unregisterForEvents(Object eventListener) {
        this.eventBus.register(eventListener);
    }

    @Override
    public void serialize(TDefinitions definitions, OutputStream target) throws IOException {
        RepositoryUtils.getRepositoryByNamespace(definitions.getTargetNamespace(), this)
            .serialize(definitions, target);
    }
}
