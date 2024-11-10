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
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaList;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;
import java.util.*;
import java.util.stream.Collectors;

public class NodeTypeParser {
    private static final Map<String, NodeType> NodeTypeNamesMap = new HashMap<>();
    public static Map<String, NodeType> parseNodeTypes(Map<String, Object> nodeTypes) {
        if (nodeTypes == null) {
            return Collections.emptyMap();
        }
        return nodeTypes.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    NodeType nodeType = new NodeType( Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), new HashMap<>(), new ToscaMap<>(new HashMap<>()), new ToscaMap<>(new HashMap<>()), new ToscaList<>(new ArrayList<>()), new ToscaMap<>(new HashMap<>()), new ToscaMap<>(new HashMap<>()));
                    if (e.getValue() instanceof Map) {
                        nodeType = parseNodeType((Map<String, Object>) e.getValue());
                        NodeTypeNamesMap.put(e.getKey(), nodeType);
                    }
                    return nodeType;
                }
            ));
    }

    public static NodeType parseNodeType(Map<String, Object> nodeTypeMap) {
        if (nodeTypeMap == null) {
            return null;
        }
        
        Optional<ToscaString> version  = Optional.empty();
        if (nodeTypeMap.get("version") != null && nodeTypeMap.get("version") instanceof String) {
            version = Optional.of(new ToscaString((String) nodeTypeMap.get("version")));
        }
        
        Optional<ToscaMap<String, String>> metadata  = Optional.empty();
        if (nodeTypeMap.get("metadata") != null && nodeTypeMap.get("metadata") instanceof Map) {
            metadata = Optional.of(new ToscaMap<>((Map<String, String>) nodeTypeMap.get("metadata")));
        }
        
        Optional<ToscaString> description  = Optional.empty();
        if (nodeTypeMap.get("description") != null && nodeTypeMap.get("description") instanceof String) {
            description = Optional.of(new ToscaString((String) nodeTypeMap.get("description")));
        }

        Map<String, PropertyDefinition> properties = new HashMap<>();
        if (nodeTypeMap.get("properties") != null && nodeTypeMap.get("properties") instanceof Map) {
            properties = PropertyDefinitionParser.parseProperties((Map<String, Object>) nodeTypeMap.get("properties"));
        }
        
        ToscaMap<String, AttributeDefinition> attributes = new ToscaMap<>(new HashMap<>());
        if (nodeTypeMap.get("attributes") != null && nodeTypeMap.get("attributes") instanceof Map) {
            attributes = new ToscaMap<>(AttributeDefinitionParser.parseAttributeDefinition( (Map<String, Object>) nodeTypeMap.get("attributes")));
        }

        ToscaMap<String, CapabilityDefinition> capabilities = new ToscaMap<>(new HashMap<>());
        if (nodeTypeMap.get("capabilities") != null && nodeTypeMap.get("capabilities") instanceof Map) {
            capabilities = new ToscaMap<>(CapabilityDefinitionParser.parseCapabilityDefinitions((Map<String, Object>) nodeTypeMap.get("capabilities")));
        }

        ToscaList<RequirementDefinition> requirements = new ToscaList<>(new ArrayList<>());
        if (nodeTypeMap.get("requirements") != null && nodeTypeMap.get("requirements") instanceof List) {
            requirements = new ToscaList<>(RequirementDefinitionParser.parseRequirementDefinitions(nodeTypeMap.get("requirements")));
        }

        ToscaMap<String, InterfaceDefinition> interfaces = new ToscaMap<>(new HashMap<>());
        if (nodeTypeMap.get("interfaces") != null && nodeTypeMap.get("interfaces") instanceof Map<?,?>) {
            interfaces = new ToscaMap<>(InterfaceDefinitionParser.parseInterfaceDefinitions((Map<String, Object>) nodeTypeMap.get("interfaces")));
        }

        ToscaMap<String, ArtifactDefinition> artifacts = new ToscaMap<>(new HashMap<>());
        if (nodeTypeMap.get("artifacts") != null && nodeTypeMap.get("artifacts") instanceof Map<?,?>) {
            artifacts = new ToscaMap<>(ArtifactDefinitionParser.parseArtifactDefinition((Map<String, Object>) nodeTypeMap.get("artifacts")));
        }
        
        Optional<NodeType> derivedFrom = Optional.empty();
        if (nodeTypeMap.get("derived_from") != null && nodeTypeMap.get("derived_from")  instanceof String) {
            NodeType derivedFromValue = getNodeType((String) nodeTypeMap.get("derived_from"));

            if (derivedFromValue != null) {
                derivedFrom = Optional.of(derivedFromValue);
                properties.putAll(derivedFromValue.properties());
                capabilities.getValue().putAll(derivedFromValue.capabilities().getValue());
                requirements.getValue().addAll(derivedFromValue.requirements().getValue());
            }
        }
        
        return new NodeType(derivedFrom,
            version,
            metadata,
            description,
            properties,
            attributes,
            capabilities,
            requirements,
            interfaces,
            artifacts
        );
    }

    public static NodeType getNodeType(String derivedFrom) {
        
        return NodeTypeNamesMap.getOrDefault(derivedFrom, null);
    }

}
