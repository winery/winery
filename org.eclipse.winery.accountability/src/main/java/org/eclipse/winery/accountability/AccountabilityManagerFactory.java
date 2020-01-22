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

package org.eclipse.winery.accountability;

import java.util.Objects;

import org.eclipse.winery.accountability.blockchain.BlockchainAccess;
import org.eclipse.winery.accountability.blockchain.BlockchainFactory;
import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.accountability.exceptions.BlockchainException;
import org.eclipse.winery.accountability.storage.ImmutableStorageProvider;
import org.eclipse.winery.accountability.storage.ImmutableStorageProviderFactory;
import org.eclipse.winery.common.configuration.AccountabilityConfigurationObject;
import org.eclipse.winery.common.configuration.Environments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountabilityManagerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountabilityManagerFactory.class);
    private static AccountabilityManager accountabilityManager;

    public static AccountabilityManager getAccountabilityManager() throws AccountabilityException {
        if (Objects.isNull(accountabilityManager)) {
            try {
                AccountabilityConfigurationObject properties = Environments.getInstance().getAccountabilityConfig();
                Environments.getInstance().addConfigurationChangeListener(() -> {
                    BlockchainFactory.reset();
                    ImmutableStorageProviderFactory.reset();
                });
                BlockchainAccess blockchain = BlockchainFactory.getBlockchainAccess(BlockchainFactory.AvailableBlockchains.ETHEREUM, properties);
                ImmutableStorageProvider storageProvider = ImmutableStorageProviderFactory.getStorageProvider(ImmutableStorageProviderFactory.AvailableImmutableStorages.SWARM, properties);
                accountabilityManager = new AccountabilityManagerImpl(blockchain, storageProvider);
            } catch (BlockchainException e) {
                String msg = "Could not instantiate accountability layer: " + e.getMessage();
                LOGGER.error(msg, e);
                throw new AccountabilityException(msg, e);
            }
        }

        return accountabilityManager;
    }
}
