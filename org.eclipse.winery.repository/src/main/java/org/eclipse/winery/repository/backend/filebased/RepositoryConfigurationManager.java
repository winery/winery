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
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.filebased.management.IRepositoryResolver;
import org.eclipse.winery.repository.backend.filebased.management.RepositoryResolverFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryConfigurationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryConfigurationManager.class);

    private static File repositoryConfiguration;
    private static List<RepositoryProperties> repositoriesList = new ArrayList<>();

    public static void addRepositoryToFile(List<RepositoryProperties> repositories) {
        repositoriesList = repositories;
        saveConfiguration();
        IRepository repository = RepositoryFactory.getRepository();
        if (repository instanceof MultiRepository) {
            loadRepositoriesByList((MultiRepository) repository);
        } else {
            throw new UnsupportedOperationException("The addition of the repository was not successful!");
        }
    }

    public static List<RepositoryProperties> getRepositoriesFromFile(MultiRepository repo) {
        if (repoContainsConfigFile()) {
            try {
                readRepositoriesConfig(repo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return repositoriesList;
    }

    private static void readRepositoriesConfig(MultiRepository repo) throws IOException {
        if (repoContainsConfigFile()) {
            LOGGER.info("Found Repositories file");
            loadConfiguration(repositoryConfiguration);
            loadRepositoriesByList(repo);
        } else {
            saveConfiguration();
        }
    }

    private static void saveConfiguration() {
        if (!repoContainsConfigFile()) {
            createRepositoryConfigurationFile();
            try {
                RepositoryFactory.reconfigure();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                objectMapper.writeValue(repositoryConfiguration, repositoriesList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean repoContainsConfigFile() {
        File repo = new File(FilebasedRepository.getActiveRepositoryFilePath(), Filename.FILENAME_JSON_REPOSITORIES);

        if (repo.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private static void loadConfiguration(File configuration) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader reader = objectMapper.readerFor(new TypeReference<List<RepositoryProperties>>() {
        });

        repositoriesList = reader.readValue(configuration);
    }

    private static void loadRepositoriesByList(MultiRepository repo) {
        for (RepositoryProperties repository : repositoriesList) {
            createRepository(repository.getUrl(), repository.getBranch(), repo);
        }
    }

    private static void createRepositoryConfigurationFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        repositoryConfiguration = new File(FilebasedRepository.getActiveRepositoryFilePath(), Filename.FILENAME_JSON_REPOSITORIES);
        try {
            Files.createFile(repositoryConfiguration.toPath());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(repositoryConfiguration, repositoriesList);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void createRepository(String url, String branch, MultiRepository repo) {
        IRepositoryResolver resolver = null;
        if (RepositoryResolverFactory.getResolver(url, branch).isPresent()) {
            resolver = RepositoryResolverFactory.getResolver(url, branch).get();
        }

        if (resolver != null && !RepositoryUtils.checkRepositoryDuplicate(url, repo)) {
            String ownerDirectory;
            File ownerRootFile;
            try {
                ownerDirectory = URLEncoder.encode(resolver.getRepositoryMaintainerUrl(), "UTF-8");
                ownerRootFile = new File(FilebasedRepository.getActiveRepositoryFilePath(), ownerDirectory);

                if (!ownerRootFile.exists()) {
                    Files.createDirectories(ownerRootFile.toPath());
                }

                File repositoryLocation = new File(ownerRootFile, resolver.getRepositoryName());
                FilebasedRepository newSubRepository = resolver.createRepository(repositoryLocation);
                repo.addRepository(newSubRepository);

                File configurationFile = new File(newSubRepository.getRepositoryDep().toString(), Filename.FILENAME_JSON_REPOSITORIES);
                if (configurationFile.exists()) {
                    loadConfiguration(configurationFile);
                    loadRepositoriesByList(repo);
                }
            } catch (IOException | GitAPIException e) {
                LOGGER.error("Error while creating the repository structure");
                e.printStackTrace();
            }
        }
    }

    public static void initialize(MultiRepository multiRepository) throws IOException {
        repositoryConfiguration = new File(multiRepository.getRepositoryDep().toString(), Filename.FILENAME_JSON_REPOSITORIES);
        readRepositoriesConfig(multiRepository);
    }
}
