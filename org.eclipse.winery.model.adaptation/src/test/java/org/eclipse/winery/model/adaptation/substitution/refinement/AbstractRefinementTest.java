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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaTypeMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;

public class AbstractRefinementTest extends TestWithGitBackedRepository {

    protected static TTopologyTemplate topology;
    protected static TTopologyTemplate topology2;
    protected static TTopologyTemplate topology3;
    protected static TTopologyTemplate topology4;
    protected static RefinementCandidate candidateForTopology;
    protected static RefinementCandidate invalidCandidateForTopology;
    protected static RefinementCandidate candidateForTopology2;
    protected static RefinementCandidate secondValidCandidateForTopology2;
    protected static RefinementCandidate candidateForTopology3WithDa;
    protected static RefinementCandidate candidateForTopology3WithNotMatchingDa;
    protected static RefinementCandidate validCandidateForTopology4;
    protected static RefinementCandidate invalidCandidateForTopology4;

    protected static void setUp() {
        // region *** topology ***
        TNodeTemplate nt1 = new TNodeTemplate.Builder("1", QName.valueOf("{http://ex.org}nodeType_1"))
            .setX("10")
            .setY("10")
            .build();

        TNodeTemplate nt2 = new TNodeTemplate.Builder("2", QName.valueOf("{http://ex.org}nodeType_2"))
            .setX("110")
            .setY("10")
            .build();

        TNodeTemplate nt3 = new TNodeTemplate.Builder("3", QName.valueOf("{http://ex.org}nodeType_3"))
            .setX("210")
            .setY("10")
            .build();

        TNodeTemplate nt4 = new TNodeTemplate.Builder("4", QName.valueOf("{http://ex.org}nodeType_4"))
            .setX("110")
            .setY("110")
            .build();

        TRelationshipTemplate rt21 = new TRelationshipTemplate.Builder(
            "21",
            QName.valueOf("{http://ex.org}relType_1"),
            nt2,
            nt1
        ).build();

        TRelationshipTemplate rt32 = new TRelationshipTemplate.Builder(
            "32",
            QName.valueOf("{http://ex.org}relType_1"),
            nt3,
            nt2
        ).build();

        TRelationshipTemplate rt24 = new TRelationshipTemplate.Builder(
            "24",
            QName.valueOf("{http://ex.org}relType_2"),
            nt2,
            nt4
        ).build();

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
        topology = new TTopologyTemplate.Builder()
            .addNodeTemplate(nt1)
            .addNodeTemplate(nt2)
            .addNodeTemplate(nt3)
            .addNodeTemplate(nt4)
            .addRelationshipTemplate(rt21)
            .addRelationshipTemplate(rt32)
            .addRelationshipTemplate(rt24)
            .build();
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
        topology2 = new TTopologyTemplate.Builder()
            .addNodeTemplate(nt1)
            .addNodeTemplate(nt2)
            .addNodeTemplate(nt4)
            .addRelationshipTemplate(rt21)
            .addRelationshipTemplate(rt24)
            .build();
        // endregion

        // region *** topology 3 ***
        /*
        #######   (1)  #######
        # (1) # <----- # (2) #
        #######        #######
                          | (2)
                         \/
                       #######----|
                       # (4) # DA |
                       #######----|
         */
        QName artifactTypeFile = new QName("file", "http://example.org/tosca/at");
        QName test_da = new QName("test_da", "http://example.org/tosca/atemp/das");

        TNodeTemplate nt30 = new TNodeTemplate.Builder("30", QName.valueOf("{http://ex.org}nodeType_4"))
            .setX("110")
            .setY("110")
            .build();

        TRelationshipTemplate rt230 = new TRelationshipTemplate.Builder(
            "302",
            QName.valueOf("{http://ex.org}relType_2"),
            nt2,
            nt30
        ).build();
        
        TDeploymentArtifact da = new TDeploymentArtifact.Builder("test_da", artifactTypeFile)
          .setArtifactRef(test_da)
            .build();
        
        nt30.setDeploymentArtifacts(
            new ArrayList<>(Collections.singletonList(da))
        );

        topology3 = new TTopologyTemplate.Builder()
            .addNodeTemplate(nt1)
            .addNodeTemplate(nt2)
            .addNodeTemplate(nt30)
            .addRelationshipTemplate(rt21)
            .addRelationshipTemplate(rt230)
            .build();
        // endregion

        // region *** topology4 ***
        TRelationshipTemplate rt14 = new TRelationshipTemplate.Builder(
            "14",
            QName.valueOf("{http://ex.org}relType_1"),
            nt1,
            nt4
        ).build();
        
        /*
        #######  (1)  #######
        # (3) # -----># (2) #
        #######       #######
                         | (2)
                        \/
        #######  (1)  #######
        # (1) # -----># (4) #
        #######       #######
        */
        topology4 = new TTopologyTemplate.Builder()
            .addNodeTemplate(nt1)
            .addNodeTemplate(nt2)
            .addNodeTemplate(nt3)
            .addNodeTemplate(nt4)
            .addRelationshipTemplate(rt32)
            .addRelationshipTemplate(rt14)
            .addRelationshipTemplate(rt24)
            .build();
        // endregion

        // region *** matching PRM ***
        // region *** detector ***
        TNodeTemplate nt7 = new TNodeTemplate.Builder("7", QName.valueOf("{http://ex.org}nodeType_2"))
            .build();

        TNodeTemplate nt8 = new TNodeTemplate.Builder("8", QName.valueOf("{http://ex.org}nodeType_4"))
            .build();

        TRelationshipTemplate rt78 = new TRelationshipTemplate.Builder(
            "78",
            QName.valueOf("{http://ex.org}relType_2"),
            nt7,
            nt8
        ).build();

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
        TTopologyTemplate detector = new TTopologyTemplate.Builder()
            .addNodeTemplate(nt7)
            .addNodeTemplate(nt8)
            .addRelationshipTemplate(rt78)
            .build();
        // endregion

        // region *** refinement structure
        TNodeTemplate nt10 = new TNodeTemplate.Builder("10", QName.valueOf("{http://ex.org}nodeType_10"))
            .setX("5")
            .setY("10")
            .build();

        TNodeTemplate nt11 = new TNodeTemplate.Builder("11", QName.valueOf("{http://ex.org}nodeType_11"))
            .setX("105")
            .setY("5")
            .build();

        TNodeTemplate nt12 = new TNodeTemplate.Builder("12", QName.valueOf("{http://ex.org}nodeType_12"))
            .setX("55")
            .setY("105")
            .build();

        TNodeTemplate nt13 = new TNodeTemplate.Builder("13", QName.valueOf("{http://ex.org}nodeType_13"))
            .setX("55")
            .setY("205")
            .build();

        TRelationshipTemplate rt1012 = new TRelationshipTemplate.Builder(
            "1012",
            QName.valueOf("{http://ex.org}relType_2"),
            nt10,
            nt12
        ).build();

        TRelationshipTemplate rt1112 = new TRelationshipTemplate.Builder(
            "1112",
            QName.valueOf("{http://ex.org}relType_2"),
            nt11,
            nt12
        ).build();

        TRelationshipTemplate rt1213 = new TRelationshipTemplate.Builder(
            "1213",
            QName.valueOf("{http://ex.org}relType_2"),
            nt12,
            nt13
        ).build();

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
        TTopologyTemplate refinementStructure = new TTopologyTemplate.Builder()
            .addNodeTemplate(nt10)
            .addNodeTemplate(nt11)
            .addNodeTemplate(nt12)
            .addNodeTemplate(nt13)
            .addRelationshipTemplate(rt1012)
            .addRelationshipTemplate(rt1112)
            .addRelationshipTemplate(rt1213)
            .build();
        // endregion

        // region *** relation mapping ***
        OTRelationMapping rm1 = new OTRelationMapping(new OTRelationMapping.Builder());
        rm1.setDetectorElement(nt7);
        rm1.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm1.setDirection(OTRelationDirection.INGOING);
        rm1.setValidSourceOrTarget(QName.valueOf("{http://ex.org}nodeType_3"));
        rm1.setRefinementElement(nt11);

        OTRelationMapping rm2 = new OTRelationMapping(new OTRelationMapping.Builder());
        rm2.setDetectorElement(nt7);
        rm2.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm2.setDirection(OTRelationDirection.OUTGOING);
        rm2.setValidSourceOrTarget(QName.valueOf("{http://ex.org}nodeType_1"));
        rm2.setRefinementElement(nt10);
        // endregion

        OTPatternRefinementModel prmWithNt2HostedOnNt4AndIngoingRt1AtNt2AndOutgoingRt1AtNt2 = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder());
        prmWithNt2HostedOnNt4AndIngoingRt1AtNt2AndOutgoingRt1AtNt2.setDetector(detector);
        prmWithNt2HostedOnNt4AndIngoingRt1AtNt2AndOutgoingRt1AtNt2.setRefinementTopology(refinementStructure);

        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(detector);

