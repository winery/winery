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

import org.eclipse.winery.lsp.Server.ServerCore.DataModels.*;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.*;
import java.util.*;
import java.util.stream.Collectors;

public class NodeTemplatesParser {
    public static Map<String, NodeTemplate> parseNodeTemplates(Map<String, Object> nodeTemplates) {
        if (nodeTemplates == null) {
            return Collections.emptyMap();
        }
        return nodeTemplates.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    NodeTemplate nodeTemplate = new NodeTemplate(  new NodeType(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), new HashMap<>(), new ToscaMap<>(new HashMap<>()), new ToscaMap<>(new HashMap<>()), new ToscaList<>(new ArrayList<>()), new ToscaMap<>(new HashMap<>()), new ToscaMap<>(new HashMap<>())), Optional.empty(), Optional.empty(), Optional.empty(), new HashMap<>(), new ToscaMap<>(new HashMap<>()), new ToscaMap<>(new HashMap<>()), new ToscaList<>(new ArrayList<>()), new ToscaMap<>(new HashMap<>()), new ToscaMap<>(new HashMap<>()), Optional.empty(), Optional.empty(), Optional.empty());
                    if (e.getValue() instanceof Map) {
                        nodeTemplate = parseNodeTemplate((Map<String, Object>) e.getValue());
                    }
                    return nodeTemplate;
                }
            ));
    }

    public static NodeTemplate parseNodeTemplate(Map<String, Object> nodeTemplateMap) {
        if (nodeTemplateMap == null) {
            return null;
        }

        NodeType type = new NodeType(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), new HashMap<>(), new ToscaMap<>(new HashMap<>()), new ToscaMap<>(new HashMap<>()), new ToscaList<>(new ArrayList<>()), new ToscaMap<>(new HashMap<>()), new ToscaMap<>(new HashMap<>())); // will be assigned in the validation

        Optional<ToscaString> description  = Optional.empty();
        if (nodeTemplateMap.get("description") != null && nodeTemplateMap.get("description") instanceof String) {
            description = Optional.ofNullable(new ToscaString((String) nodeTemplateMap.get("description")));
        }

        Optional<ToscaMap<String, Object>> metadata  = Optional.empty();
        if (nodeTemplateMap.get("metadata") != null && nodeTemplateMap.get("metadata") instanceof Map) {
            metadata = Optional.ofNullable(new ToscaMap<>((Map<String, Object>) nodeTemplateMap.get("metadata")));
        }

        Optional<ToscaList<String>> directives = Optional.empty();
        if (nodeTemplateMap.get("directives") != null && nodeTemplateMap.get("directives") instanceof List<?>) {
            directives = Optional.ofNullable(new ToscaList((List) nodeTemplateMap.get("directives")));
        }

        Map<String, PropertyDefinition> properties = new HashMap<>();
        if (nodeTemplateMap.get("properties") != null && nodeTemplateMap.get("properties") instanceof Map) {
            properties = PropertyDefinitionParser.parseProperties((Map<String, Object>) nodeTemplateMap.get("properties"));
        }

        ToscaMap<String, AttributeDefinition> attributes = new ToscaMap<>(new HashMap<>());
        if (nodeTemplateMap.get("attributes") != null && nodeTemplateMap.get("attributes") instanceof Map) {
            attributes = new ToscaMap<>(AttributeDefinitionParser.parseAttributeDefinition( (Map<String, Object>) nodeTemplateMap.get("attributes")));
        }

        ToscaMap<String, CapabilityType> capabilities = new ToscaMap<>(new HashMap<>());
        if (nodeTemplateMap.get("capability_types") != null && nodeTemplateMap.get("capability_types") instanceof Map) {
            capabilities = new ToscaMap<>(CapabilityTypeParser.parseCapabilityTypes((Map<String, Object>) nodeTemplateMap.get("capability_types")));
        }

        ToscaList<RequirementAssignment> requirements = new ToscaList<>(new ArrayList<>());
        if (nodeTemplateMap.get("requirements") != null && nodeTemplateMap.get("requirements") instanceof List) {
            requirements = new ToscaList<>(RequirementAssignmentParser.parseRequirementAssignment((List<Object>) nodeTemplateMap.get("requirements")));
        }

        ToscaMap<String, InterfaceAssignment> interfaces = new ToscaMap<>(new HashMap<>());
        if (nodeTemplateMap.get("interfaces") != null && nodeTemplateMap.get("interfaces") instanceof Map<?,?>) {
            interfaces = new ToscaMap<>(InterfaceAssignmentParser.parseInterfaceAssignment((Map<String, Object>) nodeTemplateMap.get("interfaces")));
        }

        ToscaMap<String, ArtifactDefinition> artifacts = new ToscaMap<>(new HashMap<>());
        if (nodeTemplateMap.get("artifacts") != null && nodeTemplateMap.get("artifacts") instanceof Map<?,?>) {
            artifacts = new ToscaMap<>(ArtifactDefinitionParser.parseArtifactDefinition((Map<String, Object>) nodeTemplateMap.get("artifacts")));
        }
        
        Optional<ToscaInteger> count = Optional.empty();
        if (nodeTemplateMap.get("count") != null && nodeTemplateMap.get("count") instanceof Integer) {
            count = Optional.of(new ToscaInteger((Integer) nodeTemplateMap.get("count")));
        }

        Optional<Object> node_filter = Optional.empty();
        if (nodeTemplateMap.get("node_filter") != null) { //TODO
            node_filter = Optional.of((nodeTemplateMap.get("node_filter")));
        }

        Optional<ToscaString> copy = Optional.empty();
        if (nodeTemplateMap.get("copy") != null && nodeTemplateMap.get("copy") instanceof String) {
            copy = Optional.of(new ToscaString((String) nodeTemplateMap.get("copy")));
        }
        
        return new NodeTemplate(type,
            description,
            directives,
            metadata,
            properties,
            attributes,
            capabilities,
            requirements,
            interfaces,
            artifacts,
            count,
            node_filter,
            copy
        );
    }

}
