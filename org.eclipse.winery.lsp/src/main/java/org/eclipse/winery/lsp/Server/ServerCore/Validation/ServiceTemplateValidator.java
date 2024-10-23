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

public class ServiceTemplateValidator implements DiagnosesHandler {
    public ArrayList<DiagnosticsSetter> diagnostics = new ArrayList<>();
    LSContext context;
    
    public ServiceTemplateValidator(LSContext context) {
    this.context = context;
    }

    public ArrayList<DiagnosticsSetter> validateServiceTemplates(Map<String, Object> serviceTemplatesMap, Map<String, Mark> positions, String yamlContent, String[] lines) {
        Set<String> validServiceTemplateKeywords = Set.of(
            "description", "metadata", "inputs", "node_templates", "relationship_templates", "groups", "workflows", "policies", "outputs", "substitution_mappings"
        );
        validateRequiredKeys(serviceTemplatesMap,yamlContent, lines);
        for (String serviceTemplateKeyWords : serviceTemplatesMap.keySet()) {
                    if (!validServiceTemplateKeywords.contains(serviceTemplateKeyWords)) {
                        Mark mark = context.getContextDependentConstructorPositions().get("service_template" + "." + serviceTemplateKeyWords);
                        int line = mark != null ? mark.getLine() + 1 : -1;
                        int column = mark != null ? mark.getColumn() + 1 : -1;
                        int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);

                        handleNotValidKeywords("Invalid service template keyword: " + serviceTemplateKeyWords , line, column, endColumn);
                    } else if (serviceTemplateKeyWords != null && serviceTemplateKeyWords.equals("node_templates")) {
                        Object nodeTemplates = serviceTemplatesMap.get(serviceTemplateKeyWords);
                        if (nodeTemplates instanceof Map) {
                            NodeTemplatesValidator nodeTemplatesValidator = new NodeTemplatesValidator(context);
                            ArrayList<DiagnosticsSetter> serviceTemplateDiagnostics;
                            serviceTemplateDiagnostics = nodeTemplatesValidator.validateNodeTemplates((Map<String, Object>) nodeTemplates, positions, yamlContent, lines, "service_template");
                            diagnostics.addAll(serviceTemplateDiagnostics);
                        }
                    }
                }
        return diagnostics;
    }

    private void validateRequiredKeys(Map<String, Object> serviceTemplatesMap, String yamlContent, String[] lines) {
            if (!serviceTemplatesMap.containsKey("node_templates")) {
                Mark mark = context.getContextDependentConstructorPositions().get("service_template");
                int line = mark != null ? mark.getLine() + 1 : -1;
                int column = mark != null ? mark.getColumn() + 1 : -1;
                int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
                handleNotValidKeywords("Service template  Missing required key: node_templates ", line, column, endColumn);
            }
    }

    @Override
    public void handleNotValidKeywords(String message, int line, int column, int endColumn) {
        DiagnosticsSetter serviceTemplateDiagnostic = new DiagnosticsSetter();
        serviceTemplateDiagnostic.setErrorMessage(message);
        serviceTemplateDiagnostic.setErrorContext("Not Valid Keywords");
        serviceTemplateDiagnostic.setErrorColumn(column);
        serviceTemplateDiagnostic.setErrorEndColumn(endColumn);
        serviceTemplateDiagnostic.setErrorLine(line);
        diagnostics.add(serviceTemplateDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, Path path) {
        DiagnosticsSetter serviceTemplateDiagnostic = new DiagnosticsSetter();
        serviceTemplateDiagnostic.setErrorMessage(message);
        serviceTemplateDiagnostic.setErrorContext("Parsing Error");
        try {
            long lineCount = Files.lines(path).count();
            serviceTemplateDiagnostic.setErrorLine((int) lineCount);
        } catch (IOException e) {
            serviceTemplateDiagnostic.setErrorLine(-1);
        }
        serviceTemplateDiagnostic.setErrorColumn(1);
        diagnostics.add(serviceTemplateDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, String content) {
        DiagnosticsSetter serviceTemplateDiagnostic = new DiagnosticsSetter();
        serviceTemplateDiagnostic.setErrorMessage(message);
        serviceTemplateDiagnostic.setErrorContext("Parsing Error");
        serviceTemplateDiagnostic.setErrorLine(countLines(content));
        serviceTemplateDiagnostic.setErrorColumn(1);
        diagnostics.add(serviceTemplateDiagnostic);
    }

    private int countLines(String content) {
        return (int) content.lines().count();
    }

}
