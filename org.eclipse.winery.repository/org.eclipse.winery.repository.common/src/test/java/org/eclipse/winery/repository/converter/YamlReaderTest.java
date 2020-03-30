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
package org.eclipse.winery.repository.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.converter.reader.YamlReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class YamlReaderTest extends AbstractConverterTest {

    @BeforeAll
    private static void init() {
        path = Paths.get("src/test/resources/yaml/simple-tests");
    }

    @DisplayName("Simple YAML Reader Test")
    @ParameterizedTest
    @MethodSource("getYamlFiles")
    public void testBuilder(Path filename) throws Exception {
        YamlReader reader = new YamlReader();
        InputStream is = new FileInputStream(new File(path.toFile(), filename.toString()));
        Assertions.assertNotNull(reader.parse(is));
    }

    @Test
    public void testSupportedInterfaceDefinitions() throws Exception {
        YamlReader reader = new YamlReader();
        Path file = getYamlFile("src/test/resources/yaml/supported_interfaces");
        InputStream is = new FileInputStream(file.toFile());
        TServiceTemplate template = reader.parse(is);
        Assertions.assertNotNull(template);
        TNodeType server = template.getNodeTypes().get("server");
        Assertions.assertEquals(2, server.getArtifacts().size());
        TInterfaceDefinition standard = server.getInterfaces().get("Standard");
        Assertions.assertEquals(2, standard.getOperations().size());
        Assertions.assertEquals(1, standard.getInputs().size());
    }
}
