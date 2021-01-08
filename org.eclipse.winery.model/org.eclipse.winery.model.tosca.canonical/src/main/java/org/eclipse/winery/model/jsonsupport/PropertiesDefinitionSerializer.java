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

import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PropertiesDefinitionSerializer extends StdSerializer<TEntityType.PropertiesDefinition> {
    public PropertiesDefinitionSerializer() {
        super(TEntityType.PropertiesDefinition.class);
    }

    @Override
    public void serialize(TEntityType.PropertiesDefinition propertiesDefinition, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if (propertiesDefinition instanceof WinerysPropertiesDefinition) {
            WinerysPropertiesDefinition wpd = (WinerysPropertiesDefinition) propertiesDefinition;
            jsonGenerator.writeStringField("namespace", wpd.getNamespace());
            jsonGenerator.writeStringField("elementName", wpd.getElementName());
            jsonGenerator.writeArrayFieldStart("propertyDefinitionKVList");
            JsonSerializer<Object> defSerializer = serializerProvider.findValueSerializer(PropertyDefinitionKV.class);
            for (PropertyDefinitionKV propDef : wpd.getPropertyDefinitions()) {
                defSerializer.serialize(propDef, jsonGenerator, serializerProvider);
            }
            jsonGenerator.writeEndArray();
            if (wpd.getIsDerivedFromXSD() != null && wpd.getIsDerivedFromXSD()) {
                jsonGenerator.writeBooleanField("isDerivedFromXSD", wpd.getIsDerivedFromXSD());
            }
        } else if (propertiesDefinition instanceof TEntityType.XmlElementDefinition) {
            jsonGenerator.writeStringField("element", ((TEntityType.XmlElementDefinition) propertiesDefinition).getElement().toString());
        } else if (propertiesDefinition instanceof TEntityType.XmlTypeDefinition) {
            jsonGenerator.writeStringField("type", ((TEntityType.XmlTypeDefinition) propertiesDefinition).getType().toString());
        } else if (propertiesDefinition instanceof TEntityType.YamlPropertiesDefinition) {
            jsonGenerator.writeArrayFieldStart("properties");
            JsonSerializer<Object> yamlDefSerializer = serializerProvider.findValueSerializer(TEntityType.YamlPropertyDefinition.class);
            for (TEntityType.YamlPropertyDefinition def : ((TEntityType.YamlPropertiesDefinition) propertiesDefinition).getProperties()) {
                yamlDefSerializer.serialize(def, jsonGenerator, serializerProvider);
            }
            jsonGenerator.writeEndArray();
        } else {
            // this basically throws an exception
            serializerProvider.reportBadDefinition(TEntityType.PropertiesDefinition.class, "Unknown subtype of PropertiesDefinition passed for serialization.");
        }
        jsonGenerator.writeEndObject();
    }
}
