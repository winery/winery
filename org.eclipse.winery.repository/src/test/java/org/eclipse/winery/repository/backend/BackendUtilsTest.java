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

package org.eclipse.winery.repository.backend;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateSourceDirectoryId;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.xmlunit.matchers.CompareMatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BackendUtilsTest {

    @Test
    public void testClone() throws Exception {

        TNodeTemplate nt1 = new TNodeTemplate();
        TNodeTemplate nt2 = new TNodeTemplate();
        TNodeTemplate nt3 = new TNodeTemplate();
        nt1.setId("NT1");
        nt2.setId("NT2");
        nt3.setId("NT3");

        TTopologyTemplate topologyTemplate = new TTopologyTemplate.Builder()
            .addNodeTemplates(nt1)
            .addNodeTemplates(nt2)
            .addNodeTemplates(nt3)
            .build();
        TTopologyTemplate clone = BackendUtils.clone(topologyTemplate);

        List<TEntityTemplate> entityTemplates = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
        List<TEntityTemplate> entityTemplatesClone = clone.getNodeTemplateOrRelationshipTemplate();
        assertEquals(entityTemplates, entityTemplatesClone);
    }

    @Test
    public void relationshipTemplateIsSerializedAsRefInXml() throws Exception {
        TTopologyTemplate.Builder minimalTopologyTemplate = new TTopologyTemplate.Builder();

        TNodeTemplate nt1 = new TNodeTemplate("nt1");
        minimalTopologyTemplate.addNodeTemplates(nt1);

        TNodeTemplate nt2 = new TNodeTemplate("nt2");
        minimalTopologyTemplate.addNodeTemplates(nt2);

        TRelationshipTemplate rt = new TRelationshipTemplate("rt");
        minimalTopologyTemplate.addRelationshipTemplate(rt);
        rt.setSourceNodeTemplate(nt1);
        rt.setTargetNodeTemplate(nt2);

        String minimalTopologyTemplateAsXmlString = "" +
            "<TopologyTemplate xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\" " +
            "                  xmlns:ns3=\"http://www.eclipse.org/winery/model/selfservice\" " +
            "                  xmlns:ns4=\"http://test.winery.opentosca.org\" " +
            "                  xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">\n" +
            "  <NodeTemplate id=\"nt1\"/>\n" +
            "  <NodeTemplate id=\"nt2\"/>\n" +
            "  <RelationshipTemplate id=\"rt\">\n" +
            "    <SourceElement ref=\"nt1\"/>\n" +
            "    <TargetElement ref=\"nt2\"/>\n" +
            "  </RelationshipTemplate>\n" +
            "</TopologyTemplate>";

        // FIXME deal with the missing repository here
//        org.hamcrest.MatcherAssert.assertThat(BackendUtils.getXMLAsString(minimalTopologyTemplate.build(), null), CompareMatcher.isIdenticalTo(minimalTopologyTemplateAsXmlString).ignoreWhitespace());
    }

    @Test
    public void relationshipTemplateIsSerializedAsRefInJson() throws Exception {
        TTopologyTemplate.Builder minimalTopologyTemplate = new TTopologyTemplate.Builder();

        TNodeTemplate nt1 = new TNodeTemplate("nt1");
        minimalTopologyTemplate.addNodeTemplates(nt1);

        TNodeTemplate nt2 = new TNodeTemplate("nt2");
        minimalTopologyTemplate.addNodeTemplates(nt2);

        TRelationshipTemplate rt = new TRelationshipTemplate("rt");
        minimalTopologyTemplate.addRelationshipTemplate(rt);
        rt.setSourceNodeTemplate(nt1);
        rt.setTargetNodeTemplate(nt2);

        String minimalTopologyTemplateAsJsonString = "{\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"nodeTemplates\":[{\"id\":\"nt1\",\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"minInstances\":1,\"maxInstances\":\"1\"},{\"id\":\"nt2\",\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"minInstances\":1,\"maxInstances\":\"1\"}],\"relationshipTemplates\":[{\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"id\":\"rt\",\"sourceElement\":{\"ref\":\"nt1\"},\"targetElement\":{\"ref\":\"nt2\"}}]}";

        JSONAssert.assertEquals(
            minimalTopologyTemplateAsJsonString,
            JacksonProvider.mapper.writeValueAsString(minimalTopologyTemplate.build()),
            true);
    }

    @Test
    public void repositoryFileReferenceWithoutSubdirectoryCorrectlyCreated() {
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://www.example.org", "at", false);
        ArtifactTemplateSourceDirectoryId artifactTemplateSourceDirectoryId = new ArtifactTemplateSourceDirectoryId(artifactTemplateId);

        final RepositoryFileReference repositoryFileReference = BackendUtils.getRepositoryFileReference(Paths.get("main"), Paths.get("main", "file.txt"), artifactTemplateSourceDirectoryId);
        assertEquals(artifactTemplateSourceDirectoryId, repositoryFileReference.getParent());
        assertEquals(Optional.empty(), repositoryFileReference.getSubDirectory());
        assertEquals("file.txt", repositoryFileReference.getFileName());
    }

    @Test
    public void repositoryFileReferenceWithSubdirectoryCorrectlyCreated() {
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://www.example.org", "at", false);
        ArtifactTemplateSourceDirectoryId artifactTemplateSourceDirectoryId = new ArtifactTemplateSourceDirectoryId(artifactTemplateId);
        final Path subDirectories = Paths.get("d1", "d2");

        final RepositoryFileReference repositoryFileReference = BackendUtils.getRepositoryFileReference(Paths.get("main"), Paths.get("main", "d1", "d2", "file.txt"), artifactTemplateSourceDirectoryId);
        assertEquals(artifactTemplateSourceDirectoryId, repositoryFileReference.getParent());
        assertEquals(Optional.of(subDirectories), repositoryFileReference.getSubDirectory());
        assertEquals("file.txt", repositoryFileReference.getFileName());
    }

    public TArtifactTemplate createArtifactTemplateWithSingleReferenceToAnUrl() {
        // create artifact template with a single contained reference (an absolute URL)
        TArtifactTemplate artifactTemplate = new TArtifactTemplate();
        TArtifactTemplate.ArtifactReferences artifactReferences = new TArtifactTemplate.ArtifactReferences();
        artifactTemplate.setArtifactReferences(artifactReferences);
        List<TArtifactReference> artRefList = artifactReferences.getArtifactReference();
        TArtifactReference artRef = new TArtifactReference();
        artRef.setReference("http://www.example.org/absolute-url");
        artRefList.add(artRef);

        return artifactTemplate;
    }

    public TArtifactTemplate createArtifactTemplateWithReferenceToAnUrlAndANonExistentFile() {
        // create artifact template with a single contained reference (an absolute URL)
        TArtifactTemplate artifactTemplate = new TArtifactTemplate();
        TArtifactTemplate.ArtifactReferences artifactReferences = new TArtifactTemplate.ArtifactReferences();
        artifactTemplate.setArtifactReferences(artifactReferences);
        List<TArtifactReference> artRefList = artifactReferences.getArtifactReference();

        TArtifactReference artRef = new TArtifactReference();
        artRef.setReference("http://www.example.org/absolute-url");
        artRefList.add(artRef);

        artRef = new TArtifactReference();
        artRef.setReference("does-not-exist.txt");
        artRefList.add(artRef);

        return artifactTemplate;
    }

    public TArtifactTemplate createArtifactTemplateWithReferenceToAnUrlAndExistentFile() {
        // create artifact template with a single contained reference (an absolute URL)
        TArtifactTemplate artifactTemplate = new TArtifactTemplate();
        TArtifactTemplate.ArtifactReferences artifactReferences = new TArtifactTemplate.ArtifactReferences();
        artifactTemplate.setArtifactReferences(artifactReferences);
        List<TArtifactReference> artRefList = artifactReferences.getArtifactReference();

        TArtifactReference artRef = new TArtifactReference();
        artRef.setReference("http://www.example.org/absolute-url");
        artRefList.add(artRef);

        artRef = new TArtifactReference();
        artRef.setReference("artifacttemplates/http%253A%252F%252Fexample.org/test-artifact-template/exists.txt");
        artRefList.add(artRef);

        return artifactTemplate;
    }

    @Test
    public void synchronizeReferencesDoesNotRemoveUrls() throws Exception {
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://example.org", "test-artifact-template", false);

        // alternative test implementation: Use git-based repository
        // this test at hand is closer to the implementation, but easier to write

        IRepository repository = mock(IRepository.class);
        ArtifactTemplateFilesDirectoryId artifactTemplateFilesDirectoryId = new ArtifactTemplateFilesDirectoryId(artifactTemplateId);
        when(repository.getContainedFiles(artifactTemplateFilesDirectoryId)).thenReturn(Collections.emptySortedSet());

        TArtifactTemplate artifactTemplate = createArtifactTemplateWithSingleReferenceToAnUrl();
        when(repository.getElement(artifactTemplateId)).thenReturn(artifactTemplate);
        TArtifactTemplate synchronizhedArtifactTemplate = BackendUtils.synchronizeReferences(repository, artifactTemplateId);

        assertEquals(createArtifactTemplateWithSingleReferenceToAnUrl(), synchronizhedArtifactTemplate);
    }

    @Test
    public void synchronizeReferencesRemovesNonExistantFileAndDoesNotRemoveUrls() throws Exception {
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://example.org", "test-artifact-template", false);

        // alternative test implementation: Use git-based repository
        // this test at hand is closer to the implementation, but easier to write

        IRepository repository = mock(IRepository.class);
        ArtifactTemplateFilesDirectoryId artifactTemplateFilesDirectoryId = new ArtifactTemplateFilesDirectoryId(artifactTemplateId);
        when(repository.getContainedFiles(artifactTemplateFilesDirectoryId)).thenReturn(Collections.emptySortedSet());

        TArtifactTemplate artifactTemplate = createArtifactTemplateWithReferenceToAnUrlAndANonExistentFile();
        when(repository.getElement(artifactTemplateId)).thenReturn(artifactTemplate);
        TArtifactTemplate synchronizhedArtifactTemplate = BackendUtils.synchronizeReferences(repository, artifactTemplateId);

        assertEquals(createArtifactTemplateWithSingleReferenceToAnUrl(), synchronizhedArtifactTemplate);
    }

    @Test
    public void synchronizeReferencesDoesNontRemoveExistantFileAndDoesNotRemoveUrls() throws Exception {
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://example.org", "test-artifact-template", false);

        // alternative test implementation: Use git-based repository
        // this test at hand is closer to the implementation, but easier to write

        IRepository repository = mock(IRepository.class);
        ArtifactTemplateFilesDirectoryId artifactTemplateFilesDirectoryId = new ArtifactTemplateFilesDirectoryId(artifactTemplateId);

        SortedSet<RepositoryFileReference> containedReferences = new TreeSet<>();
        RepositoryFileReference repositoryFileReference = new RepositoryFileReference(artifactTemplateId, "exists.txt");
        containedReferences.add(repositoryFileReference);

        when(repository.getContainedFiles(artifactTemplateFilesDirectoryId)).thenReturn(containedReferences);

        TArtifactTemplate artifactTemplate = createArtifactTemplateWithReferenceToAnUrlAndExistentFile();
        when(repository.getElement(artifactTemplateId)).thenReturn(artifactTemplate);
        TArtifactTemplate synchronizhedArtifactTemplate = BackendUtils.synchronizeReferences(repository, artifactTemplateId);

        assertEquals(createArtifactTemplateWithReferenceToAnUrlAndExistentFile(), synchronizhedArtifactTemplate);
    }

    @Test
    public void testUpdateVersionOfNodeTemplate() throws Exception {
        TTopologyTemplate.Builder topologyTemplate = new TTopologyTemplate.Builder();

        TNodeTemplate nt1 = new TNodeTemplate();
        TNodeTemplate nt2 = new TNodeTemplate();
        nt1.setId("java8_1.0-w1-wip1_3");
        nt1.setType(new QName("namespace", "java8_1.0-w1-wip1"));
        nt2.setId("java8_1.0-w2-wip2");
        nt2.setType(new QName("namespace", "java8_1.0-w2-wip2"));

        topologyTemplate.addNodeTemplates(nt1);

        TTopologyTemplate resultTopologyTemplate = BackendUtils.updateVersionOfNodeTemplate(topologyTemplate.build(), "java8_1.0-w1-wip1_3", "{namespace}java8_1.0-w2-wip2");
        List<TEntityTemplate> entityTemplates = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
        List<TEntityTemplate> entityTemplatesClone = resultTopologyTemplate.getNodeTemplateOrRelationshipTemplate();
        assertEquals(entityTemplates.get(0).getTypeAsQName().toString(), "{namespace}java8_1.0-w2-wip2");
    }
}
