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

package org.eclipse.winery.model.adaptation.substitution.refinement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.OTRelationDirection;
import org.eclipse.winery.model.tosca.OTRelationMapping;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaTypeMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;

public class AbstractRefinementTest {

    protected static TTopologyTemplate topology;
    protected static TTopologyTemplate topology2;
    protected static TTopologyTemplate topology3;
    protected static RefinementCandidate candidate;
    protected static RefinementCandidate candidateWithDa;
    protected static RefinementCandidate candidateWithnNotMatchingDa;
    protected static RefinementCandidate invalidCandidate;

    protected static void setUp() {
        // region *** topology ***
        TNodeTemplate nt1 = new TNodeTemplate();
        nt1.setType("{http://ex.org}nodeType_1");
        nt1.setId("1");
        nt1.setX("10");
        nt1.setY("10");

        TNodeTemplate nt2 = new TNodeTemplate();
        nt2.setType("{http://ex.org}nodeType_2");
        nt2.setId("2");
        nt2.setX("110");
        nt2.setY("10");

        TNodeTemplate nt3 = new TNodeTemplate();
        nt3.setType("{http://ex.org}nodeType_3");
        nt3.setId("3");
        nt3.setX("210");
        nt3.setY("10");

        TNodeTemplate nt4 = new TNodeTemplate();
        nt4.setType("{http://ex.org}nodeType_4");
        nt4.setId("4");
        nt4.setX("110");
        nt4.setY("110");

        TRelationshipTemplate rt21 = new TRelationshipTemplate();
        rt21.setType("{http://ex.org}relType_1");
        rt21.setId("21");
        rt21.setSourceNodeTemplate(nt2);
        rt21.setTargetNodeTemplate(nt1);

        TRelationshipTemplate rt32 = new TRelationshipTemplate();
        rt32.setType("{http://ex.org}relType_1");
        rt32.setId("32");
        rt32.setSourceNodeTemplate(nt3);
        rt32.setTargetNodeTemplate(nt2);

        TRelationshipTemplate rt24 = new TRelationshipTemplate();
        rt24.setType("{http://ex.org}relType_2");
        rt24.setId("24");
        rt24.setSourceNodeTemplate(nt2);
        rt24.setTargetNodeTemplate(nt4);

        /*
        #######   (1)  #######   (1)  #######
        # (1) # <----- # (2) # <----- # (3) #
        #######        #######        #######
                          | (2)
                         \/
                       #######
                       # (4) #
                       #######
         */
        topology = new TTopologyTemplate();
        topology.addNodeTemplate(nt1);
        topology.addNodeTemplate(nt2);
        topology.addNodeTemplate(nt3);
        topology.addNodeTemplate(nt4);
        topology.addRelationshipTemplate(rt21);
        topology.addRelationshipTemplate(rt32);
        topology.addRelationshipTemplate(rt24);
        // endregion

        // region *** topology2 ***
        /*
        #######   (1)  #######
        # (1) # <----- # (2) #
        #######        #######
                          | (2)
                         \/
                       #######
                       # (4) #
                       #######
         */
        topology2 = new TTopologyTemplate();
        topology2.addNodeTemplate(nt1);
        topology2.addNodeTemplate(nt2);
        topology2.addNodeTemplate(nt4);
        topology2.addRelationshipTemplate(rt21);
        topology2.addRelationshipTemplate(rt24);
        // endregion
        
        // region *** topology 3 ***
        QName artifactTypeFile = new QName("file", "http://example.org/tosca/at");
        QName test_da = new QName("test_da", "http://example.org/tosca/atemp/das");

        TNodeTemplate nt30 = new TNodeTemplate();
        nt30.setType("{http://ex.org}nodeType_2");
        nt30.setId("30");
        nt30.setX("110");
        nt30.setY("10");

        TRelationshipTemplate rt304 = new TRelationshipTemplate();
        rt304.setType("{http://ex.org}relType_2");
        rt304.setId("304");
        rt304.setSourceNodeTemplate(nt30);
        rt304.setTargetNodeTemplate(nt4);

        TRelationshipTemplate rt301 = new TRelationshipTemplate();
        rt301.setType("{http://ex.org}relType_1");
        rt301.setId("301");
        rt301.setSourceNodeTemplate(nt30);
        rt301.setTargetNodeTemplate(nt1);
        
        TDeploymentArtifacts das = new TDeploymentArtifacts();
        TDeploymentArtifact da = new TDeploymentArtifact();
        da.setArtifactRef(test_da);
        da.setArtifactType(artifactTypeFile);
        das.getDeploymentArtifact().add(da);
        nt30.setDeploymentArtifacts(das);

        topology3 = new TTopologyTemplate();
        topology3.addNodeTemplate(nt1);
        topology3.addNodeTemplate(nt30);
        topology3.addNodeTemplate(nt4);
        topology3.addRelationshipTemplate(rt301);
        topology3.addRelationshipTemplate(rt304);
        // endregion

        // region *** matching PRM ***
        // region *** detector ***
        TNodeTemplate nt7 = new TNodeTemplate();
        nt7.setType("{http://ex.org}nodeType_2");
        nt7.setId("7");

        TNodeTemplate nt8 = new TNodeTemplate();
        nt8.setType("{http://ex.org}nodeType_4");
        nt8.setId("8");

        TRelationshipTemplate rt78 = new TRelationshipTemplate();
        rt78.setType("{http://ex.org}relType_2");
        rt78.setId("78");
        rt78.setSourceNodeTemplate(nt7);
        rt78.setTargetNodeTemplate(nt8);

        /*
        #######
        # (2) #
        #######
           | (2)
          \/
        #######
        # (4) #
        #######
         */
        TTopologyTemplate detector = new TTopologyTemplate();
        detector.addNodeTemplate(nt7);
        detector.addNodeTemplate(nt8);
        detector.addRelationshipTemplate(rt78);
        // endregion

        // region *** refinement structure
        TNodeTemplate nt10 = new TNodeTemplate();
        nt10.setType("{http://ex.org}nodeType_10");
        nt10.setId("10");
        nt10.setX("5");
        nt10.setY("10");

        TNodeTemplate nt11 = new TNodeTemplate();
        nt11.setType("{http://ex.org}nodeType_11");
        nt11.setId("11");
        nt11.setX("105");
        nt11.setY("5");

        TNodeTemplate nt12 = new TNodeTemplate();
        nt12.setType("{http://ex.org}nodeType_12");
        nt12.setId("12");
        nt12.setX("55");
        nt12.setY("105");

        TNodeTemplate nt13 = new TNodeTemplate();
        nt13.setType("{http://ex.org}nodeType_13");
        nt13.setId("13");
        nt13.setX("55");
        nt13.setY("205");

        TRelationshipTemplate rt1012 = new TRelationshipTemplate();
        rt1012.setType("{http://ex.org}relType_2");
        rt1012.setId("1012");
        rt1012.setSourceNodeTemplate(nt10);
        rt1012.setTargetNodeTemplate(nt12);

        TRelationshipTemplate rt1112 = new TRelationshipTemplate();
        rt1112.setType("{http://ex.org}relType_2");
        rt1112.setId("1112");
        rt1112.setSourceNodeTemplate(nt11);
        rt1112.setTargetNodeTemplate(nt12);

        TRelationshipTemplate rt1213 = new TRelationshipTemplate();
        rt1213.setType("{http://ex.org}relType_2");
        rt1213.setId("1213");
        rt1213.setSourceNodeTemplate(nt12);
        rt1213.setTargetNodeTemplate(nt13);

        /*
        ########        ########
        # (10) #        # (11) #
        ########        ########
            | (2)          | (2)
            +-------|------+
                   \/
                 ########
                 # (12) #
                 ########
                    | (2)
                   \/
                 ########
                 # (13) #
                 ########
         */
        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        refinementStructure.addNodeTemplate(nt10);
        refinementStructure.addNodeTemplate(nt11);
        refinementStructure.addNodeTemplate(nt12);
        refinementStructure.addNodeTemplate(nt13);
        refinementStructure.addRelationshipTemplate(rt1012);
        refinementStructure.addRelationshipTemplate(rt1112);
        refinementStructure.addRelationshipTemplate(rt1213);
        // endregion

        // region *** relation mapping ***
        OTRelationMapping rm1 = new OTRelationMapping();
        rm1.setDetectorNode(nt7);
        rm1.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm1.setDirection(OTRelationDirection.INGOING);
        rm1.setValidSourceOrTarget(QName.valueOf("{http://ex.org}nodeType_3"));
        rm1.setRefinementNode(nt11);

        OTRelationMapping rm2 = new OTRelationMapping();
        rm2.setDetectorNode(nt7);
        rm2.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm2.setDirection(OTRelationDirection.OUTGOING);
        rm2.setValidSourceOrTarget(QName.valueOf("{http://ex.org}nodeType_1"));
        rm2.setRefinementNode(nt10);
        // endregion

        OTPatternRefinementModel matchingPrm = new OTPatternRefinementModel();
        matchingPrm.setDetector(detector);
        matchingPrm.setRefinementTopology(refinementStructure);

        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(matchingPrm.getDetector());

        ToscaIsomorphismMatcher matcher = new ToscaIsomorphismMatcher();
        Iterator<GraphMapping<ToscaNode, ToscaEdge>> mappings = matcher.findMatches(detectorGraph, topologyGraph, new ToscaTypeMatcher());
        GraphMapping<ToscaNode, ToscaEdge> mapping = mappings.next();

        List<OTRelationMapping> relationMappings = new ArrayList<>();
        relationMappings.add(rm1);
        relationMappings.add(rm2);
        matchingPrm.setRelationMappings(relationMappings);

        candidate = new RefinementCandidate(matchingPrm, mapping, detectorGraph, 1);
        // endregion

        // region *** non-matching PRM **
        OTPatternRefinementModel nonMatchingPrm = new OTPatternRefinementModel();
        nonMatchingPrm.setDetector(detector);

        List<OTRelationMapping> relationMappingsNonMatchingPrm = new ArrayList<>();
        relationMappingsNonMatchingPrm.add(rm2);
        nonMatchingPrm.setRelationMappings(relationMappingsNonMatchingPrm);

        invalidCandidate = new RefinementCandidate(nonMatchingPrm, mapping, detectorGraph, 2);
        // endregion

        // region *** candidateWithDa ***
        ToscaGraph topologyGraph3 = ToscaTransformer.createTOSCAGraph(topology3);
        GraphMapping<ToscaNode, ToscaEdge> mapping3 = new ToscaIsomorphismMatcher()
            .findMatches(detectorGraph, topologyGraph3, new ToscaTypeMatcher())
            .next();
        
        OTDeploymentArtifactMapping deploymentArtifactMapping1 = new OTDeploymentArtifactMapping();
        deploymentArtifactMapping1.setId("daMap-1");
        deploymentArtifactMapping1.setArtifactType(artifactTypeFile);
        deploymentArtifactMapping1.setDetectorNode(nt7);
        deploymentArtifactMapping1.setRefinementNode(nt11);

        OTPatternRefinementModel matchingPrmWithDa = new OTPatternRefinementModel();
        matchingPrmWithDa.setDetector(detector);
        matchingPrmWithDa.setRefinementTopology(refinementStructure);
        matchingPrmWithDa.setDeploymentArtifactMappings(Collections.singletonList(deploymentArtifactMapping1));
        matchingPrmWithDa.setRelationMappings(relationMappings);
        
        candidateWithDa = new RefinementCandidate(matchingPrmWithDa, mapping3, detectorGraph,3);
        // endregion

        // region *** candidateWithnNotMatchingDa ***
        QName artifactTypeZip = new QName("zip", "http://example.org/tosca/at");

        OTDeploymentArtifactMapping deploymentArtifactMapping2 = new OTDeploymentArtifactMapping();
        deploymentArtifactMapping2.setId("daMap-1");
        deploymentArtifactMapping2.setArtifactType(artifactTypeZip);
        deploymentArtifactMapping2.setDetectorNode(nt7);
        deploymentArtifactMapping2.setRefinementNode(nt11);

        OTPatternRefinementModel nonMatchingPrmWithDa = new OTPatternRefinementModel();
        nonMatchingPrmWithDa.setDetector(detector);
        nonMatchingPrmWithDa.setRefinementTopology(refinementStructure);
        nonMatchingPrmWithDa.setDeploymentArtifactMappings(Collections.singletonList(deploymentArtifactMapping2));
        nonMatchingPrmWithDa.setRelationMappings(relationMappings);
        
        candidateWithnNotMatchingDa = new RefinementCandidate(nonMatchingPrmWithDa, mapping3, detectorGraph,4);
        // endregion
    }
}
