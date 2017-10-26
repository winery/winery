/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Karoline Saatkamp - add test
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

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
	}

	@Test
	public void strawStallTopologyTemplateJsonCanBeParsed() throws Exception {
		final String jsonStr = AbstractResourceTest.readFromClasspath("entitytypes/servicetemplates/straw-stall.json");
		final TTopologyTemplate topologyTemplate = BackendUtils.mapper.readValue(jsonStr, TTopologyTemplate.class);
	}
}
