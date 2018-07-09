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

import org.eclipse.winery.provenance.model.authorization.AuthorizationInfo;

public class HistoryElement extends ProvenanceElement {

    private String stateSetterAddress;
    private String name;
    private String fileHash;
    
    /**
     * Contains the stored state in the blockchain
     */
    private String state;
    
    /**
     * True, if the author was authorized to be a certified contributor.
     */
    private boolean authorized;

    public HistoryElement(String transactionHash, long blockNumber, long unixTimestamp, String stateSetterAddress, String state) {
        super(transactionHash, blockNumber, unixTimestamp);
        this.stateSetterAddress = stateSetterAddress.toLowerCase();
        this.state = state;
    }

    public HistoryElement() {
    }

    public String getStateSetterAddress() {
        return stateSetterAddress;
    }

    public void setStateSetterAddress(String stateSetterAddress) {
        this.stateSetterAddress = stateSetterAddress.toLowerCase();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public void setAuthorizedFlag(AuthorizationInfo authorizationInfo) {
        this.setAuthorized(authorizationInfo.isAuthorized(stateSetterAddress));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
