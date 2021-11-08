/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NodeTemplateResourceTest extends AbstractResourceTest {

    @Test
    public void addStateArtifactToNodeTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        Path filePath = MavenTestingUtils.getProjectFilePath("src/test/resources/servicetemplates/plan.zip");
        this.assertNoContentPost("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithOneNodeTemplate_w1-wip1/"
                + "topologytemplate/nodetemplates/NodeTypeWith5Versions_0_3.4-w3-wip1/state",
            filePath);

        TNodeTemplate nodeTemplate = getObjectFromGetRequest("servicetemplates/" +
                "http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithOneNodeTemplate_w1-wip1/" +
                "topologytemplate/nodetemplates/NodeTypeWith5Versions_0_3.4-w3-wip1",
            TNodeTemplate.class
        );

        List<TDeploymentArtifact> deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
        assertNotNull(deploymentArtifacts);
        Optional<TDeploymentArtifact> state = deploymentArtifacts.stream()
            .filter(artifact -> artifact.getName().equals("state"))
            .findFirst();
        assertTrue(state.isPresent());
        assertEquals(OpenToscaBaseTypes.stateArtifactType, state.get().getArtifactType());
    }

    @Test
    public void addStateArtifactToNodeTemplateThatAlreadyHasADeploymentArtifact() throws Exception {
        this.setRevisionTo("origin/plain");

        Path filePath = MavenTestingUtils.getProjectFilePath("src/test/resources/servicetemplates/plan.zip");
        this.assertNoContentPost("servicetemplates/http%253A%252F%252Fopentosca.org%252Fexamples%252Fservicetemplates/ServiceTemplateWithDeploymentArtifact_w1-wip1/"
                + "topologytemplate/nodetemplates/StatefulComponent_w1-wip1/state",
            filePath);

        TNodeTemplate nodeTemplate = getObjectFromGetRequest("servicetemplates/" +
                "http%253A%252F%252Fopentosca.org%252Fexamples%252Fservicetemplates/ServiceTemplateWithDeploymentArtifact_w1-wip1/" +
                "topologytemplate/nodetemplates/StatefulComponent_w1-wip1",
            TNodeTemplate.class
        );

        List<TDeploymentArtifact> deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
        assertNotNull(deploymentArtifacts);
        assertEquals(2, deploymentArtifacts.size());

        Optional<TDeploymentArtifact> testArtifact = deploymentArtifacts.stream()
            .filter(artifact -> artifact.getName().equals("test-artifact"))
            .findFirst();
        assertTrue(testArtifact.isPresent());
        assertNotNull(testArtifact.get());

        assertNotNull(deploymentArtifacts);
        Optional<TDeploymentArtifact> state = deploymentArtifacts.stream()
            .filter(artifact -> artifact.getName().equals("state"))
            .findFirst();
        assertTrue(state.isPresent());
        assertEquals(OpenToscaBaseTypes.stateArtifactType, state.get().getArtifactType());
    }
}
