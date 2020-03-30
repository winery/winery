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
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.splitting.InjectRemoval;
import org.eclipse.winery.repository.splitting.Splitting;
import org.eclipse.winery.repository.splitting.SplittingException;
import org.eclipse.winery.repository.targetallocation.Criteria;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;
import org.eclipse.winery.repository.targetallocation.util.PermutationHelper;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common methods and variables for criteria.
 */
public abstract class CriteriaCommon extends Criteria {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Splitting splitting = new Splitting();
    protected InjectRemoval injectRemoval;
    protected int amountAllocated = 0;

    public CriteriaCommon(JsonNode params, int outputCap) {
        super(params, outputCap);
    }

    /**
     * Creates possible topologies with assigned target labels,
     * splits these topologies and
     * creates possible topologies with injected matching PaaS fragments.
     */
    @Override
    public List<TopologyWrapper> allocate(TopologyWrapper topology) throws Exception {
        List<TopologyWrapper> topologiesWithTargetLabels = generateTargetLabelTopologies(topology);
        for (TopologyWrapper topologyToSplit : topologiesWithTargetLabels) {
            split(topologyToSplit);
        }
        return generateTopologies(topologiesWithTargetLabels);
    }

    /**
     * Creates possible topologies with assigned target labels.
     */
    public abstract List<TopologyWrapper> generateTargetLabelTopologies(TopologyWrapper topology) throws Exception;

    /**
     * Creates possible topologies with injected matching PaaS fragments.
     */
    private List<TopologyWrapper> generateTopologies(List<TopologyWrapper> topologies) throws Exception {
        List<TopologyWrapper> allGenerated = new ArrayList<>();

        for (TopologyWrapper topology : topologies) {
            List<List<PermutationHelper>> possibilities = getPossibleMatches(topology);
            if (possibilities.isEmpty()) {
                logger.debug("No matching fragments found for this target label topology, trying next one...");
                continue;
            }
            List<List<PermutationHelper>> permutations = getPermutations(possibilities);
            List<TopologyWrapper> generated = new ArrayList<>();

            for (List<PermutationHelper> permutation : permutations) {
                TTopologyTemplate newTopologyTemplate = AllocationUtils.deepcopy(topology.getTopology());

                Map<String, TTopologyTemplate> injectParameter = toInjectParameter(topology, permutation);
                newTopologyTemplate = splitting.injectNodeTemplates(newTopologyTemplate, injectParameter, injectRemoval);
                TopologyWrapper newTopology = new TopologyWrapper(newTopologyTemplate);

                generated.add(newTopology);
            }
            allGenerated.addAll(generated);
            amountAllocated += generated.size();
        }
        return allGenerated;
    }

    /**
     * Get combinations of matching Node Templates which can be injected into the topology.
     */
    protected abstract List<List<PermutationHelper>> getPossibleMatches(TopologyWrapper topology) throws Exception;

    /**
     * Get permutation of possible Node Templates to be injected.
     */
    protected abstract List<List<PermutationHelper>> getPermutations(List<List<PermutationHelper>> possibilities);

    /**
     * Convert permutation helpers to Map in order to reuse
     * {@link Splitting#injectNodeTemplates(TTopologyTemplate, Map, InjectRemoval)}.
     */
    protected abstract Map<String, TTopologyTemplate> toInjectParameter(TopologyWrapper topology,
                                                                        List<PermutationHelper> permutation);

    protected void split(TopologyWrapper topology) throws SplittingException {
        topology.setTopology(splitting.split(topology.getTopology()));
    }
}
