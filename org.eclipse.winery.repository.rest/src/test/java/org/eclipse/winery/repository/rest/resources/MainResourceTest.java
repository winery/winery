/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources;

import java.nio.file.Path;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Ignore;
import org.junit.Test;

public class MainResourceTest extends AbstractResourceTest {

	protected void assertPostWithOverwrite(String restUrl, Path file, boolean overwrite) {
		start()
			.multiPart(file.toFile())
			.formParam("overwrite", overwrite)
			.post(callURL(restUrl))
			.then()
			.statusCode(201);
	}

	@Test
	@Ignore("The NamespaceManager does not reload Namespaces.properties upon each change. Thus, this tests fails under certain conditions -- winery-defs-for_servicetemplates-ImportCsarWithOverwriteTest vs. winery-defs-for_servicetemplates1-ImportCsarWithOverwriteTest")
	public void importCSARTestWithOverwrite() throws Exception {
		setRevisionTo("dc30db8f6086a8bcf6b39881d124f15fb05168f4");
		this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ImportCsarWithOverwriteTest/", "entitytypes/servicetemplates/importCsarWithOverwriteTest_initial.json");
		final Path path = MavenTestingUtils.getProjectFilePath("src/test/resources/entitytypes/servicetemplates/ImportCsarOverwriteTest.csar");
		this.assertPostWithOverwrite("", path, true);
		this.assertGet("servicetemplates/http%253A%252F%252Fplain.winery.opentosca.org%252Fservicetemplates/ImportCsarWithOverwriteTest/", "entitytypes/servicetemplates/importCsarWithOverwriteTest_afterOverwrite.json");
	}
}
