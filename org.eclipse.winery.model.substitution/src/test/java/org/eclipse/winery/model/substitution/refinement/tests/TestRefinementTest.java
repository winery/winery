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

package org.eclipse.winery.model.substitution.refinement.tests;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRefinementModel;
import org.eclipse.winery.model.tosca.TRelationDirection;
import org.eclipse.winery.model.tosca.TRelationMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTestRefinementModel;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEdgeFactory;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.Test;

import static org.eclipse.jdt.annotation.Checks.assertNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRefinementTest {

    @Test
    void applyRefinementTest() {
        // region *** topology ***
        TNodeTemplate tomcat = new TNodeTemplate();
        tomcat.setId("tomcat");
        TNodeTemplate webShop = new TNodeTemplate();
        webShop.setId("webShop");
        TNodeTemplate database = new TNodeTemplate();
        database.setId("database");

        TRelationshipTemplate webShopOnTomcat = new TRelationshipTemplate();
        webShopOnTomcat.setSourceNodeTemplate(webShop);
        webShopOnTomcat.setTargetNodeTemplate(tomcat);

        TRelationshipTemplate webShopToDatabase = new TRelationshipTemplate();
        webShopToDatabase.setTargetNodeTemplate(database);
        webShopToDatabase.setSourceNodeTemplate(webShop);

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
        TRefinementModel.TRelationMappings relationMappings = new TRefinementModel.TRelationMappings();
        relationMappings.getRelationMapping().add(testHostedOn);
        relationMappings.getRelationMapping().add(testConnectsTo);
        relationMappings.getRelationMapping().add(testIngoingRelationTest);

        TTestRefinementModel testRefinementModel = new TTestRefinementModel();
        testRefinementModel.setRefinementTopology(refinementTopology);
        testRefinementModel.setRelationMappings(relationMappings);
        // endregion

        RefinementCandidate refinementCandidate = new RefinementCandidate(testRefinementModel, new EmptyGraphMapping(),
            new ToscaGraph(new ToscaEdgeFactory()), 1);

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

    private class EmptyGraphMapping implements GraphMapping<ToscaNode, ToscaEdge> {
        @Override
        public ToscaNode getVertexCorrespondence(ToscaNode vertex, boolean forward) {
            return null;
        }

        @Override
        public ToscaEdge getEdgeCorrespondence(ToscaEdge edge, boolean forward) {
            return null;
        }
    }
}
