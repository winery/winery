/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.jsonsupport;

import java.io.IOException;
import java.util.List;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PropertiesDefinitionDeserializer extends StdDeserializer<TEntityType.PropertiesDefinition> {

    public PropertiesDefinitionDeserializer() {
        super(TEntityType.PropertiesDefinition.class);
    }

    @Override
    public TEntityType.PropertiesDefinition deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        // read as ObjectNode to enable removing empty properties
        ObjectNode node = parser.getCodec().readTree(parser);
        final JavaType targetType;
        if (node.hasNonNull("propertyDefinitionKVList")) {
            // deserialize as WinerysPropertiesDefinition
            targetType = context.constructType(WinerysPropertiesDefinition.class);
        } else if (node.hasNonNull("element")) {
            targetType = context.constructType(TEntityType.XmlElementDefinition.class);
            // remove unused properties to avoid tripping up the Json Parsing
            node.remove("type");
            node.remove("properties");
        } else if (node.hasNonNull("type")) {
            targetType = context.constructType(TEntityType.XmlTypeDefinition.class);
            // remove unused properties to avoid tripping up the Json Parsing
            node.remove("element");
            node.remove("properties");
        } else if (node.hasNonNull("properties")) {
            targetType = context.constructType(TEntityType.YamlPropertiesDefinition.class);
            // remove unused properties to avoid tripping up the Json Parsing
            node.remove("type");
            node.remove("element");
        } else {
            // this throws an exception
            context.reportMappingException("Could not determine EntityType.PropertiesDefinition implementation from properties", node.fieldNames());
            return null;
        }
        JsonDeserializer<Object> deserializer = context.findNonContextualValueDeserializer(targetType);
        // create a new JsonParser for the delegate deserializer to account for consumed input in original parser.
        JsonParser objectParser = node.traverse();
        // advance the parser by one token because the parser initialized by node.traverse() stays at "before start"
        objectParser.nextToken();
        return (TEntityType.PropertiesDefinition) deserializer.deserialize(objectParser, context);
    }
}
