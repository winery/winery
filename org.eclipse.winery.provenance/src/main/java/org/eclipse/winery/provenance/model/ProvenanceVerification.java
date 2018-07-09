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

package org.eclipse.winery.provenance.model;

public enum ProvenanceVerification {
    VERIFIED,
    INVALID,
    UNAUTHORIZED,
    ID_NOT_FOUND,
    NO_MANIFEST_FILE_IN_PROVENANCE_LAYER,
    NO_HASH_AVAILABLE,
    NO_AUTHORIZATION_DATA_AVAILABLE
}
