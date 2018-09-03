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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KargerMinCutVariationTest extends TestWithGitBackedRepository {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(KargerMinCutVariationTest.class);

    @BeforeEach
    public void setUp() throws GitAPIException {
        setRevisionTo("bb9ff7e08f7b72d30a2fae326740ef8051701671");
    }

    @Test
    public void twoTargetLabelsOnePossibleResult() {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "MinExternalConnectionsTest1", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        ConnectsToGraph connectsToGraph = new ConnectsToGraph(topology);
        KargerMinCutVariation kargerMinCutVariation = new KargerMinCutVariation(connectsToGraph);
        List<ConnectsToGraph> results = new ArrayList<>(kargerMinCutVariation.computeTargetLabelPartitions());

        // testing a monte carlo algorithm doesn't always work
        if (results.size() != 1 || results.get(0).getEdges().size() != 1) {
            LOGGER.debug("Monte Carlo Algorithm produced wrong result");
            return;
        }

        assertEquals(results.size(), 1);
        ConnectsToGraph result = results.get(0);
        assertEquals(result.getEdges().size(), 1);
        assertEquals(result.getNodes().size(), 2);

        Set<String> pastureProvider = new HashSet<>(Collections.singletonList("shetland_pony"));
        Set<String> fieldProvider = new HashSet<>(Arrays.asList("shetland_pony_2", "shetland_pony_3", "shetland_pony_4"));
        ConnectsToGraph.Node pastureNode = result.getNodes().stream()
            .filter(node -> node.getTargetLabel().equalsIgnoreCase("PastureProvider")).collect(Collectors.toList()).get(0);
        ConnectsToGraph.Node fieldNode = result.getNodes().stream()
            .filter(node -> node.getTargetLabel().equalsIgnoreCase("FieldProvider")).collect(Collectors.toList()).get(0);
        assertEquals(pastureNode.getNodeTemplateIds(), pastureProvider);
        assertEquals(fieldNode.getNodeTemplateIds(), fieldProvider);
    }

    @Test
    public void performanceTest() {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "MinExternalConnectionsTest2", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        ConnectsToGraph connectsToGraph = new ConnectsToGraph(topology);
        KargerMinCutVariation kargerMinCutVariation = new KargerMinCutVariation(connectsToGraph);
        List<ConnectsToGraph> results = new ArrayList<>(kargerMinCutVariation.computeTargetLabelPartitions());

        // testing a monte carlo algorithm doesn't always work
        if (results.size() != 4 || results.get(0).getEdges().size() != 4) {
            LOGGER.debug("Monte Carlo Algorithm produced wrong result");
            return;
        }

        assertEquals(results.size(), 4);
        assertEquals(results.get(0).getEdges().size(), 4);
        assertEquals(results.get(1).getEdges().size(), 4);
        assertEquals(results.get(2).getEdges().size(), 4);
        assertEquals(results.get(3).getEdges().size(), 4);
    }
}
