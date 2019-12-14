/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.ids.admin.NamespacesId;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages prefixes for the namespaces
 */
public class NamespacesResource extends AbstractAdminResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamespacesResource.class);

    private final NamespaceManager namespaceManager;

    public NamespacesResource() {
        super(new NamespacesId());
        namespaceManager = RepositoryFactory.getRepository().getNamespaceManager();
    }

    /**
     * Sets / overwrites prefix/namespace mapping
     * <p>
     * In case the prefix is already bound to another namespace, BAD_REQUEST is returned.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response replaceNamespaces(List<NamespaceProperties> namespacesList) {
        if (namespacesList == null) {
            return Response.status(Status.BAD_REQUEST).entity("namespace list must be given.").build();
        }
        this.namespaceManager.clear();
        this.namespaceManager.addAllPermanent(namespacesList);
        return Response.noContent().build();
    }

    /**
     * Deletes given namespace from the repository
     *
     * @param uri to delete. The namespace is URLencoded.
     */
    @DELETE
    @Path("{namespace}")
    public Response onDelete(@PathParam("namespace") String uri) {
        Response res;
        uri = EncodingUtil.URLdecode(uri);
        if (this.namespaceManager.hasPermanentProperties(uri)) {
            this.namespaceManager.removeNamespaceProperties(uri);
            res = Response.noContent().build();
        } else {
            res = Response.status(Status.NOT_FOUND).build();
        }
        return res;
    }

    @Path("{namespace}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getPrefixForEncodedNamespace(@PathParam("namespace") String uri) {
        uri = EncodingUtil.URLdecode(uri);
        return this.namespaceManager.getPrefix(uri);
    }

    /**
     * Returns the list of all namespaces registered with his manager and used at component instances.
     *
     * @return a JSON list containing the non-encoded URIs of each known namespace
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<NamespaceProperties> getNamespacesAsJSONlist(
        @ApiParam(value = "if set all namespaces are returned otherwise the list will be filtered by disallowed namespaces", required = false) @QueryParam("all") String allNamespaces) {
        return this.namespaceManager.getAllNamespaces().entrySet().stream()
            .filter(entry -> {
                    if (allNamespaces == null) {
                        return !Namespaces.getDisallowedNamespaces().contains(entry.getKey());
                    } else {
                        return true;
                    }
                }
            )
            .map(Map.Entry::getValue)
            .sorted()
            .collect(Collectors.toList());
    }
}
