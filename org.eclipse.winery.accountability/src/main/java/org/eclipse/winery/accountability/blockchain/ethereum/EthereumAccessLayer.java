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
package org.eclipse.winery.accountability.blockchain.ethereum;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.accountability.blockchain.BlockchainAccess;
import org.eclipse.winery.accountability.exceptions.BlockchainException;
import org.eclipse.winery.accountability.exceptions.EthereumException;
import org.eclipse.winery.accountability.model.ModelProvenanceElement;
import org.eclipse.winery.accountability.model.authorization.AuthorizationInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class EthereumAccessLayer implements BlockchainAccess {

    private static final Logger log = LoggerFactory.getLogger(EthereumAccessLayer.class);

    private Credentials credentials;
    private final ProvenanceSmartContractWrapper provenanceContract;
    private final AuthorizationSmartContractWrapper authorizationContract;

    private EthereumAccessLayer(final String nodeUrl, final String credentialsPath, final String credentialsPassword,
                                final String provenanceSmartContractAddress, final String authorizationSmartContractAddress) throws BlockchainException {
        Web3j web3j = Web3j.build(new HttpService(nodeUrl));
        unlockCredentials(credentialsPassword, credentialsPath);
        provenanceContract = new ProvenanceSmartContractWrapper(web3j,
            SmartContractProvider.buildProvenanceSmartContract(web3j, this.credentials, provenanceSmartContractAddress));
        authorizationContract = new AuthorizationSmartContractWrapper(web3j,
            SmartContractProvider.buildAuthorizationSmartContract(web3j, this.credentials, authorizationSmartContractAddress));
    }

    public EthereumAccessLayer(Properties configuration) throws BlockchainException {
        this(
            configuration.getProperty("geth-url"),
            configuration.getProperty("ethereum-credentials-file-path"),
            configuration.getProperty("ethereum-password"),
            configuration.getProperty("ethereum-provenance-smart-contract-address"),
            configuration.getProperty("ethereum-authorization-smart-contract-address")
        );
    }
    
    private void unlockCredentials(String password, String fileSource) throws EthereumException {
        try {
            
            this.credentials = WalletUtils.loadCredentials(password, fileSource);
        } catch (IOException | CipherException e) {
            final String msg = "Error occurred while setting the user credentials for Ethereum. Reason: " +
                e.getMessage();
            log.error(msg);
            throw new EthereumException(msg, e);
        }
    }

    public CompletableFuture<String> saveFingerprint(final String processIdentifier, final String fingerprint) {
        return this.provenanceContract.saveState(processIdentifier, fingerprint);
    }

    public CompletableFuture<List<ModelProvenanceElement>> getProvenance(final String processIdentifier) {
        return this.provenanceContract.getProvenance(processIdentifier);
    }

    public CompletableFuture<String> authorize(final String processIdentifier, final String authorizedEthereumAddress,
                                               final String authorizedIdentity) {
        return this.authorizationContract.authorize(processIdentifier, authorizedEthereumAddress, authorizedIdentity);
    }

    public CompletableFuture<AuthorizationInfo> getAuthorizationTree(final String processIdentifier) {
        return this.authorizationContract.getAuthorizationTree(processIdentifier);
    }
    
    @Override
    public void close() {
        // we can get a reference to web3j from either of the smart contracts.
        if (provenanceContract != null && provenanceContract.web3j != null)
            provenanceContract.web3j.shutdown();
    }
}
