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

public class FileProvenanceElement extends ProvenanceElement {
    private String fileHash;
    private String addressInImmutableStorage;
    private String fileName;
    
    public FileProvenanceElement(String transactionHash, long unixTimestamp, String authorAddress) {
        super(transactionHash, unixTimestamp, authorAddress);
    }
    
    public FileProvenanceElement(ProvenanceElement element) {
        this(element.getTransactionHash(), element.getUnixTimestamp(), element.getAuthorAddress());
        this.setAuthorized(element.isAuthorized());
        this.setAuthorName(element.getAuthorName());
    }

    public FileProvenanceElement() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAddressInImmutableStorage() {
        return addressInImmutableStorage;
    }

    public void setAddressInImmutableStorage(String addressInImmutableStorage) {
        this.addressInImmutableStorage = addressInImmutableStorage;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
