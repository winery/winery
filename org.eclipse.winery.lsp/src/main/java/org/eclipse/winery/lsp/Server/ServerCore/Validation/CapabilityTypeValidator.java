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
import java.util.*;

public class CapabilityTypeValidator implements DiagnosesHandler {
    public ArrayList<DiagnosticsSetter> diagnostics = new ArrayList<>();
    private LSContext context;

    public CapabilityTypeValidator(LSContext context) {
        this.context = context;
    }

    public ArrayList<DiagnosticsSetter> validateCapabilityTypes(Map<String, Object> capabilityTypesMap, Map<String, Mark> positions, String YamlContent, String[] lines) {
        Set<String> validCapabilityTypeKeywords = Set.of(
            "derived_from", "version", "metadata", "description", "valid_source_node_types", "valid_relationship_types", "properties", "attributes"
        );
        for (String capabilityTypeKey : capabilityTypesMap.keySet()) {
            Object capabilityType = capabilityTypesMap.get(capabilityTypeKey);
            if (capabilityType instanceof Map) {
                for (String key : ((Map<String, Object>) capabilityType).keySet()) {
                    if (!validCapabilityTypeKeywords.contains(key)) {
                        Mark mark = context.getContextDependentConstructorPositions().get("capability_types" + "." + capabilityTypeKey + "." + key);
                        int line = mark != null ? mark.getLine() + 1 : -1;
                        int column = mark != null ? mark.getColumn() + 1 : -1;
                        int endColumn = CommonUtils.getEndColumn(YamlContent, line, column, lines);

                        handleNotValidKeywords("Invalid capability type keyword: " + key + " at line " + line + ", column " + column, line, column, endColumn);
                    }
                    //Check if the derived_from keyword exists, that it contains a valid capability type parent
                    else if (key.equals("derived_from")) {
                        validateDerivedFrom(capabilityTypesMap, YamlContent, lines, capabilityTypeKey, key, ((Map<String, Object>) capabilityType));
                    } else if (key.equals("properties")) {
                        Object PropertyDefinitions = ((Map<String, Object>) capabilityType).get(key);
                        if (PropertyDefinitions instanceof Map) {
                            PropertyDefinitionValidator propertyDefinitionValidator = new PropertyDefinitionValidator(context);
                            ArrayList<DiagnosticsSetter> PropertyDefinitionDiagnostics;
                            if (((Map<?, ?>) capabilityType).containsKey("derived_from")) {
                                PropertyDefinitionDiagnostics = propertyDefinitionValidator.validatePropertyDefinitions((Map<String, Object>) PropertyDefinitions,YamlContent, lines, capabilityTypeKey, "capability_types", (String) ((Map<?, ?>) capabilityType).get("derived_from"));
                            } else {
                                PropertyDefinitionDiagnostics = propertyDefinitionValidator.validatePropertyDefinitions((Map<String, Object>) PropertyDefinitions, YamlContent, lines, capabilityTypeKey, "capability_types",null);
                            }
                            diagnostics.addAll(PropertyDefinitionDiagnostics);
                        }
                    }
                }
            }
        }
        return diagnostics;
    }

    private void validateDerivedFrom(Map<String, Object> capabilityTypesMap, String YamlContent, String[] lines, String capabilityTypeKey, String key, Map<String, Object> capabilityType) {
        try {
           if (capabilityTypesMap.containsKey(capabilityType.get(key))) {
               return;
           }
           else if (!context.getCurrentToscaFile().imports().isEmpty()) {
               Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
               for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                   for (TOSCAFile file : mapOfImportedFiles.values()) {
                       if (file != null && !file.capabilityTypes().isEmpty() && file.capabilityTypes().containsKey(capabilityType.get(key))) {
                           //TODO set the derived from value
                           return;
                       }
                   }
               }
               Collection<Map<String, TOSCAFile>> namespaces = context.getNamespaceDefinitions().get(context.getCurrentToscaFilePath());
               for (Map<String, TOSCAFile> mapOfNamespaces : namespaces) {
                   for (String namespacesKey : mapOfNamespaces.keySet()) {
                       if (capabilityType.get(key) instanceof String) {
                           String[] parts = ((String) capabilityType.get(key)).split(":");
                           if (parts.length == 2) {
                               String typeWithoutNamespace = parts[1].trim();
                               String namespace = parts[0].trim();
                               if (namespacesKey.equals(namespace)) {
                                   TOSCAFile file = mapOfNamespaces.getOrDefault(namespace, null);
                                   if (file != null && !file.capabilityTypes().isEmpty() && file.capabilityTypes().containsKey(typeWithoutNamespace)) {
                                       //TODO set the derived from value
                                       return;
                                   }
                               }
                           }
                       }
                   }
               }
           }
           Mark mark = context.getContextDependentConstructorPositions().get("capability_types" + "." + capabilityTypeKey + "." + capabilityType.get(key));
           int line = mark != null ? mark.getLine() + 1 : -1;
           int column = mark != null ? mark.getColumn() + 1 : -1;
           int endColumn = CommonUtils.getEndColumnForValueError(YamlContent, line, column, lines);

           handleNotValidKeywords("Invalid derived_from value, \"" + capabilityType.get(key) + "\" is not a parent type ", line, column,endColumn);

       } catch (Exception e) {
           Mark mark = context.getContextDependentConstructorPositions().get("capability_types" + "." + capabilityTypeKey + "." + capabilityType.get(key));
           int line = mark != null ? mark.getLine() + 1 : -1;
           int column = mark != null ? mark.getColumn() + 1 : -1;
           int endColumn = CommonUtils.getEndColumnForValueError(YamlContent, line, column, lines);

           handleNotValidKeywords(e.getMessage(), line, column,endColumn);
       }
    }

    @Override
    public void handleNotValidKeywords(String message, int line, int column, int endColumn) {
        DiagnosticsSetter capabilityTypeDiagnostic = new DiagnosticsSetter();
        capabilityTypeDiagnostic.setErrorMessage(message);
        capabilityTypeDiagnostic.setErrorContext("Not Valid Keywords");
        capabilityTypeDiagnostic.setErrorColumn(column);
        capabilityTypeDiagnostic.setErrorEndColumn(endColumn);
        capabilityTypeDiagnostic.setErrorLine(line);
        diagnostics.add(capabilityTypeDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, Path path) {
        DiagnosticsSetter capabilityTypeDiagnostic = new DiagnosticsSetter();
        capabilityTypeDiagnostic.setErrorMessage(message);
        capabilityTypeDiagnostic.setErrorContext("Parsing Error");
        try {
            long lineCount = Files.lines(path).count();
            capabilityTypeDiagnostic.setErrorLine((int) lineCount);
        } catch (IOException e) {
            capabilityTypeDiagnostic.setErrorLine(-1);
        }
        capabilityTypeDiagnostic.setErrorColumn(1);
        diagnostics.add(capabilityTypeDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, String content) {
        DiagnosticsSetter capabilityTypeDiagnostic = new DiagnosticsSetter();
        capabilityTypeDiagnostic.setErrorMessage(message);
        capabilityTypeDiagnostic.setErrorContext("Parsing Error");
        capabilityTypeDiagnostic.setErrorLine(countLines(content));
        capabilityTypeDiagnostic.setErrorColumn(1);
        diagnostics.add(capabilityTypeDiagnostic);
    }

    private int countLines(String content) {
        return (int) content.lines().count();
    }

}

