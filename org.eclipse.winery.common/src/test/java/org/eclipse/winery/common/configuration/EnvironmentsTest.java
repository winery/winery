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
import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvironmentsTest {

    private static final File configDirectory = new File(System.getProperty("user.home") + "/.winery");

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
    public void testGet() {
        ConfigurationObject testObject = Environments.get();
        assertTrue(testObject.getFeatures().get("foo"));
        assertFalse(testObject.getFeatures().get("bar"));
        assertEquals(testObject.getEndpoints().get("quaz"), "http://quaz:8080");
    }

    /**
     * This test checks for the correct Git-credentials set in the config file.
     */
    @Test
    public void testGetGit() {
        HashMap<String, String> gitInfo = Environments.getGit();
        assertEquals(gitInfo.get("clientID"), "id");
        assertEquals(gitInfo.get("clientSecret"), "secret");
        assertEquals(gitInfo.get("username"), "default");
        assertEquals(gitInfo.get("password"), "default");
    }

    /**
     * This test checks for the correct autocommit value, which is set in the config file.
     */
    @Test
    public void testIsAutocommit() {
        assertFalse(Environments.isAutoCommit());
    }

    /**
     * This test checks for the correct repositoryRoot, which is set in the config file.
     */
    @Test
    public void testGetRepositoryRoot() {
        assertEquals("thisisatestroot", Environments.getRepositoryRoot());
    }

    /**
     * This test checks if the configuration changes which are made in an ConfigurationObject instance are persisted to
     * file, when the saveFeatures method is called.
     */
    @Test
    public void testSaveFeatures() {
        ConfigurationObject object = Environments.get();
        object.getFeatures().put("foo", false);
        object.getFeatures().put("bar", true);
        object.getEndpoints().put("quaz", "changed");
        Environments.saveFeatures(object);
        //This way the get() will load the configuration from file.
        Environment.setConfiguration(null);
        ConfigurationObject result = Environments.get();
        assertFalse(result.getFeatures().get("foo"));
        assertTrue(result.getFeatures().get("bar"));
        assertEquals(result.getEndpoints().get("quaz"), "http://quaz:8080");
    }

    /**
     * This test checks whenever the repositoryRoot is changed to the correct value, when the setRepositoryRoot method
     * is called.
     */
    @Test
    public void testSetRepositoryRoot() {
        String changedRoot = "ThisIsTheChangedRoot";
        Environments.setRepositoryRoot(changedRoot);
        assertEquals(changedRoot, Environments.getRepositoryRoot());
    }

    /**
     * This test checks whenever the enpoints are set to the correct values when they are changed via the saveEndpoints
     * method. Also checks for the features no to be changed by the method.
     */
    @Test
    public void testSaveEndpoints() {
        ConfigurationObject object = Environments.get();
        object.getFeatures().put("foo", false);
        object.getFeatures().put("bar", true);
        object.getEndpoints().put("quaz", "");
        Environments.saveEndpoints(object);
        Environment.setConfiguration(null);
        ConfigurationObject result = Environments.get();
        assertTrue(result.getFeatures().get("foo"));
        assertFalse(result.getFeatures().get("bar"));
        assertEquals(result.getEndpoints().get("quaz"), "");
    }
}
