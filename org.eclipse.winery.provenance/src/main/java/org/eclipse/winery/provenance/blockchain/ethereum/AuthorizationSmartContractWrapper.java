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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.provenance.blockchain.ethereum.generated.Authorization;
import org.eclipse.winery.provenance.exceptions.EthereumException;
import org.eclipse.winery.provenance.model.authorization.AuthorizationElement;
import org.eclipse.winery.provenance.model.authorization.AuthorizationInfo;
import org.eclipse.winery.provenance.model.authorization.AuthorizationTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import rx.Subscriber;
import rx.Subscription;

/**
 * Provide access to the authorization smart contract
 */
public class AuthorizationSmartContractWrapper extends SmartContractWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationSmartContractWrapper.class);

    AuthorizationSmartContractWrapper(Web3j web3j, Contract contract) {
        super(web3j, contract);
    }

    public CompletableFuture<String> authorize(final String identifier, final String authorizedEthereumAddress,
                                               final String authorizedIndentity) {
        return ((Authorization) contract).authorize(identifier, authorizedEthereumAddress,
            authorizedIndentity)
            .sendAsync()
            // replace the complete receipt with the transaction hash only.
            .thenApply(TransactionReceipt::getTransactionHash);
    }

    /**
     * Retrieves the {@link AuthorizationInfo} from the blockchain.
     * If no authorization data can be retrieved, the completable future returns <code>null</code>.
     *
     * @param identifier The process identifier identifying the collaboration process.
     * @return A completable future containing the authorization information.
     */
    public CompletableFuture<AuthorizationInfo> getAuthorizationTree(final String identifier) {
        // eventName, indexed parameters, unindexed parameters
        final Event event = new Event("Authorized",
            Arrays.asList(new TypeReference<Utf8String>() {
            }, new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }),
            Collections.singletonList(new TypeReference<Utf8String>() {
            }));
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST,
            contract.getContractAddress()).
            addSingleTopic(EventEncoder.encode(event)).
            addOptionalTopics(Hash.sha3String(identifier)).
            addNullTopic().
            addNullTopic();
        final CompletableFuture<AuthorizationInfo> result = new CompletableFuture<>();

        try {
            final int recordsCount = web3j.ethGetLogs(filter).send().getLogs().size();
            LOGGER.info(recordsCount + " authorization elements detected.");

            if (recordsCount > 0) {
                final Subscription subscription = ((Authorization) contract).authorizedEventObservable(filter)
                    .subscribe(new Subscriber<Authorization.AuthorizedEventResponse>() {
                        private List<AuthorizationElement> authorizationElements = new ArrayList<>();

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable throwable) {
                            LOGGER.error("Error detected. Reason: " + throwable.getMessage());
                            result.completeExceptionally(new EthereumException(throwable));
                        }

                        @Override
                        public void onNext(Authorization.AuthorizedEventResponse authorizedEventResponse) {
                            try {
                                final AuthorizationElement currentElement = generateAuthorizationElement(authorizedEventResponse);
                                authorizationElements.add(currentElement);

                                if (authorizationElements.size() == recordsCount) {
                                    final AuthorizationTree tree = new AuthorizationTree(authorizationElements);
                                    result.complete(tree);
                                }
                            } catch (EthereumException e) {
                                result.completeExceptionally(e);
                            }
                        }
                    });
                // unsubscribe the observable when the CompletableFuture completes (this frees threads)
                result.whenComplete((r, e) -> subscription.unsubscribe());
            } else { // empty result
                result.complete(null);
            }
        } catch (IOException e) {
            final String msg = "Failed detecting the number of authorization elements for the collaboration resource. Reason: " +
                e.getMessage();
            LOGGER.error(msg);
            result.completeExceptionally(new EthereumException(msg, e));
        }

        return result;
    }

    private AuthorizationElement generateAuthorizationElement(final Authorization.AuthorizedEventResponse event) throws EthereumException {
        try {
            final Log log = event.log;
            final AuthorizationElement result = new AuthorizationElement();
            result.setBlockNumber(log.getBlockNumber().longValue());
            result.setTransactionHash(log.getTransactionHash());
            // get the timestamp of the block that includes the tx that includes the authorization record
            result.setUnixTimestamp(web3j.ethGetBlockByHash(log.getBlockHash(), false)
                .send()
                .getBlock()
                .getTimestamp()
                .longValue());

            result.setAuthorizerBlockchainAddress(event._authorizer);
            result.setAuthorizedBlockchainAddress(event._authorized);
            result.setAuthorizedIdentity(event.realWorldIdentity);

            return result;
        } catch (IOException e) {
            final String msg = "Error while fetching block timestamp. Reason: " + e.getMessage();
            LOGGER.error(msg);
            throw new EthereumException(msg, e);
        }
    }
}
