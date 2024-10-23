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

package org.eclipse.winery.lsp.Server.ServerCore.DataModels;

import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaList;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.Map;
import java.util.Optional;

public record RelationshipType(
    Optional<RelationshipType> derivedFrom,
    Optional<ToscaString> version,
    Optional<ToscaMap<String, String>> metadata,
    Optional<ToscaString> description,
    Map<String, PropertyDefinition> properties,
    ToscaMap<String, AttributeDefinition> attributes,
    ToscaMap<String,  InterfaceDefinition> interfaces,
    Optional<ToscaList<String>> valid_capability_types,
    Optional<ToscaList<String>> valid_target_node_types,
    Optional<ToscaList<String>> valid_source_node_types
) {
    public RelationshipType addOrOverridePropertyDefinition(String key, PropertyDefinition newDefinition) {
        if (!properties.isEmpty()) {
        Map<String, PropertyDefinition> updatedProperties = properties;
        updatedProperties.put(key, newDefinition);
        return new RelationshipType(
            derivedFrom,
            version,
            metadata,
            description,
            updatedProperties,
            attributes,
            interfaces,
            valid_capability_types,
            valid_target_node_types,
            valid_source_node_types
        );
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
   
}
