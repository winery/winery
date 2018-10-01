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
 *******************************************************************************/
package org.eclipse.winery.accountability.model;

import java.util.Collection;

public class ModelProvenanceElement extends ProvenanceElement {

    /**
     * Contains the stored fingerprint in the blockchain
     */
    private String fingerprint;
    
    private Collection<FileProvenanceElement> files;
    

    public ModelProvenanceElement(String transactionHash, long unixTimestamp, String authorAddress, String fingerprint) {
        super(transactionHash, unixTimestamp, authorAddress);
        this.fingerprint = fingerprint;
    }

    public ModelProvenanceElement() {
    }
    
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String state) {
        this.fingerprint = state;
    }

    public Collection<FileProvenanceElement> getFiles() {
        return files;
    }

    public void setFiles(Collection<FileProvenanceElement> files) {
        this.files = files;
    }
}
