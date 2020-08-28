/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.repository.backend.IWrappingRepository;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.ids.definitions.ComplianceRuleId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.HasInheritanceId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.ids.extensions.PatternRefinementModelId;
import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.ids.extensions.RefinementId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.ids.extensions.TestRefinementModelId;
import org.eclipse.winery.model.ids.extensions.TopologyFragmentRefinementModelId;
import org.eclipse.winery.model.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.model.ids.elements.ToscaElementId;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.extensions.OTComplianceRule;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.extensions.OTTestRefinementModel;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.xsd.XsdImportManager;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

import com.google.common.collect.Iterables;
import com.google.common.eventbus.EventBus;
import org.apache.commons.configuration2.Configuration;
import org.apache.tika.mime.MediaType;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CleanCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to reset repository to a certain commit id
 */
public class GitBasedRepository extends AbstractFileBasedRepository implements IWrappingRepository {

    /**
     * Used for synchronizing the method {@link GitBasedRepository#addCommit(RepositoryFileReference)}
     */
    private static final Object COMMIT_LOCK = new Object();
    private static final Logger LOGGER = LoggerFactory.getLogger(GitBasedRepository.class);

    private static List<String> ignoreFile = new ArrayList<>();
    private final Path workingRepositoryRoot;

    private final EventBus eventBus;
    private final GitBasedRepositoryConfiguration configuration;

    private final AbstractFileBasedRepository repository;

    /**
     * @param repository a repository reference to use, this has to be a YAML or XML based repository
     * @throws IOException         thrown if repository does not exist
     * @throws GitAPIException     thrown if there was an error while checking the status of the repository
     * @throws NoWorkTreeException thrown if the directory is not a git work tree
     */
    public GitBasedRepository(GitBasedRepositoryConfiguration repositoryConfiguration, AbstractFileBasedRepository repository) throws IOException, NoWorkTreeException, GitAPIException {
        super(repository.getRepositoryRoot());
        this.configuration = repositoryConfiguration;
        this.repository = repository;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository gitRepo = builder.setWorkTree(this.repository.getRepositoryRoot().toFile()).setMustExist(false).build();

        try (Git git = getGit()) {

            if (this.repository.getRepositoryRoot().resolve(Constants.DEFAULT_LOCAL_REPO_NAME).toFile().exists()) {
                this.workingRepositoryRoot = this.repository.getRepositoryRoot().resolve(Constants.DEFAULT_LOCAL_REPO_NAME);
            } else {
                this.workingRepositoryRoot = this.repository.getRepositoryRoot();
            }

            this.eventBus = new EventBus();

            // explicitly enable longpaths to ensure proper handling of long pathss
            gitRepo.getConfig().setBoolean("core", null, "longpaths", true);
            gitRepo.getConfig().save();
            if (configuration.isAutoCommit() && !git.status().call().isClean()) {
                this.addCommit("Files changed externally.");
            }
        } catch (IOException | GitAPIException ex) {
            LOGGER.error("Error initializing Git.", ex);
            throw ex;
        }
    }

