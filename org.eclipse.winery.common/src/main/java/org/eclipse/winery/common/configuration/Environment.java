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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an interface to interact with the configuration file of the winery. It contains methods to easily
 * read the current configuration of the winery and methods to change certain configurations even while the winery is
 * running.
 */
final class Environment {

    static final String TEST_CONFIG_NAME = "wineryTest.yml";
    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    private static final String DEFAULT_CONFIG_NAME = "winery.yml";
    // Visible for test classes
    private static final File DEFAULT_CONFIG_DIRECTORY = new File(System.getProperty("user.home"), ".winery");
    private static final File DEFAULT_CONFIG_FILE = new File(DEFAULT_CONFIG_DIRECTORY, DEFAULT_CONFIG_NAME);
    private static final File LINUX_CONFIG_FILE = new File("/opt/winery/.winery", DEFAULT_CONFIG_NAME);

    private static Environment instance;

    private File configFile;
    private FileTime lastChange;
    private YAMLConfiguration config;

    private Environment() {
    }

    protected static Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    public File getConfigDirectory() {
        if (configFile == null) {
            this.getConfigFromFile();
        }
        return configFile.getParentFile();
    }

    /**
     * Opens an input stream to the default configuration file.
     */
    static InputStream getDefaultConfigInputStream() {
        return Environment.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_NAME);
    }

    /**
     * Opens and returns an input stream to the test configuration file.
     */
    static InputStream getTestConfigInputStream() {
        return Environment.class.getClassLoader().getResourceAsStream(TEST_CONFIG_NAME);
    }

    /**
     * Getter for the configuration attribute, if class attribute is null, it is set to the configuration in the
     * winery.yml file.
     *
     * @return the configuration attribute of Environment
     */
    protected YAMLConfiguration getConfiguration() {
        if (config == null) {
            this.getConfigFromFile();
        }
        return config;
    }

    /**
     * Sets the Class attribute to the configuration parameter. This is only used in testing, to set the configuration
     * back to the original state, before the test.
     */
    protected void setConfiguration(YAMLConfiguration configuration) {
        this.config = configuration;
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
            } else {
                return !lastChange.equals(Files.getLastModifiedTime(DEFAULT_CONFIG_FILE.toPath()));
            }
        } catch (IOException e) {
            LOGGER.warn("Error while accessing the last changed time", e);
        }
        return false;
    }

    /**
     * Checks first, if the winery.yml file is in the config directory of the winery. If it is, it just returns a
     * Configuration Instance of the existing winery.yml file. Else it calls createDefaultConfigFile(). Is protected for
     * testing purposes.
     */
    private void getConfigFromFile() {
        File configFile = null;
        if (SystemUtils.IS_OS_LINUX && LINUX_CONFIG_FILE.exists()) {
            configFile = LINUX_CONFIG_FILE;
        }
        if (configFile == null) {
            if (!DEFAULT_CONFIG_FILE.exists()) {
                copyDefaultConfigFile();
            }
            try {
                lastChange = Files.getLastModifiedTime(DEFAULT_CONFIG_FILE.toPath());
            } catch (IOException e) {
                LOGGER.debug("Error while accessing the last changed time.", e);
            }
            configFile = DEFAULT_CONFIG_FILE;
        }
        LOGGER.info("Winery config file: {}", configFile.getAbsolutePath());
        YAMLConfiguration config = null;
        try (FileReader reader = new FileReader(configFile)) {
            config = new YAMLConfiguration();
            config.read(reader);
        } catch (ConfigurationException | IOException ex) {
            LOGGER.debug("Error while reading configuration file.", ex);
        }
        this.config = config;
        this.configFile = configFile;
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
            LOGGER.info("Copying default configuration to user home");
            FileUtils.copyInputStreamToFile(defaultConfigInputStream, Environment.DEFAULT_CONFIG_FILE);
        } catch (IOException | NullPointerException e) {
            LOGGER.debug("Error while copying the default config file into the winery directory", e);
        }
    }

    /**
     * This method resets the accountability part of the configuration by copying the default configuration to a file
     * and reading the accountability values and replacing them
     */
    void reloadAccountabilityConfiguration(InputStream defaultConfigInputStream) {
        if (configFile != null) {
            File tempFile = new File(configFile.getParent(), "wineryAccountability.yml");
            try {
                FileUtils.copyInputStreamToFile(defaultConfigInputStream, tempFile);
            } catch (IOException e) {
                LOGGER.debug("Error copying default file", e);
            }
            YAMLConfiguration defaultAccountabilityConfiguration;
            try (FileReader reader = new FileReader(tempFile)) {
                String accountabilityPrefix = "accountability";
                defaultAccountabilityConfiguration = new YAMLConfiguration();
                defaultAccountabilityConfiguration.read(reader);
                YAMLConfiguration currentConfiguration = this.getConfiguration();
                Iterator<String> accountabilityAttributes = defaultAccountabilityConfiguration.getKeys(accountabilityPrefix);
                accountabilityAttributes.forEachRemaining(key ->
                    currentConfiguration.setProperty(key, defaultAccountabilityConfiguration.getString(key)));
                this.save();
            } catch (Exception e) {
                LOGGER.warn("Error while reading accountability configuration file", e);
            }
            FileUtils.deleteQuietly(tempFile);
        }
    }

    /**
     * This method writes the current configuration of the configuration attribute into the winery.yml file.
     */
    protected void save() {
        if (configFile != null) {
            try (FileWriter writer = new FileWriter(configFile)) {
                this.getConfiguration().write(writer);
            } catch (Exception e) {
                LOGGER.warn("Error saving the config file: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Reloads configuration from file.
     */
    void updateConfig() {
        this.getConfigFromFile();
    }
}
