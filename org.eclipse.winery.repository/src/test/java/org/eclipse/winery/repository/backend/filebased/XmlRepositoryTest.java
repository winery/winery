/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.backend.filebased;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.definitions.*;
import org.eclipse.winery.model.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateSourceDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlRepositoryTest extends TestWithGitBackedRepository {

    @Test
    public void getAllDefinitionsChildIds() throws Exception {
        this.setRevisionTo("5fdcffa9ccd17743d5498cab0914081fc33606e9");
        SortedSet<XSDImportId> allImports = this.repository.getAllDefinitionsChildIds(XSDImportId.class);
        assertEquals(1, allImports.size());
    }

    @Test
    public void namespaceWithSpaceAtEndWorks() throws Exception {
        this.setRevisionTo("5fdcffa9ccd17743d5498cab0914081fc33606e9");
        NodeTypeId id = new NodeTypeId("http://www.example.org ", "id", false);
        assertFalse(this.repository.exists(id));
        this.repository.flagAsExisting(id);
        assertTrue(this.repository.exists(id));
        assertNotNull(this.repository.getElement(id));
    }

    @Test
    public void referenceCountIsOneForBaobab() throws Exception {
        this.setRevisionTo("5b7f106ab79a9ba137ece9167a79753dfc64ac84");
        final ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://winery.opentosca.org/test/artifacttemplates/fruits", "baobab_bananaInterface_IA", false);
        assertEquals(1, this.repository.getReferenceCount(artifactTemplateId));
    }

    @Test
    public void subDirectoryExpandedCorrectly() throws Exception {
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://www.example.org", "at", false);
        ArtifactTemplateSourceDirectoryId artifactTemplateSourceDirectoryId = new ArtifactTemplateSourceDirectoryId(artifactTemplateId);
        final Path subDirectories = Paths.get("dir1", "dir2", "dir3");
        RepositoryFileReference ref = new RepositoryFileReference(artifactTemplateSourceDirectoryId, subDirectories, "test.txt");
        final IRepository repository = this.repository;

        final Path expected = Paths.get("artifacttemplates", "http%3A%2F%2Fwww.example.org", "at", "source", "dir1", "dir2", "dir3", "test.txt");
        assertEquals(expected, repository.getRepositoryRoot().relativize(repository.ref2AbsolutePath(ref)));
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
        assertTrue(repository.exists(ref));
    }

    @Test
    public void containedFilesRecursesIntoSubDirectories() throws Exception {
        this.setRevisionTo("5cda0035a773a9c405a70759731be3977f37e3f3");
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://winery.opentosca.org/test/artifacttemplates/fruits", "baobab-ArtifactTemplate-Peel", false);
        ArtifactTemplateFilesDirectoryId directoryId = new ArtifactTemplateFilesDirectoryId(artifactTemplateId);

        final SortedSet<RepositoryFileReference> containedFiles = repository.getContainedFiles(directoryId);

        // TODO: real content (relative paths, ...) not checked
        assertEquals(3, containedFiles.size());
    }

    @Test
    public void getTypeForTemplateReturnsCorrectTypeForMyTinyTestArtifactTemplate() throws Exception {
        this.setRevisionTo("1374c8c13ec64899360511dbe0414223b88d3b01");
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://opentosca.org/artifacttemplates", "MyTinyTest", false);
        final TArtifactTemplate artifactTemplate = this.repository.getElement(artifactTemplateId);
        final TEntityType typeForTemplate = this.repository.getTypeForTemplate(artifactTemplate);
        assertEquals(new QName("http://winery.opentosca.org/test/artifacttypes", "MiniArtifactType"), new QName(typeForTemplate.getTargetNamespace(), typeForTemplate.getName()));
    }

    @Test
    public void getContainedFilesProducedCorrectPath() throws Exception {
        ArtifactTemplateId artifactTemplateWithFilesAndSourcesId = new ArtifactTemplateId("http://plain.winery.opentosca.org/artifacttemplates", "ArtifactTemplateWithFilesAndSources", false);
        DirectoryId fileDir = new ArtifactTemplateFilesDirectoryId(artifactTemplateWithFilesAndSourcesId);
        SortedSet<RepositoryFileReference> files = repository.getContainedFiles(fileDir);
        for (RepositoryFileReference ref : files) {
            assertFalse(ref.getSubDirectory().isPresent() && ref.getSubDirectory().get().toString().equals(""), "File " + ref.toString() + " contains empty sub directory");
        }
    }

    @Test
    public void getStableDefinitionsOnly() throws Exception {
        this.setRevisionTo("e9e8443dfce1ccb0eee3a0b937b1c2e6ab7798df");
        SortedSet<NodeTypeId> stableNodeTypes = this.repository.getStableDefinitionsChildIdsOnly(NodeTypeId.class);
        assertEquals(10, stableNodeTypes.size());
    }

    @Test
    public void getAllDefinitions() throws Exception {
        this.setRevisionTo("e9e8443dfce1ccb0eee3a0b937b1c2e6ab7798df");
        SortedSet<NodeTypeId> allNodeTypes = this.repository.getAllDefinitionsChildIds(NodeTypeId.class);
        assertEquals(13, allNodeTypes.size());
    }

    @Test
    public void duplicateDefinition() throws Exception {
        this.setRevisionTo("origin/plain");
        DefinitionsChildId old = new NodeTypeId("http://plain.winery.opentosca.org/nodetypes", "NodeTypeWithTwoKVProperties", false);
        DefinitionsChildId newId = new NodeTypeId("http://plain.winery.opentosca.org/nodetypes", "NodeTypeWithTwoKVPropertiesDuplicate", false);

        this.repository.duplicate(old, newId);

        assertTrue(this.repository.exists(old));
        assertTrue(this.repository.exists(newId));
    }

    @Test
    public void getAllElementsReferencingANodeType() throws Exception {
        // explicitly checkout commit-id to ensure that no other future elements are referencing the specified component
        this.setRevisionTo("9799e0677c41e9eeceaab01a3baff33e5a20cdaa");
        DefinitionsChildId element = new NodeTypeId("http://plain.winery.opentosca.org/nodetypes", "NodeTypeWithXmlElementProperty", false);

        Collection<DefinitionsChildId> childIds = this.repository.getReferencingDefinitionsChildIds(element);

        assertEquals(1, childIds.size());
    }

    @Test
    public void getAllElementsReferencingAnArtifactType() throws Exception {
        this.setRevisionTo("468a7f4c58424295d07f9aa4ebecbbaa2beb37c2");
        DefinitionsChildId id = new ArtifactTypeId("http://plain.winery.opentosca.org/artifacttypes", "ArtifactTypeWithoutProperties", false);

        Collection<DefinitionsChildId> childIds = this.repository.getReferencingDefinitionsChildIds(id);

        assertEquals(4, childIds.size());
    }

    @Test
    public void getAllElementsReferencingAnRequirementType() throws Exception {
        this.setRevisionTo("468a7f4c58424295d07f9aa4ebecbbaa2beb37c2");
        DefinitionsChildId id = new RequirementTypeId("http://plain.winery.opentosca.org/requirementtypes", "RequirementTypeWithoutProperties", false);

        Collection<DefinitionsChildId> childIds = this.repository.getReferencingDefinitionsChildIds(id);

        assertEquals(2, childIds.size());
    }

    @Test
    public void getAllElementsReferencingAnCapabilityType() throws Exception {
        this.setRevisionTo("468a7f4c58424295d07f9aa4ebecbbaa2beb37c2");
        DefinitionsChildId id = new CapabilityTypeId("http://plain.winery.opentosca.org/capabilitytypes", "CapabilityTypeWithoutProperties", false);

        Collection<DefinitionsChildId> childIds = this.repository.getReferencingDefinitionsChildIds(id);

        assertEquals(2, childIds.size());
    }
}
