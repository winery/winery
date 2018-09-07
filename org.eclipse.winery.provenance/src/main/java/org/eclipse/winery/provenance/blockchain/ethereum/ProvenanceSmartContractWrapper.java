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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.provenance.blockchain.ethereum.generated.Provenance;
import org.eclipse.winery.provenance.blockchain.util.CompressionUtils;
import org.eclipse.winery.provenance.exceptions.EthereumException;
import org.eclipse.winery.provenance.model.HistoryElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bytes;
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
 * Provide access to the functionality of the provenance smart contract
 */
public class ProvenanceSmartContractWrapper extends SmartContractWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvenanceSmartContractWrapper.class);

    ProvenanceSmartContractWrapper(Web3j web3j, Contract contract) {
        super(web3j, contract);
    }

    public CompletableFuture<String> saveState(final String identifier, final String state) {
        return ((Provenance) contract).addResourceVersion(identifier, CompressionUtils.compress(state.getBytes()))
            .sendAsync()
            // replace the complete receipt with the transaction hash only.
            .thenApply(TransactionReceipt::getTransactionHash);
    }

    public CompletableFuture<List<HistoryElement>> getProvenance(final String identifier) {
        // eventName, indexed parameters, unindexed parameters
        final Event event = new Event("ResourceVersion",
            Arrays.asList(new TypeReference<Utf8String>() {
            }, new TypeReference<Address>() {
            }),
            Collections.singletonList(new TypeReference<Bytes>() {
            }));
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST,
            contract.getContractAddress()).
            addSingleTopic(EventEncoder.encode(event)).
            addOptionalTopics(Hash.sha3String(identifier)).
            addNullTopic();
        final CompletableFuture<List<HistoryElement>> result = new CompletableFuture<>();

        try {
            final int recordsCount = web3j.ethGetLogs(filter).send().getLogs().size();
            LOGGER.info(recordsCount + " provenance elements detected.");

            if (recordsCount > 0) {
                final Subscription subscription = ((Provenance) contract).resourceVersionEventObservable(filter)
                    .subscribe(new Subscriber<Provenance.ResourceVersionEventResponse>() {
                        private List<HistoryElement> provenanceElements = new ArrayList<>();

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable throwable) {
                            LOGGER.error("Error detected. Reason: " + throwable.getMessage());
                            result.completeExceptionally(new EthereumException(throwable));
                        }

                        @Override
                        public void onNext(Provenance.ResourceVersionEventResponse resourceVersionEventResponse) {
                            try {
                                final HistoryElement currentElement = generateProvenanceElement(resourceVersionEventResponse);
                                provenanceElements.add(currentElement);

                                if (provenanceElements.size() == recordsCount) {
                                    result.complete(provenanceElements);
                                }
                            } catch (EthereumException e) {
                                result.completeExceptionally(e);
                            }
                        }
                    });
                // unsubscribe the observable when the CompletableFuture completes (this frees threads)
                result.whenComplete((r, e) -> subscription.unsubscribe());
            } else { // empty result
                result.complete(new ArrayList<>());
            }
        } catch (IOException e) {
            final String msg = "Failed detecting the number of provenance elements for the collaboration resource. Reason: " +
                e.getMessage();
            LOGGER.error(msg);
            result.completeExceptionally(new EthereumException(msg, e));
        }

        return result;
    }

    private HistoryElement generateProvenanceElement(final Provenance.ResourceVersionEventResponse event) throws EthereumException {
        try {
            final Log log = event.log;
            final HistoryElement result = new HistoryElement();
            result.setBlockNumber(log.getBlockNumber().longValue());
            result.setTransactionHash(log.getTransactionHash());
            result.setStateSetterAddress(event._creator);
            // decompress the state
            final byte[] compressedState = event._compressedResource;
            result.setState(new String(CompressionUtils.decompress(compressedState), StandardCharsets.UTF_8));
            // get the timestamp of the block that includes the tx that includes the state change
            result.setUnixTimestamp(web3j.ethGetBlockByHash(log.getBlockHash(), false)
                .send()
                .getBlock()
                .getTimestamp()
                .longValue());

            return result;
        } catch (IOException e) {
            final String msg = "Error while fetching block timestamp. Reason: " + e.getMessage();
            LOGGER.error(msg);
            throw new EthereumException(msg, e);
        }
    }
}
