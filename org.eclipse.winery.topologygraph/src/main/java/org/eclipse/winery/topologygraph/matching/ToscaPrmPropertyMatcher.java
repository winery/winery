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

import java.util.List;
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

        if (detectorEntityElement instanceof HasPolicies && candidateEntityElement instanceof HasPolicies) {
            HasPolicies detectorElement = (HasPolicies) detectorEntityElement;
            HasPolicies candidate = (HasPolicies) candidateEntityElement;

            if (Objects.nonNull(detectorElement.getPolicies()) && Objects.nonNull(candidate.getPolicies())) {
                List<TPolicy> candidatePolicies = candidate.getPolicies().getPolicy();
                characterizingPatternsCompatible = detectorElement.getPolicies().getPolicy()
                    .stream()
                    .allMatch(detectorPolicy -> {
                        if (this.namespaceManager.isPatternNamespace(detectorPolicy.getPolicyType().getNamespaceURI())) {
                            return candidatePolicies.stream()
                                .anyMatch(candidatePolicy -> {
                                    boolean typeEquals = candidatePolicy.getPolicyType().equals(detectorPolicy.getPolicyType());

                                    if (typeEquals && Objects.nonNull(detectorPolicy.getPolicyRef())) {
                                        return Objects.nonNull(candidatePolicy.getPolicyRef())
                                            && candidatePolicy.getPolicyRef().equals(detectorPolicy.getPolicyRef());
                                    }

                                    return typeEquals;
                                });
                        }

                        return true;
                    });
            } else if (Objects.nonNull(detectorElement.getPolicies())) {
                // only if there are patterns attached
                characterizingPatternsCompatible = detectorElement.getPolicies().getPolicy()
                    .stream()
                    .noneMatch(detectorPolicy ->
                        this.namespaceManager.isPatternNamespace(detectorPolicy.getPolicyType().getNamespaceURI())
                    );
            }
        }

        return characterizingPatternsCompatible;
    }
}
