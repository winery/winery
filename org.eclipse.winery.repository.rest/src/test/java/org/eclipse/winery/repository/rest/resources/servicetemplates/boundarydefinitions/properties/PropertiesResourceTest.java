/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.properties;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;


public class PropertiesResourceTest extends AbstractResourceTest{

	@Test
	public void addProperties() throws Exception {
		this.setRevisionTo("a5fd2da6845e9599138b7c20c1fd9d727c1df66f");
		this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/properties/","entitytypes/servicetemplates/boundarydefinitions/properties/baobab_initial_properties.xml");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/","entitytypes/servicetemplates/boundarydefinitions/properties/baobab_initial_properties_get.json");
	}

}
