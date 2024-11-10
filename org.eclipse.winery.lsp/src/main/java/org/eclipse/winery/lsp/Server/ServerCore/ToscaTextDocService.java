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
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.winery.lsp.Server.ServerAPI.API.context.BaseOperationContext;
import org.eclipse.winery.lsp.Server.ServerAPI.API.context.ContextBuilder;
import org.eclipse.winery.lsp.Server.ServerAPI.API.context.LSContext;
import org.eclipse.winery.lsp.Server.ServerCore.Completion.AutoCompletionHandler;
import org.eclipse.winery.lsp.Server.ServerCore.Utils.CommonUtils;
import org.eclipse.winery.lsp.Server.ServerCore.Validation.DiagnosticsPublisher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ToscaTextDocService implements TextDocumentService {
    private final LSContext serverContext;
    public ToscaTextDocService(LSContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        Path Path = CommonUtils.uriToPath(uri);
        serverContext.setCurrentToscaFilePath(Path);
        String content = params.getTextDocument().getText();
        serverContext.setFileContent(uri, content);
        // Get the directory path and list all files in it
        Path directoryPath = Path.getParent();
        // Determine all parent directories of all files
        try (Stream<Path> walk = Files.walk(directoryPath)) {
            Set<Path> filePaths = walk.filter(Files::isRegularFile)
                .flatMap(path -> {
                    List<Path> additions = new ArrayList<>();
                    do {
                        additions.add(path);
                        path = path.getParent();
                    } while (path != null);
                    return additions.stream();
                })
                .collect(Collectors.toSet());
            this.serverContext.setDirectoryFilePaths(filePaths);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BaseOperationContext context = ContextBuilder.baseContext(this.serverContext);
        if (CommonUtils.isToscaFile(Path)) {
            context.clientLogManager().showInfoMessage("TOSCA file opened");
            DiagnosticsPublisher diagnosticspublisher = DiagnosticsPublisher.getInstance(serverContext);
            diagnosticspublisher.publishDiagnostics(serverContext, Path);
        }
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        Path filePath = CommonUtils.uriToPath(uri);
        serverContext.setCurrentToscaFilePath(filePath);

        // Get the directory path and list all files in it
        Path directoryPath = filePath.getParent();
        // Determine all parent directories of all files
        try (Stream<Path> walk = Files.walk(directoryPath)) {
            Set<Path> filePaths = walk.filter(Files::isRegularFile)
                .flatMap(path -> {
                    List<Path> additions = new ArrayList<>();
                    do {
                        additions.add(path);
                        path = path.getParent();
                    } while (path != null);
                    return additions.stream();
                })
                .collect(Collectors.toSet());
            this.serverContext.setDirectoryFilePaths(filePaths);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (CommonUtils.isToscaFile(filePath)) {
            DiagnosticsPublisher diagnosticspublisher = DiagnosticsPublisher.getInstance(serverContext);
            List<TextDocumentContentChangeEvent> changes = params.getContentChanges();
            if (!changes.isEmpty()) {
                String content = changes.get(0).getText();
                diagnosticspublisher.publishDiagnostics(serverContext, filePath, content);
                serverContext.setFileContent(uri, content);
                // Update the edited file
                if (serverContext.getCurrentToscaFile() != null && serverContext.getCurrentToscaFile().profile().isPresent()) {
                    serverContext.getToscaFilesPath().put(filePath, serverContext.getCurrentToscaFile());
                    serverContext.getImportedToscaFiles().put(serverContext.getCurrentToscaFilePath() ,Map.of(serverContext.getCurrentToscaFile().profile().get().getValue(),serverContext.getCurrentToscaFile()));
                } //TODO revalidate the files that import this file
            }
        }
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        serverContext.setFileContent(uri, "");
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        // Currently no Implementation needed
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        String uri = params.getTextDocument().getUri();
        Position position = params.getPosition();
        String content = serverContext.getFileContent(uri);
        String line = content.split("\n")[position.getLine()];
        AutoCompletionHandler autoCompletionHandler = new AutoCompletionHandler(serverContext);
        List<CompletionItem> completionItems = autoCompletionHandler.handel(line,position, content);
        return CompletableFuture.completedFuture(Either.forLeft(completionItems));
    }

}
