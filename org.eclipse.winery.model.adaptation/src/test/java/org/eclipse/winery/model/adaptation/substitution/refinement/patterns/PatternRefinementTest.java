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

package org.eclipse.winery.model.adaptation.substitution.refinement.patterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.substitution.refinement.AbstractRefinementTest;
import org.eclipse.winery.model.adaptation.substitution.refinement.DefaultRefinementChooser;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMappingType;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
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

class PatternRefinementTest extends AbstractRefinementTest {

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
            Arguments.of(candidateForTopology, topology, true, "Expect applicable PRM"),
            Arguments.of(invalidCandidateForTopology, topology, false, "Expect inapplicable PRM"),
            Arguments.of(candidateForTopology2, topology2, true, "Expect applicable PRM"),
            Arguments.of(secondValidCandidateForTopology2, topology2, true, "Expect applicable PRM"),
            Arguments.of(candidateForTopology3WithDa, topology3, true, "Expect applicable PRM with compatible DA"),
            Arguments.of(candidateForTopology3WithNotMatchingDa, topology3, false, "Expect inapplicable PRM because of incompatible DA type"),
            Arguments.of(invalidCandidateForTopology4, topology4, false, "Expect inapplicable PRM because of not redirectable relations"),
            Arguments.of(validCandidateForTopology4, topology4, true, "Expect applicable PRM with multiple redirectable relations")
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
        patternRefinement.applyRefinement(candidateForTopology, topology);

        // static elements
        TRelationshipTemplate relation21 = topology.getRelationshipTemplate("21");
        TRelationshipTemplate relation32 = topology.getRelationshipTemplate("32");

        assertTrue(Objects.nonNull(topology.getNodeTemplate("1")));
        assertTrue(Objects.nonNull(topology.getNodeTemplate("3")));
        assertTrue(Objects.nonNull(relation21));
        assertTrue(Objects.nonNull(relation32));

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
        assertEquals("10", relation21.getSourceElement().getRef().getId());
        assertEquals("11", relation32.getTargetElement().getRef().getId());
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
        patternRefinement.applyRefinement(candidateForTopology, topology2);

        // static elements
        assertTrue(Objects.nonNull(topology2.getNodeTemplate("1")));

        TRelationshipTemplate relation21 = topology2.getRelationshipTemplate("21");
        assertTrue(Objects.nonNull(relation21));

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
        assertNotNull(relation21);
        assertEquals("10", relation21.getSourceElement().getRef().getId());
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
        Iterators.addAll(matchingNodes, candidateForTopology.getDetectorGraph().vertexSet().iterator());

        assertEquals(2, matchingNodes.size());

        TNodeTemplate nt2 = candidateForTopology.getGraphMapping().getVertexCorrespondence(matchingNodes.get(0), false).getTemplate();
        List<TRelationshipTemplate> externalRelationsOf2 = patternRefinement.getExternalRelations(nt2, candidateForTopology, topology)
            .collect(Collectors.toList());
        assertEquals(2, externalRelationsOf2.size());

