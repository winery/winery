/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Karoline Saatkamp - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.backend;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateSourceDirectoryId;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.w3c.dom.Element;
import org.xmlunit.matchers.CompareMatcher;

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
		Assert.assertEquals(entityTemplates, entityTemplatesClone);
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
		Assert.assertEquals(artifactTemplateSourceDirectoryId, repositoryFileReference.getParent());
		Assert.assertEquals(Optional.empty(), repositoryFileReference.getSubDirectory());
		Assert.assertEquals("file.txt", repositoryFileReference.getFileName());
	}

	@Test
	public void repositoryFileReferenceWithSubdirectoryCorrectlyCreated() {
		ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId("http://www.example.org", "at", false);
		ArtifactTemplateSourceDirectoryId artifactTemplateSourceDirectoryId = new ArtifactTemplateSourceDirectoryId(artifactTemplateId);
		final Path subDirectories = Paths.get("d1", "d2");

		final RepositoryFileReference repositoryFileReference = BackendUtils.getRepositoryFileReference(Paths.get("main"), Paths.get("main", "d1", "d2", "file.txt"), artifactTemplateSourceDirectoryId);
		Assert.assertEquals(artifactTemplateSourceDirectoryId, repositoryFileReference.getParent());
		Assert.assertEquals(Optional.of(subDirectories), repositoryFileReference.getSubDirectory());
		Assert.assertEquals("file.txt", repositoryFileReference.getFileName());
	}

}
