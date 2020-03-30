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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.splitting.Splitting;
import org.eclipse.winery.repository.splitting.SplittingException;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

/**
 * Splits the topology by the IDs of the specified top level NTs and
 * resets the original target labels afterwards.
 */
public class SplitByIds {

    private TopologyWrapper topology;
    private List<TNodeTemplate> topLevelNTsToSplit;

    private Splitting splitting;
    private Map<String, String> originalTargetLabels = new HashMap<>();

    public SplitByIds(TopologyWrapper topology, List<TNodeTemplate> topLevelNTsToSplit) {
        this.topology = topology;
        this.topLevelNTsToSplit = topLevelNTsToSplit;
    }

    public void split() throws SplittingException {
        prepareSplit();
        splitting.split(topology.getTopology());
        resetTargetLabels();
    }

    private void prepareSplit() {
        splitting = new Splitting();
        Map<TNodeTemplate, Set<TNodeTemplate>> transitiveClosure = splitting.computeTransitiveClosure(topology.getTopology());

        for (TNodeTemplate nodeTemplate : topology.getTopLevelNTs()) {
            String targetLabel = ModelUtilities.getTargetLabel(nodeTemplate).get();
            originalTargetLabels.put(nodeTemplate.getId(), targetLabel);
            if (topLevelNTsToSplit.contains(nodeTemplate)) {
                ModelUtilities.setTargetLabel(nodeTemplate, nodeTemplate.getId());
            }

            for (TNodeTemplate successor : transitiveClosure.get(nodeTemplate)) {
                ModelUtilities.setTargetLabel(successor, "undefined");
            }
        }
    }

    private void resetTargetLabels() {
        splitting = new Splitting();
        Map<TNodeTemplate, Set<TNodeTemplate>> transitiveClosure = splitting.computeTransitiveClosure(topology.getTopology());

        for (TNodeTemplate nodeTemplate : topology.getTopLevelNTs()) {
            String originalTargetLabel = originalTargetLabels.get(nodeTemplate.getId());
            ModelUtilities.setTargetLabel(nodeTemplate, originalTargetLabel);

            for (TNodeTemplate successor : transitiveClosure.get(nodeTemplate)) {
                ModelUtilities.setTargetLabel(successor, originalTargetLabel);
            }
        }
    }
}
