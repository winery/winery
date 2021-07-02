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

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.topologygraph.matching.MockNamespaceManager;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToscaPatternMatcherTest {

    @Test
    public void behaviorPatternsCompatible() {
        TPolicies detectorPolicies = new TPolicies();
        detectorPolicies.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("{ns}type1"))));
        TPolicies candidatePolicies = new TPolicies();

        TNodeTemplate detector = new TNodeTemplate();
        detector.setPolicies(detectorPolicies);
        ToscaNode detectorNode = new ToscaNode();
        detectorNode.setNodeTemplate(detector);
        TNodeTemplate candidate = new TNodeTemplate();
        candidate.setPolicies(candidatePolicies);
        ToscaNode candidateNode = new ToscaNode();
        candidateNode.setNodeTemplate(candidate);

        NamespaceManager namespaceManager = new MockNamespaceManager() {
            @Override
            public boolean isPatternNamespace(String namespace) {
                return namespace.equals("patternNs");
            }
        };
        ToscaPatternMatcher matcher = new ToscaPatternMatcher(null, namespaceManager) {
        };
        // detector has policy, candidate doesn't
        assertTrue(matcher.behaviorPatternsCompatible(detectorNode, candidateNode));

        detectorPolicies.getPolicy().clear();
        candidatePolicies.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("{ns}type1"))));
        // candidate has policy, detector doesn't
        assertTrue(matcher.behaviorPatternsCompatible(detectorNode, candidateNode));

        detectorPolicies.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("{patternNs}type2"))));
        // detector has behavior pattern, candidate doesn't
        assertFalse(matcher.behaviorPatternsCompatible(detectorNode, candidateNode));

        candidatePolicies.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("{patternNs}type2"))));
        // detector and candidate have same behavior pattern
        assertTrue(matcher.behaviorPatternsCompatible(detectorNode, candidateNode));

        candidatePolicies.getPolicy().add(new TPolicy(new TPolicy.Builder(QName.valueOf("{patternNs}type3"))));
        // candidate has different behavior pattern than detector
        assertFalse(matcher.behaviorPatternsCompatible(detectorNode, candidateNode));
    }
}
