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

package org.eclipse.winery.model.adaptation.substitution.refinement.patterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.substitution.refinement.DefaultRefinementChooser;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.tosca.AttributeMapping;
import org.eclipse.winery.model.tosca.TAttributeMappingType;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TPrmModelElementType;
import org.eclipse.winery.model.tosca.TRelationDirection;
import org.eclipse.winery.model.tosca.TRelationMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TStayMapping;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaTypeMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import com.google.common.collect.Iterators;
import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternRefinementTest {

    private static TTopologyTemplate topology;
    private static TTopologyTemplate topology2;
    private static RefinementCandidate candidate;
    private static RefinementCandidate invalidCandidate;

    private static void setUp() {
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
        TRelationMapping rm1 = new TRelationMapping();
        rm1.setDetectorNode(nt7);
        rm1.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm1.setDirection(TRelationDirection.INGOING);
        rm1.setValidSourceOrTarget(QName.valueOf("{http://ex.org}nodeType_3"));
        rm1.setRefinementNode(nt11);

        TRelationMapping rm2 = new TRelationMapping();
        rm2.setDetectorNode(nt7);
        rm2.setRelationType(QName.valueOf("{http://ex.org}relType_1"));
        rm2.setDirection(TRelationDirection.OUTGOING);
        rm2.setValidSourceOrTarget(QName.valueOf("{http://ex.org}nodeType_1"));
        rm2.setRefinementNode(nt10);
        // endregion

        TPatternRefinementModel matchingPrm = new TPatternRefinementModel();
        matchingPrm.setDetector(detector);
        matchingPrm.setRefinementTopology(refinementStructure);

        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(matchingPrm.getDetector());

        ToscaIsomorphismMatcher matcher = new ToscaIsomorphismMatcher();
        Iterator<GraphMapping<ToscaNode, ToscaEdge>> mappings = matcher.findMatches(detectorGraph, topologyGraph, new ToscaTypeMatcher());
        GraphMapping<ToscaNode, ToscaEdge> mapping = mappings.next();

        List<TRelationMapping> relationMappings = new ArrayList<TRelationMapping>();
        relationMappings.add(rm1);
        relationMappings.add(rm2);
        matchingPrm.setRelationMappings(relationMappings);

        candidate = new RefinementCandidate(matchingPrm, mapping, detectorGraph, 1);
        // endregion

        // region *** non-matching PRM **
        TPatternRefinementModel nonMatchingPrm = new TPatternRefinementModel();
        nonMatchingPrm.setDetector(detector);

        List<TRelationMapping> relationMappings1 = new ArrayList<>();
        relationMappings1.add(rm2);
        nonMatchingPrm.setRelationMappings(relationMappings1);

        invalidCandidate = new RefinementCandidate(nonMatchingPrm, mapping, detectorGraph, 2);
        // endregion
    }

    // region ********** isApplicable() **********
    @ParameterizedTest(name = "{index} => ''{3}''")
    @MethodSource("getIsApplicableArguments")
    void testIsApplicable(RefinementCandidate refinementCandidate, TTopologyTemplate topologyTemplate, boolean expected, String description) {
        PatternRefinement patternRefinement = new PatternRefinement(new DefaultRefinementChooser());
        assertEquals(expected, patternRefinement.isApplicable(refinementCandidate, topologyTemplate));
    }

    private static Stream<Arguments> getIsApplicableArguments() {
        setUp();
        return Stream.of(
            Arguments.of(candidate, topology, true, "Expect applicable PRM"),
            Arguments.of(invalidCandidate, topology, false, "Expect inapplicable PRM"),
            Arguments.of(candidate, topology2, true, "Expect applicable PRM"),
            Arguments.of(invalidCandidate, topology2, true, "Expect applicable PRM")
        );
    }
    // endregion

    // region ********** applyRefinement() **********
    @Test
    public void testApplyRefinementToTopology() {
        setUp();

         /*
        input:
        #######   (1)  #######   (1)  #######
        # (1) # <----- # (2) # <----- # (3) #
        #######        #######        #######
                          | (2)
                         \/
                       #######
                       # (4) #
                       #######

        expected output:
        #######   (1)  ########        ########   (1)  #######
        # (1) # <----- # (10) #        # (11) # <----- # (3) #
        #######        ########        ########        #######
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
        PatternRefinement patternRefinement = new PatternRefinement();
        patternRefinement.applyRefinement(candidate, topology);

        // static elements
        assertTrue(Objects.nonNull(topology.getNodeTemplate("1")));
        assertTrue(Objects.nonNull(topology.getNodeTemplate("3")));
        assertTrue(Objects.nonNull(topology.getRelationshipTemplate("21")));
        assertTrue(Objects.nonNull(topology.getRelationshipTemplate("32")));

        // added elements
        assertTrue(Objects.nonNull(topology.getNodeTemplate("10")));
        assertTrue(Objects.nonNull(topology.getNodeTemplate("11")));
        assertTrue(Objects.nonNull(topology.getNodeTemplate("12")));
        assertTrue(Objects.nonNull(topology.getNodeTemplate("13")));
        assertTrue(Objects.nonNull(topology.getRelationshipTemplate("1012")));
        assertTrue(Objects.nonNull(topology.getRelationshipTemplate("1112")));
        assertTrue(Objects.nonNull(topology.getRelationshipTemplate("1213")));

        // deleted elements
        assertTrue(Objects.isNull(topology.getNodeTemplate("2")));
        assertTrue(Objects.isNull(topology.getNodeTemplate("4")));
        assertTrue(Objects.isNull(topology.getRelationshipTemplate("24")));

        // changes
        assertEquals("10", topology.getRelationshipTemplate("21").getSourceElement().getRef().getId());
        assertEquals("11", topology.getRelationshipTemplate("32").getTargetElement().getRef().getId());
        assertEquals(11, topology.getNodeTemplateOrRelationshipTemplate().size());
    }

    @Test
    public void testApplyRefinementToTopology2() {
        setUp();

         /*
        input:
        #######   (1)  #######
        # (1) # <----- # (2) #
        #######        #######
                          | (2)
                         \/
                       #######
                       # (4) #
                       #######

        expected output:
        #######   (1)  ########        ########
        # (1) # <----- # (10) #        # (11) #
        #######        ########        ########
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
        PatternRefinement patternRefinement = new PatternRefinement();
        patternRefinement.applyRefinement(candidate, topology2);

        // static elements
        assertTrue(Objects.nonNull(topology2.getNodeTemplate("1")));
        assertTrue(Objects.nonNull(topology2.getRelationshipTemplate("21")));

        // added elements
        assertTrue(Objects.nonNull(topology2.getNodeTemplate("10")));
        assertTrue(Objects.nonNull(topology2.getNodeTemplate("11")));
        assertTrue(Objects.nonNull(topology2.getNodeTemplate("12")));
        assertTrue(Objects.nonNull(topology2.getNodeTemplate("13")));
        assertTrue(Objects.nonNull(topology2.getRelationshipTemplate("1012")));
        assertTrue(Objects.nonNull(topology2.getRelationshipTemplate("1112")));
        assertTrue(Objects.nonNull(topology2.getRelationshipTemplate("1213")));

        // deleted elements
        assertTrue(Objects.isNull(topology2.getNodeTemplate("2")));
        assertTrue(Objects.isNull(topology2.getNodeTemplate("4")));
        assertTrue(Objects.isNull(topology2.getRelationshipTemplate("24")));

        // changes
        assertEquals("10", topology2.getRelationshipTemplate("21").getSourceElement().getRef().getId());
        assertEquals(9, topology2.getNodeTemplateOrRelationshipTemplate().size());
    }
    // endregion

    // region ********** getExternalRelations **********

    @Test
    public void getExternalRelations() {
        setUp();

         /*
        input:
        #######   (1)  #######   (1)  #######
        # (1) # <===== # (2) # <===== # (3) #
        #######        #######        #######
                          | (2)
                         \/
                       #######
                       # (4) #
                       #######
         
         expect the algorithm to find the bold (==) relations
         */
        PatternRefinement patternRefinement = new PatternRefinement();
        ArrayList<ToscaNode> matchingNodes = new ArrayList<>();
        Iterators.addAll(matchingNodes, candidate.getDetectorGraph().vertexSet().iterator());

        assertEquals(2, matchingNodes.size());

        TNodeTemplate nt2 = candidate.getGraphMapping().getVertexCorrespondence(matchingNodes.get(0), false).getTemplate();
        List<TRelationshipTemplate> externalRelationsOf2 = patternRefinement.getExternalRelations(nt2, candidate, topology)
            .collect(Collectors.toList());
        assertEquals(2, externalRelationsOf2.size());

        TNodeTemplate nt4 = matchingNodes.get(1).getTemplate();
        List<TRelationshipTemplate> externalRelationsOf4 = patternRefinement.getExternalRelations(nt4, candidate, topology)
            .collect(Collectors.toList());
        assertEquals(0, externalRelationsOf4.size());
    }

    // endregion

    // region ********** applyPropertyMappings **********
    @Test
    public void applyPropertyMappings() {
        setUp();

        // region *** Graphical description ***
        /*
        input:
        #######   (1)  #########   (1)  #######    ########        ########
        # (1) # <----- #  (2)  # <----- # (3) #    # (10) #        # (11) #
        #######        #########        #######    ########        ########
                       #  p=1  #                       |           # k=   #
                       #  x=2 #                        |           ########
                       #########                       | (2)          | (2)
                           | (2)                       +-------|------+
                          \/                                  \/
                        #######                            ########
                        # (4) #                            # (12) #
                        #######                            ########
                        # a=3 #                            # j=   #
                        # b=4 #                            ########
                        #######                                | (2)
                                                              \/
                                                           ########
                                                           # (13) #
                                                           ########
                                                           # a=   #
                                                           # b=   #
                                                           # c=0  #
                                                           ########
        expected output
        #######   (1)  #########   (1)  #######    ########        ########
        # (1) # <----- #  (2)  # <----- # (3) #    # (10) #        # (11) #
        #######        #########        #######    ########        ########
                       #  p=1  #                       |           # k=2  #
                       #  x=2 #                        |           ########
                       #########                       | (2)          | (2)
                           | (2)                       +-------|------+
                          \/                                  \/
                        #######                            ########
                        # (4) #                            # (12) #
                        #######                            ########
                        # a=3 #                            # j=1  #
                        # b=4 #                            ########
                        #######                                | (2)
                                                              \/
                                                           ########
                                                           # (13) #
                                                           ########
                                                           # a=3  #
                                                           # b=4  #
                                                           # c=0  #
                                                           ######## */
        // endregion

        // region *** setup the PRM ***
        TNodeTemplate nt13 = candidate.getRefinementModel().getRefinementTopology().getNodeTemplate("13");
        TEntityTemplate.Properties nt13Props = new TEntityTemplate.Properties();
        HashMap<String, String> nt13PropsMap = new HashMap<>();
        nt13PropsMap.put("a", null);
        nt13PropsMap.put("b", null);
        nt13PropsMap.put("c", "0");
        nt13Props.setKVProperties(nt13PropsMap);
        nt13.setProperties(nt13Props);

        TNodeTemplate nt12 = candidate.getRefinementModel().getRefinementTopology().getNodeTemplate("12");
        TEntityTemplate.Properties nt12Props = new TEntityTemplate.Properties();
        HashMap<String, String> nt12PropsMap = new HashMap<>();
        nt12PropsMap.put("j", null);
        nt12Props.setKVProperties(nt12PropsMap);
        nt12.setProperties(nt12Props);

        TNodeTemplate nt11 = candidate.getRefinementModel().getRefinementTopology().getNodeTemplate("11");
        TEntityTemplate.Properties nt11Props = new TEntityTemplate.Properties();
        HashMap<String, String> nt11PropsMap = new HashMap<>();
        nt11PropsMap.put("k", null);
        nt11Props.setKVProperties(nt11PropsMap);
        nt11.setProperties(nt11Props);

        AttributeMapping allOn4to13 = new AttributeMapping();
        allOn4to13.setType(TAttributeMappingType.ALL);
        allOn4to13.setDetectorNode(candidate.getRefinementModel().getDetector().getNodeTemplate("8"));
        allOn4to13.setRefinementNode(nt13);

        AttributeMapping pIn2_to_jIn12 = new AttributeMapping();
        pIn2_to_jIn12.setType(TAttributeMappingType.SELECTIVE);
        pIn2_to_jIn12.setDetectorNode(candidate.getRefinementModel().getDetector().getNodeTemplate("7"));
        pIn2_to_jIn12.setRefinementNode(nt12);
        pIn2_to_jIn12.setDetectorProperty("p");
        pIn2_to_jIn12.setRefinementProperty("j");

        AttributeMapping xIn2_to_kIn11 = new AttributeMapping();
        xIn2_to_kIn11.setType(TAttributeMappingType.SELECTIVE);
        xIn2_to_kIn11.setDetectorNode(candidate.getRefinementModel().getDetector().getNodeTemplate("7"));
        xIn2_to_kIn11.setRefinementNode(nt11);
        xIn2_to_kIn11.setDetectorProperty("x");
        xIn2_to_kIn11.setRefinementProperty("k");

        List<AttributeMapping> relationMappings = new ArrayList<>();
        relationMappings.add(allOn4to13);
        relationMappings.add(pIn2_to_jIn12);
        relationMappings.add(xIn2_to_kIn11);

        ((TPatternRefinementModel) candidate.getRefinementModel()).setAttributeMappings(relationMappings);
        // endregion

        // region *** setup the topology ***
        TNodeTemplate nt2 = topology.getNodeTemplate("2");
        TEntityTemplate.Properties nt2Props = new TEntityTemplate.Properties();
        HashMap<String, String> nt2PropsMap = new HashMap<>();
        nt2PropsMap.put("p", "1");
        nt2PropsMap.put("x", "2");
        nt2Props.setKVProperties(nt2PropsMap);
        nt2.setProperties(nt2Props);

        TNodeTemplate nt4 = topology.getNodeTemplate("4");
        TEntityTemplate.Properties nt4Props = new TEntityTemplate.Properties();
        HashMap<String, String> nt4PropsMap = new HashMap<>();
        nt4PropsMap.put("a", "3");
        nt4PropsMap.put("b", "4");
        nt4Props.setKVProperties(nt4PropsMap);
        nt4.setProperties(nt4Props);

        Map<String, String> idMapping = BackendUtils.mergeTopologyTemplateAinTopologyTemplateB(
            candidate.getRefinementModel().getRefinementTopology(),
            topology
        );
        // endregion

        PatternRefinement patternRefinement = new PatternRefinement();

        patternRefinement.applyPropertyMappings(candidate, "8", nt4, topology, idMapping);
        assertEquals(3, topology.getNodeTemplate("13").getProperties().getKVProperties().size());
        assertEquals("3", topology.getNodeTemplate("13").getProperties().getKVProperties().get("a"));
        assertEquals("4", topology.getNodeTemplate("13").getProperties().getKVProperties().get("b"));
        assertEquals("0", topology.getNodeTemplate("13").getProperties().getKVProperties().get("c"));

        patternRefinement.applyPropertyMappings(candidate, "7", nt2, topology, idMapping);
        assertEquals(1, topology.getNodeTemplate("11").getProperties().getKVProperties().size());
        assertEquals("2", topology.getNodeTemplate("11").getProperties().getKVProperties().get("k"));
        assertEquals(1, topology.getNodeTemplate("12").getProperties().getKVProperties().size());
        assertEquals("1", topology.getNodeTemplate("12").getProperties().getKVProperties().get("j"));
        assertNull(topology.getNodeTemplate("10").getProperties());
    }
    // endregion

    // region ********** redirectStayMappings **********
    @Test
    void redirectStayMappings() {
        setUp();

         /*
        input:
        #######   (1)  #######
        # (1) # <----- # (2) #
        #######        #######
                          | (2)
                         \/
                       #######
                       # (4) #
                       #######

        expected output:
        #######   (1)  ########        ########
        # (1) # <----- # (10) #        # (11) #
        #######        ########        ########
                           | (2)          | (2)
                           +-------|------+
                                  \/
                                #######
                                # (4) #
                                #######
         */

        // region *** add stay mapping to PRM ***
        TTopologyTemplate refinementTopology = candidate.getRefinementModel().getRefinementTopology();
        TTopologyTemplate detector = candidate.getRefinementModel().getDetector();

        refinementTopology.getNodeTemplateOrRelationshipTemplate()
            .removeIf(template -> {
                if (template.getId().equals("13") || template.getId().equals("1213")) {
                    return true;
                }
                return false;
            });

        TNodeTemplate nt12 = refinementTopology.getNodeTemplate("12");
        nt12.setType("{http://ex.org}nodeType_4");
        TNodeTemplate nt4 = detector.getNodeTemplate("8");

        TStayMapping nt4staysAsNt12 = new TStayMapping();
        nt4staysAsNt12.setModelElementType(TPrmModelElementType.NODE);
        nt4staysAsNt12.setId("stay1");
        nt4staysAsNt12.setDetectorNode(nt4);
        nt4staysAsNt12.setRefinementNode(nt12);
        ((TPatternRefinementModel) candidate.getRefinementModel())
            .setStayMappings(Collections.singletonList(nt4staysAsNt12));
        // endregion

        // recreate the candidate
        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology2);
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(detector);

        GraphMapping<ToscaNode, ToscaEdge> mapping = new ToscaIsomorphismMatcher()
            .findMatches(detectorGraph, topologyGraph, new ToscaTypeMatcher())
            .next();

        candidate = new RefinementCandidate(candidate.getRefinementModel(), mapping, detectorGraph, 1);

        PatternRefinement patternRefinement = new PatternRefinement();
        patternRefinement.applyRefinement(candidate, topology2);

        // region *** assertions ***
        assertNotNull(topology2.getNodeTemplate("4"));
        assertEquals(topology2.getRelationshipTemplate("1012").getTargetElement().getRef().getId(), "4");
        assertEquals(topology2.getRelationshipTemplate("1012").getSourceElement().getRef().getId(), "10");

        assertEquals(topology2.getRelationshipTemplate("1112").getTargetElement().getRef().getId(), "4");
        assertEquals(topology2.getRelationshipTemplate("1112").getSourceElement().getRef().getId(), "11");

        assertEquals(topology2.getRelationshipTemplate("21").getTargetElement().getRef().getId(), "1");
        assertEquals(topology2.getRelationshipTemplate("21").getSourceElement().getRef().getId(), "10");
        // endregion
    }

    // endregion
}
