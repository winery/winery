/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.edmm.EdmmManager;
import org.eclipse.winery.edmm.model.EdmmMappingItem;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class EdmmMappingsResource {

    private final Type type;
    private final EdmmManager edmmManager;

    public enum Type {
        ONE_TO_ONE,
        EXTENDS
    }

    public EdmmMappingsResource(Type type) {
        this.type = type;
        this.edmmManager = EdmmManager.forRepository(RepositoryFactory.getRepository());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<EdmmMappingItem> getMapping() {
        if (type == Type.ONE_TO_ONE) {
            return this.edmmManager.getOneToOneMappings();
        }

        return this.edmmManager.getTypeMappings();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<EdmmMappingItem> setMappings(List<EdmmMappingItem> list) {
        if (type == Type.ONE_TO_ONE) {
            this.edmmManager.setOneToOneMappings(list);
        } else {
            this.edmmManager.setTypeMappings(list);
        }
        return this.getMapping();
    }
}
