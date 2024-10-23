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
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.CapabilityType;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.TOSCAFile;
import org.eclipse.winery.lsp.Server.ServerCore.Utils.CommonUtils;
import org.yaml.snakeyaml.error.Mark;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class NodeTypeValidator implements DiagnosesHandler {
    public ArrayList<DiagnosticsSetter> diagnostics = new ArrayList<>();
    LSContext context;
    
    public NodeTypeValidator(LSContext context) {
    this.context = context;
    }

    public ArrayList<DiagnosticsSetter> validateNodeTypes(Map<String, Object> nodeTypesMap, Map<String, Mark> positions, String yamlContent, String[] lines) {
        Set<String> validNodeTypeKeywords = Set.of(
            "derived_from", "version", "metadata", "description", "properties", "attributes", "capabilities", "requirements","interfaces", "artifacts"
        );
        for (String nodeTypeKey : nodeTypesMap.keySet()) {
            Object nodeType = nodeTypesMap.get(nodeTypeKey);
            String nodeTypePath = "node_types" + "." + nodeTypeKey;
            if (nodeType instanceof Map) {
                for (String key : ((Map<String, Object>) nodeType).keySet()) {
                    if (!validNodeTypeKeywords.contains(key)) {
                        Mark mark = context.getContextDependentConstructorPositions().get(nodeTypePath + "." + key);
                        int line = mark != null ? mark.getLine() + 1 : -1;
                        int column = mark != null ? mark.getColumn() + 1 : -1;
                        int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);

                        handleNotValidKeywords("Invalid node type keyword: " + key , line, column, endColumn);
                    }
                    //Check if the derived_from keyword exists, that it contains a valid node type parent
                    else if (key.equals("derived_from")) {
                        validateDerivedFrom(nodeTypesMap, yamlContent, lines, nodeTypeKey, key, ((Map<String, Object>) nodeType));
                    } else if (key.equals("properties")) {
                        validateProperties(positions, yamlContent, lines, nodeTypeKey, key, (Map<?, ?>) nodeType);
                    } else if (key.equals("capabilities")) {
                        Object capabilityDefinitions = ((Map<?, ?>) nodeType).get(key);
                        if (capabilityDefinitions instanceof Map) {
                            validateCapabilityDefinitions(yamlContent, lines, nodeTypeKey, (Map<String, Object>) capabilityDefinitions, nodeTypePath);
                        } else if (capabilityDefinitions instanceof String capabilityType) {
                            validateCapabilityType(yamlContent, lines, nodeTypeKey, key, capabilityType, nodeTypePath, (Map<?, ?>) nodeType);
                        } else {
                            Mark mark = context.getContextDependentConstructorPositions().get(nodeTypePath + "." + ((Map<?, ?>) nodeType).get(key));
                            int line = mark != null ? mark.getLine() + 1 : -1;
                            int column = mark != null ? mark.getColumn() + 1 : -1;
                            int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

                            handleNotValidKeywords("Invalid capability: " + ((Map<?, ?>) nodeType).get(key), line, column,endColumn);
                        }
                    } else if (key.equals("requirements")) {
                        Object requirementDefinitions = ((Map<?, ?>) nodeType).get(key);
                        if (requirementDefinitions instanceof List) {
                            validateRequirementDefinitions(yamlContent, lines, nodeTypeKey, (List<?>) requirementDefinitions, nodeTypePath + "." + "requirements");
                        }
                    }
                }
        }
    }
        return diagnostics;
    }

    private void validateRequirementDefinitions(String yamlContent, String[] lines, String nodeTypeKey, List<?> RequirementDefinitions, String nodeTypePath) {
        RequirementDefinitionValidator requirementDefinitionValidator = new RequirementDefinitionValidator(context);
        ArrayList<DiagnosticsSetter> RequirementDefinitionDiagnostics;
        RequirementDefinitionDiagnostics = requirementDefinitionValidator.validateRequirementDefinitions(RequirementDefinitions, yamlContent, lines, nodeTypePath, nodeTypeKey);
        diagnostics.addAll(RequirementDefinitionDiagnostics);
    }

    private void validateCapabilityDefinitions(String yamlContent, String[] lines, String nodeTypeKey, Map<String, Object> capabilityDefinitions, String nodeTypePath) {
        CapabilityDefinitionValidator capabilityDefinitionValidator = new CapabilityDefinitionValidator(context);
        ArrayList<DiagnosticsSetter> capabilityDefinitionDiagnostics;
        capabilityDefinitionDiagnostics = capabilityDefinitionValidator.validateCapabilityDefinitions(capabilityDefinitions, yamlContent, lines, nodeTypePath + "." + "capabilities", nodeTypeKey);
        diagnostics.addAll(capabilityDefinitionDiagnostics);
    }

    private void validateDerivedFrom(Map<String, Object> nodeTypesMap, String yamlContent, String[] lines, String nodeTypeKey, String key, Map<String, Object> nodeType) {
        try {
            if (nodeTypesMap.containsKey(nodeType.get(key))) {
                return;
            }
            else if (!context.getCurrentToscaFile().imports().isEmpty()) {
                Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                    for (TOSCAFile file : mapOfImportedFiles.values()) {
                        if (file != null && file.nodeTypes() != null && file.nodeTypes().getValue().containsKey(nodeType.get(key))) {
                            //TODO set the derived from value
                            return;
                        }
                    }
                }
                Collection<Map<String, TOSCAFile>> namespaces = context.getNamespaceDefinitions().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfNamespaces : namespaces) {
                    for (String namespacesKey : mapOfNamespaces.keySet()) {
                        if (nodeType.get(key) instanceof String) {
                            String[] parts = ((String) nodeType.get(key)).split(":");
                            if (parts.length == 2) {
                                String typeWithoutNamespace = parts[1].trim();
                                String namespace = parts[0].trim();
                                if (namespacesKey.equals(namespace)) {
                                    TOSCAFile file = mapOfNamespaces.getOrDefault(namespace, null);
                                    if (file != null && file.nodeTypes() != null && file.nodeTypes() != null && file.nodeTypes().getValue().containsKey(typeWithoutNamespace)) {
                                        //TODO set the derived from value
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Mark mark = context.getContextDependentConstructorPositions().get("node_types" + "." + nodeTypeKey + "." + ((Map<?, ?>) nodeType).get(key));
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

            handleNotValidKeywords("Invalid derived_from value, \"" + ((Map<?, ?>) nodeType).get(key) + "\" is not a parent type ", line, column,endColumn);

        } catch (Exception e) {
            Mark mark = context.getContextDependentConstructorPositions().get("node_types" + "." + nodeTypeKey + "." + ((Map<?, ?>) nodeType).get(key));
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

            handleNotValidKeywords(e.getMessage(), line, column,endColumn);
        }
    }
    
    private void validateProperties(Map<String, Mark> positions, String yamlContent, String[] lines, String nodeTypeKey, String key, Map<?, ?> nodeType) {
        Object PropertyDefinitions = nodeType.get(key);
        if (PropertyDefinitions instanceof Map) {
            PropertyDefinitionValidator propertyDefinitionValidator = new PropertyDefinitionValidator(context);
            ArrayList<DiagnosticsSetter> PropertyDefinitionDiagnostics;
            if (nodeType.containsKey("derived_from")) {
                PropertyDefinitionDiagnostics = propertyDefinitionValidator.validatePropertyDefinitions((Map<String, Object>) PropertyDefinitions, yamlContent, lines, nodeTypeKey, "node_types", (String) nodeType.get("derived_from"));
            } else {
                PropertyDefinitionDiagnostics = propertyDefinitionValidator.validatePropertyDefinitions((Map<String, Object>) PropertyDefinitions, yamlContent, lines, nodeTypeKey, "node_types", null);
            }
            diagnostics.addAll(PropertyDefinitionDiagnostics);
        }
    }

    private void validateCapabilityType(String yamlContent, String[] lines, String nodeTypeKey, String key, String capabilityType, String nodeTypePath, Map<?, ?> nodeType) {
        // checks if it exists in the same file
        if (!context.getCurrentToscaFile().capabilityTypes().isEmpty() && context.getCurrentToscaFile().capabilityTypes().containsKey(capabilityType)) {
            //the capability definition name is the same as the provided capability type name.
            CapabilityType capabilityTypeObject = context.getCurrentToscaFile().capabilityTypes().get(capabilityType);
            if (context.getCurrentToscaFile().nodeTypes() != null && context.getCurrentToscaFile().nodeTypes().getValue().containsKey(nodeTypeKey) && context.getCurrentToscaFile().nodeTypes().getValue().get(nodeTypeKey).capabilities() != null) {
                context.getCurrentToscaFile().nodeTypes().getValue().get(nodeTypeKey).capabilities().getValue().put(capabilityType, new CapabilityDefinition(capabilityTypeObject, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), new HashMap<>(), new HashMap<>())) ;
            }
            return;
        } try {
            if (!context.getCurrentToscaFile().imports().isEmpty()) {
                Collection<Map<String, TOSCAFile>> imports = context.getImportedToscaFiles().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfImportedFiles : imports) {
                    for (TOSCAFile file : mapOfImportedFiles.values()) {
                        if (file != null && file.capabilityTypes() != null && file.capabilityTypes() != null && file.capabilityTypes().containsKey(capabilityType)) {
                            //the capability definition name is the same as the provided capability type name.
                            CapabilityType capabilityTypeObject = file.capabilityTypes().get(capabilityType);
                            if (context.getCurrentToscaFile().nodeTypes() != null && context.getCurrentToscaFile().nodeTypes().getValue().containsKey(nodeTypeKey) && context.getCurrentToscaFile().nodeTypes().getValue().get(nodeTypeKey).capabilities() != null) {
                                context.getCurrentToscaFile().nodeTypes().getValue().get(nodeTypeKey).capabilities().getValue().put(capabilityType, new CapabilityDefinition(capabilityTypeObject, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), new HashMap<>(), new HashMap<>())) ;
                            }
                            return;
                        }
                    }
                }
                Collection<Map<String, TOSCAFile>> namespaces = context.getNamespaceDefinitions().get(context.getCurrentToscaFilePath());
                for (Map<String, TOSCAFile> mapOfNamespaces : namespaces) {
                    for (String namespacesKey : mapOfNamespaces.keySet()) {
                        if ( capabilityType instanceof String) {
                            String[] parts = capabilityType.split(":");
                            if (parts.length == 2) {
                                String typeWithoutNamespace = parts[1].trim();
                                String namespace = parts[0].trim();
                                if (namespacesKey.equals(namespace)) {
                                    TOSCAFile file = mapOfNamespaces.getOrDefault(namespace, null);
                                    if (file != null && !file.capabilityTypes().isEmpty() && file.capabilityTypes() != null && file.capabilityTypes().containsKey(typeWithoutNamespace)) {
                                        //the capability definition name is the same as the provided capability type name.
                                        CapabilityType capabilityTypeObject = file.capabilityTypes().get(typeWithoutNamespace);
                                        if (context.getCurrentToscaFile().nodeTypes() != null && context.getCurrentToscaFile().nodeTypes().getValue().containsKey(nodeTypeKey) && context.getCurrentToscaFile().nodeTypes().getValue().get(nodeTypeKey).capabilities() != null) {
                                            context.getCurrentToscaFile().nodeTypes().getValue().get(nodeTypeKey).capabilities().getValue().put(capabilityType, new CapabilityDefinition(capabilityTypeObject, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), new HashMap<>(), new HashMap<>())) ;
                                        }
                                        return; 
                                    }
                                }
                            }
                        }
                    }
                }
            }
                Mark mark = context.getContextDependentConstructorPositions().get(nodeTypePath + "." + nodeType.get(key));
                int line = mark != null ? mark.getLine() + 1 : -1;
                int column = mark != null ? mark.getColumn() + 1 : -1;
                int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

                handleNotValidKeywords("Invalid capability: " + nodeType.get(key), line, column,endColumn);
            
        } catch (Exception e) {
            Mark mark = context.getContextDependentConstructorPositions().get(nodeTypePath + "." + nodeType.get(key));
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);

            handleNotValidKeywords(e.getMessage(), line, column,endColumn);
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
