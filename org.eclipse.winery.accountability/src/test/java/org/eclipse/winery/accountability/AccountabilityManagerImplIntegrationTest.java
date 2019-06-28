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

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.accountability.blockchain.BlockchainAccess;
import org.eclipse.winery.accountability.blockchain.BlockchainFactory;
import org.eclipse.winery.accountability.blockchain.ethereum.AuthorizationSmartContractWrapper;
import org.eclipse.winery.accountability.blockchain.ethereum.EthereumAccessLayer;
import org.eclipse.winery.accountability.blockchain.ethereum.ProvenanceSmartContractWrapper;
import org.eclipse.winery.accountability.exceptions.EthereumException;
import org.eclipse.winery.accountability.model.FileProvenanceElement;
import org.eclipse.winery.accountability.model.ModelProvenanceElement;
import org.eclipse.winery.accountability.model.authorization.AuthorizationInfo;
import org.eclipse.winery.accountability.storage.ImmutableStorageProvider;
import org.eclipse.winery.accountability.storage.ImmutableStorageProviderFactory;

import org.apache.commons.lang3.StringUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import sun.security.x509.GeneralName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@EnabledIf("(new java.io.File(\"C:/Ethereum/keystore/UTC--2019-01-23T10-48-04.632976800Z--44fb31577305b7b6ed8ff05ee7c0f07b3cc99306\").exists())")
//remove d to enable the relative path
@EnabledIf("(new java.io.File(\"./UTC--2019-01-23T10-48-04.632976800Z--44fb31577305b7b6ed8ff05ee7c0f07b3cc99306\").exists())")
class AccountabilityManagerImplIntegrationTest {

    private ContractDeployer deploying;
    private GanacheManager ganacheManager;
    
    private static final String CONFIGURATION_FILE_NAME = "defaultaccountabilityconfig.properties";
    private AccountabilityManagerImpl provenance;

    @BeforeEach
    public void setUp() throws Exception {

        try (InputStream propsStream = getClass().getClassLoader().getResourceAsStream(CONFIGURATION_FILE_NAME)) {
        this.ganacheManager = new GanacheManager();
        this.ganacheManager.startGanache();
        this.ganacheManager.startGanache();

            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() < start + 10000)
            {
            }


            Properties props = new Properties();
        props.load(propsStream);
        this.deploying = new ContractDeployer(props);
        props.setProperty("ethereum-provenance-smart-contract-address", this.deploying.deployProvenance());
            props.setProperty("ethereum-authorization-smart-contract-address", this.deploying.deployAuthorization());
            BlockchainAccess blockchainAccess = BlockchainFactory
                .getBlockchainAccess(BlockchainFactory.AvailableBlockchains.ETHEREUM, props);
            ImmutableStorageProvider storageProvider = ImmutableStorageProviderFactory
                .getStorageProvider(ImmutableStorageProviderFactory.AvailableImmutableStorages.SWARM, props);
            this.provenance = new AccountabilityManagerImpl(blockchainAccess, storageProvider);
        }
    }
    
    @AfterEach
    public void killGanache(){
        this.ganacheManager.stopGanache();
    }
    @Test
    void addParticipant() throws Exception {

        String processId = "ServiceTemplateWithAllReqCapVariants";
        String participantBlockchainId = "0x0000000000000000000000000111111222223333";
        String participantName = "Ghareeb";

        CompletableFuture<String> authorize = this.provenance.authorize(processId, participantBlockchainId, participantName);
        assertNotNull(authorize.get());

        CompletableFuture<AuthorizationInfo> authorization = this.provenance.getAuthorization(processId);
        AuthorizationInfo authorizationInfo = authorization.get();

        assertTrue(authorizationInfo.isAuthorized(participantBlockchainId));
    }

    @Test
    void getHistory() throws Exception {
        this.deploying.makehistory(); //method that tries to push the needed records before the Test
        String processId = "{http://plain.winery.opentosca.org/servicetemplates}ServiceTemplateWithAllReqCapVariants";

        CompletableFuture<List<ModelProvenanceElement>> history = this.provenance.getHistory(processId);
        List<ModelProvenanceElement> historyElements = history.get();

        assertEquals(2,historyElements.size() );// we manually added 2

        historyElements.forEach(

            historyElement -> assertTrue(StringUtils.isNotEmpty(historyElement.getAuthorAddress()))
        );
    }

    @Test
    void getHistoryWithoutAuthenticationData() throws Exception {
        String processId = "SomeProcessIdForIvalidTestingPurposeOnly";
        String fileId = "not needed in this test";

        CompletableFuture<List<FileProvenanceElement>> history = this.provenance.getHistory(processId, fileId);
        List<FileProvenanceElement> historyElements = history.get();
        FileProvenanceElement element = historyElements.get(0);

        assertFalse(element.isAuthorized());
        assertEquals("no authorization data stored in the blockchain", element.getAuthorAddress());
    }
}
