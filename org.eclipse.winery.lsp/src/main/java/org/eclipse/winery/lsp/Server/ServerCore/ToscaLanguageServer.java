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

package org.eclipse.winery.lsp.Server.ServerCore;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;
import org.eclipse.winery.lsp.Server.ServerCore.Utils.ServerInitUtils;

import java.util.concurrent.CompletableFuture;

public class ToscaLanguageServer implements LanguageClientAware, LanguageServer {
    private final TextDocumentService textDocumentService;
    private final WorkspaceService workspaceService;
    private final ToscaLSContentImpl serverContext;
    private LanguageClient client;
    private boolean shutdownInitiated = false;

    public ToscaLanguageServer(TextDocumentService textDocumentService, WorkspaceService workspaceService, ToscaLSContentImpl serverContext) {
        this.textDocumentService = textDocumentService;
        this.workspaceService = workspaceService;
        this.serverContext = serverContext;
    }

    public ToscaLanguageServer() {
        this.workspaceService = null;
        this.serverContext = new ToscaLSContentImpl();
        this.textDocumentService = new ToscaTextDocService(this.serverContext);
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
        this.serverContext.setClient(this.client);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        //setting the client capabilities
        return CompletableFuture.supplyAsync(() -> {
            serverContext.setClientCapabilities(params.
                getCapabilities());
            ServerCapabilities sCapabilities = new
                ServerCapabilities();
            TextDocumentSyncOptions documentSyncOption =
                ServerInitUtils.getDocumentSyncOption();
            CompletionOptions completionOptions =
                ServerInitUtils.getCompletionOptions();
            sCapabilities.setTextDocumentSync(documentSyncOption);
            sCapabilities.setCompletionProvider(completionOptions);
            return new InitializeResult(sCapabilities);
        });
    }

    @Override
    public void initialized(InitializedParams params) {
        MessageParams messageParams = new MessageParams();
        messageParams.setMessage("Tosca Language Server Initiated");
        messageParams.setType(MessageType.Info);
        this.serverContext.getClient().logMessage(messageParams);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        this.shutdownInitiated = true;
        return CompletableFuture.supplyAsync(Object::new);
    }

    @Override
    public void exit() {
        System.exit(this.shutdownInitiated ? 0 : 1);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this.textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
    }
}
