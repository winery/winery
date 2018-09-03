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

package org.eclipse.winery.repository.targetallocation.criteria.minhosts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.exceptions.AllocationException;
import org.eclipse.winery.repository.splitting.InjectRemoval;
import org.eclipse.winery.repository.splitting.ProviderRepository;
import org.eclipse.winery.repository.targetallocation.criteria.CriteriaCommon;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;
import org.eclipse.winery.repository.targetallocation.util.PermutationHelper;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Distribute present target labels with the objective of having a minimal amount of Node Templates in the topology.
 * Two factors have to be considered for this:
 * - the split of a topology increases the amount of Node Templates drastically -> target labels are assigned so that
 * Node Templates on the same host have the same target label to avoid the split
 * - the topology is traversed from top to bottom and the first match is selected
 */
public class MinHosts extends CriteriaCommon {

    public MinHosts(JsonNode params, int outputCap) {
        super(params, outputCap);
        injectRemoval = InjectRemoval.REMOVE_REPLACED;
    }

    /**
     * Filter the topologies based on their Node Template count.
     *
     * @return all topologies with minimum amount of Node Templates
     */
    @Override
    public List<TopologyWrapper> filter(List<TopologyWrapper> topologies) {
        int minCount = Collections.min(topologies, Comparator.comparingInt(t -> t.getNodeTemplates().size()))
            .getNodeTemplates().size();
        return topologies.stream().filter(t -> t.getNodeTemplates().size() == minCount).collect(Collectors.toList());
    }

    @Override
    public List<TopologyWrapper> generateTargetLabelTopologies(TopologyWrapper topology) {
        return new TargetLabelAssignment(topology, outputCap).computeTargetLabelPermutations();
    }

    /**
     * Calculate all possible replacements for a topology by traversing starting from the top level hosts
     * and selecting first match.
     */
    @Override
    protected List<List<PermutationHelper>> getPossibleMatches(TopologyWrapper topology) throws AllocationException {
        Map<TNodeTemplate, List<TTopologyTemplate>> possibleReplacements = new HashMap<>();
        List<TNodeTemplate> done = new ArrayList<>();

        for (TNodeTemplate topLevelHost : topology.getTopLevelHosts()) {
            TNodeTemplate next = topLevelHost;
            // at this point all top level nts have been assigned target labels by TargetLabelAssignment
            String targetLabel = ModelUtilities.getTargetLabel(topology.getHostedOnPredecessors(topLevelHost).get(0)).get();
            List<TTopologyTemplate> possibleReplacementsForNT = null;

            while (next != null && !done.contains(next)) {
                possibleReplacementsForNT = ProviderRepository.INSTANCE
                    .getPaaSFragments(targetLabel, topology.getPredecessorRequirements(next));

                if (!possibleReplacementsForNT.isEmpty()) {
                    possibleReplacements.put(next, possibleReplacementsForNT);
                    done.add(next);
                    done.addAll(topology.getTransitiveTopLevelHostPredecessors(next));
                    // needed because in case all predecessors had target labels not all nts were removed during assignment
                    topology.removeNotNeededSuccessors(next);
                    break;
                }
                next = topology.getHostedOnSuccessor(next);
            }
            if (!done.contains(next) &&
                (possibleReplacementsForNT == null || possibleReplacementsForNT.isEmpty())) {
                throw new AllocationException("No matching fragments found");
            }
        }
        return toPermutationHelpers(possibleReplacements);
    }

    @Override
    protected List<List<PermutationHelper>> getPermutations(List<List<PermutationHelper>> possibilities) {
        return AllocationUtils.getPermutations(possibilities, outputCap - amountAllocated);
    }

    /**
     * Wrap possible replacements for permutation algorithm.
     */
    private List<List<PermutationHelper>> toPermutationHelpers(Map<TNodeTemplate, List<TTopologyTemplate>> possibleReplacements) {
        List<List<PermutationHelper>> possibilities = new ArrayList<>();
        for (Map.Entry<TNodeTemplate, List<TTopologyTemplate>> entry : possibleReplacements.entrySet()) {
            List<PermutationHelper> possibilitiesForNT = new ArrayList<>();
            for (TTopologyTemplate possibility : entry.getValue()) {
                possibilitiesForNT.add(new PermutationHelper(entry.getKey(), possibility));
            }
            possibilities.add(possibilitiesForNT);
        }
        return possibilities;
    }

    @Override
    protected Map<String, TTopologyTemplate> toInjectParameter(TopologyWrapper topology, List<PermutationHelper> permutation) {
        Map<String, TTopologyTemplate> injectParameter = new HashMap<>();
        for (PermutationHelper possibility : permutation) {
            // all predecessors of the nt to replace get the same matching nt
            TTopologyTemplate clone = AllocationUtils.deepcopy(possibility.getReplacement());
            for (TNodeTemplate predecessor : topology.getHostedOnPredecessors(possibility.getCorrespondingNT())) {
                injectParameter.put(predecessor.getId(), clone);
            }
        }
        return injectParameter;
    }
}
