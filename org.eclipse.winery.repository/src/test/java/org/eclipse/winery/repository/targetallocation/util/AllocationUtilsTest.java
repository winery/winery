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

package org.eclipse.winery.repository.targetallocation.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AllocationUtilsTest extends TestWithGitBackedRepository {

    private TTopologyTemplate topology;

    @BeforeEach
    public void setUp() throws GitAPIException {
        setRevisionTo("bb9ff7e08f7b72d30a2fae326740ef8051701671");
        ServiceTemplateId id = new ServiceTemplateId("http://www.winery.opentosca.org/test/targetallocation/servicetemplates",
            "TargetAllocationFulfillPoliciesTest1", false);
        topology = repository.getElement(id).getTopologyTemplate();
    }

    @Test
    public void getPermutations() {
        List<List<String>> possibilities = Arrays.asList(Arrays.asList("A", "B"), Arrays.asList("C", "D"));

        // bound
        List<List<String>> results = AllocationUtils.getPermutations(possibilities, 4);
        assertEquals(4, results.size());
        assertEquals("[[A, C], [A, D], [B, C], [B, D]]", results.toString());
        results = AllocationUtils.getPermutations(possibilities, 2);
        assertEquals(2, results.size());
        assertEquals("[[A, C], [A, D]]", results.toString());

        // negative max
        results = AllocationUtils.getPermutations(possibilities, -10);
        assertEquals(0, results.size());

        // special case of one element
        possibilities = Collections.singletonList(Arrays.asList("A", "B", "C"));
        results = AllocationUtils.getPermutations(possibilities, Integer.MAX_VALUE);
        assertEquals("[[A], [B], [C]]", results.toString());
    }

    @Test
    public void getPolicyTemplates() {
        TNodeTemplate nodeTemplate = topology.getNodeTemplate("shetland_pony_3");
        List<TPolicyTemplate> policies = AllocationUtils.getPolicyTemplates(nodeTemplate);

        assertEquals(1, policies.size());
        assertEquals("FloorWarmth10", policies.get(0).getId());
    }

    @Test
    public void deepcopy() {
        topology.getNodeTemplates().forEach(nt -> {
            nt.setMinInstances(1);
            nt.setMaxInstances("1");
        });
        TTopologyTemplate clone = BackendUtils.clone(topology);
        assertEquals(topology, clone);
        TTopologyTemplate cloneNotEquals = AllocationUtils.deepcopy(topology);
        assertNotEquals(topology, cloneNotEquals);
    }

    @Test
    public void cloneNotEqualsNT() {
        TNodeTemplate original = topology.getNodeTemplate("shetland_pony");
        original.setMinInstances(1);
        original.setMaxInstances("1");
        TNodeTemplate clone = BackendUtils.clone(original);
        assertEquals(original, clone);
        TNodeTemplate cloneNotEquals = AllocationUtils.clone(original, true);
        assertNotEquals(original, cloneNotEquals);
    }

    @Test
    public void cloneNotEqualsRT() {
        TRelationshipTemplate original = topology.getRelationshipTemplates().get(0);
        TRelationshipTemplate clone = BackendUtils.clone(original);
        assertEquals(original, clone);
        TRelationshipTemplate cloneNotEquals = AllocationUtils.clone(original, true);
        assertNotEquals(original, cloneNotEquals);
    }
}
