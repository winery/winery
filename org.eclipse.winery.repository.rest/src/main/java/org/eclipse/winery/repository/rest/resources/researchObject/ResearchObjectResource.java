/*******************************************************************************
 * Copyright (c) 2021-2023 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.researchObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.researchobject.ResearchObject;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.ResearchObjectUtils;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.datatypes.ids.elements.ResearchObjectDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.ResearchObjectFilesDirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.FileOrFolderElementApiData;
import org.eclipse.winery.repository.rest.resources.apiData.FileApiData;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.repository.backend.ResearchObjectUtils.correctPath;

public class ResearchObjectResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchObjectResource.class);

    public final RepositoryFileReference metadata_xml_ref;
    public final RepositoryFileReference files_ref;
    private final ResearchObjectDirectoryId researchObjectDirectoryId;
    private final ResearchObjectFilesDirectoryId researchObjectFilesDirectoryId;
    private final IRepository repository;
    private final java.nio.file.Path filesPath;

    public ResearchObjectResource(ServiceTemplateResource serviceTemplateResource) {
        this((ServiceTemplateId) serviceTemplateResource.getId());
    }

    public ResearchObjectResource(ServiceTemplateId serviceTemplateId) {
        this.repository = RepositoryFactory.getRepository();
        this.researchObjectDirectoryId = new ResearchObjectDirectoryId(serviceTemplateId);
        this.researchObjectFilesDirectoryId = new ResearchObjectFilesDirectoryId(researchObjectDirectoryId);
        this.metadata_xml_ref = ResearchObjectUtils.getMetaDataXmlRef(this.researchObjectDirectoryId);
        this.files_ref = ResearchObjectUtils.getFilesRef(this.researchObjectDirectoryId);
        this.filesPath = ResearchObjectUtils.getFilesPath(this.repository, this.files_ref);
    }

    @GET
    @Path("metadata")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public ResearchObject.Metadata getMetadata() {
        return ResearchObjectUtils.getResearchObjectMetadata(repository, this.researchObjectDirectoryId);
    }

    @PUT
    @Path("metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putMetadata(ResearchObject.Metadata metadata) {
        InputStream content = ResearchObjectUtils.putResearchObjectMetadata(metadata, repository, this.researchObjectDirectoryId);
        return RestUtils.putContentToFile(this.metadata_xml_ref, content, MediaType.TEXT_XML_TYPE);
    }

    @GET
    @Path("publication")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public ResearchObject.Publication getPublication() {
        return ResearchObjectUtils.getResearchObjectPublication(repository, this.researchObjectDirectoryId);
    }

    @PUT
    @Path("publication")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putPublication(ResearchObject.Publication publication) {
        InputStream content = ResearchObjectUtils.putResearchObjectPublication(publication, repository, this.researchObjectDirectoryId);
        return RestUtils.putContentToFile(this.metadata_xml_ref, content, MediaType.TEXT_XML_TYPE);
    }

    @PUT
    @Path("files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response putFile(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body, @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("path") FormDataBodyPart targetPath) {
        RepositoryFileReference ref = new RepositoryFileReference(this.researchObjectFilesDirectoryId, Paths.get(correctPath(targetPath.getValue())), fileDetail.getFileName());
        return RestUtils.putContentToFile(ref, uploadedInputStream, body.getMediaType());
    }

    @GET
    @Path("files")
    @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public Map<String, List<FileOrFolderElementApiData>> getDirsAndFiles() {
        Map<String, List<FileOrFolderElementApiData>> map = new HashMap<>();
        try {
            RestUtils.getAllDirsAndFiles(files_ref, Integer.MAX_VALUE).filter(file -> !file.equals(this.filesPath)).forEach(file -> {
                String path = "";
                java.nio.file.Path parentPath = this.filesPath.relativize(file).getParent();
                if (parentPath != null) {
                    path = "/" + parentPath.toString().replace("\\", "/");
                }
                if (map.containsKey(path)) {
                    map.get(path).add(new FileOrFolderElementApiData(file));
                } else {
                    ArrayList<FileOrFolderElementApiData> list = new ArrayList<>();
                    list.add(new FileOrFolderElementApiData(file));
                    map.put(path, list);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to get the files stored on the disk. Reason {}", e.getMessage());
        }
        return map;
    }

    @DELETE
    @Path("files")
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public Response delete(FileApiData element) {
        RepositoryFileReference ref = new RepositoryFileReference(this.researchObjectFilesDirectoryId, correctPath(element.path));
        return RestUtils.delete(ref);
    }

    @POST
    @Path("files")
    @Consumes( {MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public Response update(FileApiData fileApiData) {
        if (fileApiData.moveToPath == null) {
            return createDir(fileApiData.path);
        } else {
            return move(fileApiData.path, fileApiData.moveToPath);
        }
    }

    public Response createDir(String newDir) {
        RepositoryFileReference ref = new RepositoryFileReference(this.researchObjectFilesDirectoryId, correctPath(newDir));
        return RestUtils.createDir(ref);
    }

    public Response move(String sourcePath, String targetPath) {
        if (ResearchObjectUtils.isURL(sourcePath)) {
            return downloadGitRepo(sourcePath, targetPath);
        } else {
            RepositoryFileReference sourceRef = new RepositoryFileReference(this.researchObjectFilesDirectoryId, correctPath(sourcePath));
            RepositoryFileReference targetRef = new RepositoryFileReference(this.researchObjectFilesDirectoryId, correctPath(targetPath));
            return RestUtils.move(sourceRef, targetRef);
        }
    }

    public Response downloadGitRepo(String location, String targetPath) {
        try {
            URL url = new URL(location);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            con.connect();
            String fieldValue = con.getHeaderField("Content-Disposition");
            String filename = fieldValue.substring(fieldValue.indexOf("filename=") + "filename=".length());
            RepositoryFileReference ref = new RepositoryFileReference(this.researchObjectFilesDirectoryId, Paths.get(correctPath(targetPath)), filename);
            return RestUtils.putContentToFile(ref, con.getInputStream(), org.apache.tika.mime.MediaType.parse("application/zip"));
        } catch (IOException e) {
            LOGGER.error("Failed to download the file from {}. Reason {}", location, e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