        TNodeTemplate nt4 = matchingNodes.get(1).getTemplate();
        List<TRelationshipTemplate> externalRelationsOf4 = patternRefinement.getExternalRelations(nt4, candidateForTopology, topology)
            .collect(Collectors.toList());
        assertEquals(0, externalRelationsOf4.size());
    }

    // endregion

    // region ********** applyPropertyMappings **********
    @Test
    public void applyPropertyMappings() {
        setUp();

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

        // region *** setup the PRM ***
        TNodeTemplate nt13 = candidateForTopology.getRefinementModel().getRefinementTopology().getNodeTemplate("13");
        assert nt13 != null;
        TEntityTemplate.WineryKVProperties nt13Props = new TEntityTemplate.WineryKVProperties();
        LinkedHashMap<String, String> nt13PropsMap = new LinkedHashMap<>();
        nt13PropsMap.put("a", null);
        nt13PropsMap.put("b", null);
        nt13PropsMap.put("c", "0");
        nt13Props.setKVProperties(nt13PropsMap);
        nt13.setProperties(nt13Props);

        TNodeTemplate nt12 = candidateForTopology.getRefinementModel().getRefinementTopology().getNodeTemplate("12");
        assert nt12 != null;
        TEntityTemplate.WineryKVProperties nt12Props = new TEntityTemplate.WineryKVProperties();
        LinkedHashMap<String, String> nt12PropsMap = new LinkedHashMap<>();
        nt12PropsMap.put("j", null);
        nt12Props.setKVProperties(nt12PropsMap);
        nt12.setProperties(nt12Props);

        TNodeTemplate nt11 = candidateForTopology.getRefinementModel().getRefinementTopology().getNodeTemplate("11");
        assert nt11 != null;
        TEntityTemplate.WineryKVProperties nt11Props = new TEntityTemplate.WineryKVProperties();
        LinkedHashMap<String, String> nt11PropsMap = new LinkedHashMap<>();
        nt11PropsMap.put("k", null);
        nt11Props.setKVProperties(nt11PropsMap);
        nt11.setProperties(nt11Props);

        OTAttributeMapping allOn4to13 = new OTAttributeMapping(new OTAttributeMapping.Builder());
        allOn4to13.setType(OTAttributeMappingType.ALL);
        allOn4to13.setDetectorElement(candidateForTopology.getRefinementModel().getDetector().getNodeTemplate("8"));
        allOn4to13.setRefinementElement(nt13);

        OTAttributeMapping pIn2_to_jIn12 = new OTAttributeMapping(new OTAttributeMapping.Builder());
        pIn2_to_jIn12.setType(OTAttributeMappingType.SELECTIVE);
        pIn2_to_jIn12.setDetectorElement(candidateForTopology.getRefinementModel().getDetector().getNodeTemplate("7"));
        pIn2_to_jIn12.setRefinementElement(nt12);
        pIn2_to_jIn12.setDetectorProperty("p");
        pIn2_to_jIn12.setRefinementProperty("j");

        OTAttributeMapping xIn2_to_kIn11 = new OTAttributeMapping(new OTAttributeMapping.Builder());
        xIn2_to_kIn11.setType(OTAttributeMappingType.SELECTIVE);
        xIn2_to_kIn11.setDetectorElement(candidateForTopology.getRefinementModel().getDetector().getNodeTemplate("7"));
        xIn2_to_kIn11.setRefinementElement(nt11);
        xIn2_to_kIn11.setDetectorProperty("x");
        xIn2_to_kIn11.setRefinementProperty("k");

        List<OTAttributeMapping> relationMappings = new ArrayList<>();
        relationMappings.add(allOn4to13);
        relationMappings.add(pIn2_to_jIn12);
        relationMappings.add(xIn2_to_kIn11);

        ((OTPatternRefinementModel) candidateForTopology.getRefinementModel()).setAttributeMappings(relationMappings);
        // endregion

        // region *** setup the topology ***
        TNodeTemplate nt2 = topology.getNodeTemplate("2");
        assert nt2 != null;
        TEntityTemplate.WineryKVProperties nt2Props = new TEntityTemplate.WineryKVProperties();
        LinkedHashMap<String, String> nt2PropsMap = new LinkedHashMap<>();
        nt2PropsMap.put("p", "1");
        nt2PropsMap.put("x", "2");
        nt2Props.setKVProperties(nt2PropsMap);
        nt2.setProperties(nt2Props);

        TNodeTemplate nt4 = topology.getNodeTemplate("4");
        assert nt4 != null;
        TEntityTemplate.WineryKVProperties nt4Props = new TEntityTemplate.WineryKVProperties();
        LinkedHashMap<String, String> nt4PropsMap = new LinkedHashMap<>();
        nt4PropsMap.put("a", "3");
        nt4PropsMap.put("b", "4");
        nt4Props.setKVProperties(nt4PropsMap);
        nt4.setProperties(nt4Props);

        Map<String, String> idMapping = BackendUtils.mergeTopologyTemplateAinTopologyTemplateB(
            candidateForTopology.getRefinementModel().getRefinementTopology(),
            topology
        );
        // endregion

        PatternRefinement patternRefinement = new PatternRefinement();

        patternRefinement.applyPropertyMappings(candidateForTopology, "8", nt4, topology, idMapping);

        TNodeTemplate node13 = topology.getNodeTemplate("13");
        assertNotNull(node13);
        Map<String, String> properties13 = ModelUtilities.getPropertiesKV(node13);
        assertNotNull(properties13);
        assertEquals(3, properties13.size());
        assertEquals("3", properties13.get("a"));
        assertEquals("4", properties13.get("b"));
        assertEquals("0", properties13.get("c"));

        patternRefinement.applyPropertyMappings(candidateForTopology, "7", nt2, topology, idMapping);
        TNodeTemplate node11 = topology.getNodeTemplate("11");
        assertNotNull(node11);
        Map<String, String> properties11 = ModelUtilities.getPropertiesKV(node11);
        TNodeTemplate node12 = topology.getNodeTemplate("12");
        assertNotNull(node12);
        Map<String, String> properties12 = ModelUtilities.getPropertiesKV(node12);

        assertNotNull(properties11);
        assertEquals(1, properties11.size());
        assertEquals("2", properties11.get("k"));
        assertNotNull(properties12);
        assertEquals(1, properties12.size());
        assertEquals("1", properties12.get("j"));

        TNodeTemplate node10 = topology.getNodeTemplate("10");
        assertNotNull(node10);
        assertNull(node10.getProperties());
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
        TTopologyTemplate refinementTopology = candidateForTopology.getRefinementModel().getRefinementTopology();
        TTopologyTemplate detector = candidateForTopology.getRefinementModel().getDetector();

        refinementTopology.getNodeTemplateOrRelationshipTemplate()
            .removeIf(template -> template.getId().equals("13") || template.getId().equals("1213"));

        TNodeTemplate nt12 = refinementTopology.getNodeTemplate("12");
        assertNotNull(nt12);
        nt12.setType("{http://ex.org}nodeType_4");
        TNodeTemplate nt4 = detector.getNodeTemplate("8");

        OTStayMapping nt4staysAsNt12 = new OTStayMapping.Builder("stay1")
            .setDetectorElement(nt4)
            .setRefinementElement(nt12)
            .build();
        ((OTPatternRefinementModel) candidateForTopology.getRefinementModel())
            .setStayMappings(Collections.singletonList(nt4staysAsNt12));
        // endregion

        // recreate the candidate
        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology2);
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(detector);

        GraphMapping<ToscaNode, ToscaEdge> mapping = new ToscaIsomorphismMatcher()
            .findMatches(detectorGraph, topologyGraph, new ToscaTypeMatcher())
            .next();

        candidateForTopology = new RefinementCandidate(candidateForTopology.getRefinementModel(), mapping, detectorGraph, 1);

        PatternRefinement patternRefinement = new PatternRefinement();
        patternRefinement.applyRefinement(candidateForTopology, topology2);

        // region *** assertions ***
        assertNotNull(topology2.getNodeTemplate("4"));
        TRelationshipTemplate relation1012 = topology2.getRelationshipTemplate("1012");
        assertNotNull(relation1012);
        assertEquals("4", relation1012.getTargetElement().getRef().getId());
        assertEquals("10", relation1012.getSourceElement().getRef().getId());

        TRelationshipTemplate relation1112 = topology2.getRelationshipTemplate("1112");
        assertNotNull(relation1112);
        assertEquals("4", relation1112.getTargetElement().getRef().getId());
        assertEquals("11", relation1112.getSourceElement().getRef().getId());

        TRelationshipTemplate relation21 = topology2.getRelationshipTemplate("21");
        assertNotNull(relation21);
        assertEquals("1", relation21.getTargetElement().getRef().getId());
        assertEquals("10", relation21.getSourceElement().getRef().getId());
        // endregion
    }

    // endregion

    // region ********** redirectDeploymentArtifactMappings **********
    @Test
    void redirectDeploymentArtifacts() {
        setUp();
        
        /*
        input:
        #######   (1)  ########----
        # (1) # <----- # (2) # DA |
        #######        #######-----
                          | (2)
                         \/
                       #######
                       # (4) #
                       #######

        expected output:
        #######   (1)  ########        ########-----
        # (1) # <----- # (10) #        # (11) # DA |
        #######        ########        ########-----
                           | (2)          | (2)
                           +-------|------+
                                  \/
                                #######
                                # (4) #
                                #######
         */
        PatternRefinement patternRefinement = new PatternRefinement();
        patternRefinement.applyRefinement(candidateForTopology3WithDa, topology3);

        // region *** assertions ***
        TNodeTemplate refinedNt = topology3.getNodeTemplate("13");
        assertNotNull(refinedNt);
        assertNotNull(refinedNt.getDeploymentArtifacts());
        assertEquals(1, refinedNt.getDeploymentArtifacts().getDeploymentArtifact().size());
        assertEquals(
            new QName("test_da", "http://example.org/tosca/atemp/das"),
            refinedNt.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactRef());
        assertEquals(
            new QName("file", "http://example.org/tosca/at"),
            refinedNt.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType());

        // endregion
    }
    // endregion
}
