/********************************************************************************
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
 ********************************************************************************/
package org.eclipse.winery.model.ids.definitions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefinitionsChildIdTest {

    @Test
    public void testCompareToDifferentDefinitionsChildId() {
        String stId = "myDefinition";
        String otherStId = "myOtherDefinition";
        DefinitionsChildId serviceTemplate = new ServiceTemplateId("http://example.org/tosca/serviceTemplates", stId, false);
        DefinitionsChildId otherServiceTemplate = new ServiceTemplateId("http://example.org/tosca/serviceTemplates", otherStId, false);

        assertEquals(stId.compareTo(otherStId), serviceTemplate.compareTo(otherServiceTemplate));
    }

    @Test
    public void testCompareToDifferentNamespace() {
        String stNamespace = "http://example.org/tosca/different/serviceTemplates";
        String otherNamespace = "http://example.org/tosca/serviceTemplates";
        DefinitionsChildId serviceTemplate = new ServiceTemplateId(stNamespace, "myDefinition", false);
        DefinitionsChildId otherServiceTemplate = new ServiceTemplateId(otherNamespace, "myDefinition", false);

        assertEquals(stNamespace.compareTo(otherNamespace), serviceTemplate.compareTo(otherServiceTemplate));
    }

    @Test
    public void testCompareToDifferentSubClass() {
        DefinitionsChildId serviceTemplate = new ServiceTemplateId("http://example.org/tosca/serviceTemplates", "myDefinition", false);
        DefinitionsChildId nodeType = new NodeTypeId("http://example.org/tosca/nodeTypes", "myDefinition", false);

        assertEquals("ServiceTemplate".compareTo("NodeType"), serviceTemplate.compareTo(nodeType));
    }

    @Test
    public void testCompareToEqualDefinitionsChildId() {
        DefinitionsChildId policyTemplate = new PolicyTemplateId("http://example.org/tosca/policyTemplates", "myPolicyTemplate", false);
        DefinitionsChildId secondPolicyTemplate = new PolicyTemplateId("http://example.org/tosca/policyTemplates", "myPolicyTemplate", false);

        assertEquals(0, policyTemplate.compareTo(secondPolicyTemplate));
    }

    @Test
    public void compareTwoDefinitionsWithDifferentVersions() {
        DefinitionsChildId newerVersion = new NodeTypeId("http://example.org/tosca/nodeTypes", "myNodeType_3.2.4-w7", false);
        DefinitionsChildId olderVersion = new NodeTypeId("http://example.org/tosca/nodeTypes", "myNodeType_3.2.4-w7-wip40", false);

        assertEquals(-1, olderVersion.compareTo(newerVersion));
        assertEquals(1, newerVersion.compareTo(olderVersion));
    }
}
