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

package org.eclipse.winery.repository.resources.entitytypes.policytypes;

import org.eclipse.winery.repository.resources.AbstractResourceTest;

import org.junit.Test;

public class PolicyTypeResourceTest extends AbstractResourceTest {

	@Test
	public void getInstancesOfOnePolicyTypeTest() throws Exception {
		this.setRevisionTo("34adf7aba86ff05ce34741bb5c5cb50e468ba7ff");
		this.assertGet("policytypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fpolicytypes%252Ffruits/european/instances/", "entitytypes/policytypes/policyInstances_european.json");
	}

	@Test
	public void getPoliciesGroupedByNamespaceTest() throws Exception {
		this.setRevisionTo("34adf7aba86ff05ce34741bb5c5cb50e468ba7ff");
		this.assertGet("policytypes?grouped=angularSelect", "entitytypes/policytypes/allGroupedByNamespace.json");
	}
}
