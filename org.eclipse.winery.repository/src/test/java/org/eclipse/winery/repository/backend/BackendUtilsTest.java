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
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.junit.Assert;
import org.junit.Test;

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
}
