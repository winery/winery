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

package org.eclipse.winery.repository.targetallocation.criteria.fulfilpolicies;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.exceptions.AllocationException;
import org.eclipse.winery.repository.targetallocation.Criteria;
import org.eclipse.winery.repository.targetallocation.criteria.fulfillpolicies.FulfillPolicies;
import org.eclipse.winery.repository.targetallocation.criteria.minexternalconnections.MinExternalConnections;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FulfillPoliciesTest extends TestWithGitBackedRepository {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FulfillPoliciesTest.class);

    private TopologyWrapper topology;

    @BeforeEach
    public void setUp() throws GitAPIException {
        setRevisionTo("bb9ff7e08f7b72d30a2fae326740ef8051701671");
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationFulfillPoliciesTest1", false);
        topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
    }

    @Test
    public void testSimpleOperators() throws Exception {
        JsonNode params = JacksonProvider.mapper.readTree("{\"policySelection\":[" +
            "{\"policy\":\"floorwarmth10\",\"property\":\"warmth\",\"operator\":\">\"}," +
            "{\"policy\":\"floorwarmth30\",\"property\":\"warmth\",\"operator\":\"<\"}]}");
        Criteria fulfillPolicies = new FulfillPolicies(params, Integer.MAX_VALUE);
        List<TopologyWrapper> allocatedTopologies = fulfillPolicies.allocate(topology);
        assertEquals(4, allocatedTopologies.size());

        TopologyWrapper allocated1 = allocatedTopologies.get(0);
        TopologyWrapper allocated2 = allocatedTopologies.get(1);
        TopologyWrapper allocated3 = allocatedTopologies.get(2);
        TopologyWrapper allocated4 = allocatedTopologies.get(3);

        assertEquals(8, allocated1.getNodeTemplates().size());
        assertEquals(8, allocated2.getNodeTemplates().size());
        assertEquals(8, allocated3.getNodeTemplates().size());
        assertEquals(8, allocated4.getNodeTemplates().size());

        List<String> targetLabels1 = getTargetLabels(allocated1);
        List<String> targetLabels2 = getTargetLabels(allocated2);
        List<String> targetLabels3 = getTargetLabels(allocated3);
        List<String> targetLabels4 = getTargetLabels(allocated4);

        assertEquals(3, targetLabels1.stream().filter("PastureProvider"::equalsIgnoreCase).count());
        assertEquals(1, targetLabels1.stream().filter("FieldProvider"::equalsIgnoreCase).count());

        assertEquals(2, targetLabels2.stream().filter("PastureProvider"::equalsIgnoreCase).count());
        assertEquals(2, targetLabels2.stream().filter("FieldProvider"::equalsIgnoreCase).count());

        assertEquals(2, targetLabels3.stream().filter("PastureProvider"::equalsIgnoreCase).count());
        assertEquals(2, targetLabels3.stream().filter("FieldProvider"::equalsIgnoreCase).count());

        assertEquals(1, targetLabels4.stream().filter("PastureProvider"::equalsIgnoreCase).count());
        assertEquals(3, targetLabels4.stream().filter("FieldProvider"::equalsIgnoreCase).count());
    }

    private List<String> getTargetLabels(TopologyWrapper topology) {
        return topology.getNodeTemplates().stream()
            .filter(nt -> ModelUtilities.getTargetLabel(nt).isPresent() &&
                topology.isTopLevelNT(nt))
            .map(nt -> ModelUtilities.getTargetLabel(nt).get())
            .collect(Collectors.toList());
    }

    @Test
    public void testMinMax() throws Exception {
        JsonNode params = JacksonProvider.mapper.readTree("{\"policySelection\":[" +
            "{\"policy\":\"floorwarmth10\",\"property\":\"warmth\",\"operator\":\"min\"}," +
            "{\"policy\":\"floorwarmth30\",\"property\":\"warmth\",\"operator\":\"max\"}]}");
        Criteria fulfillPolicies = new FulfillPolicies(params, Integer.MAX_VALUE);
        List<TTopologyTemplate> allocatedTopologies = fulfillPolicies.allocate(topology).stream()
            .map(TopologyWrapper::getTopology)
            .collect(Collectors.toList());
        assertEquals(1, allocatedTopologies.size());

        TTopologyTemplate allocated = allocatedTopologies.get(0);
        assertEquals(8, allocated.getNodeTemplates().size());
        // set by criteria
        assertEquals("PastureProvider".toLowerCase(),
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_3")).get());
        assertEquals("FieldProvider".toLowerCase(),
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_4")).get());
        // set by user
        assertEquals("PastureProvider".toLowerCase(),
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony")).get());
        assertEquals("FieldProvider".toLowerCase(),
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_2")).get());
    }

    @Test
    public void testNotPossible() throws Exception {
        JsonNode params = JacksonProvider.mapper.readTree("{\"policySelection\":[" +
            "{\"policy\":\"floorwarmth10\",\"property\":\"warmth\",\"operator\":\"=\"}," +
            "{\"policy\":\"floorwarmth30\",\"property\":\"warmth\",\"operator\":\"=\"}]}");
        Criteria fulfillPolicies = new FulfillPolicies(params, Integer.MAX_VALUE);
        Executable executable = () -> fulfillPolicies.allocate(topology);
        assertThrows(AllocationException.class, executable, "");
    }

    @Test
    public void testApprox() throws Exception {
        JsonNode params = JacksonProvider.mapper.readTree("{\"policySelection\":[" +
            "{\"policy\":\"floorwarmth10\",\"property\":\"warmth\",\"operator\":\"approx\"}," +
            "{\"policy\":\"floorwarmth30\",\"property\":\"warmth\",\"operator\":\"approx\"}]}");
        Criteria fulfillPolicies = new FulfillPolicies(params, Integer.MAX_VALUE);
        List<TTopologyTemplate> allocatedTopologies = fulfillPolicies.allocate(topology).stream()
            .map(TopologyWrapper::getTopology)
            .collect(Collectors.toList());
        assertEquals(1, allocatedTopologies.size());

        TTopologyTemplate allocated = allocatedTopologies.get(0);
        assertEquals(8, allocated.getNodeTemplates().size());
        // set by criteria
        assertEquals("PastureProvider".toLowerCase(),
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_3")).get());
        assertEquals("FieldProvider".toLowerCase(),
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_4")).get());
        // set by user
        assertEquals("PastureProvider".toLowerCase(),
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony")).get());
        assertEquals("FieldProvider".toLowerCase(),
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_2")).get());
    }

    @Test
    public void testFilter() throws Exception {
        Criteria minConnections = new MinExternalConnections(null, Integer.MAX_VALUE);
        List<TopologyWrapper> topologies = minConnections.allocate(topology);
        if (topologies.size() != 4) {
            LOGGER.debug("Min cut algorithm didn't find all results");
            return;
        }
        boolean rightResultPresent = false;
        for (TopologyWrapper topology : topologies) {
            TTopologyTemplate topologyTemplate = topology.getTopology();
            TNodeTemplate pony3 = topologyTemplate.getNodeTemplate("shetland_pony_3");
            TNodeTemplate pony4 = topologyTemplate.getNodeTemplate("shetland_pony_4");
            if (ModelUtilities.getTargetLabel(pony3).get().equals("PastureProvider") &&
                ModelUtilities.getTargetLabel(pony4).get().equals("FieldProvider")) {
                LOGGER.debug("Right result present");
                rightResultPresent = true;
                break;
            }
        }
        if (!rightResultPresent) {
            LOGGER.debug("Min cut algorithm didn't find needed result");
            return;
        }

        JsonNode params = JacksonProvider.mapper.readTree("{\"policySelection\":[" +
            "{\"policy\":\"floorwarmth10\",\"property\":\"warmth\",\"operator\":\"min\"}," +
            "{\"policy\":\"floorwarmth30\",\"property\":\"warmth\",\"operator\":\"max\"}]}");
        Criteria fulfillPolicies = new FulfillPolicies(params, Integer.MAX_VALUE);
        topologies = fulfillPolicies.filter(topologies);
        assertEquals(1, topologies.size());

        TTopologyTemplate allocated = topologies.get(0).getTopology();
        assertEquals(6, allocated.getNodeTemplates().size());
        // set by criteria
        assertEquals("PastureProvider",
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_3")).get());
        assertEquals("FieldProvider",
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_4")).get());
        // set by user
        assertEquals("PastureProvider",
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony")).get());
        assertEquals("FieldProvider",
            ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_2")).get());
    }
}

