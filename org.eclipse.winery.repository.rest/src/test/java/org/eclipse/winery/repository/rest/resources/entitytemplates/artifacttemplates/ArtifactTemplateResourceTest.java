/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates;

import java.nio.file.Path;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Ignore;
import org.junit.Test;

public class ArtifactTemplateResourceTest extends AbstractResourceTest {

	@Test
	public void getFilesZip() throws Exception {
		this.setRevisionTo("88e5ccd6c35aeffdebc19c8dda9cd76f432538f8");
		this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/files/zip", "entitytemplates/artifacttemplates/MyTinyTest_src.zip");
	}

	@Test
	public void getSourceZip() throws Exception {
		this.setRevisionTo("88e5ccd6c35aeffdebc19c8dda9cd76f432538f8");
		this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/source/zip", "entitytemplates/artifacttemplates/MyTinyTest_src.zip");
	}

	@Test
	@Ignore("Ignored, because git-lfs is not mandatory for Winery to work properly")
	public void lfsTest() throws Exception {
		this.setRevisionTo("6ca5993d6a9abd255fb28f70c4ea73b189a47a57");
		// in case git-lsf is not available this test fails
		// NOT because the .iso file contained in source is not 2 bytes as expected,
		// BUT git-lfs is not available in the path
		this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest/source/", "entitytemplates/artifacttemplates/largeSource.json");
	}

	@Test
	public void artifactTemplateContainsFileReferenceInJson() throws Exception {
		this.setRevisionTo("6aabc1c52ad74ab2692e7d59dbe22a263667e2c9");
		this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest", "entitytemplates/artifacttemplates/MyTinyTest.json");
	}

	@Test
	public void artifactTemplateContainsFileReferenceInXml() throws Exception {
		this.setRevisionTo("6aabc1c52ad74ab2692e7d59dbe22a263667e2c9");
		this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyTest", "entitytemplates/artifacttemplates/MyTinyTest.xml");
	}

	@Test
	public void artifactTemplateContainsUpdatedFileReferenceInJson() throws Exception {
		this.setRevisionTo("15cd64e30770ca7986660a34e1a4a7e0cf332f19");
		this.assertNotFound("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson");
		this.assertPost("artifacttemplates/", "entitytemplates/artifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson-create.json");
		this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson", "entitytemplates/artifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson-withoutFile.json");

		// post an arbitrary file
		final Path path = MavenTestingUtils.getProjectFilePath("src/test/resources/entitytemplates/artifacttemplates/empty_text_file.txt");
		this.assertPost("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson/files/", path);

		this.assertGet("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson", "entitytemplates/artifacttemplates/artifactTemplateContainsUpdatedFileReferenceInJson-withFile.json");
	}
}
