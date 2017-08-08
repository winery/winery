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

package org.eclipse.winery.repository.rest.resources.admin;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Test;

public class NamespaceResourceTest extends AbstractResourceTest {

	@Test
	public void getNamespaceList() throws Exception {
		this.setRevisionTo("8b57ea031ea0786a46ef8338ed322db886a77cd6");
		this.assertGet("admin/namespaces/", "entitytypes/admin/namspacesList.json");
		this.assertGetSize("admin/namespaces/", 11);
	}

}
