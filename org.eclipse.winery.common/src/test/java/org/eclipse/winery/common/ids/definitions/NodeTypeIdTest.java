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
package org.eclipse.winery.common.ids.definitions;

import org.junit.Assert;
import org.junit.Test;

public class NodeTypeIdTest {

	@Test
	public void equalityOfNodeTypeIds() {
		NodeTypeId id1 = new NodeTypeId("ns1", "id1", false);
		NodeTypeId id2 = new NodeTypeId("ns1", "id1", false);
		Assert.assertEquals(id1, id2);
	}

	@Test
	public void twoDifferentIdTypesAreNotEqual() {
		NodeTypeId id1 = new NodeTypeId("ns1", "id1", false);
		RelationshipTypeId id2 = new RelationshipTypeId("ns1", "id1", false);
		Assert.assertNotEquals(id1, id2);
	}

}
