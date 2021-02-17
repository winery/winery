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

package org.eclipse.winery.compliance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.compliance.checking.ComplianceCheckingException;
import org.eclipse.winery.compliance.checking.ComplianceRuleChecker;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.eclipse.winery.compliance.ToscaModelHelper.TEST_TARGET_NAMESPACE;
import static org.eclipse.winery.compliance.ToscaModelHelper.createNodeTypeId;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTNodeTemplate;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTNodeType;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTRelationshipTemplate;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTRelationshipType;
import static org.eclipse.winery.compliance.ToscaModelHelper.createTTopologyTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComplianceRuleCheckerTests  extends TestWithGitBackedRepository {

    private TTopologyTemplate topologyTemplateToSearchIn;
    private TTopologyTemplate requiredStructure;
    private TTopologyTemplate identifier;
    private TTopologyTemplate unrelatedTopology;

    private void persist(HashMap<DefinitionsChildId, TExtensibleElements> allEntities) throws IOException {
        for (Map.Entry<DefinitionsChildId, TExtensibleElements> entry : allEntities.entrySet()) {
            repository.setElement(entry.getKey(), entry.getValue());
        }
    }

    @BeforeEach
    public void setup() throws IOException {
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
        identifier = createTTopologyTemplate(nodeTemplates, relationshipTemplates);
        identifier.setNodeTemplates(nodeTemplates);
        identifier.setRelationshipTemplates(relationshipTemplates);
        nodeTemplates.clear();
        relationshipTemplates.clear();

        //create required structure
        nodeTemplates.add(nodeTemplate3);
        nodeTemplates.add(nodeTemplate4);
        relationshipTemplates.add(relTemplate2);
        requiredStructure = createTTopologyTemplate(nodeTemplates, relationshipTemplates);
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
        topologyTemplateToSearchIn = createTTopologyTemplate(nodeTemplates, relationshipTemplates);
        topologyTemplateToSearchIn.setNodeTemplates(nodeTemplates);
        topologyTemplateToSearchIn.setRelationshipTemplates(relationshipTemplates);
        nodeTemplates.clear();
        relationshipTemplates.clear();

        //create unrelated topology
        nodeTemplates.add(nodeTemplate9);
        unrelatedTopology = createTTopologyTemplate(nodeTemplates, relationshipTemplates);
        unrelatedTopology.setNodeTemplates(nodeTemplates);
        unrelatedTopology.setRelationshipTemplates(relationshipTemplates);
        nodeTemplates.clear();
        relationshipTemplates.clear();

        persist(allEntities);
    }

    @Test
    public void nullTemplateToCheck() {
        ComplianceRuleChecker checker = new ComplianceRuleChecker(null, null, null);
        ComplianceCheckingException expected = null;
        try {
            checker.checkComplianceRule();
        } catch (ComplianceCheckingException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertTrue(StringUtils.equals(ComplianceCheckingException.NO_TEMPLATE_TO_CHECK, expected.getMessage()));
    }

    @Test
    public void emptyRule() {
        ComplianceRuleChecker checker = new ComplianceRuleChecker(null, null, null);
        checker.setToCheckTemplate(topologyTemplateToSearchIn);

        ComplianceCheckingException expected = null;
        try {
            checker.checkComplianceRule();
        } catch (ComplianceCheckingException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertTrue(StringUtils.equals(ComplianceCheckingException.EMPTY_COMPLIANCE_RULE, expected.getMessage()));
    }

    @Test
    public void whitelist() {
        ComplianceRuleChecker checker = new ComplianceRuleChecker(null, null, null);
        checker.setToCheckTemplate(topologyTemplateToSearchIn);

        checker.setRequiredStructureTemplate(requiredStructure);
        ComplianceCheckingException expected = null;
        try {
            checker.checkComplianceRule();
        } catch (ComplianceCheckingException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertTrue(StringUtils.equals(ComplianceCheckingException.WHITELISTING_NOT_YET_IMPLEMENTED, expected.getMessage()));
    }

    @Test
    public void blacklist() throws Exception {
        ComplianceRuleChecker checker = new ComplianceRuleChecker(null, null, null);
        checker.setToCheckTemplate(topologyTemplateToSearchIn);
        checker.setRequiredStructureTemplate(null);
        checker.setIdentifierTemplate(identifier);
        List<GraphMapping> blacklistResult = checker.checkComplianceRule();
        assertEquals(2, blacklistResult.size());
    }

    @Test
    public void completeRule() {
        ComplianceRuleChecker checker = new ComplianceRuleChecker(null, null, null);
        checker.setToCheckTemplate(topologyTemplateToSearchIn);

        checker.setIdentifierTemplate(unrelatedTopology);
        checker.setRequiredStructureTemplate(requiredStructure);
        ComplianceCheckingException expected = null;
        try {
            checker.checkComplianceRule();
        } catch (ComplianceCheckingException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertTrue(StringUtils.equals(ComplianceCheckingException.IDENTIFIER_NOT_IN_REQUIREDSTRUCTURE, expected.getMessage()));
    }

    @Test
    public void completeRule_noMapping() {
        ComplianceRuleChecker checker = new ComplianceRuleChecker(null, null, null);
        checker.setToCheckTemplate(topologyTemplateToSearchIn);

        // test completeRule
        // invalid Rule: identifier and required structure have no mapping
        checker.setIdentifierTemplate(unrelatedTopology);
        checker.setRequiredStructureTemplate(requiredStructure);
        ComplianceCheckingException expected = null;
        try {
            checker.checkComplianceRule();
        } catch (ComplianceCheckingException e) {
            expected = e;
        }
        assertNotNull(expected);
        assertTrue(StringUtils.equals(ComplianceCheckingException.IDENTIFIER_NOT_IN_REQUIREDSTRUCTURE, expected.getMessage()));
    }

    @Test
    public void validRule_identifierInRequired_findsOne() throws Exception {
        ComplianceRuleChecker checker = new ComplianceRuleChecker(null, null, null);
        checker.setToCheckTemplate(topologyTemplateToSearchIn);

        checker.setIdentifierTemplate(identifier);
        checker.setRequiredStructureTemplate(requiredStructure);
        List<GraphMapping> violatingMappings = checker.checkComplianceRule();
        assertEquals(1, violatingMappings.size());

        ToscaNode identifierNode1 = checker.getIdentifierGraph().getNode("01");
        ToscaNode identifierNode2 = checker.getIdentifierGraph().getNode("02");
        ToscaEdge identifierEdge = checker.getIdentifierGraph().getEdge(identifierNode1, identifierNode2);

        GraphMapping violatingMapping = violatingMappings.stream().findFirst().get();

        assertNotNull(violatingMapping);
        Map<ToscaNode, ToscaNode> resultMap = checker.getSubGraphMappingAsMap(violatingMapping, checker.getIdentifierGraph());

        assertTrue(resultMap.get(identifierNode1).getId().matches("05"));
        assertTrue(resultMap.get(identifierNode2).getId().matches("06"));
        assertTrue(((ToscaEdge) violatingMapping.getEdgeCorrespondence(identifierEdge, false)).getId().matches("3"));
    }

    @Test
    public void findsNoViolation() throws Exception {
        ComplianceRuleChecker checker = new ComplianceRuleChecker(null, null, null);
        checker.setToCheckTemplate(topologyTemplateToSearchIn);

        checker.setIdentifierTemplate(identifier);
        checker.setRequiredStructureTemplate(identifier);
        assertEquals(0, checker.checkComplianceRule().size());
    }
}
