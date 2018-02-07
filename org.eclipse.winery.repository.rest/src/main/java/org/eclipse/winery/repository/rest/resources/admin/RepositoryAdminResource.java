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

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.eclipse.winery.repository.backend.IRepositoryAdministration;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        StreamingOutput so = new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                ((IRepositoryAdministration) RepositoryFactory.getRepository()).doDump(output);
            }
        };
        return Response.ok().header("Content-Disposition", "attachment;filename=\"repository.zip\"").type(org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_ZIP).entity(so).build();
    }
}
