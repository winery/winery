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

public record CapabilityDefinition(CapabilityType type,
                                   Optional<ToscaString> description,
                                   Optional<ToscaMap<String, String>> metadata,
                                   Optional<ToscaList<String>> valid_source_node_types,
                                   Optional<ToscaList<String>> valid_relationship_types,
                                   Map<String, PropertyDefinition> properties,
                                   Map<String, AttributeDefinition> attributes
) {   
    public CapabilityDefinition withType(CapabilityType newType) {
    return new CapabilityDefinition(
        newType, // Update the type field
        this.description,
        this.metadata,
        this.valid_source_node_types,
        this.valid_relationship_types,
        this.properties,
        this.attributes
    );
}
}

