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

import org.eclipse.winery.lsp.Server.ServerAPI.API.context.LSContext;
import org.eclipse.winery.lsp.Server.ServerCore.Utils.CommonUtils;
import org.yaml.snakeyaml.error.Mark;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SchemaDefinitionValidator implements DiagnosesHandler  {
    public ArrayList<DiagnosticsSetter> diagnostics = new ArrayList<>();
    private final LSContext context;

    public SchemaDefinitionValidator(LSContext context) {
    this.context = context;
    }

    public ArrayList<DiagnosticsSetter> validateSchemaDefinitions(Map<String, Object> SchemaDefinitionMap, String yamlContent, String[] lines, String schemaPath) {
        Set<String> validPropertyDefinitionKeywords = Set.of(
            "type", "description","validation", "key_schema", "entry_schema"
        );
        validateRequiredKeys(SchemaDefinitionMap,yamlContent, lines,  schemaPath);
        for (String SchemaDefinitionKey : SchemaDefinitionMap.keySet()) {
            if (!validPropertyDefinitionKeywords.contains(SchemaDefinitionKey)) {
                Mark mark = context.getContextDependentConstructorPositions().get( schemaPath + "." + SchemaDefinitionKey);
                int line = mark != null ? mark.getLine() + 1 : -1;
                int column = mark != null ? mark.getColumn() + 1 : -1;
                int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
                handleNotValidKeywords("Invalid Schema definition keyword: " + SchemaDefinitionKey, line, column, endColumn);
            }
            if ((SchemaDefinitionMap).containsKey("type") && (((Map<?, ?>) SchemaDefinitionMap).get("type").equals("list") || ((Map<?, ?>) SchemaDefinitionMap).get("type").equals("map"))) {
                ValidateEntrySchema(yamlContent, lines, SchemaDefinitionMap, schemaPath );
            }
        }
        if ((SchemaDefinitionMap).containsKey("entry_schema")) {
            Object entrySchema = SchemaDefinitionMap.get("entry_schema");
            if (entrySchema instanceof Map) {
                validateSchemaDefinitions((Map<String, Object>) entrySchema, yamlContent, lines, schemaPath + "." + "entry_schema" );
            } else {
                Mark mark = context.getContextDependentConstructorPositions().get( schemaPath + "." + "entry_schema");
                int line = mark != null ? mark.getLine() + 1 : -1;
                int column = mark != null ? mark.getColumn() + 1 : -1;
                int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
                handleNotValidKeywords("Invalid Schema definition, Schema definition needs to be a map.", line, column, endColumn);
            }
        }
        return diagnostics;
    }
    
    public void ValidateEntrySchema(String YamlContent, String[] lines, Map<?, ?> schemaDefinition, String schemaPath) {
        if (! schemaDefinition.containsKey("entry_schema")) {
            Mark mark = context.getContextDependentConstructorPositions().get(schemaPath);
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumn(YamlContent, line, column, lines);
            handleNotValidKeywords("Missing entry_schema", line, column, endColumn);
        }
    }
    
    @Override
    public void handleNotValidKeywords(String message, int line, int column, int endColumn) {
        DiagnosticsSetter SchemaDefinitionDiagnostic = new DiagnosticsSetter();
        SchemaDefinitionDiagnostic.setErrorMessage(message);
        SchemaDefinitionDiagnostic.setErrorContext("Not Valid Keywords");
        SchemaDefinitionDiagnostic.setErrorColumn(column);
        SchemaDefinitionDiagnostic.setErrorEndColumn(endColumn);
        SchemaDefinitionDiagnostic.setErrorLine(line);
        diagnostics.add(SchemaDefinitionDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, Path path) {
        DiagnosticsSetter SchemaDefinitionDiagnostic = new DiagnosticsSetter();
        SchemaDefinitionDiagnostic.setErrorMessage(message);
        SchemaDefinitionDiagnostic.setErrorContext("Parsing Error");
        try {
            long lineCount = Files.lines(path).count();
            SchemaDefinitionDiagnostic.setErrorLine((int) lineCount);
        } catch (IOException e) {
            SchemaDefinitionDiagnostic.setErrorLine(-1);
        }
        SchemaDefinitionDiagnostic.setErrorColumn(1);
        diagnostics.add(SchemaDefinitionDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, String content) {
        DiagnosticsSetter SchemaDefinitionDiagnostic = new DiagnosticsSetter();
        SchemaDefinitionDiagnostic.setErrorMessage(message);
        SchemaDefinitionDiagnostic.setErrorContext("Parsing Error");
        SchemaDefinitionDiagnostic.setErrorLine(countLines(content));
        SchemaDefinitionDiagnostic.setErrorColumn(1);
        diagnostics.add(SchemaDefinitionDiagnostic);
    }

    private int countLines(String content) {
        return (int) content.lines().count();
    }

    public void validateRequiredKeys(Map<String, Object> yamlMap, String content, String[] lines, String SchemaPath) {
        if (!yamlMap.containsKey("type")) {
            Mark mark = context.getContextDependentConstructorPositions().get(SchemaPath);
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumn(content, line, column, lines);
            handleNotValidKeywords("Schema Definition Missing required key: type ", line, column, endColumn);
        }
        
    }

}
