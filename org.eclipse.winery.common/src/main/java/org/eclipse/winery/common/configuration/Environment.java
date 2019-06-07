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
    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);
    //Path to the directory of the winery configuration file
    private static final File configDirectory = new File(System.getProperty("user.home") + "/.winery");
    //Path of the winery configuration file
    private static final File configFile = new File(configDirectory + "/winery.yml");
    //InputStream that accesses the defaultConfiguration from the resources
    private static final InputStream defaultConfigInputStream = Environment.class.getClassLoader().getResourceAsStream("winery.yml");
    //Contains the configuration
    private static YAMLConfiguration configuration;

    /**
     * Getter for the configuration attribute, if class attribute is null, it is set to the configuration in the
     * winery.yml file.
     *
     * @return the configuration attribute of Environment
     */
    protected static YAMLConfiguration getConfiguration() {
        if (Environment.configuration == null) {
            Environment.getConfigFromFile();
        }
        return Environment.configuration;
    }

    /**
     * Sets the Class attribute to the configuration parameter. This is only used in testing, to set the configuration
     * back to the original state, before the test.
     */
    protected static void setConfiguration(YAMLConfiguration configuration) {
        Environment.configuration = configuration;
    }

    /**
     * Checks first, if the winery.yml file is in the config directory of the winery. If it is, it just returns a
     * Configuration Instance of the existing winery.yml file. Else it calls createDefaultConfigFile(). Is protected for
     * testing purposes.
     */
    private static void getConfigFromFile() {
        if (!configFile.exists()) {
            copyDefaultConfigFile(configFile);
        }
        YAMLConfiguration config = null;
        try (FileReader reader = new FileReader(configFile)) {
            config = new YAMLConfiguration();
            config.read(reader);
        } catch (ConfigurationException | IOException ex) {
            LOGGER.debug("Error while reading configuration file.", ex);
        }
        Environment.configuration = config;
    }

    /**
     * Method copies the default winery.yml file (all flags are true) into the config directory of the win ery in
     * HOME/.winery. If the directory doesn't exist it will be created.
     *
     * @param configFilePath Path where the configuration will be copied to
     */
    private static void copyDefaultConfigFile(File configFilePath) {
        try {
            if (defaultConfigInputStream == null) {
                throw new NullPointerException();
            }
            FileUtils.copyInputStreamToFile(defaultConfigInputStream, configFilePath);
            defaultConfigInputStream.close();
        } catch (IOException | NullPointerException ex) {
            LOGGER.debug("Error while copying the default config file into the winery directory", ex);
        }
    }

    /**
     * This method writes the current configuration of the configuration attribute into the winery.yml file.
     */
    protected static void save() {
        try {
            FileWriter writer = new FileWriter(configFile);
            configuration.write(writer);
            writer.close();
        } catch (IOException | ConfigurationException ioex) {
            LOGGER.debug("Error while saving the config file.", ioex);
        }
    }
}
