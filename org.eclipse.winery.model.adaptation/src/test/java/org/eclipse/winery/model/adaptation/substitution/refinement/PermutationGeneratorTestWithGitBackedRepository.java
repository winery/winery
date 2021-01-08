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

import java.util.Map;

import org.eclipse.winery.model.ids.extensions.PatternRefinementModelId;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.junit.jupiter.api.Test;

import static org.eclipse.winery.model.adaptation.substitution.refinement.PermutationHelper.addAllPermutationMappings;
import static org.eclipse.winery.model.adaptation.substitution.refinement.PermutationHelper.generateComplexPrmWithPatternSet;
import static org.eclipse.winery.model.adaptation.substitution.refinement.PermutationHelper.generatePrmWithTwoPatternsHostedOnAThird;
import static org.eclipse.winery.model.adaptation.substitution.refinement.PermutationHelper.generatePrmWithoutPermutationMaps;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermutationGeneratorTestWithGitBackedRepository extends TestWithGitBackedRepository {

    @Test
    void testGeneration() throws Exception {
        this.setRevisionTo("origin/plain");

        OTPatternRefinementModel refinementModel = generateComplexPrmWithPatternSet();
        addAllPermutationMappings(refinementModel);

        PatternRefinementModelId id = new PatternRefinementModelId(refinementModel.getTargetNamespace(), refinementModel.getIdFromIdOrNameField(), false);
        RepositoryFactory.getRepository().setElement(id, refinementModel);

        PermutationGenerator generator = new PermutationGenerator();
        Map<String, OTTopologyFragmentRefinementModel> permutations = generator.generatePermutations(refinementModel);

        assertEquals(2, permutations.size());

        OTTopologyFragmentRefinementModel permutation_1 = permutations.get("ComplexPrmWithPatternSet_permutation-1-w1-wip1");
        assertNotNull(permutation_1);
        assertEquals(4, permutation_1.getDetector().getNodeTemplates().size());
        assertEquals(4, permutation_1.getDetector().getRelationshipTemplates().size());
        assertTrue(permutation_1.getDetector().getRelationshipTemplates().removeIf(
            relation -> "11".equals(relation.getSourceElement().getRef().getId())
                && "12".equals(relation.getTargetElement().getRef().getId())));
        assertTrue(permutation_1.getDetector().getRelationshipTemplates().removeIf(
            relation -> "11".equals(relation.getSourceElement().getRef().getId())
                && "2".equals(relation.getTargetElement().getRef().getId())));
        assertTrue(permutation_1.getDetector().getRelationshipTemplates().removeIf(
            relation -> "12".equals(relation.getSourceElement().getRef().getId())
                && "2".equals(relation.getTargetElement().getRef().getId())));
        assertTrue(permutation_1.getDetector().getRelationshipTemplates().removeIf(
            relation -> "11".equals(relation.getSourceElement().getRef().getId())
                && "12".equals(relation.getTargetElement().getRef().getId())));
        assertTrue(permutation_1.getDetector().getRelationshipTemplates().removeIf(
            relation -> "11".equals(relation.getSourceElement().getRef().getId())
                && "2".equals(relation.getTargetElement().getRef().getId())));

        assertNotNull(permutation_1.getStayMappings());
        assertTrue(permutation_1.getStayMappings().removeIf(
            mapping -> "11".equals(mapping.getDetectorElement().getId())
                && "11".equals(mapping.getRefinementElement().getId())));
        assertTrue(permutation_1.getStayMappings().removeIf(
            mapping -> "12".equals(mapping.getDetectorElement().getId())
                && "12".equals(mapping.getRefinementElement().getId())));

        assertEquals(0, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_1.getRefinementTopology().getNodeTemplate("11"),
            permutation_1).size());
        assertEquals(0, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_1.getRefinementTopology().getNodeTemplate("12"),
            permutation_1).size());

        assertEquals(2, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_1.getRefinementTopology().getNodeTemplate("13"),
            permutation_1).size());
        assertEquals(1, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_1.getRefinementTopology().getNodeTemplate("14"),
            permutation_1).size());
        assertEquals(3, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_1.getRefinementTopology().getNodeTemplate("15"),
            permutation_1).size());
        assertEquals(1, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_1.getRefinementTopology().getNodeTemplate("16"),
            permutation_1).size());

        OTTopologyFragmentRefinementModel permutation_2 = permutations.get("ComplexPrmWithPatternSet_permutation-2-3-w1-wip1");
        assertNotNull(permutation_2);
        assertEquals(5, permutation_2.getDetector().getNodeTemplates().size());
        assertEquals(5, permutation_2.getDetector().getRelationshipTemplates().size());
        assertTrue(permutation_2.getDetector().getRelationshipTemplates().removeIf(
            relation -> "1".equals(relation.getSourceElement().getRef().getId())
                && "14".equals(relation.getTargetElement().getRef().getId())));
        assertTrue(permutation_2.getDetector().getRelationshipTemplates().removeIf(
            relation -> "13".equals(relation.getSourceElement().getRef().getId())
                && "14".equals(relation.getTargetElement().getRef().getId())));
        assertTrue(permutation_2.getDetector().getRelationshipTemplates().removeIf(
            relation -> "13".equals(relation.getSourceElement().getRef().getId())
                && "15".equals(relation.getTargetElement().getRef().getId())));
        assertTrue(permutation_2.getDetector().getRelationshipTemplates().removeIf(
            relation -> "15".equals(relation.getSourceElement().getRef().getId())
                && "16".equals(relation.getTargetElement().getRef().getId())));
        assertTrue(permutation_2.getDetector().getRelationshipTemplates().removeIf(
            relation -> "14".equals(relation.getSourceElement().getRef().getId())
                && "16".equals(relation.getTargetElement().getRef().getId())));

        assertNotNull(permutation_2.getStayMappings());
        assertTrue(permutation_2.getStayMappings().removeIf(
            mapping -> "13".equals(mapping.getDetectorElement().getId())
                && "13".equals(mapping.getRefinementElement().getId())));
        assertTrue(permutation_2.getStayMappings().removeIf(
            mapping -> "14".equals(mapping.getDetectorElement().getId())
                && "14".equals(mapping.getRefinementElement().getId())));
        assertTrue(permutation_2.getStayMappings().removeIf(
            mapping -> "15".equals(mapping.getDetectorElement().getId())
                && "15".equals(mapping.getRefinementElement().getId())));
        assertTrue(permutation_2.getStayMappings().removeIf(
            mapping -> "16".equals(mapping.getDetectorElement().getId())
                && "16".equals(mapping.getRefinementElement().getId())));

        assertEquals(2, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_2.getRefinementTopology().getNodeTemplate("11"),
            permutation_2).size());
        assertEquals(2, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_2.getRefinementTopology().getNodeTemplate("12"),
            permutation_2).size());
        assertEquals(0, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_2.getRefinementTopology().getNodeTemplate("13"),
            permutation_2).size());
        assertEquals(0, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_2.getRefinementTopology().getNodeTemplate("14"),
            permutation_2).size());
        assertEquals(0, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_2.getRefinementTopology().getNodeTemplate("15"),
            permutation_2).size());
        assertEquals(0, RefinementUtils.getAllMappingsForRefinementNode(
            permutation_2.getRefinementTopology().getNodeTemplate("16"),
            permutation_2).size());
    }

    @Test
    void simplePrmTest() throws Exception {
        this.setRevisionTo("origin/plain");

        OTPatternRefinementModel refinementModel = generatePrmWithoutPermutationMaps();
        PatternRefinementModelId id = new PatternRefinementModelId(refinementModel.getTargetNamespace(), refinementModel.getIdFromIdOrNameField(), false);
        RepositoryFactory.getRepository().setElement(id, refinementModel);

        PermutationGenerator generator = new PermutationGenerator();
        Map<String, OTTopologyFragmentRefinementModel> permutations = generator.generatePermutations(refinementModel);

        assertEquals(6, permutations.size());

        OTTopologyFragmentRefinementModel permutation_1 = permutations.get("SimplePrm_permutation-1-w1-wip1");
        assertNotNull(permutation_1);
        assertEquals(4, permutation_1.getDetector().getNodeTemplates().size());
        assertEquals(4, permutation_1.getDetector().getRelationshipTemplates().size());

        OTTopologyFragmentRefinementModel permutation_2 = permutations.get("SimplePrm_permutation-2-w1-wip1");
        assertNotNull(permutation_2);
        assertEquals(3, permutation_2.getDetector().getNodeTemplates().size());
        assertEquals(2, permutation_2.getDetector().getRelationshipTemplates().size());

        OTTopologyFragmentRefinementModel permutation_3 = permutations.get("SimplePrm_permutation-3-w1-wip1");
        assertNotNull(permutation_3);
        assertEquals(4, permutation_3.getDetector().getNodeTemplates().size());
        assertEquals(3, permutation_3.getDetector().getRelationshipTemplates().size());

        OTTopologyFragmentRefinementModel permutation_1_2 = permutations.get("SimplePrm_permutation-1-2-w1-wip1");
        assertNotNull(permutation_1_2);
        assertEquals(4, permutation_1_2.getDetector().getNodeTemplates().size());
        assertEquals(4, permutation_1_2.getDetector().getRelationshipTemplates().size());

        OTTopologyFragmentRefinementModel permutation_1_3 = permutations.get("SimplePrm_permutation-1-3-w1-wip1");
        assertNotNull(permutation_1_3);
        assertEquals(5, permutation_1_3.getDetector().getNodeTemplates().size());
        assertEquals(5, permutation_1_3.getDetector().getRelationshipTemplates().size());

        OTTopologyFragmentRefinementModel permutation_2_3 = permutations.get("SimplePrm_permutation-2-3-w1-wip1");
        assertNotNull(permutation_2_3);
        assertEquals(4, permutation_2_3.getDetector().getNodeTemplates().size());
        assertEquals(3, permutation_2_3.getDetector().getRelationshipTemplates().size());
    }

    @Test
    void prmWithTwoHostedOnOneTest() throws Exception {
        this.setRevisionTo("origin/plain");

        OTPatternRefinementModel refinementModel = generatePrmWithTwoPatternsHostedOnAThird();
        PatternRefinementModelId id = new PatternRefinementModelId(refinementModel.getTargetNamespace(), refinementModel.getIdFromIdOrNameField(), false);
        RepositoryFactory.getRepository().setElement(id, refinementModel);

        PermutationGenerator generator = new PermutationGenerator();
        Map<String, OTTopologyFragmentRefinementModel> permutations = generator.generatePermutations(refinementModel);

        assertEquals(6, permutations.size());

        OTTopologyFragmentRefinementModel permutation_1 = permutations.get("PrmWithComplexRelationMappings_permutation-1-w1-wip1");
        assertNotNull(permutation_1);
        assertEquals(3, permutation_1.getDetector().getNodeTemplates().size());
        assertEquals(3, permutation_1.getDetector().getRelationshipTemplates().size());
        assertTrue(permutation_1.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("11") && relation.getTargetElement().getRef().getId().equals("2")));
        assertTrue(permutation_1.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("11") && relation.getTargetElement().getRef().getId().equals("3")));

        OTTopologyFragmentRefinementModel permutation_2 = permutations.get("PrmWithComplexRelationMappings_permutation-2-w1-wip1");
        assertNotNull(permutation_2);
        assertEquals(4, permutation_2.getDetector().getNodeTemplates().size());
        assertEquals(4, permutation_2.getDetector().getRelationshipTemplates().size());
        assertTrue(permutation_2.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("1") && relation.getTargetElement().getRef().getId().equals("13")));
        assertTrue(permutation_2.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("3") && relation.getTargetElement().getRef().getId().equals("13")));

        OTTopologyFragmentRefinementModel permutation_3 = permutations.get("PrmWithComplexRelationMappings_permutation-3-w1-wip1");
        assertNotNull(permutation_3);
        assertEquals(3, permutation_3.getDetector().getNodeTemplates().size());
        assertEquals(3, permutation_3.getDetector().getRelationshipTemplates().size());
        assertTrue(permutation_3.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("12") && relation.getTargetElement().getRef().getId().equals("2")));
        assertTrue(permutation_3.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("1") && relation.getTargetElement().getRef().getId().equals("12")));

        OTTopologyFragmentRefinementModel permutation_1_2 = permutations.get("PrmWithComplexRelationMappings_permutation-1-2-w1-wip1");
        assertNotNull(permutation_1_2);
        assertEquals(4, permutation_1_2.getDetector().getNodeTemplates().size());
        assertEquals(4, permutation_1_2.getDetector().getRelationshipTemplates().size());
        assertTrue(permutation_1_2.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("11") && relation.getTargetElement().getRef().getId().equals("13")));
        assertTrue(permutation_1_2.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("11") && relation.getTargetElement().getRef().getId().equals("3")));
        assertTrue(permutation_1_2.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("3") && relation.getTargetElement().getRef().getId().equals("13")));

        OTTopologyFragmentRefinementModel permutation_1_3 = permutations.get("PrmWithComplexRelationMappings_permutation-1-3-w1-wip1");
        assertNotNull(permutation_1_3);
        assertEquals(3, permutation_1_3.getDetector().getNodeTemplates().size());
        assertEquals(3, permutation_1_3.getDetector().getRelationshipTemplates().size());
        assertTrue(permutation_1_3.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("11") && relation.getTargetElement().getRef().getId().equals("2")));
        assertTrue(permutation_1_3.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("11") && relation.getTargetElement().getRef().getId().equals("12")));
        assertTrue(permutation_1_3.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("12") && relation.getTargetElement().getRef().getId().equals("2")));

        OTTopologyFragmentRefinementModel permutation_2_3 = permutations.get("PrmWithComplexRelationMappings_permutation-2-3-w1-wip1");
        assertNotNull(permutation_2_3);
        assertEquals(4, permutation_2_3.getDetector().getNodeTemplates().size());
        assertEquals(4, permutation_2_3.getDetector().getRelationshipTemplates().size());
        assertTrue(permutation_2_3.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("1") && relation.getTargetElement().getRef().getId().equals("12")));
        assertTrue(permutation_2_3.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("1") && relation.getTargetElement().getRef().getId().equals("13")));
        assertTrue(permutation_2_3.getDetector().getRelationshipTemplates().removeIf(relation ->
            relation.getSourceElement().getRef().getId().equals("12") && relation.getTargetElement().getRef().getId().equals("13")));
    }
}
