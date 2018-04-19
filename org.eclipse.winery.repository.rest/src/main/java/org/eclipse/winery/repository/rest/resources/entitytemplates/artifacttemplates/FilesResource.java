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
package org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.datatypes.FileMeta;
import org.eclipse.winery.repository.rest.resources.apiData.ArtifactResourceApiData;
import org.eclipse.winery.repository.rest.resources.apiData.ArtifactResourcesApiData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilesResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesResource.class);
    private final DirectoryId fileDir;
    private final FilesResource destinationDir;

    public FilesResource(DirectoryId fileDir, FilesResource destinationDir) {
        this.fileDir = fileDir;
        this.destinationDir = destinationDir;
    }

    public FilesResource(DirectoryId fileDir) {
        this(fileDir, null);
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
        String pathsJson = BackendUtils.Object2JSON(this.getAllFilePaths());
        json = "{\"files\":" + json + "," +
            "\"paths\":" + pathsJson + "}";
        return json;
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
        String name = fileNameEncoded ? Util.URLdecode(fileName) : fileName;
        return new RepositoryFileReference(this.fileDir, name);
    }

    private RepositoryFileReference fileName2fileRef(String fileName, String path, boolean fileNameEncoded) {
        String name = fileNameEncoded ? Util.URLdecode(fileName) : fileName;
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

    @POST
    @Path("/{fileName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postFile(@PathParam("fileName") String fileName, ArtifactResourceApiData data) {
        if (StringUtils.isEmpty(fileName)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        RepositoryFileReference ref = this.fileName2fileRef(fileName, data.subDirectory, false);
        return RestUtils.putContentToFile(ref, data.content, MediaType.TEXT_PLAIN_TYPE);
    }

    @PUT
    @Path("/{fileName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putFile(@PathParam("fileName") String fileName, ArtifactResourceApiData data) {
        if (StringUtils.isEmpty(fileName)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        RepositoryFileReference ref = this.fileName2fileRef(fileName, data.subDirectory, false);
        return RestUtils.putContentToFile(ref, data.content, MediaType.TEXT_PLAIN_TYPE);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response copySourceToFiles(@ApiParam(value = "if data contains a non-empty array than only the files" +
        " whose names are included are copied ", required = true) ArtifactResourcesApiData data) {
        if (Objects.isNull(this.destinationDir)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        List<String> artifactList = data.getArtifactNames();
        for (RepositoryFileReference ref : RepositoryFactory.getRepository().getContainedFiles(this.fileDir)) {
            if (artifactList == null || artifactList.contains(ref.getFileName())) {
                try (InputStream inputStream = RepositoryFactory.getRepository().newInputStream(ref)) {
                    String fileName = ref.getFileName();
                    String subDirectory = ref.getSubDirectory().map(s -> s.toString()).orElse("");
                    this.destinationDir.putFile(fileName, subDirectory, inputStream);
                } catch (IOException e) {
                    LOGGER.debug("The artifact source " + ref.getFileName() + " could not be copied to the files directory.", e);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        }
        return Response.status(Status.CREATED).build();
    }

    private Response putFile(String fileName, String subDirectory, InputStream content) {
        if (StringUtils.isEmpty(fileName)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        RepositoryFileReference ref = this.fileName2fileRef(fileName, subDirectory, false);
        return RestUtils.putContentToFile(ref, content, MediaType.TEXT_PLAIN_TYPE);
    }
}
