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

package org.eclipse.winery.repository.targetallocation.criteria.minexternalconnections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.exceptions.AllocationException;
import org.eclipse.winery.repository.targetallocation.criteria.CriteriaCached;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;
import org.eclipse.winery.repository.targetallocation.util.PermutationHelper;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Distributes present target labels so that the amount of external connectsTo Relationship Templates is minimized.
 * A Relationship Template is external if the target label of the source Node Template
 * is different from the target label of the target Node Template.
 * Also creates possible topologies out of these distributions with injected matching PaaS fragments.
 */
public class MinExternalConnections extends CriteriaCached {

    public MinExternalConnections(JsonNode params, int outputCap) {
        super(params, outputCap);
    }

    /**
     * Filters topologies based on the amount of their external connectsTo Relationship Templates.
     *
     * @return all topologies with minimum external Relationship Template amount
     */
    @Override
    public List<TopologyWrapper> filter(List<TopologyWrapper> topologies) {
        List<TopologyWrapper> minExternalRTs = new ArrayList<>();
        int minExternalRTsCount = Integer.MAX_VALUE;

        for (TopologyWrapper topology : topologies) {
            int externalRTsCount = 0;

            for (TRelationshipTemplate relationshipTemplate : topology.getConnectsTos(topology.getRelationshipTemplates())) {
                TNodeTemplate source = topology.getSourceNT(relationshipTemplate);
                TNodeTemplate target = topology.getTargetNT(relationshipTemplate);
                // only consider connectsTos between top level nts
                if (!topology.isTopLevelNT(source) || !topology.isTopLevelNT(target)) {
                    continue;
                }

                // external edge?
                String sourceTargetLabel = ModelUtilities.getTargetLabel(source).get();
                String targetTargetLabel = ModelUtilities.getTargetLabel(target).get();
                if (!sourceTargetLabel.equals(targetTargetLabel)) {
                    externalRTsCount++;
                }
            }
            if (externalRTsCount < minExternalRTsCount) {
                minExternalRTs.clear();
                minExternalRTs.add(topology);
                minExternalRTsCount = externalRTsCount;
            } else if (externalRTsCount == minExternalRTsCount) {
                minExternalRTs.add(topology);
            }
        }
        return minExternalRTs;
    }

    @Override
    public List<TopologyWrapper> generateTargetLabelTopologies(TopologyWrapper topology) {
        ConnectsToGraph connectsToGraph = new ConnectsToGraph(topology);
        KargerMinCutVariation minCut = new KargerMinCutVariation(connectsToGraph);
        Set<ConnectsToGraph> minCutGraphs = minCut.computeTargetLabelPartitions();
        List<TopologyWrapper> generatedTopologies = new ArrayList<>();

        for (ConnectsToGraph minCutGraph : minCutGraphs) {
            if (generatedTopologies.size() >= outputCap) {
                break;
            }
            TopologyWrapper clone = new TopologyWrapper(AllocationUtils.deepcopy(topology.getTopology()));
            Map<String, TNodeTemplate> topLevelNtsByIds = clone.getTopLevelNtsByIds();

            // map merged ConnectsToGraph to topology
            for (ConnectsToGraph.Node node : minCutGraph.getNodes()) {
                for (String nodeTemplateId : node.getNodeTemplateIds()) {
                    ModelUtilities.setTargetLabel(topLevelNtsByIds.get(nodeTemplateId), node.getTargetLabel());
                }
            }
            generatedTopologies.add(clone);
        }
        return generatedTopologies;
    }

    @Override
    protected List<List<PermutationHelper>> getPossibleMatches(TopologyWrapper topology) {
        List<List<PermutationHelper>> possibilities = new ArrayList<>();

        try {
            for (TNodeTemplate topLevelNT : topology.getTopLevelNTs()) {
                Map<TNodeTemplate, List<TTopologyTemplate>> fragments =
                    fragmentsCache.getAllMatchingFragments(topology, topLevelNT);

                List<PermutationHelper> possibility = new ArrayList<>();
                for (Map.Entry<TNodeTemplate, List<TTopologyTemplate>> entry : fragments.entrySet()) {
                    possibility.add(new PermutationHelper(entry.getKey(), entry.getValue()));
                }
                possibilities.add(possibility);
            }
        } catch (AllocationException e) {
            logger.debug("Exception calculating matching fragments: " + e.getMessage());
            return new ArrayList<>();
        }
        return possibilities;
    }
}
