/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.substitution.refinement.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationDirection;
import org.eclipse.winery.model.tosca.TRelationMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTestRefinementModel;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaTypeMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.Test;

import static org.eclipse.jdt.annotation.Checks.assertNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRefinementTest {

    @Test
    public void applyRefinementTest() {
        // region *** topology ***
        TNodeTemplate tomcat = new TNodeTemplate();
        tomcat.setId("tomcat");
        tomcat.setType("{ns}tomcat");
        TNodeTemplate webShop = new TNodeTemplate();
        webShop.setId("webShop");
        webShop.setType("{ns}webShop");
        TNodeTemplate database = new TNodeTemplate();
        database.setId("database");
        database.setType("{ns}database");

        TRelationshipTemplate webShopOnTomcat = new TRelationshipTemplate();
        webShopOnTomcat.setSourceNodeTemplate(webShop);
        webShopOnTomcat.setTargetNodeTemplate(tomcat);
        webShopOnTomcat.setType("{ns}hostedOn");

        TRelationshipTemplate webShopToDatabase = new TRelationshipTemplate();
        webShopToDatabase.setTargetNodeTemplate(database);
        webShopToDatabase.setSourceNodeTemplate(webShop);
        webShopToDatabase.setType("{ns}connectsTo");

        TTopologyTemplate topologyTemplate = new TTopologyTemplate();
        topologyTemplate.addNodeTemplate(tomcat);
        topologyTemplate.addNodeTemplate(webShop);
        topologyTemplate.addNodeTemplate(database);
        topologyTemplate.addRelationshipTemplate(webShopOnTomcat);
        topologyTemplate.addRelationshipTemplate(webShopToDatabase);
        // endregion

        // region *** refinement model ***
        TNodeTemplate mySqlConnectorTest = new TNodeTemplate();
        mySqlConnectorTest.setId("sqlConnectorTest");
        mySqlConnectorTest.setType("{ns}sqlConnectorTest");
        TTopologyTemplate refinementTopology = new TTopologyTemplate();
        refinementTopology.addNodeTemplate(mySqlConnectorTest);

        TRelationMapping testHostedOn = new TRelationMapping();
        testHostedOn.setDirection(TRelationDirection.OUTGOING);
        testHostedOn.setRelationType(QName.valueOf("{ns}hostedOn"));
        testHostedOn.setRefinementNode(mySqlConnectorTest);
        testHostedOn.setDetectorNode(tomcat);
        TRelationMapping testConnectsTo = new TRelationMapping();
        testConnectsTo.setDirection(TRelationDirection.OUTGOING);
        testConnectsTo.setRelationType(QName.valueOf("{ns}connectsTo"));
        testConnectsTo.setRefinementNode(mySqlConnectorTest);
        testConnectsTo.setDetectorNode(database);
        TRelationMapping testIngoingRelationTest = new TRelationMapping();
        testIngoingRelationTest.setDirection(TRelationDirection.INGOING);
        testIngoingRelationTest.setRelationType(QName.valueOf("{ns}ingoingTest"));
        testIngoingRelationTest.setRefinementNode(mySqlConnectorTest);
        testIngoingRelationTest.setDetectorNode(webShop);
        List<TRelationMapping> relationMappings = new ArrayList<>();
        relationMappings.add(testHostedOn);
        relationMappings.add(testConnectsTo);
        relationMappings.add(testIngoingRelationTest);

        TTestRefinementModel testRefinementModel = new TTestRefinementModel();
        testRefinementModel.setRefinementTopology(refinementTopology);
        testRefinementModel.setDetector(topologyTemplate);
        testRefinementModel.setRelationMappings(relationMappings);
        // endregion

        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topologyTemplate);
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(testRefinementModel.getDetector());
        ToscaIsomorphismMatcher matcher = new ToscaIsomorphismMatcher();
        Iterator<GraphMapping<ToscaNode, ToscaEdge>> mappings = matcher.findMatches(detectorGraph, topologyGraph, new ToscaTypeMatcher());
        GraphMapping<ToscaNode, ToscaEdge> mapping = mappings.next();

        RefinementCandidate refinementCandidate = new RefinementCandidate(testRefinementModel, mapping, detectorGraph, 1);

        TestRefinement testRefinement = new TestRefinement();
        testRefinement.applyRefinement(refinementCandidate, topologyTemplate);

        // region *** assertions ***
        assertEquals(4, topologyTemplate.getNodeTemplates().size());
        assertEquals(5, topologyTemplate.getRelationshipTemplates().size());

        TRelationshipTemplate mimicTestHostedOnTomcat = topologyTemplate.getRelationshipTemplate("mimicTest-hostedOn");
        assertNonNull(mimicTestHostedOnTomcat);
        assertEquals("tomcat", mimicTestHostedOnTomcat.getTargetElement().getRef().getId());
        assertEquals("sqlConnectorTest", mimicTestHostedOnTomcat.getSourceElement().getRef().getId());

        TRelationshipTemplate mimicTestConnectsToDatabase = topologyTemplate.getRelationshipTemplate("mimicTest-connectsTo");
        assertNonNull(mimicTestConnectsToDatabase);
        assertEquals("database", mimicTestConnectsToDatabase.getTargetElement().getRef().getId());
        assertEquals("sqlConnectorTest", mimicTestConnectsToDatabase.getSourceElement().getRef().getId());

        TRelationshipTemplate mimicTestTestIngoingConnection = topologyTemplate.getRelationshipTemplate("mimicTest-ingoingTest");
        assertNonNull(mimicTestTestIngoingConnection);
        assertEquals("webShop", mimicTestTestIngoingConnection.getSourceElement().getRef().getId());
        assertEquals("sqlConnectorTest", mimicTestTestIngoingConnection.getTargetElement().getRef().getId());
        // endregion
    }
}
