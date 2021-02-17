/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
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

    private static RepositoryConfigurationObject repositoryConfigurationObject;
    private static GitConfigurationObject gitConfigurationObject;
    private static UiConfigurationObject uiConfigurationObject;
    private static AccountabilityConfigurationObject accountabilityConfigurationObject;
    private static Environments instance;
    private static ArrayList<ConfigurationChangeListener> configurationChangeListeners = new ArrayList<>();

    private Environments() {
        accountabilityConfigurationObject = new AccountabilityConfigurationObject(Environment.getInstance().getConfiguration());
        gitConfigurationObject = new GitConfigurationObject(Environment.getInstance().getConfiguration());
        repositoryConfigurationObject = new RepositoryConfigurationObject(Environment.getInstance().getConfiguration(), gitConfigurationObject);
        uiConfigurationObject = new UiConfigurationObject(Environment.getInstance().getConfiguration());
    }

    public static Environments getInstance() {
        if (Objects.isNull(instance)) {
            instance = new Environments();
            initializeConfigurationObjects();
        }
        return instance;
    }

    static void updateInstances(YAMLConfiguration updatedConfiguration) {
        repositoryConfigurationObject.update(updatedConfiguration);
        gitConfigurationObject.update(updatedConfiguration);
        uiConfigurationObject.update(updatedConfiguration);
        accountabilityConfigurationObject.update(updatedConfiguration);
    }

    static void initializeConfigurationObjects() {
        uiConfigurationObject.initialize();
        repositoryConfigurationObject.initialize();
        gitConfigurationObject.initialize();
        accountabilityConfigurationObject.initialize();
    }

    /**
     * Returns an instance of the ui configuration.
     *
     * @return Returns an UiConfigurationObject object which represents the ui configuration of the winery.yml
     * configuration file.
     */
    public UiConfigurationObject getUiConfig() {
        checkForUpdateAndUpdateInstances();
        if (uiConfigurationObject == null) {
            uiConfigurationObject = new UiConfigurationObject(Environment.getInstance().getConfiguration());
        }
        return uiConfigurationObject;
    }

    /**
     * Returns an instance of the git configuration.
     *
     * @return Returns a GitConfigurationObject object which represents the git configuration of the winery.yml
     * configuration file.
     */
    public GitConfigurationObject getGitConfig() {
        checkForUpdateAndUpdateInstances();
        if (gitConfigurationObject == null) {
            gitConfigurationObject = new GitConfigurationObject(Environment.getInstance().getConfiguration());
        }
        return gitConfigurationObject;
    }

    /**
     * Returns an instance of the repository configuration. This includes the GitConfigurationObject
     *
     * @return Returns a RepositoryConfigurationObject object which represents the repository configuration of the
     * winery.yml configuration file.
     */
    public RepositoryConfigurationObject getRepositoryConfig() {
        checkForUpdateAndUpdateInstances();
        if (repositoryConfigurationObject == null) {
            repositoryConfigurationObject = new RepositoryConfigurationObject(Environment.getInstance().getConfiguration(),
                getGitConfig());
        }
        return repositoryConfigurationObject;
    }

    /**
     * Returns an instance of the ui configuration.
     *
     * @return Returns an AccountabilityConfigurationObject object which represents the accountability configuration of
     * the winery.yml configuration file.
     */
    public AccountabilityConfigurationObject getAccountabilityConfig() {
        checkForUpdateAndUpdateInstances();
        if (accountabilityConfigurationObject == null) {
            accountabilityConfigurationObject = new AccountabilityConfigurationObject(Environment.getInstance().getConfiguration());
        }
        return accountabilityConfigurationObject;
    }

    /**
     * Method to retrieve the set version.
     *
     * @return the version declared in the pom file in case of an exception returns the version 0.0.0
     */
    public String getVersion() {
        try {
            return this.getVersionFromProperties();
        } catch (IOException e) {
            LOGGER.debug("Error while retrieving version from pom.", e);
        }
        return "0.0.0";
    }

    /**
     * Checks the configuration file for an update and updates the configuration object instances, so that they will be
     * up to date with the new changes
     */
    private void checkForUpdateAndUpdateInstances() {
        if (Environment.getInstance().checkConfigurationForUpdate()) {
            Environment.getInstance().updateConfig();
            Environments.updateInstances(Environment.getInstance().getConfiguration());
            afterUpdateNotify();
        }
    }

    public void addConfigurationChangeListener(ConfigurationChangeListener newListener) {
        configurationChangeListeners.add(newListener);
    }

    public void removeConfigurationChangeListener(ConfigurationChangeListener toRemoveListener) {
        configurationChangeListeners.remove(toRemoveListener);
    }

    private void afterUpdateNotify() {
        configurationChangeListeners.forEach(ConfigurationChangeListener::update);
    }

    /**
     * Returns a FileBasedRepositoryConfiguration
     *
     * @return an instance of FileBasedRepositoryConfiguration
     */
    public FileBasedRepositoryConfiguration getFilebasedRepositoryConfiguration() {
        Path path = Paths.get(this.getRepositoryConfig().getRepositoryRoot());
        return new FileBasedRepositoryConfiguration(path);
    }

    /**
     * Returns a GitBasedRepositoryConfiguration
     *
     * @return an instance of GitBasedRepositoryConfiguration
     */
    public GitBasedRepositoryConfiguration getGitBasedRepositoryConfiguration() {
        final FileBasedRepositoryConfiguration filebasedRepositoryConfiguration = getFilebasedRepositoryConfiguration();
        return new GitBasedRepositoryConfiguration(this.getGitConfig().isAutocommit(), filebasedRepositoryConfiguration);
    }

    /**
     * Changes the configuration accordingly to the given ConfigurationObject.
     *
     * @param configuration the changed configuration object
     */
    public static void save(final AbstractConfigurationObject configuration) {
        configuration.save();
    }

    private String getVersionFromProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("version.properties"));
        return properties.getProperty("version");
    }

    /**
     * Invokes the reloadAccountabilityConfiguration method from Environment.
     */
    public static void resetAccountabilityConfiguration() throws IOException {
        try (InputStream inputStream = Environment.getDefaultConfigInputStream()) {
            Environment.getInstance().reloadAccountabilityConfiguration(inputStream);
        }
    }

    public static boolean isFeatureEnabled(String name) {
        Boolean value = getInstance().getUiConfig().getFeatures().get(name);
        if (value == null) {
            return false;
        }
        return value;
    }
}
