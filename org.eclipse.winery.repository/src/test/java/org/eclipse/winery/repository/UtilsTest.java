/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class UtilsTest {

	private TTopologyTemplate minimalTopologyTemplate;
	private String minimalTopologyTemplateAsJsonString;
	private String minimalTopologyTemplateAsXmlString;

	@Before
	public void initialize() {
		minimalTopologyTemplate = new TTopologyTemplate();

		TNodeTemplate nt1 = new TNodeTemplate("nt1");
		minimalTopologyTemplate.addNodeTemplate(nt1);

		TNodeTemplate nt2 = new TNodeTemplate("nt2");
		minimalTopologyTemplate.addNodeTemplate(nt2);

		TRelationshipTemplate rt = new TRelationshipTemplate("rt");
		minimalTopologyTemplate.addRelationshipTemplate(rt);
		rt.setSourceNodeTemplate(nt1);
		rt.setTargetNodeTemplate(nt2);

		minimalTopologyTemplateAsJsonString = "{\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"nodeTemplates\":[{\"id\":\"nt1\",\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"minInstances\":1,\"maxInstances\":\"1\"},{\"id\":\"nt2\",\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"minInstances\":1,\"maxInstances\":\"1\"}],\"relationshipTemplates\":[{\"documentation\":[],\"any\":[],\"otherAttributes\":{},\"id\":\"rt\",\"sourceElement\":{\"ref\":\"nt1\"},\"targetElement\":{\"ref\":\"nt2\"}}]}";

		minimalTopologyTemplateAsXmlString = "<tosca:TopologyTemplate xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\" xmlns:ns1=\"http://www.eclipse.org/winery/model/selfservice\">\n" +
				"    <tosca:NodeTemplate id=\"nt1\"/>\n" +
				"    <tosca:NodeTemplate id=\"nt2\"/>\n" +
				"    <tosca:RelationshipTemplate id=\"rt\">\n" +
				"        <tosca:SourceElement ref=\"nt1\"/>\n" +
				"        <tosca:TargetElement ref=\"nt2\"/>\n" +
				"    </tosca:RelationshipTemplate>\n" +
				"</tosca:TopologyTemplate>";
	}

	@Test
	public void testCreateID() {
		Assert.assertEquals("Frank_s_test", Utils.createXMLid("Frank's test").getDecoded());
		Assert.assertEquals("MyNodeType", Utils.createXMLid("MyNodeType").getDecoded());
		Assert.assertEquals("A_Node_Type", Utils.createXMLid("A Node Type").getDecoded());
	}

	@Test
	public void relationshipTemplateIsSerializedAsRefInJson() throws Exception {
		JSONAssert.assertEquals(
				minimalTopologyTemplateAsJsonString,
				Utils.Object2JSON(minimalTopologyTemplate),
				true);
	}

	@Test
	public void relationshipTemplateIsSerializedAsRefInXml() throws Exception {
		// Initialize preferences
		// We do not directly need the object, but constructing them has the side effect that Repository.INSTANCE is != null
		// The initialized Repository.INSTANCE is required for the namespaces prefixes
		new PrefsTestEnabledGitBackedRepository();
		((GitBasedRepository) Repository.INSTANCE).setRevisionTo("8cf0ce80c2c40c6ec178ef8e5bdc4e2fcdecc7f9");

		Assert.assertEquals(minimalTopologyTemplateAsXmlString, Utils.getXMLAsString(minimalTopologyTemplate));
	}
}
