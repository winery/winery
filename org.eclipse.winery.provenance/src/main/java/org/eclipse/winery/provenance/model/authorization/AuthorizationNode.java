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

import org.eclipse.winery.provenance.model.ProvenanceElement;

/**
 * Represents one node in the tree-of-trust (authorization tree)
 */
public class AuthorizationNode extends ProvenanceElement {

    private String address;
    private String identity;

    public AuthorizationNode() {
    }

    public AuthorizationNode(String transactionHash, long blockNumber, long unixTimestamp, String address, String identity) {
        super(transactionHash, blockNumber, unixTimestamp);
        this.address = address;
        this.identity = identity;
    }

    public AuthorizationNode(ProvenanceElement element, String address, String identity) {
        this(element.getTransactionHash(), element.getBlockNumber(), element.getUnixTimestamp(), address, identity);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Override
    public String toString() {
        return identity + "@" + address;
    }
}
