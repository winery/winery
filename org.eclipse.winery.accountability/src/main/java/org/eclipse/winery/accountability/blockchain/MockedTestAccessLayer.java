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

package org.eclipse.winery.accountability.blockchain;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.accountability.model.ModelProvenanceElement;
import org.eclipse.winery.accountability.model.authorization.AuthorizationInfo;

/**
 * Workaround to test the Blockchain Access because Mockito and PowerMockito are not supporting JUnit5 up until now...
 */
public class MockedTestAccessLayer implements BlockchainAccess {

    @Override
    public CompletableFuture<String> saveFingerprint(String identifier, String fingerprint) {
        return null;
    }

    @Override
    public CompletableFuture<List<ModelProvenanceElement>> getProvenance(String identifier) {
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

    @Override
    public void close() {
        // no resources to release.
    }
}
