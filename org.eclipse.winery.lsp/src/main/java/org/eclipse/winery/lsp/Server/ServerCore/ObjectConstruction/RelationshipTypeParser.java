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

public class RelationshipTypeParser {
    private static final Map<String, RelationshipType> relationshipTypesNamesMap = new HashMap<>();
    public static Map<String, RelationshipType> parseRelationshipTypes(Map<String, Object> relationshipTypesMap) {
        if (relationshipTypesMap == null) {
            return Collections.emptyMap();
        }
        return relationshipTypesMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    RelationshipType relationshipType = new RelationshipType(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), new HashMap<>(), new ToscaMap<>(new HashMap<>()), new ToscaMap<>(new HashMap<>()), Optional.empty(), Optional.empty(),  Optional.empty());
                    if (e.getValue() instanceof Map) {
                        relationshipType = parseRelationshipType((Map<String, Object>) e.getValue());
                        relationshipTypesNamesMap.put(e.getKey(), relationshipType);
                    }
                    return relationshipType;
                }
            ));
    }
    
    public static RelationshipType parseRelationshipType(Map<String, Object> relationshipTypeMap) {
        if (relationshipTypeMap == null) {
            return null;
        }

        Optional<ToscaString> version = Optional.empty();
        if (relationshipTypeMap.get("version") != null && relationshipTypeMap.get("version") instanceof String) {
            version = Optional.of(new ToscaString((String) relationshipTypeMap.get("version")));
        }

        Optional<ToscaMap<String, String>> metadata = Optional.empty();
        if (relationshipTypeMap.get("metadata") != null && relationshipTypeMap.get("metadata") instanceof String) {
            metadata = Optional.of(new ToscaMap<>((Map<String, String>) relationshipTypeMap.get("metadata")));
        }

        Optional<ToscaString> description = Optional.empty();
        if (relationshipTypeMap.get("description") != null && relationshipTypeMap.get("description") instanceof String) {
            description = Optional.of(new ToscaString((String) relationshipTypeMap.get("description")));
        }
        
        Map<String, PropertyDefinition> properties  = new HashMap<>();
        if (relationshipTypeMap.get("properties") != null && relationshipTypeMap.get("properties") instanceof Map) {
            properties = PropertyDefinitionParser.parseProperties((Map<String, Object>) relationshipTypeMap.get("properties"));
        }

        ToscaMap<String, AttributeDefinition> attributes = new ToscaMap<>(new HashMap<>());
        if (relationshipTypeMap.get("attributes") != null && relationshipTypeMap.get("attributes") instanceof Map) {
            attributes = new ToscaMap<>(AttributeDefinitionParser.parseAttributeDefinition( (Map<String, Object>) relationshipTypeMap.get("attributes")));
        }

        ToscaMap<String, InterfaceDefinition> interfaces = new ToscaMap<>(new HashMap<>());
        if (relationshipTypeMap.get("interfaces") != null && relationshipTypeMap.get("interfaces") instanceof Map<?,?>) {
            interfaces = new ToscaMap<>(InterfaceDefinitionParser.parseInterfaceDefinitions((Map<String, Object>) relationshipTypeMap.get("interfaces")));
        }
        
        Optional<ToscaList<String>> valid_source_node_types = Optional.empty();
        if (relationshipTypeMap.get("valid_source_node_types") != null && relationshipTypeMap.get("valid_source_node_types") instanceof List<?>) {
            valid_source_node_types = Optional.of(new ToscaList<>((List<String>) relationshipTypeMap.get("valid_source_node_types")));
        }

        Optional<ToscaList<String>> valid_capability_types = Optional.empty();
        if (relationshipTypeMap.get("valid_capability_types") != null && relationshipTypeMap.get("valid_capability_types") instanceof List<?>) {
            valid_capability_types = Optional.of(new ToscaList<>((List<String>) relationshipTypeMap.get("valid_capability_types")));
        }

        Optional<ToscaList<String>> valid_target_node_types = Optional.empty();
        if (relationshipTypeMap.get("valid_target_node_types") != null && relationshipTypeMap.get("valid_target_node_types") instanceof List<?>) {
            valid_target_node_types = Optional.of(new ToscaList<>((List<String>) relationshipTypeMap.get("valid_target_node_types")));
        }

        Optional<RelationshipType> derivedFrom = Optional.empty();
        try {
            if (relationshipTypeMap.get("derived_from") != null && relationshipTypeMap.get("derived_from")  instanceof String) {
                RelationshipType derivedFromValue = getRelationshipType((String) relationshipTypeMap.get("derived_from"));

                if (derivedFromValue != null) {
                    derivedFrom = Optional.of(derivedFromValue);
                    properties.putAll(derivedFromValue.properties());
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
        return new RelationshipType(
            derivedFrom,
            version,
            metadata,
            description,
            properties,
            attributes,
            interfaces,
            valid_capability_types,
            valid_target_node_types,
            valid_source_node_types
        );
    }

    public static RelationshipType getRelationshipType(String derivedFrom) {
        return relationshipTypesNamesMap.getOrDefault(derivedFrom, null);
    }
}
