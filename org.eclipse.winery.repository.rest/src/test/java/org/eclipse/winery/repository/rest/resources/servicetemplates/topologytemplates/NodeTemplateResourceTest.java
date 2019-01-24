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

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Test;

import static org.eclipse.jdt.annotation.Checks.assertNonNull;
import static org.junit.Assert.assertEquals;

public class NodeTemplateResourceTest extends AbstractResourceTest {

    @Test
    public void addStateArtifactToNodeTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        Path filePath = MavenTestingUtils.getProjectFilePath("src/test/resources/servicetemplates/plan.zip");
        this.assertNoContentPost("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithOneNodeTemplate_w1-wip1/"
                + "topologytemplate/nodetemplates/NodeTypeWith5Versions_0_3.4-w3-wip1/state",
            filePath);

        String s = start()
            .accept(ContentType.JSON.toString())
            .get(callURL("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithOneNodeTemplate_w1-wip1/" +
                "topologytemplate/nodetemplates/NodeTypeWith5Versions_0_3.4-w3-wip1"))
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .asString();

        ObjectMapper objectMapper = new ObjectMapper();
        TNodeTemplate nodeTemplate = objectMapper.readValue(s, TNodeTemplate.class);

        TDeploymentArtifacts deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
        assertNonNull(deploymentArtifacts);
        TDeploymentArtifact deploymentArtifact = deploymentArtifacts.getDeploymentArtifact("state");
        assertNonNull(deploymentArtifact);
        assertEquals(OpenToscaBaseTypes.stateArtifactType, deploymentArtifact.getArtifactType());
    }
}
