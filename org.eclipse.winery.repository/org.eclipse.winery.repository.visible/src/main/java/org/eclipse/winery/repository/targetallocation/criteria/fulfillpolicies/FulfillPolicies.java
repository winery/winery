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

package org.eclipse.winery.repository.targetallocation.criteria.fulfillpolicies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.exceptions.AllocationException;
import org.eclipse.winery.repository.splitting.SplittingException;
import org.eclipse.winery.repository.targetallocation.criteria.CriteriaCached;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;
import org.eclipse.winery.repository.targetallocation.util.FragmentsCache;
import org.eclipse.winery.repository.targetallocation.util.PermutationHelper;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Create topologies based on non-functional requirements specified in the GUI.
 */
public class FulfillPolicies extends CriteriaCached {

    public FulfillPolicies(JsonNode params, int outputCap) {
        super(params, outputCap);
    }

    @Override
    public List<TopologyWrapper> filter(List<TopologyWrapper> topologies) {
        // clone to avoid altering input topologies
        List<TopologyWrapper> cloned = topologies.stream().map(t ->
            new TopologyWrapper(AllocationUtils.deepcopy(t.getTopology()))).collect(Collectors.toList());
        // all generated topologies have the same policies for top level nts -> get policies from any of them
        Map<TNodeTemplate, List<PolicyWrapper>> policiesForNTs = getPoliciesForNTs(cloned.get(0), params);
        return new PoliciesFilter(cloned, policiesForNTs).filter();
    }

    @Override
    public List<TopologyWrapper> generateTargetLabelTopologies(TopologyWrapper topology) throws AllocationException {
        if (fragmentsCache == null) {
            fragmentsCache = new FragmentsCache(topology.getPresentTargetLabels());
        }
        Map<TNodeTemplate, Set<String>> possibleTargetLabels = assignTargetLabels(topology);
        List<List<PermutationHelper>> possibilities = new ArrayList<>();

        for (Map.Entry<TNodeTemplate, Set<String>> entry : possibleTargetLabels.entrySet()) {
            List<PermutationHelper> possibility = new ArrayList<>();
            for (String targetLabel : entry.getValue()) {
                possibility.add(new PermutationHelper(entry.getKey(), targetLabel));
            }
            possibilities.add(possibility);
        }
        List<List<PermutationHelper>> permutations = AllocationUtils.getPermutations(possibilities, outputCap);
        return AllocationUtils.generateTargetLabelTopologies(topology, permutations);
    }

    private Map<TNodeTemplate, Set<String>> assignTargetLabels(TopologyWrapper topology) throws AllocationException {
        Map<TNodeTemplate, Set<String>> possibleTargetLabels = new HashMap<>();
        Map<TNodeTemplate, List<PolicyWrapper>> policiesForNTs = getPoliciesForNTs(topology, params);

        for (TNodeTemplate topLevelNT : topology.getTopLevelNTs()) {
            // set present target labels
            Optional<String> targetLabelOptional = ModelUtilities.getTargetLabel(topLevelNT);
            if (targetLabelOptional.isPresent()) {
                possibleTargetLabels.put(topLevelNT, Collections.singleton(targetLabelOptional.get()));
                continue;
            } else {
                possibleTargetLabels.put(topLevelNT, new HashSet<>());
            }

            // get matching fragments
            List<TTopologyTemplate> matchingFragments = fragmentsCache.getAllMatchingFragments(topology, topLevelNT)
                .values().stream().flatMap(List::stream).collect(Collectors.toList());
            // remove matching fragments that don't fulfill the policies
            matchingFragments = new PolicyComparison(policiesForNTs.get(topLevelNT),
                matchingFragments).getFragmentsFulfillingPolicies();
            for (TTopologyTemplate fragment : matchingFragments) {
                String targetLabel = ModelUtilities.getTargetLabel(fragment.getNodeTemplates().get(0)).get();
                possibleTargetLabels.get(topLevelNT).add(targetLabel);
            }

            if (possibleTargetLabels.get(topLevelNT).isEmpty()) {
                throw new AllocationException("Couldn't find fragments from providers fulfilling specified policies");
            }
        }
        return possibleTargetLabels;
    }

