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
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTBehaviorPatternMapping;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.kvproperties.OTPropertyKV;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToscaBehaviorPatternMatcherTest {

    @Test
    public void propertiesCompatible() {
        LinkedHashMap<String, String> refinementProps = new LinkedHashMap<>();
        refinementProps.put("null", null);
        refinementProps.put("empty", "");
        refinementProps.put("star", "*");
        refinementProps.put("match", "this has to match");
        refinementProps.put("ignoreCase", "THIS HAS TO MATCH INDEPENDENT OF CASE");
        LinkedHashMap<String, String> candidateProps = new LinkedHashMap<>();
        candidateProps.put("null", "does not have to be null");
        candidateProps.put("empty", "does not have to be empty");
        candidateProps.put("star", "this has to be non-null and non-empty");
        candidateProps.put("match", "this has to match");
        candidateProps.put("ignoreCase", "this has to match independent of case");

        TNodeTemplate refinement = new TNodeTemplate();
        refinement.setId("refinement");
        ModelUtilities.setPropertiesKV(refinement, refinementProps);
        ToscaNode refinementNode = new ToscaNode();
        refinementNode.setNodeTemplate(refinement);
        TNodeTemplate candidate = new TNodeTemplate();
        candidate.setId("candidate");
        ModelUtilities.setPropertiesKV(candidate, candidateProps);
        ToscaNode candidateNode = new ToscaNode();
        candidateNode.setNodeTemplate(candidate);

        OTPatternRefinementModel prm = new OTPatternRefinementModel();
        prm.setRefinementTopology(new TTopologyTemplate(new TTopologyTemplate.Builder()
            .addNodeTemplate(refinement)
        ));
        // needs to be swapped manually as only prms retrieved from repo are swapped automatically
        PatternDetectionUtils.swapDetectorWithRefinement(prm);
        ToscaBehaviorPatternMatcher matcher = new ToscaBehaviorPatternMatcher(prm, null);
        assertTrue(matcher.propertiesCompatible(refinementNode, candidateNode));

        refinementProps.put("doesNotMatch", "something");
        candidateProps.put("doesNotMatch", "something else");
        assertFalse(matcher.propertiesCompatible(refinementNode, candidateNode));

        // props with behavior pattern mappings can be ignored
        List<OTBehaviorPatternMapping> behaviorPatternMappings = new ArrayList<>();
        OTBehaviorPatternMapping behaviorPatternMapping = new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder("behaviorPatternMap0")
            .setRefinementElement(refinement)
            .setProperty(new OTPropertyKV("doesNotMatch", ""))
        );
        behaviorPatternMappings.add((OTBehaviorPatternMapping) PatternDetectionUtils.swapDetectorWithRefinement(behaviorPatternMapping));
        prm.setBehaviorPatternMappings(behaviorPatternMappings);
        assertTrue(matcher.propertiesCompatible(refinementNode, candidateNode));

        candidateProps.put("empty", "");
        assertTrue(matcher.propertiesCompatible(refinementNode, candidateNode));
        candidateProps.put("star", null);
        assertFalse(matcher.propertiesCompatible(refinementNode, candidateNode));
        candidateProps.put("star", "");
        assertFalse(matcher.propertiesCompatible(refinementNode, candidateNode));
    }
}
