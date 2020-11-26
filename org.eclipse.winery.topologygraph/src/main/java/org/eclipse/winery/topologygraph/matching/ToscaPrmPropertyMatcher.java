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
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.tosca.HasPolicies;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEntity;
import org.eclipse.winery.topologygraph.model.ToscaNode;

public class ToscaPrmPropertyMatcher extends ToscaTypeMatcher {

    private final List<TEntityTemplate> detectorElements;
    private final NamespaceManager namespaceManager;

    public ToscaPrmPropertyMatcher(List<TEntityTemplate> detectorElements, NamespaceManager namespaceManager) {
        this.detectorElements = detectorElements;
        this.namespaceManager = namespaceManager;
    }

    @Override
    public boolean isCompatible(ToscaNode left, ToscaNode right) {
        return super.isCompatible(left, right)
            && propertiesCompatible(left, right)
            && characterizingPatternsCompatible(left, right);
    }

    @Override
    public boolean isCompatible(ToscaEdge left, ToscaEdge right) {
        return super.isCompatible(left, right)
            && propertiesCompatible(left, right)
            && characterizingPatternsCompatible(left, right);
    }

    public boolean propertiesCompatible(ToscaEntity left, ToscaEntity right) {
        boolean propertiesCompatible = true;

        TEntityTemplate[] templates = getTemplates(left, right);
        TEntityTemplate detectorElement = templates[0];
        TEntityTemplate candidate = templates[1];

        if (Objects.nonNull(detectorElement.getProperties()) && Objects.nonNull(candidate.getProperties()) 
            // TODO the implementation (currently) works for KV properties only
            && Objects.nonNull(ModelUtilities.getPropertiesKV(detectorElement))
            && Objects.nonNull(ModelUtilities.getPropertiesKV(candidate))) {
            Map<String, String> detectorProperties = ModelUtilities.getPropertiesKV(detectorElement);
            Map<String, String> candidateProperties = ModelUtilities.getPropertiesKV(candidate);

            propertiesCompatible = detectorProperties.entrySet().stream()
                .allMatch(entry -> {
                    if (entry.getValue() == null || !(entry.getValue() instanceof String)) {
                        return true;
                    }
                    String val = (String) entry.getValue();
                    if (val.isEmpty()) { return true; }
                    // Assumption: properties are simple KV Properties
                    String refProp = (String)candidateProperties.get(entry.getKey());
                    if (val.equalsIgnoreCase("*")) {
                        // if the detector defines a wildcard, the property must be set in the candidate
                        return !refProp.isEmpty();
                    } else {
                        // if the detector defines a specific value, the candidate's property must match
                        return val.equalsIgnoreCase(refProp);
                    }
                });
        }

        return propertiesCompatible;
    }

    public boolean characterizingPatternsCompatible(ToscaEntity left, ToscaEntity right) {
        // if the detector has no patterns attached but the candidate has --> it's a match
        boolean characterizingPatternsCompatible = true;

        TEntityTemplate[] templates = getTemplates(left, right);

        if (templates[0] instanceof HasPolicies && templates[1] instanceof HasPolicies) {
            HasPolicies detectorElement = (HasPolicies) templates[0];
            HasPolicies candidate = (HasPolicies) templates[1];

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

    private TEntityTemplate[] getTemplates(ToscaEntity left, ToscaEntity right) {
        TEntityTemplate detectorNodeTemplate, candidate;

        if (this.detectorElements.contains(left.getTemplate())) {
            detectorNodeTemplate = left.getTemplate();
            candidate = right.getTemplate();
        } else {
            detectorNodeTemplate = right.getTemplate();
            candidate = left.getTemplate();
        }

        return new TEntityTemplate[] {detectorNodeTemplate, candidate};
    }
}
