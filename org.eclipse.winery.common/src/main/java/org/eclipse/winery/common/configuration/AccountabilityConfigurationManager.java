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
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Offers basic management capabilities: - Restore the Accountability Settings to default. - Management of the
 * KeystoreFile.
 */
public class AccountabilityConfigurationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountabilityConfigurationManager.class);

    private static final String DEFAULT_KEYSTORE_FILE_NAME = "UTC--2019-01-23T10-48-04.632976800Z--44fb31577305b7b6ed8ff05ee7c0f07b3cc99306.json";

    private static AccountabilityConfigurationManager instance;

    private AccountabilityConfigurationManager() {
    }

    public static String getDefaultKeystoreFileName() {
        return DEFAULT_KEYSTORE_FILE_NAME;
    }

    /**
     * Copies the default keystore from the resources folder to the repository and sets as the active keystore in the
     * properties
     */
    public void setDefaultKeystore() {
        File credentialsDirectory = new File(Environment.getInstance().getConfigDirectory(), "accountability-credentials");
        try {
            try (InputStream defaultKeystoreStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_KEYSTORE_FILE_NAME)) {
                Objects.requireNonNull(defaultKeystoreStream);
                FileUtils.copyInputStreamToFile(defaultKeystoreStream, new File(credentialsDirectory, DEFAULT_KEYSTORE_FILE_NAME));
                Environments.getInstance().getAccountabilityConfig().setEthereumCredentialsFileName(DEFAULT_KEYSTORE_FILE_NAME);
                Environments.getInstance().getAccountabilityConfig().save();
            }
        } catch (IOException e) {
            LOGGER.error("Cannot open stream for default Ethereum keystore file. Reason: {}", e.getMessage());
        }
    }

    public static File getEthereumCredentialsFilePath() {
        return new File(Environment.getInstance().getConfigDirectory(), "accountability-credentials");
    }

    public void restoreDefaults() throws IOException {
        Environments.resetAccountabilityConfiguration();
        setDefaultKeystore();
    }

    /**
     * Changes the active keystore file
     *
     * @param keystoreFile the new keystore file input stream
     * @param fileName     the name of the new keystore file
     * @throws IOException if an error occurs when trying to write to the application keystore file.
     */
    public void setNewKeystoreFile(InputStream keystoreFile, String fileName) throws IOException {
        File credentialsDirectory = new File(Environment.getInstance().getConfigDirectory(), "accountability-credentials");
        try {
            Environments.getInstance().getAccountabilityConfig().setEthereumCredentialsFileName(fileName);
            FileUtils.copyInputStreamToFile(keystoreFile, new File(credentialsDirectory, fileName));
        } catch (IOException e) {
            LOGGER.error("Cannot open stream for Ethereum keystore application file. Reason: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * This will return the instance of the AccountabilityConfigurationManager. Ensures that there is only 1 instance of
     * the AccountabilityConfigurationManager.
     *
     * @return the instance of the Accountability Configuration Manager
     */
    public static AccountabilityConfigurationManager getInstance() {
        if (instance == null) {
            instance = new AccountabilityConfigurationManager();
        }
        return instance;
    }
}
