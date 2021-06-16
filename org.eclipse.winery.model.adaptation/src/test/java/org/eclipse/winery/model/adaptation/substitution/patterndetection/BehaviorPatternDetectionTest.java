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

package org.eclipse.winery.model.adaptation.substitution.patterndetection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.substitution.refinement.DefaultRefinementChooser;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTBehaviorPatternMapping;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.tosca.extensions.kvproperties.OTPropertyKV;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.patterndetection.PatternDetectionUtils;
import org.eclipse.winery.topologygraph.matching.patterndetection.ToscaBehaviorPatternMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.jgrapht.GraphMapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BehaviorPatternDetectionTest {

    @Test
    public void addCompatibleBehaviorPatterns() {
        List<TPolicy> behaviorPatterns = new ArrayList<>();
        behaviorPatterns.add(new TPolicy(new TPolicy.Builder(QName.valueOf("{patternNs}oneProp")).setName("oneProp")));
        TNodeTemplate detectorElement = new TNodeTemplate(
            new TNodeTemplate.Builder("detectorElement", QName.valueOf("{ns}patternType"))
                .setPolicies(new TPolicies(behaviorPatterns))
                .setX("1")
                .setY("1")
        );
        TNodeTemplate refinementElement = new TNodeTemplate(
            new TNodeTemplate.Builder("refinementElement", QName.valueOf("{ns}concreteType"))
                .setX("1")
                .setY("1")
        );
        LinkedHashMap<String, String> refinementProps = new LinkedHashMap<>();
        refinementProps.put("oneProp", "true");
        ModelUtilities.setPropertiesKV(refinementElement, refinementProps);

        List<TPolicy> stayingPolicies = new ArrayList<>();
        stayingPolicies.add(new TPolicy(new TPolicy.Builder(QName.valueOf("{ns}normalPolicy")).setName("normalPolicy")));
        TNodeTemplate stayingElement = new TNodeTemplate(
            new TNodeTemplate.Builder("stayingElement", QName.valueOf("{ns}concreteType"))
                .setPolicies(new TPolicies(stayingPolicies))
                .setX("1")
                .setY("1")
        );
        LinkedHashMap<String, String> stayingProps = new LinkedHashMap<>();
        stayingProps.put("oneProp", "false");
        ModelUtilities.setPropertiesKV(stayingElement, stayingProps);

        List<OTStayMapping> stayMappings = Collections.singletonList(
            new OTStayMapping(new OTStayMapping.Builder()
                .setDetectorElement(detectorElement)
                .setRefinementElement(refinementElement)
            )
        );
        List<OTBehaviorPatternMapping> behaviorPatternMappings = Collections.singletonList(
            new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder("behaviorPatternMap0")
                .setDetectorElement(detectorElement)
                .setRefinementElement(refinementElement)
                .setBehaviorPattern("oneProp")
                .setProperty(new OTPropertyKV("oneProp", "true"))
            )
        );
        TTopologyTemplate detector = new TTopologyTemplate();
        detector.addNodeTemplate(detectorElement);
        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        refinementStructure.addNodeTemplate(refinementElement);
        OTPatternRefinementModel prm = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setDetector(detector)
            .setRefinementStructure(refinementStructure)
            .setStayMappings(stayMappings)
            .setBehaviorPatternMappings(behaviorPatternMappings)
        );

        // needs to be swapped manually as only prms retrieved from repo are swapped automatically
        PatternDetectionUtils.swapDetectorWithRefinement(prm);
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(stayingElement);
        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(prm.getDetector());
        IToscaMatcher matcher = new ToscaBehaviorPatternMatcher(prm, namespaceManager());
        ToscaIsomorphismMatcher isomorphismMatcher = new ToscaIsomorphismMatcher();
        Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = isomorphismMatcher
            .findMatches(detectorGraph, topologyGraph, matcher);
        RefinementCandidate refinementCandidate = new RefinementCandidate(prm, matches.next(), detectorGraph, 1);

        // 'normalPolicy' is not removed because not behavior pattern, 'oneProp' not present behavior pattern mapping doesn't match
        BehaviorPatternDetection behaviorPatternDetection = new BehaviorPatternDetection(new DefaultRefinementChooser());
        behaviorPatternDetection.applyRefinement(refinementCandidate, topology);
        assertEquals(topology.getNodeTemplateOrRelationshipTemplate().size(), 1);
        assertEquals(topology.getNodeTemplates().get(0).getId(), "stayingElement");
        List<TPolicy> policies = topology.getNodeTemplates().get(0).getPolicies().getPolicy();
        assertEquals(policies.size(), 1);
        assertEquals(policies.get(0).getName(), "normalPolicy");

        // 'normalPolicy' is not removed because not behavior pattern, 'oneProp' present behavior pattern mapping matches
        stayingProps.put("oneProp", "true");
        topology.getNodeTemplateOrRelationshipTemplate().clear();
        topology.addNodeTemplate(stayingElement);
        behaviorPatternDetection.applyRefinement(refinementCandidate, topology);
        assertEquals(topology.getNodeTemplateOrRelationshipTemplate().size(), 1);
        assertEquals(topology.getNodeTemplates().get(0).getId(), "stayingElement");
        policies = topology.getNodeTemplates().get(0).getPolicies().getPolicy();
        assertEquals(policies.size(), 2);
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("normalPolicy")));
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("oneProp")));
    }

    @Test
    public void removeIncompatibleBehaviorPatterns() {
        TPolicies behaviorPatterns = new TPolicies(new ArrayList<>());
        behaviorPatterns.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("noProp")).setName("noProp")));
        behaviorPatterns.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("oneProp")).setName("oneProp")));
        behaviorPatterns.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("multiProps")).setName("multiProps")));
        TNodeTemplate detectorElement = new TNodeTemplate(
            new TNodeTemplate.Builder("detectorElement", QName.valueOf("{ns}patternType"))
                .setPolicies(behaviorPatterns)
                .setX("1")
                .setY("1")
        );
        TNodeTemplate refinementElement = new TNodeTemplate(
            new TNodeTemplate.Builder("refinementElement", QName.valueOf("{ns}concreteType"))
                .setX("1")
                .setY("1")
        );
        LinkedHashMap<String, String> refinementProps = new LinkedHashMap<>();
        refinementProps.put("oneProp", "true");
        refinementProps.put("multiPropsProp1", "true");
        refinementProps.put("multiPropsProp2", "true");
        refinementProps.put("multiPropsProp3", "*");
        refinementProps.put("multiPropsProp4", "");
        refinementProps.put("multiPropsProp5", null);
        ModelUtilities.setPropertiesKV(refinementElement, refinementProps);
        TNodeTemplate candidateElement = new TNodeTemplate(
            new TNodeTemplate.Builder("candidateElement", QName.valueOf("{ns}concreteType"))
                .setX("1")
                .setY("1")
        );
        LinkedHashMap<String, String> candidateProps = new LinkedHashMap<>();
        candidateProps.put("oneProp", "false");
        candidateProps.put("multiPropsProp1", "false");
        candidateProps.put("multiPropsProp2", "false");
        candidateProps.put("multiPropsProp3", "");
        candidateProps.put("multiPropsProp4", null);
        candidateProps.put("multiPropsProp5", "");
        ModelUtilities.setPropertiesKV(candidateElement, candidateProps);

        List<OTBehaviorPatternMapping> behaviorPatternMappings = new ArrayList<>();
        behaviorPatternMappings.add(new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder("behaviorPatternMap0")
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setBehaviorPattern("oneProp")
            .setProperty(new OTPropertyKV("oneProp", "true"))
        ));
        behaviorPatternMappings.add(new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder("behaviorPatternMap1")
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setBehaviorPattern("multiProps")
            .setProperty(new OTPropertyKV("multiPropsProp1", "true"))
        ));
        behaviorPatternMappings.add(new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder("behaviorPatternMap2")
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setBehaviorPattern("multiProps")
            .setProperty(new OTPropertyKV("multiPropsProp2", "true"))
        ));
        behaviorPatternMappings.add(new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder("behaviorPatternMap3")
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setBehaviorPattern("multiProps")
            .setProperty(new OTPropertyKV("multiPropsProp3", "*"))
        ));
        behaviorPatternMappings.add(new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder("behaviorPatternMap4")
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setBehaviorPattern("multiProps")
            .setProperty(new OTPropertyKV("multiPropsProp4", ""))
        ));
        behaviorPatternMappings.add(new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder("behaviorPatternMap5")
            .setDetectorElement(detectorElement)
            .setRefinementElement(refinementElement)
            .setBehaviorPattern("multiProps")
            .setProperty(new OTPropertyKV("multiPropsProp5", null))
        ));
        TTopologyTemplate detector = new TTopologyTemplate();
        detector.addNodeTemplate(detectorElement);
        TTopologyTemplate refinementStructure = new TTopologyTemplate();
        refinementStructure.addNodeTemplate(refinementElement);
        OTPatternRefinementModel prm = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setDetector(detector)
            .setRefinementStructure(refinementStructure)
            .setBehaviorPatternMappings(behaviorPatternMappings)
        );

        // needs to be swapped manually as only prms retrieved from repo are swapped automatically
        PatternDetectionUtils.swapDetectorWithRefinement(prm);
        TTopologyTemplate topology = new TTopologyTemplate();
        topology.addNodeTemplate(candidateElement);
        ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);
        ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(prm.getDetector());
        IToscaMatcher matcher = new ToscaBehaviorPatternMatcher(prm, null);
        ToscaIsomorphismMatcher isomorphismMatcher = new ToscaIsomorphismMatcher();
        Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = isomorphismMatcher
            .findMatches(detectorGraph, topologyGraph, matcher);
        RefinementCandidate refinementCandidate = new RefinementCandidate(prm, matches.next(), detectorGraph, 1);

        // 'noProp' is applicable independent of properties as no behavior pattern mappings are specified
        BehaviorPatternDetection behaviorPatternDetection = new BehaviorPatternDetection(new DefaultRefinementChooser());
        behaviorPatternDetection.applyRefinement(refinementCandidate, topology);
        assertEquals(topology.getNodeTemplateOrRelationshipTemplate().size(), 1);
        List<TPolicy> policies = topology.getNodeTemplates().get(0).getPolicies().getPolicy();
        assertEquals(policies.size(), 1);
        assertEquals(policies.get(0).getName(), "noProp");

        // 'noProp' still applicable, 'oneProp' now applicable as well because property is set
        candidateProps.put("oneProp", "true");
        topology.getNodeTemplateOrRelationshipTemplate().clear();
        topology.addNodeTemplate(candidateElement);
        behaviorPatternDetection.applyRefinement(refinementCandidate, topology);
        assertEquals(topology.getNodeTemplateOrRelationshipTemplate().size(), 1);
        policies = topology.getNodeTemplates().get(0).getPolicies().getPolicy();
        assertEquals(policies.size(), 2);
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("noProp")));
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("oneProp")));

        // 'noProp' & 'oneProp' still applicable, 'multiProps' not applicable because not all properties are set
        candidateProps.put("multiPropsProp1", "true");
        topology.getNodeTemplateOrRelationshipTemplate().clear();
        topology.addNodeTemplate(candidateElement);
        behaviorPatternDetection.applyRefinement(refinementCandidate, topology);
        assertEquals(topology.getNodeTemplateOrRelationshipTemplate().size(), 1);
        policies = topology.getNodeTemplates().get(0).getPolicies().getPolicy();
        assertEquals(policies.size(), 2);
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("noProp")));
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("oneProp")));

        // 'noProp' & 'oneProp' still applicable, 'multiProps' not applicable because not all properties are set
        candidateProps.put("multiPropsProp2", "true");
        topology.getNodeTemplateOrRelationshipTemplate().clear();
        topology.addNodeTemplate(candidateElement);
        behaviorPatternDetection.applyRefinement(refinementCandidate, topology);
        assertEquals(topology.getNodeTemplateOrRelationshipTemplate().size(), 1);
        policies = topology.getNodeTemplates().get(0).getPolicies().getPolicy();
        assertEquals(policies.size(), 2);
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("noProp")));
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("oneProp")));

        // 'noProp' & 'oneProp' & 'multiProps' applicable
        candidateProps.put("multiPropsProp3", "non-null and non-empty");
        topology.getNodeTemplateOrRelationshipTemplate().clear();
        topology.addNodeTemplate(candidateElement);
        behaviorPatternDetection.applyRefinement(refinementCandidate, topology);
        assertEquals(topology.getNodeTemplateOrRelationshipTemplate().size(), 1);
        policies = topology.getNodeTemplates().get(0).getPolicies().getPolicy();
        assertEquals(policies.size(), 3);
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("noProp")));
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("oneProp")));
        assertTrue(policies.stream().anyMatch(policy -> policy.getName().equals("multiProps")));
    }

    private NamespaceManager namespaceManager() {
        return new NamespaceManager() {
            @Override
            public @Nullable String getPrefix(String namespace) {
                return null;
            }

            @Override
            public boolean hasPermanentProperties(String namespace) {
                return false;
            }

            @Override
            public void removeNamespaceProperties(String namespace) {

            }

            @Override
            public void setNamespaceProperties(String namespace, NamespaceProperties properties) {

            }

            @Override
            public Map<String, NamespaceProperties> getAllNamespaces() {
                return null;
            }

            @Override
            public @NonNull NamespaceProperties getNamespaceProperties(String namespace) {
                return null;
            }

            @Override
            public void addAllPermanent(Collection<NamespaceProperties> properties) {

            }

            @Override
            public void replaceAll(Map<String, NamespaceProperties> map) {

            }

            @Override
            public void clear() {

            }

            @Override
            public boolean isPatternNamespace(String namespace) {
                return namespace.equals("patternNs");
            }

            @Override
            public boolean isSecureCollection(String namespace) {
                return false;
            }

            @Override
            public boolean isGeneratedNamespace(String namespace) {
                return false;
            }
        };
    }
}
