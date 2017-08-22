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
package org.eclipse.winery.repository.rest.resources.servicetemplates.selfserviceportal;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class XMLTest extends AbstractResourceTest {

	@Test
	public void getXML() throws Exception {
		this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/selfserviceportal/xml", "entitytypes/servicetemplates/selfserviceportal/getXML.xml");
	}

	@Test
	public void putXML() throws Exception {
		this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
		this.assertPut("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/selfserviceportal/", "entitytypes/servicetemplates/selfserviceportal/putXML.xml");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/selfserviceportal/xml", "entitytypes/servicetemplates/selfserviceportal/getAfterPut.xml");
	}
}
