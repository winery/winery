/**
 * Copyright (c) 2017 University of Stuttgart. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 and the Apache License 2.0 which both accompany this
 * distribution, and are available at http://www.eclipse.org/legal/epl-v10.html and
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.eclipse.winery.repository.rest.resources.entitytypes.artifacttypes;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class ArtifactTypeResourceTest extends AbstractResourceTest {

	@Test
	public void postPropertiesDefinitionFromElement() throws Exception {
		this.setRevisionTo("85e157a2ebc512d760ce3def9fa1728ccef319b0");
		this.assertNoContentPost("artifacttypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttypes%252Ffruits/WAR/propertiesdefinition/",
			"entitytypes/artifacttypes/propertiesDefinitionsElement.json");
		this.assertGet("artifacttypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttypes%252Ffruits/WAR/",
			"entitytypes/artifacttypes/war_with_properties_element.xml");
	}

	@Test
	public void postPropertiesDefinitionFromType() throws Exception {
		this.setRevisionTo("85e157a2ebc512d760ce3def9fa1728ccef319b0");
		this.assertNoContentPost("artifacttypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttypes%252Ffruits/WAR/propertiesdefinition/",
			"entitytypes/artifacttypes/propertiesDefinitionsType.json");
		this.assertGet("artifacttypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttypes%252Ffruits/WAR/",
			"entitytypes/artifacttypes/war_with_properties_type.xml");
	}

	@Test
	public void postPropertiesDefinitionFromCustomKeyValuePairs() throws Exception {
		this.setRevisionTo("85e157a2ebc512d760ce3def9fa1728ccef319b0");
		this.assertNoContentPost("artifacttypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttypes%252Ffruits/WAR/propertiesdefinition/",
			"entitytypes/artifacttypes/propertiesDefinitionsKV.json");
		this.assertGet("artifacttypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttypes%252Ffruits/WAR/",
			"entitytypes/artifacttypes/war_with_properties_kv.xml");
	}

	@Test
	public void getPropertiesDefinitions() throws Exception {
		this.setRevisionTo("5142e3f95295710778060479aac6c2099e68703c");
		this.assertGet("artifacttypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttypes%252Ffruits/WAR/",
			"entitytypes/artifacttypes/war_with_properties.xml");
	}
}
