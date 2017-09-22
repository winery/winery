/*******************************************************************************
 * Copyright (c) 2012-2013,2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateDirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.datatypes.FileMeta;
import org.eclipse.winery.repository.rest.resources.apiData.ArtifactResourceApiData;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilesResource.class);
	private final ArtifactTemplateDirectoryId fileDir;


	public FilesResource(ArtifactTemplateDirectoryId fileDir) {
		this.fileDir = fileDir;
	}

	/**
	 * Handles the upload of a <em>single</em> file. Adds the given file to the current artifact template.
	 *
	 * If the file already exists, is it <em>overridden</em>
	 *
	 * @return JSON with data required by JQuery-File-Upload (see https://github.com/blueimp/jQuery-File-Upload/wiki/Setup)
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response onPost(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("file") FormDataBodyPart body, @Context UriInfo uriInfo) {
		// existence check not required as instantiation of the resource ensures that the object only exists if the resource exists
		FilesResource.LOGGER.debug("Beginning with file upload");

		String fileName = fileDetail.getFileName();
		if (StringUtils.isEmpty(fileName)) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		RepositoryFileReference ref = this.fileName2fileRef(fileName, false);

		// TODO: instead of fixing the media type, we could overwrite the browser's mediatype by using some user configuration
		BufferedInputStream bis = new BufferedInputStream(uploadedInputStream);
		org.apache.tika.mime.MediaType mediaType = BackendUtils.getFixedMimeType(bis, fileName, org.apache.tika.mime.MediaType.parse(body.getMediaType().toString()));

		Response response = RestUtils.putContentToFile(ref, bis, mediaType);
		if (response.getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
			return response;
		}

		try {
			BackendUtils.synchronizeReferences((ArtifactTemplateId) fileDir.getParent());
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		String URL = RestUtils.getAbsoluteURL(this.fileDir) + Util.URLencode(fileName);
		return Response.created(RestUtils.createURI(URL)).entity(this.getAllFileMetas()).build();
	}

	/**
	 * Returns a list of file meta object
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getJSON() {
		String json = BackendUtils.Object2JSON(this.getAllFileMetas());
		json = "{\"files\":" + json + "}";
		return json;
	}

	private List<FileMeta> getAllFileMetas() {
		return RepositoryFactory.getRepository()
			.getContainedFiles(this.fileDir)
			.stream()
			.map(ref -> new FileMeta(ref))
			.collect(Collectors.toList());
	}

	private RepositoryFileReference fileName2fileRef(String fileName, boolean encoded) {
		if (encoded) {
			fileName = Util.URLdecode(fileName);
		}
		return new RepositoryFileReference(this.fileDir, fileName);
	}

	@GET
	@Path("/{fileName}")
	public Response getFile(@PathParam("fileName") String fileName, @HeaderParam("If-Modified-Since") String modified) {
		RepositoryFileReference ref = this.fileName2fileRef(fileName, true);
		return RestUtils.returnRepoPath(ref, modified);
	}

	@DELETE
	@Path("/{fileName}")
	public Response deleteFile(@PathParam("fileName") String fileName) {
		RepositoryFileReference ref = this.fileName2fileRef(fileName, true);
		return RestUtils.delete(ref);
	}

	@POST
	@Path("/{fileName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postFile(@PathParam("fileName") String fileName, ArtifactResourceApiData data) {
		if (StringUtils.isEmpty(fileName)) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		RepositoryFileReference ref = this.fileName2fileRef(fileName, false);
		return RestUtils.putContentToFile(ref, data.content, MediaType.TEXT_PLAIN_TYPE);
	}
	
	@PUT
	@Path("/{fileName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putFile(@PathParam("fileName") String fileName, ArtifactResourceApiData data) {
		if (StringUtils.isEmpty(fileName)) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		RepositoryFileReference ref = this.fileName2fileRef(fileName, false);
		return RestUtils.putContentToFile(ref, data.content, MediaType.TEXT_PLAIN_TYPE);
	}
}
