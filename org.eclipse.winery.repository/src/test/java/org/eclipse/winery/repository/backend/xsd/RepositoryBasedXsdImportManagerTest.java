/**
 * Copyright (c) 2017 University of Stuttgart. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 and the Apache License 2.0 which both accompany this
 * distribution, and are available at http://www.eclipse.org/legal/epl-v10.html and
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors: Lukas Harzenetter - initial API and implementation
 */
package org.eclipse.winery.repository.backend.xsd;

import java.util.List;

import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class RepositoryBasedXsdImportManagerTest extends TestWithGitBackedRepository {

	@Test
	public void getAllDeclaredElementsLocalNamesTest() throws Exception {
		this.setRevisionTo("5fdcffa9ccd17743d5498cab0914081fc33606e9");

		RepositoryBasedXsdImportManager manager = (RepositoryBasedXsdImportManager) this.repository.getXsdImportManager();
		List<NamespaceAndDefinedLocalNames> list = manager.getAllDeclaredElementsLocalNames();

		Assert.assertEquals(1, list.size());
	}

	@Test
	@Ignore("Not yet implemented.")
	public void getAllDefinedLocalNamesForElementsTest() throws Exception {
		this.setRevisionTo("5fdcffa9ccd17743d5498cab0914081fc33606e9");

		RepositoryBasedXsdImportManager manager = (RepositoryBasedXsdImportManager) this.repository.getXsdImportManager();
		List<NamespaceAndDefinedLocalNames> list = manager.getAllDefinedTypesLocalNames();

		Assert.assertEquals(1, list.size());
	}
}
