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
package org.eclipse.winery.repository.rest.resources.servicetemplates.selfserviceportal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.selfservice.Application.Options;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.SelfServiceMetaDataUtils;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.datatypes.ids.elements.ServiceTemplateSelfServiceFilesDirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.entitytemplates.artifacttemplates.ArtifactTemplateFilesResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfServicePortalResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfServicePortalResource.class);

    public final RepositoryFileReference data_xml_ref;
    public final RepositoryFileReference icon_jpg_ref;
    public final RepositoryFileReference image_jpg_ref;

    private final ServiceTemplateResource serviceTemplateResource;

    private final Application application;

    private final SelfServiceMetaDataId id;
    private final ServiceTemplateSelfServiceFilesDirectoryId filesDirectoryId;
    private final IRepository repository;

    public SelfServicePortalResource(ServiceTemplateResource serviceTemplateResource, IRepository repository) {
        this(serviceTemplateResource, (ServiceTemplateId) serviceTemplateResource.getId(), repository);
    }

    /**
     * @param serviceTemplateResource may be null
     * @param serviceTemplateId       the id, must not be null
     */
    private SelfServicePortalResource(ServiceTemplateResource serviceTemplateResource, ServiceTemplateId serviceTemplateId, IRepository repository) {
        this.repository = repository;
        this.serviceTemplateResource = serviceTemplateResource;
        this.id = new SelfServiceMetaDataId(serviceTemplateId);
        this.filesDirectoryId = new ServiceTemplateSelfServiceFilesDirectoryId(serviceTemplateId);
        this.data_xml_ref = SelfServiceMetaDataUtils.getDataXmlRef(this.id);
        this.icon_jpg_ref = SelfServiceMetaDataUtils.getIconJpgRef(this.id);
        this.image_jpg_ref = SelfServiceMetaDataUtils.getImageJpgRef(this.id);
        this.application = this.getData();
    }

    SelfServiceMetaDataId getId() {
        return this.id;
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public Application getData() {
        return SelfServiceMetaDataUtils.getApplication(RepositoryFactory.getRepository(), this.id);
    }

    @PUT
    @Consumes( {MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public Response onPutXML(Application data) {
        String content = BackendUtils.getXMLAsString(data, repository);
        return RestUtils.putContentToFile(this.data_xml_ref, content, MediaType.TEXT_XML_TYPE);
    }

    @GET
    @Path("icon.jpg")
    public Response getIcon(@HeaderParam("If-Modified-Since") String modified) {
        RepositoryFileReference ref = new RepositoryFileReference(this.id, "icon.jpg");
        return RestUtils.returnRepoPath(ref, modified);
    }

    @PUT
    @Path("icon.jpg")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response putIcon(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body) {
        try {
            SelfServiceMetaDataUtils.ensureDataXmlExists(RepositoryFactory.getRepository(), this.id);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
        RepositoryFileReference ref = new RepositoryFileReference(this.id, "icon.jpg");
        Response response = RestUtils.putContentToFile(ref, uploadedInputStream, body.getMediaType());
        if (StringUtils.isEmpty(this.application.getIconUrl())) {
            this.application.setIconUrl("icon.jpg");
            persist();
        }
        return response;
    }

    @GET
    @Path("image.jpg")
    public Response getImage(@HeaderParam("If-Modified-Since") String modified) {
        RepositoryFileReference ref = new RepositoryFileReference(this.id, "image.jpg");
        return RestUtils.returnRepoPath(ref, modified);
    }

    @PUT
    @Path("image.jpg")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response putImage(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body) {
        try {
            SelfServiceMetaDataUtils.ensureDataXmlExists(RepositoryFactory.getRepository(), this.id);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
        RepositoryFileReference ref = new RepositoryFileReference(this.id, "image.jpg");
        Response response = RestUtils.putContentToFile(ref, uploadedInputStream, body.getMediaType());
        if (StringUtils.isEmpty(this.application.getImageUrl())) {
            this.application.setImageUrl("image.jpg");
            persist();
        }
        return response;
    }

    @Path("files")
    public ArtifactTemplateFilesResource files() {
        return new ArtifactTemplateFilesResource(this.filesDirectoryId);
    }

    @GET
    @Path("files/zip")
    @Produces(MimeTypes.MIMETYPE_ZIP)
    public Response getFilesZip() {
        return RestUtils.getZippedContents(this.filesDirectoryId);
    }

    @PUT
    @Path("displayname")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onPutOnDisplayName(Application value) {
        this.application.setDisplayName(value.getDisplayName());
        return persist();
    }

    @PUT
    @Path("description")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onPutOnDescription(Application value) {
        this.application.setDescription(value.getDescription());
        return persist();
    }

    @Path("options/")
    public OptionsResource getOptionsResource() {
        Options options = this.application.getOptions();
        if (options == null) {
            options = new Options();
            this.application.setOptions(options);
        }
        return new OptionsResource(options.getOption(), serviceTemplateResource, this.id);
    }

    /**
     * @return the internal application object. Used for the export.
     */
    public Application getApplication() {
        return this.application;
    }

    @GET
    @Path("xml")
    @Produces( {MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    public String getApplicationAsXMLStringEncoded() {
        String res;
        if (RepositoryFactory.getRepository().exists(this.data_xml_ref)) {
            StringWriter sw = new StringWriter();
            try (InputStream is = RepositoryFactory.getRepository().newInputStream(this.data_xml_ref)) {
                IOUtils.copy(is, sw);
            } catch (IOException e) {
                SelfServicePortalResource.LOGGER.error("Could not read from file", e);
            }
            res = sw.toString();
        } else {
            // return skeleton for application
            // application object is already filled with default values if no file exists in repo
            res = BackendUtils.getXMLAsString(this.getApplication(), repository);
        }
        return res;
    }

    public Response persist() {
        return RestUtils.persist(this.application, this.data_xml_ref, MediaType.TEXT_XML);
    }
}
