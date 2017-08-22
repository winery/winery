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

package org.eclipse.winery.repository.rest.resources.entitytemplates;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class PropertiesResourceTest extends AbstractResourceTest {

	@Test
	public void getPropertiesJsonList() throws Exception {
		this.setRevisionTo("2fb90960edfb32e337a440c115976ff4bd7a5634");
		this.assertGet("artifacttemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttemplates%252Ffruits/baobab-ArtifactTemplate-Peel/properties/", "entitytemplates/initialProperties.json");
	}

	@Test
	public void postProperties() throws Exception {
		this.setRevisionTo("2fb90960edfb32e337a440c115976ff4bd7a5634");
		this.assertPut("artifacttemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttemplates%252Ffruits/baobab-ArtifactTemplate-Peel/properties/", "entitytemplates/updateProperty.xml");
		this.assertGet("artifacttemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttemplates%252Ffruits/baobab-ArtifactTemplate-Peel/properties/", "entitytemplates/updatedProperties.json");
	}
}
