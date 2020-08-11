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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class YesNo {

    public static class Serializer extends StdSerializer<Boolean> {
        public Serializer() { this(null); }
        protected Serializer(Class<Boolean> t) {
            super(t);
        }

        @Override
        public void serialize (Boolean aBoolean, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws
        IOException {
            jsonGenerator.writeString(aBoolean ? "YES" : "NO");
        }
    }

    public static class Deserializer extends StdDeserializer<Boolean> {
        public Deserializer() { this(null); }
        protected Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Boolean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return jsonParser.getText().equalsIgnoreCase("YES");
        }
    }
}
