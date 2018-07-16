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
 * Represents one authorization entry as it is stored in the blockchain
 */
public class AuthorizationElement extends ProvenanceElement {
    /**
     * The ethereum address of the participant that performed the authorization
     */
    private String authorizerBlockchainAddress;
    /**
     * The ethereum address of the participant that was authorized
     */
    private String authorizedBlockchainAddress;
    /**
     * The real-world identity of the participant that was authorized
     */
    private String authorizedIdentity;
    
    AuthorizationElement(String transactionHash, long blockNumber, long unixTimestamp, String authorizerBlockchainAddress,
                         String authorizedBlockchainAddress, String authorizedIdentity) {
        super(transactionHash, blockNumber, unixTimestamp);
        
        this.authorizerBlockchainAddress = authorizerBlockchainAddress.toLowerCase();
        this.authorizedBlockchainAddress = authorizedBlockchainAddress.toLowerCase();
        this.authorizedIdentity = authorizedIdentity;
    }
    
    public AuthorizationElement() {
        
    }

    public String getAuthorizerBlockchainAddress() {
        return authorizerBlockchainAddress;
    }

    public void setAuthorizerBlockchainAddress(String authorizerBlockchainAddress) {
        this.authorizerBlockchainAddress = authorizerBlockchainAddress.toLowerCase();
    }

    public String getAuthorizedBlockchainAddress() {
        return authorizedBlockchainAddress;
    }

    public void setAuthorizedBlockchainAddress(String authorizedBlockchainAddress) {
        this.authorizedBlockchainAddress = authorizedBlockchainAddress.toLowerCase();
    }

    public String getAuthorizedIdentity() {
        return authorizedIdentity;
    }

    public void setAuthorizedIdentity(String authorizedIdentity) {
        this.authorizedIdentity = authorizedIdentity;
    }
    
}
