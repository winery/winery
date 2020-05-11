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

import org.apache.commons.configuration2.YAMLConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("This test seems to fail transiently on test infrastructure")
public class EnvironmentsTest {

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
     * This test checks for the ConfigurationObject instance to contain the correct fields and values.
     */
    @Test
    public void testGetUiConfig() {
        UiConfigurationObject testObject = Environments.getInstance().getUiConfig();
        assertTrue(testObject.getFeatures().get("foo"));
        assertFalse(testObject.getFeatures().get("bar"));
        assertEquals("http://quaz:8080", testObject.getEndpoints().get("quaz"));
    }

    /**
     * Tests whenever the configuration is loaded from backend if it was changed
     */
    @Test
    public void testReload() {
        YAMLConfiguration configuration = Environment.getInstance().getConfiguration();
        assertFalse(Environment.getInstance().checkConfigurationForUpdate());
        configuration.setProperty("ui.features.foo", false);
        Environment.getInstance().save();
        assertFalse(Environments.getInstance().getUiConfig().getFeatures().get("foo"));
    }

    /**
     * This test checks for the correct Git-credentials set in the config file.
     */
    @Test
    public void testGetGitConfig() {
        GitConfigurationObject gitInfo = Environments.getInstance().getGitConfig();
        assertEquals("id", gitInfo.getClientID());
        assertEquals("secret", gitInfo.getClientSecret());
        assertEquals("default", gitInfo.getUsername());
        assertEquals("default", gitInfo.getPassword());
        assertFalse(gitInfo.isAutocommit());
    }

    @Test
    public void testGetRepositoryConfig() {
        RepositoryConfigurationObject repositoryConfig = Environments.getInstance().getRepositoryConfig();
        assertEquals("file", repositoryConfig.getProvider().toString());
        assertEquals("thisisatestroot", repositoryConfig.getRepositoryRoot());
    }

    @Test
    public void testGetAccountabilityConfig() {
        AccountabilityConfigurationObject accountabilityConfig = Environments.getInstance().getAccountabilityConfig();
        assertEquals("test.url", accountabilityConfig.getGethUrl());
        assertEquals("123456789", accountabilityConfig.getEthereumPassword());
        assertEquals("provenancesmartcontracttestaddress", accountabilityConfig.getEthereumProvenanceSmartContractAddress());
        assertEquals("authorizationsmartcontracttestaddress", accountabilityConfig.getEthereumAuthorizationSmartContractAddress());
        assertEquals("swarmgatewaytesturl", accountabilityConfig.getSwarmGatewayUrl());
    }

    /**
     * This test checks for the correct repositoryRoot, which is set in the config file.
     */
    @Test
    public void testGetRepositoryRoot() throws IOException {
        // explicitly reset the test file
        ConfigurationTestUtils.replaceFileWithTestFile();
        assertEquals("thisisatestroot", Environments.getInstance().getRepositoryConfig().getRepositoryRoot());
    }

    /**
     * This test checks whenever the repositoryRoot is changed to the correct value, when the setRepositoryRoot method
     * is called.
     */
    @Test
    public void testSetRepositoryRoot() {
        String changedRoot = "ThisIsTheChangedRoot";
        Environments.getInstance().getRepositoryConfig().setRepositoryRoot(changedRoot);
        assertEquals(changedRoot, Environments.getInstance().getRepositoryConfig().getRepositoryRoot());
    }

    /**
     * This test checks if the configuration changes which are made in an UiConfigurationObject instance are persisted
     * to file, when the saveFeatures method is called.
     */
    @Test
    public void testSaveFeatures() throws Exception {
        // Explicitly reset the test file 
        ConfigurationTestUtils.replaceFileWithTestFile();
        UiConfigurationObject object = Environments.getInstance().getUiConfig();
        object.getFeatures().put("foo", false);
        object.getFeatures().put("bar", true);
        object.save();
        UiConfigurationObject result = Environments.getInstance().getUiConfig();
        assertFalse(result.getFeatures().get("foo"));
        assertTrue(result.getFeatures().get("bar"));
    }

    /**
     * This test checks whenever the enpoints are set to the correct values when they are changed via the saveEndpoints
     * method.
     */
    @Test
    public void testSaveEndpoints() {
        UiConfigurationObject object = Environments.getInstance().getUiConfig();
        object.getEndpoints().put("quaz", "");
        object.save();
        UiConfigurationObject result = Environments.getInstance().getUiConfig();
        assertEquals("", result.getEndpoints().get("quaz"));
    }
}
