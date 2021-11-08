/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.common.configuration;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountabilityConfigurationObject extends AbstractConfigurationObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountabilityConfigurationObject.class);
    private final String key = "accountability.";
    private String ethereumPassword;
    private String gethUrl;
    private String ethereumCredentialsFileName;
    private String ethereumProvenanceSmartContractAddress;
    private String ethereumAuthorizationSmartContractAddress;
    private String swarmGatewayUrl;

    AccountabilityConfigurationObject(YAMLConfiguration configuration) {
        this.update(configuration);
    }

    @Override
    void save() {
        configuration.setProperty(key + "ethereum-password", this.ethereumPassword);
        configuration.setProperty(key + "geth-url", this.gethUrl);
        configuration.setProperty(key + "ethereum-credentials-file-name", this.ethereumCredentialsFileName);
        configuration.setProperty(key + "ethereum-provenance-smart-contract-address", this.ethereumProvenanceSmartContractAddress);
        configuration.setProperty(key + "ethereum-authorization-smart-contract-address", this.ethereumAuthorizationSmartContractAddress);
        configuration.setProperty(key + "swarm-gateway-url", this.swarmGatewayUrl);
        Environment.getInstance().save();
    }

    @Override
    void update(YAMLConfiguration configuration) {
        this.configuration = configuration;
        ethereumPassword = this.configuration.getString(key + "ethereum-password", "winery");
        gethUrl = this.configuration.getString(key + "geth-url", "http://localhost:8545");
        ethereumCredentialsFileName = this.configuration.getString(key + "ethereum-credentials-file-name", "");
        ethereumProvenanceSmartContractAddress = this.configuration.getString(key + "ethereum-provenance-smart-contract-address", "0x7a7286d6a5bc548234850821d57c649Eb71A8519");
        ethereumAuthorizationSmartContractAddress = this.configuration.getString(key + "ethereum-authorization-smart-contract-address", "0x8414D5f1AF1749B349D9cE88dEB28E5aCB19E417");
        swarmGatewayUrl = this.configuration.getString(key + "swarm-gateway-url", "http://localhost");
    }

    @Override
    /**
     * This loads the default keystore file from resources and sets it as the current keystore file in the
     * configuration, when there is no custom keystore file listed.
     * This method does not check for the existence of a file under that path.
     */
    void initialize() {
        if (this.ethereumCredentialsFileName.equals("")) {
            AccountabilityConfigurationManager.getInstance().setDefaultKeystore();
            this.save();
        }
    }

    public String getEthereumPassword() {
        return ethereumPassword;
    }

    public void setEthereumPassword(String ethereumPassword) {
        this.ethereumPassword = ethereumPassword;
    }

    public String getGethUrl() {
        return gethUrl;
    }

    public void setGethUrl(String gethUrl) {
        this.gethUrl = gethUrl;
    }

    public String getEthereumCredentialsFileName() {
        return ethereumCredentialsFileName;
    }

    public void setEthereumCredentialsFileName(String ethereumCredentialsFileName) {
        this.ethereumCredentialsFileName = ethereumCredentialsFileName;
    }

    /**
     * This will construct an absolute path to the in the configuration specified credential file
     */
    public String getEthereumCredentialsFile() {
        return AccountabilityConfigurationManager.getEthereumCredentialsFilePath() + "/" + this.ethereumCredentialsFileName;
    }

    public String getEthereumProvenanceSmartContractAddress() {
        return ethereumProvenanceSmartContractAddress;
    }

    public void setEthereumProvenanceSmartContractAddress(String ethereumProvenanceSmartContractAddress) {
        this.ethereumProvenanceSmartContractAddress = ethereumProvenanceSmartContractAddress;
    }

    public String getEthereumAuthorizationSmartContractAddress() {
        return ethereumAuthorizationSmartContractAddress;
    }

    public void setEthereumAuthorizationSmartContractAddress(String ethereumAuthorizationSmartContractAddress) {
        this.ethereumAuthorizationSmartContractAddress = ethereumAuthorizationSmartContractAddress;
    }

    public String getSwarmGatewayUrl() {
        return swarmGatewayUrl;
    }

    public void setSwarmGatewayUrl(String swarmGatewayUrl) {
        this.swarmGatewayUrl = swarmGatewayUrl;
    }
}
