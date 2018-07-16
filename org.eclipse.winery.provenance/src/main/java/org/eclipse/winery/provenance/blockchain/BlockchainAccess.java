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

public interface BlockchainAccess {

    /**
     * Saves a version of the collaborative resource in the blockchain
     *
     * @param processIdentifier the identifier of the collaboration process
     * @param state             the state of the collaborative resource we want to store
     * @return a completable future that, when completed, returns the blockchain address of the transaction that contains
     * the stored version.
     */
    CompletableFuture<String> saveState(final String processIdentifier, final String state);

    /**
     * Gets the history of a given collaboration process
     *
     * @param processIdentifier the identifier of the collaboration process
     * @return a completable future that, when completed, returns a list containing the historic versions of the
     * collaborative resource.
     */
    CompletableFuture<List<HistoryElement>> getProvenance(final String processIdentifier);

    /**
     * Authorizes a new participant for the given collaboration process.
     *
     * @param processIdentifier         the identifier of the collaboration process
     * @param authorizedEthereumAddress the blockchain address of the participant we want to authorize
     * @param authorizedIdentity        the real-world-identity of the participant we want to authorize
     * @return a completable future that, when completed, returns the blockchain address of the transaction that contains
     * the authorization information.
     */
    CompletableFuture<String> authorize(final String processIdentifier, final String authorizedEthereumAddress,
                                        final String authorizedIdentity);

    /**
     * Gets the authorization tree of a given process which allows various querying capabilities.
     *
     * @param processIdentifier the identifier of the collaboration process
     * @return a completable future that, when completed, returns the authorization tree.
     */
    CompletableFuture<AuthorizationInfo> getAuthorizationTree(final String processIdentifier);
}
