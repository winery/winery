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
package org.eclipse.winery.repository.rest.resources.apiData;

import java.io.Serializable;

public class AccountabilityConfigurationData implements Serializable {
    private static final long serialVersionUID = -2267642315902119999L;
    
    private String blockchainNodeUrl;
    private String activeKeystore;
    private String keystorePassword;
    private String authorizationSmartContractAddress;
    private String provenanceSmartContractAddress;
    private String swarmGatewayUrl;

    public String getBlockchainNodeUrl() {
        return blockchainNodeUrl;
    }

    public void setBlockchainNodeUrl(String blockchainNodeUrl) {
        this.blockchainNodeUrl = blockchainNodeUrl;
    }

    public String getActiveKeystore() {
        return activeKeystore;
    }

    public void setActiveKeystore(String activeKeystore) {
        this.activeKeystore = activeKeystore;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getAuthorizationSmartContractAddress() {
        return authorizationSmartContractAddress;
    }

    public void setAuthorizationSmartContractAddress(String authorizationSmartContractAddress) {
        this.authorizationSmartContractAddress = authorizationSmartContractAddress;
    }

    public String getProvenanceSmartContractAddress() {
        return provenanceSmartContractAddress;
    }

    public void setProvenanceSmartContractAddress(String provenanceSmartContractAddress) {
        this.provenanceSmartContractAddress = provenanceSmartContractAddress;
    }

    public String getSwarmGatewayUrl() {
        return swarmGatewayUrl;
    }

    public void setSwarmGatewayUrl(String swarmGatewayUrl) {
        this.swarmGatewayUrl = swarmGatewayUrl;
    }
}
