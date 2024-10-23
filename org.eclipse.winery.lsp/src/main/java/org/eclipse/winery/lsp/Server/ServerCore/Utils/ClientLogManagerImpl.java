/*******************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.lsp.Server.ServerCore.Utils;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.winery.lsp.Server.ServerAPI.API.ClientLogManager;
import org.eclipse.winery.lsp.Server.ServerAPI.API.context.LSContext;

public class ClientLogManagerImpl implements ClientLogManager {
    
    private static final LSContext.Key<ClientLogManager> CLIENT_LOG_MANAGER_KEY = new LSContext.Key<>();

    private final LanguageClient client;

    private ClientLogManagerImpl(LanguageClient client) {
        this.client = client;
    }

    public static ClientLogManager getInstance(LSContext serverContext) {
        ClientLogManager clientLogManager = serverContext.get(CLIENT_LOG_MANAGER_KEY);
        if (clientLogManager == null) {
            clientLogManager = new ClientLogManagerImpl(serverContext.getClient());
        }

        return clientLogManager;
    }

    @Override
    public void publishInfo(String message) {
        this.client.logMessage(this.getMessageParams(message, MessageType.Info));
    }

    @Override
    public void publishLog(String message) {
        this.client.logMessage(this.getMessageParams(message, MessageType.Log));
    }

    @Override
    public void publishError(String message) {
        this.client.logMessage(this.getMessageParams(message, MessageType.Error));
    }

    @Override
    public void publishWarning(String message) {
        this.client.logMessage(this.getMessageParams(message, MessageType.Warning));
    }

    @Override
    public void showErrorMessage(String message) {
        this.client.showMessage(this.getMessageParams(message, MessageType.Error));
    }

    @Override
    public void showInfoMessage(String message) {
        this.client.showMessage(this.getMessageParams(message, MessageType.Info));
    }

    @Override
    public void showLogMessage(String message) {
        this.client.showMessage(this.getMessageParams(message, MessageType.Warning));
    }

    private MessageParams getMessageParams(String message, MessageType type) {
        MessageParams messageParams = new MessageParams();
        messageParams.setMessage(message);
        messageParams.setType(type);

        return messageParams;
    }
}
