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

public abstract class ProvenanceElement implements Comparable {
    /**
     * Blockchain transaction hash identifying the inserted state
     */
    private String transactionHash;
    private long blockNumber;
    private long unixTimestamp;

    public ProvenanceElement(String transactionHash, long blockNumber, long unixTimestamp) {
        this.transactionHash = transactionHash.toLowerCase();
        this.blockNumber = blockNumber;
        this.unixTimestamp = unixTimestamp;
    }
    
    public ProvenanceElement() {
        
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash.toLowerCase();
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public long getUnixTimestamp() {
        return unixTimestamp;
    }

    public void setUnixTimestamp(long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof ProvenanceElement) {
            return Long.compare(this.unixTimestamp, ((ProvenanceElement) o).unixTimestamp);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
