/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.TestWithGitRepoAndSshServer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceModelRefinementTest extends TestWithGitRepoAndSshServer {

    @Test
    void refineApplication() throws Exception {
        this.setRevisionTo("origin/plain");

        TNodeTemplate mySpecialNode = new TNodeTemplate.Builder("mySpecialNode", OpenToscaBaseTypes.OperatingSystem)
            .build();
        InstanceModelRefinement modelRefinement = new InstanceModelRefinement((template, plugins) ->
            template.getNodeTemplate("mySpecialNode") != null
                ? null
                : new InstanceModelRefinementPlugin("noop") {
                @Override
                public TTopologyTemplate apply(TTopologyTemplate template) {
                    template.addNodeTemplate(mySpecialNode);
                    return template;
                }

                @Override
                public Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced) {
                    return null;
                }

                @Override
                protected List<TTopologyTemplate> getDetectorGraphs() {
                    return null;
                }
            });

        TTopologyTemplate topologyTemplate = modelRefinement.refine(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "SshTest_w1-wip1", false)
        );

        assertNotNull(topologyTemplate);
        assertEquals(2, topologyTemplate.getNodeTemplates().size());
        assertTrue(
            topologyTemplate.getNodeTemplates().remove(mySpecialNode)
        );
    }
    
    @Test
    void refineEmpty() {
        InstanceModelRefinement modelRefinement = new InstanceModelRefinement((template, plugins) -> null);
        
        TTopologyTemplate topologyTemplate = modelRefinement.refine(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "myCoolNotExistingServiceTemplate", false)
        );        
        
        assertNull(topologyTemplate);
    }
}
