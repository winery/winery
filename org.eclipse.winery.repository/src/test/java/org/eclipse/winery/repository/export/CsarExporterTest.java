/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.winery.repository.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.junit.Assert;
import org.junit.Test;

public class CsarExporterTest extends TestWithGitBackedRepository {

	public ByteArrayInputStream createOutputAndInputStream(String commitId, DefinitionsChildId id) throws Exception {
		setRevisionTo(commitId);
		CsarExporter exporter = new CsarExporter();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		exporter.writeCsar(RepositoryFactory.getRepository(), id, os);
		return new ByteArrayInputStream(os.toByteArray());
	}

	@Test
	public void csarIsValidZipForArtifactTemplateWithFilesAndSources() throws Exception {
		try (InputStream inputStream = this.createOutputAndInputStream("origin/plain", new ArtifactTemplateId("http://plain.winery.opentosca.org/artifacttemplates", "ArtifactTemplateWithFilesAndSources-ArtifactTypeWithoutProperties", false)); ZipInputStream zis = new ZipInputStream(inputStream)) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				String name = entry.getName();
				Assert.assertNotNull(name);
				Assert.assertFalse("name contains backslashes", name.contains("\\"));
			}
		}
	}
}
