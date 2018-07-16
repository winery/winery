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

import org.eclipse.winery.provenance.blockchain.ethereum.generated.Authorization;
import org.eclipse.winery.provenance.blockchain.ethereum.generated.Provenance;
import org.eclipse.winery.provenance.config.Configuration;
import org.eclipse.winery.provenance.exceptions.BlockchainException;
import org.eclipse.winery.provenance.exceptions.EthereumException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.DefaultGasProvider;

public class SmartContractProvider {

    private static final Logger log = LoggerFactory.getLogger(EthereumAccessLayer.class);

    private static void validateSmartContract(Contract contract, String address) throws EthereumException {
        try {
            if (!contract.isValid()) {
                final String msg = "Contract at address " + address +
                    " doesn't match the desired contract.";
                log.error(msg);
                throw new EthereumException(msg);
            }
        } catch (IOException e) {
            final String msg = "Error while checking the validity of referenced smart contract. Reason: "
                + e.getMessage();
            log.error(msg);

            throw new EthereumException(msg, e);
        }
    }

    public static Provenance buildProvenanceSmartContract(final Web3j web3j, final Credentials credentials) throws BlockchainException {
        final String contractAddress =
            Configuration.getInstance().properties.getProperty("ethereum-provenance-smart-contract-address");
        final Provenance contract = Provenance.load(contractAddress, web3j, credentials, DefaultGasProvider.GAS_PRICE,
            DefaultGasProvider.GAS_LIMIT);

        validateSmartContract(contract, contractAddress);
        return contract;
    }

    public static Authorization buildAuthorizationSmartContract(final Web3j web3j, final Credentials credentials) throws BlockchainException {
        final String contractAddress =
            Configuration.getInstance().properties.getProperty("ethereum-authorization-smart-contract-address");
        final Authorization contract = Authorization.load(contractAddress, web3j, credentials, DefaultGasProvider.GAS_PRICE,
            DefaultGasProvider.GAS_LIMIT);

        validateSmartContract(contract, contractAddress);
        return contract;
    }
}
