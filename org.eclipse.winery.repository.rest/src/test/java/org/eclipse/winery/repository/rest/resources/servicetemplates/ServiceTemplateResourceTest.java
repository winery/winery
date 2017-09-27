/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 *     Oliver Kopp - test of create instance
 *     Karoline Saatkamp - test injector
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.servicetemplates;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Assert;
import org.junit.Test;

public class ServiceTemplateResourceTest extends AbstractResourceTest {

	@Test
	public void addServiceTemplate() throws Exception {
		this.setRevisionTo("337119ea2e694e70b994bcb3d97295856c0ab0f6");
		this.assertPost("servicetemplates/", "entitytypes/servicetemplates/baobab_create_servicetemplate.json");
		this.assertGet("servicetemplates/", "entitytypes/servicetemplates/baobab_inital.json");
	}

	@Test
	public void addTopologyTemplate() throws Exception {
		this.setRevisionTo("84d064a2f7390b3274ca8b3641a5902ba4c822d7");
		this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Fponyuniverse%252Finjector/FoodandHouseInjectionTest/topologytemplate/", "entitytypes/servicetemplates/straw-stall.json");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Fponyuniverse%252Finjector/FoodandHouseInjectionTest/topologytemplate/", "entitytypes/servicetemplates/straw-stall.json");
	}

	@Test
	public void getServicetemplate() throws Exception {
		this.setRevisionTo("a5fd2da6845e9599138b7c20c1fd9d727c1df66f");
		this.assertGet("servicetemplates/", "entitytypes/servicetemplates/baobab_inital.json");
	}

	@Test
	public void getServiceTemplateWithProperties() throws Exception {
		this.setRevisionTo("3465576f5b46079bb26f5c8e93663424440421a0");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Fponyuniverse%252Fdriverinjection/DriverInjectionTest/topologytemplate", "entitytypes/servicetemplates/DriverInjection.json");
	}

	@Test
	public void updateTopology() throws Exception {
		this.setRevisionTo("a5fd2da6845e9599138b7c20c1fd9d727c1df66f");
		this.assertGet("servicetemplates/", "entitytypes/servicetemplates/baobab_inital.json");
	}

	@Test
	public void createInstance() throws Exception {
		this.setRevisionTo("8cf0ce80c2c40c6ec178ef8e5bdc4e2fcdecc7f9");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/fruits", "farm", false);
		ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(id);
		Assert.assertNotNull(serviceTemplateResource);
	}

	@Test
	public void getInjectorOptions() throws Exception {
		this.setRevisionTo("84d064a2f7390b3274ca8b3641a5902ba4c822d7");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Fponyuniverse%252Finjector/FoodandHouseInjectionTest/injector/options", "servicetemplates/ServiceTemplateResource-getInjectionOptions.json");
	}

	@Test
	public void getInjectorOptionsWithoutOpenRequirementsBadRequest() throws Exception {
		this.setRevisionTo("black");
		//this.assertGetExpectBadRequestResponse("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/injector/options", "servicetemplates/pony.json");
		this.assertGetExpectBadRequestResponse("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/injector/options", "servicetemplates/ServiceTemplateResource-getInjectorOptionsWithoutOpenRequirements-badrequest.txt");
	}

	@Test
	public void getInjectOptionsForDriverInjection() throws Exception {
		this.setRevisionTo("f647f86f43b2cb7cc4f40a9b01f24cbc67e12d34");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Fponyuniverse%252Fdriverinjection/DriverInjectionTest/injector/options", "servicetemplates/ServiceTemplateResource-getInjectionOptions-DriverInjectionTest.json");
	}

	@Test
	public void injectNodeTemplates() throws Exception {
		this.setRevisionTo("d535f69bf50b2c4eda437be46b7ba1f85c4ff3bc");
		this.assertPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Fponyuniverse%252Finjector/FoodandHouseInjectionTest/injector/replace", "servicetemplates/ServiceTemplateResource-injectNodeTemplates-input2.json");
	}

	@Test
	public void injectNodeTemplatesWithDriverInjection() throws Exception {
		this.setRevisionTo("c25aa724201824fce6eddcc7c35a666c6e015880");
		this.assertPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Fponyuniverse%252Fdriverinjection/DriverInjectionTest/injector/replace", "servicetemplates/ServiceTemplateResource-injectNodeTemplates-input-DriverInjectionTest.json");
	}

	@Test
	public void getXaasPackagerData() throws Exception {
		this.setRevisionTo("c6484855143943a84ee50cb31d1ee986eb9a1c89");
		this.assertGet("servicetemplates/createfromartifact", "servicetemplates/xaasPackagerData.json");
	}
}
