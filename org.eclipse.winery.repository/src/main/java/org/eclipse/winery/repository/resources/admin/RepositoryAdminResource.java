/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.winery.repository.backend.IRepositoryAdministration;
import org.eclipse.winery.repository.backend.Repository;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryAdminResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryAdminResource.class);

	/**
	 * Imports the given ZIP
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response importRepositoryDump(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
		((IRepositoryAdministration) Repository.INSTANCE).doImport(uploadedInputStream);
		return Response.noContent().build();
	}

	@DELETE
	public void deleteRepositoryData() {
		((IRepositoryAdministration) Repository.INSTANCE).doClear();
	}

	@GET
	@Produces(org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_ZIP)
	public Response dumpRepository() {
		StreamingOutput so = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				((IRepositoryAdministration) Repository.INSTANCE).doDump(output);
			}
		};
		return Response.ok().header("Content-Disposition", "attachment;filename=\"repository.zip\"").type(org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_ZIP).entity(so).build();
	}
}
