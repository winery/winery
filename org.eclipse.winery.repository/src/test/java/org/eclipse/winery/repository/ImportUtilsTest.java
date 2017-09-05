/**
 * Copyright (c) 2017 University of Stuttgart. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 and the Apache License 2.0 which both accompany this
 * distribution, and are available at http://www.eclipse.org/legal/epl-v10.html and
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors: Lukas Harzenetter - initial API and implementation
 */
package org.eclipse.winery.repository;

import java.util.Optional;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.repository.backend.ImportUtils;

import org.junit.Assert;
import org.junit.Test;

public class ImportUtilsTest extends TestWithGitBackedRepository {

	@Test
	public void getLocationForImportTest() throws Exception {
		this.setRevisionTo("5fdcffa9ccd17743d5498cab0914081fc33606e9");

		XSDImportId id = new XSDImportId(
			new Namespace("http://opentosca.org/nodetypes", false),
			new XMLId("CloudProviderProperties", false));
		Optional<String> importLocation = ImportUtils.getLocation(id);

		Assert.assertEquals(true, importLocation.isPresent());
	}
}
