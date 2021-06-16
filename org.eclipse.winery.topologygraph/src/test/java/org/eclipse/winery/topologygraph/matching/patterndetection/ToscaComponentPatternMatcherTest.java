/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologygraph.matching.patterndetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.topologygraph.matching.MockNamespaceManager;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToscaComponentPatternMatcherTest {

    @Test
    public void isCompatible() {
        // same type
        TNodeTemplate refinementTemplate = new TNodeTemplate(new TNodeTemplate.Builder("d", new QName("type1")));
        ToscaNode refinement = new ToscaNode();
        refinement.addTEntityType(new TNodeType(new TNodeType.Builder("type1")));
        refinement.setNodeTemplate(refinementTemplate);
        TNodeTemplate candidateTemplate = new TNodeTemplate(new TNodeTemplate.Builder("c", new QName("type1")));
        ToscaNode candidate = new ToscaNode();
        candidate.addTEntityType(new TNodeType(new TNodeType.Builder("type1")));
        candidate.setNodeTemplate(candidateTemplate);
        OTPatternRefinementModel prm = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setRefinementStructure(new TTopologyTemplate(new TTopologyTemplate.Builder().addNodeTemplate(refinementTemplate)))
        );
        // needs to be swapped manually as only prms retrieved from repo are swapped automatically
        PatternDetectionUtils.swapDetectorWithRefinement(prm);
        NamespaceManager namespaceManager = new MockNamespaceManager() {
            @Override
            public boolean isPatternNamespace(String namespace) {
                return namespace.equals("patternNs");
            }
        };
        ToscaComponentPatternMatcher matcher = new ToscaComponentPatternMatcher(prm, namespaceManager, new ArrayList<>(), new HashMap<>());
        assertTrue(matcher.isCompatible(refinement, candidate));

        // different type
        refinementTemplate.setType(new QName("type1"));
        refinement.getTypes().clear();
        refinement.addTEntityType(new TNodeType(new TNodeType.Builder("type1")));
        candidateTemplate.setType(new QName("type2"));
        candidate.getTypes().clear();
        candidate.addTEntityType(new TNodeType(new TNodeType.Builder("type2")));
        assertFalse(matcher.isCompatible(refinement, candidate));

        // share supertype
        refinement.addTEntityType(new TNodeType(new TNodeType.Builder("super")));
        candidate.addTEntityType(new TNodeType(new TNodeType.Builder("super")));
        assertTrue(matcher.isCompatible(refinement, candidate));

        // stay mapping exists
        OTStayMapping stayMapping = new OTStayMapping(new OTStayMapping.Builder()
            .setDetectorElement(null)
            .setRefinementElement(refinementTemplate)
        );
        List<OTStayMapping> stayMappings = new ArrayList<>();
        stayMappings.add((OTStayMapping) PatternDetectionUtils.swapDetectorWithRefinement(stayMapping));
        prm.setStayMappings(stayMappings);
        assertFalse(matcher.isCompatible(refinement, candidate));
        prm.getStayMappings().clear();
        assertTrue(matcher.isCompatible(refinement, candidate));

        // component pattern mapping exists
        List<OTPermutationMapping> componentPatternMappings = new ArrayList<>();
        OTPermutationMapping componentPatternMapping = new OTPermutationMapping(new OTPermutationMapping.Builder()
            .setDetectorElement(new TNodeTemplate())
            .setRefinementElement(refinementTemplate)
        );
        componentPatternMappings.add((OTPermutationMapping) PatternDetectionUtils.swapDetectorWithRefinement(componentPatternMapping));
        prm.setPermutationMappings(componentPatternMappings);
        assertFalse(matcher.isCompatible(refinement, candidate));
        prm.getPermutationMappings().clear();
        assertTrue(matcher.isCompatible(refinement, candidate));

        // different behavior patterns
        TPolicies refinementPolicies = new TPolicies();
        refinementPolicies.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("{patternNs}type1"))));
        refinementTemplate.setPolicies(refinementPolicies);
        TPolicies candidatePolicies = new TPolicies();
        candidateTemplate.setPolicies(candidatePolicies);
        // detector has behavior pattern, candidate doesn't
        assertFalse(matcher.isCompatible(refinement, candidate));
        candidatePolicies.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("{patternNs}type1"))));
        // detector and candidate have same behavior pattern
        assertTrue(matcher.isCompatible(refinement, candidate));
        refinementPolicies.getPolicy().clear();
        // candidate has behavior pattern, detector doesn't
        assertFalse(matcher.isCompatible(refinement, candidate));
        candidatePolicies.getPolicy().clear();
        assertTrue(matcher.isCompatible(refinement, candidate));

        // detector supertype of candidate
        refinementTemplate.setType(new QName("super"));
        refinement.getTypes().clear();
        refinement.addTEntityType(new TNodeType(new TNodeType.Builder("super")));
        candidateTemplate.setType(new QName("type"));
        candidate.getTypes().clear();
        candidate.addTEntityType(new TNodeType(new TNodeType.Builder("type")));
        candidate.addTEntityType(new TNodeType(new TNodeType.Builder("super")));
        assertTrue(matcher.isCompatible(refinement, candidate));

        // candidate supertype of detector
        refinementTemplate.setType(new QName("type"));
        refinement.getTypes().clear();
        refinement.addTEntityType(new TNodeType(new TNodeType.Builder("type")));
        refinement.addTEntityType(new TNodeType(new TNodeType.Builder("super")));
        candidateTemplate.setType(new QName("super"));
        candidate.getTypes().clear();
        candidate.addTEntityType(new TNodeType(new TNodeType.Builder("super")));
        assertTrue(matcher.isCompatible(refinement, candidate));

        // different supertypes
        refinementTemplate.setType(new QName("type1"));
        refinement.getTypes().clear();
        refinement.addTEntityType(new TNodeType(new TNodeType.Builder("type1")));
        refinement.addTEntityType(new TNodeType(new TNodeType.Builder("super1")));
        candidateTemplate.setType(new QName("type2"));
        candidate.getTypes().clear();
        candidate.addTEntityType(new TNodeType(new TNodeType.Builder("type2")));
        candidate.addTEntityType(new TNodeType(new TNodeType.Builder("super2")));
        assertFalse(matcher.isCompatible(refinement, candidate));
    }

    @Test
    public void componentPatternsCompatible() {
        // prm1 doesn't match the candidate but implements the same Component Pattern 'patternType1'
        TNodeTemplate detector1 = new TNodeTemplate(new TNodeTemplate.Builder("detector1", new QName("patternType1")));
        TNodeTemplate refinement1 = new TNodeTemplate(new TNodeTemplate.Builder("refinement1", new QName("concreteType1")));
        ToscaNode refinement1Node = new ToscaNode();
        refinement1Node.setNodeTemplate(refinement1);
        List<OTPermutationMapping> permutationMappings1 = new ArrayList<>();
        permutationMappings1.add(new OTPermutationMapping(new OTPermutationMapping.Builder()
            .setDetectorElement(detector1)
            .setRefinementElement(refinement1)
        ));
        OTPatternRefinementModel prm1 = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setDetector(new TTopologyTemplate(new TTopologyTemplate.Builder().addNodeTemplate(detector1)))
            .setRefinementStructure(new TTopologyTemplate(new TTopologyTemplate.Builder().addNodeTemplate(refinement1)))
            .setPermutationMappings(permutationMappings1)
        );
        // needs to be swapped manually as only prms retrieved from repo are swapped automatically
        PatternDetectionUtils.swapDetectorWithRefinement(prm1);

        // prm2 matches the candidate and also implements the Component Pattern 'patternType1'
        TNodeTemplate detector2 = new TNodeTemplate(new TNodeTemplate.Builder("detector2", new QName("patternType1")));
        TNodeTemplate refinement2 = new TNodeTemplate(new TNodeTemplate.Builder("refinement2", new QName("concreteType2")));
        ToscaNode refinement2Node = new ToscaNode();
        refinement2Node.setNodeTemplate(refinement2);
        List<OTPermutationMapping> permutationMappings2 = new ArrayList<>();
        permutationMappings2.add(new OTPermutationMapping(new OTPermutationMapping.Builder()
            .setDetectorElement(detector2)
            .setRefinementElement(refinement2)
        ));
        OTPatternRefinementModel prm2 = new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setDetector(new TTopologyTemplate(new TTopologyTemplate.Builder().addNodeTemplate(detector2)))
            .setRefinementStructure(new TTopologyTemplate(new TTopologyTemplate.Builder().addNodeTemplate(refinement2)))
            .setPermutationMappings(permutationMappings2)
        );
        // needs to be swapped manually as only prms retrieved from repo are swapped automatically
        PatternDetectionUtils.swapDetectorWithRefinement(prm2);

        List<OTRefinementModel> prms = new ArrayList<>();
        prms.add(prm1);
        ToscaComponentPatternMatcher matcher = new ToscaComponentPatternMatcher(prm1, null, prms, new HashMap<>());
        ToscaNode candidate = new ToscaNode();
        candidate.setNodeTemplate(new TNodeTemplate(new TNodeTemplate.Builder("nt1", new QName("concreteType2"))));
        // only prm1 available -> doesn't match
        assertFalse(matcher.isCompatible(refinement1Node, candidate));
        prms.add(prm2);
        // now prm2 can also be used -> matches
        assertTrue(matcher.isCompatible(refinement1Node, candidate));
        prms.clear();
        prms.add(prm2);
        matcher = new ToscaComponentPatternMatcher(prm2, null, prms, new HashMap<>());
        // prm2 used directly -> also matches
        assertTrue(matcher.isCompatible(refinement2Node, candidate));
        prms.add(prm1);
        // still matches
        assertTrue(matcher.isCompatible(refinement2Node, candidate));

        matcher = new ToscaComponentPatternMatcher(prm1, null, prms, new HashMap<>());
        OTPermutationMapping oneTooMany = new OTPermutationMapping(new OTPermutationMapping.Builder()
            .setDetectorElement(new TNodeTemplate(new TNodeTemplate.Builder("tmp1", new QName("tmp1"))))
            .setRefinementElement(refinement1)
        );
        permutationMappings1.add((OTPermutationMapping) PatternDetectionUtils.swapDetectorWithRefinement(oneTooMany));
        // detector is associated with multiple Component patterns -> not one-to-one
        assertFalse(matcher.isCompatible(refinement1Node, candidate));
        oneTooMany.setDetectorElement(detector1);
        oneTooMany.setRefinementElement(new TNodeTemplate(new TNodeTemplate.Builder("tmp2", new QName("tmp2"))));
        PatternDetectionUtils.swapDetectorWithRefinement(oneTooMany);
        // refinement element is associated with multiple Component patterns -> not one-to-one
        assertFalse(matcher.isCompatible(refinement1Node, candidate));
        permutationMappings1.remove(oneTooMany);
        // after removal, it's one-to-one again
        assertTrue(matcher.isCompatible(refinement1Node, candidate));

        // empty or null
        prm2.getPermutationMappings().clear();
        assertFalse(matcher.isCompatible(refinement1Node, candidate));
        prm1.getPermutationMappings().clear();
        assertFalse(matcher.isCompatible(refinement1Node, candidate));
        prm2.setPermutationMappings(null);
        assertFalse(matcher.isCompatible(refinement1Node, candidate));
        prm1.setPermutationMappings(null);
        assertFalse(matcher.isCompatible(refinement1Node, candidate));
    }
}
