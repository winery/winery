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

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConnectsToGraphTest extends TestWithGitBackedRepository {

    @Test
    public void testClone() {
        ConnectsToGraph.Node node1 = new ConnectsToGraph.Node("id", "targetLabel");
        ConnectsToGraph.Node node2 = new ConnectsToGraph.Node(node1);

        node1.setTargetLabel("A");
        assertEquals("A", node1.getTargetLabel());
        assertEquals("targetLabel", node2.getTargetLabel());

        node1.getNodeTemplateIds().clear();
        assertEquals(0, node1.getNodeTemplateIds().size());
        assertEquals(1, node2.getNodeTemplateIds().size());
    }

    @Test
    public void testCreation() throws GitAPIException {
        setRevisionTo("bb9ff7e08f7b72d30a2fae326740ef8051701671");
        ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier",
            "DASpecificationTest", false);
        TopologyWrapper topology = new TopologyWrapper(repository.getElement(id).getTopologyTemplate());

        assertEquals(5, topology.getNodeTemplates().size());
        assertEquals(4, topology.getRelationshipTemplates().size());
        ConnectsToGraph connectsToGraph = new ConnectsToGraph(topology);
        assertEquals(3, connectsToGraph.getNodes().size());
        assertEquals(2, connectsToGraph.getEdges().size());
    }
}
