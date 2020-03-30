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

/**
 * Contains the set of options applicable while exporting a CSAR.
 */
public class CsarExportOptions {
    /**
     * Indicates that the exported CSAR will be registered in the blockchain to maintain its integrity and provenance.
     */
    private boolean addToProvenance;

    public boolean isAddToProvenance() {
        return addToProvenance;
    }

    public void setAddToProvenance(boolean addToProvenance) {
        this.addToProvenance = addToProvenance;
    }
}
