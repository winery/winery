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
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternRefinementTestWithGitBackedRepository extends TestWithGitBackedRepository {

    @Test
    public void refineTopologyWithMultipleSameSubGraphs() throws Exception {
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

    @Test
    public void applyMoreConcreteRelationRedirect() throws Exception {
        this.setRevisionTo("origin/plain");

        TServiceTemplate serviceTemplate = repository.getElement(new ServiceTemplateId(
            "http://winery.opentosca.org/test/concrete/servicetemplates",
            "Pattern-basedDeplymentModel_complexRelationMapping-w1-wip1",
            false
        ));

        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        assertNotNull(topologyTemplate);
        
        new PatternRefinement().refineTopology(topologyTemplate);
        
        assertNotNull(topologyTemplate.getNodeTemplateOrRelationshipTemplate());
        assertEquals(12, topologyTemplate.getNodeTemplateOrRelationshipTemplate().size());
        
        assertNotNull(topologyTemplate.getNodeTemplate("rs_Tomcat_8.5-w1_0"));
        assertNotNull(topologyTemplate.getNodeTemplate("rs_Ubuntu_w1-wip1_0"));
        assertNotNull(topologyTemplate.getNodeTemplate("ThirdPattern_w1"));
        assertNotNull(topologyTemplate.getNodeTemplate("FifthPattern_w1"));
        assertNotNull(topologyTemplate.getNodeTemplate("NodeTypeInheritingFromAbstractType_1-w1-wip1_0"));
        assertNotNull(topologyTemplate.getNodeTemplate("NodeTypeInheritingFromAbstractType_1-w1-wip1_1"));

        TRelationshipTemplate patternOnUbuntu = topologyTemplate.getRelationshipTemplate("con_HostedOn_1");
        assertNotNull(patternOnUbuntu);
        assertEquals("rs_Ubuntu_w1-wip1_0", patternOnUbuntu.getTargetElement().getRef().getId());
        assertEquals("FifthPattern_w1", patternOnUbuntu.getSourceElement().getRef().getId());
        
        TRelationshipTemplate ubuntuOnPattern = topologyTemplate.getRelationshipTemplate("con_HostedOn_2");
        assertNotNull(ubuntuOnPattern);
        assertEquals("rs_Ubuntu_w1-wip1_0", ubuntuOnPattern.getSourceElement().getRef().getId());
        assertEquals("ThirdPattern_w1", ubuntuOnPattern.getTargetElement().getRef().getId());
        
        TRelationshipTemplate tomcatOnUbuntu = topologyTemplate.getRelationshipTemplate("rs_con_HostedOn_0");
        assertNotNull(tomcatOnUbuntu);
        assertEquals("rs_Ubuntu_w1-wip1_0", tomcatOnUbuntu.getTargetElement().getRef().getId());
        assertEquals("rs_Tomcat_8.5-w1_0", tomcatOnUbuntu.getSourceElement().getRef().getId());
        
        TRelationshipTemplate compOneOnTomcat = topologyTemplate.getRelationshipTemplate("con_HostedOn_0");
        assertNotNull(compOneOnTomcat);
        assertEquals("NodeTypeInheritingFromAbstractType_1-w1-wip1_0", compOneOnTomcat.getSourceElement().getRef().getId());
        assertEquals("rs_Tomcat_8.5-w1_0", compOneOnTomcat.getTargetElement().getRef().getId());
        
        TRelationshipTemplate compTwoOnTomcat = topologyTemplate.getRelationshipTemplate("con_HostedOn_3");
        assertNotNull(compTwoOnTomcat);
        assertEquals("NodeTypeInheritingFromAbstractType_1-w1-wip1_1", compTwoOnTomcat.getSourceElement().getRef().getId());
        assertEquals("rs_Tomcat_8.5-w1_0", compTwoOnTomcat.getTargetElement().getRef().getId());

        TRelationshipTemplate compOnetoPattern = topologyTemplate.getRelationshipTemplate("con_ConnectsTo_0");
        assertNotNull(compOnetoPattern);
        assertEquals("NodeTypeInheritingFromAbstractType_1-w1-wip1_0", compOnetoPattern.getSourceElement().getRef().getId());
        assertEquals("FifthPattern_w1", compOnetoPattern.getTargetElement().getRef().getId());
    }

    @Test
    public void testStyingElementWithSameIdInRefinementStructureAndRefinedTopology() throws Exception {
        //this.setRevisionTo("origin/plain");

        TServiceTemplate serviceTemplate = repository.getElement(new ServiceTemplateId(
            "http://winery.opentosca.org/test/concrete/servicetemplates",
            "Pattern-basedDeploymentModelWithTwoSameSubgraphs_staying-same-id-w1-wip1",
            false
        ));

        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        assertNotNull(topologyTemplate);

        new PatternRefinement((candidates, refinementServiceTemplate, currentTopology) -> {
            for (RefinementCandidate candidate : candidates) {
                if (candidate.getRefinementModel().getName().equals("ProblemWithStayingElementSameId_w1-wip1")) {
                    return candidate; 
                }
            }
            throw new RuntimeException("Did not find expected RefinementCandidate!");
        }).refineTopology(topologyTemplate);

        TRelationshipTemplate ubuntuHostedOnCloud_0 = topologyTemplate.getRelationshipTemplate("con_HostedOn_0");
        assertNotNull(ubuntuHostedOnCloud_0);
        assertEquals("Ubuntu_w1-wip1_0", ubuntuHostedOnCloud_0.getSourceElement().getRef().getId());
        assertEquals("rs_CloudProvider1_w1-wip1_0", ubuntuHostedOnCloud_0.getTargetElement().getRef().getId());

        TRelationshipTemplate newUbuntuHostedOnCloud_1 = topologyTemplate.getRelationshipTemplate("rs_con_HostedOn_0");
        assertNotNull(newUbuntuHostedOnCloud_1);
        assertEquals("rs_Ubuntu_w1-wip1_0", newUbuntuHostedOnCloud_1.getSourceElement().getRef().getId());
        assertEquals("rs_CloudProvider1_w1-wip1_1", newUbuntuHostedOnCloud_1.getTargetElement().getRef().getId());
        
        long ingoingRelationsAtStayingElement = topologyTemplate.getRelationshipTemplates().stream()
            .filter(relation -> "rs_CloudProvider1_w1-wip1_1".equals(relation.getTargetElement().getRef().getId()))
            .count();
        assertEquals(1, ingoingRelationsAtStayingElement);
        long ingoingRelationsAtElementWithSameIdAsStayingRefinedElement = topologyTemplate.getRelationshipTemplates().stream()
            .filter(relation -> "rs_CloudProvider1_w1-wip1_0".equals(relation.getTargetElement().getRef().getId()))
            .count();
        assertEquals(1, ingoingRelationsAtElementWithSameIdAsStayingRefinedElement);
    }
}
