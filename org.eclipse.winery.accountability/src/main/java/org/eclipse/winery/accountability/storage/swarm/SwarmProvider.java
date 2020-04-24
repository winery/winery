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
package org.eclipse.winery.accountability.storage.swarm;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.accountability.storage.ImmutableStorageProvider;

public class SwarmProvider implements ImmutableStorageProvider {
    private static ExecutorService executorService = null;
    private final String swarmUrl;

    public SwarmProvider(String swarmUrl) {
        this.swarmUrl = swarmUrl + "/bzz-raw:/";
    }

    private static ExecutorService getExecutorService() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(10);
        }

        return executorService;
    }

    @Override
    public CompletableFuture<String> store(InputStream input) {
        Client client = ClientBuilder.newClient();

        return CompletableFuture.supplyAsync(
            () -> client.target(swarmUrl)
                .request()
                .accept(MediaType.TEXT_PLAIN)
                .post(Entity.entity(input, MediaType.APPLICATION_OCTET_STREAM_TYPE), String.class),
            getExecutorService()
        );
    }

    @Override
    public CompletableFuture<InputStream> retrieve(String address) {
        Client client = ClientBuilder.newClient();

        return CompletableFuture.supplyAsync(
            () -> client.target(swarmUrl + address + "/")
                .request()
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .get(InputStream.class),
            getExecutorService()
        );
    }

    @Override
    public void close() {
        if (executorService != null)
            executorService.shutdown();
    }
}
