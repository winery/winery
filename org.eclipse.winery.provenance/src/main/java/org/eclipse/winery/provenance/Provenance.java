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
 ********************************************************************************/
package org.eclipse.winery.provenance;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.provenance.model.HistoryElement;
import org.eclipse.winery.provenance.model.ProvenanceManifestFields;
import org.eclipse.winery.provenance.model.ProvenanceVerification;
import org.eclipse.winery.provenance.model.authorization.AuthorizationInfo;

public interface Provenance {

    /**
     * Verifies that the given set of files is contained in any previous transaction withing the given id. The files map must contain the
     * fileId as the key and the reference to the actual file as its value.
     *
     * @param id         Identifies the provenance object
     * @param manifestId The identifier of the manifest file inside the given files map
     * @param files      The map of {@link ProvenanceManifestFields} NAME attributes identifying the file inside the manifest pointing to the
     *                   actual file.
     */
    CompletableFuture<Map<String, ProvenanceVerification>> verify(String id, String manifestId, Map<String, File> files);

    /**
     * Stores the given manifest file in as the current state for the given id.
     *
     * @param id    Identifies the provenance object .
     * @param state The manifest file as a string describing the current state of the provenance object.
     */
    CompletableFuture<String> storeState(String id, String state);

    /**
     * Retrieves the history of the given provenance object.
     *
     * @param id Identifies the provenance object.
     */
    CompletableFuture<List<HistoryElement>> getHistory(String id);

    /**
     * Retrieves the history of a specific file inside the given provenance object.
     *
     * @param id     Identifies the provenance object
     * @param fileId The {@link ProvenanceManifestFields} NAME attribute of the file inside the manifest.
     */
    CompletableFuture<List<HistoryElement>> getHistory(String id, String fileId);

    /**
     * Authorizes a new participant for the given collaboration process.
     *
     * @param processIdentifier         the identifier of the collaboration process
     * @param authorizedEthereumAddress the blockchain address of the participant we want to authorize
     * @param authorizedIdentity        the real-world-identity of the participant we want to authorize - e.g., his name
     * @return a completable future that, when completed, returns the transaction hash that contains the authorization information.
     */
    CompletableFuture<String> authorize(String processIdentifier, String authorizedEthereumAddress, String authorizedIdentity);

    /**
     * Gets the authorization tree of a given process which allows various querying capabilities.
     *
     * @param processIdentifier the identifier of the collaboration process
     * @return a completable future that, when completed, returns the authorization tree.
     */
    CompletableFuture<AuthorizationInfo> getAuthorization(String processIdentifier);
}
