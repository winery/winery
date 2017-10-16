/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class NodeTypesResourceTest extends AbstractResourceTest {
	
	@Test
	public void getListOfAllIdsReturnsCorrectJson() throws Exception {
		// we use the concrete Id as this test lists all available PLAIN node types. If these are changed, this test will fail if "origin/plain" was used.
		this.setRevisionTo("67bb1650522ced3872220ad2d17c1afd82e7e1f3");
		this.assertGet("nodetypes/?grouped&full", "entitytypes/nodetypes/all-nodetypes-of-commit-67bb1650522ced3872220ad2d17c1afd82e7e1f3.json");
	}  

}
