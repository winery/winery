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

public class CsarContentProperties {
    
    private String pathInsideCsar;
    private String fileHash;

    /**
     * Address of the file in the immutable file storage
     */
    private String immutableAddress;

    public CsarContentProperties(String pathInsideCsar) {
        this.pathInsideCsar = pathInsideCsar;
    }

    public CsarContentProperties(String pathInsideCsar, String fileHash) {
        this(pathInsideCsar);
        this.fileHash = fileHash;
    }

    public CsarContentProperties(String pathInsideCsar, String fileHash, String immutableAddress) {
        this(pathInsideCsar, fileHash);
        this.immutableAddress = immutableAddress;
    }

    public String getPathInsideCsar() {
        return pathInsideCsar;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getImmutableAddress() {
        return immutableAddress;
    }

    public void setImmutableAddress(String immutableAddress) {
        this.immutableAddress = immutableAddress;
    }
    
    @Override
    public boolean equals(Object other) {
        return 
            other instanceof CsarContentProperties && 
                this.pathInsideCsar != null &&
                this.pathInsideCsar.equals(((CsarContentProperties) other).getPathInsideCsar());
    }
    
    @Override
    public int hashCode() {
        if (this.pathInsideCsar == null)
            return 0;
        
        return this.pathInsideCsar.hashCode();
    }
}
