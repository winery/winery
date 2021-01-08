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
package org.eclipse.winery.repository.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.filebased.MultiRepository;
import org.eclipse.winery.repository.xml.XmlRepository;
import org.eclipse.winery.repository.yaml.YamlRepository;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryFactory.class);

    private static GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration = null;
    private static FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration = null;

    private static IRepository repository = null;

    private static boolean repositoryContainsRepoConfig(FileBasedRepositoryConfiguration config) {
        if (config.getRepositoryPath().isPresent()) {
            return new File(config.getRepositoryPath().get().toString(), Filename.FILENAME_JSON_REPOSITORIES).exists();
        } else {
            return new File(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(), Filename.FILENAME_JSON_REPOSITORIES).exists();
        }
    }

    public static AbstractFileBasedRepository createXmlOrYamlRepository(FileBasedRepositoryConfiguration configuration, Path repositoryRoot) {
        if (RepositoryConfigurationObject.RepositoryProvider.YAML.equals(configuration.getRepositoryProvider())) {
            return new YamlRepository(repositoryRoot);
        } else {
            return new XmlRepository(repositoryRoot);
        }
    }

    public static void reconfigure(GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration) throws IOException, GitAPIException {
        RepositoryFactory.gitBasedRepositoryConfiguration = gitBasedRepositoryConfiguration;
        RepositoryFactory.fileBasedRepositoryConfiguration = null;
        AbstractFileBasedRepository compositeRepository;
        // if a repository root is specified, use it instead of the root specified in the config
        if (gitBasedRepositoryConfiguration.getRepositoryPath().isPresent()) {
            compositeRepository = createXmlOrYamlRepository(gitBasedRepositoryConfiguration, gitBasedRepositoryConfiguration.getRepositoryPath().get());
        } else {
            compositeRepository = createXmlOrYamlRepository(fileBasedRepositoryConfiguration, Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot()));
        }
        if (repositoryContainsRepoConfig(gitBasedRepositoryConfiguration)) {
            repository = new MultiRepository(compositeRepository.getRepositoryRoot());
        } else {
            repository = new GitBasedRepository(gitBasedRepositoryConfiguration, compositeRepository);
        }
    }

    public static void reconfigure(FileBasedRepositoryConfiguration fileBasedRepositoryConfiguration) {
        RepositoryFactory.fileBasedRepositoryConfiguration = fileBasedRepositoryConfiguration;
        RepositoryFactory.gitBasedRepositoryConfiguration = null;
        AbstractFileBasedRepository compositeRepository;
        // if a repository root is specified by the configuration use it instead of the root specified in the configuration
        if (fileBasedRepositoryConfiguration.getRepositoryPath().isPresent()) {
            compositeRepository = createXmlOrYamlRepository(fileBasedRepositoryConfiguration, fileBasedRepositoryConfiguration.getRepositoryPath().get());
        } else {
            compositeRepository = createXmlOrYamlRepository(fileBasedRepositoryConfiguration, Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot()));
        }
        if (repositoryContainsRepoConfig(fileBasedRepositoryConfiguration)) {
            try {
                repository = new MultiRepository(compositeRepository.getRepositoryRoot());
            } catch (IOException | GitAPIException exception) {
                exception.printStackTrace();
            }
        } else {
            repository = compositeRepository;
        }
    }

    /**
     * Reconfigures based on Environment
     */
    public static void reconfigure() throws Exception {
        final GitBasedRepositoryConfiguration gitBasedRepositoryConfiguration = Environments.getInstance().getGitBasedRepositoryConfiguration();
        reconfigure(gitBasedRepositoryConfiguration);
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
        // FIXME: currently, the CSAR export does not reuse the repository instance returned here. 
        //  Thus, we have to reconfigure the repository.
        //  This should be fixed by always passing IRepository when working with the repository
        reconfigure(fileBasedRepositoryConfiguration);
        if (fileBasedRepositoryConfiguration.getRepositoryPath().isPresent()) {
            return createXmlOrYamlRepository(fileBasedRepositoryConfiguration, fileBasedRepositoryConfiguration.getRepositoryPath().get());
        } else {
            return createXmlOrYamlRepository(fileBasedRepositoryConfiguration, Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot()));
        }
    }
}
