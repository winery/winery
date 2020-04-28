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
import java.io.IOException;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvironmentTest {

    private static final File configDirectory = new File(System.getProperty("user.home") + "/.winery");
    private static final File configFile = new File(configDirectory + "/winery.yml");

    @BeforeAll
    public static void saveCurrentConfiguration() {
        ConfigurationTestUtils.saveCurrentConfiguration();
    }

    @AfterAll
    public static void setConfigBack() {
        ConfigurationTestUtils.setConfigBack();
    }

    @BeforeEach
    public void replaceFileWithTestFile() throws IOException {
        ConfigurationTestUtils.replaceFileWithTestFile();
    }

    /**
     * This test checks, when there is no .winery directory in HOME and therefor no winery.yml Config file, whether the
     * path and config gets created.
     */
    @Test
    public void testDefaultConfigFileCreation() throws IOException {
        FileUtils.deleteDirectory(configDirectory);
        Environment.getInstance().getConfiguration();
        assertTrue(configDirectory.exists());
        assertTrue(configFile.isFile());
    }

    /**
     * This test checks for correctness of the retrieved values from the config file. Also tests whether or not the
     * properties in the config file are found.
     */
    @Test
    public void testConfigValues() throws IOException {
        YAMLConfiguration tested = Environment.getInstance().getConfiguration();
        assertTrue(tested.getBoolean("ui.features.foo"));
        assertFalse(tested.getBoolean("ui.features.bar"));
        assertEquals("http://quaz:8080", tested.getString("ui.endpoints.quaz"));
    }

    /**
     * This test checks whether changes made to the configuration are persisted correctly. and whenever the file is
     * reloaded when a change occurs
     */
    @Test
    public void testSave() {
        YAMLConfiguration configuration = Environment.getInstance().getConfiguration();
        configuration.setProperty("ui.features.foo", false);
        configuration.setProperty("ui.features.bar", true);
        configuration.setProperty("ui.endpoints.quaz", "");
        Environment.getInstance().save();
        Environment.getInstance().setConfiguration(null);
        configuration = Environment.getInstance().getConfiguration();
        assertFalse(configuration.getBoolean("ui.features.foo"));
        assertTrue(configuration.getBoolean("ui.features.bar"));
        assertEquals(configuration.getString("ui.endpoints.quaz"), "");
    }

    /**
     * Tests whenever a change in the configuration file is detected.
     */
    @Test
    @Disabled("This test seems to fail transiently on test infrastructure")
    public void testReload() {
        YAMLConfiguration configuration = Environment.getInstance().getConfiguration();
        assertFalse(Environment.getInstance().checkConfigurationForUpdate());
        configuration.setProperty("ui.features.foo", false);
        Environment.getInstance().save();
        assertTrue(Environment.getInstance().checkConfigurationForUpdate());
    }

    @Test
    public void testReloadAccountabilityConfiguration() {
        assertEquals("test.url", Environment.getInstance().getConfiguration().getString("accountability.geth-url"));
        Environment.getInstance().getConfiguration().setProperty("accountability.geth-url", "test2.url");
        assertEquals("test2.url", Environment.getInstance().getConfiguration().getString("accountability.geth-url"));
        Environment.getInstance().save();
        assertEquals("test2.url", Environment.getInstance().getConfiguration().getString("accountability.geth-url"));
        Environment.getInstance().reloadAccountabilityConfiguration(Environment.getTestConfigInputStream());
        assertEquals("test.url", Environment.getInstance().getConfiguration().getString("accountability.geth-url"));
    }
}
