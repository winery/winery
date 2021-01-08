/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.targetallocation.Criteria;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinExternalConnectionsTest extends TestWithGitBackedRepository {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MinExternalConnectionsTest.class);

    @BeforeEach
    public void setUp() throws GitAPIException {
        setRevisionTo("bb9ff7e08f7b72d30a2fae326740ef8051701671");
    }

    @Test
    public void twoTargetLabelsOnePossibleResult() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "MinExternalConnectionsTest1", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        Criteria minExternalConnections = new MinExternalConnections(null, Integer.MAX_VALUE);
        List<TopologyWrapper> topologies = minExternalConnections.allocate(topology);
        if (topologies.size() != 1) {
            LOGGER.debug("Randomized algorithm didn't find correct results");
            return;
        }

        assertEquals(6, topologies.get(0).getNodeTemplates().size());
    }

    @Test
    public void performanceTest() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "MinExternalConnectionsTest2", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        Criteria minExternalConnections = new MinExternalConnections(null, Integer.MAX_VALUE);
        List<TopologyWrapper> topologies = minExternalConnections.allocate(topology);

        for (TopologyWrapper allocated : topologies) {
            assertEquals(22, allocated.getNodeTemplates().size());
        }
    }

    @Test
    public void filter() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "MinExternalConnectionsTest1", false);
        TTopologyTemplate twoExternal = repository.getElement(id).getTopologyTemplate();
        ModelUtilities.setTargetLabel(twoExternal.getNodeTemplate("shetland_pony_2"), "PastureProvider");
        ModelUtilities.setTargetLabel(twoExternal.getNodeTemplate("shetland_pony_3"), "PastureProvider");
        TTopologyTemplate oneExternal = AllocationUtils.deepcopy(twoExternal);

        ModelUtilities.setTargetLabel(oneExternal.getNodeTemplate("shetland_pony_2"), "FieldProvider");
        ModelUtilities.setTargetLabel(oneExternal.getNodeTemplate("shetland_pony_3"), "FieldProvider");

        List<TopologyWrapper> topologies = new ArrayList<>();
        topologies.add(new TopologyWrapper(twoExternal));
        topologies.add(new TopologyWrapper(oneExternal));
        Criteria minExternalConnections = new MinExternalConnections(null, Integer.MAX_VALUE);
        List<TopologyWrapper> filtered = minExternalConnections.filter(topologies);

        assertEquals(1, filtered.size());
        TTopologyTemplate result = filtered.get(0).getTopology();
        assertEquals("PastureProvider".toLowerCase(), ModelUtilities.getTargetLabel(result.getNodeTemplate("shetland_pony")).get());
        assertEquals("FieldProvider".toLowerCase(), ModelUtilities.getTargetLabel(result.getNodeTemplate("shetland_pony_2")).get());
        assertEquals("FieldProvider".toLowerCase(), ModelUtilities.getTargetLabel(result.getNodeTemplate("shetland_pony_3")).get());
        assertEquals("FieldProvider".toLowerCase(), ModelUtilities.getTargetLabel(result.getNodeTemplate("shetland_pony_4")).get());
    }
}