    private Git getGit() throws IOException, GitAPIException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository gitRepo = builder.setWorkTree(this.repository.getRepositoryRoot().toFile()).setMustExist(false).build();
        String repoUrl = configuration.getRepositoryUrl();
        String branch = configuration.getBranch();
        Git git;
        if (!Files.exists(this.repository.getRepositoryRoot().resolve(".git"))) {
            if (repoUrl != null && !repoUrl.isEmpty()) {
                git = cloneRepository(repoUrl, branch);
            } else {
                gitRepo.create();
                git = new Git(gitRepo);
            }
        } else {
            git = new Git(gitRepo);
        }
        return git;
    }

    Path generateWorkingRepositoryRoot() {
        if (this.repository.getRepositoryRoot().resolve(Constants.DEFAULT_LOCAL_REPO_NAME).toFile().exists()) {
            return this.repository.getRepositoryRoot().resolve(Constants.DEFAULT_LOCAL_REPO_NAME);
        } else {
            return this.repository.getRepositoryRoot();
        }
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

    /**
     * This method is synchronized with an extra static object (meaning all instances are locked). The same lock object
     * is also used in {@link #addCommit(RepositoryFileReference)}. This is to ensure that every commit only has one
     * change.
     *
     * @param message The message that is used in the commit.
     * @throws GitAPIException thrown when anything with adding or committing goes wrong.
     */
    public void addCommit(String message) throws GitAPIException {
        addCommit(new String[] {"."}, message);
    }

    public void addCommit(String[] patterns, String message) throws GitAPIException {
        if (!message.isEmpty()) {
            synchronized (COMMIT_LOCK) {
                try (Git git = getGit()) {
                    AddCommand add = git.add();
                    Status status = git.status().call();

                    for (String pattern : patterns) {
                        add.addFilepattern(pattern);
                    }

                    if (!status.getMissing().isEmpty() || !status.getRemoved().isEmpty()) {
                        RmCommand remove = git.rm();
                        for (String file : Iterables.concat(status.getMissing(), status.getRemoved())) {
                            remove.addFilepattern(file);
                        }
                        remove.call();
                    }

                    add.call();

                    CommitCommand commit = git.commit();
                    commit.setMessage(message);
                    commit.call();
                } catch (IOException e) {
                    LOGGER.error("Error initializing Git.", e);
                }
            }
        }
        postEventMap();
    }

    public void postEventMap() throws GitAPIException {
        Map<DiffEntry, String> diffMap = new HashMap<>();
        try (OutputStream stream = new ByteArrayOutputStream();
             Git git = getGit()) {
            List<DiffEntry> list = git.diff().setOutputStream(stream).call();
            BufferedReader reader = new BufferedReader(new StringReader(stream.toString()));
            for (DiffEntry entry : list) {
                String line = reader.readLine();
                StringWriter diff = new StringWriter();
                while (line != null && !line.startsWith("diff")) {
                    diff.append(line);
                    diff.write('\n');
                    line = reader.readLine();
                }
                diffMap.put(entry, diff.toString());
            }
        } catch (IOException exc) {
            LOGGER.trace("Reading of git information failed!", exc);
        } catch (JGitInternalException gitException) {
            LOGGER.trace("Could not create Diff!", gitException);
        }
        this.eventBus.post(diffMap);
    }

    /**
     * This method is synchronized with an extra static object (meaning all instances are locked). The same lock object
     * is also used in {@link #addCommit(String)}. This is to ensure that every commit only has one change.
     *
     * @param ref RepositoryFileReference to the file that was changed.
     * @throws GitAPIException thrown when anything with adding or committing goes wrong.
     */
    public void addCommit(RepositoryFileReference ref) throws GitAPIException {
        synchronized (COMMIT_LOCK) {
            String message;
            if (ref == null) {
                message = "Files changed externally.";
            } else {
                message = ref.toString() + " was updated";
            }
            addCommit(message);
        }
    }

    private void clean() throws NoWorkTreeException, GitAPIException {
        // remove untracked files
        try (Git git = getGit()) {
            CleanCommand clean = git.clean();
            clean.setCleanDirectories(true);
            clean.call();
        } catch (IOException e) {
            LOGGER.error("Error initializing Git.", e);
        }
    }

    public void cleanAndResetHard() throws NoWorkTreeException, GitAPIException {
        // enable updating by resetting the content of the repository
        this.clean();

        // reset to the latest version
        try (Git git = getGit()) {
            ResetCommand reset = git.reset();
            reset.setMode(ResetType.HARD);
            reset.call();
        } catch (IOException e) {
            LOGGER.error("Error initializing Git.", e);
        }
    }

    public void setRevisionTo(String ref) throws GitAPIException {
        this.clean();

        // reset repository to the desired reference
        try (Git git = getGit()) {
            ResetCommand reset = git.reset();
            reset.setMode(ResetType.HARD);
            reset.setRef(ref);
            reset.call();
        } catch (IOException e) {
            LOGGER.error("Error initializing Git.", e);
        }
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException {
        repository.putContentToFile(ref, inputStream, mediaType);
        try {
            if (configuration.isAutoCommit()) {
                this.addCommit(ref);
            } else {
                postEventMap();
            }
        } catch (GitAPIException e) {
            LOGGER.trace(e.getMessage(), e);
        }
    }

    public boolean hasChangesInFile(RepositoryFileReference ref) {
        try (Git git = getGit()) {
            if (!git.status().call().isClean()) {
                List<DiffEntry> diffEntries = git.diff().call();
                List<DiffEntry> entries = diffEntries.stream()
                    // we use String::startsWith() and RepositoryFileReference::getParent()
                    // because the component is considered changed, if any file of this component is changed.
                    // -> check if any file in the folder is changed
                    .filter(item -> item.getNewPath().startsWith(BackendUtils.getPathInsideRepo(ref.getParent())))
                    .collect(Collectors.toList());
                return entries.size() > 0;
            }
        } catch (GitAPIException e) {
            LOGGER.trace(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error("Error initializing Git.", e);
        }

        return false;
    }

    public Status getStatus() {
        try (Git git = getGit()) {
            return git.status().call();
        } catch (GitAPIException e) {
            LOGGER.trace(e.getMessage(), e);
            return null;
        } catch (IOException e) {
            LOGGER.error("Error initializing Git.", e);
            return null;
        }
    }

    public String getRepositoryUrl() {
        return this.configuration.getRepositoryUrl();
    }

    private Git cloneRepository(String repoUrl, String branch) throws GitAPIException {
        return Git.cloneRepository()
            .setURI(repoUrl)
            .setDirectory(this.repository.getRepositoryRoot().toFile())
            .setBranch(branch)
            .call();
    }

    @Override
    public void setMimeType(RepositoryFileReference ref, MediaType mediaType) throws IOException {
        repository.setMimeType(ref, mediaType);
    }

    @Override
    public boolean exists(GenericId id) {
        return repository.exists(id);
    }

    @Override
    public void forceDelete(RepositoryFileReference ref) throws IOException {
        repository.forceDelete(ref);
    }

    @Override
    public boolean exists(RepositoryFileReference ref) {
        return repository.exists(ref);
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, String content, MediaType mediaType) throws IOException {
        repository.putContentToFile(ref, content, mediaType);
        try {
            if (configuration.isAutoCommit()) {
                this.addCommit(ref);
            } else {
                postEventMap();
            }
        } catch (GitAPIException e) {
            LOGGER.trace(e.getMessage(), e);
        }
    }
    
    @Override
    public void putDefinition(RepositoryFileReference ref, TDefinitions definitions) throws IOException {
        repository.putDefinition(ref, definitions);
        try {
            if (configuration.isAutoCommit()) {
                this.addCommit(ref);
            } else {
                postEventMap();
            }
        } catch (GitAPIException e) {
            LOGGER.trace(e.getMessage(), e);
        }
    }

    @Override
    public InputStream newInputStream(RepositoryFileReference ref) throws IOException {
        return repository.newInputStream(ref);
    }

    @Override
    public TDefinitions definitionsFromRef(RepositoryFileReference ref) throws IOException {
        return repository.definitionsFromRef(ref);
    }

    @Override
    public void getZippedContents(GenericId id, OutputStream out) throws WineryRepositoryException {
        repository.getZippedContents(id, out);
    }

    @Override
    public long getSize(RepositoryFileReference ref) throws IOException {
        return repository.getSize(ref);
    }

    @Override
    public FileTime getLastModifiedTime(RepositoryFileReference ref) throws IOException {
        return repository.getLastModifiedTime(ref);
    }

    @Override
    public String getMimeType(RepositoryFileReference ref) throws IOException {
        return repository.getMimeType(ref);
    }

    @Override
    public Date getLastUpdate(RepositoryFileReference ref) {
        return repository.getLastUpdate(ref);
    }

    @Override
    public <T extends DefinitionsChildId> SortedSet<T> getAllDefinitionsChildIds(Class<T> idClass) {
        return repository.getAllDefinitionsChildIds(idClass);
    }

    @Override
    public <T extends DefinitionsChildId> SortedSet<T> getStableDefinitionsChildIdsOnly(Class<T> idClass) {
        return repository.getStableDefinitionsChildIdsOnly(idClass);
    }

    @Override
    public <T extends DefinitionsChildId> SortedSet<T> getDefinitionsChildIds(Class<T> idClass, boolean omitDevelopmentVersions) {
        return repository.getDefinitionsChildIds(idClass, omitDevelopmentVersions);
    }

    @Override
    public Path ref2AbsolutePath(RepositoryFileReference ref) {
        return repository.ref2AbsolutePath(ref);
    }

    @Override
    public SortedSet<DefinitionsChildId> getAllDefinitionsChildIds() {
        return repository.getAllDefinitionsChildIds();
    }

    @Override
    public <T extends DefinitionsChildId, S extends TExtensibleElements> Map<QName, S> getQNameToElementMapping(Class<T> idClass) {
        return repository.getQNameToElementMapping(idClass);
    }

    @Override
    public <T extends ToscaElementId> SortedSet<T> getNestedIds(GenericId ref, Class<T> idClass) {
        return repository.getNestedIds(ref, idClass);
    }

    @Override
    public SortedSet<RepositoryFileReference> getContainedFiles(GenericId id) {
        return repository.getContainedFiles(id);
    }

    @Override
    public Collection<Namespace> getUsedNamespaces() {
        return repository.getUsedNamespaces();
    }

    @Override
    public Collection<Namespace> getComponentsNamespaces(Class<? extends DefinitionsChildId> clazz) {
        return repository.getComponentsNamespaces(clazz);
    }

    @Override
    public <X extends DefinitionsChildId> Collection<X> getAllElementsReferencingGivenType(Class<X> clazz, QName qNameOfTheType) {
        return repository.getAllElementsReferencingGivenType(clazz, qNameOfTheType);
    }

    @Override
    public Optional<DefinitionsChildId> getDefinitionsChildIdOfParent(HasInheritanceId id) {
        return repository.getDefinitionsChildIdOfParent(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(NodeTypeId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(NodeTypeImplementationId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(RelationshipTypeImplementationId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedTOSCAComponentImplementationArtifactIds(Collection<DefinitionsChildId> ids, TImplementationArtifacts implementationArtifacts, DefinitionsChildId id) {
        return repository.getReferencedTOSCAComponentImplementationArtifactIds(ids, implementationArtifacts, id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(RequirementTypeId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(PolicyTemplateId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(RelationshipTypeId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(ArtifactTemplateId id) throws RepositoryCorruptException {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(ServiceTemplateId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(PatternRefinementModelId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(TopologyFragmentRefinementModelId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(TestRefinementModelId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(ComplianceRuleId id) {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencedDefinitionsChildIds(DefinitionsChildId id) throws RepositoryCorruptException {
        return repository.getReferencedDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(NodeTypeId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(NodeTypeImplementationId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(RelationshipTypeImplementationId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(RelationshipTypeId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(RequirementTypeId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(ArtifactTypeId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(ArtifactTemplateId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(PolicyTemplateId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(PolicyTypeId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(CapabilityTypeId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(GenericImportId id) {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public Collection<DefinitionsChildId> getReferencingDefinitionsChildIds(DefinitionsChildId id) throws RepositoryCorruptException {
        return repository.getReferencingDefinitionsChildIds(id);
    }

    @Override
    public NamespaceManager getNamespaceManager() {
        return repository.getNamespaceManager();
    }

    @Override
    public XsdImportManager getXsdImportManager() {
        return repository.getXsdImportManager();
    }

    @Override
    public void setElement(DefinitionsChildId id, TExtensibleElements element) throws IOException {
        repository.setElement(id, element);
    }

    @Override
    public int getReferenceCount(ArtifactTemplateId id) {
        return repository.getReferenceCount(id);
    }

    @Override
    public Path getRepositoryRoot() {
        return this.workingRepositoryRoot;
    }

    @Override
    public Configuration getConfiguration(GenericId id) {
        return repository.getConfiguration(id);
    }

    @Override
    public Configuration getConfiguration(RepositoryFileReference ref) {
        return repository.getConfiguration(ref);
    }

    @Override
    public Date getConfigurationLastUpdate(GenericId id) {
        return repository.getConfigurationLastUpdate(id);
    }

    @Override
    public TDefinitions getDefinitions(DefinitionsChildId id) {
        return repository.getDefinitions(id);
    }

    @Override
    public <T extends DefinitionsChildId, S extends TExtensibleElements> S getElement(T id) {
        return repository.getElement(id);
    }

    @Override
    public TNodeTypeImplementation getElement(NodeTypeImplementationId id) {
        return repository.getElement(id);
    }

    @Override
    public TRelationshipTypeImplementation getElement(RelationshipTypeImplementationId id) {
        return repository.getElement(id);
    }

    @Override
    public TNodeType getElement(NodeTypeId id) {
        return repository.getElement(id);
    }

    @Override
    public TRelationshipType getElement(RelationshipTypeId id) {
        return repository.getElement(id);
    }

    @Override
    public TServiceTemplate getElement(ServiceTemplateId id) {
        return repository.getElement(id);
    }

    @Override
    public TArtifactTemplate getElement(ArtifactTemplateId id) {
        return repository.getElement(id);
    }

    @Override
    public TArtifactType getElement(ArtifactTypeId id) {
        return repository.getElement(id);
    }

    @Override
    public TPolicyTemplate getElement(PolicyTemplateId id) {
        return repository.getElement(id);
    }

    @Override
    public TCapabilityType getElement(CapabilityTypeId id) {
        return repository.getElement(id);
    }

    @Override
    public TRequirementType getElement(RequirementTypeId id) {
        return repository.getElement(id);
    }

    @Override
    public TPolicyType getElement(PolicyTypeId id) {
        return repository.getElement(id);
    }

    @Override
    public OTComplianceRule getElement(ComplianceRuleId id) {
        return repository.getElement(id);
    }

    @Override
    public OTPatternRefinementModel getElement(PatternRefinementModelId id) {
        return repository.getElement(id);
    }

    @Override
    public OTTopologyFragmentRefinementModel getElement(TopologyFragmentRefinementModelId id) {
        return repository.getElement(id);
    }

    @Override
    public OTTestRefinementModel getElement(TestRefinementModelId id) {
        return repository.getElement(id);
    }

    @Override
    public OTRefinementModel getElement(RefinementId id) {
        return repository.getElement(id);
    }

    @Override
    public void forceDelete(GenericId id) {
        repository.forceDelete(id);
    }

    @Override
    public void rename(DefinitionsChildId oldId, DefinitionsChildId newId) throws IOException {
        repository.rename(oldId, newId);
    }

    @Override
    public void duplicate(DefinitionsChildId from, DefinitionsChildId newId) throws IOException {
        repository.duplicate(from, newId);
    }

    @Override
    public void forceDelete(Class<? extends DefinitionsChildId> definitionsChildIdClazz, Namespace namespace) {
        repository.forceDelete(definitionsChildIdClazz, namespace);
    }

    @Override
    public TEntityType getTypeForTemplate(TEntityTemplate template) {
        return repository.getTypeForTemplate(template);
    }

    @Override
    public void doDump(OutputStream out) throws IOException {
        repository.doDump(out);
    }

    @Override
    public void doClear() {
        repository.doClear();
    }

    @Override
    public void doImport(InputStream in) {
        repository.doImport(in);
    }

    @Override
    public AbstractFileBasedRepository getRepository() {
        return repository;
    }

    @Override
    public void serialize(TDefinitions definitions, OutputStream target) throws IOException {
        repository.serialize(definitions, target);
    }
}
