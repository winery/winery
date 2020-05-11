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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ComplianceRuleId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.compliance.checking.ComplianceCheckingException;
import org.eclipse.winery.compliance.checking.ComplianceRuleChecker;
import org.eclipse.winery.compliance.checking.ServiceTemplateCheckingResult;
import org.eclipse.winery.compliance.checking.ServiceTemplateComplianceRuleRuleChecker;
import org.eclipse.winery.compliance.checking.ToscaComplianceRuleMatcher;
import org.eclipse.winery.model.tosca.TComplianceRule;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.Disabled;
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
import static org.eclipse.winery.compliance.ToscaModelHelper.createTRelationshipTemplate;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTRelationshipType;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTServiceTemplate;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTTopologyTemplate;
import static org.eclipse.winery.compliance.ToscaModelHelper.setDerivedFrom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToscaGraphIsomorphismTest extends TestWithGitBackedRepository {

    // TODO revise
//    private final IRepository repository = initializeRepository();
//
//    private IRepository initializeRepository() {
//        Path path = Paths.get(System.getProperty("java.io.tmpdir")).resolve("test-repository");
//        return RepositoryFactory.getRepository(new FileBasedRepositoryConfiguration(path));
//    }

    private void persist(HashMap<DefinitionsChildId, TExtensibleElements> allEntities) throws IOException {
        for (Map.Entry<DefinitionsChildId, TExtensibleElements> entry : allEntities.entrySet()) {
            repository.setElement(entry.getKey(), entry.getValue());
        }
    }

    // TODO revise
//    @BeforeEach
//    public void cleanUp() {
//        repository.doClear();
//    }

    @Test
    public void testTComplianceRulePersistence() throws Exception {
        TComplianceRule rule = new TComplianceRule();
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

//		String repositoryURI = "http://localhost:8080/winery";
//
//		boolean USE_PROXY = false;
//
//		IWineryRepositoryClient client = new WineryRepositoryClient(USE_PROXY);
//
//		client.addRepository(repositoryURI);
//
//		TTopologyTemplate testTemplate = client.getTopologyTemplate(new QName( "http://opentosca.org/compliancerules","TestTemplate"));
//		
//		TNodeTemplate.Policies leftPolicies;
//		TPolicy policy;

    }

    @Test
    @Disabled // TODO Fix this test using the repo test superclass
    public void testServiceTemplateComplianceRuleChecker() throws IOException {
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
        TComplianceRule ruleOne = createTComplianceRule(crId1);
        ruleOne.setIdentifier(createTTopologyTemplate(Arrays.asList(ruleOneIdentifier), newArrayList()));
        ruleOne.setRequiredStructure(createTTopologyTemplate(Arrays.asList(ruleOneRequiredStructure), newArrayList()));
        allEntities.put(crId1, ruleOne);

        //unsatisfied
        TComplianceRule ruleTwo = createTComplianceRule(crId2);
        ruleTwo.setIdentifier(createTTopologyTemplate(Arrays.asList(ruleTwoIdentifier), newArrayList()));
        ruleTwo.setRequiredStructure(createTTopologyTemplate(Arrays.asList(ruleTwoRequiredStructure), newArrayList()));
        allEntities.put(crId2, ruleTwo);

        //invalid rule
        TComplianceRule ruleThree = createTComplianceRule(crId3);
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
    public void testTOSCAComplianceRuleChecker() throws IOException, ComplianceCheckingException {

        HashMap<DefinitionsChildId, TExtensibleElements> allEntities = new HashMap<>();

        ToscaModelPropertiesBuilder bldr = new ToscaModelPropertiesBuilder(TEST_TARGET_NAMESPACE, "MyProperties");
        bldr.addProperty("key1", "value1");
        bldr.addProperty("key2", "value2");

        //create NodeTypes A B 
        String idNodeTypeA = "idA";
        TNodeType nodeTypeA = createTNodeType(idNodeTypeA, TEST_TARGET_NAMESPACE);
        String idNodeTypeB = "idB";
        TNodeType nodeTypeB = createTNodeType(idNodeTypeB, TEST_TARGET_NAMESPACE);
        String idNodeTypeC = "idC";
        TNodeType nodeTypeC = createTNodeType(idNodeTypeC, TEST_TARGET_NAMESPACE);

        NodeTypeId idA = createNodeTypeId(idNodeTypeA);
        NodeTypeId idB = createNodeTypeId(idNodeTypeB);
        NodeTypeId idC = createNodeTypeId(idNodeTypeC);

        allEntities.put(idA, nodeTypeA);
        allEntities.put(idB, nodeTypeB);
        allEntities.put(idC, nodeTypeC);

        //createRelationshipTypes
        String relTypeIdAString = "adRelA";
        RelationshipTypeId relTypeIdA = new RelationshipTypeId(new QName(TEST_TARGET_NAMESPACE, relTypeIdAString));
        TRelationshipType relTypeA = createTRelationshipType(relTypeIdAString, TEST_TARGET_NAMESPACE);

        allEntities.put(relTypeIdA, relTypeA);

        //createNodeTemplates
        TNodeTemplate nodeTemplate1 = createTNodeTemplate("01");
        nodeTemplate1.setType(idA.getQName());

        TNodeTemplate nodeTemplate2 = createTNodeTemplate("02");
        nodeTemplate2.setType(idB.getQName());

        TNodeTemplate nodeTemplate3 = createTNodeTemplate("03");
        nodeTemplate3.setType(idA.getQName());
        nodeTemplate3.setProperties(bldr.build());

        TNodeTemplate nodeTemplate4 = createTNodeTemplate("04");
        nodeTemplate4.setType(idB.getQName());

        TNodeTemplate nodeTemplate5 = createTNodeTemplate("05");
        nodeTemplate5.setType(idA.getQName());

        TNodeTemplate nodeTemplate6 = createTNodeTemplate("06");
        nodeTemplate6.setType(idB.getQName());

        TNodeTemplate nodeTemplate7 = createTNodeTemplate("07");
        nodeTemplate7.setType(idA.getQName());
        nodeTemplate7.setProperties(bldr.build());

        TNodeTemplate nodeTemplate8 = createTNodeTemplate("08");
        nodeTemplate8.setType(idB.getQName());

        TNodeTemplate nodeTemplate9 = createTNodeTemplate("CompletelyUnrelated");
        nodeTemplate9.setType(idC.getQName());

        //create RelationshipTemplates
        TRelationshipTemplate relTemplate1 = createTRelationshipTemplate("1");
        relTemplate1.setSourceNodeTemplate(nodeTemplate1);
        relTemplate1.setTargetNodeTemplate(nodeTemplate2);
        relTemplate1.setType(relTypeIdA.getQName());

        TRelationshipTemplate relTemplate2 = createTRelationshipTemplate("2");
        relTemplate2.setSourceNodeTemplate(nodeTemplate3);
        relTemplate2.setTargetNodeTemplate(nodeTemplate4);
        relTemplate2.setType(relTypeIdA.getQName());

        TRelationshipTemplate relTemplate3 = createTRelationshipTemplate("3");
        relTemplate3.setSourceNodeTemplate(nodeTemplate5);
        relTemplate3.setTargetNodeTemplate(nodeTemplate6);
        relTemplate3.setType(relTypeIdA.getQName());

        TRelationshipTemplate relTemplate4 = createTRelationshipTemplate("4");
        relTemplate4.setSourceNodeTemplate(nodeTemplate7);
        relTemplate4.setTargetNodeTemplate(nodeTemplate8);
        relTemplate4.setType(relTypeIdA.getQName());

        //create TopologyTemplates
        List<TNodeTemplate> nodeTemplates = new ArrayList<>();
        List<TRelationshipTemplate> relationshipTemplates = new ArrayList<>();

        // create identifier
        nodeTemplates.add(nodeTemplate1);
        nodeTemplates.add(nodeTemplate2);
        relationshipTemplates.add(relTemplate1);
        TTopologyTemplate identifier = createTTopologyTemplate(nodeTemplates, relationshipTemplates);
        identifier.setNodeTemplates(nodeTemplates);
        identifier.setRelationshipTemplates(relationshipTemplates);
        nodeTemplates.clear();
        relationshipTemplates.clear();

        //create required structure
        nodeTemplates.add(nodeTemplate3);
        nodeTemplates.add(nodeTemplate4);
        relationshipTemplates.add(relTemplate2);
        TTopologyTemplate requiredStructure = createTTopologyTemplate(nodeTemplates, relationshipTemplates);
        requiredStructure.setNodeTemplates(nodeTemplates);
        requiredStructure.setRelationshipTemplates(relationshipTemplates);
        nodeTemplates.clear();
        relationshipTemplates.clear();

        //create topologyToSearchIn
        nodeTemplates.add(nodeTemplate5);
        nodeTemplates.add(nodeTemplate6);
        nodeTemplates.add(nodeTemplate7);
        nodeTemplates.add(nodeTemplate8);
        relationshipTemplates.add(relTemplate3);
        relationshipTemplates.add(relTemplate4);
        TTopologyTemplate topologyTemplateToSearchIn = createTTopologyTemplate(nodeTemplates, relationshipTemplates);
        topologyTemplateToSearchIn.setNodeTemplates(nodeTemplates);
        topologyTemplateToSearchIn.setRelationshipTemplates(relationshipTemplates);
        nodeTemplates.clear();
        relationshipTemplates.clear();

        //create unrelated topology
        nodeTemplates.add(nodeTemplate9);
        TTopologyTemplate unrelatedTopology = createTTopologyTemplate(nodeTemplates, relationshipTemplates);
        unrelatedTopology.setNodeTemplates(nodeTemplates);
        unrelatedTopology.setRelationshipTemplates(relationshipTemplates);
        nodeTemplates.clear();
        relationshipTemplates.clear();

        persist(allEntities);

        // **************** //
        // Test starts here //
        // **************** //

        ComplianceRuleChecker checker;

        // test null topologyTemplateToCheck
        checker = new ComplianceRuleChecker(null, null, null);
        ComplianceCheckingException expected = null;
        try {
            checker.checkComplianceRule();
        } catch (ComplianceCheckingException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertTrue(StringUtils.equals(ComplianceCheckingException.NO_TEMPLATE_TO_CHECK, expected.getMessage()));

        checker.setToCheckTemplate(topologyTemplateToSearchIn);

        // test empty rule
        expected = null;
        try {
            checker.checkComplianceRule();
        } catch (ComplianceCheckingException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertTrue(StringUtils.equals(ComplianceCheckingException.EMPTY_COMPLIANCE_RULE, expected.getMessage()));

        // test Whitelist
        checker.setRequiredStructureTemplate(requiredStructure);
        expected = null;
        try {
            checker.checkComplianceRule();
        } catch (ComplianceCheckingException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertTrue(StringUtils.equals(ComplianceCheckingException.WHITELISTING_NOT_YET_IMPLEMENTED, expected.getMessage()));

        // test blacklist
        checker.setRequiredStructureTemplate(null);
        checker.setIdentifierTemplate(identifier);
        List<GraphMapping> blacklistResult = checker.checkComplianceRule();
        assertEquals(2, blacklistResult.size());

        // test completeRule
        // invalid Rule: identifier and required structure have no mapping
        checker.setIdentifierTemplate(unrelatedTopology);
        checker.setRequiredStructureTemplate(requiredStructure);
        expected = null;
        try {
            checker.checkComplianceRule();
        } catch (ComplianceCheckingException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertTrue(StringUtils.equals(ComplianceCheckingException.IDENTIFIER_NOT_IN_REQUIREDSTRUCTURE, expected.getMessage()));

        // valid Rule: identifier in required structure
        // finds one violation
        checker.setIdentifierTemplate(identifier);
        checker.setRequiredStructureTemplate(requiredStructure);
        List<GraphMapping> violatingMappings = checker.checkComplianceRule();
        assertEquals(1, violatingMappings.size());

        // check the mapping. 
        // must contain nodesTemplates 5 & 6 and relTemplate3

        ToscaNode identifierNode1 = checker.getIdentifierGraph().getNode("01");
        ToscaNode identifierNode2 = checker.getIdentifierGraph().getNode("02");
        ToscaEdge identifierEdge = checker.getIdentifierGraph().getEdge(identifierNode1, identifierNode2);

        GraphMapping violatingMapping = violatingMappings.stream().findFirst().get();

        assertNotNull(violatingMapping);
        Map<ToscaNode, ToscaNode> resultMap = checker.getSubGraphMappingAsMap(violatingMapping, checker.getIdentifierGraph());

        assertTrue(resultMap.get(identifierNode1).getId().matches("05"));
        assertTrue(resultMap.get(identifierNode2).getId().matches("06"));
        assertTrue(((ToscaEdge) violatingMapping.getEdgeCorrespondence(identifierEdge, false)).getId().matches("3"));

        //finds no violation
        checker.setRequiredStructureTemplate(identifier);
        assertEquals(0, checker.checkComplianceRule().size());

        TComplianceRule rule = new TComplianceRule();
        rule.setName("test");
        rule.setTargetNamespace(TEST_TARGET_NAMESPACE);
        rule.setIdentifier(identifier);
        rule.setRequiredStructure(requiredStructure);
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

        TComplianceRule ruleOne = createTComplianceRule(crId1);
        allEntities.put(crId1, ruleOne);

        TComplianceRule ruleTwo = createTComplianceRule(crId2);
        allEntities.put(crId2, ruleTwo);

        TComplianceRule ruleThree = createTComplianceRule(crId3);
        allEntities.put(crId3, ruleThree);

        TComplianceRule ruleFour = createTComplianceRule(crId4);
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
