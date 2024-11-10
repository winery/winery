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
package org.eclipse.winery.lsp.Server.ServerCore.Validation;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.winery.lsp.Server.ServerAPI.API.context.LSContext;
import org.eclipse.winery.lsp.Server.ServerCore.Parsing.TOSCAFileParser;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiagnosticsPublisher {
    private static final LSContext.Key<DiagnosticsPublisher> DIAGNOSTICS_PUBLISHER_KEY = new LSContext.Key<>();
    private final LanguageClient client;

    public DiagnosticsPublisher(LanguageClient client) {
        this.client = client;
    }

    private DiagnosticsPublisher(LSContext serverContext) {
        serverContext.put(DIAGNOSTICS_PUBLISHER_KEY, this);
        this.client = serverContext.getClient();
    }
    
    public static DiagnosticsPublisher getInstance(LSContext serverContext) {
        DiagnosticsPublisher diagnosticsPublisher = serverContext.get(DIAGNOSTICS_PUBLISHER_KEY);
        if (diagnosticsPublisher == null) {
            diagnosticsPublisher = new DiagnosticsPublisher(serverContext);
        }
        return diagnosticsPublisher;
    }
    
    public void publishDiagnostics(LSContext context, Path path) {
        TOSCAFileValidator toscaFileValidator = new TOSCAFileValidator();
        try {
            List<Diagnostic> diagnostics = getDiagnostics(context, path, toscaFileValidator);
            client.publishDiagnostics(new PublishDiagnosticsParams(path.toUri().toString(), diagnostics));
        }
        catch (IOException e) {
            toscaFileValidator.handleDiagnosticsError("Failed to read TOSCA file: " + e.getMessage(), path);
            List<Diagnostic> diagnostics = setDiagnostics(toscaFileValidator.diagnostics);
            client.publishDiagnostics(new PublishDiagnosticsParams(path.toUri().toString(), diagnostics));
        } 
        catch (YAMLException e) {
            toscaFileValidator.handleDiagnosticsError("Failed to parse TOSCA file: " + e.getMessage(), path); 
            List<Diagnostic> diagnostics = setDiagnostics(toscaFileValidator.diagnostics);
            client.publishDiagnostics(new PublishDiagnosticsParams(path.toUri().toString(), diagnostics));
        }
        catch (IllegalArgumentException e) {
            toscaFileValidator.handleDiagnosticsError("Validation failed: " + e.getMessage(), path);
            List<Diagnostic> diagnostics = setDiagnostics(toscaFileValidator.diagnostics);
            client.publishDiagnostics(new PublishDiagnosticsParams(path.toUri().toString(), diagnostics));
        }

    }

    private List<Diagnostic> getDiagnostics(LSContext context, Path path, TOSCAFileValidator toscaFileValidator) throws IOException {
        TOSCAFileParser toscaFileParser = new TOSCAFileParser();
        Map<String, Object> yamlMap = toscaFileParser.ParseTOSCAFile(path,client);
        context.setCurrentToscaFile(toscaFileParser.getToscaFile());
        context.setCotextDependentPositions(toscaFileParser.getContextDependentConstructorPositions());
        toscaFileValidator.validate(yamlMap, context, toscaFileParser.getYamlContent(), toscaFileParser.getConstructorPositions());
        List<Diagnostic> diagnostics = setDiagnostics(toscaFileValidator.diagnostics);
        return diagnostics;
    }

    public void publishDiagnostics(LSContext context, Path path, String content) {
        TOSCAFileParser toscaFileParser = new TOSCAFileParser();
        TOSCAFileValidator toscaFileValidator = new TOSCAFileValidator();
        try {
            Map<String, Object> yamlMap = toscaFileParser.ParseTOSCAFile(content , client);
            context.setCurrentToscaFile(toscaFileParser.getToscaFile());
            context.setCotextDependentPositions(toscaFileParser.getContextDependentConstructorPositions());
            toscaFileValidator.validate(yamlMap, context , content, toscaFileParser.getConstructorPositions());
            List<Diagnostic> diagnostics = setDiagnostics(toscaFileValidator.diagnostics);
            client.publishDiagnostics(new PublishDiagnosticsParams(path.toUri().toString(), diagnostics));
        }
        catch (YAMLException e) {
            toscaFileValidator.handleDiagnosticsError("Failed to parse TOSCA file: " + e.getMessage(), path);
            List<Diagnostic> diagnostics = setDiagnostics(toscaFileValidator.diagnostics);
            client.publishDiagnostics(new PublishDiagnosticsParams(path.toUri().toString(), diagnostics));
        }
        catch (IllegalArgumentException e) {
            toscaFileValidator.handleDiagnosticsError("Validation failed: " + e.getMessage(), path);
            List<Diagnostic> diagnostics = setDiagnostics(toscaFileValidator.diagnostics);
            client.publishDiagnostics(new PublishDiagnosticsParams(path.toUri().toString(), diagnostics));
        }
    }
    
    public List<Diagnostic> setDiagnostics(ArrayList<DiagnosticsSetter> diagnostics) {
        List<Diagnostic> OutputDiagnostics = new ArrayList<>();
        for (DiagnosticsSetter diagnostic : diagnostics) {
            Diagnostic diag = new Diagnostic();
            diag.setSeverity(DiagnosticSeverity.Error);
            diag.setMessage(diagnostic.getErrorMessage());
            diag.setRange(new Range(
                    new Position(diagnostic.getErrorLine() - 1, diagnostic.getErrorColumn() - 1),
                    new Position(diagnostic.getErrorLine() - 1, diagnostic.getErrorEndColumn() + 1 )
                ));                
            
            OutputDiagnostics.add(diag);
        }
        return OutputDiagnostics;
    }
}
