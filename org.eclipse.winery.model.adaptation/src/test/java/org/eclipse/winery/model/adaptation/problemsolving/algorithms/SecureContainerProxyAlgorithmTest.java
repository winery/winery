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

package org.eclipse.winery.model.adaptation.problemsolving.algorithms;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.adaptation.problemsolving.ComponentFinding;
import org.eclipse.winery.model.adaptation.problemsolving.SolutionInputData;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecureContainerProxyAlgorithmTest extends TestWithGitBackedRepository {

    @Test
    public void insertContainerProxyTest() throws Exception {
        this.setRevisionTo("eb37f5cfec50c046985eac308e46482ce8bea8e3");

        TTopologyTemplate topologyTemplate = RepositoryFactory.getRepository().getElement(
            new ServiceTemplateId(QName.valueOf("{http://plain.winery.opentosca.org/servicetemplates}StWithSecureProxyContainerProblem_w1-wip1"))
        ).getTopologyTemplate();

        ArrayList<ComponentFinding> componentFindings = new ArrayList<>();
        componentFindings.add(new ComponentFinding(null, "sourceNode"));
        componentFindings.add(new ComponentFinding(null, "targetNode"));
        SolutionInputData inputData = new SolutionInputData();
        inputData.setFindings(componentFindings);

        new SecureContainerProxyAlgorithm().applySolution(topologyTemplate, inputData);

        assertEquals(6, topologyTemplate.getNodeTemplates().size());
        assertEquals(7, topologyTemplate.getRelationshipTemplates().size());

        assertNotNull(topologyTemplate.getNodeTemplate("sourceNode_proxy"));
        assertNotNull(topologyTemplate.getNodeTemplate("targetNode_proxy"));

        TRelationshipTemplate sourceToProxy = topologyTemplate.getRelationshipTemplate("sourceNode-connectsTo-sourceNode_proxy");
        assertNotNull(sourceToProxy);
        assertEquals("sourceNode", sourceToProxy.getSourceElement().getRef().getId());
        assertEquals("sourceNode_proxy", sourceToProxy.getTargetElement().getRef().getId());

        TRelationshipTemplate proxyToProxy = topologyTemplate.getRelationshipTemplate("sourceNode_proxy-securely-connectsTo-targetNode_proxy");
        assertNotNull(proxyToProxy);
        assertEquals("sourceNode_proxy", proxyToProxy.getSourceElement().getRef().getId());
        assertEquals("targetNode_proxy", proxyToProxy.getTargetElement().getRef().getId());

        TRelationshipTemplate targetProxyToTarget = topologyTemplate.getRelationshipTemplate("targetNode_proxy-connectsTo-targetNode");
        assertNotNull(targetProxyToTarget);
        assertEquals("targetNode_proxy", targetProxyToTarget.getSourceElement().getRef().getId());
        assertEquals("targetNode", targetProxyToTarget.getTargetElement().getRef().getId());

        TRelationshipTemplate sourceProxyOnDocker = topologyTemplate.getRelationshipTemplate("sourceNode_proxy-hostedOn-DockerEngine");
        assertNotNull(sourceProxyOnDocker);
        assertEquals("sourceNode_proxy", sourceProxyOnDocker.getSourceElement().getRef().getId());
        assertEquals("DockerEngine", sourceProxyOnDocker.getTargetElement().getRef().getId());

        TRelationshipTemplate targetProxyOnDocker2 = topologyTemplate.getRelationshipTemplate("targetNode_proxy-hostedOn-DockerEngine_2");
        assertNotNull(targetProxyOnDocker2);
        assertEquals("targetNode_proxy", targetProxyOnDocker2.getSourceElement().getRef().getId());
        assertEquals("DockerEngine_2", targetProxyOnDocker2.getTargetElement().getRef().getId());
    }
}
