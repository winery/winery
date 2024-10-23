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

import org.eclipse.winery.lsp.Server.ServerCore.DataModels.PropertyDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.DataModels.SchemaDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.*;
import org.tinylog.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class PropertyDefinitionParser {
    
    public static Map<String, PropertyDefinition> parseProperties(Map<String, Object> propertiesMap) {
        if (propertiesMap == null) {
            return Collections.emptyMap();
        }
        return propertiesMap.entrySet().stream()
            .filter(e -> e.getValue() instanceof Map) // Check if the value is an instance of Map
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                
                e -> {
                    PropertyDefinition propertyDefinition  = new PropertyDefinition(new ToscaString(""), Optional.empty(), Optional.empty(), new ToscaBoolean(true), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
                    if (e.getValue() != null && e.getValue() instanceof Map) {
                        propertyDefinition =  parsePropertyDefinition((Map<String, Object>) e.getValue());
                    }
                  return propertyDefinition;
                }
            ));
    }

    public static PropertyDefinition parsePropertyDefinition(Map<String, Object> propertyDefinitionMap) {
        if (propertyDefinitionMap == null) {
            return null;
        }
        
        ToscaString type = new ToscaString("");
        if (propertyDefinitionMap.get("type") != null && propertyDefinitionMap.get("type") instanceof String) {
            type = new ToscaString((String) propertyDefinitionMap.get("type"));
        }

        Optional<ToscaString> description  = Optional.empty();
        if (propertyDefinitionMap.get("description") != null && propertyDefinitionMap.get("description") instanceof String) {
            description = Optional.ofNullable(new ToscaString((String) propertyDefinitionMap.get("description")));        
        }

        Optional<ToscaMap<String, Object>> metadata  = Optional.empty();
        if (propertyDefinitionMap.get("metadata") != null && propertyDefinitionMap.get("metadata") instanceof Map) {
            metadata = Optional.ofNullable(new ToscaMap<>((Map<String, Object>) propertyDefinitionMap.get("metadata")));
        }

        ToscaBoolean required  =  new ToscaBoolean(true);
        if (propertyDefinitionMap.get("required") != null && propertyDefinitionMap.get("required") instanceof Boolean) {
            required = new ToscaBoolean((Boolean) propertyDefinitionMap.getOrDefault("required", true));
        }

        Optional<Object> Default = Optional.empty();
        if (propertyDefinitionMap.get("default") != null) {
            Default = Optional.ofNullable( propertyDefinitionMap.get("default"));
        }

        Optional<Object> value  = Optional.ofNullable(propertyDefinitionMap.get("value"));
        if (propertyDefinitionMap.get("value") != null) {
            value = Optional.ofNullable(propertyDefinitionMap.get("value"));
        }

        Optional<Stack<Map<String, List<String>>>> validation = Optional.empty();
        if ((propertyDefinitionMap.get("validation") != null && propertyDefinitionMap.get("validation") instanceof String)) {
            Logger.warn("the validation: " + propertyDefinitionMap.get("validation"));
            validation = ValidationParser.parseValidation((String) propertyDefinitionMap.get("validation"));
            Logger.warn("the validation: " + propertyDefinitionMap.get("validation"));
        } else if (propertyDefinitionMap.get("validation") != null) {
            try {
            validation = ValidationParser.parseValidation((propertyDefinitionMap.get("validation").toString()));
            } catch (Exception e) {
                 Logger.error("The error message, " + e,e);
            }     
        }
        
        Optional<SchemaDefinition> keySchema = Optional.empty();
        if (propertyDefinitionMap.get("keySchema") != null && propertyDefinitionMap.get("keySchema") instanceof Map) {
            keySchema = Optional.ofNullable(SchemaDefinitionParser.parseSchemaDefinition((Map<String, Object>) propertyDefinitionMap.getOrDefault("key_schema",Optional.empty())));
        }

        Optional<SchemaDefinition> entrySchema = Optional.empty();
        if (propertyDefinitionMap.get("entrySchema") != null && propertyDefinitionMap.get("entrySchema") instanceof Map) {
            entrySchema = Optional.ofNullable(SchemaDefinitionParser.parseSchemaDefinition((Map<String, Object>) propertyDefinitionMap.getOrDefault("entrySchema",Optional.empty())));
        }
   
        return new PropertyDefinition (
            type,
            description,
            metadata,
            required,
            Default,
            value,
            validation,
            keySchema,
            entrySchema
        );
    }
}
