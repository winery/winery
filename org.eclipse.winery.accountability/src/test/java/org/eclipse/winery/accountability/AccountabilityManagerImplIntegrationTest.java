/********************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.accountability.blockchain.BlockchainAccess;
import org.eclipse.winery.accountability.blockchain.BlockchainFactory;
import org.eclipse.winery.accountability.model.FileProvenanceElement;
import org.eclipse.winery.accountability.model.ModelProvenanceElement;
import org.eclipse.winery.accountability.model.authorization.AuthorizationInfo;
import org.eclipse.winery.accountability.storage.ImmutableStorageProvider;
import org.eclipse.winery.accountability.storage.ImmutableStorageProviderFactory;
import org.eclipse.winery.common.configuration.AccountabilityConfigurationManager;
import org.eclipse.winery.common.configuration.Environments;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("This test seems to fail transiently on test infrastructure")
class AccountabilityManagerImplIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(AccountabilityManagerImplIntegrationTest.class);
    private static final String privateKey = "0xb1211de9fb36bc81c9e269e1f6b783d80e01f3709562bb78451d1a956b3c2038";
    private AccountabilityManagerImpl accountabilityManager;

    @BeforeAll
    static void startGanache() throws Exception {
        GanacheManager ganacheManager = GanacheManager.getInstance(privateKey);
        ganacheManager.startGanache();
    }

    @AfterAll
    static void killGanache() throws InterruptedException {
        GanacheManager ganacheManager = GanacheManager.getInstance(privateKey);
        ganacheManager.stopGanache();
    }

    @BeforeEach
    public void setUp() {
        AccountabilityConfigurationManager.getInstance().setDefaultKeystore();
        Environments.getInstance().getAccountabilityConfig().setEthereumPassword("winery");
        Environments.getInstance().getAccountabilityConfig().setGethUrl("http://127.0.0.1:8545");
        Environments.getInstance().getAccountabilityConfig().setSwarmGatewayUrl("https://swarm-gateways.net/");
        // deploy smart contracts to (a new address in) the blockchain
        try {
        ContractDeployer deployer = new ContractDeployer(Environments.getInstance().getAccountabilityConfig());
        Environments.getInstance().getAccountabilityConfig().setEthereumProvenanceSmartContractAddress(deployer.deployProvenance());
        Environments.getInstance().getAccountabilityConfig().setEthereumAuthorizationSmartContractAddress(deployer.deployAuthorization());
        // reset the contents of the accountability layer so older, possibly incompatible instances are nullified.
        BlockchainFactory.reset();
        ImmutableStorageProviderFactory.reset();
        // create new instances

        BlockchainAccess blockchainAccess = BlockchainFactory
            .getBlockchainAccess(BlockchainFactory.AvailableBlockchains.ETHEREUM, Environments.getInstance().getAccountabilityConfig());
        ImmutableStorageProvider storageProvider = ImmutableStorageProviderFactory
            .getStorageProvider(ImmutableStorageProviderFactory.AvailableImmutableStorages.SWARM, Environments.getInstance().getAccountabilityConfig());

        this.accountabilityManager = new AccountabilityManagerImpl(blockchainAccess, storageProvider);
        } catch (Exception e) {
            Preconditions.condition(false, e.getMessage());
        }
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
