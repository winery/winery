/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 * Lukas Harzenetter - initial API and implementation
 */

package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.interfaces;

import org.eclipse.winery.repository.resources.AbstractResourceTest;

import org.junit.Test;

public class InterfacesResourceTest extends AbstractResourceTest {

	@Test
	public void addNodeTemplateInterface() throws Exception {
		this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
		this.assertPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/interfaces/", "entitytypes/servicetemplates/boundarydefinitions/interfaces/addNodeTemplateInterface.json");
	}

	@Test
	public void addRelationshipTemplateInterface() throws Exception {
		this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
		this.assertPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/interfaces/", "entitytypes/servicetemplates/boundarydefinitions/interfaces/addRelationshipTemplateInterface.json");
	}

	@Test
	public void addPlanInterface() throws Exception {
		this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
		this.assertPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/interfaces/", "entitytypes/servicetemplates/boundarydefinitions/interfaces/addPlanInterface.json");
	}
}
