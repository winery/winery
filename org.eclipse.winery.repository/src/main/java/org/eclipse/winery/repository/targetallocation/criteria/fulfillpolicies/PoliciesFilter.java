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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.splitting.Splitting;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

/**
 * Filter generated topologies based on the policy templates of the lowest level injected PaaS NTs.
 *
 * @see FulfillPolicies
 */
public class PoliciesFilter {

    private List<TopologyWrapper> topologies;
    private Map<TNodeTemplate, List<PolicyWrapper>> policiesForNTs;

    private Map<TopologyWrapper, Map<TNodeTemplate, Set<TNodeTemplate>>> transitiveClosures = new HashMap<>();
    private Map<TopologyWrapper, Map<String, TNodeTemplate>> topLevelNTsByIds = new HashMap<>();
    private Map<TNodeTemplate, TopologyWrapper> topologiesByFragments = new HashMap<>();

    public PoliciesFilter(List<TopologyWrapper> topologies, Map<TNodeTemplate, List<PolicyWrapper>> policiesForNTs) {
        this.topologies = topologies;
        this.policiesForNTs = policiesForNTs;

        for (TopologyWrapper topology : topologies) {
            Splitting splitting = new Splitting();
            transitiveClosures.put(topology, splitting.computeTransitiveClosure(topology.getTopology()));
            topLevelNTsByIds.put(topology, topology.getTopLevelNtsByIds());
        }
    }

    public List<TopologyWrapper> filter() {
        List<List<TTopologyTemplate>> fragmentsFulfilling = new ArrayList<>();

        // sort topologies by the policies they fulfill
        for (Map.Entry<TNodeTemplate, List<PolicyWrapper>> entry : policiesForNTs.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }

            List<TNodeTemplate> fragments = new ArrayList<>();
            for (TopologyWrapper topology : topologies) {
                fragments.add(getPolicyFragment(topology, entry.getKey().getId()));
            }
            PolicyComparison comparison = new PolicyComparison(entry.getValue(), fragments);
            fragmentsFulfilling.add(comparison.getFragmentsFulfillingPolicies());
        }

        // get topologies that fulfill all policies
        List<List<TopologyWrapper>> topologiesFulfilling = toTopologies(fragmentsFulfilling);
        Iterator<List<TopologyWrapper>> iterator = topologiesFulfilling.iterator();
        List<TopologyWrapper> intersection = iterator.next();
        while (iterator.hasNext()) {
            intersection.retainAll(iterator.next());
        }
        return intersection;
    }

    private TNodeTemplate getPolicyFragment(TopologyWrapper topology, String nodeTemplateId) {
        TNodeTemplate topLevelNT = topLevelNTsByIds.get(topology).get(nodeTemplateId);
        // lowest inserted nt is the matched one from cloud provider with policy template
        for (TNodeTemplate nodeTemplate : transitiveClosures.get(topology).get(topLevelNT)) {
            if (topology.isBottomLevelNT(nodeTemplate)) {
                topologiesByFragments.put(nodeTemplate, topology);
                return nodeTemplate;
            }
        }
        return null;
    }

    private List<List<TopologyWrapper>> toTopologies(List<List<TTopologyTemplate>> fragmentsByPolicies) {
        List<List<TopologyWrapper>> topologiesByPolicies = new ArrayList<>();

        for (List<TTopologyTemplate> fragments : fragmentsByPolicies) {
            List<TopologyWrapper> toTopologies = new ArrayList<>();
            for (TTopologyTemplate topologyTemplate : fragments) {
                // nts uniquely identifiable because of cloneNotEquals
                toTopologies.add(topologiesByFragments.get(topologyTemplate.getNodeTemplates().get(0)));
            }
            topologiesByPolicies.add(toTopologies);
        }
        return topologiesByPolicies;
    }
}
