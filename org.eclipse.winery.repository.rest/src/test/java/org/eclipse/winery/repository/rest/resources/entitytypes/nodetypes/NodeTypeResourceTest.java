/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes;

import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.eclipse.winery.repository.rest.resources.TestIds;

import org.junit.jupiter.api.Test;
import org.xmlunit.matchers.CompareMatcher;

public class NodeTypeResourceTest extends AbstractResourceTest {

    @Test
    public void baobabInitialExistsUsingResource() throws Exception {
        this.setRevisionTo("5b5ad1106a3a428020b6bc5d2f154841acb5f779"); // repository containing boabab fruit only
        NodeTypeResource nodeTypeResource = (NodeTypeResource) NodeTypesResource.getComponentInstanceResource(TestIds.ID_FRUIT_BAOBAB);
        String testXml = BackendUtils.getXMLAsString(nodeTypeResource.getNodeType(), repository);
        String controlXml = readFromClasspath("entitytypes/nodetypes/baobab_initial.xml");
        org.hamcrest.MatcherAssert.assertThat(testXml, CompareMatcher.isIdenticalTo(controlXml).ignoreWhitespace());
    }

    @Test
    public void baobabInitialExistsUsingRest() throws Exception {
        this.setRevisionTo("5b5ad1106a3a428020b6bc5d2f154841acb5f779");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/", "entitytypes/nodetypes/baobab_initial_with_definitions.xml");
    }

