/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.jupiter.api.Test;

public class NodeTypeImplementationResourceTest extends AbstractResourceTest {
    private static final String FOLDERPATH = "http%3A%2F%2Fwinery.opentosca.org%2Ftest%2Fnodetypeimplementations%2Ffruits/baobab_impl";
    private static final String ENTITY_TYPE = "nodetypeimplementations/";
    private static final String INSTANCE_XML_PATH = "entityimplementations/" + ENTITY_TYPE + "baobab_bananaInterface_IA-at-c25aa724201824fce6eddcc7c35a666c6e015880.xml";
    private static final String BAOBAB_JSON_PATH = "entityimplementations/" + ENTITY_TYPE + "baobab_initial.json";
    private static final String INSTANCE_URL = ENTITY_TYPE + FOLDERPATH;

    @Test
    public void getInstanceXml() throws Exception {
        this.setRevisionTo("c25aa724201824fce6eddcc7c35a666c6e015880");
        this.assertGet(replacePathStringEncoding(INSTANCE_URL), INSTANCE_XML_PATH);
    }

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
        this.setRevisionTo("8d4abf7f7d79b99e27ec59e2421802c7e021f2a3");
        this.assertPost("nodetypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypeimplementations%252Ffruits/baobab_impl/implementationartifacts/",
            "entityimplementations/nodetypeimplementations/baobab_create_artifact.json");
        this.assertGet("artifacttemplates/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fartifacttemplates%252Ffruits/baobab_bananaInterface_IA/", "entityimplementations/nodetypeimplementations/initial_artifact_template.json");
    }

    @Test
    public void getInterfacesOfAssociatedType() throws Exception {
        this.setRevisionTo("9c486269f6280e0eb14730d01554e7e4553a3d60");
        this.assertGet("nodetypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypeimplementations%252Ffruits/baobab_impl/implementationartifacts/interfaces/",
            "entityimplementations/nodetypeimplementations/baobab_interfacesOfAssociatedType.json");
    }

    @Test
    public void getInheritanceData() throws Exception {
        this.setRevisionTo("410ec7b55bf7cf7daa5e18f4a8562d7b7c0efd1d");
        this.assertGet("nodetypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypeimplementations%252Ffruits/baobab_impl/inheritance/",
            "entityimplementations/nodetypeimplementations/baobab_initial_inheritance.json");
    }

    @Test
    public void putInheritanceData() throws Exception {
        this.setRevisionTo("1c7fbb0495c3ae07817ed7f44e60e8c275a79877");
        this.assertPut("nodetypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypeimplementations%252Ffruits/grape_impl/inheritance/",
            "entityimplementations/nodetypeimplementations/grape_inheritance.json");
        this.assertGet("nodetypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypeimplementations%252Ffruits/grape_impl/xml",
            "entityimplementations/nodetypeimplementations/grape_inheritance.xml");
    }
}
