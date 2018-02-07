/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.repository.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.repository.configuration.JCloudsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class RepositoryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryFactory.class);

    private static GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration = null;
    private static FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = null;
    private static JCloudsConfiguration jCloudsConfiguration;

    private static IRepository repository = null;

    public static void reconfigure(GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration) throws IOException, GitAPIException {
        RepositoryFactory.gitBasedRepositoryConfiguration = gitBasedRepositoryConfiguration;
        RepositoryFactory.fileBasedRepositoryConfiguration = null;
        RepositoryFactory.jCloudsConfiguration = null;

        repository = new GitBasedRepository(gitBasedRepositoryConfiguration);
    }

    public static void reconfigure(FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration) {
        RepositoryFactory.fileBasedRepositoryConfiguration = fileBasedRepositoryConfiguration;
        RepositoryFactory.gitBasedRepositoryConfiguration = null;
        RepositoryFactory.jCloudsConfiguration = null;

        repository = new FilebasedRepository(fileBasedRepositoryConfiguration);
    }

    public static void reconfigure(JCloudsConfiguration jCloudsConfiguration) {
        RepositoryFactory.jCloudsConfiguration = jCloudsConfiguration;
        RepositoryFactory.fileBasedRepositoryConfiguration = null;
        RepositoryFactory.gitBasedRepositoryConfiguration = null;

        // TODO
    }

    /**
     * Reconfigures based on Environment
     */
    public static void reconfigure() throws Exception {
        final Optional<JCloudsConfiguration> jCloudsConfiguration = Environment.getJCloudsConfiguration();
        if (jCloudsConfiguration.isPresent()) {
            reconfigure(jCloudsConfiguration.get());
        } else {
            final Optional<GitBasedRepositoryConfiguration> gitBasedRepositoryConfiguration = Environment.getGitBasedRepositoryConfiguration();
            final FileBasedRepositoryConfiguration filebasedRepositoryConfiguration = Environment.getFilebasedRepositoryConfiguration().orElse(new FileBasedRepositoryConfiguration());

            // Determine whether the filebased repository could be git repository.
            // We do not use JGit's capabilities, but do it just by checking for the existance of a ".git" directory.
            final Path repositoryRoot = FilebasedRepository.getRepositoryRoot(filebasedRepositoryConfiguration);
            final Path gitDirectory = repositoryRoot.resolve(".git");
            boolean isGit = (Files.exists(gitDirectory) && Files.isDirectory(gitDirectory));

            if (gitBasedRepositoryConfiguration.isPresent()) {
                reconfigure(gitBasedRepositoryConfiguration.get());
            } else if (isGit) {
                reconfigure(new GitBasedRepositoryConfiguration(false, filebasedRepositoryConfiguration));
            } else {
                reconfigure(filebasedRepositoryConfiguration);
            }
        }
    }

    public static IRepository getRepository() {
        if ((gitBasedRepositoryConfiguration == null) && (fileBasedRepositoryConfiguration == null) && (jCloudsConfiguration == null)) {
            // in case nothing is configured, use the file-based repository as fallback
            LOGGER.debug("No repository configuration available. Using default configuration.");
            reconfigure(new FileBasedRepositoryConfiguration());
        }
        return repository;
    }

    /**
     * Generates a new IRepository working on the specified path. No git support, just plain file system
     */
    public static IRepository getRepository(Path path) {
        Objects.requireNonNull(path);
        FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = new FileBasedRepositoryConfiguration(path);
        return getRepository(fileBasedRepositoryConfiguration);
    }

    public static IRepository getRepository(FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration) {
        // FIXME: currently, the CSAR export does not reuse the repository instance returned here. Thus, we have to reconfigure the repository.
        // This should be fixed by always passing IRepository when working with the repository
        reconfigure(fileBasedRepositoryConfiguration);
        return new FilebasedRepository(Objects.requireNonNull(fileBasedRepositoryConfiguration));
    }
}
