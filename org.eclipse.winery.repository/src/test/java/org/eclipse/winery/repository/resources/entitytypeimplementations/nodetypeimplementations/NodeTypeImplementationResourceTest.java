/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 * Lukas Harzenetter - initial API and implementation
 */

package org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations;

import org.eclipse.winery.repository.resources.AbstractResourceTest;

import org.junit.Test;

public class NodeTypeImplementationResourceTest extends AbstractResourceTest {

	@Test
	public void nodeTypeImplementationResourceCreation() throws Exception {
		this.setRevisionTo("8b125a426721f8a0eb17340dc08e9b571b0cd7f7");
		this.assertPost("nodetypeimplementations/", "entityimplementations/nodetypeimplementations/baobab_create.json");
		this.assertGetSize("nodetypeimplementations/", 1);
		this.assertGet("nodetypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypeimplementations%252Ffruits/baobab_impl/",
				"entityimplementations/nodetypeimplementations/baobab_initial.json");
	}

	@Test
	public void nodeTypeImplementationResourceImplementationArtifactsCreation() throws Exception {
		this.setRevisionTo("9c486269f6280e0eb14730d01554e7e4553a3d60");
		this.assertPost("nodetypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypeimplementations%252Ffruits/baobab_impl/implementationartifacts/",
				"entityimplementations/nodetypeimplementations/baobab_create_artifact.json");
		this.assertGet("artifacttemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttemplates%252Ffruits/baobab_bananaInterface_IA/", "entityimplementations/nodetypeimplementations/initial_artifact_template.json");
	}

	@Test
	public void getInterfacesOfAssociatedType() throws Exception {
		this.setRevisionTo("9c486269f6280e0eb14730d01554e7e4553a3d60");
		this.assertGet("nodetypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypeimplementations%252Ffruits/baobab_impl/implementationartifacts/interfaces/","entityimplementations/nodetypeimplementations/baobab_interfacesOfAssociatedType.json");
	}
}
