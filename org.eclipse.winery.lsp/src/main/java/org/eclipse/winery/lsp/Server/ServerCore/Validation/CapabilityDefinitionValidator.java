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
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.CapabilityDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.TOSCAFile;
import org.eclipse.winery.lsp.Server.ServerCore.Utils.CommonUtils;
import org.tinylog.Logger;
import org.yaml.snakeyaml.error.Mark;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CapabilityDefinitionValidator implements DiagnosesHandler {
    public ArrayList<DiagnosticsSetter> diagnostics = new ArrayList<>();
    private LSContext context;

    public CapabilityDefinitionValidator(LSContext context) {
        this.context = context;
    }
    
    public ArrayList<DiagnosticsSetter> validateCapabilityDefinitions(Map<String, Object> capabilityDefinitionsMap, String yamlContent, String[] lines, String capabilityDefinitionPath, String parent) {
        
        Set<String> validNodeTypeKeywords = Set.of(
            "type", "description", "metadata", "properties", "attributes", "valid_source_node_types", "valid_relationship_types"
        );
        for (String capabilityDefinitionsKey : capabilityDefinitionsMap.keySet()) {
            Object capabilityDefinition = capabilityDefinitionsMap.get(capabilityDefinitionsKey);
            if (capabilityDefinition instanceof Map) {
                String capabilityDefinitionPathWithName = capabilityDefinitionPath + "." + capabilityDefinitionsKey;
                if (capabilityDefinitionPathWithName.contains("node_templates") && capabilityDefinitionPathWithName.contains("capabilities")) {
                    if (validateCapabilitiesFromNodeTemplateParent(yamlContent, lines, parent, capabilityDefinitionsKey, capabilityDefinitionPathWithName))
                        continue;
                } else {
                    validateRequiredKeys((Map<String, Object>) capabilityDefinition,yamlContent, lines, capabilityDefinitionPathWithName);
                }
                validateCapabilityDefinition(yamlContent, lines, capabilityDefinitionPath, parent, capabilityDefinitionsKey, capabilityDefinition, validNodeTypeKeywords, capabilityDefinitionPathWithName);
            } else if (capabilityDefinition instanceof String) {
                if (validateCapabilitiesFromNodeTemplateParent(yamlContent, lines, parent, capabilityDefinitionsKey, capabilityDefinitionPath + "." + capabilityDefinitionsKey))
                    continue;
            }
        }
        return diagnostics;
    }

    private boolean validateCapabilitiesFromNodeTemplateParent(String yamlContent, String[] lines, String parent, String capabilityDefinitionsKey, String capabilityDefinitionPathWithName) {
        if (context.getCurrentToscaFile() == null || context.getCurrentToscaFile().serviceTemplate().isEmpty() || context.getCurrentToscaFile().serviceTemplate().get().nodeTemplates().getValue() == null || !context.getCurrentToscaFile().serviceTemplate().get().nodeTemplates().getValue().containsKey(parent) || context.getCurrentToscaFile().serviceTemplate().get().nodeTemplates().getValue().get(parent).type() == null || !context.getCurrentToscaFile().serviceTemplate().get().nodeTemplates().getValue().get(parent).type().capabilities().getValue().containsKey(capabilityDefinitionsKey)) {
            Mark mark = context.getContextDependentConstructorPositions().get(capabilityDefinitionPathWithName);
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
            handleNotValidKeywords("Invalid capability definition" , line, column, endColumn);

            return true;
        }
        return false;
    }

    private void validateCapabilityDefinition(String yamlContent, String[] lines, String capabilityDefinitionPath, String parent, String capabilityDefinitionsKey, Object capabilityDefinition, Set<String> validNodeTypeKeywords, String capabilityDefinitionPathWithName) {
        for (String key : ((Map<String, Object>) capabilityDefinition).keySet()) {
            if (!validNodeTypeKeywords.contains(key)) {
                Mark mark = context.getContextDependentConstructorPositions().get(capabilityDefinitionPathWithName + "." + key);
                int line = mark != null ? mark.getLine() + 1 : -1;
                int column = mark != null ? mark.getColumn() + 1 : -1;
                int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);

                handleNotValidKeywords("Invalid capability definition keyword: " + key , line, column, endColumn);
            }
            //Check if the type keyword exists, and contains existing capability type
            else if (key.equals("type")) {
                if (capabilityDefinitionPath.contains("node_types")) {
                    try {
                        validateTypeFromNodeTypeParent(yamlContent, lines, key, capabilityDefinition, capabilityDefinitionPathWithName, parent, capabilityDefinitionsKey);
                    }
                    catch (Exception e) {
                        Logger.error("The error message: " + e.getMessage(), e);
                    }
                    }
            } else if (key.equals("properties")) {
                validateProperties(yamlContent, lines, capabilityDefinitionsKey, key, (Map<?, ?>) capabilityDefinition, parent, capabilityDefinitionPath);

            }
        }
    }

    private void validateProperties(String yamlContent, String[] lines, String capabilityDefinitionKey, String key, Map<?, ?> capabilityDefinition, String parent, String CapabilityDefinitionPath) {
        Object PropertyDefinitions = capabilityDefinition.get(key);
        if (PropertyDefinitions instanceof Map) {
            PropertyDefinitionValidator propertyDefinitionValidator = new PropertyDefinitionValidator(context);
            ArrayList<DiagnosticsSetter> PropertyDefinitionDiagnostics;
            PropertyDefinitionDiagnostics = propertyDefinitionValidator.validatePropertyDefinitions((Map<String, Object>) PropertyDefinitions, yamlContent, lines, capabilityDefinitionKey, CapabilityDefinitionPath, parent);
            diagnostics.addAll(PropertyDefinitionDiagnostics);
        }
    }
    
    private void validateTypeFromNodeTypeParent(String yamlContent, String[] lines, String key, Object capabilityDefinition, String capabilityDefinitionPathWithName, String parent, String capabilityDefinitionsKey) {
        try {
            if (!context.getCurrentToscaFile().capabilityTypes().isEmpty() && context.getCurrentToscaFile().capabilityTypes().containsKey(((Map<String, Object>) capabilityDefinition).get(key))) {
                if (context.getCurrentToscaFile().nodeTypes().getValue().containsKey(parent) && context.getCurrentToscaFile().nodeTypes().getValue().get(parent).capabilities().getValue().containsKey(capabilityDefinitionsKey)) {
                    CapabilityDefinition newCapabilityDefinition = context.getCurrentToscaFile().nodeTypes().getValue().get(parent).capabilities().getValue().get(capabilityDefinitionsKey).withType(context.getCurrentToscaFile().capabilityTypes().get(((Map<String, Object>) capabilityDefinition).get(key)));
                    context.getCurrentToscaFile().nodeTypes().getValue().get(parent).capabilities().getValue().put(capabilityDefinitionsKey, newCapabilityDefinition);
                }
                return;
            }
            if ( !context.getCurrentToscaFile().imports().isEmpty()) {
                Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                    for (TOSCAFile file : mapOfImportedFiles.values()) {
                        if (file != null && !file.capabilityTypes().isEmpty() && file.capabilityTypes().containsKey(((Map<String, Object>) capabilityDefinition).get(key))) {
                            if (context.getCurrentToscaFile().nodeTypes().getValue().containsKey(parent) && context.getCurrentToscaFile().nodeTypes().getValue().get(parent).capabilities().getValue().containsKey(capabilityDefinitionsKey)) {
                                CapabilityDefinition newCapabilityDefinition = context.getCurrentToscaFile().nodeTypes().getValue().get(parent).capabilities().getValue().get(capabilityDefinitionsKey).withType(file.capabilityTypes().get(((Map<String, Object>) capabilityDefinition).get(key)));
                                context.getCurrentToscaFile().nodeTypes().getValue().get(parent).capabilities().getValue().put(capabilityDefinitionsKey, newCapabilityDefinition);
                            }
                            return;
                        }
                    }
                }
                    Collection<Map<String, TOSCAFile>> namespaces = context.getNamespaceDefinitions().get(context.getCurrentToscaFilePath());
                    for (Map<String, TOSCAFile> mapOfNamespaces : namespaces) {
                        for (String namespacesKey : mapOfNamespaces.keySet()) {
                            if ( ((Map<String, Object>) capabilityDefinition).get(key) instanceof String) {
                                String[] parts = ((String) ((Map<String, Object>) capabilityDefinition).get(key)).split(":");
                                if (parts.length == 2) {
                                    String typeWithoutNamespace = parts[1].trim();
                                    String namespace = parts[0].trim();
                                if (namespacesKey.equals(namespace)) {
                                    TOSCAFile file = mapOfNamespaces.getOrDefault(namespace, null);
                                    if (file != null && !file.capabilityTypes().isEmpty() && file.capabilityTypes().containsKey(typeWithoutNamespace)) {
                                        if (context.getCurrentToscaFile().nodeTypes().getValue().containsKey(parent) && context.getCurrentToscaFile().nodeTypes().getValue().get(parent).capabilities().getValue().containsKey(capabilityDefinitionsKey)) {
                                            CapabilityDefinition newCapabilityDefinition = context.getCurrentToscaFile().nodeTypes().getValue().get(parent).capabilities().getValue().get(capabilityDefinitionsKey).withType(file.capabilityTypes().get(typeWithoutNamespace));context.getCurrentToscaFile().nodeTypes().getValue().get(parent).capabilities().getValue().put(capabilityDefinitionsKey, newCapabilityDefinition);
                                        }
                                        return;
                                    }
                                }                                                
                            }
                            }
                        }
                    }
                
            } 
                Mark mark = context.getContextDependentConstructorPositions().get(capabilityDefinitionPathWithName + "." + ((Map<?, ?>) capabilityDefinition).get(key));
                int line = mark != null ? mark.getLine() + 1 : -1;
                int column = mark != null ? mark.getColumn() + 1 : -1;
                int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

                handleNotValidKeywords("Invalid capability type value, \"" + ((Map<?, ?>) capabilityDefinition).get(key) + "\" is not exist.", line, column,endColumn);
        } catch (Exception e) {
            Mark mark = context.getContextDependentConstructorPositions().get(capabilityDefinitionPathWithName + "." + ((Map<?, ?>) capabilityDefinition).get(key));
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

            handleNotValidKeywords(e.getMessage(), line, column,endColumn);
        }
    }

    @Override
    public void handleNotValidKeywords(String message, int line, int column, int endColumn) {
        DiagnosticsSetter capabilityDefinitionDiagnostic = new DiagnosticsSetter();
        capabilityDefinitionDiagnostic.setErrorMessage(message);
        capabilityDefinitionDiagnostic.setErrorContext("Not Valid Keywords");
        capabilityDefinitionDiagnostic.setErrorColumn(column);
        capabilityDefinitionDiagnostic.setErrorEndColumn(endColumn);
        capabilityDefinitionDiagnostic.setErrorLine(line);
        diagnostics.add(capabilityDefinitionDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, Path path) {
        DiagnosticsSetter capabilityDefinitionDiagnostic = new DiagnosticsSetter();
        capabilityDefinitionDiagnostic.setErrorMessage(message);
        capabilityDefinitionDiagnostic.setErrorContext("Parsing Error");
        try {
            long lineCount = Files.lines(path).count();
            capabilityDefinitionDiagnostic.setErrorLine((int) lineCount);
        } catch (IOException e) {
            capabilityDefinitionDiagnostic.setErrorLine(-1);
        }
        capabilityDefinitionDiagnostic.setErrorColumn(1);
        diagnostics.add(capabilityDefinitionDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, String content) {
        DiagnosticsSetter capabilityDefinitionDiagnostic = new DiagnosticsSetter();
        capabilityDefinitionDiagnostic.setErrorMessage(message);
        capabilityDefinitionDiagnostic.setErrorContext("Parsing Error");
        capabilityDefinitionDiagnostic.setErrorLine(countLines(content));
        capabilityDefinitionDiagnostic.setErrorColumn(1);
        diagnostics.add(capabilityDefinitionDiagnostic);
    }

    private int countLines(String content) {
        return (int) content.lines().count();
    }

    public void validateRequiredKeys(Map<String, Object> yamlMap, String content, String[] lines, String nodeTemplatePath) {
        if (!yamlMap.containsKey("type")) {
            Mark mark = context.getContextDependentConstructorPositions().get(nodeTemplatePath);
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumn(content, line, column, lines);
            handleNotValidKeywords("Capability definition Missing required key: type ", line, column, endColumn);
        }
    }

}
