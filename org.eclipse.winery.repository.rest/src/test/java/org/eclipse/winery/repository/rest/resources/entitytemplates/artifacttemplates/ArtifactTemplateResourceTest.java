/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 * 	   Oliver Kopp - initial API and implementation
 *     Philipp Meyer - support for source directory
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.repository.rest.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ArtifactTemplateResourceTest extends AbstractResourceTest {

	@Test
	@Ignore
	public void countMatches() {
		ArtifactTemplateId id = new ArtifactTemplateId("http%3A%2F%2Fdocs.oasis-open.org%2Ftosca%2Fns%2F2011%2F12%2FToscaSpecificTypes", "at-0cd9ab5d-6c2e-4fc2-9cb0-3fee1e431f9f", true);
		ArtifactTemplateResource res = (ArtifactTemplateResource) AbstractComponentsResource.getComponentInstaceResource(id);
		Assert.assertEquals(1, res.getReferenceCount());
	}

	@Test
	public void getSourceZip() throws Exception {
		this.setRevisionTo("88e5ccd6c35aeffdebc19c8dda9cd76f432538f8");
		this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/source/zip", "entitytemplates/artifacttemplates/MyTinyTest_src.zip");
	}
}