    @Override
    protected List<List<PermutationHelper>> getPossibleMatches(TopologyWrapper topology) throws Exception {
        List<List<PermutationHelper>> possibilities = new ArrayList<>();
        Map<TNodeTemplate, List<PolicyWrapper>> policiesForNTs = getPoliciesForNTs(topology, params);

        for (TNodeTemplate topLevelNT : topology.getTopLevelNTs()) {
            // get matching fragments
            Map<TNodeTemplate, List<TTopologyTemplate>> fragments =
                fragmentsCache.getAllMatchingFragments(topology, topLevelNT);
            // remove matching fragments that don't fulfill the policies
            List<TTopologyTemplate> allFragments = fragments.values().stream()
                .flatMap(List::stream).collect(Collectors.toList());
            List<TTopologyTemplate> fragmentsFulfilling = new PolicyComparison(policiesForNTs.get(topLevelNT), allFragments)
                .getFragmentsFulfillingPolicies();

            Map<TNodeTemplate, List<TTopologyTemplate>> fragmentsFulfillingPoliciesWithNTs
                = mapFragmentsByNTs(fragments, fragmentsFulfilling);
            List<PermutationHelper> possibility = new ArrayList<>();
            for (Map.Entry<TNodeTemplate, List<TTopologyTemplate>> entry : fragmentsFulfillingPoliciesWithNTs.entrySet()) {
                possibility.add(new PermutationHelper(entry.getKey(), entry.getValue()));
            }
            possibilities.add(possibility);
        }
        return possibilities;
    }

    /**
     * Get policy templates by names specified in GUI.
     * The Policy Template names are used as IDs -> have to be unique.
     */
    private Map<TNodeTemplate, List<PolicyWrapper>> getPoliciesForNTs(TopologyWrapper topology, JsonNode params) {
        Map<TNodeTemplate, List<PolicyWrapper>> policiesForNTs = new HashMap<>();
        for (TNodeTemplate topLevelNT : topology.getTopLevelNTs()) {
            policiesForNTs.put(topLevelNT, new ArrayList<>());
            if (topLevelNT.getPolicies() == null) {
                continue;
            }

            for (JsonNode policyWithOperator : params.get("policySelection")) {
                String propertyKey = policyWithOperator.get("property").asText();
                String operator = policyWithOperator.get("operator").asText();

                for (TPolicy policy : topLevelNT.getPolicies().getPolicy()) {
                    if (policy.getName().equals(policyWithOperator.get("policy").asText())) {
                        TPolicyTemplate policyTemplate = AllocationUtils.toPolicyTemplate(policy);
                        policiesForNTs.get(topLevelNT).add(new PolicyWrapper(policyTemplate, propertyKey, operator));
                    }
                }
            }
        }
        return policiesForNTs;
    }

    private List<TNodeTemplate> getNTsWithPolicies(TopologyWrapper topology) {
        return topology.getTopLevelNTs().stream()
            .filter(nt -> !AllocationUtils.getPolicyTemplates(nt).isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * Remap fragments fulfilling policies to the corresponding node templates.
     */
    private Map<TNodeTemplate, List<TTopologyTemplate>> mapFragmentsByNTs(Map<TNodeTemplate, List<TTopologyTemplate>> fragments,
                                                                          List<TTopologyTemplate> fragmentsFulfillingPolicies) {
        Map<TNodeTemplate, List<TTopologyTemplate>> fragmentsWithNTs = new HashMap<>();

        for (TTopologyTemplate fragmentFulfillingPolicies : fragmentsFulfillingPolicies) {
            for (Map.Entry<TNodeTemplate, List<TTopologyTemplate>> entry : fragments.entrySet()) {
                if (entry.getValue().contains(fragmentFulfillingPolicies)) {

                    if (fragmentsWithNTs.containsKey(entry.getKey())) {
                        fragmentsWithNTs.get(entry.getKey()).add(fragmentFulfillingPolicies);
                    } else {
                        List<TTopologyTemplate> frags = new ArrayList<>();
                        frags.add(fragmentFulfillingPolicies);
                        fragmentsWithNTs.put(entry.getKey(), frags);
                    }
                }
            }
        }
        return fragmentsWithNTs;
    }

    @Override
    protected void split(TopologyWrapper topology) throws SplittingException {
        // workaround: split by ids -> avoid injecting fragments that don't match all policies
        List<TNodeTemplate> ntsWithPolicies = getNTsWithPolicies(topology);
        SplitByIds splitByIds = new SplitByIds(topology, ntsWithPolicies);
        splitByIds.split();
    }
}
