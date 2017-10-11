/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

package org.eclipse.winery.repository.rest.resources.entitytypeimplementations.relationshiptypeimplementations;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class RelationshipTypeImplementationResourceTest extends AbstractResourceTest {

	@Test
	public void getComponentAsJson() throws Exception {
		this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
		this.assertGet("relationshiptypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypeimplementations%252Ffruits/kiwi_implementation/", "entityimplementations/relationshiptypeimplementations/initial.json");
	}

	@Test
	public void getInheritanceData() throws Exception {
		this.setRevisionTo("410ec7b55bf7cf7daa5e18f4a8562d7b7c0efd1d");
		this.assertGet("relationshiptypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypeimplementations%252Ffruits/kiwi_implementation/inheritance",
			"entityimplementations/relationshiptypeimplementations/kiwi_initial_inheritance.json");
	}

	@Test
	public void putInheritanceData() throws Exception {
		this.setRevisionTo("aae0a874dd18cfed6abf4e33cb06f78a5a22b861");
		this.assertPut("relationshiptypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypeimplementations%252Ffruits/attendTo_implementation/inheritance/",
			"entityimplementations/relationshiptypeimplementations/attendTo_inheritance.json");
		this.assertGet("relationshiptypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypeimplementations%252Ffruits/attendTo_implementation/xml/",
			"entityimplementations/relationshiptypeimplementations/attendTo_inheritance.xml");

	}
}
