/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.propertyconstraints;

import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.eclipse.winery.repository.resources.AbstractResourceTest;
import org.eclipse.winery.repository.resources.apiData.boundarydefinitions.PropertyConstraintsApiData;

import io.restassured.http.ContentType;
import jdk.nashorn.internal.parser.JSONParser;
import org.junit.Test;

public class PropertyConstraintsResourceTest extends AbstractResourceTest{

	@Test
	public void addPropertyMapping() throws Exception {
		this.setRevisionTo("86d472dca0340c02f67321f77a71d88f1eef93ce");
		this.assertNoContentPost("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/propertyconstraints/",
				"entitytypes/servicetemplates/boundarydefinitions/propertyConstraints/initial_property_constraint.json");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/boundarydefinitions/propertyconstraints/",
				"entitytypes/servicetemplates/boundarydefinitions/propertyConstraints/initial_property_constraint_get.json");
	}
}
