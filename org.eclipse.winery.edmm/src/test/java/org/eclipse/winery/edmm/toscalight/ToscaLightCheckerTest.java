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

package org.eclipse.winery.edmm.toscalight;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.edmm.EdmmDependantTest;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToscaLightCheckerTest extends EdmmDependantTest {

    protected ToscaLightCheckerTest() throws UnsupportedEncodingException {
    }

    @Test
    void checkValidServiceTemplate() throws Exception {
        // region *** build the TopologyTemplate ***
        TTopologyTemplate.Builder topology = new TTopologyTemplate.Builder();
        topology.addNodeTemplates(nodeTemplates.get("test_node_1"));
        topology.addNodeTemplates(nodeTemplates.get("test_node_2"));
        topology.addNodeTemplates(nodeTemplates.get("test_node_3"));
        topology.addNodeTemplates(nodeTemplates.get("test_node_4"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("2_hosted_on_3"));
        topology.addRelationshipTemplate(relationshipTemplates.get("4_hosted_on_1"));
        topology.addRelationshipTemplate(relationshipTemplates.get("1_connects_to_2"));
        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setTopologyTemplate(topology.build());
        serviceTemplate.setName("MyTestServiceTemplate");
        serviceTemplate.setId(serviceTemplate.getName());
        serviceTemplate.setTargetNamespace(this.NAMESPACE);
        // endregion

        ToscaLightChecker toscaLightChecker = new ToscaLightChecker(this.nodeTypes, this.relationshipTypes, this.edmmTypeExtendsMapping, this.edmm1to1Mapping);
        boolean compliant = toscaLightChecker.isToscaLightCompliant(serviceTemplate);
        Map<QName, List<String>> resultList = toscaLightChecker.getErrorList();

        assertTrue(compliant);
        // expect 7 instead of 9 as the hostedOn type is used 3 times
        assertEquals(7, resultList.size());

        resultList.forEach((qName, errorList) -> assertTrue(errorList.isEmpty()));
    }
}
