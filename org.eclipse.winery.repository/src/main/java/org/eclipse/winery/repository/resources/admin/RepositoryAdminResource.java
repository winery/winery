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
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.winery.repository.Prefs;
import org.eclipse.winery.repository.backend.IRepositoryAdministration;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.restdoc.annotations.RestDoc;
import org.restdoc.annotations.RestDocParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

public class RepositoryAdminResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryAdminResource.class);


	// @formatter:off
	@GET
	@Produces(MediaType.TEXT_HTML) // we cannot add MimeTypes.MIMETYPE_ZIP as dumpRepository also produces that mimetype
	@RestDoc(methodDescription = "Returns the repository admin page and implements administration utility")
	public Response onGet(
		@QueryParam(value = "dump") @RestDocParam(description = "If given, a dump of the repository is sent") String dump,
		@QueryParam(value = "reset") @RestDocParam(description = "Resets the repository to the last &ldquo;official&rdquo; known state") String reset,
		@QueryParam(value = "commit") @RestDocParam(description = "Commits the current state to the repository and pushes it upstream") String commit
	) {
		// @formatter:on
		if (dump != null) {
			return this.dumpRepository();
		} else if (reset != null) {
			try {
				((GitBasedRepository) Prefs.INSTANCE.getRepository()).cleanAndResetHard();
			} catch (Exception e) {
				Response res;
				res = Response.serverError().entity(e.getMessage()).build();
				return res;
			}
			return Response.noContent().build();
		} else if (commit != null) {
			try {
				((GitBasedRepository) Prefs.INSTANCE.getRepository()).addCommit();
			} catch (Exception e) {
				Response res;
				res = Response.serverError().entity(e.getMessage()).build();
				return res;
			}
			return Response.noContent().build();
		} else {
			Viewable viewable = new Viewable("/jsp/admin/repository.jsp", this);
			return Response.ok().entity(viewable).build();
		}
	}

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
		StreamingOutput so = output -> ((IRepositoryAdministration) Repository.INSTANCE).doDump(output);
		StringBuilder sb = new StringBuilder();
		sb.append("attachment;filename=\"repository.zip\"");
		return Response.ok().header("Content-Disposition", sb.toString()).type(org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_ZIP).entity(so).build();
	}
}
