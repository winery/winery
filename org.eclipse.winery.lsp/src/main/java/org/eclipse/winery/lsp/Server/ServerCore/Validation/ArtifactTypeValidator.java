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

public class ArtifactTypeValidator implements DiagnosesHandler {
    
        public ArrayList<DiagnosticsSetter> diagnostics = new ArrayList<>();
        private LSContext context;

    public ArtifactTypeValidator(LSContext context) {
        this.context = context;
    }

    public ArrayList<DiagnosticsSetter> validateArtifactTypes(Map<String, Object> artifactTypesMap, Map<String, Mark> positions, String YamlContent, String[] lines) {
        
        Set<String> validArtifactTypeKeywords = Set.of(
                "derived_from", "version", "metadata", "description", "mime_type", "file_ext", "properties"
        );
        for (String artifactTypeKey : artifactTypesMap.keySet()) {
            Object artifactType = artifactTypesMap.get(artifactTypeKey);
            if (artifactType instanceof Map) {
                for (String key : ((Map<String, Object>) artifactType).keySet()) {
                    if (!validArtifactTypeKeywords.contains(key)) {
                        Mark mark = context.getContextDependentConstructorPositions().get("artifact_types" + "." + artifactTypeKey + "." + key);
                        int line = mark != null ? mark.getLine() + 1 : -1;
                        int column = mark != null ? mark.getColumn() + 1 : -1;
                        int endColumn = CommonUtils.getEndColumn(YamlContent, line, column, lines);

                        handleNotValidKeywords("Invalid artifact type keyword: " + key + " at line " + line + ", column " + column, line, column, endColumn);
                    }
                    //Check if the derived_from keyword exists, that it contains a valid Artifact type parent
                    else if (key.equals("derived_from")) {
                        validateDerivedFrom(artifactTypesMap, YamlContent, lines, artifactTypeKey, key, ((Map<String, Object>) artifactType));
                    } else if (key.equals("properties")) {
                        Object PropertyDefinitions = ((Map<?, ?>) artifactType).get(key);
                        if (PropertyDefinitions instanceof Map) {
                            PropertyDefinitionValidator propertyDefinitionValidator = new PropertyDefinitionValidator(context);
                            ArrayList<DiagnosticsSetter> PropertyDefinitionDiagnostics;
                            if (((Map<?, ?>) artifactType).containsKey("derived_from")) {
                                PropertyDefinitionDiagnostics = propertyDefinitionValidator.validatePropertyDefinitions((Map<String, Object>) PropertyDefinitions, YamlContent, lines, artifactTypeKey, "artifact_types", (String) ((Map<?, ?>) artifactType).get("derived_from"));
                            } else {
                                PropertyDefinitionDiagnostics = propertyDefinitionValidator.validatePropertyDefinitions((Map<String, Object>) PropertyDefinitions, YamlContent, lines, artifactTypeKey, "artifact_types",null);
                            }
                            diagnostics.addAll(PropertyDefinitionDiagnostics);
                        }
                    }
                }
            }
        }
        return diagnostics;
    }
    
    private void validateDerivedFrom(Map<String, Object> artifactTypesMap, String yamlContent, String[] lines, String artifactTypeKey, String key, Map<String, Object> artifactType) {
        try {
            if (artifactTypesMap.containsKey(artifactType.get(key))) {
                return;
            }
            else if (!context.getCurrentToscaFile().imports().isEmpty()) {
                Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                    for (TOSCAFile file : mapOfImportedFiles.values()) {
                        if (file != null && !file.artifactTypes().isEmpty() && file.artifactTypes().containsKey(artifactType.get(key))) {
                            //TODO set the derived from value
                            return;
                        }
                    }
                }
                Collection<Map<String, TOSCAFile>> namespaces = context.getNamespaceDefinitions().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfNamespaces : namespaces) {
                    for (String namespacesKey : mapOfNamespaces.keySet()) {
                        if (artifactType.get(key) instanceof String) {
                            String[] parts = ((String) artifactType.get(key)).split(":");
                            if (parts.length == 2) {
                                String typeWithoutNamespace = parts[1].trim();
                                String namespace = parts[0].trim();
                                if (namespacesKey.equals(namespace)) {
                                    TOSCAFile file = mapOfNamespaces.getOrDefault(namespace, null);
                                    if (file != null && !file.artifactTypes().isEmpty() && file.artifactTypes().containsKey(typeWithoutNamespace)) {
                                        //TODO set the derived from value
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Mark mark = context.getContextDependentConstructorPositions().get("artifact_types" + "." + artifactTypeKey + "." + ((Map<?, ?>) artifactType).get(key));
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

            handleNotValidKeywords("Invalid derived_from value, \"" + ((Map<?, ?>) artifactType).get(key) + "\" is not a parent type ", line, column,endColumn);
        } catch (Exception e) {
            Mark mark = context.getContextDependentConstructorPositions().get("artifact_types" + "." + artifactTypeKey + "." + ((Map<?, ?>) artifactType).get(key));
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);
            
            handleNotValidKeywords(e.getMessage(), line, column,endColumn);
        }
    }
    
    @Override
    public void handleNotValidKeywords(String message, int line, int column, int endColumn) {
        DiagnosticsSetter ArtifactTypeDiagnostic = new DiagnosticsSetter();
        ArtifactTypeDiagnostic.setErrorMessage(message);
        ArtifactTypeDiagnostic.setErrorContext("Not Valid Keywords");
        ArtifactTypeDiagnostic.setErrorColumn(column);
        ArtifactTypeDiagnostic.setErrorEndColumn(endColumn);
        ArtifactTypeDiagnostic.setErrorLine(line);
        diagnostics.add(ArtifactTypeDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, Path path) {
        DiagnosticsSetter ArtifactTypeDiagnostic = new DiagnosticsSetter();
        ArtifactTypeDiagnostic.setErrorMessage(message);
        ArtifactTypeDiagnostic.setErrorContext("Parsing Error");
        try {
            long lineCount = Files.lines(path).count();
            ArtifactTypeDiagnostic.setErrorLine((int) lineCount);
        } catch (IOException e) {
            ArtifactTypeDiagnostic.setErrorLine(-1);
        }
        ArtifactTypeDiagnostic.setErrorColumn(1);
        diagnostics.add(ArtifactTypeDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, String content) {
        DiagnosticsSetter ArtifactTypeDiagnostic = new DiagnosticsSetter();
        ArtifactTypeDiagnostic.setErrorMessage(message);
        ArtifactTypeDiagnostic.setErrorContext("Parsing Error");
        ArtifactTypeDiagnostic.setErrorLine(countLines(content));
        ArtifactTypeDiagnostic.setErrorColumn(1);
        diagnostics.add(ArtifactTypeDiagnostic);
    }
    
    private int countLines(String content) {
        return (int) content.lines().count();
    }

}
