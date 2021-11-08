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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.YTCallOperationActivityDefinition;
import org.eclipse.winery.model.tosca.yaml.YTConstraintClause;
import org.eclipse.winery.model.tosca.yaml.YTEventFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.YTImportDefinition;
import org.eclipse.winery.model.tosca.yaml.YTNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.YTParameterDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPolicyType;
import org.eclipse.winery.model.tosca.yaml.YTPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.YTPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.YTServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.YTTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.YTTriggerDefinition;
import org.eclipse.winery.model.tosca.yaml.support.Defaults;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.YTMapActivityDefinition;
import org.eclipse.winery.repository.converter.writer.YamlPrinter;
import org.eclipse.winery.repository.converter.writer.YamlWriter;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlWriterTests {

    private static String inputStreamToString(InputStream is) throws Exception {
        return IOUtils.toString(is, StandardCharsets.UTF_8);
    }

    @ParameterizedTest
    @ArgumentsSource(ServiceTemplatesProvider.class)
    public void testServiceTemplates(YTServiceTemplate template, String expected) {
        YamlWriter writer = new YamlWriter();
        YamlPrinter p = writer.visit(template, new YamlWriter.Parameter(0));
        assertEquals(expected, p.toString());
    }

    @ParameterizedTest
    @ArgumentsSource(ServiceTmpltTopologyTmpltProvider.class)
    public void testTopologyTemplateContent(YTServiceTemplate serv, String expected) {
        YamlWriter writer = new YamlWriter();
        YamlPrinter p = writer.visit(serv, new YamlWriter.Parameter(0).addContext("root"));
        assertEquals(expected, p.toString());
    }

    @ParameterizedTest
    @ArgumentsSource(ImportArgumentsProvider.class)
    public void testImports(YTImportDefinition importDef, String expected) {
        YamlWriter writer = new YamlWriter();
        YamlPrinter p = writer.visit(importDef, new YamlWriter.Parameter(0).addContext("root"));
        assertEquals(expected, p.toString());
    }

    @ParameterizedTest
    @ArgumentsSource(ConstraintClausesArgumentsProvider.class)
    public void testConstraintClauses(YTConstraintClause constraint, String expected) {
        YamlWriter writer = new YamlWriter();
        YamlPrinter p = writer.visit(constraint, new YamlWriter.Parameter(0).addContext("root"));
        assertEquals(expected, p.toString());
    }

    @ParameterizedTest
    @ArgumentsSource(PropertyAssignmentArgumentsProvider.class)
    public void testPropertyAssignmentSerialization(YTPropertyAssignment prop, String expected) {
        YamlWriter writer = new YamlWriter();
        YamlPrinter p = writer.visit(prop, new YamlWriter.Parameter(0).addContext("root"));
        assertEquals(expected, p.toString());
    }

    @ParameterizedTest
    @ArgumentsSource(PropertyFunctionArgumentsProvider.class)
    public void testPropertyFunctionSerialization(YTPropertyAssignment prop, String expected) {
        YamlWriter writer = new YamlWriter();
        YamlPrinter p = writer.visit(prop, new YamlWriter.Parameter(0).addContext("root"));
        assertEquals(expected, p.toString());
    }

    @ParameterizedTest
    @ArgumentsSource(PolicyTypeArgumentsProvider.class)
    public void testPolicyTypeSerialization(YTServiceTemplate prop, String expected) {
        YamlWriter writer = new YamlWriter();
        YamlPrinter p = writer.visit(prop, new YamlWriter.Parameter(0).addContext("root"));
        assertEquals(expected, p.toString());
    }

    static class ServiceTemplatesProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            YTServiceTemplate stWithImports = new YTServiceTemplate.Builder(Defaults.TOSCA_DEFINITIONS_VERSION)
                .addImports("S3Bucket", new YTImportDefinition.Builder("radonnodesaws__AwsS3Bucket.tosca")
                    .setNamespacePrefix("radonnodesaws")
                    .setNamespaceUri("radon.nodes.aws")
                    .build())
                .addImports("Lambda", new YTImportDefinition.Builder("radonnodesaws__AwsLambdaFunctionFromS3.tosca")
                    .setNamespacePrefix("radonnodesaws")
                    .setNamespaceUri("radon.nodes.aws")
                    .build())
                .build();
            return Stream.of(
                Arguments.of(stWithImports, "tosca_definitions_version: tosca_simple_yaml_1_3\n\nimports:\n  - file: radonnodesaws__AwsS3Bucket.tosca\n    namespace_uri: radon.nodes.aws\n    namespace_prefix: radonnodesaws\n  - file: radonnodesaws__AwsLambdaFunctionFromS3.tosca\n    namespace_uri: radon.nodes.aws\n    namespace_prefix: radonnodesaws\n")
            );
        }
    }

    static class ServiceTmpltTopologyTmpltProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            InputStream s = getClass().getClassLoader().getResourceAsStream("yaml/simple-tests/valid-topology_templates-1_3.yml");
            Map<String, YTPolicyDefinition> polTemplate = new LinkedHashMap<>();
            YTPolicyDefinition pol1 = new YTPolicyDefinition.Builder(
                new QName("plt1")).setTargets(Collections.singletonList(new QName("ndt1"))).build();
            YTPolicyDefinition pol2 = new YTPolicyDefinition.Builder(
                new QName("plt2")).setTargets(Collections.singletonList(new QName("ndt1"))).build();
            polTemplate.put("plc1", pol1);
            polTemplate.put("plc2", pol2);
            Map<String, YTRelationshipTemplate> relTemplate = new LinkedHashMap<>();
            YTRelationshipTemplate rel1 = new YTRelationshipTemplate.Builder(new QName("rlt1")).build();
            relTemplate.put("rltp1", rel1);
            Map<String, YTNodeTemplate> nodTemplate = new LinkedHashMap<>();
            YTNodeTemplate nod1 = new YTNodeTemplate.Builder(new QName("tosca.nodes.Database")).build();
            nodTemplate.put("ndt1", nod1);
            YTTopologyTemplateDefinition topo = new YTTopologyTemplateDefinition.Builder()
                .setPolicies(polTemplate)
                .setRelationshipTemplates(relTemplate)
                .setNodeTemplates(nodTemplate).build();
            YTServiceTemplate base = new YTServiceTemplate.Builder(Defaults.TOSCA_DEFINITIONS_VERSION)
                .setTopologyTemplate(topo).build();
            return Stream.of(
                Arguments.of(base, inputStreamToString(s))
            );
        }
    }

    static class ImportArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            YTImportDefinition base = new YTImportDefinition.Builder("radonnodes__AwsS3Bucket.tosca")
                .setNamespacePrefix("radonnodesaws")
                .setNamespaceUri("radon.nodes.aws")
                .build();
            return Stream.of(
                Arguments.of(base, "file: radonnodes__AwsS3Bucket.tosca\nnamespace_uri: radon.nodes.aws\nnamespace_prefix: radonnodesaws\n")
            );
        }
    }

    static class ConstraintClausesArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            final YTConstraintClause.Builder simpleClause = new YTConstraintClause.Builder();
            simpleClause.setKey("in_range");
            simpleClause.setList(Arrays.asList("512", "2048"));
            final YTConstraintClause.Builder valueClause = new YTConstraintClause.Builder();
            valueClause.setKey("key");
            valueClause.setValue("value");
            return Stream.of(
                Arguments.of(simpleClause.build(), "in_range: [ 512, 2048 ]\n"),
                Arguments.of(valueClause.build(), "key: value\n")
            );
        }
    }

    static class PropertyAssignmentArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            final YTPropertyAssignment baseList = new YTPropertyAssignment.Builder().setValue(Stream.of("a1", "a2")
                    .map(v -> new YTPropertyAssignment.Builder().setValue(v).build())
                    .collect(Collectors.toList()))
                .build();
            final Map<String, YTPropertyAssignment> nestedMap = new HashMap<>();
            nestedMap.put("pre_activities", baseList);
            nestedMap.put("post_activities", baseList);
            nestedMap.put("type", new YTPropertyAssignment.Builder().setValue("sequence").build());
            final List<YTPropertyAssignment> multipleMaps = new ArrayList<>();
            multipleMaps.add(new YTPropertyAssignment.Builder().setValue(nestedMap).build());
            multipleMaps.add(new YTPropertyAssignment.Builder().setValue(nestedMap).build());
            return Stream.of(
                Arguments.of(new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("key", new YTPropertyAssignment.Builder().setValue("value").build())).build(), "root:\n  key: \"value\"\n"),
                Arguments.of(new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("key", new YTPropertyAssignment.Builder().setValue(null).build())).build(), "root:\n  key: null\n"),
                Arguments.of(new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("key", new YTPropertyAssignment.Builder().setValue("").build())).build(), "root:\n"),
                Arguments.of(new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("key", new YTPropertyAssignment.Builder().setValue(Collections.emptyMap()).build())).build(), "root:\n  key: {}\n"),
                Arguments.of(new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("key", new YTPropertyAssignment.Builder().setValue(Collections.emptyList()).build())).build(), "root:\n  key: []\n"),
                Arguments.of(baseList, "root:\n  - \"a1\"\n  - \"a2\"\n"),
                Arguments.of(new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("entries", baseList)).build(), "root:\n  entries:\n    - \"a1\"\n    - \"a2\"\n"),
                Arguments.of(new YTPropertyAssignment.Builder().setValue(multipleMaps).build(), "root:\n  - post_activities:\n      - \"a1\"\n      - \"a2\"\n    pre_activities:\n      - \"a1\"\n      - \"a2\"\n    type: \"sequence\"\n  - post_activities:\n      - \"a1\"\n      - \"a2\"\n    pre_activities:\n      - \"a1\"\n      - \"a2\"\n    type: \"sequence\"\n")
            );
        }
    }

    static class PropertyFunctionArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(
                    new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("get_input", new YTPropertyAssignment.Builder().setValue("value").build())).build(),
                    "root: { get_input: value }\n"),
                Arguments.of(
                    new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("get_input", new YTPropertyAssignment.Builder().setValue(Arrays.asList("hierarchical", "value")).build())).build(),
                    "root: { get_input: [ hierarchical, value ] }\n"),
                Arguments.of(
                    new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("get_property", new YTPropertyAssignment.Builder().setValue(Arrays.asList("entity_name", "prop_name")).build())).build(),
                    "root: { get_property: [ entity_name, prop_name ] }\n"),
                Arguments.of(
                    new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("get_attribute", new YTPropertyAssignment.Builder().setValue(Arrays.asList("entity_name", "attribute_name")).build())).build(),
                    "root: { get_attribute: [ entity_name, attribute_name ] }\n"),
                Arguments.of(
                    new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("get_operation_output", new YTPropertyAssignment.Builder().setValue(Arrays.asList("entity_name", "interface_name", "operation_name", "output_var_name")).build())).build(),
                    "root: { get_operation_output: entity_name, interface_name, operation_name, output_var_name }\n"),
                Arguments.of(
                    new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("get_nodes_of_type", new YTPropertyAssignment.Builder().setValue("nt_name").build())).build(),
                    "root: { get_nodes_of_type: nt_name }\n"),
                Arguments.of(
                    new YTPropertyAssignment.Builder().setValue(Collections.singletonMap("get_artifact", new YTPropertyAssignment.Builder().setValue(Arrays.asList("e_name", "a_name", "loc", false)).build())).build(),
                    "root: { get_artifact: [ e_name, a_name, loc, false ] }\n")
            );
        }
    }

    static class PolicyTypeArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            YTServiceTemplate.Builder st = new YTServiceTemplate.Builder(Defaults.TOSCA_DEFINITIONS_VERSION);

            final YTPolicyType noEmptyElementsAfterGetTriggersMethod = getValidYTPolicyTypeBuilder().build();
            noEmptyElementsAfterGetTriggersMethod.getTriggers();

            YTTriggerDefinition triggerWithoutEvent = new YTTriggerDefinition.Builder(null).setDescription("description").build();
            final YTPolicyType policyTypeWithNonSerializableTrigger = getValidYTPolicyTypeBuilder().build();
            policyTypeWithNonSerializableTrigger.setTriggers(Collections.singletonMap("my.test.namespace.TriggerDefinition", triggerWithoutEvent));

            final YTPolicyType policyTypeWithValidTrigger = getValidYTPolicyTypeBuilder().build();
            policyTypeWithValidTrigger.setTriggers(Collections.singletonMap("my.test.namespace.TriggerDefinition", getValidTriggerDefinitionBuilder(true).build()));

            StringBuilder expectedValidTriggerWithInput = getValidYTPolicTypeWithTriggerAndNoInputsStringBuilder();
            expectedValidTriggerWithInput.append("              inputs:").append("\n");
            expectedValidTriggerWithInput.append("                inputOne: { get_property: [ SELF, propertyOne ] }").append("\n");

            final YTPolicyType policyTypeWithValidTriggerWithoutInput = getValidYTPolicyTypeBuilder().build();
            policyTypeWithValidTriggerWithoutInput.setTriggers(Collections.singletonMap("my.test.namespace.TriggerDefinition", getValidTriggerDefinitionBuilder(false).build()));

            return Stream.of(
                Arguments.of(st.setPolicyTypes(Collections.singletonMap("my.test.namespace.PolicyTypeName", getValidYTPolicyTypeBuilder().build())).build(), getValidYTPolicTypeStringBuilder().toString()),
                Arguments.of(st.setPolicyTypes(Collections.singletonMap("my.test.namespace.PolicyTypeName", noEmptyElementsAfterGetTriggersMethod)).build(), getValidYTPolicTypeStringBuilder().toString()),
                Arguments.of(st.setPolicyTypes(Collections.singletonMap("my.test.namespace.PolicyTypeName", policyTypeWithNonSerializableTrigger)).build(), getValidYTPolicTypeStringBuilder().toString()),
                Arguments.of(st.setPolicyTypes(Collections.singletonMap("my.test.namespace.PolicyTypeName", policyTypeWithValidTrigger)).build(), expectedValidTriggerWithInput.toString()),
                Arguments.of(st.setPolicyTypes(Collections.singletonMap("my.test.namespace.PolicyTypeName", policyTypeWithValidTriggerWithoutInput)).build(), getValidYTPolicTypeWithTriggerAndNoInputsStringBuilder().toString())
            );
        }

        private YTPolicyType.Builder getValidYTPolicyTypeBuilder() {
            final YTPolicyType.Builder policyTypeWithoutTriggers = new YTPolicyType.Builder();
            policyTypeWithoutTriggers.setDerivedFrom(new QName("tosca.policies.Root"));
            Metadata metadata = new Metadata();
            metadata.put("targetNamespace", "my.test.namespace");
            metadata.put("abstract", "false");
            metadata.put("final", "false");
            policyTypeWithoutTriggers.setMetadata(metadata);
            policyTypeWithoutTriggers.setProperties(Collections.singletonMap("propertyOne", new YTPropertyDefinition.Builder(new QName("string")).build()));
            policyTypeWithoutTriggers.setTargets(Collections.singletonList(new QName("my.test.namespace.TargetNodeTypeName")));

            return policyTypeWithoutTriggers;
        }

        private YTTriggerDefinition.Builder getValidTriggerDefinitionBuilder(boolean withInput) {
            YTTriggerDefinition.Builder correctTriggerBuilder = new YTTriggerDefinition.Builder("eventName")
                .setDescription("description")
                .setTargetFilter(new YTEventFilterDefinition.Builder("my.test.namespace.TargetNodeTypeName").build());

            YTMapActivityDefinition action = new YTMapActivityDefinition();
            YTCallOperationActivityDefinition correctCallActivityDefinition = new YTCallOperationActivityDefinition("interface.operation");
            if (withInput) {
                correctCallActivityDefinition.setInputs(Collections.singletonMap("inputOne", new YTParameterDefinition.Builder().setValue("{ get_property: [ SELF, propertyOne ] }").build()));
            } else {
                correctCallActivityDefinition.setInputs(null);
            }
            action.put("call_operation", correctCallActivityDefinition);
            correctTriggerBuilder.setAction(Collections.singletonList(action));

            return correctTriggerBuilder;
        }

        private StringBuilder getValidYTPolicTypeStringBuilder() {
            return new StringBuilder()
                .append("tosca_definitions_version: tosca_simple_yaml_1_3").append("\n").append("\n")
                .append("policy_types:").append("\n")
                .append("  my.test.namespace.PolicyTypeName:").append("\n")
                .append("    derived_from: tosca.policies.Root").append("\n")
                .append("    metadata:").append("\n")
                .append("      targetNamespace: \"my.test.namespace\"").append("\n")
                .append("      abstract: \"false\"").append("\n")
                .append("      final: \"false\"").append("\n")
                .append("    properties:").append("\n")
                .append("      propertyOne:").append("\n")
                .append("        type: string").append("\n")
                .append("    targets: [ my.test.namespace.TargetNodeTypeName ]").append("\n");
        }

        private StringBuilder getValidYTPolicTypeWithTriggerAndNoInputsStringBuilder() {
            StringBuilder expectedValidTrigger = getValidYTPolicTypeStringBuilder();
            expectedValidTrigger.append("    triggers:").append("\n");
            expectedValidTrigger.append("      my.test.namespace.TriggerDefinition:").append("\n");
            expectedValidTrigger.append("        description: description").append("\n");
            expectedValidTrigger.append("        event: eventName").append("\n");
            expectedValidTrigger.append("        target_filter:").append("\n");
            expectedValidTrigger.append("          node: my.test.namespace.TargetNodeTypeName").append("\n");
            expectedValidTrigger.append("        action:").append("\n");
            expectedValidTrigger.append("          - call_operation:").append("\n");
            expectedValidTrigger.append("              operation: interface.operation").append("\n");

            return expectedValidTrigger;
        }
    }
}
