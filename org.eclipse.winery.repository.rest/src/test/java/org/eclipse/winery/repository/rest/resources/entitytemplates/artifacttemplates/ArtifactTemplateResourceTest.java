/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.repository.rest.PrefsTestEnabledGitBackedRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.rest.resources.AbstractComponentsResource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Working on old test repository")
public class ArtifactTemplateResourceTest {

	@BeforeClass
	public static void init() throws Exception {
		// enable git-backed repository
		new PrefsTestEnabledGitBackedRepository();
	}

	@Before
	public void setRevision() throws Exception {
		((GitBasedRepository) RepositoryFactory.getRepository()).setRevisionTo("97fa997b92965d8bc84e86274b0203f1db7495c5");
	}

	@Test
	public void countMatches() {
		ArtifactTemplateId id = new ArtifactTemplateId("http%3A%2F%2Fdocs.oasis-open.org%2Ftosca%2Fns%2F2011%2F12%2FToscaSpecificTypes", "at-0cd9ab5d-6c2e-4fc2-9cb0-3fee1e431f9f", true);
		ArtifactTemplateResource res = (ArtifactTemplateResource) AbstractComponentsResource.getComponentInstaceResource(id);
		Assert.assertEquals(1, res.getReferenceCount());
	}
}
