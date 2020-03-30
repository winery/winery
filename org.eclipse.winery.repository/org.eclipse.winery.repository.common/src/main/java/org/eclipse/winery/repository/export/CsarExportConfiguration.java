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

package org.eclipse.winery.repository.export;

public enum CsarExportConfiguration {
    /**
     * Indicates that all refs included in the exported CSAR should be stored in an immutable file storage, and that
     * the address to locate them in that storage should be included in the tosca meta-file
     */
    STORE_IMMUTABLY,

    /**
     * Indicates the hash of all files referenced in the exported CSAR should be included in the tosca meta-file
     */
    INCLUDE_HASHES;
}
