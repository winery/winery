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

package org.eclipse.winery.repository.targetallocation.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopologyMappingsWrapperTest extends TestWithGitBackedRepository {

    private TopologyWrapper policiesTopology;
    private TopologyWrapper minHostsTopology;

    @BeforeEach
    public void setUp() throws Exception {
        setRevisionTo("bb9ff7e08f7b72d30a2fae326740ef8051701671");
        ServiceTemplateId policiesIO = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationFulfillPoliciesTest1", false);
        policiesTopology = new TopologyWrapper(repository.getElement(policiesIO).getTopologyTemplate());

        ServiceTemplateId minHostsId = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationMinHostsTest4", false);
        minHostsTopology = new TopologyWrapper(repository.getElement(minHostsId).getTopologyTemplate());
    }

    @Test
    public void getHostedOnPredecessors() {
        List<TNodeTemplate> hostedOnPredecessors = policiesTopology.getHostedOnPredecessors(policiesTopology.getTopology().getNodeTemplate("straw"));
        assertEquals(4, hostedOnPredecessors.size());
        Set<String> ids = hostedOnPredecessors.stream().map(HasId::getId).collect(Collectors.toSet());
        assertTrue(ids.contains("shetland_pony"));
        assertTrue(ids.contains("shetland_pony_2"));
        assertTrue(ids.contains("shetland_pony_3"));
        assertTrue(ids.contains("shetland_pony_4"));
    }

    @Test
    public void getHostedOnSuccessor() {
        TNodeTemplate hostedOnSuccessor = policiesTopology.getHostedOnSuccessor(policiesTopology.getTopology().getNodeTemplate("straw"));
        assertNotNull(hostedOnSuccessor);
        assertEquals("stall", hostedOnSuccessor.getId());
    }

    @Test
    public void removeNT() {
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();
        TNodeTemplate one = new TNodeTemplate("id1");
        TNodeTemplate two = new TNodeTemplate("id2");
        TNodeTemplate three = new TNodeTemplate("id3");
        topologyTemplate.addNodeTemplate(one);
        topologyTemplate.addNodeTemplate(two);
        topologyTemplate.addNodeTemplate(three);
        assertEquals(3, topologyTemplate.getNodeTemplates().size());
        TopologyWrapper topology = new TopologyWrapper(topologyTemplate);
        topology.removeNT(one);
        assertEquals(2, topology.getNodeTemplates().size());
    }

    @Test
    public void removeAllNTs() {
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();
        TNodeTemplate one = new TNodeTemplate("id1");
        TNodeTemplate two = new TNodeTemplate("id2");
        TNodeTemplate three = new TNodeTemplate("id3");
        topologyTemplate.addNodeTemplate(one);
        topologyTemplate.addNodeTemplate(two);
        topologyTemplate.addNodeTemplate(three);
        assertEquals(3, topologyTemplate.getNodeTemplates().size());
        TopologyWrapper topology = new TopologyWrapper(topologyTemplate);
        topology.removeAllNTs(Arrays.asList(one, two));
        assertEquals(1, topology.getNodeTemplates().size());
    }

    @Test
    public void removeRT() {
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();
        TRelationshipTemplate one = new TRelationshipTemplate("1");
        TRelationshipTemplate two = new TRelationshipTemplate("2");
        TRelationshipTemplate three = new TRelationshipTemplate("3");
        topologyTemplate.addRelationshipTemplate(one);
        topologyTemplate.addRelationshipTemplate(two);
        topologyTemplate.addRelationshipTemplate(three);
        assertEquals(3, topologyTemplate.getRelationshipTemplates().size());

        TopologyWrapper topology = new TopologyWrapper(topologyTemplate);
        topology.removeRT(one);
        assertEquals(2, topology.getRelationshipTemplates().size());
    }

    @Test
    public void removeAllRTs() {
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();
        TRelationshipTemplate one = new TRelationshipTemplate("1");
        TRelationshipTemplate two = new TRelationshipTemplate("2");
        TRelationshipTemplate three = new TRelationshipTemplate("3");
        topologyTemplate.addRelationshipTemplate(one);
        topologyTemplate.addRelationshipTemplate(two);
        topologyTemplate.addRelationshipTemplate(three);
        assertEquals(3, topologyTemplate.getRelationshipTemplates().size());

        TopologyWrapper topology = new TopologyWrapper(topologyTemplate);
        topology.removeAllRTs(Arrays.asList(one, two));
        assertEquals(1, topology.getRelationshipTemplates().size());
    }

    @Test
    public void getHostedOns() {
        assertEquals(5, policiesTopology.getHostedOns(policiesTopology.getRelationshipTemplates()).size());
    }

    @Test
    public void getConnectsTos() {
        assertEquals(4, policiesTopology.getConnectsTos(policiesTopology.getRelationshipTemplates()).size());
    }

    @Test
    public void getTopLevelHosts() {
        List<TNodeTemplate> topLevelHosts = policiesTopology.getTopLevelHosts();
        assertEquals(1, topLevelHosts.size());
        assertEquals("straw", topLevelHosts.get(0).getId());
    }

    @Test
    public void getTopLevelNTs() {
        List<TNodeTemplate> topLevelNTs = policiesTopology.getTopLevelNTs();
        assertEquals(4, topLevelNTs.size());
        Set<String> ids = topLevelNTs.stream().map(HasId::getId).collect(Collectors.toSet());
        assertTrue(ids.contains("shetland_pony"));
        assertTrue(ids.contains("shetland_pony_2"));
        assertTrue(ids.contains("shetland_pony_3"));
        assertTrue(ids.contains("shetland_pony_4"));
    }

    @Test
    public void testGetTransitivePredecessors() {
        TNodeTemplate stall = minHostsTopology.getTopology().getNodeTemplate("stall");
        assertEquals(5, minHostsTopology.getTransitiveTopLevelPredecessors(stall).size());
        assertEquals(1, minHostsTopology.getTransitiveTopLevelHostPredecessors(stall).size());

        TNodeTemplate straw = minHostsTopology.getTopology().getNodeTemplate("straw");
        assertEquals(5, minHostsTopology.getTransitiveTopLevelPredecessors(straw).size());
        assertEquals(1, minHostsTopology.getTransitiveTopLevelHostPredecessors(straw).size());

        TNodeTemplate pony1 = minHostsTopology.getTopology().getNodeTemplate("shetland_pony");
        assertEquals(1, minHostsTopology.getTransitiveTopLevelPredecessors(pony1).size());
        assertEquals(0, minHostsTopology.getTransitiveTopLevelHostPredecessors(pony1).size());
    }

    @Test
    public void removeNotNeededSuccessors() {
        ServiceTemplateId policiesIO = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationMinHostsTest1", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(policiesIO).getTopologyTemplate());
        assertEquals(9, topology.getNodeTemplates().size());

        topology.removeNotNeededSuccessors(topology.getTopology().getNodeTemplate("shetland_pony"));
        assertEquals(7, topology.getNodeTemplates().size());
        assertEquals(4, topology.getRelationshipTemplates().size());
    }

    @Test
    public void getPresentTargetLabels() {
        Set<String> targetLabels = policiesTopology.getPresentTargetLabels();
        assertEquals(2, targetLabels.size());
        assertTrue(targetLabels.contains("PastureProvider".toLowerCase()));
        assertTrue(targetLabels.contains("FieldProvider".toLowerCase()));
    }

    @Test
    public void getRequirements() {
        TNodeTemplate nodeTemplate = policiesTopology.getTopology().getNodeTemplate("shetland_pony");
        List<TRequirement> requirements = policiesTopology.getRequirements(nodeTemplate);
        assertEquals(1, requirements.size());
        assertEquals("ReqWarmFloor", requirements.get(0).getType().getLocalPart());
    }

    @Test
    public void getRequirementsMultipleNTs() {
        List<TNodeTemplate> nodeTemplates = policiesTopology.getTopLevelNTs();
        List<TRequirement> requirements = policiesTopology.getRequirements(nodeTemplates);
        assertEquals(4, requirements.size());
    }

    @Test
    public void getPredecessorRequirements() {
        TNodeTemplate straw = policiesTopology.getTopology().getNodeTemplate("straw");
        List<TRequirement> requirements = policiesTopology.getPredecessorRequirements(straw);
        assertEquals(4, requirements.size());
    }

    @Test
    public void getTopLevelNtsByIds() {
        Map<String, TNodeTemplate> ntsByIds = policiesTopology.getTopLevelNtsByIds();
        Set<String> ids = new HashSet<>(Arrays.asList("shetland_pony", "shetland_pony_2", "shetland_pony_3", "shetland_pony_4"));
        assertEquals(4, ntsByIds.size());
        assertEquals(ids, ntsByIds.keySet());
    }

    @Test
    public void getNTsByIds() {
        Map<String, TNodeTemplate> ntsByIds = policiesTopology.getNTsByIds();
        Set<String> ids = new HashSet<>(Arrays.asList("shetland_pony", "shetland_pony_2", "shetland_pony_3", "shetland_pony_4",
            "straw", "stall"));
        assertEquals(6, ntsByIds.size());
        assertEquals(ids, ntsByIds.keySet());
    }

    @Test
    public void isTopLevelNT() {
        TNodeTemplate pony = policiesTopology.getTopology().getNodeTemplate("shetland_pony");
        TNodeTemplate stall = policiesTopology.getTopology().getNodeTemplate("stall");
        assertTrue(policiesTopology.isTopLevelNT(pony));
        assertFalse(policiesTopology.isTopLevelNT(stall));
    }

    @Test
    public void isBottomLevelNT() {
        TNodeTemplate pony = policiesTopology.getTopology().getNodeTemplate("shetland_pony");
        TNodeTemplate stall = policiesTopology.getTopology().getNodeTemplate("stall");
        assertFalse(policiesTopology.isBottomLevelNT(pony));
        assertTrue(policiesTopology.isBottomLevelNT(stall));
    }

    @Test
    public void isTopLevelHost() {
        TNodeTemplate pony = policiesTopology.getTopology().getNodeTemplate("shetland_pony");
        TNodeTemplate straw = policiesTopology.getTopology().getNodeTemplate("straw");
        assertFalse(policiesTopology.isTopLevelHost(pony));
        assertTrue(policiesTopology.isTopLevelHost(straw));
    }
}
