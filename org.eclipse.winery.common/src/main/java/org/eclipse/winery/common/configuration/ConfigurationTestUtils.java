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
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationTestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationTestUtils.class);
    private static final File configDirectory = new File(System.getProperty("user.home") + "/.winery");
    private static final File configFile = new File(configDirectory + "/winery.yml");
    private static YAMLConfiguration savedConfig;

    /**
     * Saves the current configuration from file in the savedConfig property
     */
    public static void saveCurrentConfiguration() {
        if (configFile.exists()) {
            YAMLConfiguration config = null;
            try (FileReader reader = new FileReader(configFile)) {
                config = new YAMLConfiguration();
                config.read(reader);
            } catch (ConfigurationException | IOException ex) {
                LOGGER.debug("Error while reading configuration file.", ex);
            }
            ConfigurationTestUtils.savedConfig = config;
        } else {
            ConfigurationTestUtils.savedConfig = null;
        }
    }

    /**
     * This method replaces the current winery.yml file with a test winery.yml file.
     */
    public static void replaceFileWithTestFile() throws IOException {
        Environment.getInstance().setConfiguration(null);
        InputStream testConfigInputStream = ConfigurationTestUtils.class.getClassLoader().getResourceAsStream("wineryTest.yml");
        if (testConfigInputStream == null) {
            throw new NullPointerException();
        }
        FileUtils.copyInputStreamToFile(testConfigInputStream, configFile);
        testConfigInputStream.close();
    }

    /**
     * Sets the file configuration to the state before the test.
     */
    public static void setConfigBack() {
        if (savedConfig == null) {
            return;
        } else {
            Environment.getInstance().setConfiguration(savedConfig);
            Environment.getInstance().save();
        }
    }
}
