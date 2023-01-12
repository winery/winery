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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.edmm.EdmmManager;
import org.eclipse.winery.edmm.model.EdmmType;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class EdmmTypesResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<EdmmType> getEdmmTypes() {
        return EdmmManager.forRepository(RepositoryFactory.getRepository()).getEdmmTypes();
    }

    /***
     * Replaces the list of EDMM types.
     * Returns a status code 400 if the new list contains the same EDMM type multiple times.
     * Returns a status code 409 if the new list is missing a type being used in a mapping.
     * @param list the new list of EDMM types
     * @return the stored list of EDMM types after executing this operation
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setEdmmTypes(List<EdmmType> list) {
        Set<EdmmType> typeSet = new HashSet<>(list);

        if (typeSet.size() < list.size()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!typeSet.containsAll(EdmmManager.forRepository(RepositoryFactory.getRepository()).getOneToOneMap().values())) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        EdmmManager.forRepository(RepositoryFactory.getRepository()).setEdmmTypes(list);

        return Response.ok(this.getEdmmTypes()).build();
    }
}
