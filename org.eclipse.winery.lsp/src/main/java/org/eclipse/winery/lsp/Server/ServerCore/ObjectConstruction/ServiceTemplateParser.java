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

package org.eclipse.winery.lsp.Server.ServerCore.ObjectConstruction;

import com.google.common.collect.ImmutableMap;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.*;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaList;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.*;

public class ServiceTemplateParser {
    public static ServiceTemplate parseServiceTemplate(Map<String, Object> serviceTemplate) { 
        Optional<ToscaString> description  = Optional.empty();
        if (serviceTemplate.get("description") != null && serviceTemplate.get("description") instanceof String) {
            description = Optional.of(new ToscaString((String) serviceTemplate.get("description")));
        }

        Optional<ToscaMap<String, Object>> metadata  = Optional.empty();
        if (serviceTemplate.get("metadata") != null && serviceTemplate.get("metadata") instanceof Map) {
            metadata = Optional.of(new ToscaMap<>((Map<String, Object>) serviceTemplate.get("metadata")));
        }

        ToscaMap<String, ParameterDefinition> inputs  = new ToscaMap<>(new HashMap<>());
        if (serviceTemplate.get("inputs") != null && serviceTemplate.get("inputs") instanceof Map) {
            inputs = new ToscaMap<>(ParameterDefinitionParser.parseParameterDefinition((Map<String, Object>) serviceTemplate.get("inputs")));
        }

        ToscaMap<String, NodeTemplate> nodeTemplates  =  new ToscaMap<>(ImmutableMap.of()) ;
        if (serviceTemplate.get("node_templates") != null && serviceTemplate.get("node_templates") instanceof Map) {
            nodeTemplates = new ToscaMap<>(NodeTemplatesParser.parseNodeTemplates((Map<String, Object>) serviceTemplate.get("node_templates")));
        }

        ToscaMap<String, RelationshipTemplate> relationshipTemplate  = new ToscaMap<>(new HashMap<>());
        if (serviceTemplate.get("relationship_templates") != null && serviceTemplate.get("relationship_templates") instanceof Map) {
            relationshipTemplate = new ToscaMap<>(RelationshipTemplateParser.parseRelationshipTemplate((Map<String, Object>) serviceTemplate.get("relationship_templates")));
        }

        ToscaMap<String, GroupDefinition> groups  = new ToscaMap<>(new HashMap<>());
        if (serviceTemplate.get("groups") != null && serviceTemplate.get("groups") instanceof Map) {
            groups = new ToscaMap<>(GroupDefinitionParser.parseGroupDefinition((Map<String, Object>) serviceTemplate.get("groups")));
        }

        ToscaMap<String, WorkflowDefinitions> workflows  = new ToscaMap<>(new HashMap<>());
        if (serviceTemplate.get("workflows") != null && serviceTemplate.get("workflows") instanceof Map) {
            workflows = new ToscaMap<>(WorkflowsDefinitionParser.parseWorkflowsDefinition((Map<String, Object>) serviceTemplate.get("workflows")));
        }

        ToscaList<PolicyDefinition> policies  = new ToscaList<>(new ArrayList<>());
        if (serviceTemplate.get("policies") != null && serviceTemplate.get("policies") instanceof List<?>) {
            policies = new ToscaList<>(PolicyDefinitionParser.parsePolicyDefinition((Map<String, Object>) serviceTemplate.get("policies")));
        }

        ToscaMap<String, ParameterDefinition> outputs  = new ToscaMap<>(new HashMap<>());
        if (serviceTemplate.get("outputs") != null && serviceTemplate.get("outputs") instanceof Map) {
            outputs = new ToscaMap<>(ParameterDefinitionParser.parseParameterDefinition((Map<String, Object>) serviceTemplate.get("outputs")));
        }

        Optional<SubstitutionMapping> substitutionMappings  = Optional.empty();
        if (serviceTemplate.get("substitution_mappings") != null && serviceTemplate.get("substitution_mappings") instanceof List<?>) {
            substitutionMappings = Optional.of(SubstitutionMappingParser.parseSubstitutionMapping((Map<String, Object>) serviceTemplate.get("substitution_mappings")));
        }

        return new ServiceTemplate(
            description,
            metadata,
            inputs,
            nodeTemplates,
            relationshipTemplate,
            groups,
            workflows,
            policies,
            outputs,
            substitutionMappings
        );

    }
}
