/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Karoline Saatkamp - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.backend;

import java.util.List;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
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

		String minimalTopologyTemplateAsXmlString = "<tosca:TopologyTemplate xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\" xmlns:ns1=\"http://www.eclipse.org/winery/model/selfservice\">\n" +
			"    <tosca:NodeTemplate id=\"nt1\"/>\n" +
			"    <tosca:NodeTemplate id=\"nt2\"/>\n" +
			"    <tosca:RelationshipTemplate id=\"rt\">\n" +
			"        <tosca:SourceElement ref=\"nt1\"/>\n" +
			"        <tosca:TargetElement ref=\"nt2\"/>\n" +
			"    </tosca:RelationshipTemplate>\n" +
			"</tosca:TopologyTemplate>";

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


}
