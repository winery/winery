/*******************************************************************************
 * Copyright (c) 2022-2023 Contributors to the Eclipse Foundation
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
import javax.xml.namespace.QName;

import org.eclipse.winery.edmm.EdmmManager;
import org.eclipse.winery.edmm.model.EdmmMappingItem;
import org.eclipse.winery.edmm.model.EdmmType;
import org.eclipse.winery.repository.rest.resources.AbstractResourceTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EdmmTypesResourceTest extends AbstractResourceTest {
    final static String PATH = "admin/edmmtypes";
    private static final Logger LOGGER = LoggerFactory.getLogger(EdmmTypesResourceTest.class);

    @Test
    void testPutTypes() {
        EdmmManager.forRepository(repository).setOneToOneMappings(new ArrayList<>());
        List<EdmmType> types = new ArrayList<>();
        types.add(new EdmmType("theAppleType"));
        types.add(new EdmmType("theBananaType"));
        // now we will have two types
        EdmmManager.forRepository(repository).setEdmmTypes(types);
        // let's replace the 2 types with 3 different types
        assertPutWithResponse(PATH, "entitytypes/admin/test-put-edmm-types-3.json");
        Assertions.assertEquals(3, EdmmManager.forRepository(repository).getEdmmTypes().size());
        // original types must not be there anymore. Let's try one of them.
        Assertions.assertFalse(EdmmManager.forRepository(repository).getEdmmTypes().contains(types.get(0)));
        // let's try to duplicate types! an error is expected!
        assertPutStatusCode(PATH, "entitytypes/admin/test-put-edmm-types-bad.json",
            Response.Status.BAD_REQUEST.getStatusCode());

        // let's create a dummy mapping
        final EdmmType sample = EdmmManager.forRepository(repository).getEdmmTypes().get(0);
        final QName dummy = new QName("journey", "toHell");
        List<EdmmMappingItem> mappings = new ArrayList<>();
        mappings.add(new EdmmMappingItem(sample, dummy));
        EdmmManager.forRepository(repository).setOneToOneMappings(mappings);

        // let's try to introduce new types that are missing the one used in the mapping!
        assertPutStatusCode(PATH, "entitytypes/admin/test-put-edmm-types-2.json",
            Response.Status.CONFLICT.getStatusCode());
    }
}
