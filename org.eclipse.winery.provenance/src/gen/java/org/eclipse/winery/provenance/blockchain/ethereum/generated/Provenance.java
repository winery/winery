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

package org.eclipse.winery.provenance.blockchain.ethereum.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class Provenance extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610212806100206000396000f3006080604052600436106100405763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416633f4f2e8e8114610045575b600080fd5b34801561005157600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100dc94369492936024939284019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497506100de9650505050505050565b005b3373ffffffffffffffffffffffffffffffffffffffff16826040518082805190602001908083835b602083106101255780518252601f199092019160209182019101610106565b51815160209384036101000a60001901801990921691161790526040805192909401829003822081835288518383015288519096507f97115272ac7620b8a7ff3c7b54a8a06fb928f8c71309478216d21757a5750b2595508894929350839283019185019080838360005b838110156101a8578181015183820152602001610190565b50505050905090810190601f1680156101d55780820380516001836020036101000a031916815260200191505b509250505060405180910390a350505600a165627a7a723058205cbbbcca6ea3516072c8d8c2712c09bd1c25e743d3acd64bd4391bf4f63470e30029";

    public static final String FUNC_ADDRESOURCEVERSION = "addResourceVersion";

    public static final Event RESOURCEVERSION_EVENT = new Event("ResourceVersion", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
    ;

    protected Provenance(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Provenance(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> addResourceVersion(String _resourceIdentifier, byte[] _compressedResource) {
        final Function function = new Function(
                FUNC_ADDRESOURCEVERSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_resourceIdentifier), 
                new org.web3j.abi.datatypes.DynamicBytes(_compressedResource)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<ResourceVersionEventResponse> getResourceVersionEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RESOURCEVERSION_EVENT, transactionReceipt);
        ArrayList<ResourceVersionEventResponse> responses = new ArrayList<ResourceVersionEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ResourceVersionEventResponse typedResponse = new ResourceVersionEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._resourceIdentifier = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._creator = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._compressedResource = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ResourceVersionEventResponse> resourceVersionEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ResourceVersionEventResponse>() {
            @Override
            public ResourceVersionEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RESOURCEVERSION_EVENT, log);
                ResourceVersionEventResponse typedResponse = new ResourceVersionEventResponse();
                typedResponse.log = log;
                typedResponse._resourceIdentifier = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._creator = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._compressedResource = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ResourceVersionEventResponse> resourceVersionEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RESOURCEVERSION_EVENT));
        return resourceVersionEventObservable(filter);
    }

    public static RemoteCall<Provenance> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Provenance.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Provenance> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Provenance.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static Provenance load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Provenance(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Provenance load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Provenance(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class ResourceVersionEventResponse {
        public Log log;

        public byte[] _resourceIdentifier;

        public String _creator;

        public byte[] _compressedResource;
    }
}
