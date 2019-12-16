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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountabilityConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountabilityConfigurationManager.class);
    private static AccountabilityConfigurationManager instance;
    private static final String DEFAULT_PROPERTIES_FILE_NAME = "defaultaccountabilityconfig.properties";
    private static final String DEFAULT_KEYSTORE_FILE_NAME = "UTC--2018-03-05T15-33-22.456000000Z--e4b51a3d4e77d2ce2a9d9ce107ec8ec7cff5571d.json";

    public Properties properties = null;

    private final File APPLICATION_PROPERTIES_FILE;
    private final File APPLICATION_KEYSTORE_FILE;
    private final File DEFAULT_KEYSTORE_FILE;

    private AccountabilityConfigurationManager(File applicationPropertiesFile, File applicationKeystoreFile, File defaultKeystoreFile) {
        APPLICATION_PROPERTIES_FILE = applicationPropertiesFile;
        APPLICATION_KEYSTORE_FILE = applicationKeystoreFile;
        DEFAULT_KEYSTORE_FILE = defaultKeystoreFile;
    }

    /**
     * Copies the default keystore from the resources folder to the repository and sets as the active keystore in the properties
     *
     * @param myProps the properties in which to include the path and name of the copied default keystore.
     */
    private void setDefaultKeystore(Properties myProps) {
        try {
            // create the folder and an empty file if they don't exist
            if (!DEFAULT_KEYSTORE_FILE.exists()) {
                if (DEFAULT_KEYSTORE_FILE.getParentFile().mkdirs() || DEFAULT_KEYSTORE_FILE.createNewFile()) {
                    LOGGER.info("Successfully created default Ethereum keystore file at {}", DEFAULT_KEYSTORE_FILE);
                } else {
                    LOGGER.error("Could not create default Ethereum keystore file at {}", DEFAULT_KEYSTORE_FILE);
                }
            }

            try (InputStream defaultKeystoreStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_KEYSTORE_FILE_NAME)) {
                Objects.requireNonNull(defaultKeystoreStream);
                try (FileOutputStream output = new FileOutputStream(DEFAULT_KEYSTORE_FILE)) {
                    IOUtils.copy(defaultKeystoreStream, output);
                    myProps.setProperty("ethereum-credentials-file-name", DEFAULT_KEYSTORE_FILE_NAME);
                    myProps.setProperty("ethereum-credentials-file-path", DEFAULT_KEYSTORE_FILE.getAbsolutePath());
                    LOGGER.info("Copied default Ethereum keystore file to {}", DEFAULT_KEYSTORE_FILE);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Cannot open stream for default Ethereum keystore file. Reason: {}", e.getMessage());
        }
    }

    /**
     * Fills-in the properties using a properties file.
     *
     * @param propertiesFile the input properties file
     * @param myProperties   the Properties object to be filled with content from the file.
     */
    private void loadPropertiesFromFile(File propertiesFile, Properties myProperties) {
        try (InputStream inputStream = new FileInputStream(propertiesFile)) {

            this.loadPropertiesFromFile(inputStream, myProperties);
        } catch (IOException e) {
            // this file is expected to be missing if the default configuration is not changed and saved
            LOGGER.info("Accountability application configuration file missing.");
        }
    }

    private void loadPropertiesFromFile(InputStream inputStream, Properties myProperties) {
        try {
            myProperties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Failed to load configuration file. Reason: {}", e.getMessage());
        }
    }

    /**
     * Loads properties from the default properties file, and sets tha active keystore to the default one.
     *
     * @return the defualt properties
     */
    private Properties loadDefaultProperties() {
        Properties myProps = new Properties();

        try (InputStream defaultPropsStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE_NAME)) {
            Objects.requireNonNull(defaultPropsStream);

            loadPropertiesFromFile(defaultPropsStream, myProps);
            URL defaultKeystoreFileURL = getClass().getClassLoader().getResource(DEFAULT_KEYSTORE_FILE_NAME);
            Objects.requireNonNull(defaultKeystoreFileURL);

            // use the default keystore
            setDefaultKeystore(myProps);
        } catch (IOException e) {
            LOGGER.error("Failed occurred while loading default propertie. Reason: {}", e.getMessage());
        }

        return myProps;
    }

    /**
     * Loads the active properties, if some properties are missing in the application properties file their values are
     * derived from the default properties file instead.
     */
    private void loadProperties() {
        Properties result = loadDefaultProperties();
        loadPropertiesFromFile(APPLICATION_PROPERTIES_FILE, result);
        properties = result;
    }

    /**
     * Save changes made to the properties to the application configuration file of the repository.
     *
     * @throws IOException if an error occurs while accessing the application configuration file.
     */
    public void saveProperties() throws IOException {

        if (!APPLICATION_PROPERTIES_FILE.exists()) {
            if (APPLICATION_PROPERTIES_FILE.getParentFile().mkdirs() || APPLICATION_PROPERTIES_FILE.createNewFile()) {
                LOGGER.info("Created new accountability application configuration file at {}", APPLICATION_PROPERTIES_FILE);
            } else {
                LOGGER.error("Could not create accountability application configuration file at {}", APPLICATION_PROPERTIES_FILE);
            }
        }
        try (FileOutputStream output = new FileOutputStream(APPLICATION_PROPERTIES_FILE)) {
            this.properties.store(output, "---Application Configuration---");
        } catch (IOException e) {
            LOGGER.error("Cannot open stream for application configuration file. Reason: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Restores the default properties and keystore.
     */
    public void restoreDefaults() throws IOException {
        this.properties = loadDefaultProperties();
        saveProperties();
    }

    /**
     * Changes the active keystore file
     *
     * @param keystoreFile the new keystore file input stream
     * @param fileName     the name of the new keystore file
     * @throws IOException if an error occurs when trying to write to the application keystore file.
     */
    public void setNewKeystoreFile(InputStream keystoreFile, String fileName) throws IOException {
        try {
            if (!APPLICATION_KEYSTORE_FILE.exists()) {
                if (APPLICATION_KEYSTORE_FILE.getParentFile().mkdirs() || APPLICATION_KEYSTORE_FILE.createNewFile()) {
                    LOGGER.info("Created new Ethereum keystore application file at {}", APPLICATION_KEYSTORE_FILE);
                } else {
                    LOGGER.error("Could not create Ethereum keystore application file at {}", APPLICATION_KEYSTORE_FILE);
                }
            }

            this.properties.setProperty("ethereum-credentials-file-path", APPLICATION_KEYSTORE_FILE.getAbsolutePath());
            this.properties.setProperty("ethereum-credentials-file-name", fileName);

            try (FileOutputStream output = new FileOutputStream(APPLICATION_KEYSTORE_FILE)) {
                IOUtils.copy(keystoreFile, output);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot open stream for Ethereum keystore application file. Reason: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Discards all unsaved changes made to the properties
     */
    public void reload() {
        properties.clear();
        loadProperties();
    }

    public static AccountabilityConfigurationManager getInstance(File applicationConfigurationFile, File applicationKeystoreFils, File defaultKeystoreFile) {
        if (instance == null) {
            instance = new AccountabilityConfigurationManager(applicationConfigurationFile, applicationKeystoreFils, defaultKeystoreFile);
        }

        instance.loadProperties();
        
        return instance;
    }
}
