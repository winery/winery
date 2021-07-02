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
import java.util.Map;

import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementChooser;
import org.eclipse.winery.model.adaptation.substitution.refinement.topologyrefinement.TopologyFragmentRefinement;
import org.eclipse.winery.model.ids.extensions.PatternRefinementModelId;
import org.eclipse.winery.model.tosca.HasPolicies;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.patterndetection.PatternDetectionUtils;
import org.eclipse.winery.topologygraph.matching.patterndetection.ToscaBehaviorPatternMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEntity;

import static org.eclipse.winery.topologygraph.matching.patterndetection.PatternDetectionUtils.getEntityCorrespondence;

class BehaviorPatternDetection extends TopologyFragmentRefinement {

    public BehaviorPatternDetection(RefinementChooser refinementChooser) {
        super(refinementChooser, PatternRefinementModelId.class, "detected");
        /*
        All PRMs with isPdrm=true are considered to be PDRMs.
        During pattern refinement, the Refinement Structure replaces a matching subgraph.
        During pattern detection, the Detector replaces a matching subgraph.
        To reuse the pattern refinement implementation for pattern detection, i.e., use the implementation to replace a 
        matching subgraph with the Detector of a PRM instead of its Refinement Structure, the two structures are swapped.
        Therefore, the following applies:
            - in the UI, in the respective files, and in the model the Detector and Refinement Structure of PRMs remain unchanged
            - in the PatternDetection classes and the Matcher classes, the PRMs retrieved from the repo are swapped
            - the PRMs created during test setups are defined as usual and then swapped before/during the test execution
         */
        PatternDetectionUtils.toPdrms(this.refinementModels);
    }

    @Override
    public boolean getLoopCondition(TTopologyTemplate topology) {
        return true;
    }

    @Override
    public IToscaMatcher getMatcher(OTRefinementModel prm) {
        return new ToscaBehaviorPatternMatcher((OTPatternRefinementModel) prm, repository.getNamespaceManager());
    }

    @Override
    public boolean isApplicable(RefinementCandidate candidate, TTopologyTemplate topology) {
        return super.isApplicable(candidate, topology);
    }

    @Override
    public Map<String, String> applyRefinement(RefinementCandidate refinement, TTopologyTemplate topology) {
        Map<String, String> idMapping = super.applyRefinement(refinement, topology);
        OTPatternRefinementModel prm = (OTPatternRefinementModel) refinement.getRefinementModel();

        prm.getRefinementStructure().getNodeTemplateOrRelationshipTemplate()
            .forEach(refinementElement -> {
                String newId = idMapping.get(refinementElement.getId());
                boolean isStayingElement = newId == null;

                if (isStayingElement) {
                    addCompatibleBehaviorPatterns(refinementElement, refinement);
                } else {
                    TEntityTemplate addedElement = topology.getNodeTemplateOrRelationshipTemplate(newId);
                    removeIncompatibleBehaviorPatterns(refinementElement, addedElement, refinement);
                }
            });
        return idMapping;
    }

    @Override
    protected boolean shouldRemoveBehaviorPatterns(TNodeTemplate vertex, TNodeTemplate matchingNode) {
        return false;
    }

    private void addCompatibleBehaviorPatterns(TEntityTemplate refinementElement, RefinementCandidate refinement) {
        OTPatternRefinementModel prm = (OTPatternRefinementModel) refinement.getRefinementModel();
        TEntityTemplate detectorElement = prm.getStayMappings().stream()
            .filter(stayMapping -> stayMapping.getRefinementElement().getId().equals(refinementElement.getId()))
            .findFirst().get()
            .getDetectorElement();
        ToscaEntity detectorEntity = refinement.getDetectorGraph().getVertexOrEdge(detectorElement.getId()).get();
        TEntityTemplate stayingElement = getEntityCorrespondence(detectorEntity, refinement.getGraphMapping());

        TPolicies refinementPolicies = ((HasPolicies) refinementElement).getPolicies();
        TPolicies stayingPolicies = ((HasPolicies) stayingElement).getPolicies();
        if (refinementPolicies != null) {
            if (stayingPolicies != null) {
                // avoid duplicates
                refinementPolicies.getPolicy().forEach(refinementPolicy -> {
                    boolean policyExists = stayingPolicies.getPolicy().stream()
                        .anyMatch(stayingPolicy -> stayingPolicy.getPolicyType().equals(refinementPolicy.getPolicyType()));
                    if (!policyExists) {
                        stayingPolicies.getPolicy().add(refinementPolicy);
                    }
                });
            } else {
                ((HasPolicies) stayingElement)
                    .setPolicies(new TPolicies(new ArrayList<>(refinementPolicies.getPolicy())));
            }
            removeIncompatibleBehaviorPatterns(refinementElement, stayingElement, refinement);
        }
    }

    private void removeIncompatibleBehaviorPatterns(TEntityTemplate refinementElement, TEntityTemplate addedElement,
                                                    RefinementCandidate refinement) {
        OTPatternRefinementModel prm = (OTPatternRefinementModel) refinement.getRefinementModel();
        TPolicies addedElementPolicies = ((HasPolicies) addedElement).getPolicies();

        prm.getBehaviorPatternMappings().stream()
            .filter(bpm -> bpm.getRefinementElement().getId().equals(refinementElement.getId()))
            .forEach(bpm -> {
                ToscaEntity detectorElement = refinement.getDetectorGraph()
                    .getVertexOrEdge(bpm.getDetectorElement().getId()).get();
                TEntityTemplate candidateElement = getEntityCorrespondence(detectorElement, refinement.getGraphMapping());

                if (ModelUtilities.hasKvProperties(detectorElement.getTemplate())
                    && ModelUtilities.hasKvProperties(candidateElement)) {
                    String detectorValue = ModelUtilities.getPropertiesKV(detectorElement.getTemplate())
                        .get(bpm.getProperty().getKey());
                    String candidateValue = ModelUtilities.getPropertiesKV(candidateElement)
                        .get(bpm.getProperty().getKey());
                    boolean propsNotCompatible = (detectorValue != null && !detectorValue.isEmpty())
                        && !detectorValue.equalsIgnoreCase(candidateValue)
                        && (!detectorValue.equals("*") || (candidateValue == null || candidateValue.isEmpty()));

                    if (propsNotCompatible) {
                        TPolicy behaviorPattern = ((HasPolicies) refinementElement).getPolicies().getPolicy().stream()
                            .filter(policy -> bpm.getBehaviorPattern().equals(policy.getName()))
                            .findFirst().get();
                        addedElementPolicies.getPolicy()
                            .removeIf(policy -> policy.getPolicyType().equals(behaviorPattern.getPolicyType()));
                    }
                }
            });
    }
}
