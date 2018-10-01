/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Properties;

import org.apache.tika.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountabilityConfigurationManagerTest {
    private static final String APPLICATION_CONFIGURATION_FILE = "test/appConfig.properties";
    private static final String APPLICATION_KEYSTORE_FILE = "test/appKeystore.json";
    private static final String DEFAULT_KEYSTORE_FILE = "test/defaultKeystore.json";
    private static final String DEFAULT_KEYSTORE_FILE_NAME = "UTC--2018-03-05T15-33-22.456000000Z--e4b51a3d4e77d2ce2a9d9ce107ec8ec7cff5571d.json";

    private File appConfigFile;
    private File appKeystoreFile;
    private File defaultKeystoreFile;
    private AccountabilityConfigurationManager manager;

    @AfterEach
    public void removeFiles() throws IOException {
        Path index = Paths.get("test/");

        Files.walk(index)
            .sorted(Comparator.reverseOrder())  // as the file tree is traversed depth-first and that deleted dirs have to be empty  
            .forEach(t -> {
                try {
                    Files.delete(t);
                } catch (IOException ignored) {
                }
            });
    }

    @BeforeEach
    public void setUp() {
        this.appConfigFile = new File(APPLICATION_CONFIGURATION_FILE);
        this.appKeystoreFile = new File(APPLICATION_KEYSTORE_FILE);
        this.defaultKeystoreFile = new File(DEFAULT_KEYSTORE_FILE);
        this.manager = AccountabilityConfigurationManager.getInstance(this.appConfigFile,
            this.appKeystoreFile, this.defaultKeystoreFile);
    }

    @Test
    public void saveProperties() throws IOException {
        this.manager.properties.setProperty("geth-url", "http://mytesturl.com:5555");
        this.manager.properties.setProperty("ethereum-password", "mypassword");
        this.manager.saveProperties();
        assertEquals(true, appConfigFile.exists());
        assertEquals(true, defaultKeystoreFile.exists());
        Properties fromFile = new Properties();

        try (InputStream stream = new FileInputStream(appConfigFile)) {
            fromFile.load(stream);

            assertEquals("http://mytesturl.com:5555", fromFile.getProperty("geth-url"));
            assertEquals("mypassword", fromFile.get("ethereum-password"));
        }
    }

    @Test
    public void keystoreFile() throws IOException {
        String fileContents = "{\"address\":\"fffffa3d4e77d2ce2a9d9ce107ec8ec7cffffff\"}";

        try (InputStream newKeystoreFileStream = IOUtils.toInputStream(fileContents)) {
            String newKeystoreFileName = "mySpecialKeystoreFile!!";
            this.manager.setNewKeystoreFile(newKeystoreFileStream, newKeystoreFileName);

            assertEquals(appKeystoreFile.getAbsolutePath(), this.manager.properties.getProperty("ethereum-credentials-file-path"));
            assertEquals(newKeystoreFileName, this.manager.properties.getProperty("ethereum-credentials-file-name"));
        }
    }

    @Test
    public void loadDefaults() throws IOException {
        // change some properties
        this.manager.properties.setProperty("geth-url", "http://mytesturl.com:5555");
        this.manager.properties.setProperty("ethereum-password", "mypassword");
        this.manager.saveProperties();
        // change the keystore file
        String fileContents = "{\"address\":\"fffffa3d4e77d2ce2a9d9ce107ec8ec7cffffff\"}";
        try (InputStream newKeystoreFileStream = IOUtils.toInputStream(fileContents)) {
            String newKeystoreFileName = "mySpecialKeystoreFile!!";
            this.manager.setNewKeystoreFile(newKeystoreFileStream, newKeystoreFileName);
            // restore defaults
            this.manager.restoreDefaults();
            // compare managed properties with defaults
            Properties defaults = new Properties();
            defaults.load(getClass().getClassLoader().getResourceAsStream("defaultaccountabilityconfig.properties"));
            assertEquals(defaults.getProperty("geth-url"), this.manager.properties.getProperty("geth-url"));
            assertEquals(defaults.getProperty("ethereum-password"), this.manager.properties.get("ethereum-password"));
            // compare managed keystorefile with defaults
            assertEquals(defaultKeystoreFile.getAbsolutePath(), this.manager.properties.get("ethereum-credentials-file-path"));
            assertEquals(DEFAULT_KEYSTORE_FILE_NAME, this.manager.properties.get("ethereum-credentials-file-name"));
        }
    }
}
