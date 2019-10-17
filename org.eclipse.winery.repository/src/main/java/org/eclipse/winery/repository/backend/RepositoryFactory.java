/*******************************************************************************
 * Copyright (c) 2012-2019 Contributors to the Eclipse Foundation
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.backend.filebased.MultiRepository;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryFactory {

    public static List<FilebasedRepository> repositoryList = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryFactory.class);

    private static GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration = null;
    private static FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = null;

    private static IRepository repository = null;

    private static boolean repositoryContainsRepoConfig(FileBasedRepositoryConfiguration config) {
        return FilebasedRepository.getRepositoryRoot(config).resolve(Filename.FILENAME_JSON_REPOSITORIES).toFile().exists();
    }

    public static void reconfigure(GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration) throws IOException, GitAPIException {
        RepositoryFactory.gitBasedRepositoryConfiguration = gitBasedRepositoryConfiguration;
        RepositoryFactory.fileBasedRepositoryConfiguration = null;

        if (repositoryContainsRepoConfig(gitBasedRepositoryConfiguration)) {
            repository = new MultiRepository(gitBasedRepositoryConfiguration);
        } else {
            repository = new GitBasedRepository(gitBasedRepositoryConfiguration);
        }
    }

    public static void reconfigure(FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration) {
        RepositoryFactory.fileBasedRepositoryConfiguration = fileBasedRepositoryConfiguration;
        RepositoryFactory.gitBasedRepositoryConfiguration = null;

        if (repositoryContainsRepoConfig(fileBasedRepositoryConfiguration)) {
            try {
                repository = new MultiRepository(new GitBasedRepositoryConfiguration(false, fileBasedRepositoryConfiguration));
            } catch (IOException | GitAPIException exception) {
                exception.printStackTrace();
            }
        } else {
            repository = new FilebasedRepository(fileBasedRepositoryConfiguration);
        }
    }

    /**
     * Reconfigures based on Environment
     */
    public static void reconfigure() throws Exception {
        final Optional<GitBasedRepositoryConfiguration> gitBasedRepositoryConfiguration = Environments.getGitBasedRepsitoryConfiguration();
        final FileBasedRepositoryConfiguration filebasedRepositoryConfiguration = Environments.getFilebasedRepositoryConfiguration();

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

    public static IRepository getRepository() {
        if ((gitBasedRepositoryConfiguration == null) && (fileBasedRepositoryConfiguration == null)) {
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
