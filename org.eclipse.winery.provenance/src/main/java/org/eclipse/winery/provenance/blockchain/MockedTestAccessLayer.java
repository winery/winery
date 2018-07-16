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

import org.eclipse.winery.provenance.model.authorization.AuthorizationInfo;
import org.eclipse.winery.provenance.model.HistoryElement;

/**
 * Workaround to test the Blockchain Access because Mockito and PowerMockito are not supporting JUnit5 up until now...
 */
public class MockedTestAccessLayer implements BlockchainAccess {

    @Override
    public CompletableFuture<String> saveState(String identifier, String state) {
        return null;
    }

    @Override
    public CompletableFuture<List<HistoryElement>> getProvenance(String identifier) {
        return null;
    }

    @Override
    public CompletableFuture<String> authorize(String processIdentifier, String authorizedEthereumAddress, String authorizedIdentity) {
        return null;
    }

    @Override
    public CompletableFuture<AuthorizationInfo> getAuthorizationTree(String processIdentifier) {
        return null;
    }
}
