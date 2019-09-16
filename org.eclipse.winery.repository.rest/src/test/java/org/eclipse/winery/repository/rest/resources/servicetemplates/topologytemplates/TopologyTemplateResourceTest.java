/*******************************************************************************
 * Copyright (c) 2012-2019 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Assert;
import org.junit.Test;

public class TopologyTemplateResourceTest extends AbstractResourceTest {

    private static final String FOLDERPATH = "http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/";
    private static final String ENTITY_TYPE = "topologytemplates/";
    private static final String INSTANCE_XML_PATH = "servicetemplates/" + ENTITY_TYPE + "fruits-at-3fe0df76e98d46ead68295920e5d1cf1354bdea1.xml";
    private static final String BAOBAB_JSON_PATH = "servicetemplates/" + ENTITY_TYPE + "list-at-3fe0df76e98d46ead68295920e5d1cf1354bdea1.json";
    private static final String INSTANCE_URL = "servicetemplates/" + FOLDERPATH;

    @Test
    public void getInstanceXml() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertGet(replacePathStringEncoding(INSTANCE_URL), INSTANCE_XML_PATH);
    }

    @Test
    public void getServicetemplate() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertGet("servicetemplates/", BAOBAB_JSON_PATH);
    }

    @Test
    public void getComponentInstanceJSON() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/baobab_topologytemplate.json");
    }

    @Test
    public void getComponentInstanceXML() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/baobab_topologytemplate.xml");
    }

    @Test
    public void topologyTemplateUpdate() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/baobab_topologytemplate_v2.json");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/baobab_topologytemplate_v2.json");
    }

    @Test
    public void topologyTemplateUpdateWithEmptyListsGetTheListsRemoved() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/baobab_topologytemplate_v2-with-empty-objects.json");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/baobab_topologytemplate_v2.json");
    }

    @Test
    public void apacheSparkAppOnVspheretopologyTemplateUpdateWithEmptyListsGetTheListsRemoved() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/apache-spark-on-vsphere-input.json");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/apache-spark-on-vsphere-result.json");
    }

    @Test
    public void farmTopologyTemplateIsCorrectlyReturnAsJson() throws Exception {
        this.setRevisionTo("2d35f0d3c15b384c53df10967164d97e4a7dd6f2");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/farm/topologytemplate/", "servicetemplates/farm_topologytemplate.json");
    }

    @Test
    public void farmTopologyTemplateIsCorrectlyReturnedAsXml() throws Exception {
        this.setRevisionTo("2d35f0d3c15b384c53df10967164d97e4a7dd6f2");
        this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/farm/topologytemplate/", "servicetemplates/farm_topologytemplate.xml");
    }

    @Test
    public void farmTopologyTemplateCanBeCreatedAsJson() throws Exception {
        this.setRevisionTo("1e2054315f18e80c466c26e6918d6506ce53f7f7");

        // Quick hack to ensure that the service template containing the tpology template exists
        ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/fruits", "farm", false);
        RepositoryFactory.getRepository().flagAsExisting(id);

        this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/farm/topologytemplate/", "servicetemplates/farm_topologytemplate.json");
    }

    @Test
    public void farmTopologyTemplateJsonCanBeParsed() throws Exception {
        final String jsonStr = AbstractResourceTest.readFromClasspath("servicetemplates/farm_topologytemplate.json");
        final TTopologyTemplate topologyTemplate = BackendUtils.mapper.readValue(jsonStr, TTopologyTemplate.class);
        Assert.assertNotNull(topologyTemplate);
    }

    @Test
    public void strawStallTopologyTemplateJsonCanBeParsed() throws Exception {
        final String jsonStr = AbstractResourceTest.readFromClasspath("entitytypes/servicetemplates/straw-stall.json");
        final TTopologyTemplate topologyTemplate = BackendUtils.mapper.readValue(jsonStr, TTopologyTemplate.class);
    }

    @Test
    public void checkJsonOfTopologyTemplateMinimalExampleWithAllPropertyVariants() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateMinimalExampleWithAllPropertyVariants/topologytemplate/", "servicetemplates/topologytemplates/plain-TopologyTemplateMinimalExampleWithAllPropertyVariants.json");
        this.assertPut("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateMinimalExampleWithAllPropertyVariants/topologytemplate/", "servicetemplates/topologytemplates/plain-TopologyTemplateMinimalExampleWithAllPropertyVariants.json");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateMinimalExampleWithAllPropertyVariants/topologytemplate/", "servicetemplates/topologytemplates/plain-TopologyTemplateMinimalExampleWithAllPropertyVariants.json");
    }

    @Test
    public void jsonWithEmptyPoliciesElementProducesValidJson() throws Exception {
        this.setRevisionTo("origin/plain");
        // for testing an arbitrary existing service template is used
        // we do not fill the properties of the node template to have easy testing
        this.assertPut("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateMinimalExampleWithAllPropertyVariants/topologytemplate/", "entitytypes/servicetemplates/topologytemplates/empty-xml-test--topology-with-empty-policy.json");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateMinimalExampleWithAllPropertyVariants/topologytemplate/", "entitytypes/servicetemplates/topologytemplates/empty-xml-test--topology-with-empty-policy-empty-relationshiptemplates.json");
    }

    @Test
    public void jsonWithEmptyPoliciesElementProducesValidXml() throws Exception {
        this.setRevisionTo("origin/plain");
        // for testing an arbitrary existing service template is used
        this.assertPut("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateMinimalExampleWithAllPropertyVariants/topologytemplate/", "entitytypes/servicetemplates/topologytemplates/empty-xml-test--topology-with-empty-policy.json");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateMinimalExampleWithAllPropertyVariants/topologytemplate/", "entitytypes/servicetemplates/topologytemplates/empty-xml-test--topology-without-empty-elements.xml");
    }

    @Test
    public void getAvailableFeatures() throws Exception {
        this.setRevisionTo("origin/plain");

        this.assertGet("servicetemplates/http%253A%252F%252Fopentosca.org%252Fadd%252Fmanagement%252Fto%252Finstances%252Fservicetemplates/STWithBasicManagementOnly_w1-wip1/topologytemplate/availablefeatures",
            "entitytypes/servicetemplates/topologytemplates/availableFeaturesForBasicManagementST.json");
    }

    @Test
    public void getAvailableFeaturesWithDifferentFeaturesForSameType() throws Exception {
        this.setRevisionTo("origin/plain");

        this.assertGet("servicetemplates/http%253A%252F%252Fopentosca.org%252Fadd%252Fmanagement%252Fto%252Finstances%252Fservicetemplates/STWithBasicManagementOnly_w1-wip4/topologytemplate/availablefeatures",
            "entitytypes/servicetemplates/topologytemplates/availableFeaturesForBasicManagementSTWithDifferentFeaturesForSameType.json");
    }

    @Test
    public void applyAvailableFeatures() throws Exception {
        this.setRevisionTo("origin/plain");

        this.assertPutWithResponse("servicetemplates/http%253A%252F%252Fopentosca.org%252Fadd%252Fmanagement%252Fto%252Finstances%252Fservicetemplates/STWithBasicManagementOnly_w1-wip1/topologytemplate/availablefeatures",
            "entitytypes/servicetemplates/topologytemplates/availableFeaturesForBasicManagementST.json");
    }

    @Test
    public void getNewVersionList() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ServiceTemplateWithTwoNodeTemplates_oldVersions-w1-wip1/topologytemplate/newversions",
            "servicetemplates/newVersionList.json");
    }

    @Test
    public void getNewVersionListWithoutFeaturesAndGeneratedTypes() throws Exception {
        this.setRevisionTo("origin/plain");
        this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/STWithUpdateableComponent_w1-wip1/topologytemplate/newversions",
            "servicetemplates/nodeTemplateVersionListWithoutFeatures.json");
    }
}
