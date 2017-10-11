/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

package org.eclipse.winery.repository.rest.resources.entitytypes.requirementtypes;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class RequirementTypeResourceTest extends AbstractResourceTest {

	@Test
	public void getRequiredCapabilityTypeList() throws Exception {
		this.setRevisionTo("e889d1e0fdde49e23d91a7aaacffa180f57953f5");
		this.assertGet("requirementtypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frequirementtypes%252Ffruits/MinimumWeight/requiredcapabilitytype/", "entitytypes/requirementtypes/requiredCapabilityTypeList.json");
	}

	@Test
	public void setRequiredCapabilityTypeList() throws Exception {
		this.setRevisionTo("e889d1e0fdde49e23d91a7aaacffa180f57953f5");
		this.assertPutText("requirementtypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frequirementtypes%252Ffruits/MinimumWeight/requiredcapabilitytype/", "{http://winery.opentosca.org/test/capabilitytypes/fruits}Healthy");
	}

	@Test
	public void deleteRequiredCapabilityTypeList() throws Exception {
		this.setRevisionTo("e889d1e0fdde49e23d91a7aaacffa180f57953f5");
		this.assertDelete("requirementtypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frequirementtypes%252Ffruits/MinimumWeight/requiredcapabilitytype/");
	}
}
