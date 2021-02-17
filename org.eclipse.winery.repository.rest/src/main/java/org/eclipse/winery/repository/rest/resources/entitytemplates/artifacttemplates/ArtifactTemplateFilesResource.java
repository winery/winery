/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.GenericFileResource;
import org.eclipse.winery.repository.rest.resources.apiData.ArtifactResourceApiData;
import org.eclipse.winery.repository.rest.resources.apiData.ArtifactResourcesApiData;

import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtifactTemplateFilesResource extends GenericFileResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactTemplateFilesResource.class);
    private final ArtifactTemplateFilesResource destinationDir;

    public ArtifactTemplateFilesResource(DirectoryId fileDir, ArtifactTemplateFilesResource destinationDir) {
        super(fileDir);
        this.destinationDir = destinationDir;
    }

    public ArtifactTemplateFilesResource(DirectoryId fileDir) {
        this(fileDir, null);
    }

    @Override
    public Response onPost(InputStream uploadedInputStream, FormDataContentDisposition fileDetail,
                           FormDataBodyPart body, UriInfo uriInfo, String fileName) {
        Response response = super.onPost(uploadedInputStream, fileDetail, body, uriInfo, fileName);

        try {
            BackendUtils.synchronizeReferences(RepositoryFactory.getRepository(), (ArtifactTemplateId) fileDir.getParent());
        } catch (IOException e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }

        return response;
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
