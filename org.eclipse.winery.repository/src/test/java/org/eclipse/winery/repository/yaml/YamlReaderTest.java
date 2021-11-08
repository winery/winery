/*******************************************************************************
 * Copyright (c) 2020-2021 Contributors to the Eclipse Foundation
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
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.converter.support.exception.MultiException;
import org.eclipse.winery.model.tosca.yaml.YTActivityDefinition;
import org.eclipse.winery.model.tosca.yaml.YTCallOperationActivityDefinition;
import org.eclipse.winery.model.tosca.yaml.YTInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.YTNodeType;
import org.eclipse.winery.model.tosca.yaml.YTPolicyType;
import org.eclipse.winery.model.tosca.yaml.YTServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.YTTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.YTTriggerDefinition;
import org.eclipse.winery.model.tosca.yaml.support.YTMapActivityDefinition;
import org.eclipse.winery.repository.converter.AbstractConverterTest;
import org.eclipse.winery.repository.converter.reader.YamlReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("tests for reading valid Policy Types")
    class ValidPolicyTypeWithTriggerTest {
        private YTServiceTemplate template;

        @BeforeEach
        void beforeEach() throws MultiException {
            YamlReader reader = new YamlReader();
            InputStream is = getClass().getClassLoader().getResourceAsStream("yaml/simple-tests/policy-types/valid-policy-type-with-trigger-1_3.yml");
            this.template = reader.parse(is);
        }

        @Test
        public void testPolicyTypesPresence() {
            Map<String, YTPolicyType> policyTypes = template.getPolicyTypes();
            Assertions.assertNotNull(policyTypes);
            Assertions.assertEquals(1, policyTypes.size());
        }

        @Test
        public void testPolicyTypeContents() {
            YTPolicyType policyType = template.getPolicyTypes().get("my.test.namespace.PolicyTypeName");
            Assertions.assertNotNull(policyType);
            Assertions.assertEquals(3, policyType.getProperties().size());
            Assertions.assertEquals(1, policyType.getTargets().size());
        }

        @Test
        public void testTriggerDefinitionsPresence() {
            Map<String, YTTriggerDefinition> triggerDefinitions = template.getPolicyTypes().get("my.test.namespace.PolicyTypeName").getTriggers();
            Assertions.assertNotNull(triggerDefinitions);
            Assertions.assertEquals(1, triggerDefinitions.size());
        }

        @Test
        public void testTriggerDefinitionContents() {
            YTTriggerDefinition triggerDefinition = template.getPolicyTypes()
                .get("my.test.namespace.PolicyTypeName")
                .getTriggers()
                .get("my.test.namespace.TriggerDefinition");
            Assertions.assertNotNull(triggerDefinition);
            Assertions.assertNotNull(triggerDefinition.getDescription());
            Assertions.assertNotNull(triggerDefinition.getEvent());
        }

        @Test
        public void testTargetFilter() {
            YTTriggerDefinition triggerDefinition = template.getPolicyTypes()
                .get("my.test.namespace.PolicyTypeName")
                .getTriggers()
                .get("my.test.namespace.TriggerDefinition");
            Assertions.assertNotNull(triggerDefinition.getTargetFilter());
            Assertions.assertNotNull(triggerDefinition.getTargetFilter().getNode());
            Assertions.assertNull(triggerDefinition.getTargetFilter().getRequirement());
            Assertions.assertNull(triggerDefinition.getTargetFilter().getCapability());
        }

        @Test
        public void testActivityDefinition() {
            List<YTMapActivityDefinition> actions = template.getPolicyTypes()
                .get("my.test.namespace.PolicyTypeName")
                .getTriggers()
                .get("my.test.namespace.TriggerDefinition")
                .getAction();
            Assertions.assertNotNull(actions);
            Assertions.assertEquals(1, actions.size());
            Assertions.assertEquals(1, actions.get(0).getMap().size());
            YTActivityDefinition activity = actions.get(0).getMap().get("call_operation");
            Assertions.assertNotNull(activity);
            Assertions.assertNotNull(((YTCallOperationActivityDefinition) activity).getOperation());
            Assertions.assertEquals(2, ((YTCallOperationActivityDefinition) activity).getInputs().size());
        }
    }

    @Nested
    @DisplayName("tests for reading invalid Policy Types")
    class InvalidPolicyTypeWithTriggerTest {
        private YTServiceTemplate template;
        private YTServiceTemplate templateTwo;

        @BeforeEach
        void beforeEach() throws MultiException {
            YamlReader reader = new YamlReader();
            InputStream is = getClass().getClassLoader().getResourceAsStream("yaml/simple-tests/policy-types/invalid/invalid-policy-type-with-malformed-trigger-1_3.yml");
            this.template = reader.parse(is);

            InputStream is2 = getClass().getClassLoader().getResourceAsStream("yaml/simple-tests/policy-types/invalid/invalid-policy-type-with-malformed-activities-1_3.yml");
            this.templateTwo = reader.parse(is2);
        }

        @Test
        public void testPolicyTypesPresence() {
            YamlReader reader = new YamlReader();
            InputStream is = getClass().getClassLoader().getResourceAsStream("yaml/simple-tests/policy-types/invalid/invalid-policy-type-with-malformed-trigger-1_3.yml");
            Map<String, YTPolicyType> policyTypes = template.getPolicyTypes();
            Assertions.assertNotNull(policyTypes);
            Assertions.assertEquals(1, policyTypes.size());
        }

        @Test
        public void testTriggersWithoutEventIsIgnored() {
            YTPolicyType policyType = template.getPolicyTypes().get("my.test.namespace.PolicyTypeName");
            Assertions.assertNotNull(policyType);
            Assertions.assertEquals(0, policyType.getProperties().size());
            Assertions.assertEquals(1, policyType.getTargets().size());
            Assertions.assertEquals(1, policyType.getTriggers().size());
        }

        @Test
        public void testUnsupportedActivityIsIgnored() {
            YTPolicyType policyType = templateTwo.getPolicyTypes().get("my.test.namespace.PolicyTypeName");
            Assertions.assertNotNull(policyType);
            Assertions.assertEquals(2, policyType.getTriggers().get("my.test.namespace.TriggerDefinition2").getAction().size());
        }
    }
}
