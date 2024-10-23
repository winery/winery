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

import org.eclipse.winery.lsp.Server.ServerCore.DataModels.SchemaDefinition;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;
import java.util.Map;
import java.util.Optional;

public class SchemaDefinitionParser {
    public static SchemaDefinition parseSchemaDefinition(Map<String, Object> Schema) {
        if (Schema == null) { return null; }
        
        ToscaString type  = new ToscaString("");
        if (Schema.get("type") != null && Schema.get("type") instanceof String) {
            type = new ToscaString((String) Schema.get("type"));
        }

        Optional<ToscaString> description  = Optional.empty();
        if (Schema.get("description") != null && Schema.get("description") instanceof String) {
            description = Optional.of(new ToscaString((String) Schema.get("description")));
        }

        Optional<Object> validation   = Optional.empty();
        if (Schema.get("validation") != null && Schema.get("validation") instanceof String) {
            validation = Optional.of(Schema.get("validation")); //TODO add validation clause as a stack of function
        }
        
        Optional<SchemaDefinition> keySchema = Optional.empty();
        if (Schema.get("keySchema") != null && Schema.get("keySchema") instanceof Map) {
            keySchema = Optional.of(parseSchemaDefinition((Map<String, Object>) Schema.getOrDefault("key_schema",getDefaultKeySchema())));

        }
        
        Optional<SchemaDefinition> entrySchema = Optional.empty();
        if (Schema.get("entrySchema") != null && Schema.get("entrySchema") instanceof Map) {
            entrySchema = Optional.of(parseSchemaDefinition((Map<String, Object>) Schema.getOrDefault("entrySchema",Optional.empty())));
        }
            
        return new SchemaDefinition(
            type,
            description,
            validation,
            keySchema,
            entrySchema
        );
    }

    private static SchemaDefinition getDefaultKeySchema() {
        ToscaString type = new ToscaString("string");
        return new SchemaDefinition(
            type,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
    }

}
