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
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.targetallocation.Criteria;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinHostsTest extends TestWithGitBackedRepository {

    @BeforeEach
    public void setUp() throws GitAPIException {
        setRevisionTo("bb9ff7e08f7b72d30a2fae326740ef8051701671");
    }

    @Test
    public void testOneTopLevelTwoPossibilitiesNoTargetLabel() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationMinHostsTest1", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        Criteria minHosts = new MinHosts(null, Integer.MAX_VALUE);
        List<String> ntIdsWithoutTargetLabel = topology.getTopLevelNTs().stream()
            .filter(nt -> !ModelUtilities.getTargetLabel(nt).isPresent()).map(HasId::getId).collect(Collectors.toList());

        List<TopologyWrapper> topologies = minHosts.allocate(topology);
        assertEquals(2, topologies.size());
        TTopologyTemplate topology1 = topologies.get(0).getTopology();
        TTopologyTemplate topology2 = topologies.get(1).getTopology();
        assertEquals(9, topology1.getNodeTemplateOrRelationshipTemplate().size());
        assertEquals(9, topology2.getNodeTemplateOrRelationshipTemplate().size());

        List<String> types1 = new ArrayList<>();
        for (TNodeTemplate nodeTemplate : topology1.getNodeTemplates()) {
            types1.add(nodeTemplate.getType().getLocalPart());
        }
        assertEquals(6, types1.size());
        assertTrue(types1.contains("shetland_pony"));
        assertTrue(types1.contains("pasture"));
        for (TNodeTemplate nodeTemplate : topologies.get(0).getTopLevelNTs()) {
            if (ntIdsWithoutTargetLabel.contains(nodeTemplate.getId())) {
                assertEquals("pasture", topologies.get(0).getHostedOnSuccessor(nodeTemplate).getType().getLocalPart());
            }
        }

        List<String> types2 = new ArrayList<>();
        for (TNodeTemplate nodeTemplate : topology2.getNodeTemplates()) {
            types2.add(nodeTemplate.getType().getLocalPart());
        }
        assertEquals(6, types2.size());
        assertTrue(types2.contains("shetland_pony"));
        assertTrue(types2.contains("field_-w1-wip1"));
        for (TNodeTemplate nodeTemplate : topologies.get(1).getTopLevelNTs()) {
            if (ntIdsWithoutTargetLabel.contains(nodeTemplate.getId())) {
                assertEquals("field_-w1-wip1", topologies.get(1).getHostedOnSuccessor(nodeTemplate).getType().getLocalPart());
            }
        }
    }

    @Test
    public void testOneTargetLabelPresent() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationMinHostsTest2", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        Criteria minHosts = new MinHosts(null, Integer.MAX_VALUE);

        List<TopologyWrapper> topologies = minHosts.allocate(topology);
        assertEquals(1, topologies.size());
        TTopologyTemplate topology1 = topologies.get(0).getTopology();
        assertEquals(7, topology1.getNodeTemplateOrRelationshipTemplate().size());

        List<String> types1 = new ArrayList<>();
        for (TNodeTemplate nodeTemplate : topology1.getNodeTemplates()) {
            types1.add(nodeTemplate.getType().getLocalPart());
        }
        assertEquals(4, types1.size());
        assertEquals(3, types1.stream().filter(type -> type.equalsIgnoreCase("shetland_pony")).count());
        assertEquals(1, types1.stream().filter(type -> type.equalsIgnoreCase("pasture")).count());
        for (TNodeTemplate nodeTemplate : topologies.get(0).getTopLevelNTs()) {
            assertEquals("pasture", topologies.get(0).getHostedOnSuccessor(nodeTemplate).getType().getLocalPart());
        }
    }

    @Test
    public void testTwoTargetLabelsPresent() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationMinHostsTest3", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        Criteria minHosts = new MinHosts(null, Integer.MAX_VALUE);

        List<TopologyWrapper> topologies = minHosts.allocate(topology);
        assertEquals(2, topologies.size());
        TTopologyTemplate topology1 = topologies.get(0).getTopology();
        TTopologyTemplate topology2 = topologies.get(1).getTopology();
        assertEquals(8, topology1.getNodeTemplateOrRelationshipTemplate().size());
        assertEquals(8, topology2.getNodeTemplateOrRelationshipTemplate().size());

        List<String> types1 = new ArrayList<>();
        for (TNodeTemplate nodeTemplate : topology1.getNodeTemplates()) {
            types1.add(nodeTemplate.getType().getLocalPart());
        }
        assertEquals(5, types1.size());
        assertEquals(3, types1.stream().filter(type -> type.equalsIgnoreCase("shetland_pony")).count());
        assertEquals(1, types1.stream().filter(type -> type.equalsIgnoreCase("pasture")).count());
        assertEquals(1, types1.stream().filter(type -> type.equalsIgnoreCase("field_-w1-wip1")).count());
        for (TNodeTemplate nodeTemplate : topologies.get(0).getTopLevelHosts()) {
            List<TNodeTemplate> predecessors = topologies.get(0).getHostedOnPredecessors(nodeTemplate);
            if (nodeTemplate.getType().getLocalPart().equalsIgnoreCase("pasture")) {
                assertEquals(predecessors.size(), 2);
            } else if (nodeTemplate.getType().getLocalPart().equalsIgnoreCase("field")) {
                assertEquals(predecessors.size(), 1);
            }
        }

        List<String> types2 = new ArrayList<>();
        for (TNodeTemplate nodeTemplate : topology2.getNodeTemplates()) {
            types2.add(nodeTemplate.getType().getLocalPart());
        }
        assertEquals(5, types2.size());
        assertEquals(3, types2.stream().filter(type -> type.equalsIgnoreCase("shetland_pony")).count());
        assertEquals(1, types2.stream().filter(type -> type.equalsIgnoreCase("pasture")).count());
        assertEquals(1, types2.stream().filter(type -> type.equalsIgnoreCase("field_-w1-wip1")).count());
        for (TNodeTemplate nodeTemplate : topologies.get(1).getTopLevelHosts()) {
            List<TNodeTemplate> predecessors = topologies.get(1).getHostedOnPredecessors(nodeTemplate);
            if (nodeTemplate.getType().getLocalPart().equalsIgnoreCase("pasture")) {
                assertEquals(predecessors.size(), 1);
            } else if (nodeTemplate.getType().getLocalPart().equalsIgnoreCase("field")) {
                assertEquals(predecessors.size(), 2);
            }
        }
    }

    @Test
    public void testNoTargetLabelsPresent() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationMinHostsTest4", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        Criteria minHosts = new MinHosts(null, Integer.MAX_VALUE);
        List<String> ntIdsWithoutTargetLabel = topology.getTopLevelNTs().stream()
            .filter(nt -> !ModelUtilities.getTargetLabel(nt).isPresent()).map(HasId::getId).collect(Collectors.toList());

        List<TopologyWrapper> topologies = minHosts.allocate(topology);
        assertEquals(2, topologies.size());
        TTopologyTemplate topology1 = topologies.get(0).getTopology();
        TTopologyTemplate topology2 = topologies.get(1).getTopology();
        assertEquals(17, topology1.getNodeTemplateOrRelationshipTemplate().size());
        assertEquals(17, topology2.getNodeTemplateOrRelationshipTemplate().size());

        List<String> types1 = new ArrayList<>();
        for (TNodeTemplate nodeTemplate : topology1.getNodeTemplates()) {
            types1.add(nodeTemplate.getType().getLocalPart());
        }
        assertEquals(10, types1.size());
        assertEquals(7, types1.stream().filter(type -> type.equalsIgnoreCase("shetland_pony")).count());
        assertEquals(2, types1.stream().filter(type -> type.equalsIgnoreCase("pasture")).count());
        for (TNodeTemplate nodeTemplate : topologies.get(0).getTopLevelNTs()) {
            if (ntIdsWithoutTargetLabel.contains(nodeTemplate.getId())) {
                assertEquals("pasture", topologies.get(0).getHostedOnSuccessor(nodeTemplate).getType().getLocalPart());
            }
        }

        List<String> types2 = new ArrayList<>();
        for (TNodeTemplate nodeTemplate : topology2.getNodeTemplates()) {
            types2.add(nodeTemplate.getType().getLocalPart());
        }
        assertEquals(10, types2.size());
        assertEquals(7, types2.stream().filter(type -> type.equalsIgnoreCase("shetland_pony")).count());
        assertEquals(2, types2.stream().filter(type -> type.equalsIgnoreCase("field_-w1-wip1")).count());
        for (TNodeTemplate nodeTemplate : topologies.get(1).getTopLevelNTs()) {
            if (ntIdsWithoutTargetLabel.contains(nodeTemplate.getId())) {
                assertEquals("field_-w1-wip1", topologies.get(1).getHostedOnSuccessor(nodeTemplate).getType().getLocalPart());
            }
        }
    }

    @Test
    public void testFilter() throws Exception {
        List<TopologyWrapper> topologies = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
                "TargetAllocationMinHostsTest" + i, false);
            topologies.add(new TopologyWrapper(repository.getElement(id).getTopologyTemplate()));
        }
        assertEquals(4, topologies.size());

        Criteria minHosts = new MinHosts(null, Integer.MAX_VALUE);
        List<TopologyWrapper> filtered = minHosts.filter(topologies);
        assertEquals(2, filtered.size());
        assertEquals(5, filtered.get(0).getNodeTemplates().size());
    }

    @Test
    public void test5Original() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationMinHostsTest5_-w1-wip1", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        Criteria minHosts = new MinHosts(null, Integer.MAX_VALUE);
        List<TopologyWrapper> topologies = minHosts.allocate(topology);

        assertEquals(1, topologies.size());
        TTopologyTemplate allocated = topologies.get(0).getTopology();
        assertEquals(6, allocated.getNodeTemplates().size());
        assertEquals(5, allocated.getRelationshipTemplates().size());

        assertEquals("LargeStallProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony")).get());
        assertEquals("LargeStallProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_2")).get());
        assertEquals("LargeStallProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_3")).get());
        assertEquals(1, allocated.getNodeTemplates().stream().map(ModelUtilities::getTargetLabel).collect(Collectors.toSet()).size());
    }

    @Test
    public void test5Pony3FieldProvider() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationMinHostsTest5_-w1-wip1", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        ModelUtilities.setTargetLabel(topology.getTopology().getNodeTemplate("shetland_pony_3"), "FieldProvider");
        Criteria minHosts = new MinHosts(null, Integer.MAX_VALUE);
        List<TopologyWrapper> topologies = minHosts.allocate(topology);

        assertEquals(1, topologies.size());
        TTopologyTemplate allocated = topologies.get(0).getTopology();
        assertEquals(6, allocated.getNodeTemplates().size());
        assertEquals(4, allocated.getRelationshipTemplates().size());

        assertEquals("LargeStallProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony")).get());
        assertEquals("LargeStallProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_2")).get());
        assertEquals("FieldProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated.getNodeTemplate("shetland_pony_3")).get());
        assertEquals(2, allocated.getNodeTemplates().stream().map(ModelUtilities::getTargetLabel).collect(Collectors.toSet()).size());
    }

    @Test
    public void test5PonyPastureProviderPony2FieldProvider() throws Exception {
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationMinHostsTest5_-w1-wip1", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());
        ModelUtilities.setTargetLabel(topology.getTopology().getNodeTemplate("shetland_pony"), "PastureProvider");
        ModelUtilities.setTargetLabel(topology.getTopology().getNodeTemplate("shetland_pony_2"), "FieldProvider");
        Criteria minHosts = new MinHosts(null, Integer.MAX_VALUE);
        List<TopologyWrapper> topologies = minHosts.allocate(topology);

        assertEquals(2, topologies.size());
        TTopologyTemplate allocated1 = topologies.get(0).getTopology();
        assertEquals(6, allocated1.getNodeTemplates().size());
        assertEquals(3, allocated1.getRelationshipTemplates().size());
        TTopologyTemplate allocated2 = topologies.get(1).getTopology();
        assertEquals(6, allocated2.getNodeTemplates().size());
        assertEquals(3, allocated2.getRelationshipTemplates().size());

        assertEquals("PastureProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated1.getNodeTemplate("shetland_pony")).get());
        assertEquals("FieldProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated1.getNodeTemplate("shetland_pony_2")).get());
        assertEquals(2, allocated1.getNodeTemplates().stream().map(ModelUtilities::getTargetLabel).collect(Collectors.toSet()).size());
        assertEquals("PastureProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated2.getNodeTemplate("shetland_pony")).get());
        assertEquals("FieldProvider".toLowerCase(), ModelUtilities.getTargetLabel(allocated2.getNodeTemplate("shetland_pony_2")).get());
        assertEquals(2, allocated2.getNodeTemplates().stream().map(ModelUtilities::getTargetLabel).collect(Collectors.toSet()).size());

        // one topology has shetland_pony_3 on pastureprovider, one on fieldprovider
        TNodeTemplate pony3Allocated1 = allocated1.getNodeTemplate("shetland_pony_3");
        TNodeTemplate pony3Allocated2 = allocated2.getNodeTemplate("shetland_pony_3");
        boolean pony3Provider = (ModelUtilities.getTargetLabel(pony3Allocated1).get().equalsIgnoreCase("PastureProvider") &&
            ModelUtilities.getTargetLabel(pony3Allocated2).get().equalsIgnoreCase("FieldProvider")) ||
            (ModelUtilities.getTargetLabel(pony3Allocated1).get().equalsIgnoreCase("FieldProvider") &&
                ModelUtilities.getTargetLabel(pony3Allocated2).get().equalsIgnoreCase("PastureProvider"));
        assertTrue(pony3Provider);
    }
}
