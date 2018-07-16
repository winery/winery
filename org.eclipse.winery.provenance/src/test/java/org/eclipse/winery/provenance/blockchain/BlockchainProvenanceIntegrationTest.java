/*******************************************************************************
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

package org.eclipse.winery.provenance.blockchain;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.provenance.model.HistoryElement;
import org.eclipse.winery.provenance.model.authorization.AuthorizationInfo;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIf("(new java.io.File(\"C:/Ethereum/keystore/UTC--2018-03-05T15-33-22.456000000Z--e4b51a3d4e77d2ce2a9d9ce107ec8ec7cff5571d.json\").exists())")
class BlockchainProvenanceIntegrationTest {

    private BlockchainProvenance provenance;

    @BeforeEach
    public void setUp() throws Exception {
        this.provenance = new BlockchainProvenance(BlockchainFactory.AvailableBlockchains.ETHEREUM);
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
        String processId = "{http://plain.winery.opentosca.org/servicetemplates}ServiceTemplateWithAllReqCapVariants";

        CompletableFuture<List<HistoryElement>> history = this.provenance.getHistory(processId);
        List<HistoryElement> historyElements = history.get();

        assertEquals(11, historyElements.size());

        historyElements.forEach(
            historyElement -> assertTrue(StringUtils.isNotEmpty(historyElement.getStateSetterAddress()))
        );
    }

    @Test
    void getHistoryWithoutAuthenticationData() throws Exception {
        String processId = "SomeProcessIdForIvalidTestingPurposeOnly";
        String fileId = "not needed in this test";

        CompletableFuture<List<HistoryElement>> history = this.provenance.getHistory(processId, fileId);
        List<HistoryElement> historyElements = history.get();
        HistoryElement element = historyElements.get(0);

        assertFalse(element.isAuthorized());
        assertEquals("no authorization data stored in the blockchain", element.getStateSetterAddress());
    }
}
