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
public class Authorization extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610246806100206000396000f3006080604052600436106100405763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166389121ae98114610045575b600080fd5b34801561005157600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100f894369492936024939284019190819084018382808284375050604080516020601f818a01358b0180359182018390048302840183018552818452989b73ffffffffffffffffffffffffffffffffffffffff8b35169b909a9099940197509195509182019350915081908401838280828437509497506100fa9650505050505050565b005b8173ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16846040518082805190602001908083835b602083106101585780518252601f199092019160209182019101610139565b51815160209384036101000a60001901801990921691161790526040805192909401829003822081835289518383015289519096507fc48d9b34e49168f1453c1f74e666c288cfd8370d3113c297c2a70f9d8ec64ea095508994929350839283019185019080838360005b838110156101db5781810151838201526020016101c3565b50505050905090810190601f1680156102085780820380516001836020036101000a031916815260200191505b509250505060405180910390a45050505600a165627a7a7230582080bc272357d346ffbad3eb04357f62fac8dec1826d45ed3c5e1bfc76951cbf040029";

    public static final String FUNC_AUTHORIZE = "authorize";

    public static final Event AUTHORIZED_EVENT = new Event("Authorized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    protected Authorization(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Authorization(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> authorize(String _resourceIdentifier, String _authorized, String _realWorldIdentity) {
        final Function function = new Function(
                FUNC_AUTHORIZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_resourceIdentifier), 
                new org.web3j.abi.datatypes.Address(_authorized), 
                new org.web3j.abi.datatypes.Utf8String(_realWorldIdentity)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<AuthorizedEventResponse> getAuthorizedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(AUTHORIZED_EVENT, transactionReceipt);
        ArrayList<AuthorizedEventResponse> responses = new ArrayList<AuthorizedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AuthorizedEventResponse typedResponse = new AuthorizedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._resourceIdentifier = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._authorizer = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._authorized = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.realWorldIdentity = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<AuthorizedEventResponse> authorizedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, AuthorizedEventResponse>() {
            @Override
            public AuthorizedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(AUTHORIZED_EVENT, log);
                AuthorizedEventResponse typedResponse = new AuthorizedEventResponse();
                typedResponse.log = log;
                typedResponse._resourceIdentifier = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._authorizer = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._authorized = (String) eventValues.getIndexedValues().get(2).getValue();
                typedResponse.realWorldIdentity = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<AuthorizedEventResponse> authorizedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(AUTHORIZED_EVENT));
        return authorizedEventObservable(filter);
    }

    public static RemoteCall<Authorization> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Authorization.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Authorization> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Authorization.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static Authorization load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Authorization(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Authorization load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Authorization(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class AuthorizedEventResponse {
        public Log log;

        public byte[] _resourceIdentifier;

        public String _authorizer;

        public String _authorized;

        public String realWorldIdentity;
    }
}
