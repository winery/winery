/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
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

import org.junit.Assert;
import org.junit.Test;

public class JAXBSupportTest {

	@Test
	public void createMarshaller() throws Exception {
		Assert.assertNotNull(JAXBSupport.createMarshaller(true));
	}

	@Test
	public void createUnmarshaller() throws Exception {
		Assert.assertNotNull(JAXBSupport.createUnmarshaller());
	}

	@Test
	public void JAXBContextIsNotNull() {
		Assert.assertNotNull(JAXBSupport.context);
	}

}
