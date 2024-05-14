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

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.edmm.EdmmManager;
import org.eclipse.winery.edmm.model.EdmmMappingItem;
import org.eclipse.winery.edmm.model.EdmmType;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class EdmmMappingsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<EdmmMappingItem> getMapping() {
        return EdmmManager.forRepository(RepositoryFactory.getRepository()).getOneToOneMappings();
    }

    /***
     * Sets TOSCA-EDMM 1-to-1 type mappings.
     * @param list the new edmm mapping list
     * @return the list after execution
     * returns code 404 if at least one of the specified types does not exist.
     * returns code 409 if a TOSCA type or an EDMM type is mentioned more than once (i.e., mappings are not 1-to-1)
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setMappings(List<EdmmMappingItem> list) {
        // wrapped into hash set to enhance performance
        if (!new HashSet<>(EdmmManager.forRepository(RepositoryFactory.getRepository()).getEdmmTypes())
            .containsAll(list.stream().map(m -> m.edmmType).collect(Collectors.toList()))) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<EdmmType> allEdmmTypesOfNewMappingsList = list
            .stream()
            .map(i -> i.edmmType)
            .collect(Collectors.toList());
        // dupliacte EDMM types?
        if (new HashSet<>(allEdmmTypesOfNewMappingsList).size() < allEdmmTypesOfNewMappingsList.size()) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        List<QName> allTOSCATypesOfNewMappingsList = list
            .stream()
            .map(i -> i.toscaType)
            .collect(Collectors.toList());
        // duplicate TOSCA types?
        if (new HashSet<>(allTOSCATypesOfNewMappingsList).size() < allTOSCATypesOfNewMappingsList.size()) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        EdmmManager.forRepository(RepositoryFactory.getRepository()).setOneToOneMappings(list);

        return Response.ok(this.getMapping()).build();
    }
}
