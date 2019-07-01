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

package org.eclipse.winery.accountability;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;

import org.eclipse.winery.accountability.blockchain.BlockchainAccess;
import org.eclipse.winery.accountability.blockchain.BlockchainFactory;
import org.eclipse.winery.accountability.blockchain.ethereum.EthereumAccessLayer;
import org.eclipse.winery.accountability.blockchain.ethereum.generated.Authorization;
import org.eclipse.winery.accountability.blockchain.ethereum.generated.Provenance;
import org.eclipse.winery.accountability.storage.ImmutableStorageProvider;
import org.eclipse.winery.accountability.storage.ImmutableStorageProviderFactory;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.FileInputStream;

public class ContractDeployer {
    private Credentials credentials;
    private Web3j web3deploycontract;
    private BigInteger gasPrice;
    private BigInteger gasLimit;

    //Attributes for the Contract
    private String password;
    private String filelocation;

    EthereumAccessLayer test;

    //stores Contracts
    private Provenance provenance;
    private Authorization authorization;
    private AccountabilityManagerImpl provenances;
    Properties props;

    /**
     * @param props the File that stores the Information
     * @throws Exception
     */
    public ContractDeployer(Properties props) throws Exception {
        //loading configuration file
        this.props = props;
        String url = props.getProperty("geth-url");
        String password = props.getProperty("ethereum-password");
        String credentialsFilePath = props.getProperty("ethereum-credentials-file-path");

        gasPrice = new BigInteger(props.getProperty("gas-price"));
        gasLimit = new BigInteger(props.getProperty("gas-limit"));

        //initialize Websocket
        web3deploycontract = Web3j.build(new HttpService(url));
        credentials = WalletUtils.loadCredentials(password,
            credentialsFilePath);
    }

    /**
     * @return Address of the smart Contract that was deployed
     */
    public String deployProvenance() throws Exception {
        provenance = Provenance.deploy(web3deploycontract, credentials, gasPrice, gasLimit).send();

        System.err.println("Provenance Address: " + provenance.getContractAddress());

        return provenance.getContractAddress();
    }

    /**
     * @return Address of the smart Contract that was deployed
     */
    public String deployAuthorization() throws Exception {
        authorization = Authorization.deploy(web3deploycontract, credentials, gasPrice, gasLimit).send();

        System.err.println("Provenance Address: " + authorization.getContractAddress());

        return authorization.getContractAddress();
    }

    /**
     * @param Adress the Address to The Account to send Ether
     * @param Ether The Amount of Ether that should be Sent
     **/
    
    public void sendEther(String Adress, double Ether) throws Exception {
        TransactionReceipt transaction = Transfer.sendFunds(web3deploycontract, credentials,
            Adress, BigDecimal.valueOf(Ether), Convert.Unit.ETHER).send();
    }

    /**
     * Deploys Fingerprints and Metafile to the Blockchain
     * @throws Exception
     */
    public void makehistory() throws Exception {
        String file0 = "myTestFile.tosca";
        String manifest = "TOSCA-Meta-Version: 1.0\n" +
            "CSAR-Version: 1.0\n" +
            "Created-By: Winery 2.0.0-SNAPSHOT\n" +
            "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
            "\n" +
            "Name: " + file0 + "\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n" +
            "SHA-256: 97193968948686d6947d4d760d3fe724b9981980056b8902be92e91fbe9e3eed\n";
        
        BlockchainAccess blockchainAccess = BlockchainFactory
            .getBlockchainAccess(BlockchainFactory.AvailableBlockchains.ETHEREUM, props);
        ImmutableStorageProvider storageProvider = ImmutableStorageProviderFactory
            .getStorageProvider(ImmutableStorageProviderFactory.AvailableImmutableStorages.SWARM, props);

        String processIdentifier = "{http://plain.winery.opentosca.org/servicetemplates}ServiceTemplateWithAllReqCapVariants";

        AccountabilityManager test = new AccountabilityManagerImpl(blockchainAccess, storageProvider);
        test.authorize(processIdentifier,
            "44fb31577305b7b6ed8ff05ee7c0f07b3cc99306", "winery").get();
        test.storeFingerprint(processIdentifier, manifest).get();
        test.storeFingerprint(processIdentifier, manifest).get();
        
        FileInputStream stream = new FileInputStream("./UTC--2019-01-23T10-48-04.632976800Z--44fb31577305b7b6ed8ff05ee7c0f07b3cc99306");

        test.storeState(stream);
    }

    
    
}
