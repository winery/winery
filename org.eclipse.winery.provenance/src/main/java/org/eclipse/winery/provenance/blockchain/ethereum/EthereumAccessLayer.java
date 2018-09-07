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
package org.eclipse.winery.provenance.blockchain.ethereum;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.provenance.blockchain.BlockchainAccess;
import org.eclipse.winery.provenance.config.Configuration;
import org.eclipse.winery.provenance.exceptions.BlockchainException;
import org.eclipse.winery.provenance.exceptions.EthereumException;
import org.eclipse.winery.provenance.model.HistoryElement;
import org.eclipse.winery.provenance.model.authorization.AuthorizationInfo;

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

    private EthereumAccessLayer(final String nodeUrl, final String credentialsPath, final String credentialsPassword)
        throws BlockchainException {
        Web3j web3j = Web3j.build(new HttpService(nodeUrl));
        unlockCredentials(credentialsPassword, credentialsPath);
        provenanceContract = new ProvenanceSmartContractWrapper(web3j,
            SmartContractProvider.buildProvenanceSmartContract(web3j, this.credentials));
        authorizationContract = new AuthorizationSmartContractWrapper(web3j,
            SmartContractProvider.buildAuthorizationSmartContract(web3j, this.credentials));
    }

    public EthereumAccessLayer() throws BlockchainException {
        this(
            Configuration.getInstance().properties.getProperty("geth-url"),
            Configuration.getInstance().properties.getProperty("ethereum-credentials-file"),
            Configuration.getInstance().properties.getProperty("ethereum-password")
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

    public CompletableFuture<String> saveState(final String processIdentifier, final String state) {
        return this.provenanceContract.saveState(processIdentifier, state);
    }

    public CompletableFuture<List<HistoryElement>> getProvenance(final String processIdentifier) {
        return this.provenanceContract.getProvenance(processIdentifier);
    }

    public CompletableFuture<String> authorize(final String processIdentifier, final String authorizedEthereumAddress,
                                               final String authorizedIdentity) {
        return this.authorizationContract.authorize(processIdentifier, authorizedEthereumAddress, authorizedIdentity);
    }

    public CompletableFuture<AuthorizationInfo> getAuthorizationTree(final String processIdentifier) {
        return this.authorizationContract.getAuthorizationTree(processIdentifier);
    }

    // TODO: refactor into a test which is only enabled, if the ethereum keystore is present: see CsarExporterTest#testPutCsarInBlockchain
    public static void main(String[] args) {
        final String array1 = "Hi Hi Hi Hi";
        final String participant1Address = "0x696c7c33aC2AA448880F7c1e5F85eb8c2401Cf03";
        final String participant1Name = "Ghareeb";
        final String participant1KeystorePath = "C:\\Ethereum\\keystore\\UTC--2018-05-31T17-09-06.917268191Z--696c7c33ac2aa448880f7c1e5f85eb8c2401cf03.json";
        final String participant2Address = "0x76C3d17870216E3644e4708Fa84059474b4464cc";
        final String participant2Name = "Lukas";
        final String participant2KeystorePath = "C:\\Ethereum\\keystore\\UTC--2018-05-31T17-09-36.155943818Z--76c3d17870216e3644e4708fa84059474b4464cc.json";
        final String password = "123456789";
        final String id = "5";
        try {
            EthereumAccessLayer owner = new EthereumAccessLayer();
            EthereumAccessLayer participant1 = new EthereumAccessLayer(
                Configuration.getInstance().properties.getProperty("geth-url"), participant1KeystorePath, password);
            EthereumAccessLayer participant2 = new EthereumAccessLayer(
                Configuration.getInstance().properties.getProperty("geth-url"), participant2KeystorePath, password);

            owner.authorize(id, participant1Address, participant1Name)
                .thenCompose((hash1) -> {
                    log.info("TX hash for authorizing particpant 1 is: " + hash1);
                    return participant1.authorize(id, participant2Address, participant2Name);
                })
                .thenCompose((hash2) -> {
                    log.info("TX hash for authorizing particpant 2 is: " + hash2);
                    return participant2.saveState(id, array1);
                })
                .thenCompose((hash3) -> {
                    log.info("TX hash for saveState is: " + hash3);
                    return owner.getAuthorizationTree(id);
                })
                .thenAcceptBoth(owner.getProvenance(id)
                    , (tree, provenanceElements) -> {
                        final String lastAuthorAddress = provenanceElements.get(provenanceElements.size() - 1)
                            .getStateSetterAddress();
                        if (tree.isAuthorized(lastAuthorAddress))
                            log.info("identity of latest author: " + tree.getRealWorldIdentity(lastAuthorAddress));
                        else
                            log.info("last author is not authorized!!");
                    })
                .exceptionally((throwable) -> {
                    log.error("Error occurred. Details: " + throwable.getMessage());
                    return null;
                });
        } catch (BlockchainException e) {
            e.printStackTrace();
        }
    }
}
