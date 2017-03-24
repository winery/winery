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
package org.eclipse.winery.repository.resources;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;

public class TestIds {

	public static final Namespace NS = new Namespace("http://winery.opentosca.org/test/nodetypes", false);

	public static final Namespace NS_TEST_FRUITS = new Namespace("http://winery.opentosca.org/test/nodetypes/fruits", false);

	public static final NodeTypeId ID_FRUIT_BAOBAB = new NodeTypeId(NS_TEST_FRUITS, new XMLId("baobab", false));

}
