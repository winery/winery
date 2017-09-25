/**
 * Copyright (c) 2017 University of Stuttgart. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 and the Apache License 2.0 which both accompany this
 * distribution, and are available at http://www.eclipse.org/legal/epl-v20.html and
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *  - Lukas Harzenetter - initial API and implementation
 *  - Oliver Kopp - test for namespaceWithSpaceAtEndWorks
 */
package org.eclipse.winery.repository.backend.filebased;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateSourceDirectoryId;

import org.junit.Assert;
import org.junit.Test;

public class FilebasedRepositoryTest extends TestWithGitBackedRepository {

	@Test
	public void getAllDefinitionsChildIds() throws Exception {
		this.setRevisionTo("5fdcffa9ccd17743d5498cab0914081fc33606e9");
		SortedSet<XSDImportId> allImports = this.repository.getAllDefinitionsChildIds(XSDImportId.class);
		Assert.assertEquals(1, allImports.size());
	}

	@Test
	public void namespaceWithSpaceAtEndWorks() throws Exception {
		this.setRevisionTo("5fdcffa9ccd17743d5498cab0914081fc33606e9");
		NodeTypeId id = new NodeTypeId("http://www.example.org ", "id", false);
		Assert.assertFalse(this.repository.exists(id));
		this.repository.flagAsExisting(id);
		Assert.assertTrue(this.repository.exists(id));
		Assert.assertNotNull(this.repository.getElement(id));
	}

	@Test
	public void referenceCountIsOneForBaobab() throws Exception {
		this.setRevisionTo("5b7f106ab79a9ba137ece9167a79753dfc64ac84");
		final ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://winery.opentosca.org/test/artifacttemplates/fruits", "baobab_bananaInterface_IA", false);
		Assert.assertEquals(1, this.repository.getReferenceCount(artifactTemplateId));
	}

	@Test
	public void subDirectoryExpandedCorrectly() throws Exception {
		ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://www.example.org", "at", false);
		ArtifactTemplateSourceDirectoryId artifactTemplateSourceDirectoryId = new ArtifactTemplateSourceDirectoryId(artifactTemplateId);
		final Path subDirectories = Paths.get("dir1", "dir2", "dir3");
		RepositoryFileReference ref = new RepositoryFileReference(artifactTemplateSourceDirectoryId, subDirectories, "test.txt");
		final FilebasedRepository repository = (FilebasedRepository) this.repository;

		final Path expected = Paths.get("artifacttemplates", "http%3A%2F%2Fwww.example.org", "at", "source", "dir1", "dir2", "dir3", "test.txt");
		Assert.assertEquals(expected, repository.getRepositoryRoot().relativize(repository.ref2AbsolutePath(ref)));
	}

	@Test
	public void allFilesCorrectlyImported() throws Exception {
		this.setRevisionTo("5b7f106ab79a9ba137ece9167a79753dfc64ac84");
		ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://www.example.org", "at", false);
		ArtifactTemplateSourceDirectoryId artifactTemplateSourceDirectoryId = new ArtifactTemplateSourceDirectoryId(artifactTemplateId);

		final Path subDirectories = Paths.get("dir1", "dir2", "dir3");
		Path workingDir = Files.createTempDirectory("winery");
		final Path subDir = workingDir.resolve(subDirectories);
		Files.createDirectories(subDir);
		Path tempFile = subDir.resolve("test.txt");
		Files.createFile(tempFile);

		BackendUtils.importDirectory(workingDir, this.repository, artifactTemplateSourceDirectoryId);

		RepositoryFileReference ref = new RepositoryFileReference(artifactTemplateSourceDirectoryId, subDirectories, "test.txt");
		Assert.assertTrue(repository.exists(ref));
	}

	@Test
	public void containedFilesRecursesIntoSubDirectories() throws Exception {
		this.setRevisionTo("5cda0035a773a9c405a70759731be3977f37e3f3");
		ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://winery.opentosca.org/test/artifacttemplates/fruits", "baobab-ArtifactTemplate-Peel", false);
		ArtifactTemplateFilesDirectoryId directoryId = new ArtifactTemplateFilesDirectoryId(artifactTemplateId);

		final SortedSet<RepositoryFileReference> containedFiles = repository.getContainedFiles(directoryId);

		// TODO: real content (relative paths, ...) not checked
		Assert.assertEquals(3, containedFiles.size());
	}

	@Test
	public void getTypeForTemplateReturnsCorrectTypeForMyTinyTestArtifactTemplate() throws Exception {
		this.setRevisionTo("1374c8c13ec64899360511dbe0414223b88d3b01");
		ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://opentosca.org/artifacttemplates", "MyTinyTest", false);
		final TArtifactTemplate artifactTemplate = this.repository.getElement(artifactTemplateId);
		final TEntityType typeForTemplate = this.repository.getTypeForTemplate(artifactTemplate);
		Assert.assertEquals(new QName("http://winery.opentosca.org/test/artifacttypes", "MiniArtifactType"), new QName(typeForTemplate.getTargetNamespace(), typeForTemplate.getName()));
	}

}
