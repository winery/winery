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
package org.eclipse.winery.repository.rest;

import org.junit.Assert;
import org.junit.Test;

public class RestUtilsTest {

	@Test
	public void testCreateID() {
		Assert.assertEquals("Frank_s_test", RestUtils.createXMLid("Frank's test").getDecoded());
		Assert.assertEquals("MyNodeType", RestUtils.createXMLid("MyNodeType").getDecoded());
		Assert.assertEquals("A_Node_Type", RestUtils.createXMLid("A Node Type").getDecoded());
	}

}
