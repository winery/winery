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
package org.eclipse.winery.provenance.model.authorization;

import java.util.List;
import java.util.Optional;

/**
 * Allows querying various information about authorized participants.
 */
public interface AuthorizationInfo {

    /**
     * Checks whether a participant with the given blockchain address is part of the tree.
     *
     * @param blockchainAddress the blockchain address of the participant we want to verify
     * @return true if the participant is authorized, otherwise false.
     */
    boolean isAuthorized(String blockchainAddress);

    /**
     * Gets the blockchain address of the service owner (the root of the tree).
     *
     * @return the blockchain address of the service owner (the root of the tree) if the tree is not empty.
     */
    Optional<String> getServiceOwnerBlockchainAddress();

    /**
     * Gets the real-world identity of a given participant
     *
     * @param blockchainAddress the blockchain address identifying the participant
     * @return the real-world-identity of the participant if in the tree-of-trust.
     */
    Optional<String> getRealWorldIdentity(String blockchainAddress);

    /**
     * Gets the lineage of authorization of a given participant (the path in the tree starting from the root -the Service
     * Owner- and ending at the node of the given participant)
     *
     * @param blockchainAddress the blockchain address of the participant
     * @return the lineage of authorization of a given participant if in the tree-of-trust
     */
    Optional<List<AuthorizationNode>> getAuthorizationLineage(String blockchainAddress);
}
