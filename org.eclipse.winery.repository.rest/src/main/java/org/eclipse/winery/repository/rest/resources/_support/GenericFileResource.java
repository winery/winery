/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources._support;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.datatypes.FileMeta;
import org.eclipse.winery.repository.rest.resources.apiData.MetaDataApiData;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericFileResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericFileResource.class);
    protected final DirectoryId fileDir;

    public GenericFileResource(DirectoryId fileDir) {
        this.fileDir = fileDir;
    }

    /**
     * Handles the upload of a <em>single</em> file. Adds the given file to the current artifact template.
     * <p>
     * If the file already exists, is it <em>overridden</em>
     *
     * @return JSON with data required by JQuery-File-Upload (see https://github.com/blueimp/jQuery-File-Upload/wiki/Setup)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response onPost(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail,
                           @FormDataParam("file") FormDataBodyPart body, @Context UriInfo uriInfo) {
        return onPost(uploadedInputStream, fileDetail, body, uriInfo, fileDetail.getFileName());
    }

    public Response onPost(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail,
                           @FormDataParam("file") FormDataBodyPart body, @Context UriInfo uriInfo, String fileName) {
        // existence check not required as instantiation of the resource ensures that the object only exists if the resource exists
        LOGGER.debug("Beginning with file upload");

        if (StringUtils.isEmpty(fileName)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        RepositoryFileReference ref = this.fileName2fileRef(fileName, false);

        // TODO: instead of fixing the media type, we could overwrite the browser's mediatype by using some user configuration
        BufferedInputStream bis = new BufferedInputStream(uploadedInputStream);
        org.apache.tika.mime.MediaType mediaType = BackendUtils.getFixedMimeType(bis, fileName, org.apache.tika.mime.MediaType.parse(body.getMediaType().toString()));

        Response response = RestUtils.putContentToFile(ref, bis, mediaType);
        if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            return response;
        }

        String URL = RestUtils.getAbsoluteURL(this.fileDir) + EncodingUtil.URLencode(fileName);
        return Response.created(URI.create(URL)).entity(this.getAllFileMetas()).build();
    }

    /**
     * Returns a list of file meta object
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MetaDataApiData getJSON() {
        return new MetaDataApiData(this.getAllFileMetas(), this.getAllFilePaths());
    }

    private List<FileMeta> getAllFileMetas() {
        return RepositoryFactory.getRepository()
            .getContainedFiles(this.fileDir)
            .stream()
            .map(ref -> new FileMeta(ref))
            .collect(Collectors.toList());
    }

    private List<String> getAllFilePaths() {
        List<String> paths = new ArrayList<>();
        for (RepositoryFileReference ref : RepositoryFactory.getRepository().getContainedFiles(this.fileDir)) {
            if (ref.getSubDirectory().isPresent()) {
                paths.add(ref.getSubDirectory().get().toString());
            } else {
                paths.add("");
            }
        }
        return paths;
    }

    private RepositoryFileReference fileName2fileRef(String fileName, boolean fileNameEncoded) {
        String name = fileNameEncoded ? EncodingUtil.URLdecode(fileName) : fileName;
        return new RepositoryFileReference(this.fileDir, name);
    }

    protected RepositoryFileReference fileName2fileRef(String fileName, String path, boolean fileNameEncoded) {
        String name = fileNameEncoded ? EncodingUtil.URLdecode(fileName) : fileName;
        return new RepositoryFileReference(this.fileDir, Paths.get(path), name);
    }

    @GET
    @Path("/{fileName}")
    public Response getFile(@PathParam("fileName") String fileName, @HeaderParam("If-Modified-Since") String modified, @QueryParam("path") String path) {
        path = Objects.isNull(path) ? "" : path;
        RepositoryFileReference ref = this.fileName2fileRef(fileName, path, true);
        return RestUtils.returnRepoPath(ref, modified);
    }

    @DELETE
    @Path("/{fileName}")
    public Response deleteFile(@PathParam("fileName") String fileName, @QueryParam("path") String path) {
        path = Objects.isNull(path) ? "" : path;
        RepositoryFileReference ref = this.fileName2fileRef(fileName, path, true);
        return RestUtils.delete(ref);
    }
}
