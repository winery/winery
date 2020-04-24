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

package org.eclipse.winery.repository.rest.resources.entitytypeimplementations.relationshiptypeimplementations;

import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;
import org.junit.jupiter.api.Test;

public class RelationshipTypeImplementationResourceTest extends AbstractResourceTest {
    private static final String FOLDERPATH = "http%3A%2F%2Fwinery.opentosca.org%2Ftest%2Frelationshiptypeimplementations%2Ffruits/kiwi_implementation";
    private static final String ENTITY_TYPE = "relationshiptypeimplementations/";
    private static final String INSTANCE_XML_PATH = "entityimplementations/" + ENTITY_TYPE + "kiwi-at-c25aa724201824fce6eddcc7c35a666c6e015880.xml";
    private static final String BAOBAB_JSON_PATH = "entityimplementations/" + ENTITY_TYPE + "list-at-c25aa724201824fce6eddcc7c35a666c6e015880.json";
    private static final String INSTANCE_URL = ENTITY_TYPE + FOLDERPATH;

    @Test
    public void getInstanceXml() throws Exception {
        this.setRevisionTo("c25aa724201824fce6eddcc7c35a666c6e015880");
        this.assertGet(replacePathStringEncoding(INSTANCE_URL), INSTANCE_XML_PATH);
    }

    @Test
    public void getServicetemplate() throws Exception {
        this.setRevisionTo("c25aa724201824fce6eddcc7c35a666c6e015880");
        this.assertGet(ENTITY_TYPE, BAOBAB_JSON_PATH);
    }

    @Test
    public void getComponentAsJson() throws Exception {
        this.setRevisionTo("3fe0df76e98d46ead68295920e5d1cf1354bdea1");
        this.assertGet("relationshiptypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypeimplementations%252Ffruits/kiwi_implementation/", "entityimplementations/relationshiptypeimplementations/initial.json");
    }

    @Test
    public void getInheritanceData() throws Exception {
        this.setRevisionTo("410ec7b55bf7cf7daa5e18f4a8562d7b7c0efd1d");
        this.assertGet("relationshiptypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypeimplementations%252Ffruits/kiwi_implementation/inheritance",
            "entityimplementations/relationshiptypeimplementations/kiwi_initial_inheritance.json");
    }

    @Test
    public void putInheritanceData() throws Exception {
        this.setRevisionTo("aae0a874dd18cfed6abf4e33cb06f78a5a22b861");
        this.assertPut("relationshiptypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypeimplementations%252Ffruits/attendTo_implementation/inheritance/",
            "entityimplementations/relationshiptypeimplementations/attendTo_inheritance.json");
        this.assertGet("relationshiptypeimplementations/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Frelationshiptypeimplementations%252Ffruits/attendTo_implementation/xml/",
            "entityimplementations/relationshiptypeimplementations/attendTo_inheritance.xml");

    }
}
