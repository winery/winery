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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.filebased.management.IRepositoryResolver;
import org.eclipse.winery.repository.backend.filebased.management.RepositoryResolverFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryConfigurationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryConfigurationManager.class);

    private static File repositoriesConfiguration;
    private static JsonNode repoNode = JsonNodeFactory.instance.objectNode();
    private static Map<String, String> repositories = new HashMap<>();

    private final MultiRepository multiRepository;

    public RepositoryConfigurationManager(MultiRepository repository) {
        this.multiRepository = repository;
    }

    public void initRepositoryConfigurationManager() throws IOException {
        repositoriesConfiguration = this.multiRepository.getRepositoryDep().resolve(Filename.FILENAME_JSON_REPOSITORIES).toFile();
        readRepositoriesConfig();
    }

    protected void readRepositoriesConfig() throws IOException {
        if (repositoriesConfiguration.exists()) {
            LOGGER.info("Found Repositories file");
            loadCfg(repositoriesConfiguration);
            loadRepositories();
        } else {
            saveConfiguration(repositoriesConfiguration, repoNode);
        }
        LOGGER.info("Repositories loaded = " + this.multiRepository.getRepositoriesMap().keySet().size());
        this.multiRepository.getRepositoriesMap().keySet().forEach(k -> LOGGER.info("Repository {} loaded.", k.getRepositoryRoot()));
    }

    private void loadRepositories() {
        Map<String, String> tempRepo = new HashMap<>(repositories);
        repositoriesByMap(tempRepo);
        saveConfiguration(repositoriesConfiguration, repoNode);
    }

    private void saveConfiguration(File repoCfg, JsonNode repoNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(repoCfg, repoNode);
        } catch (IOException e) {
            LOGGER.error("Error while saving the configuration", e);
            e.printStackTrace();
        }
    }

    private Map<String, String> loadCfg(File cfg) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        byte[] jsonData = Files.readAllBytes(cfg.toPath());
        JsonNode node = objectMapper.readTree(jsonData);

        Map<String, String> result = new HashMap<>();
        node.fieldNames().forEachRemaining(name -> {
            String repoBranch = node.get(name).toString().replace('"', ' ').trim();
            result.put(name, repoBranch);
            ((ObjectNode) repoNode).put(name, repoBranch);
        });

        repositories.putAll(result);
        return result;
    }

    private void repositoriesByMap(Map<String, String> map) {
        map.forEach((url, branch) -> createRepository(url, branch));
    }

    private void createRepository(String url) {
        createRepository(url, Constants.MASTER_BRANCH);
    }

    private void createRepository(String url, String branch) {
        Optional<IRepositoryResolver> optResolver = RepositoryResolverFactory.getResolver(url, branch);

        if (!optResolver.isPresent()) {
            return;
        }

        IRepositoryResolver resolver = optResolver.get();

        if (!this.multiRepository.getRepositoryUtils().checkRepositoryDuplicate(url)) {
            try {
                String ownerDirectory = URLEncoder.encode(resolver.getRepositoryMaintainerUrl(), "UTF-8");
                File ownerRootFile = new File(multiRepository.getRepositoryRoot().toFile(), ownerDirectory);

                if (!ownerRootFile.exists()) {
                    Files.createDirectories(ownerRootFile.toPath());
                }

                File repositoryLocation = new File(ownerRootFile, resolver.getRepositoryName());
                FilebasedRepository repository = resolver.createRepository(repositoryLocation);

                this.multiRepository.addRepository(repository);

                File cfgFile = new File(repository.getRepositoryDep().toString(), Filename.FILENAME_JSON_REPOSITORIES);
                if (cfgFile.exists()) {
                    repositoriesByMap(loadCfg(cfgFile));
                }
            } catch (IOException | GitAPIException e) {
                LOGGER.error("Error while creating the repository structure");
                e.printStackTrace();
            }
        }
    }
}
