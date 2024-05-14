/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologygraph.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.tosca.HasPolicies;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEntity;
import org.eclipse.winery.topologygraph.model.ToscaNode;

public class ToscaPrmPropertyMatcher extends ToscaPropertyMatcher {

    private final NamespaceManager namespaceManager;

    private final Map<String, List<String>> warnings = new HashMap<>();

    public ToscaPrmPropertyMatcher(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    @Override
    public boolean isCompatible(ToscaNode left, ToscaNode right) {
        return super.isCompatible(left, right)
            && characterizingPatternsCompatible(left, right);
    }

    @Override
    public boolean isCompatible(ToscaEdge left, ToscaEdge right) {
        return super.isCompatible(left, right)
            && characterizingPatternsCompatible(left, right);
    }

    public boolean characterizingPatternsCompatible(ToscaEntity left, ToscaEntity right) {
        // By convention, the left node is always the element to search in right.
        TEntityTemplate detectorEntityElement = left.getTemplate();
        TEntityTemplate candidateEntityElement = right.getTemplate();
        return characterizingPatternsCompatible(detectorEntityElement, candidateEntityElement);
    }

    public boolean characterizingPatternsCompatible(TEntityTemplate detectorEntityElement, TEntityTemplate candidateEntityElement) {
        // if the detector has no patterns attached but the candidate has --> it's a match
        boolean characterizingPatternsCompatible = true;

        if (detectorEntityElement instanceof HasPolicies detectorElement
            && candidateEntityElement instanceof HasPolicies candidate) {

            List<TPolicy> unMatchedBehaviorPatterns = new ArrayList<>();
            if (candidate.getPolicies() != null) {
                for (TPolicy policy : candidate.getPolicies()) {
                    if (this.namespaceManager.isPatternNamespace(policy.getPolicyType().getNamespaceURI())) {
                        unMatchedBehaviorPatterns.add(policy);
                    }
                }
            }

            if (Objects.nonNull(detectorElement.getPolicies()) && Objects.nonNull(candidate.getPolicies())) {
                List<TPolicy> candidatePolicies = candidate.getPolicies();
                characterizingPatternsCompatible = detectorElement.getPolicies()
                    .stream()
                    .allMatch(detectorPolicy -> {
                        if (this.namespaceManager.isPatternNamespace(detectorPolicy.getPolicyType().getNamespaceURI())) {
                            return candidatePolicies.stream()
                                .anyMatch(candidatePolicy -> {
                                    boolean typeEquals = candidatePolicy.getPolicyType().equals(detectorPolicy.getPolicyType());

                                    if (typeEquals && Objects.nonNull(detectorPolicy.getPolicyRef())) {
                                        return Objects.nonNull(candidatePolicy.getPolicyRef())
                                            && candidatePolicy.getPolicyRef().equals(detectorPolicy.getPolicyRef())
                                            && unMatchedBehaviorPatterns.remove(candidatePolicy);
                                    } else if (typeEquals) {
                                        unMatchedBehaviorPatterns.remove(candidatePolicy);
                                    }

                                    return typeEquals;
                                });
                        }

                        return true;
                    });
            } else if (Objects.nonNull(detectorElement.getPolicies())) {
                // only if there are patterns attached
                characterizingPatternsCompatible = detectorElement.getPolicies()
                    .stream()
                    .noneMatch(detectorPolicy ->
                        this.namespaceManager.isPatternNamespace(detectorPolicy.getPolicyType().getNamespaceURI())
                    );
            }

            if (!unMatchedBehaviorPatterns.isEmpty()) {
                unMatchedBehaviorPatterns.forEach(policy -> {
                    List<String> matchingWarnings = this.warnings.computeIfAbsent(candidateEntityElement.getId(), k -> new ArrayList<>());
                    matchingWarnings.add("The Behavior Pattern \"" + policy.getPolicyType().getLocalPart() + "\" named \"" + policy.getName()
                        + "\" was not matched by this PRM and will not be refined!");
                });
            }
        }

        return characterizingPatternsCompatible;
    }

    @Override
    public Map<String, List<String>> getWarnings() {
        return this.warnings;
    }
}
