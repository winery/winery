/********************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.compliance;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.winery.compliance.checking.ServiceTemplateCheckingResult;
import org.eclipse.winery.compliance.checking.ServiceTemplateComplianceRuleRuleChecker;
import org.eclipse.winery.compliance.checking.ToscaComplianceRuleMatcher;
import org.eclipse.winery.model.ids.extensions.ComplianceRuleId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.extensions.OTComplianceRule;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.winery.compliance.ToscaModelHelper.TEST_TARGET_NAMESPACE;
import static org.eclipse.winery.compliance.ToscaModelHelper.addEdge;
import static org.eclipse.winery.compliance.ToscaModelHelper.createNodeTypeId;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTComplianceRule;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTNodeTemplate;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTNodeType;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTOSCANode;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTOSCANodeOnlyProperties;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTOSCANodeOnlyTypes;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTServiceTemplate;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTTopologyTemplate;
import static org.eclipse.winery.compliance.ToscaModelHelper.setDerivedFrom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToscaGraphIsomorphismTest extends TestWithGitBackedRepository {

    private void persist(HashMap<DefinitionsChildId, TExtensibleElements> allEntities) throws IOException {
        for (Map.Entry<DefinitionsChildId, TExtensibleElements> entry : allEntities.entrySet()) {
            repository.setElement(entry.getKey(), entry.getValue());
        }
    }

    @Test
    public void testTComplianceRulePersistence() throws Exception {
        OTComplianceRule rule = new OTComplianceRule(new OTComplianceRule.Builder());
        rule.setName("test");
        rule.setTargetNamespace(TEST_TARGET_NAMESPACE);

        ComplianceRuleId id = new ComplianceRuleId(new QName(TEST_TARGET_NAMESPACE, "test"));
        BackendUtils.persist(repository, id, rule);
        assertEquals("test", repository.getElement(id).getIdFromIdOrNameField());
    }

    @Test
    public void testTOSCAComplianceRuleMatcher() throws IOException {
        // TNodeTemplateMatching
        // by TNodeType		
        HashMap<DefinitionsChildId, TExtensibleElements> allEntities = new HashMap<>();

        NodeTypeId nodeTypeIdAbstractA = createNodeTypeId("AbstractA");
        allEntities.put(nodeTypeIdAbstractA, createTNodeType(nodeTypeIdAbstractA));

        NodeTypeId nodeTypeIdAbstractB = createNodeTypeId("AbstractB");
        allEntities.put(nodeTypeIdAbstractB, createTNodeType(nodeTypeIdAbstractB));

        NodeTypeId nodeTypeIdActualA = createNodeTypeId("ActualA");
        TNodeType actualA = createTNodeType(nodeTypeIdActualA);
        setDerivedFrom(nodeTypeIdAbstractA, actualA);
        allEntities.put(nodeTypeIdActualA, actualA);

        NodeTypeId nodeTypeIdActualB = createNodeTypeId("ActualB");
        TNodeType actualB = createTNodeType(nodeTypeIdActualB);
        setDerivedFrom(nodeTypeIdAbstractB, actualB);
        allEntities.put(nodeTypeIdActualB, actualB);

        NodeTypeId nodeTypeIdActualB2 = createNodeTypeId("ActualB2");
        TNodeType actualB2 = createTNodeType(nodeTypeIdActualB2);
        setDerivedFrom(nodeTypeIdAbstractB, actualB2);
        allEntities.put(nodeTypeIdActualB2, actualB2);

        persist(allEntities);

        TNodeTemplate nodeTemplate1 = createTNodeTemplate("01", nodeTypeIdAbstractA);
        TNodeTemplate nodeTemplate2 = createTNodeTemplate("02", nodeTypeIdAbstractB);
        TNodeTemplate nodeTemplate3 = createTNodeTemplate("03", nodeTypeIdActualA);
        TNodeTemplate nodeTemplate4 = createTNodeTemplate("04", nodeTypeIdActualB);
        TNodeTemplate nodeTemplate5 = createTNodeTemplate("05", nodeTypeIdActualB);
        TNodeTemplate nodeTemplate6 = createTNodeTemplate("06", nodeTypeIdActualB2);

        ToscaComplianceRuleMatcher matcher = new ToscaComplianceRuleMatcher();

        // same type
        assertTrue(matcher.isTEntityTypesCompatible(createTOSCANodeOnlyTypes(nodeTemplate1), createTOSCANodeOnlyTypes(nodeTemplate1)));

        // "01" is supertype of "03" -> match
        assertTrue(matcher.isTEntityTypesCompatible(createTOSCANodeOnlyTypes(nodeTemplate1), createTOSCANodeOnlyTypes(nodeTemplate3)));

        // "03" is subtype of "01" -> match
        assertTrue(matcher.isTEntityTypesCompatible(createTOSCANodeOnlyTypes(nodeTemplate3), createTOSCANodeOnlyTypes(nodeTemplate1)));

        // different types
        assertFalse(matcher.isTEntityTypesCompatible(createTOSCANodeOnlyTypes(nodeTemplate4), createTOSCANodeOnlyTypes(nodeTemplate3)));

        // different types
        assertFalse(matcher.isTEntityTypesCompatible(createTOSCANodeOnlyTypes(nodeTemplate3), createTOSCANodeOnlyTypes(nodeTemplate2)));

        // same type
        assertTrue(matcher.isTEntityTypesCompatible(createTOSCANodeOnlyTypes(nodeTemplate5), createTOSCANodeOnlyTypes(nodeTemplate4)));

        //different types derived from same type
        assertFalse(matcher.isTEntityTypesCompatible(createTOSCANodeOnlyTypes(nodeTemplate5), createTOSCANodeOnlyTypes(nodeTemplate6)));

        // by TEntityTemplate.Properties
        ToscaModelPropertiesBuilder bldrLeft = new ToscaModelPropertiesBuilder(TEST_TARGET_NAMESPACE, "TestProperties");
        ToscaModelPropertiesBuilder bldrRight = new ToscaModelPropertiesBuilder(TEST_TARGET_NAMESPACE, "TestProperties");

        // test by values
        // test simple matching
        bldrLeft.addProperty("A", "Something");
        bldrRight.addProperty("A", "Something");
        assertTrue(matcher.isPropertiesCompatible(createTOSCANodeOnlyProperties(bldrLeft), createTOSCANodeOnlyProperties(bldrRight)));

        bldrRight.addProperty("A", "SomeOtherThing");
        assertFalse(matcher.isPropertiesCompatible(createTOSCANodeOnlyProperties(bldrLeft), createTOSCANodeOnlyProperties(bldrRight)));

        // testRegex
        bldrLeft.addProperty("A", ".*");
        assertTrue(matcher.isPropertiesCompatible(createTOSCANodeOnlyProperties(bldrLeft), createTOSCANodeOnlyProperties(bldrRight)));

        // test by property set

        // left subset of right 
        bldrLeft.addProperty("B", ".*");
        bldrRight.addProperty("B", ".*");
        bldrRight.addProperty("C", ".*");
        assertTrue(matcher.isPropertiesCompatible(createTOSCANodeOnlyProperties(bldrLeft), createTOSCANodeOnlyProperties(bldrRight)));

        // other way round
        assertFalse(matcher.isPropertiesCompatible(createTOSCANodeOnlyProperties(bldrRight), createTOSCANodeOnlyProperties(bldrLeft)));
    }

    @Test
    public void testServiceTemplateComplianceRuleChecker() throws Exception {
        this.setRevisionTo("origin/plain");

        HashMap<DefinitionsChildId, TExtensibleElements> allEntities = new HashMap<>();

        TServiceTemplate tServiceTemplate = createTServiceTemplate("ServiceTemplateTestId", TEST_TARGET_NAMESPACE);
        ComplianceRuleId crId1 = new ComplianceRuleId(new QName(TEST_TARGET_NAMESPACE, "test1"));
        ComplianceRuleId crId2 = new ComplianceRuleId(new QName(TEST_TARGET_NAMESPACE, "test2"));
        ComplianceRuleId crId3 = new ComplianceRuleId(new QName(TEST_TARGET_NAMESPACE, "test3"));

        //create NodeType
        NodeTypeId nodeTypeIdA = createNodeTypeId("IDA");
        NodeTypeId nodeTypeIdB = createNodeTypeId("IDB");
        NodeTypeId nodeTypeIdC = createNodeTypeId("IDC");
        NodeTypeId nodeTypeIdD = createNodeTypeId("IDD");

        TNodeType nodeTypeA = createTNodeType(nodeTypeIdA);
        allEntities.put(nodeTypeIdA, nodeTypeA);

        TNodeType nodeTypeB = createTNodeType(nodeTypeIdB);
        setDerivedFrom(nodeTypeIdA, nodeTypeB);
        allEntities.put(nodeTypeIdB, nodeTypeB);

        TNodeType nodeTypeC = createTNodeType(nodeTypeIdC);
        setDerivedFrom(nodeTypeIdA, nodeTypeC);
        allEntities.put(nodeTypeIdC, nodeTypeC);

        TNodeType nodeTypeD = createTNodeType(nodeTypeIdD);
        allEntities.put(nodeTypeIdD, nodeTypeD);

        TNodeTemplate ruleOneIdentifier = createTNodeTemplate("R1_I", nodeTypeIdA);
        TNodeTemplate ruleOneRequiredStructure = createTNodeTemplate("R1_RS", nodeTypeIdB);

        TNodeTemplate ruleTwoIdentifier = createTNodeTemplate("R2_I", nodeTypeIdA);
        TNodeTemplate ruleTwoRequiredStructure = createTNodeTemplate("R2_RS", nodeTypeIdC);

        TNodeTemplate ruleThreeIdentifier = createTNodeTemplate("R3_I", nodeTypeIdA);
        TNodeTemplate ruleThreeRequiredStructure = createTNodeTemplate("R3_RS", nodeTypeIdD);

        TNodeTemplate serviceTemplateTopology = createTNodeTemplate("ST", nodeTypeIdB);

        //satisfied
        OTComplianceRule ruleOne = createTComplianceRule(crId1);
        ruleOne.setIdentifier(createTTopologyTemplate(Arrays.asList(ruleOneIdentifier), newArrayList()));
        ruleOne.setRequiredStructure(createTTopologyTemplate(Arrays.asList(ruleOneRequiredStructure), newArrayList()));
        allEntities.put(crId1, ruleOne);

        //unsatisfied
        OTComplianceRule ruleTwo = createTComplianceRule(crId2);
        ruleTwo.setIdentifier(createTTopologyTemplate(Arrays.asList(ruleTwoIdentifier), newArrayList()));
        ruleTwo.setRequiredStructure(createTTopologyTemplate(Arrays.asList(ruleTwoRequiredStructure), newArrayList()));
        allEntities.put(crId2, ruleTwo);

        //invalid rule
        OTComplianceRule ruleThree = createTComplianceRule(crId3);
        ruleThree.setIdentifier(createTTopologyTemplate(Arrays.asList(ruleThreeIdentifier), newArrayList()));
        ruleThree.setRequiredStructure(createTTopologyTemplate(Arrays.asList(ruleThreeRequiredStructure), newArrayList()));
        allEntities.put(crId3, ruleThree);
        tServiceTemplate.setTopologyTemplate(createTTopologyTemplate(Arrays.asList(serviceTemplateTopology), newArrayList()));

        persist(allEntities);

        ServiceTemplateComplianceRuleRuleChecker checker = new ServiceTemplateComplianceRuleRuleChecker(tServiceTemplate);
        assertEquals(3, checker.getRuleIds(tServiceTemplate).stream().count());
        assertEquals(3, checker.getRuleIds(tServiceTemplate).stream().filter(id -> id.getQName().getLocalPart().matches("test1|test2|test3")).count());

        ServiceTemplateCheckingResult serviceTemplateCheckingResult = checker.checkComplianceRules();
        assertEquals(1, serviceTemplateCheckingResult.getSatisfied().size());
        assertEquals(1, serviceTemplateCheckingResult.getUnsatisfied().size());
        assertEquals(1, serviceTemplateCheckingResult.getException().size());

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(ServiceTemplateCheckingResult.class);

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(serviceTemplateCheckingResult, sw);
            System.out.println(sw.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTOSCADefaultMatcher() {
        ToscaGraph queryGraph = new ToscaGraph();
        ToscaGraph searchInGraph = new ToscaGraph();

        ToscaNode node1 = createTOSCANode("node_01", "A");
        ToscaNode node2 = createTOSCANode("node_02", "B");
        queryGraph.addVertex(node1);
        queryGraph.addVertex(node2);
        ToscaEdge edge1 = addEdge(queryGraph, node1, node2, "edge_1", "a");

        ToscaNode node3 = createTOSCANode("node_03", "A");
        ToscaNode node4 = createTOSCANode("node_04", "A");
        ToscaNode node5 = createTOSCANode("node_05", "B");
        ToscaNode node6 = createTOSCANode("node_06", "B");
        searchInGraph.addVertex(node3);
        searchInGraph.addVertex(node4);
        searchInGraph.addVertex(node5);
        searchInGraph.addVertex(node6);
        addEdge(searchInGraph, node3, node4, "edge_2", "b");

        addEdge(searchInGraph, node3, node5, "edge_3", "a");

        addEdge(searchInGraph, node4, node6, "edge_4", "a");

        ToscaIsomorphismMatcher matcher = new ToscaIsomorphismMatcher();
        Iterator<GraphMapping<ToscaNode, ToscaEdge>> iterator = matcher.findMatches(queryGraph, searchInGraph, null);

        int isomorphismCount = 0;
        while (iterator.hasNext()) {
            isomorphismCount++;

            GraphMapping<ToscaNode, ToscaEdge> mapping = iterator.next();
            mapping.getEdgeCorrespondence(edge1, false);
            mapping.getVertexCorrespondence(node1, false);
            mapping.getVertexCorrespondence(node2, false);
        }
        assertEquals(2, isomorphismCount);
    }

    @Test
    public void testServiceTemplateAndComplianceRuleAssociation() throws IOException {
        HashMap<DefinitionsChildId, TExtensibleElements> allEntities = new HashMap<>();

        String dirABC = "/ABC";
        String dirBCD = "/BCD";

        ServiceTemplateId stId = new ServiceTemplateId(new QName(TEST_TARGET_NAMESPACE + dirABC + dirBCD, "test1"));
        TServiceTemplate tServiceTemplate = createTServiceTemplate("ServiceTemplateTestId", TEST_TARGET_NAMESPACE + dirABC + dirBCD);
        allEntities.put(stId, tServiceTemplate);

        ComplianceRuleId crId1 = new ComplianceRuleId(new QName(TEST_TARGET_NAMESPACE, "test1"));
        ComplianceRuleId crId2 = new ComplianceRuleId(new QName(TEST_TARGET_NAMESPACE + dirABC, "test2"));
        ComplianceRuleId crId3 = new ComplianceRuleId(new QName(TEST_TARGET_NAMESPACE + dirABC, "test3"));
        ComplianceRuleId crId4 = new ComplianceRuleId(new QName(TEST_TARGET_NAMESPACE + dirBCD, "test4"));

        OTComplianceRule ruleOne = createTComplianceRule(crId1);
        allEntities.put(crId1, ruleOne);

        OTComplianceRule ruleTwo = createTComplianceRule(crId2);
        allEntities.put(crId2, ruleTwo);

        OTComplianceRule ruleThree = createTComplianceRule(crId3);
        allEntities.put(crId3, ruleThree);

        OTComplianceRule ruleFour = createTComplianceRule(crId4);
        allEntities.put(crId4, ruleFour);

        persist(allEntities);

        ServiceTemplateComplianceRuleRuleChecker checker = new ServiceTemplateComplianceRuleRuleChecker(tServiceTemplate);
        List<ComplianceRuleId> ruleIds = checker.getRuleIds(tServiceTemplate);

        assertTrue(ruleIds.stream().anyMatch(id -> id.equals(crId1)));
        assertTrue(ruleIds.stream().anyMatch(id -> id.equals(crId2)));
        assertTrue(ruleIds.stream().anyMatch(id -> id.equals(crId3)));
        assertFalse(ruleIds.stream().anyMatch(id -> id.equals(crId4)));
    }
}
