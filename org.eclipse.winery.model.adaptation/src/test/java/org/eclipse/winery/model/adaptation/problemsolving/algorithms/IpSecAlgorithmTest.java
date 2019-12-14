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

class IpSecAlgorithmTest extends TestWithGitBackedRepository {

    @Test
    public void applySolutionTest() throws Exception {
        this.setRevisionTo("a23f1c89c77fcde4de9fe7822532dc04e31731a0");
        IpSecAlgorithm ipSecAlgorithm = new IpSecAlgorithm();

        TTopologyTemplate topologyTemplate = RepositoryFactory.getRepository().getElement(
            new ServiceTemplateId(QName.valueOf("{http://plain.winery.opentosca.org/servicetemplates}ServiceTemplateWithIpSecProblem_w1-wip1"))
        ).getTopologyTemplate();

        ArrayList<ComponentFinding> componentFindings = new ArrayList<>();
        componentFindings.add(new ComponentFinding(null, "NodeTypeWithImplementation_1.0-w1-wip1"));
        componentFindings.add(new ComponentFinding(null, "NodeTypeWithXmlElementProperty"));
        SolutionInputData inputData = new SolutionInputData();
        inputData.setFindings(componentFindings);

        ipSecAlgorithm.applySolution(topologyTemplate, inputData);

        assertEquals(4, topologyTemplate.getNodeTemplates().size());
        assertEquals(QName.valueOf("{http://plain.winery.opentosca.org/secure/nodetypes}ubuntu_18-secure-w1-wip1"), topologyTemplate.getNodeTemplate("replaceableNode_1").getType());
        assertEquals(QName.valueOf("{http://plain.winery.opentosca.org/secure/nodetypes}ubuntu_18-secure-w1-wip1"), topologyTemplate.getNodeTemplate("replaceableNode_2").getType());
        assertEquals(5, topologyTemplate.getRelationshipTemplates().size());
        TRelationshipTemplate forward = topologyTemplate.getRelationshipTemplate("replaceableNode_2-securely_connectsTo-replaceableNode_1");
        assertNotNull(forward);
        assertEquals("replaceableNode_2", forward.getSourceElement().getRef().getId());
        assertEquals("replaceableNode_1", forward.getTargetElement().getRef().getId());
        TRelationshipTemplate backward = topologyTemplate.getRelationshipTemplate("replaceableNode_1-securely_connectsTo-replaceableNode_2");
        assertNotNull(backward);
        assertEquals("replaceableNode_1", backward.getSourceElement().getRef().getId());
        assertEquals("replaceableNode_2", backward.getTargetElement().getRef().getId());
    }
}
