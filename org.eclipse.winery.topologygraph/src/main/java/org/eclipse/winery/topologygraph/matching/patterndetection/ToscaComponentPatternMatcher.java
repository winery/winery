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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEntity;
import org.eclipse.winery.topologygraph.model.ToscaNode;

public class ToscaComponentPatternMatcher extends ToscaPatternMatcher {

    private final List<OTRefinementModel> refinementModels;
    private final Map<OTPatternRefinementModel, List<ComponentPatternCandidate>> componentPatternCandidates;

    public ToscaComponentPatternMatcher(OTPatternRefinementModel prm, NamespaceManager namespaceManager,
                                        List<OTRefinementModel> refinementModels,
                                        Map<OTPatternRefinementModel,
                                            List<ComponentPatternCandidate>> componentPatternCandidates) {
        super(prm, namespaceManager);
        this.refinementModels = refinementModels;
        this.componentPatternCandidates = componentPatternCandidates;
        this.componentPatternCandidates.put(prm, new ArrayList<>());
    }

    @Override
    public boolean isCompatible(ToscaNode left, ToscaNode right) {
        return (super.isCompatible(left, right)
            || (!isStayingElement(left) && (shareSupertype(left, right) || componentPatternsCompatible(left, right))));
    }

    @Override
    public boolean isCompatible(ToscaEdge left, ToscaEdge right) {
        return (super.isCompatible(left, right)
            || (!isStayingElement(left) && (shareSupertype(left, right) || componentPatternsCompatible(left, right))));
    }

    protected boolean isStayingElement(ToscaEntity detector) {
        return prm.getStayMappings() != null && prm.getStayMappings().stream()
            .anyMatch(mapping -> mapping.getDetectorElement().getId().equals(detector.getId()));
    }

    protected boolean shareSupertype(ToscaEntity left, ToscaEntity right) {
        if (isLinkedToComponentPattern(left)) {
            return false;
        }
        Set<QName> leftTypes = left.getTypes().stream()
            .map(TEntityType::getQName)
            .collect(Collectors.toSet());
        Set<QName> rightTypes = right.getTypes().stream()
            .map(TEntityType::getQName)
            .collect(Collectors.toSet());

        // the elements are compatible if they share a common supertype
        leftTypes.retainAll(rightTypes);
        return !leftTypes.isEmpty()
            && behaviorPatternsCompatible(left, right);
    }

    protected boolean isLinkedToComponentPattern(ToscaEntity detector) {
        return prm.getPermutationMappings() != null && prm.getPermutationMappings().stream()
            .anyMatch(mapping -> mapping.getDetectorElement().getId().equals(detector.getId()));
    }

    protected boolean componentPatternsCompatible(ToscaEntity left, ToscaEntity right) {
        // By convention, the left node is always the element to search in right.
        TEntityTemplate detectorElement = left.getTemplate();
        TEntityTemplate candidateElement = right.getTemplate();
        if (!(detectorElement instanceof TNodeTemplate) || !(candidateElement instanceof TNodeTemplate)) {
            return false;
        }

        if (prm.getPermutationMappings() == null || prm.getPermutationMappings().isEmpty()) {
            return false;
        }
        List<OTPermutationMapping> detectorMappings = prm.getPermutationMappings().stream()
            .filter(mapping -> isOneToOne(mapping, prm)
                && mapping.getDetectorElement().getId().equals(detectorElement.getId()))
            .collect(Collectors.toList());
        if (detectorMappings.isEmpty()) {
            return false;
        }
        TEntityTemplate refinementElement = detectorMappings.get(0).getRefinementElement();

        return refinementModels.stream()
            .map(OTPatternRefinementModel.class::cast)
            .filter(otherPrm -> otherPrm.getPermutationMappings() != null
                && !otherPrm.getPermutationMappings().isEmpty())
            .anyMatch(otherPrm -> {
                List<OTPermutationMapping> matchingDetectorMappings = otherPrm.getPermutationMappings().stream()
                    .filter(mapping -> isOneToOne(mapping, otherPrm)
                        && super.isCompatible(mapping.getDetectorElement(), candidateElement)
                        && mapping.getRefinementElement().getType().equals(refinementElement.getType()))
                    .collect(Collectors.toList());

                if (!matchingDetectorMappings.isEmpty()) {
                    componentPatternCandidates.get(prm).add(new ComponentPatternCandidate(
                        (TNodeTemplate) detectorElement,
                        (TNodeTemplate) refinementElement,
                        otherPrm,
                        (TNodeTemplate) matchingDetectorMappings.get(0).getDetectorElement()
                    ));
                    return true;
                }
                return false;
            });
    }

    /**
     * Only permutation mappings with 1:1 correspondence are supported
     */
    private boolean isOneToOne(OTPermutationMapping permutationMapping, OTRefinementModel prm) {
        long detectorMappings = prm.getPermutationMappings().stream()
            .filter(mapping -> mapping.getDetectorElement().equals(permutationMapping.getDetectorElement()))
            .count();
        long refinementMappings = prm.getPermutationMappings().stream()
            .filter(mapping -> mapping.getRefinementElement().equals(permutationMapping.getRefinementElement()))
            .count();
        return detectorMappings == 1 && refinementMappings == 1;
    }
}
