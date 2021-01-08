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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.winery.model.tosca.yaml.YTInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.YTNodeType;
import org.eclipse.winery.model.tosca.yaml.YTServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.YTTopologyTemplateDefinition;
import org.eclipse.winery.repository.converter.AbstractConverterTest;
import org.eclipse.winery.repository.converter.reader.YamlReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class YamlReaderTest extends AbstractConverterTest {

    @BeforeAll
    private static void init() throws URISyntaxException {
        path = Paths.get(YamlReaderTest.class.getClassLoader().getResource("yaml/simple-tests").toURI());
    }

    @DisplayName("Simple YAML Reader Test")
    @ParameterizedTest
    @MethodSource("getYamlFiles")
    public void testBuilder(Path filename) throws Exception {
        YamlReader reader = new YamlReader();
        InputStream is = new FileInputStream(new File(path.toFile(), filename.toString()));
        Assertions.assertNotNull(reader.parse(is));
    }

    @DisplayName("Basic Topology Template Reader Test")
    @Test
    public void testBasicTopologyTemplate() throws Exception {
        YamlReader reader = new YamlReader();
        InputStream is = getClass().getClassLoader().getResourceAsStream("yaml/simple-tests/valid-topology_templates-1_3.yml");
        YTServiceTemplate serviceTemplate = reader.parse(is);
        Assertions.assertNotNull(serviceTemplate);
        YTTopologyTemplateDefinition topoTemplate = serviceTemplate.getTopologyTemplate();
        Assertions.assertNotNull(topoTemplate);
        Assertions.assertEquals(topoTemplate.getPolicies().size(), 2);
        Assertions.assertEquals(topoTemplate.getNodeTemplates().size(), 1);
        Assertions.assertEquals(topoTemplate.getRelationshipTemplates().size(), 1);
    }

    @Test
    public void testSupportedInterfaceDefinitions() throws Exception {
        YamlReader reader = new YamlReader();
        InputStream is = getClass().getClassLoader().getResourceAsStream("yaml/supported_interfaces.yml");
        YTServiceTemplate template = reader.parse(is);
        Assertions.assertNotNull(template);
        YTNodeType server = template.getNodeTypes().get("server");
        Assertions.assertEquals(2, server.getArtifacts().size());
        YTInterfaceDefinition standard = server.getInterfaces().get("Standard");
        Assertions.assertEquals(2, standard.getOperations().size());
        Assertions.assertEquals(1, standard.getInputs().size());
    }

    @Test
    public void testPropertyFunctionReading() throws Exception {
        YamlReader reader = new YamlReader();
        InputStream is = getClass().getClassLoader().getResourceAsStream("yaml/property_functions.yml");
        YTServiceTemplate template = reader.parse(is);
        Assertions.assertNotNull(template);
    }

    @Test
    public void testPolicyDefinitionsAsMap() throws Exception {
        YamlReader reader = new YamlReader();
        InputStream is = getClass().getClassLoader().getResourceAsStream("yaml/simple-tests/wrong-policy-map-in-tt.yml");
        YTServiceTemplate template = reader.parse(is);
        Assertions.assertNotNull(template);
        YTTopologyTemplateDefinition topologyTemplate = template.getTopologyTemplate();
        Assertions.assertNotNull(topologyTemplate);
        Assertions.assertEquals(0, topologyTemplate.getPolicies().size());
    }

    @Test
    public void testPolicyDefinitionsAsList() throws Exception {
        YamlReader reader = new YamlReader();
        InputStream is = getClass().getClassLoader().getResourceAsStream("yaml/simple-tests/valid-topology_templates-1_3.yml");
        YTServiceTemplate template = reader.parse(is);
        Assertions.assertNotNull(template);
        YTTopologyTemplateDefinition topologyTemplate = template.getTopologyTemplate();
        Assertions.assertNotNull(topologyTemplate);
        Assertions.assertEquals(2, topologyTemplate.getPolicies().size());
    }
}
