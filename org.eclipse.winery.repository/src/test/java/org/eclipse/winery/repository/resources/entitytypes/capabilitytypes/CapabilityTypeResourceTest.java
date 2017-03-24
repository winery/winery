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
package org.eclipse.winery.repository.resources.entitytypes.capabilitytypes;

import java.io.IOException;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.repository.PrefsTestEnabledGitBackedRepository;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.resources.ResourceTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Working on old test repository")
public class CapabilityTypeResourceTest extends ResourceTest {

	private static final CapabilityTypeId id = new CapabilityTypeId(new Namespace("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", false), new XMLId("ContainerCapability", false));


	@BeforeClass
	public static void init() throws Exception {
		// enable git-backed repository
		new PrefsTestEnabledGitBackedRepository();
	}

	@Before
	public void setRevision() throws Exception {
		((GitBasedRepository) Repository.INSTANCE).setRevisionTo("97fa997b92965d8bc84e86274b0203f1db7495c5");
	}

	@Test
	public void getElementAsXMLString() throws IOException {
		// ensure that no test object exists
		Repository.INSTANCE.forceDelete(CapabilityTypeResourceTest.id);

		CapabilityTypeResource res = new CapabilityTypeResource(CapabilityTypeResourceTest.id);
		String s = res.getDefinitionsAsXMLString();
		Assert.assertNotNull(s);
	}
}
