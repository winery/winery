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

package org.eclipse.winery.repository.targetallocation.criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.splitting.InjectRemoval;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;
import org.eclipse.winery.repository.targetallocation.util.FragmentsCache;
import org.eclipse.winery.repository.targetallocation.util.PermutationHelper;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Common methods and variables for
 * {@link org.eclipse.winery.repository.targetallocation.criteria.fulfillpolicies.FulfillPolicies} and
 * {@link org.eclipse.winery.repository.targetallocation.criteria.minexternalconnections.MinExternalConnections}.
 */
public abstract class CriteriaCached extends CriteriaCommon {

    protected FragmentsCache fragmentsCache;

    public CriteriaCached(JsonNode params, int outputCap) {
        super(params, outputCap);
        // not needed NTs will be removed after topology generation
        injectRemoval = InjectRemoval.REMOVE_NOTHING;
    }

    @Override
    public List<TopologyWrapper> allocate(TopologyWrapper topology) throws Exception {
        Set<String> originalTopLevelNTIds = topology.getTopLevelNTs().stream().map(HasId::getId).collect(Collectors.toSet());
        fragmentsCache = new FragmentsCache(topology.getPresentTargetLabels());
        List<TopologyWrapper> generatedTopologies = super.allocate(topology);
        // remove NTs that were no top level nts originally but are now because of injection
        generatedTopologies.forEach(t -> t.removeNotNeededNTs(originalTopLevelNTIds));
        return generatedTopologies;
    }

    @Override
    protected List<List<PermutationHelper>> getPermutations(List<List<PermutationHelper>> possibilities) {
        int generated = amountAllocated;
        // first permutation: determine where to inject nts
        List<List<PermutationHelper>> permutationsWithDuplicates =
            AllocationUtils.getPermutations(possibilities, outputCap - generated);
        List<List<PermutationHelper>> permutations = new ArrayList<>();

        for (List<PermutationHelper> permutation : permutationsWithDuplicates) {
            // duplicates can be present because topology was traversed from top level nts independently
            Set<PermutationHelper> removeDuplicates = new HashSet<>(permutation);

            List<List<PermutationHelper>> newPossibilities = new ArrayList<>();
            for (PermutationHelper permutationHelper : removeDuplicates) {
                List<PermutationHelper> possibility = new ArrayList<>();
                for (TTopologyTemplate replacement : permutationHelper.getReplacements()) {
                    possibility.add(new PermutationHelper(permutationHelper.getCorrespondingNT(), replacement));
                }
                newPossibilities.add(possibility);
            }
            // second permutation: get actual injections
            List<List<PermutationHelper>> generatedPermutations =
                AllocationUtils.getPermutations(newPossibilities, outputCap - generated);
            generated += generatedPermutations.size();
            permutations.addAll(generatedPermutations);
        }
        return permutations;
    }

    @Override
    protected Map<String, TTopologyTemplate> toInjectParameter(TopologyWrapper topology, List<PermutationHelper> permutation) {
        Map<String, TTopologyTemplate> injectParameter = new HashMap<>();
        Map<PermutationHelper, TNodeTemplate> successors = new HashMap<>();
        List<PermutationHelper> done = new ArrayList<>();

        // check predecessors of successors of nts to replace to see which nts should be hosted together
        for (PermutationHelper possibility : permutation) {
            TNodeTemplate successor = topology.getHostedOnSuccessor(possibility.getCorrespondingNT());
            if (successor == null) {
                // no successor -> no check needed
                injectParameter.put(possibility.getCorrespondingNT().getId(), possibility.getReplacement());
            } else {
                successors.put(possibility, successor);
            }
        }

        // group nts that should be hosted together
        for (Map.Entry<PermutationHelper, TNodeTemplate> entry : successors.entrySet()) {
            List<PermutationHelper> hostTogether = new ArrayList<>();
            for (Map.Entry<PermutationHelper, TNodeTemplate> entry2 : successors.entrySet()) {
                if (entry.getKey().getReplacement().equals(entry2.getKey().getReplacement()) &&
                    entry.getValue().equals(entry2.getValue()) &&
                    !done.contains(entry2.getKey())) {
                    hostTogether.add(entry2.getKey());
                }
            }

            // the same matching nt is injected for all nts that should be hosted together
            TTopologyTemplate replacement = AllocationUtils.deepcopy(entry.getKey().getReplacement());
            for (PermutationHelper permutationHelper : hostTogether) {
                injectParameter.put(permutationHelper.getCorrespondingNT().getId(), replacement);
                done.add(permutationHelper);
            }
        }
        return injectParameter;
    }
}
