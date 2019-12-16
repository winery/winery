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
package org.eclipse.winery.accountability.model;

import org.apache.commons.lang3.StringUtils;

public abstract class BlockchainElement implements Comparable {
    /**
     * Blockchain transaction hash identifying the inserted state
     */
    private String transactionHash;
    private long unixTimestamp;

    public BlockchainElement(String transactionHash, long unixTimestamp) {
        this.transactionHash = transactionHash.toLowerCase();
        this.unixTimestamp = unixTimestamp;
    }
    
    public BlockchainElement() {
        
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash.toLowerCase();
    }

    public long getUnixTimestamp() {
        return unixTimestamp;
    }

    public void setUnixTimestamp(long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof BlockchainElement) {
            return Long.compare(this.unixTimestamp, ((BlockchainElement) o).unixTimestamp);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockchainElement) {
            BlockchainElement other = (BlockchainElement)obj;

            return
                StringUtils.equalsIgnoreCase(other.getTransactionHash(), this.getTransactionHash()) &&
                    other.getUnixTimestamp() == this.getUnixTimestamp();

        }

        return false;
    }

    @Override
    public int hashCode() {
        if (this.getTransactionHash() != null) {
            return Integer.parseUnsignedInt(this.getTransactionHash().subSequence(2, Math.min(9, this.getTransactionHash().length()))
                .toString(), 16);
        }

        return (int)getUnixTimestamp();
    }
}
