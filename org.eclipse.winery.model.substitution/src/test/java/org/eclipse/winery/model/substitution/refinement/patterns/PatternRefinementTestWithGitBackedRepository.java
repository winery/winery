/*******************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.substitution.refinement.patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternRefinementTestWithGitBackedRepository extends TestWithGitBackedRepository {

    @Test
    void refineTopologyWithMultipleSameSubGraphs() throws Exception {
        this.setRevisionTo("origin/plain");

        List<RefinementCandidate> myCandidates = new ArrayList<>();
        PatternRefinement refinement = new PatternRefinement((candidates, refinementServiceTemplate, currentTopology) -> {
            myCandidates.addAll(candidates);
            return null;
        });

        ServiceTemplateId serviceTemplateId = refinement.refineServiceTemplate(new ServiceTemplateId(
            "http://winery.opentosca.org/test/concrete/servicetemplates",
            "Pattern-basedDeplyomentModelWithTwoSameSubgraphs_w1-wip1", false));

        assertEquals(2, myCandidates.size());
        myCandidates.forEach(prmc -> assertEquals("IaaS_connectedTo_ThirdPattern_w1-wip1", prmc.getRefinementModel().getName()));

        ArrayList<String> nodeIdsToBeReplacedInFirstPmc = myCandidates.get(0).getNodeIdsToBeReplaced();
        assertEquals("Infrastructure-As-A-Service_w1", nodeIdsToBeReplacedInFirstPmc.get(0));
        assertEquals("ThirdPattern_w1", nodeIdsToBeReplacedInFirstPmc.get(1));

        ArrayList<String> nodeIdsToBeReplacedInSecondPmc = myCandidates.get(1).getNodeIdsToBeReplaced();
        assertEquals("Infrastructure-As-A-Service_w1_2", nodeIdsToBeReplacedInSecondPmc.get(0));
        assertEquals("ThirdPattern_w1", nodeIdsToBeReplacedInSecondPmc.get(1));

        assertEquals("Pattern-basedDeplyomentModelWithTwoSameSubgraphs_w1-wip1-refined-w1-wip1", serviceTemplateId.getQName().getLocalPart());
        assertEquals("http://winery.opentosca.org/test/concrete/servicetemplates", serviceTemplateId.getQName().getNamespaceURI());
    }

    @Test
    void refineTopologyWithPatternsAnnotatedAsPolicies() throws Exception {
        this.setRevisionTo("origin/plain");

        List<RefinementCandidate> myCandidates = new ArrayList<>();
        PatternRefinement refinement = new PatternRefinement((candidates, refinementServiceTemplate, currentTopology) -> {
            myCandidates.addAll(candidates);
            return null;
        });

        ServiceTemplateId serviceTemplateId = refinement.refineServiceTemplate(new ServiceTemplateId(
            "http://winery.opentosca.org/test/servicetemplates",
            "NodeTemplateAnnotedWithPattern_w1-wip2", false));

        assertEquals(1, myCandidates.size());
        assertEquals("PolicyAnnotatedNtWithTwoKvProps_w1-wip1", myCandidates.get(0).getRefinementModel().getName());
    }

    @Test
    void refineTopologyWithPatternsAnnotatedAsPoliciesInMultipleInstances() throws Exception {
        this.setRevisionTo("origin/plain");

        List<RefinementCandidate> myCandidates = new ArrayList<>();
        PatternRefinement refinement = new PatternRefinement((candidates, refinementServiceTemplate, currentTopology) -> {
            myCandidates.addAll(candidates);
            return null;
        });

        // This ST contains two similar sub-graphs where one only defines a policy type whereas the second one defines a specific template as well. 
        ServiceTemplateId serviceTemplateId = refinement.refineServiceTemplate(new ServiceTemplateId(
            "http://winery.opentosca.org/test/servicetemplates",
            "NodeTemplateAnnotedWithPattern_w1-wip3", false));

        assertEquals(3, myCandidates.size());

        long countForRefinementModelsWithPolicyTypeOnly = myCandidates.stream()
            .filter(refinementCandidate -> refinementCandidate.getRefinementModel().getName().equals("PolicyAnnotatedNtWithTwoKvProps_w1-wip1"))
            .count();
        assertEquals(2, countForRefinementModelsWithPolicyTypeOnly);

        List<RefinementCandidate> moreSpecificPrm = myCandidates.stream()
            .filter(refinementCandidate -> refinementCandidate.getRefinementModel().getName().equals("PolicyAnnotatedNtWithTwoKvProps_w1-wip2"))
            .collect(Collectors.toList());
        assertEquals(1, moreSpecificPrm.size());

        ArrayList<String> nodeIdsToBeReplaced = moreSpecificPrm.get(0).getNodeIdsToBeReplaced();
        assertEquals(2, nodeIdsToBeReplaced.size());
        assertTrue(nodeIdsToBeReplaced.contains("NodeTypeWithTwoKVProperties_3"));
        assertTrue(nodeIdsToBeReplaced.contains("Infrastructure-As-A-Service_w1_3"));
    }
}
