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

import java.util.Properties;

import org.eclipse.winery.accountability.blockchain.ethereum.generated.Authorization;
import org.eclipse.winery.accountability.blockchain.ethereum.generated.Provenance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

/**
 * Deploys the required smart contracts without depending on accountability layer classes
 */
public class ContractDeployer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractDeployer.class);
    private Credentials credentials;
    private Web3j web3j;

    /**
     * @param props The properties instance that allows to connect to a web3 provider and to unlock a credentials file
     */
    public ContractDeployer(Properties props) throws Exception {
        //loading configuration file
        String url = props.getProperty("geth-url");
        String password = props.getProperty("ethereum-password");
        String credentialsFilePath = props.getProperty("ethereum-credentials-file-path");

        //initialize Websocket
        web3j = Web3j.build(new HttpService(url));
        credentials = WalletUtils.loadCredentials(password,
            credentialsFilePath);
    }

    /**
     * Deploys the provenance smart contract
     * 
     * @return Address of the smart Contract that was deployed
     */
    public String deployProvenance() throws Exception {
        final StaticGasProvider provider = new DefaultGasProvider();
        Provenance provenance = Provenance.deploy(web3j, credentials, provider).send();
        LOGGER.debug("Provenance SC Address: " + provenance.getContractAddress());

        return provenance.getContractAddress();
    }

    /**
     * Deploys the authorization smart contract.
     * 
     * @return Address of the smart Contract that was deployed
     */
    public String deployAuthorization() throws Exception {
        final StaticGasProvider provider = new DefaultGasProvider();
        Authorization authorization = Authorization.deploy(web3j, credentials, provider).send();
        LOGGER.debug("Provenance SC Address: " + authorization.getContractAddress());

        return authorization.getContractAddress();
    }
}
