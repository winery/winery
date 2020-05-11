/*******************************************************************************
 * Copyright (c) 2012-2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.client;

import org.eclipse.winery.repository.backend.IWineryRepository;

public interface IWineryRepositoryClient extends IWineryRepository {
    /**
     * Adds an URI to the list of known repositories
     * <p>
     * SIDE EFFECT: If currently no primary repository is defined, the given repository is used as primary repository
     *
     * @param uri the URI of the repository
     */
    void addRepository(String uri);

    /**
     * Get the currently defined primary repository
     */
    String getPrimaryRepository();

    /**
     * Sets the primary repository
     * <p>
     * SIDE EFFECT: If the repository is not known as general repository (via addRepository), the given repository is
     * added to the list of known repositories
     */
    void setPrimaryRepository(String uri);

    /**
     * Checks whether the primary repository is available to be used. Typically, this method should return "true". In
     * case of network or server failures, the result is "false". Note that the availability may change over time and
     * also a repository might become unavailable during querying it.
     * <p>
     * This method also returns "false" if no primary repository is set. For instance, this is the case of no repository
     * is registered at the client.
     *
     * @return true if the repository is reachable over network, false otherwise
     */
    boolean primaryRepositoryAvailable();
}
