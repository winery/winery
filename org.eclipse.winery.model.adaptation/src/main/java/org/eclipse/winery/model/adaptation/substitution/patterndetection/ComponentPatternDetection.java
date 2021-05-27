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

package org.eclipse.winery.model.adaptation.substitution.patterndetection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementChooser;
import org.eclipse.winery.model.adaptation.substitution.refinement.topologyrefinement.TopologyFragmentRefinement;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.ids.extensions.PatternRefinementModelId;
import org.eclipse.winery.model.tosca.HasPolicies;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.patterndetection.ComponentPatternCandidate;
import org.eclipse.winery.topologygraph.matching.patterndetection.PatternDetectionUtils;
import org.eclipse.winery.topologygraph.matching.patterndetection.ToscaComponentPatternMatcher;

class ComponentPatternDetection extends TopologyFragmentRefinement {

    private final Map<OTPatternRefinementModel,
        List<ComponentPatternCandidate>> componentPatternCandidates = new HashMap<>();
    private final Map<RefinementCandidate, RefinementCandidate> adaptedPrms = new HashMap<>();

    public ComponentPatternDetection(RefinementChooser refinementChooser) {
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

    /**
     * For tests.
     */
    protected ComponentPatternDetection(RefinementChooser refinementChooser, List<OTRefinementModel> refinementModels) {
        super(refinementChooser, PatternRefinementModelId.class, "detected");
        this.refinementModels.clear();
        this.refinementModels.addAll(refinementModels);
    }

    @Override
    protected ServiceTemplateId getSubstitutionServiceTemplateId(ServiceTemplateId serviceTemplateId) {
        /*
        BehaviorPatternDetection and ComponentPatternDetection are connected in series
        -> service template for the detected patterns is created by BehaviorPatternDetection.
        To avoid the creation of two service templates for the detected patterns,
        the ComponentPatternDetection reuses the service template created by the BehaviorPatternDetection.
         */
        return serviceTemplateId;
    }

    @Override
    public boolean getLoopCondition(TTopologyTemplate topology) {
        return true;
    }

    @Override
    public IToscaMatcher getMatcher(OTRefinementModel prm) {
        return new ToscaComponentPatternMatcher(
            (OTPatternRefinementModel) prm,
            repository.getNamespaceManager(),
            refinementModels,
            componentPatternCandidates
        );
    }

    @Override
    public boolean isApplicable(RefinementCandidate candidate, TTopologyTemplate topology) {
        RefinementCandidate adaptedPrm = adaptPrm(candidate);
        adaptedPrms.put(candidate, adaptedPrm);
        return super.isApplicable(adaptedPrm, topology);
    }

    @Override
    public Map<String, String> applyRefinement(RefinementCandidate refinement, TTopologyTemplate topology) {
        RefinementCandidate adaptedPrm = adaptedPrms.get(refinement);
        componentPatternCandidates.clear();
        adaptedPrms.clear();
        return super.applyRefinement(adaptedPrm, topology);
    }

    @Override
    protected boolean shouldRemoveBehaviorPatterns(TNodeTemplate vertex, TNodeTemplate matchingNode) {
        return false;
    }

    private RefinementCandidate adaptPrm(RefinementCandidate candidate) {
        OTPatternRefinementModel prm = (OTPatternRefinementModel) candidate.getRefinementModel();
        OTPatternRefinementModel adaptedPrm = PatternDetectionUtils.clone(prm);

        componentPatternCandidates.get(prm).forEach(componentPatternCandidate ->
            addRelationMappings(componentPatternCandidate, adaptedPrm)
        );
        removeNotApplicable(adaptedPrm);
        return new RefinementCandidate(adaptedPrm, candidate.getGraphMapping(), candidate.getDetectorGraph(), candidate.getId());
    }

    private void addRelationMappings(ComponentPatternCandidate componentPatternCandidate, OTPatternRefinementModel adaptedPrm) {
        TNodeTemplate detectorElement = componentPatternCandidate.getDetectorElement();
        TNodeTemplate refinementElement = componentPatternCandidate.getRefinementElement();
        OTPatternRefinementModel otherPrm = componentPatternCandidate.getOtherPrm();
        TNodeTemplate otherDetectorElement = componentPatternCandidate.getOtherDetectorElement();

        // Redirect and add Relation Mappings of pdrm_x
        if (otherPrm.getRelationMappings() != null) {
            otherPrm.getRelationMappings().stream()
                .filter(mapping -> mapping.getDetectorElement().getId().equals(otherDetectorElement.getId())
                    && mapping.getRefinementElement().getType().equals(refinementElement.getType()))
                .forEach(mapping -> {
                    mapping.setDetectorElement(detectorElement);
                    mapping.setRefinementElement(refinementElement);
                    adaptedPrm.getRelationMappings().add(mapping);
                });
        }
    }

    private void removeNotApplicable(OTPatternRefinementModel adaptedPrm) {
        // Remove Property Mappings of pdrm
        if (adaptedPrm.getAttributeMappings() != null) {
            adaptedPrm.getAttributeMappings().clear();
        }

        // Remove all Behavior Patterns not in the Executable Structure
        Set<QName> detectorPolicies = adaptedPrm.getDetector().getNodeTemplateOrRelationshipTemplate().stream()
            .map(HasPolicies.class::cast)
            .filter(hasPolicies -> hasPolicies.getPolicies() != null)
            .map(hasPolicies -> hasPolicies.getPolicies().getPolicy())
            .flatMap(List::stream)
            .map(TPolicy::getPolicyType)
            .collect(Collectors.toSet());
        adaptedPrm.getRefinementStructure().getNodeTemplateOrRelationshipTemplate().stream()
            .map(HasPolicies.class::cast)
            .filter(hasPolicies -> hasPolicies.getPolicies() != null)
            .forEach(hasPolicies -> hasPolicies.getPolicies().getPolicy()
                .removeIf(policy -> !detectorPolicies.contains(policy.getPolicyType())));
    }
}
