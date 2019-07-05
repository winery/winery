/*******************************************************************************
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

package org.eclipse.winery.common.configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Class offers Methods for the backend to interact with the Configuration of the winery. There are Methods for
 * either reading or editing certain parts of the configuration file. This Class does not support the addition of
 * properties that are not part of the configuration by default.
 */
public final class Environments {
    private static final Logger LOGGER = LoggerFactory.getLogger(Environments.class);
    private final static String featurePrefix = "ui.features.";
    private final static String endpointPrefix = "ui.endpoints.";
    private final static String gitPrefix = "repository.git.";
    private final static String repositoryPrefix = "repository.";

    private Environments() {
    }

    /**
     * Returns an ConfigurationObject Instance representing the configuration of the configuration - class attribute of
     * Environment.
     *
     * @return Returns a configuration object which has the same structure as the winery.yml configuration file.
     */
    public static ConfigurationObject get() {
        YAMLConfiguration configuration = Environment.getConfiguration();
        HashMap<String, Boolean> features = new HashMap<>();
        HashMap<String, String> endpoints = new HashMap<>();
        Iterator<String> featureIterator = configuration.getKeys(featurePrefix);
        Iterator<String> endpointIterator = configuration.getKeys(endpointPrefix);
        featureIterator.forEachRemaining(key -> features.put(key.replace(featurePrefix, ""), configuration.getBoolean((key))));
        endpointIterator.forEachRemaining(key -> endpoints.put(key.replace(endpointPrefix, ""), configuration.getString(key)));
        return new ConfigurationObject(features, endpoints);
    }

    /**
     * This method returns a map containing the values set in the configuration for: clientID, clientSecret, username,
     * password
     */
    public static HashMap<String, String> getGit() {
        HashMap<String, String> git = new HashMap<>();
        YAMLConfiguration configuration = Environment.getConfiguration();
        configuration.getKeys(gitPrefix).forEachRemaining(key -> {
            if (!key.equals(gitPrefix + "autocommit"))
                git.put(key.replace(gitPrefix, ""), configuration.getString(key));
        });
        return git;
    }

    /**
     * Returns the status of the AutoCommit flag
     *
     * @return status of auto commit flag, either true or false
     */
    public static boolean isAutoCommit() {
        return Environment.getConfiguration().getBoolean(gitPrefix + "autocommit");
    }

    /**
     * When no error occurs, this method returns the version specified in the pom file. Otherwise null is returned. If
     * an error occurs the version 0.0.0 is returned.
     */
    public static String getVersion() {
        try {
            return new Environments().getVersionFromProperties();
        } catch (IOException e) {
            LOGGER.debug("Error while retrieving version from pom.", e);
        }
        return "0.0.0";
    }

    /**
     * Returns the path to the repositiory saved in the configuration file.
     *
     * @return path to configuration
     */
    public static String getRepositoryRoot() {
        if (Environment.getConfiguration().getString(repositoryPrefix + "repositoryRoot").equals("")) {
            return null;
        } else {
            return Environment.getConfiguration().getString(repositoryPrefix + "repositoryRoot");
        }
    }

    /**
     * Returns a FileBasedRepositoryConfiguration
     *
     * @return an instance of FileBasedRepositoryConfiguration
     */
    public static Optional<FileBasedRepositoryConfiguration> getFilebasedRepositoryConfiguration() {
        String repositoryRoot = getRepositoryRoot();
        if (repositoryRoot != null) {
            final Path path = Paths.get(repositoryRoot);
            return Optional.of(new FileBasedRepositoryConfiguration(path));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns a GitBasedRepositoryConfiguration
     *
     * @return an instance of GitBasedRepositoryConfiguration
     */
    public static Optional<GitBasedRepositoryConfiguration> getGitBasedRepsitoryConfiguration() {
        final FileBasedRepositoryConfiguration filebasedRepositoryConfiguration = getFilebasedRepositoryConfiguration().orElse(new FileBasedRepositoryConfiguration());
        return Optional.of(new GitBasedRepositoryConfiguration(isAutoCommit(), filebasedRepositoryConfiguration));
    }

    /**
     * This method propagates changes made to the feature flags to the config file.
     *
     * @param changedProperties a Map that contains the name of the changed properties as keys and the changed flags as
     *                          values as
     */
    public static void saveFeatures(final ConfigurationObject changedProperties) {
        YAMLConfiguration config = Environment.getConfiguration();
        changedProperties.getFeatures().keySet().forEach(property -> config.setProperty(featurePrefix + property, changedProperties.getFeatures().get(property)));
        Environment.save();
    }

    /**
     * Sets the repositoryRoot property to the value of the given path.
     *
     * @param repositoryRoot The path wich represents the new repositoryRoot
     */
    public static void setRepositoryRoot(String repositoryRoot) {
        YAMLConfiguration config = Environment.getConfiguration();
        config.setProperty(repositoryPrefix + "repositoryRoot", repositoryRoot);
        Environment.save();
    }

    /**
     * This methods propagates changes made to the endpoints to the config file.
     *
     * @param changedProperties a Map that contains the name of the changed properties as keys and the changed flags as
     *                          values as
     */
    public static void saveEndpoints(final ConfigurationObject changedProperties) {
        YAMLConfiguration config = Environment.getConfiguration();
        changedProperties.getEndpoints().keySet().forEach(property -> config.setProperty(endpointPrefix + property, changedProperties.getEndpoints().get(property)));
        Environment.save();
    }

    private String getVersionFromProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("version.properties"));
        return properties.getProperty("version");
    }
}
