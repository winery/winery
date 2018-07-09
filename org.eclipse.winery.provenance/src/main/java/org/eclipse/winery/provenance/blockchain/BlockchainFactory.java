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

package org.eclipse.winery.provenance.blockchain;

import java.util.Objects;

import org.eclipse.winery.provenance.blockchain.ethereum.EthereumAccessLayer;
import org.eclipse.winery.provenance.exceptions.BlockchainException;

public class BlockchainFactory {

    private static BlockchainAccess blockchain;

    public static BlockchainAccess getBlockchainAccess(AvailableBlockchains desiredBlockchain) throws BlockchainException {
        // The requested blockchain technology could be retrieved from the configurations file
        if (Objects.isNull(blockchain)) {
            switch (desiredBlockchain) {
                case ETHEREUM:
                    blockchain = new EthereumAccessLayer();
                    break;
                case TEST:
                default:
                    blockchain = new MockedTestAccessLayer();
                    break;
            }
        }

        return blockchain;
    }

    public enum AvailableBlockchains {
        ETHEREUM,
        TEST
    }
}