        ToscaIsomorphismMatcher matcher = new ToscaIsomorphismMatcher();
        Iterator<GraphMapping<ToscaNode, ToscaEdge>> mappings = matcher.findMatches(detectorGraph, topologyGraph, new ToscaTypeMatcher());
        GraphMapping<ToscaNode, ToscaEdge> detectorWithTopologyMapping = mappings.next();

        List<OTRelationMapping> relationMappings = new ArrayList<>();
        relationMappings.add(rm1);
        relationMappings.add(rm2);
        prmWithNt2HostedOnNt4AndIngoingRt1AtNt2AndOutgoingRt1AtNt2.setRelationMappings(relationMappings);

        candidateForTopology = new RefinementCandidate(prmWithNt2HostedOnNt4AndIngoingRt1AtNt2AndOutgoingRt1AtNt2,
            detectorWithTopologyMapping, detectorGraph, 1);
        // endregion

        // region *** invalidCandidateForTopology **
        OTPatternRefinementModel prmWithNt2HostedOnNt4AndOutgoingRt1AtNt2 = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder());
        prmWithNt2HostedOnNt4AndOutgoingRt1AtNt2.setDetector(detector);

        List<OTRelationMapping> relationMappingsNonMatchingPrm = new ArrayList<>();
        relationMappingsNonMatchingPrm.add(rm2);
        prmWithNt2HostedOnNt4AndOutgoingRt1AtNt2.setRelationMappings(relationMappingsNonMatchingPrm);

        invalidCandidateForTopology = new RefinementCandidate(prmWithNt2HostedOnNt4AndOutgoingRt1AtNt2, detectorWithTopologyMapping,
            detectorGraph, 2);
        // endregion

        // region *** Candidates for Topology 2 ***
        ToscaGraph topology2Graph = ToscaTransformer.createTOSCAGraph(topology2);
        GraphMapping<ToscaNode, ToscaEdge> detectorWithTopology2Mapping = matcher
            .findMatches(detectorGraph, topology2Graph, new ToscaTypeMatcher())
            .next();

        candidateForTopology2 = new RefinementCandidate(prmWithNt2HostedOnNt4AndIngoingRt1AtNt2AndOutgoingRt1AtNt2,
            detectorWithTopology2Mapping, detectorGraph, 3);
        secondValidCandidateForTopology2 = new RefinementCandidate(prmWithNt2HostedOnNt4AndOutgoingRt1AtNt2, detectorWithTopology2Mapping, detectorGraph, 4);
        // endregion

        // region *** Candidates for Topology 3 ***
        ToscaGraph topologyGraph3 = ToscaTransformer.createTOSCAGraph(topology3);
        GraphMapping<ToscaNode, ToscaEdge> detectorToTopology3Mapping = new ToscaIsomorphismMatcher()
            .findMatches(detectorGraph, topologyGraph3, new ToscaTypeMatcher())
            .next();

        OTDeploymentArtifactMapping deploymentArtifactMapping1 = new OTDeploymentArtifactMapping(new OTDeploymentArtifactMapping.Builder());
        deploymentArtifactMapping1.setId("daMap-1");
        deploymentArtifactMapping1.setArtifactType(artifactTypeFile);
        deploymentArtifactMapping1.setDetectorElement(nt8);
        deploymentArtifactMapping1.setRefinementElement(nt13);

        OTPatternRefinementModel matchingPrmWithDa = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder());
        matchingPrmWithDa.setDetector(detector);
        matchingPrmWithDa.setRefinementTopology(refinementStructure);
        matchingPrmWithDa.setDeploymentArtifactMappings(Collections.singletonList(deploymentArtifactMapping1));
        matchingPrmWithDa.setRelationMappings(relationMappings);

        candidateForTopology3WithDa = new RefinementCandidate(matchingPrmWithDa, detectorToTopology3Mapping, detectorGraph, 5);

        QName artifactTypeZip = new QName("zip", "http://example.org/tosca/at");

        OTDeploymentArtifactMapping deploymentArtifactMapping2 = new OTDeploymentArtifactMapping(new OTDeploymentArtifactMapping.Builder());
        deploymentArtifactMapping2.setId("daMap-1");
        deploymentArtifactMapping2.setArtifactType(artifactTypeZip);
        deploymentArtifactMapping2.setDetectorElement(nt7);
        deploymentArtifactMapping2.setRefinementElement(nt11);

        OTPatternRefinementModel nonMatchingPrmWithDa = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder());
        nonMatchingPrmWithDa.setDetector(detector);
        nonMatchingPrmWithDa.setRefinementTopology(refinementStructure);
        nonMatchingPrmWithDa.setDeploymentArtifactMappings(Collections.singletonList(deploymentArtifactMapping2));
        nonMatchingPrmWithDa.setRelationMappings(relationMappings);

        candidateForTopology3WithNotMatchingDa = new RefinementCandidate(nonMatchingPrmWithDa, detectorToTopology3Mapping, detectorGraph, 6);
        // endregion

        // region *** Candidates for Topology 4 ***
        ToscaGraph topology4Graph = ToscaTransformer.createTOSCAGraph(topology4);
        GraphMapping<ToscaNode, ToscaEdge> detectorWithTopology4Mapping = matcher
            .findMatches(detectorGraph, topology4Graph, new ToscaTypeMatcher())
            .next();

        OTRelationMapping rm3 = new OTRelationMapping(new OTRelationMapping.Builder());
        rm3.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm3.setDirection(OTRelationDirection.INGOING);
        rm3.setDetectorElement(nt7);
        rm3.setRefinementElement(nt11);

        OTPatternRefinementModel prmWithNT2HostedOnNT4AndIngoingRT1AtNT2 = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder());
        prmWithNT2HostedOnNT4AndIngoingRT1AtNT2.setDetector(detector);
        prmWithNT2HostedOnNT4AndIngoingRT1AtNT2.setRefinementTopology(refinementStructure);
        prmWithNT2HostedOnNT4AndIngoingRT1AtNT2.setRelationMappings(Collections.singletonList(rm3));

        invalidCandidateForTopology4 = new RefinementCandidate(prmWithNT2HostedOnNT4AndIngoingRT1AtNT2,
            detectorWithTopology4Mapping, detectorGraph, 7);

        OTRelationMapping rm4 = new OTRelationMapping(new OTRelationMapping.Builder());
        rm4.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm4.setDirection(OTRelationDirection.INGOING);
        rm4.setDetectorElement(nt8);
        rm4.setRefinementElement(nt13);

        OTPatternRefinementModel prmWithNT2HostedOnNT4AndIngoingRT1AtNT2AndIngoingRT1AtNT4 = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder());
        prmWithNT2HostedOnNT4AndIngoingRT1AtNT2AndIngoingRT1AtNT4.setDetector(detector);
        prmWithNT2HostedOnNT4AndIngoingRT1AtNT2AndIngoingRT1AtNT4.setRefinementTopology(refinementStructure);
        prmWithNT2HostedOnNT4AndIngoingRT1AtNT2AndIngoingRT1AtNT4.setRelationMappings(Arrays.asList(rm3, rm4));

        validCandidateForTopology4 = new RefinementCandidate(prmWithNT2HostedOnNT4AndIngoingRT1AtNT2AndIngoingRT1AtNT4,
            detectorWithTopology4Mapping, detectorGraph, 8);
        // endregion
    }
}
