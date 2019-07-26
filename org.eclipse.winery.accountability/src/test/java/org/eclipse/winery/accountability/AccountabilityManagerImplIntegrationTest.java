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

package org.eclipse.winery.accountability;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.accountability.blockchain.BlockchainAccess;
import org.eclipse.winery.accountability.blockchain.BlockchainFactory;
import org.eclipse.winery.accountability.model.FileProvenanceElement;
import org.eclipse.winery.accountability.model.ModelProvenanceElement;
import org.eclipse.winery.accountability.model.authorization.AuthorizationInfo;
import org.eclipse.winery.accountability.storage.ImmutableStorageProvider;
import org.eclipse.winery.accountability.storage.ImmutableStorageProviderFactory;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIf("(new java.io.File(\"./UTC--2019-01-23T10-48-04.632976800Z--44fb31577305b7b6ed8ff05ee7c0f07b3cc99306\").exists())")
class AccountabilityManagerImplIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(AccountabilityManagerImplIntegrationTest.class);
    private static final String CONFIGURATION_FILE_NAME = "defaultaccountabilityconfig.properties";
    private static Properties props;
    private AccountabilityManagerImpl accountabilityManager;

    @BeforeAll
    static void startGanache() throws Exception {
        try (InputStream propsStream = AccountabilityManagerImplIntegrationTest.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILE_NAME)) {
            props = new Properties();
            props.load(propsStream);
        }

        final String privateKey = props.getProperty("ethereum-private-key");
        GanacheManager ganacheManager = GanacheManager.getInstance(privateKey);
        ganacheManager.startGanache();
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() < start + 10000) {
        }
    }

    @AfterAll
    static void killGanache() throws InterruptedException {
        final String privateKey = props.getProperty("ethereum-private-key");
        GanacheManager ganacheManager = GanacheManager.getInstance(privateKey);
        ganacheManager.stopGanache();
    }

    @BeforeEach
    public void setUp() throws Exception {
        // deploy smart contracts to (a new address in) the blockchain
        ContractDeployer deployer = new ContractDeployer(props);
        props.setProperty("ethereum-provenance-smart-contract-address", deployer.deployProvenance());
        props.setProperty("ethereum-authorization-smart-contract-address", deployer.deployAuthorization());
        // reset the contents of the accountability layer so older, possibly incompatible instances are nullified.
        BlockchainFactory.reset();
        ImmutableStorageProviderFactory.reset();
        // create new instances
        BlockchainAccess blockchainAccess = BlockchainFactory
            .getBlockchainAccess(BlockchainFactory.AvailableBlockchains.ETHEREUM, props);
        ImmutableStorageProvider storageProvider = ImmutableStorageProviderFactory
            .getStorageProvider(ImmutableStorageProviderFactory.AvailableImmutableStorages.SWARM, props);
        this.accountabilityManager = new AccountabilityManagerImpl(blockchainAccess, storageProvider);
    }

    @Test
    void addParticipant() throws Exception {
        String processId = "ServiceTemplateWithAllReqCapVariants";
        String participantBlockchainId = "0x0000000000000000000000000111111222223333";
        String participantName = "Ghareeb";
        CompletableFuture<String> authorize = this.accountabilityManager.authorize(processId, participantBlockchainId, participantName);
        assertNotNull(authorize.get());
        CompletableFuture<AuthorizationInfo> authorization = this.accountabilityManager.getAuthorization(processId);
        AuthorizationInfo authorizationInfo = authorization.get();
        
        assertTrue(authorizationInfo.isAuthorized(participantBlockchainId));
    }

    /**
     * stores fingerprints and a metafile in the blockchain
     */
    public void makeHistory() throws Exception {
        String file0 = "myTestFile.tosca";
        String manifest = "TOSCA-Meta-Version: 1.0\n" +
            "CSAR-Version: 1.0\n" +
            "Created-By: Winery 2.0.0-SNAPSHOT\n" +
            "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
            "\n" +
            "Name: " + file0 + "\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n" +
            "SHA-256: 97193968948686d6947d4d760d3fe724b9981980056b8902be92e91fbe9e3eed\n";

        String processIdentifier = "{http://plain.winery.opentosca.org/servicetemplates}ServiceTemplateWithAllReqCapVariants";
        // store two fingerprints (it doesn't matter that they are identical)
        this.accountabilityManager.storeFingerprint(processIdentifier, manifest).get();
        this.accountabilityManager.storeFingerprint(processIdentifier, manifest).get();
        
        try (ByteArrayInputStream stream = new ByteArrayInputStream(manifest.getBytes())) {
            this.accountabilityManager.storeState(stream);
        }
    }

    @Test
    void getHistory() throws Exception {
        this.makeHistory(); //method that tries to push the needed records before the Test
        String processId = "{http://plain.winery.opentosca.org/servicetemplates}ServiceTemplateWithAllReqCapVariants";
        CompletableFuture<List<ModelProvenanceElement>> history = this.accountabilityManager.getHistory(processId);
        List<ModelProvenanceElement> historyElements = history.get();

        assertEquals(2, historyElements.size());// we manually added 2

        historyElements.forEach(
            historyElement -> assertTrue(StringUtils.isNotEmpty(historyElement.getAuthorAddress()))
        );
    }

    @Test
    void getHistoryWithoutAuthenticationData() throws Exception {
        String processId = "SomeProcessIdForIvalidTestingPurposeOnly";
        String fileId = "not needed in this test";

        CompletableFuture<List<FileProvenanceElement>> history = this.accountabilityManager.getHistory(processId, fileId);
        List<FileProvenanceElement> historyElements = history.get();
        FileProvenanceElement element = historyElements.get(0);

        assertFalse(element.isAuthorized());
        assertEquals("no authorization data stored in the blockchain", element.getAuthorAddress());
    }
}
