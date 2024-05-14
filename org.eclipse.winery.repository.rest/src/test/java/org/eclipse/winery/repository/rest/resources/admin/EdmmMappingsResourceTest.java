/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.admin;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.eclipse.winery.edmm.EdmmManager;
import org.eclipse.winery.edmm.model.EdmmType;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EdmmMappingsResourceTest extends AbstractResourceTest {

    final static String PATH = "admin/1to1edmmmappings";
    private static final Logger LOGGER = LoggerFactory.getLogger(EdmmTypesResourceTest.class);

    @Test
    void testPutMappings() {
        EdmmManager.forRepository(repository).setOneToOneMappings(new ArrayList<>());
        List<EdmmType> types = new ArrayList<>();
        types.add(new EdmmType("sauron"));
        types.add(new EdmmType("istar"));
        EdmmManager.forRepository(repository).setEdmmTypes(types);
        // we don't have the correct EDMM types yet!
        assertPutStatusCode(PATH, "entitytypes/admin/test-put-1to1-edmm-type-mappings.json",
            Response.Status.NOT_FOUND.getStatusCode());
        // let's add one of the needed edmm types. it has to be not enough!
        types.clear();
        types.add(new EdmmType("software_component"));
        EdmmManager.forRepository(repository).setEdmmTypes(types);
        assertPutStatusCode(PATH, "entitytypes/admin/test-put-1to1-edmm-type-mappings.json",
            Response.Status.NOT_FOUND.getStatusCode());
        // let's add the other one needed
        types.add(new EdmmType("web_application"));
        EdmmManager.forRepository(repository).setEdmmTypes(types);
        assertPutWithResponse(PATH, "entitytypes/admin/test-put-1to1-edmm-type-mappings.json");
        // let's try a set of mappings that contain "software_component" twice!
        assertPutStatusCode(PATH, "entitytypes/admin/test-put-1to1-edmm-type-mappings-bad-1.json",
            Response.Status.CONFLICT.getStatusCode());
        // let's try a set of mappings that contain "{https://edmm.uni-stuttgart.de/nodetypes}Software_Component" twice!
        types.add(new EdmmType("sauron"));
        EdmmManager.forRepository(repository).setEdmmTypes(types);
        assertPutStatusCode(PATH, "entitytypes/admin/test-put-1to1-edmm-type-mappings-bad-2.json",
            Response.Status.CONFLICT.getStatusCode());
    }
}
