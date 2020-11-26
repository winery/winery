/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.model.tosca.extensions.OTTestRefinementModel;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaTypeMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        TTopologyTemplate topologyTemplate = new TTopologyTemplate.Builder()
            .addNodeTemplates(tomcat)
            .addNodeTemplates(webShop)
            .addNodeTemplates(database)
            .addRelationshipTemplate(webShopOnTomcat)
            .addRelationshipTemplate(webShopToDatabase)
            .build();
        // endregion

        // region *** refinement model ***
        TNodeTemplate mySqlConnectorTest = new TNodeTemplate();
        mySqlConnectorTest.setId("sqlConnectorTest");
        mySqlConnectorTest.setType("{ns}sqlConnectorTest");
        TTopologyTemplate refinementTopology = new TTopologyTemplate.Builder()
            .addNodeTemplates(mySqlConnectorTest)
            .build();

        OTRelationMapping testHostedOn = new OTRelationMapping(new OTRelationMapping.Builder()
            .setDirection(OTRelationDirection.OUTGOING)
            .setRelationType(QName.valueOf("{ns}hostedOn"))
            .setRefinementElement(mySqlConnectorTest)
            .setDetectorElement(tomcat));
        OTRelationMapping testConnectsTo = new OTRelationMapping(new OTRelationMapping.Builder()
            .setDirection(OTRelationDirection.OUTGOING)
            .setRelationType(QName.valueOf("{ns}connectsTo"))
            .setRefinementElement(mySqlConnectorTest)
            .setDetectorElement(database));
        OTRelationMapping testIngoingRelationTest = new OTRelationMapping(new OTRelationMapping.Builder()
            .setDirection(OTRelationDirection.INGOING)
            .setRelationType(QName.valueOf("{ns}ingoingTest"))
            .setRefinementElement(mySqlConnectorTest)
            .setDetectorElement(webShop));
        List<OTRelationMapping> relationMappings = new ArrayList<>();
        relationMappings.add(testHostedOn);
        relationMappings.add(testConnectsTo);
        relationMappings.add(testIngoingRelationTest);

        OTTestRefinementModel testRefinementModel = new OTTestRefinementModel(new OTTestRefinementModel.Builder()
            .setTestFragment(refinementTopology)
            .setDetector(topologyTemplate)
            .setRelationMappings(relationMappings));
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
        assertNotNull(mimicTestHostedOnTomcat);
        assertEquals("tomcat", mimicTestHostedOnTomcat.getTargetElement().getRef().getId());
        assertEquals("sqlConnectorTest", mimicTestHostedOnTomcat.getSourceElement().getRef().getId());

        TRelationshipTemplate mimicTestConnectsToDatabase = topologyTemplate.getRelationshipTemplate("mimicTest-connectsTo");
        assertNotNull(mimicTestConnectsToDatabase);
        assertEquals("database", mimicTestConnectsToDatabase.getTargetElement().getRef().getId());
        assertEquals("sqlConnectorTest", mimicTestConnectsToDatabase.getSourceElement().getRef().getId());

        TRelationshipTemplate mimicTestTestIngoingConnection = topologyTemplate.getRelationshipTemplate("mimicTest-ingoingTest");
        assertNotNull(mimicTestTestIngoingConnection);
        assertEquals("webShop", mimicTestTestIngoingConnection.getSourceElement().getRef().getId());
        assertEquals("sqlConnectorTest", mimicTestTestIngoingConnection.getTargetElement().getRef().getId());
        // endregion
    }
}
