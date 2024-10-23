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
/**
 * Property Definition
 * For more details on the TOSCA specification, visit:
 * <a href="https://docs.oasis-open.org/tosca/TOSCA/v2.0/csd06/TOSCA-v2.0-csd06.html#93-property-definition">Property Definition</a>
 */
package org.eclipse.winery.lsp.Server.ServerCore.DataModels;

import io.soabase.recordbuilder.core.RecordBuilder;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaBoolean;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;
import java.util.*;

@RecordBuilder
public record PropertyDefinition(
    ToscaString type,
    Optional<ToscaString> description,
    Optional<ToscaMap<String, Object>> metadata,
    ToscaBoolean required,
    Optional<Object> Default,
    Optional<Object> value,
    Optional<Stack<Map<String, List<String>>>> validation,
    Optional<SchemaDefinition> keySchema,
    Optional<SchemaDefinition> entrySchema) implements Cloneable {
    
    // Method to set the validation variable
    public PropertyDefinition withValidation(Stack<Map<String, List<String>>> newValidation) {
        return new PropertyDefinition(
            this.type,
            this.description,
            this.metadata,
            this.required,
            this.Default,
            this.value,
            Optional.ofNullable(newValidation),
            this.keySchema,
            this.entrySchema
        );
    }

    // Method to set the value variable
    public PropertyDefinition withValue(Object newValue) {
        return new PropertyDefinition(
            this.type,
            this.description,
            this.metadata,
            this.required,
            this.Default,
            Optional.ofNullable(newValue),
            this.validation,
            this.keySchema,
            this.entrySchema
        );
    }

    @Override
    public PropertyDefinition clone() {
        try {
            return (PropertyDefinition) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen
        }
    }
}
