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
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Comparator;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountabilityConfigurationManagerTest {
    private AccountabilityConfigurationManager manager;

    @AfterEach
    public void removeFiles() throws IOException {
        Files.walk(AccountabilityConfigurationManager.getEthereumCredentialsFilePath().toPath())
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
        this.manager = AccountabilityConfigurationManager.getInstance();
    }

    @Test
    void keystoreFile() throws IOException {
        String fileContents = "{\"address\":\"fffffa3d4e77d2ce2a9d9ce107ec8ec7cffffff\"}";

        try (InputStream newKeystoreFileStream = IOUtils.toInputStream(fileContents)) {
            String newKeystoreFileName = "mySpecialKeystoreFile!!";
            this.manager.setNewKeystoreFile(newKeystoreFileStream, newKeystoreFileName);
            assertTrue(new File(AccountabilityConfigurationManager.getEthereumCredentialsFilePath() + "/" + newKeystoreFileName).exists());
        }
    }

    @Test
    public void loadDefaults() throws IOException {
        // change the keystore file
        String fileContents = "{\"address\":\"fffffa3d4e77d2ce2a9d9ce107ec8ec7cffffff\"}";
        try (InputStream newKeystoreFileStream = IOUtils.toInputStream(fileContents)) {
            String newKeystoreFileName = "mySpecialKeystoreFile!!";
            this.manager.setNewKeystoreFile(newKeystoreFileStream, newKeystoreFileName);
            // restore defaults
            this.manager.restoreDefaults();
            assertTrue(new File(
                AccountabilityConfigurationManager.getEthereumCredentialsFilePath()
                    + "/"
                    + AccountabilityConfigurationManager.getDefaultKeystoreFileName()).exists()
            );
            assertEquals(Environments.getInstance().getAccountabilityConfig().getEthereumCredentialsFileName(), AccountabilityConfigurationManager.getDefaultKeystoreFileName());
        }
    }
}


