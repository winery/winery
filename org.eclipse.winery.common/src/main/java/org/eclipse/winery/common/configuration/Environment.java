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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.Objects;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an interface to interact with the configuration file of the winery. It contains methods to easily
 * read the current configuration of the winery and methods to change certain configurations even while the winery is
 * running.
 */
final class Environment {

    //InputStream that accesses the defaultConfiguration from the resources
    private static final String defaultConfigName = "winery.yml";
    private static final String testConfigName = "wineryTest.yml";
    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);
    //Path to the directory of the winery configuration file
    private static final File configDirectory = new File(System.getProperty("user.home") + "/.winery");
    //Path of the winery configuration file
    private static final File configFile = new File(configDirectory + "/" + defaultConfigName);
    private static Environment instance;
    //Contains the configuration
    private YAMLConfiguration configuration = null;
    private FileTime lastChange = null;

    private Environment() {
    }

    protected static Environment getInstance() {
        if (Objects.isNull(instance)) {
            instance = new Environment();
        }
        return instance;
    }

    /**
     * Opens an input stream to the default configuration file.
     */
    static InputStream getDefaultConfigInputStream() {
        return Environment.class.getClassLoader().getResourceAsStream(defaultConfigName);
    }

    /**
     * Opens and returns an input stream to the test configuration file.
     */
    static InputStream getTestConfigInputStream() {
        return Environment.class.getClassLoader().getResourceAsStream(testConfigName);
    }

    /**
     * Getter for the configuration attribute, if class attribute is null, it is set to the configuration in the
     * winery.yml file.
     *
     * @return the configuration attribute of Environment
     */
    protected YAMLConfiguration getConfiguration() {
        if (Objects.isNull(this.configuration)) {
            this.getConfigFromFile();
        }
        return this.configuration;
    }

    /**
     * Checks for updates on the configuration. If the configuration wasn't loaded yet (lastChange == null) the check
     * will return true;
     *
     * @return true if the configuration in the file is updated or not loaded yet
     */
    boolean checkConfigurationForUpdate() {
        try {
            if (lastChange == null) {
                return true;
            } else
                return !lastChange.equals(Files.getLastModifiedTime(configFile.toPath()));
        } catch (IOException ioex) {
            LOGGER.debug("Error while accessing the last changed time.", ioex);
        }
        return false;
    }

    /**
     * Sets the Class attribute to the configuration parameter. This is only used in testing, to set the configuration
     * back to the original state, before the test.
     */
    protected void setConfiguration(YAMLConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Checks first, if the winery.yml file is in the config directory of the winery. If it is, it just returns a
     * Configuration Instance of the existing winery.yml file. Else it calls createDefaultConfigFile(). Is protected for
     * testing purposes.
     */
    private void getConfigFromFile() {
        if (!configFile.exists()) {
            copyDefaultConfigFile();
        }
        try {
            lastChange = Files.getLastModifiedTime(configFile.toPath());
        } catch (IOException ioex) {
            LOGGER.debug("Error while accessing the last changed time.", ioex);
        }
        YAMLConfiguration config = null;
        try (FileReader reader = new FileReader(configFile)) {
            config = new YAMLConfiguration();
            config.read(reader);
        } catch (ConfigurationException | IOException ex) {
            LOGGER.debug("Error while reading configuration file.", ex);
        }
        this.configuration = config;
    }

    /**
     * Method copies the default winery.yml file (all flags are true) into the config directory of the winery in
     * HOME/.winery. If the directory doesn't exist it will be created.
     */
    private void copyDefaultConfigFile() {
        try (InputStream defaultConfigInputStream = getDefaultConfigInputStream()) {
            if (defaultConfigInputStream == null) {
                throw new NullPointerException();
            }
            FileUtils.copyInputStreamToFile(defaultConfigInputStream, Environment.configFile);
        } catch (IOException | NullPointerException ex) {
            LOGGER.debug("Error while copying the default config file into the winery directory", ex);
        }
    }

    /**
     * This method resets the accountability part of the configuration by copying the default configuration to a file
     * and reading the accountability values and replacing them
     */
    void reloadAccountabilityConfiguration(InputStream defaultConfigInputStream) {
        File tempFile = new File(configDirectory + "/wineryAccountability.yml");
        if (!configFile.exists()) {
            copyDefaultConfigFile();
        } else {
            try {
                FileUtils.copyInputStreamToFile(defaultConfigInputStream, tempFile);
            } catch (IOException e) {
                LOGGER.debug("Error copying default file.", e);
            }
            YAMLConfiguration defaultAccountabilityConfiguration;
            try (FileReader reader = new FileReader(tempFile)) {
                String accountabilityPrefix = "accountability";
                defaultAccountabilityConfiguration = new YAMLConfiguration();
                defaultAccountabilityConfiguration.read(reader);
                YAMLConfiguration currentConfiguration = this.getConfiguration();
                Iterator<String> accountabilityAttributes = defaultAccountabilityConfiguration.getKeys(accountabilityPrefix);
                accountabilityAttributes.forEachRemaining(key -> {
                    currentConfiguration.setProperty(key,
                        defaultAccountabilityConfiguration.getString(key));
                });
                this.save();
            } catch (ConfigurationException | IOException ex) {
                LOGGER.debug("Error while reading configuration file.", ex);
            }
            tempFile.delete();
        }
    }

    /**
     * This method writes the current configuration of the configuration attribute into the winery.yml file.
     */
    protected void save() {
        try (FileWriter writer = new FileWriter(configFile)) {
            this.getConfiguration().write(writer);
        } catch (IOException | ConfigurationException ioex) {
            LOGGER.debug("Error while saving the config file.", ioex);
        }
    }

    /**
     * Reloads configuration from file.
     */
    void updateConfig() {
        this.getConfigFromFile();
    }
}
