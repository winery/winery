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
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.TOSCAFile;
import org.eclipse.winery.lsp.Server.ServerCore.Utils.CommonUtils;
import org.yaml.snakeyaml.error.Mark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RelationshipTypeValidator implements DiagnosesHandler {
    public ArrayList<DiagnosticsSetter> diagnostics = new ArrayList<>();
    LSContext context;

    public RelationshipTypeValidator(LSContext context) {
    this.context = context;
    }

    public ArrayList<DiagnosticsSetter> validateRelationshipTypes(Map<String, Object> relationshipTypesMap, Map<String, Mark> positions, String yamlContent, String[] lines) {
        Set<String> validNodeTypeKeywords = Set.of(
            "derived_from", "version", "metadata", "description", "properties", "attributes", "interfaces", "valid_capability_types","valid_target_node_types", "valid_source_node_types"
        );
        for (String relationshipTypeKey : relationshipTypesMap.keySet()) {
            Object relationshipType = relationshipTypesMap.get(relationshipTypeKey);
            String relationshipTypePath = "relationship_types" + "." + relationshipTypeKey;
            if (relationshipType instanceof Map) {
                for (String key : ((Map<String, Object>) relationshipType).keySet()) {
                    if (!validNodeTypeKeywords.contains(key)) {
                        Mark mark = context.getContextDependentConstructorPositions().get(relationshipTypePath + "." + key);
                        int line = mark != null ? mark.getLine() + 1 : -1;
                        int column = mark != null ? mark.getColumn() + 1 : -1;
                        int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);

                        handleNotValidKeywords("Invalid relationship type keyword: " + key , line, column, endColumn);
                    }
                    //Check if the derived_from keyword exists, that it contains a valid relationship type parent
                    else if (key.equals("derived_from")) {
                        validateDerivedFrom(relationshipTypesMap, yamlContent, lines, relationshipTypeKey, key, ((Map<String, Object>) relationshipType));
                    } else if (key.equals("properties")) {
                        validateProperties(positions, yamlContent, lines, relationshipTypeKey, key, (Map<?, ?>) relationshipType);
                    }
    }
            }
        }
        return diagnostics;
    }
    
    private void validateDerivedFrom(Map<String, Object> relationshipTypesMap, String yamlContent, String[] lines, String relationshipTypeKey, String key, Map<String, Object> relationshipType) {
        try {
            if (relationshipTypesMap.containsKey(relationshipType.get(key))) {
                return;
            }
            else if (!context.getCurrentToscaFile().imports().isEmpty()) {
                Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                    for (TOSCAFile file : mapOfImportedFiles.values()) {
                        if (file != null && file.relationshipTypes() != null && file.relationshipTypes().getValue().containsKey(relationshipType.get(key))) {
                            //TODO set the derived from value
                            return;
                        }
                    }
                }
                Collection<Map<String, TOSCAFile>> namespaces = context.getNamespaceDefinitions().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfNamespaces : namespaces) {
                    for (String namespacesKey : mapOfNamespaces.keySet()) {
                        if (relationshipType.get(key) instanceof String) {
                            String[] parts = ((String) relationshipType.get(key)).split(":");
                            if (parts.length == 2) {
                                String typeWithoutNamespace = parts[1].trim();
                                String namespace = parts[0].trim();
                                if (namespacesKey.equals(namespace)) {
                                    TOSCAFile file = mapOfNamespaces.getOrDefault(namespace, null);
                                    if (file != null && file.relationshipTypes() != null && file.relationshipTypes() != null && file.relationshipTypes().getValue().containsKey(typeWithoutNamespace)) {
                                        //TODO set the derived from value
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Mark mark = context.getContextDependentConstructorPositions().get("relationship_types" + "." + relationshipTypeKey + "." + ((Map<?, ?>) relationshipType).get(key));
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

            handleNotValidKeywords("Invalid derived_from value, \"" + ((Map<?, ?>) relationshipType).get(key) + "\" is not a parent type ", line, column,endColumn);

        } catch (Exception e) {
            Mark mark = context.getContextDependentConstructorPositions().get("relationship_types" + "." + relationshipTypeKey + "." + ((Map<?, ?>) relationshipType).get(key));
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

            handleNotValidKeywords(e.getMessage(), line, column,endColumn);
        }
    }
    
    private void validateProperties(Map<String, Mark> positions, String yamlContent, String[] lines, String relationshipTypeKey, String key, Map<?, ?> relationshipType) {
        Object PropertyDefinitions = relationshipType.get(key);
        if (PropertyDefinitions instanceof Map) {
            PropertyDefinitionValidator propertyDefinitionValidator = new PropertyDefinitionValidator(context);
            ArrayList<DiagnosticsSetter> PropertyDefinitionDiagnostics;
            if (relationshipType.containsKey("derived_from")) {
                PropertyDefinitionDiagnostics = propertyDefinitionValidator.validatePropertyDefinitions((Map<String, Object>) PropertyDefinitions,yamlContent, lines, relationshipTypeKey, "relationship_types", (String) relationshipType.get("derived_from"));
            } else {
                PropertyDefinitionDiagnostics = propertyDefinitionValidator.validatePropertyDefinitions((Map<String, Object>) PropertyDefinitions, yamlContent, lines, relationshipTypeKey, "relationship_types", null);
            }
            diagnostics.addAll(PropertyDefinitionDiagnostics);
        }
    }

    @Override
    public void handleNotValidKeywords(String message, int line, int column, int endColumn) {
        DiagnosticsSetter nodeTypeDiagnostic = new DiagnosticsSetter();
        nodeTypeDiagnostic.setErrorMessage(message);
        nodeTypeDiagnostic.setErrorContext("Not Valid Keywords");
        nodeTypeDiagnostic.setErrorColumn(column);
        nodeTypeDiagnostic.setErrorEndColumn(endColumn);
        nodeTypeDiagnostic.setErrorLine(line);
        diagnostics.add(nodeTypeDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, Path path) {
        DiagnosticsSetter nodeTypeDiagnostic = new DiagnosticsSetter();
        nodeTypeDiagnostic.setErrorMessage(message);
        nodeTypeDiagnostic.setErrorContext("Parsing Error");
        try {
            long lineCount = Files.lines(path).count();
            nodeTypeDiagnostic.setErrorLine((int) lineCount);
        } catch (IOException e) {
            nodeTypeDiagnostic.setErrorLine(-1);
        }
        nodeTypeDiagnostic.setErrorColumn(1);
        diagnostics.add(nodeTypeDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, String content) {
        DiagnosticsSetter nodeTypeDiagnostic = new DiagnosticsSetter();
        nodeTypeDiagnostic.setErrorMessage(message);
        nodeTypeDiagnostic.setErrorContext("Parsing Error");
        nodeTypeDiagnostic.setErrorLine(countLines(content));
        nodeTypeDiagnostic.setErrorColumn(1);
        diagnostics.add(nodeTypeDiagnostic);
    }

    private int countLines(String content) {
        return (int) content.lines().count();
    }
            }
