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

import org.eclipse.winery.lsp.Server.ServerCore.DataModels.AttributeDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.CapabilityDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.CapabilityType;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.PropertyDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaList;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.*;
import java.util.stream.Collectors;

public class CapabilityDefinitionParser {
    public static Map<String, CapabilityDefinition> parseCapabilityDefinitions(Map<String, Object> capabilityDefinitionMap) {
        if (capabilityDefinitionMap == null) {
            return Collections.emptyMap();
        }
        return capabilityDefinitionMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    CapabilityDefinition capabilityDefinition = new CapabilityDefinition(null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Map.of(), Map.of());
                    if (e.getValue() instanceof Map) {
                        capabilityDefinition = parseCapabilityDefinition((Map<String, Object>) e.getValue());
                    }
                    return capabilityDefinition;
                }
            ));
    }

    public static CapabilityDefinition parseCapabilityDefinition(Map<String, Object> capabilityDefinitionMap) {
        if (capabilityDefinitionMap == null) {
            return null;
        }

        CapabilityType type = new CapabilityType(Optional.empty(),Optional.empty(),Optional.empty(), Optional.empty(),Optional.empty(),Optional.empty(), new HashMap<>(), new ToscaMap<>(new HashMap<>())); // will be handled in the validation
        
        Optional<ToscaMap<String, String>> metadata = Optional.empty();
        if (capabilityDefinitionMap.get("metadata") != null && capabilityDefinitionMap.get("metadata") instanceof String) {
            metadata = Optional.of(new ToscaMap<>((Map<String, String>) capabilityDefinitionMap.get("metadata")));
        }

        Optional<ToscaString> description = Optional.empty();
        if (capabilityDefinitionMap.get("description") != null && capabilityDefinitionMap.get("description") instanceof String) {
            description = Optional.of(new ToscaString((String) capabilityDefinitionMap.get("description")));
        }

        Optional<ToscaList<String>> valid_source_node_types = Optional.empty();
        if (capabilityDefinitionMap.get("valid_source_node_types") != null && capabilityDefinitionMap.get("valid_source_node_types") instanceof List<?>) {
            valid_source_node_types = Optional.of(new ToscaList<>((List<String>) capabilityDefinitionMap.get("valid_source_node_types")));
        }

        Optional<ToscaList<String>> valid_relationship_types  = Optional.empty();
        if (capabilityDefinitionMap.get("valid_relationship_types") != null && capabilityDefinitionMap.get("valid_relationship_types") instanceof List<?>) {
            valid_relationship_types = Optional.of(new ToscaList<>((List<String>) capabilityDefinitionMap.get("valid_relationship_types")));
        }

        Map<String, PropertyDefinition> properties  = new HashMap<>();
        if (capabilityDefinitionMap.get("properties") != null && capabilityDefinitionMap.get("properties") instanceof Map) {
            properties = PropertyDefinitionParser.parseProperties((Map<String, Object>) capabilityDefinitionMap.get("properties"));
        }

        Map<String, AttributeDefinition> attributes = (Map<String, AttributeDefinition>) capabilityDefinitionMap.get("attributes"); //TODO add the attribute definition parser

        return new CapabilityDefinition( type,
            description,
            metadata,
            valid_source_node_types,
            valid_relationship_types,
            properties,
            attributes
        );
    }
}
