/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.IRepositoryAdministration;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.MultiRepository;
import org.eclipse.winery.repository.backend.filebased.RepositoryConfigurationManager;
import org.eclipse.winery.repository.backend.filebased.RepositoryProperties;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

public class RepositoryAdminResource {

    /**
     * Imports the given ZIP
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importRepositoryDump(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
        ((IRepositoryAdministration) RepositoryFactory.getRepository()).doImport(uploadedInputStream);
        return Response.noContent().build();
    }

    @DELETE
    public void deleteRepositoryData() {
        ((IRepositoryAdministration) RepositoryFactory.getRepository()).doClear();
    }

    @GET
    @Produces(org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_ZIP)
    public Response dumpRepository() {
        StreamingOutput so = output -> ((IRepositoryAdministration) RepositoryFactory.getRepository()).doDump(output);
        return Response.ok()
            .header("Content-Disposition", "attachment;filename=\"repository.zip\"")
            .type(org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_ZIP)
            .entity(so)
            .build();
    }

    /**
     * returns List of Repositories to frontend
     *
     * @return List of Repositories
     */
    @GET
    @Path("repositories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RepositoryProperties> getRepositoriesAsJson() {
        IRepository repository = RepositoryFactory.getRepository();
        if (repository instanceof MultiRepository) {
            return RepositoryConfigurationManager.getRepositoriesFromFile((MultiRepository) repository);
        }
        return Collections.emptyList();
    }

    /**
     * get Repositories from frontend
     */
    @POST
    @Path("repositories")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRepository(List<RepositoryProperties> repositoriesList) {
        if (repositoriesList == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Repositories list must be given.")
                .build();
        }
        RepositoryConfigurationManager.addRepositoryToFile(repositoriesList);
        return Response.ok().build();
    }
}
