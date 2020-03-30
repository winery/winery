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

package org.eclipse.winery.repository.targetallocation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.exceptions.AllocationException;
import org.eclipse.winery.repository.splitting.ProviderRepository;

/**
 * Enables calculating all possible matching PaaS fragments of a topology
 * while caching these fragments.
 */
public class FragmentsCache {

    private Set<String> presentTargetLabels;
    private Map<CachingKey, List<TTopologyTemplate>> cachedFragments = new HashMap<>();

    public FragmentsCache(Set<String> presentTargetLabels) {
        this.presentTargetLabels = presentTargetLabels;
    }

    /**
     * @param topLevelNT nt to start traversing from
     * @return all successors (including nt itself) and their matching fragments
     */
    public Map<TNodeTemplate, List<TTopologyTemplate>> getAllMatchingFragments(TopologyWrapper topology, TNodeTemplate topLevelNT)
        throws AllocationException {
        Set<String> targetLabels = new HashSet<>();
        if (ModelUtilities.getTargetLabel(topLevelNT).isPresent()) {
            targetLabels.add(ModelUtilities.getTargetLabel(topLevelNT).get());
        } else {
            targetLabels = presentTargetLabels;
        }

        Map<TNodeTemplate, List<TTopologyTemplate>> allFragments = new HashMap<>();
        TNodeTemplate next = topLevelNT;
        while (next != null) {
            for (String targetLabel : targetLabels) {
                List<TRequirement> requirements = topology.getRequirements(next);
                // use QName type to avoid checking all variables of the requirements with equals
                Set<QName> reqTypes = requirements.stream().map(TRequirement::getType).collect(Collectors.toSet());

                List<TTopologyTemplate> fragments;
                CachingKey cachingKey = new CachingKey(targetLabel, reqTypes);
                if (cachedFragments.containsKey(cachingKey)) {
                    fragments = cachedFragments.get(cachingKey);
                } else {
                    fragments = ProviderRepository.INSTANCE.getPaaSFragments(targetLabel, requirements);
                    cachedFragments.put(cachingKey, fragments);
                }
                if (fragments.isEmpty()) {
                    continue;
                }

                if (allFragments.get(next) == null) {
                    allFragments.put(next, new ArrayList<>(fragments));
                } else {
                    allFragments.get(next).addAll(fragments);
                }
            }
            next = topology.getHostedOnSuccessor(next);
        }
        if (allFragments.values().stream().flatMap(List::stream).count() == 0) {
            throw new AllocationException("No matching fragments found for NT " + topLevelNT.getId() +
                " with target labels " + targetLabels);
        }
        return allFragments;
    }

    private static class CachingKey {
        private String targetLabel;
        private Set<QName> requirementTypes;

        private CachingKey(String targetLabel, Set<QName> requirementTypes) {
            this.targetLabel = targetLabel;
            this.requirementTypes = requirementTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CachingKey that = (CachingKey) o;
            return Objects.equals(targetLabel, that.targetLabel) &&
                Objects.equals(requirementTypes, that.requirementTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetLabel, requirementTypes);
        }
    }
}
