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
package org.eclipse.winery.repository.resources;

import java.io.IOException;

import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.repository.backend.MockXMLElement;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.entitytypes.capabilitytypes.CapabilityTypeResource;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Working on old test repository")
public class AbstractComponentInstanceResourceDefinitionsBackedTest extends ResourceTest {

	private static final CapabilityTypeId id = new CapabilityTypeId(TestIds.NS, new XMLId("testCapabilityType", false));


	@Test
	public void testPlainPersist() throws IOException {
		// ensure that no test object exists
		Repository.INSTANCE.forceDelete(AbstractComponentInstanceResourceDefinitionsBackedTest.id);

		CapabilityTypeResource res = new CapabilityTypeResource(AbstractComponentInstanceResourceDefinitionsBackedTest.id);
		res.persist();
		Assert.assertTrue("Element has to exist", Repository.INSTANCE.exists(AbstractComponentInstanceResourceDefinitionsBackedTest.id));
	}

	@Test
	public void testPersistWithData() throws IOException {
		// ensure that no test object exists
		Repository.INSTANCE.forceDelete(AbstractComponentInstanceResourceDefinitionsBackedTest.id);

		CapabilityTypeResource res = new CapabilityTypeResource(AbstractComponentInstanceResourceDefinitionsBackedTest.id);
		res.getElement().getAny().add(new MockXMLElement());
		res.persist();
		Assert.assertTrue("Element has to exist", Repository.INSTANCE.exists(AbstractComponentInstanceResourceDefinitionsBackedTest.id));

		// reload data
		res = new CapabilityTypeResource(AbstractComponentInstanceResourceDefinitionsBackedTest.id);

		Assert.assertEquals(1, res.getElement().getAny().size());
	}
}
