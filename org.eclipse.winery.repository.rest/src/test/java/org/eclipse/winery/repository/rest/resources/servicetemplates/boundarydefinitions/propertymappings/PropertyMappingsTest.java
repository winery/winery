/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.propertymappings;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class PropertyMappingsTest extends AbstractResourceTest {

	@Test
	public void addPropertyMapping() throws Exception {
		this.setRevisionTo("86d472dca0340c02f67321f77a71d88f1eef93ce");
		this.assertNoContentPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/propertymappings/",
			"entitytypes/servicetemplates/boundarydefinitions/propertyMappings/initial_property_mapping.json");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/propertymappings/",
			"entitytypes/servicetemplates/boundarydefinitions/propertyMappings/initial_property_mappings_get.json");
	}

	@Test
	public void getPropertyMappings() throws Exception {
		this.setRevisionTo("cb41dac45499903cf18c03036a6a2629e55df08e");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/propertymappings/",
			"entitytypes/servicetemplates/boundarydefinitions/propertyMappings/initial_property_mappings_get.json");
	}
}
