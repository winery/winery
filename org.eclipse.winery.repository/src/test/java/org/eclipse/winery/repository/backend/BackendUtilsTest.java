/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateSourceDirectoryId;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.xmlunit.matchers.CompareMatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BackendUtilsTest {

    @Test
    public void testClone() throws Exception {
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();

        TNodeTemplate nt1 = new TNodeTemplate();
        TNodeTemplate nt2 = new TNodeTemplate();
        TNodeTemplate nt3 = new TNodeTemplate();
        nt1.setId("NT1");
        nt2.setId("NT2");
        nt3.setId("NT3");
        List<TEntityTemplate> entityTemplates = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
        entityTemplates.add(nt1);
        entityTemplates.add(nt2);
        entityTemplates.add(nt3);

        TTopologyTemplate clone = BackendUtils.clone(topologyTemplate);
        List<TEntityTemplate> entityTemplatesClone = clone.getNodeTemplateOrRelationshipTemplate();
        assertEquals(entityTemplates, entityTemplatesClone);
    }

    @Test
    public void relationshipTemplateIsSerializedAsRefInXml() throws Exception {
        TTopologyTemplate minimalTopologyTemplate = new TTopologyTemplate();

        TNodeTemplate nt1 = new TNodeTemplate("nt1");
        minimalTopologyTemplate.addNodeTemplate(nt1);

        TNodeTemplate nt2 = new TNodeTemplate("nt2");
        minimalTopologyTemplate.addNodeTemplate(nt2);

        TRelationshipTemplate rt = new TRelationshipTemplate("rt");
        minimalTopologyTemplate.addRelationshipTemplate(rt);
        rt.setSourceNodeTemplate(nt1);
        rt.setTargetNodeTemplate(nt2);

        String minimalTopologyTemplateAsXmlString = "<TopologyTemplate xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ns3=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:ns4=\"http://test.winery.opentosca.org\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">\n" +
            "  <NodeTemplate id=\"nt1\"/>\n" +
            "  <NodeTemplate id=\"nt2\"/>\n" +
            "  <RelationshipTemplate id=\"rt\">\n" +
            "    <SourceElement ref=\"nt1\"/>\n" +
            "    <TargetElement ref=\"nt2\"/>\n" +
            "  </RelationshipTemplate>\n" +
            "</TopologyTemplate>";

        org.hamcrest.MatcherAssert.assertThat(BackendUtils.getXMLAsString(minimalTopologyTemplate), CompareMatcher.isIdenticalTo(minimalTopologyTemplateAsXmlString).ignoreWhitespace());
    }

    @Test
    public void relationshipTemplateIsSerializedAsRefInJson() throws Exception {
        TTopologyTemplate minimalTopologyTemplate = new TTopologyTemplate();

        TNodeTemplate nt1 = new TNodeTemplate("nt1");
        minimalTopologyTemplate.addNodeTemplate(nt1);

        TNodeTemplate nt2 = new TNodeTemplate("nt2");
        minimalTopologyTemplate.addNodeTemplate(nt2);

        TRelationshipTemplate rt = new TRelationshipTemplate("rt");
        minimalTopologyTemplate.addRelationshipTemplate(rt);
        rt.setSourceNodeTemplate(nt1);
        rt.setTargetNodeTemplate(nt2);

        String minimalTopologyTemplateAsJsonString = "{\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"nodeTemplates\":[{\"id\":\"nt1\",\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"minInstances\":1,\"maxInstances\":\"1\"},{\"id\":\"nt2\",\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"minInstances\":1,\"maxInstances\":\"1\"}],\"relationshipTemplates\":[{\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"id\":\"rt\",\"sourceElement\":{\"ref\":\"nt1\"},\"targetElement\":{\"ref\":\"nt2\"}}]}";

        JSONAssert.assertEquals(
            minimalTopologyTemplateAsJsonString,
            BackendUtils.Object2JSON(minimalTopologyTemplate),
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

}
