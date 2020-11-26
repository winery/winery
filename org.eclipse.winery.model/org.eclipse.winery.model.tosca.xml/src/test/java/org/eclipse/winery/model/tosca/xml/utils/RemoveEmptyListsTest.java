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

package org.eclipse.winery.model.tosca.xml.utils;

import org.eclipse.winery.model.tosca.xml.XTEntityTemplate;
import org.eclipse.winery.model.tosca.xml.XTNodeTemplate;
import org.eclipse.winery.model.tosca.xml.XTPolicies;
import org.eclipse.winery.model.tosca.xml.XTTopologyTemplate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RemoveEmptyListsTest {

    @Test
    public void emptyPropertiesAndPoliciesListsRemovedFromNodeTemplate() {
        XTTopologyTemplate topologyTemplate = new XTTopologyTemplate();
        XTNodeTemplate nodeTemplate = new XTNodeTemplate();
        topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(nodeTemplate);

        nodeTemplate.setProperties(new XTEntityTemplate.Properties());
        nodeTemplate.setPolicies(new XTPolicies());

        assertNotNull(((XTNodeTemplate) topologyTemplate.getNodeTemplateOrRelationshipTemplate().get(0)).getPolicies());
        assertNotNull(((XTNodeTemplate) topologyTemplate.getNodeTemplateOrRelationshipTemplate().get(0)).getProperties());
        // preconditions fulfilled

        RemoveEmptyLists removeEmptyLists = new RemoveEmptyLists();
        removeEmptyLists.removeEmptyLists(topologyTemplate);
        
        assertNull(((XTNodeTemplate) topologyTemplate.getNodeTemplateOrRelationshipTemplate().get(0)).getPolicies());
        assertNull(((XTNodeTemplate) topologyTemplate.getNodeTemplateOrRelationshipTemplate().get(0)).getProperties());
    }
}
