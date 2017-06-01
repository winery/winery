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
package org.eclipse.winery.repository.resources.servicetemplates.topologytemplates;

import org.eclipse.winery.repository.resources.AbstractResourceTest;

import org.junit.Test;

public class TopologyTemplateResourceTest  extends AbstractResourceTest {

	@Test
	public void getComponentInstanceJSON() throws Exception {
		this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/baobab_topologytemplate.json");
	}

	@Test
	public void getComponentInstanceXML() throws Exception {
		this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
		this.assertGet("servicetemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fservicetemplates%252Ffruits/baobab_serviceTemplate/topologytemplate/", "servicetemplates/baobab_topologytemplate.xml");
	}
}