    @Test
    public void baobabVisualAppearance() throws Exception {
        this.setRevisionTo("9c486269f6280e0eb14730d01554e7e4553a3d60");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/appearance",
            "entitytypes/nodetypes/baobab_visual_appearance.json");
    }

    @Test
    public void patternNodeTypeVisualAppearance() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("nodetypes/http%253A%252F%252Fplain.winery.opentosca.org%252Fpatterns/Infrastructure-As-A-Service_w1/appearance",
            "entitytypes/nodetypes/Infrasctrucure_As-A-Service_visual_appearance.json");
    }

    @Test
    public void baobabAdd50x50Image() throws Exception {
        this.setRevisionTo("9c486269f6280e0eb14730d01554e7e4553a3d60");
        this.assertUploadBinary("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/appearance/50x50", "entitytypes/nodetypes/bigIcon.png");
    }

    @Test
    public void baobabCapabilitiesJSON() throws Exception {
        this.setRevisionTo("8b125a426721f8a0eb17340dc08e9b571b0cd7f7");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/", "entitytypes/nodetypes/baobab_capabilites.json");
    }

    @Test
    public void baobabGetCapabilityDefinitions() throws Exception {
        this.setRevisionTo("8b125a426721f8a0eb17340dc08e9b571b0cd7f7");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/capabilitydefinitions/", "entitytypes/nodetypes/baobab_capability_definitions_get.json");
    }

    @Test
    public void baobabAddCapabilityDefinition() throws Exception {
        this.setRevisionTo("8b125a426721f8a0eb17340dc08e9b571b0cd7f7");
        this.assertNoContentPost("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/capabilitydefinitions/", "entitytypes/nodetypes/baobab_capability_definitions_add_capabilitydefinition.json");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/capabilitydefinitions/", "entitytypes/nodetypes/baobab_capability_definitions_add_capabilitydefinition_contents.json");
    }

    @Test
    public void addCapabilityDefinitionWithNoValidSourceTypes() throws Exception {
        this.setRevisionTo("8b125a426721f8a0eb17340dc08e9b571b0cd7f7");
        this.assertNoContentPost("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/capabilitydefinitions/", "entitytypes/nodetypes/baobab_capability_definitions_add_capabilitydefinition_without_validSourceTypes.json");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/capabilitydefinitions/", "entitytypes/nodetypes/baobab_capability_definitions_add_capabilitydefinition_contents.json");
    }

    @Test
    public void baobabDeleteCapabilityDefiniton() throws Exception {
        this.setRevisionTo("8b125a426721f8a0eb17340dc08e9b571b0cd7f7");
        this.assertDelete("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/capabilitydefinitions/ImportConstraintsHealthy/");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/capabilitydefinitions/", "entitytypes/nodetypes/baobab_capability_definitions_delete_contents.json");
    }

    @Test
    public void baobabGetRequirementDefinition() throws Exception {
        this.setRevisionTo("494da65d11c8191b8254cba23a82a1abbface09c");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/requirementdefinitions/", "entitytypes/nodetypes/baobab_requirement_definitions_get.json");
    }

    @Test
    public void baobabCreateRequirementDefinition() throws Exception {
        this.setRevisionTo("494da65d11c8191b8254cba23a82a1abbface09c");
        this.assertNoContentPost("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/requirementdefinitions/", "entitytypes/nodetypes/baobab_requirement_definitions_add_requirementdefinition.json");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/requirementdefinitions/", "entitytypes/nodetypes/baobab_requirement_definitions_add_requirementdefinition_contents.json");
    }

    @Test
    public void baobabDeleteRequirementdefinition() throws Exception {
        this.setRevisionTo("494da65d11c8191b8254cba23a82a1abbface09c");
        this.assertDelete("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/requirementdefinitions/ImportConstraintsHealthy/");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/requirementdefinitions/", "entitytypes/nodetypes/baobab_requirement_definitions_delete_contents.json");
    }

    @Test
    public void tagsRoundTrip() throws Exception {
        this.setRevisionTo("8b125a426721f8a0eb17340dc08e9b571b0cd7f7");
        this.assertPost("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/tags/", "entitytypes/nodetypes/baobab_tag_step1_add.json");
        this.assertPost("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/tags/", "entitytypes/nodetypes/baobab_tag_step2_add.json");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/tags/", "entitytypes/nodetypes/baobab_tag_step3_values.json");
        this.assertDelete("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/tags/1594499300/");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/tags/", "entitytypes/nodetypes/baobab_tag_step5_values.json");
    }

    @Test
    public void baobabRoundTrip() throws Exception {
        this.setRevisionTo("15cd64e30770ca7986660a34e1a4a7e0cf332f19"); // empty repository
        this.assertNotFound("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/");

        // the following line is not possible: We have to create it first (post) and then put data
        // this.assertPost("nodetypes/", "entitytypes/nodetypes/baobab_initial.xml");
        this.assertPost("nodetypes/", "entitytypes/nodetypes/baobab_create.json");
        this.assertPut("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/", "entitytypes/nodetypes/baobab_initial_with_definitions_put.xml");

        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/", "entitytypes/nodetypes/baobab_initial_with_definitions_expected.xml");

        this.assertPut("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/", "entitytypes/nodetypes/baobab_updated_put.xml");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/", "entitytypes/nodetypes/baobab_updated_expected.xml");
        this.assertDelete("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/");
        this.assertNotFound("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/");
    }

    @Test
    public void baobabHasNoImplementations() throws Exception {
        this.setRevisionTo("5b5ad1106a3a428020b6bc5d2f154841acb5f779");
        this.assertGetSize("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/implementations", 0);
    }

    @Test
    public void baobabHasNoImage() throws Exception {
        this.setRevisionTo("5b5ad1106a3a428020b6bc5d2f154841acb5f779");
        this.assertNotFound("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/appearance/50x50");
    }

    @Test
    public void baobabAddInterface() throws Exception {
        this.setRevisionTo("5b5ad1106a3a428020b6bc5d2f154841acb5f779");
        this.assertNoContentPost("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/interfaces/", "entitytypes/nodetypes/baobab_create_interface_and_operations.json");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/interfaces/", "entitytypes/nodetypes/baobab_initial_interface.json");
    }

    @Test
    public void baobabGetCsar() throws Exception {
        this.setRevisionTo("85e157a2ebc512d760ce3def9fa1728ccef319b0");
        this.assertGet("nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/?csar", "entitytypes/nodetypes/baobab.csar");
    }

    @Test
    public void getAllVisualAppearances() throws Exception {
        this.setRevisionTo("8cf0ce80c2c40c6ec178ef8e5bdc4e2fcdecc7f9");
        this.assertGet("nodetypes/allvisualappearancedata", "entitytypes/nodetypes/test.visualappearances.json");
    }

    @Test
    public void nodeTypeWithTwoKVPropertiesCorrectJson() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("nodetypes/http%253A%252F%252Fplain.winery.opentosca.org%252Fnodetypes/NodeTypeWithTwoKVProperties/", "entitytypes/nodetypes/NodeTypeWithTwoKVProperties.json");
    }

    @Test
    public void getNodeTypeVersions() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("nodetypes/http%253A%252F%252Fopentosca.org%252Fnodetypes/NodeTypeWith5Versions_0.3.4-w3?versions",
            "entitytypes/nodetypes/nodeTypeWith5Versions.json");
    }
}
