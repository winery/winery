/********************************************************************************
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
 ********************************************************************************/
package org.eclipse.winery.repository.backend;

import java.util.LinkedHashMap;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.Assert;
import org.junit.Test;

public class BackendUtilsTestWithGitBackedRepository extends TestWithGitBackedRepository {

	@Test
	public void initializePropertiesGeneratesCorrectKvProperties() throws Exception {
		this.setRevisionTo("origin/plain");
		
		PolicyTemplateId policyTemplateId = new PolicyTemplateId("http://www.example.org", "policytemplate", false);

		// create prepared policy template
		final IRepository repository = RepositoryFactory.getRepository();
		final Definitions definitions = BackendUtils.createWrapperDefinitionsAndInitialEmptyElement(repository, policyTemplateId);
		final TPolicyTemplate policyTemplate = (TPolicyTemplate) definitions.getElement();
		QName policyTypeQName = new QName("http://plain.winery.opentosca.org/policytypes", "PolicyTypeWithTwoKvProperties");
		policyTemplate.setType(policyTypeQName);

		BackendUtils.initializeProperties(repository, policyTemplate);

		Assert.assertNotNull(policyTemplate.getProperties());

		LinkedHashMap<String,String> kvProperties = policyTemplate.getProperties().getKVProperties();
		LinkedHashMap<String,String> expectedPropertyKVS = new LinkedHashMap<>();
		expectedPropertyKVS.put("key1", "");
		expectedPropertyKVS.put("key2", "");
		Assert.assertEquals(expectedPropertyKVS, kvProperties);
	}


	@Test
	public void initializePropertiesDoesNothingInTheCaseOfXmlElemenetPropperties() throws Exception {
		this.setRevisionTo("origin/plain");
		
		PolicyTemplateId policyTemplateId = new PolicyTemplateId("http://www.example.org", "policytemplate", false);

		// create prepared policy template
		final IRepository repository = RepositoryFactory.getRepository();
		final Definitions definitions = BackendUtils.createWrapperDefinitionsAndInitialEmptyElement(repository, policyTemplateId);
		final TPolicyTemplate policyTemplate = (TPolicyTemplate) definitions.getElement();
		QName policyTypeQName = new QName("http://plain.winery.opentosca.org/policytypes", "PolicyTypeWithXmlElementProperty");
		policyTemplate.setType(policyTypeQName);

		BackendUtils.initializeProperties(repository, policyTemplate);

		Assert.assertNull(policyTemplate.getProperties());
	}


}
