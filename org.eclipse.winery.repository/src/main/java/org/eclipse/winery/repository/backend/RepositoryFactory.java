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

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.filebased.MultiRepository;
import org.eclipse.winery.repository.filebased.TenantRepository;
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

    public static boolean repositoryContainsMultiRepositoryConfiguration(FileBasedRepositoryConfiguration config) {
        return repositoryContainsRepoConfig(config, Filename.FILENAME_JSON_MUTLI_REPOSITORIES);
    }

    public static boolean repositoryContainsRepoConfig(FileBasedRepositoryConfiguration config, String fileName) {
        if (config.getRepositoryPath().isPresent()) {
            return new File(config.getRepositoryPath().get().toString(), fileName).exists();
        } else {
            return new File(Environments.getInstance().getRepositoryConfig().getRepositoryRoot(), fileName).exists();
        }
    }

    public static AbstractFileBasedRepository createXmlOrYamlRepository(FileBasedRepositoryConfiguration configuration, Path repositoryRoot) {
        return createXmlOrYamlRepository(configuration, repositoryRoot, Constants.DEFAULT_LOCAL_REPO_NAME);
    }

    public static AbstractFileBasedRepository createXmlOrYamlRepository(FileBasedRepositoryConfiguration configuration, Path repositoryRoot, String id) {
        if (RepositoryConfigurationObject.RepositoryProvider.YAML.equals(configuration.getRepositoryProvider())) {
            return new YamlRepository(repositoryRoot, id);
        } else {
            return new XmlRepository(repositoryRoot, id);
        }
    }

    public static void reconfigure(GitBasedRepositoryConfiguration configuration) throws IOException, GitAPIException {
        RepositoryFactory.gitBasedRepositoryConfiguration = configuration;
        RepositoryFactory.fileBasedRepositoryConfiguration = null;

        if (repositoryContainsMultiRepositoryConfiguration(configuration)) {
            repository = new MultiRepository(configuration.getRepositoryPath().get());
        } else if (Environments.getInstance().getRepositoryConfig().isTenantRepository()) {
            repository = new TenantRepository(configuration.getRepositoryPath().get());
        } else {
            // if a repository root is specified, use it instead of the root specified in the config
            AbstractFileBasedRepository localRepository = configuration.getRepositoryPath().isPresent()
                ? createXmlOrYamlRepository(configuration, configuration.getRepositoryPath().get())
                : createXmlOrYamlRepository(configuration, Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot()));
            repository = new GitBasedRepository(configuration, localRepository);
        }
    }

    public static void reconfigure(FileBasedRepositoryConfiguration configuration) {
        RepositoryFactory.fileBasedRepositoryConfiguration = configuration;
        RepositoryFactory.gitBasedRepositoryConfiguration = null;

        if (repositoryContainsMultiRepositoryConfiguration(configuration)) {
            try {
                repository = new MultiRepository(configuration.getRepositoryPath().get());
            } catch (IOException | GitAPIException e) {
                LOGGER.error("Error while initializing Multi-Repository!");
            }
        } else if (Environments.getInstance().getRepositoryConfig().isTenantRepository()) {
            try {
                repository = new TenantRepository(configuration.getRepositoryPath().get());
            } catch (IOException | GitAPIException e) {
                LOGGER.error("Error while initializing Tenant-Repository");
            }
        } else {
            // if a repository root is specified, use it instead of the root specified in the config
            repository = configuration.getRepositoryPath().isPresent()
                ? createXmlOrYamlRepository(configuration, configuration.getRepositoryPath().get())
                : createXmlOrYamlRepository(configuration, Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot()));
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
     * Generates a new IRepository working on the specified path.
     */
    public static IRepository getRepository(Path path) {
        Objects.requireNonNull(path);
        GitBasedRepositoryConfiguration config = new GitBasedRepositoryConfiguration(
            false,
            new FileBasedRepositoryConfiguration(path)
        );
        try {
            reconfigure(config);
        } catch (IOException | GitAPIException e) {
            LOGGER.error("Error while reconfiguring the repository", e);
        }
        return repository;
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
