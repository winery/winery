/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 *     Nicole Keppler - test cases for validSourcesAndTargets
 *******************************************************************************/

package org.eclipse.winery.repository.resources.entitytypes.relationshiptypes;

import org.eclipse.winery.repository.resources.AbstractResourceTest;

import org.junit.Test;

public class RelationshipTypeResourceTest extends AbstractResourceTest {

	@Test
	public void createRelationshipType() throws Exception {
		this.setRevisionTo("9c486269f6280e0eb14730d01554e7e4553a3d60");
		this.assertPost("relationshiptypes/", "entitytypes/relationshiptypes/kiwi_create.json");
		this.assertGet("relationshiptypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypes%252Ffruits/kiwi/", "entitytypes/relationshiptypes/kiwi_initial.json");
	}

	@Test
	public void kiwiVisualAppearance() throws Exception {
		this.setRevisionTo("d71de5e3c4c8bd117d035602ffbae115eff981d8");
		this.assertGet("relationshiptypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypes%252Ffruits/kiwi/visualappearance/", "entitytypes/relationshiptypes/kiwi_visualAppearance.json");
	}

	@Test
	public void kiwiPutVisualAppearance() throws Exception {
		this.setRevisionTo("d71de5e3c4c8bd117d035602ffbae115eff981d8");
		this.assertPut("relationshiptypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypes%252Ffruits/kiwi/visualappearance/", "entitytypes/relationshiptypes/kiwi_visualAppearance_put.json");
		this.assertGet("relationshiptypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypes%252Ffruits/kiwi/visualappearance/", "entitytypes/relationshiptypes/kiwi_visualAppearance.json");
	}

	@Test
	public void kiwiValidSourcesAndTargets() throws Exception {
		this.setRevisionTo("d71de5e3c4c8bd117d035602ffbae115eff981d8");
		this.assertGet("relationshiptypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypes%252Ffruits/kiwi/validsourcesandtargets/", "entitytypes/relationshiptypes/kiwi_validEndings.json");
	}

	@Test
	public void kiwiPutValidSourcesAndTargets() throws Exception {
		this.setRevisionTo("d71de5e3c4c8bd117d035602ffbae115eff981d8");
		this.assertPut("relationshiptypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypes%252Ffruits/kiwi/validsourcesandtargets/", "entitytypes/relationshiptypes/kiwi_validEndings_put.json");
		this.assertGet("relationshiptypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypes%252Ffruits/kiwi/validsourcesandtargets/", "entitytypes/relationshiptypes/kiwi_validEndings_put.json");
	}
}
