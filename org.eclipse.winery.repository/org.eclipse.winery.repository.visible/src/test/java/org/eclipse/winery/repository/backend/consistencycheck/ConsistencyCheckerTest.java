/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend.consistencycheck;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConsistencyCheckerTest extends TestWithGitBackedRepository {

    private ConsistencyChecker consistencyChecker;

    @BeforeEach
    public void initializeConsistencyChecker() {
        ConsistencyCheckerConfiguration consistencyCheckerConfiguration = new ConsistencyCheckerConfiguration(false, true, EnumSet.of(ConsistencyCheckerVerbosity.NONE), repository);
        consistencyChecker = new ConsistencyChecker(consistencyCheckerConfiguration);
    }

    @Test
    public void nodeTypeImplementationNamespaceHasNoErrors() throws Exception {
        NodeTypeImplementationId id = new NodeTypeImplementationId("http://winery.opentosca.org/test/nodetypeimplementations/fruits", "baobab_impl", false);
        ConsistencyErrorCollector errorLogger = new ConsistencyErrorCollector();
        consistencyChecker.checkNamespaceUri(id);
        assertEquals(Collections.emptyMap(), errorLogger.getErrorList());
    }

    @Test
    public void openToscaNamespaceIsNotLowerCaseErrorRaisedAtWrongNodeTypeImplementationNamespace() {
        QName qname = new QName("http://www.opentosca.org/NodeTypeImplementations/fruits", "baobab_impl");
        NodeTypeImplementationId id = new NodeTypeImplementationId(qname);
        consistencyChecker.checkNamespaceUri(id);

        Map<QName, ElementErrorList> expected = new HashMap<>();
        ElementErrorList elementErrorList;

        elementErrorList = new ElementErrorList("NodeTypeImplementation");
        elementErrorList.addError("opentosca URI is not lowercase");
        expected.put(qname, elementErrorList);

        assertEquals(expected, consistencyChecker.getErrorCollector().getErrorList());
    }

    private static Stream<Arguments> getCamelCasedOpenToscaQNames() {
        return Stream.of(
            Arguments.of(
                new QName("http://www.opentosca.org/nodetypes/fruits/Baobab", "Baobab")
            ),
            Arguments.of(
                new QName("http://www.opentosca.org/providers/LargeStallProvider", "LargeStallProvider")
            ),
            Arguments.of(
                new QName("http://www.opentosca.org/providers/LargeStallProvider", "LargeStallProvider_-w1-wip1")
            )
        );
    }

    @ParameterizedTest()
    @MethodSource("getCamelCasedOpenToscaQNames")
    public void openToscaNamespaceIsNotLowerCaseErrorNotRaisedAtQNameHavingCasedLocalName(QName qname) {
        NodeTypeId id = new NodeTypeId(qname);
        ConsistencyErrorCollector errorLogger = new ConsistencyErrorCollector();
        consistencyChecker.checkNamespaceUri(id);
        assertEquals(Collections.emptyMap(), errorLogger.getErrorList());
    }

    public ConsistencyErrorCollector checkRevisionWithoutDocumentation(String revision) throws Exception {
        this.setRevisionTo(revision);
        EnumSet<ConsistencyCheckerVerbosity> verbosity = EnumSet.noneOf(ConsistencyCheckerVerbosity.class);
        ConsistencyCheckerConfiguration configuration = new ConsistencyCheckerConfiguration
            (false, false, verbosity, repository, true);
        final ConsistencyChecker consistencyChecker = new ConsistencyChecker(configuration);
        consistencyChecker.checkCorruption();
        return consistencyChecker.getErrorCollector();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "83714d54cefe30792a5ad181af7cf036a77baf9e" // origin/plain in a working version
    })
    public void noErrors(String revision) throws Exception {
        final ConsistencyErrorCollector errorLogger = checkRevisionWithoutDocumentation(revision);
        assertNotNull(errorLogger);
        assertEquals(Collections.emptyMap(), errorLogger.getErrorList());
    }

    private static Map<QName, ElementErrorList> getErrorsFor20f6d0afd4395ab83f059cb5fabbb08218c9fcbd() {
        Map<QName, ElementErrorList> expected = new HashMap<>();
        ElementErrorList elementErrorList;

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.2.4.b: The content of element 'Properties' is not complete. One of '{WC[##other:\"http://docs.oasis-open.org/tosca/ns/2011/12\"]}' is expected.\n");
        expected.put(new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithAllReqCapVariants"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Referenced element \"RelationshipTypeWithoutProperties\" is not a full QName");
        elementErrorList.addError("Corrupt: Component instance RelationshipType RelationshipTypeWithoutProperties in namespace  does not exist.");
        expected.put(new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithTwoNodeTemplates_w2-wip1"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Referenced element \"RelationshipTypeWithoutProperties\" is not a full QName");
        elementErrorList.addError("Corrupt: Component instance RelationshipType RelationshipTypeWithoutProperties in namespace  does not exist.");
        expected.put(new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithTwoNodeTemplates_w2-wip2"), elementErrorList);

        elementErrorList = new ElementErrorList("ArtifactTemplate");
        elementErrorList.addError("Namespace URI contains tosca definitions name from other type. E.g., Namespace is ...servicetemplates..., but the type is an artifact template");
        expected.put(new QName("http://plain.winery.opentosca.org/nodetypeimplementations", "NodeTypeWithImplementation-Implementation-DeploymentArtifact"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.3.2.2: Attribute 'x' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'y' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'location' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.4: Attribute 'artifactType' must appear on element 'DeploymentArtifact'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'x' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'y' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'location' is not allowed to appear in element 'NodeTemplate'.\n");
        elementErrorList.addError("type is null");
        elementErrorList.addError("Referenced element \"RelationshlpTypeWithValidSourceAndTarget_w1-wip1\" is not a full QName");
        elementErrorList.addError("propertiesKV of node template NodeTypeWithTwoKVProperties is null");
        elementErrorList.addError("propertiesKV of node template NodeTypeWithTwoKVProperties is null");
        elementErrorList.addError("propertiesKV of node template NodeTypeWithTwoKVProperties_2 is null");
        elementErrorList.addError("propertiesKV of node template NodeTypeWithTwoKVProperties_2 is null");
//        elementErrorList.addError("java.lang.NullPointerException");
        elementErrorList.addError("Corrupt: Component instance RelationshipType RelationshlpTypeWithValidSourceAndTarget_w1-wip1 in namespace  does not exist.");
        expected.put(new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithTwoNodeTemplates_w1-wip4"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.2.4.b: The content of element 'ServiceTemplate' is not complete. One of '{\"http://docs.oasis-open.org/tosca/ns/2011/12\":documentation, WC[##other:\"http://docs.oasis-open.org/tosca/ns/2011/12\"], \"http://docs.oasis-open.org/tosca/ns/2011/12\":Tags, \"http://docs.oasis-open.org/tosca/ns/2011/12\":BoundaryDefinitions, \"http://docs.oasis-open.org/tosca/ns/2011/12\":TopologyTemplate}' is expected.\n");
        expected.put(new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithOneFileInSelfServicePortal"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.3.2.2: Attribute 'x' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'y' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'location' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'x' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'y' is not allowed to appear in element 'NodeTemplate'.\n" +
            "Fatal Error: cvc-complex-type.3.2.2: Attribute 'location' is not allowed to appear in element 'NodeTemplate'.\n");
        elementErrorList.addError("Referenced element \"RelationshipTypeWithoutProperties\" is not a full QName");
        elementErrorList.addError("propertiesKV of node template NodeTypeWithTwoKVProperties is null");
        elementErrorList.addError("propertiesKV of node template NodeTypeWithTwoKVProperties is null");
        elementErrorList.addError("Corrupt: Component instance RelationshipType RelationshipTypeWithoutProperties in namespace  does not exist.");
        expected.put(new QName("http://plain.winery.opentosca.org/servicetemplates", "ServiceTemplateWithTwoNodeTemplates_w1-wip3"), elementErrorList);

        return expected;
    }

    private static Map<QName, ElementErrorList> getErrorsFor0750656975fec2d8a7c8400df85e37b1f2cdb5ac() {
        Map<QName, ElementErrorList> expected = new HashMap<>();
        ElementErrorList elementErrorList;

        elementErrorList = new ElementErrorList("ArtifactTemplate");
        elementErrorList.addError("Namespace URI contains tosca definitions name from other type. E.g., Namespace is ...servicetemplates..., but the type is an artifact template");
        elementErrorList.addError("Properties required, but no properties defined");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DressageEquipment_Unicorn"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-datatype-valid.1.2.1: '2' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '2' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '3' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '3' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '4' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '4' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '5' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '5' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '1' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '1' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '6' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '6' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '7' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '7' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n");
        expected.put(new QName("http://www.winery.opentosca.org/test/targetallocation/servicetemplates", "TargetAllocationMinHostsTest4"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("propertiesKV of node template shetland_pony is null");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/injector", "FoodandHouseInjectionTest"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("propertiesKV of node template shetland_pony is null");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/injector", "FoodandHouseInjectionTest-matched"), elementErrorList);

        elementErrorList = new ElementErrorList("ArtifactTemplate");
        elementErrorList.addError("Namespace URI contains tosca definitions name from other type. E.g., Namespace is ...servicetemplates..., but the type is an artifact template");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "WesternEquipment_Pony"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("propertiesKV of node template shetland_pony is null");
        elementErrorList.addError("propertiesKV of node template ponycompetition is null");
        elementErrorList.addError("propertiesKV of node template ponycompetition_2 is null");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("propertiesKV of node template shetland_pony is null");
        elementErrorList.addError("propertiesKV of node template straw is null");
        elementErrorList.addError("propertiesKV of node template ponycompetition is null");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/driverinjection", "DriverInjectionTest"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.4: Attribute 'id' must appear on element 'tosca:ServiceTemplate'.\n");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/splittingservicetemplate", "SplittingServiceTemplateTest-0"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.4: Attribute 'id' must appear on element 'tosca:ServiceTemplate'.\n");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/splittingservicetemplate", "SplittingServiceTemplateTest-1"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.4: Attribute 'id' must appear on element 'tosca:ServiceTemplate'.\n");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/splittingservicetemplate", "SplittingServiceTemplateTest-2"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.2.4.b: The content of element 'tosca:ServiceTemplate' is not complete. One of '{\"http://docs.oasis-open.org/tosca/ns/2011/12\":documentation, WC[##other:\"http://docs.oasis-open.org/tosca/ns/2011/12\"], \"http://docs.oasis-open.org/tosca/ns/2011/12\":Tags, \"http://docs.oasis-open.org/tosca/ns/2011/12\":BoundaryDefinitions, \"http://docs.oasis-open.org/tosca/ns/2011/12\":TopologyTemplate}' is expected.\n");
        expected.put(new QName("http://opentosca.org/servicetemplates", "AAInjectorTest"), elementErrorList);

        elementErrorList = new ElementErrorList("ArtifactTemplate");
        elementErrorList.addError("Namespace URI contains tosca definitions name from other type. E.g., Namespace is ...servicetemplates..., but the type is an artifact template");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "PonyEquipment_abstract"), elementErrorList);

        elementErrorList = new ElementErrorList("ArtifactTemplate");
        elementErrorList.addError("Namespace URI contains tosca definitions name from other type. E.g., Namespace is ...servicetemplates..., but the type is an artifact template");
        elementErrorList.addError("Properties required, but no properties defined");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "WesternEquipment_Unicorn"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.2.4.a: Invalid content was found starting with element 'harvest'. One of '{WC[##other:\"http://docs.oasis-open.org/tosca/ns/2011/12\"]}' is expected.\n");
        elementErrorList.addError("propertiesKV of node template unicorn is null");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/fruits", "baobab_serviceTemplate"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.2.4.b: The content of element 'Properties' is not complete. One of '{WC[##other:\"http://docs.oasis-open.org/tosca/ns/2011/12\"]}' is expected.\n" +
            "Fatal Error: cvc-complex-type.2.4.b: The content of element 'Requirements' is not complete. One of '{\"http://docs.oasis-open.org/tosca/ns/2011/12\":Requirement}' is expected.\n" +
            "Fatal Error: cvc-complex-type.2.4.b: The content of element 'DeploymentArtifacts' is not complete. One of '{\"http://docs.oasis-open.org/tosca/ns/2011/12\":DeploymentArtifact}' is expected.\n");
        expected.put(new QName("http://www.opentosca.org/providers/FieldProvider", "FieldProvider"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-datatype-valid.1.2.1: '1' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '1' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '2' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '2' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '3' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '3' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '4' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '4' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '5' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '5' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n");
        expected.put(new QName("http://www.winery.opentosca.org/test/targetallocation/servicetemplates", "TargetAllocationMinHostsTest5_-w1-wip1"), elementErrorList);

        elementErrorList = new ElementErrorList("ArtifactTemplate");
        elementErrorList.addError("Namespace URI contains tosca definitions name from other type. E.g., Namespace is ...servicetemplates..., but the type is an artifact template");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DressageEquipment_Pony"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-datatype-valid.1.2.1: '1' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '1' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '2' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '2' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '3' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '3' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '4' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '4' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n");
        expected.put(new QName("http://www.winery.opentosca.org/test/targetallocation/servicetemplates", "MinExternalConnectionsTest1"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-datatype-valid.1.2.1: '21' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '21' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '1' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '1' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '213' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '213' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '11234' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '11234' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n");
        expected.put(new QName("http://www.winery.opentosca.org/test/targetallocation/servicetemplates", "TargetAllocationFulfillPoliciesTest1"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-datatype-valid.1.2.1: '1' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '1' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '6' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '6' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '8' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '8' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '16' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '16' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '3' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '3' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '7' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '7' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '9' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '9' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '14' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '14' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '4' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '4' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '2' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '2' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '15' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '15' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '13' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '13' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '11' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '11' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '12' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '12' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '5' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '5' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '10' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '10' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '17' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '17' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '18' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '18' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '19' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '19' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n" +
            "Fatal Error: cvc-datatype-valid.1.2.1: '20' is not a valid value for 'NCName'.\n" +
            "Fatal Error: cvc-attribute.3: The value '20' of attribute 'id' on element 'Requirement' is not valid with respect to its type, 'ID'.\n");
        expected.put(new QName("http://www.winery.opentosca.org/test/targetallocation/servicetemplates", "MinExternalConnectionsTest2"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("propertiesKV of node template straw is null");
        elementErrorList.addError("propertiesKV of node template stall is null");
        expected.put(new QName("http://www.opentosca.org/providers/StallProvider", "StallProvider"), elementErrorList);

        return expected;
    }

    private static Map<QName, ElementErrorList> getErrorsFor304b62b06556afa1a7227164a9c0d2c9a1178b8f() {
        Map<QName, ElementErrorList> expected = new HashMap<>();
        ElementErrorList elementErrorList;

        elementErrorList = new ElementErrorList("ArtifactTemplate");
        elementErrorList.addError("Fatal Error: UndeclaredPrefix: Cannot resolve 'wfa:WAR' as a QName: the prefix 'wfa' is not declared.\n" +
            "Fatal Error: cvc-attribute.3: The value 'wfa:WAR' of attribute 'type' on element 'tosca:ArtifactTemplate' is not valid with respect to its type, 'QName'.\n");
        elementErrorList.addError("type is null");
        elementErrorList.addError("Corrupt: Type is null for ArtifactTemplate baobab_bananaInterface_IA in namespace http://winery.opentosca.org/test/artifacttemplates/fruits");
        expected.put(new QName("http://winery.opentosca.org/test/artifacttemplates/fruits", "baobab_bananaInterface_IA"), elementErrorList);

        elementErrorList = new ElementErrorList("ServiceTemplate");
        elementErrorList.addError("Fatal Error: cvc-complex-type.2.4.a: Invalid content was found starting with element 'harvest'. One of '{WC[##other:\"http://docs.oasis-open.org/tosca/ns/2011/12\"]}' is expected.\n");
        elementErrorList.addError("Corrupt: Type is null for ArtifactTemplate baobab_bananaInterface_IA in namespace http://winery.opentosca.org/test/artifacttemplates/fruits");
        expected.put(new QName("http://winery.opentosca.org/test/servicetemplates/fruits", "baobab_serviceTemplate"), elementErrorList);

        elementErrorList = new ElementErrorList("NodeType");
        elementErrorList.addError("Corrupt: Type is null for ArtifactTemplate baobab_bananaInterface_IA in namespace http://winery.opentosca.org/test/artifacttemplates/fruits");
        expected.put(new QName("http://winery.opentosca.org/test/nodetypes/fruits", "grape"), elementErrorList);

        elementErrorList = new ElementErrorList("NodeTypeImplementation");
        elementErrorList.addError("Corrupt: Type is null for ArtifactTemplate baobab_bananaInterface_IA in namespace http://winery.opentosca.org/test/artifacttemplates/fruits");
        expected.put(new QName("http://winery.opentosca.org/test/nodetypeimplementations/fruits", "baobab_impl"), elementErrorList);

        elementErrorList = new ElementErrorList("NodeType");
        elementErrorList.addError("Fatal Error: cvc-complex-type.2.4.b: The content of element 'tosca:Constraint' is not complete. One of '{WC[##other:\"http://docs.oasis-open.org/tosca/ns/2011/12\"]}' is expected.\n");
        elementErrorList.addError("Corrupt: Type is null for ArtifactTemplate baobab_bananaInterface_IA in namespace http://winery.opentosca.org/test/artifacttemplates/fruits");
        expected.put(new QName("http://winery.opentosca.org/test/nodetypes/fruits", "baobab"), elementErrorList);

        return expected;
    }

    private static Stream<Arguments> getNonWorkingRepositoryStates() {
        return Stream.of(
            Arguments.of(
                // origin/plain in a non-working version
                "20f6d0afd4395ab83f059cb5fabbb08218c9fcbd",
                getErrorsFor20f6d0afd4395ab83f059cb5fabbb08218c9fcbd()
            ),
            Arguments.of(
                "0750656975fec2d8a7c8400df85e37b1f2cdb5ac",
                // origin/black in a non-working version
                getErrorsFor0750656975fec2d8a7c8400df85e37b1f2cdb5ac()
            ),
            Arguments.of(
                "304b62b06556afa1a7227164a9c0d2c9a1178b8f",
                // origin/fruits in a non-working version
                getErrorsFor304b62b06556afa1a7227164a9c0d2c9a1178b8f()
            )
        );
    }

    /**
     * If this method fails, reconfigure IntelliJ and rebuild the complete project <br> Settings | Build, Execution,
     * Deployment | Compiler | [ ] Add runtime assertions for not-null-annotated methods and parameters.
     *
     * See https://stackoverflow.com/a/40847858/873282 for more information
     */
    @ParameterizedTest
    @MethodSource("getNonWorkingRepositoryStates")
    public void hasErrors(String revision, Map<QName, ElementErrorList> expected) throws Exception {
        final ConsistencyErrorCollector errorLogger = checkRevisionWithoutDocumentation(revision);
        assertNotNull(errorLogger);
        assertEquals(expected, errorLogger.getErrorList());
    }
}
