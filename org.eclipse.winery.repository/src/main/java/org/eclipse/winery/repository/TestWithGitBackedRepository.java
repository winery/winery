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
package org.eclipse.winery.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class resides in the main package and not in test, because other modules (such as the REST module) also rely on
 * this test class.
 */
public abstract class TestWithGitBackedRepository {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestWithGitBackedRepository.class);

    public final Path repositoryPath;

    public final IRepository repository;

    public final Git git;

    /**
     * Initializes the git repository from https://github.com/winery/test-repository into %TEMP%/test-repository
     *
     * @throws RuntimeException wraps an Exception
     */
    public TestWithGitBackedRepository() {
        this(RepositoryConfigurationObject.RepositoryProvider.FILE);
    }

    protected TestWithGitBackedRepository(RepositoryConfigurationObject.RepositoryProvider provider) {
        this.repositoryPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("test-repository");
        String remoteUrl = "https://github.com/winery/test-repository.git";

        try {
            LOGGER.debug("Testing with repository directory {}", repositoryPath);

            if (!Files.exists(repositoryPath)) {
                Files.createDirectory(repositoryPath);
            }

            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            if (!Files.exists(repositoryPath.resolve(".git"))) {
                FileUtils.cleanDirectory(repositoryPath.toFile());
                this.git = Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setBare(false)
                    .setCloneAllBranches(true)
                    .setDirectory(repositoryPath.toFile())
                    .call();
            } else {
                Repository gitRepo = builder.setWorkTree(repositoryPath.toFile()).setMustExist(false).build();
                this.git = new Git(gitRepo);
                try {
                    this.git.fetch().call();
                } catch (TransportException e) {
                    // we ignore it to enable offline testing
                    LOGGER.debug("Working in offline mode", e);
                }
            }

            // inject the current path to the repository factory
            FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = new FileBasedRepositoryConfiguration(repositoryPath, provider);
            // force xml repository provider
            fileBasedRepositoryConfiguration.setRepositoryProvider(provider);
            GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration = new GitBasedRepositoryConfiguration(false, fileBasedRepositoryConfiguration);
            RepositoryFactory.reconfigure(gitBasedRepositoryConfiguration);

            this.repository = RepositoryFactory.getRepository();
            LOGGER.debug("Initialized test repository");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void setRevisionTo(String ref) throws GitAPIException {
        git.clean().setForce(true).setCleanDirectories(true).call();
        git.reset()
            .setMode(ResetCommand.ResetType.HARD)
            .setRef(ref)
            .call();
        LOGGER.debug("Switched to commit {}", ref);
    }

    protected void makeSomeChanges(NodeTypeId id) throws Exception {
        IRepository repo = RepositoryFactory.getRepository();
        TNodeType element = repo.getElement(id);
        TTopologyElementInstanceStates states = new TTopologyElementInstanceStates();
        TTopologyElementInstanceStates.InstanceState instanceState = new TTopologyElementInstanceStates.InstanceState();
        instanceState.setState("mySuperExtraStateWhichNobodyWouldHaveGuessed");
        states.getInstanceState().add(instanceState);
        element.setInstanceStates(states);
        BackendUtils.persist(repo, id, element);
    }
}
