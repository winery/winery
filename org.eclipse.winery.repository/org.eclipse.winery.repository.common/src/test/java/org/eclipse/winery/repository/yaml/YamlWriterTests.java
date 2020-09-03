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

package org.eclipse.winery.repository.yaml;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.repository.converter.writer.YamlPrinter;
import org.eclipse.winery.repository.converter.writer.YamlWriter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlWriterTests {

    @ParameterizedTest
    @ArgumentsSource(PropertyFunctionArgumentsProvider.class)
    public void testPropertyFunctionSerialization(TPropertyAssignment prop, String expected) {
        YamlWriter writer = new YamlWriter();
        YamlPrinter p = writer.visit(prop, new YamlWriter.Parameter(0).addContext("root"));
        assertEquals(expected, p.toString());
    }

    static class PropertyFunctionArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            // workaround for test #2 to not use an unmodifiable collection
            Map<String, TPropertyAssignment> input = new HashMap<>();
            input.put("key", new TPropertyAssignment("value"));
            return Stream.of(
                Arguments.of(new TPropertyAssignment(), ""),
                Arguments.of(
                    new TPropertyAssignment(input),
                    "root:\n  key: \"value\"\n"),
                Arguments.of(
                    new TPropertyAssignment(Collections.singletonMap("get_input", new TPropertyAssignment("value"))),
                    "root: { get_input: value }\n"),
                Arguments.of(
                    new TPropertyAssignment(Collections.singletonMap("get_input", new TPropertyAssignment(Arrays.asList("hierarchical", "value")))),
                    "root: { get_input: [ hierarchical, value ] }\n"),
                Arguments.of(
                    new TPropertyAssignment(Collections.singletonMap("get_property", new TPropertyAssignment(Arrays.asList("entity_name", "prop_name")))),
                    "root: { get_property: [ entity_name, prop_name ] }\n"),
                Arguments.of(
                    new TPropertyAssignment(Collections.singletonMap("get_attribute", new TPropertyAssignment(Arrays.asList("entity_name", "attribute_name")))),
                    "root: { get_attribute: [ entity_name, attribute_name ] }\n"),
                Arguments.of(
                    new TPropertyAssignment(Collections.singletonMap("get_operation_output", new TPropertyAssignment(Arrays.asList("entity_name", "interface_name", "operation_name", "output_var_name")))),
                    "root: { get_operation_output: entity_name, interface_name, operation_name, output_var_name }\n"),
                Arguments.of(
                    new TPropertyAssignment(Collections.singletonMap("get_nodes_of_type", new TPropertyAssignment("nt_name"))),
                    "root: { get_nodes_of_type: nt_name }\n"),
                Arguments.of(
                    new TPropertyAssignment(Collections.singletonMap("get_artifact", new TPropertyAssignment(Arrays.asList("e_name", "a_name", "loc", false)))),
                    "root: { get_artifact: [ e_name, a_name, loc, false ] }\n")
            );
        }
    }
}
