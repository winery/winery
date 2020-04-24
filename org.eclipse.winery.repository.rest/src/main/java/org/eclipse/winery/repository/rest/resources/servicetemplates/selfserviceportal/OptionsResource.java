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
package org.eclipse.winery.repository.rest.resources.servicetemplates.selfserviceportal;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptionsResource extends EntityWithIdCollectionResource<OptionResource, ApplicationOption> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionsResource.class);

    private SelfServiceMetaDataId selfServiceMetaId;

    /**
     * @param res is the parent of the SelfServicePortalResource, which is a parent of this resource
     */
    public OptionsResource(List<ApplicationOption> list, ServiceTemplateResource res, SelfServiceMetaDataId selfServiceMetaId) {
        super(OptionResource.class, ApplicationOption.class, list, res);
        this.selfServiceMetaId = selfServiceMetaId;
    }

    @Override
    public String getId(ApplicationOption entity) {
        return entity.getId();
    }

    @POST
    @ApiOperation(value = "Adds a new option<p>TODO: @return JSON with .tableData: Array with row data for dataTable</p>")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    // @formatter:off
    public Response onPost(
        @FormDataParam("name") String name,
        @FormDataParam("description") String description,
        @FormDataParam("planServiceName") String planServiceName,
        @FormDataParam("planInputMessage") String planInputMessage,
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @FormDataParam("file") FormDataBodyPart body
    ) {
        // @formatter:on
        if (StringUtils.isEmpty(name)) {
            return Response.status(Status.BAD_REQUEST).entity("planName must be given").build();
        }
        if (StringUtils.isEmpty(description)) {
            return Response.status(Status.BAD_REQUEST).entity("description must be given").build();
        }
        if (StringUtils.isEmpty(planServiceName)) {
            return Response.status(Status.BAD_REQUEST).entity("planServiceName must be given").build();
        }
        if (StringUtils.isEmpty(planInputMessage)) {
            return Response.status(Status.BAD_REQUEST).entity("planInputMessage must be given").build();
        }
        if (uploadedInputStream == null) {
            return Response.status(Status.BAD_REQUEST).entity("file has to be provided").build();
        }
        ApplicationOption option = new ApplicationOption();

        String id = RestUtils.createXmlIdAsString(name);

        String fileNamePrefix = OptionResource.getFileNamePrefix(id);
        String iconFileName = fileNamePrefix + OptionResource.ICON_JPG;
        String planInputMessageFileName = fileNamePrefix + OptionResource.PLAN_INPUT_XML;

        // create option data
        option.setId(id);
        option.setName(name);
        option.setDescription(description);
        option.setIconUrl(iconFileName);
        option.setPlanInputMessageUrl(planInputMessageFileName);
        option.setPlanServiceName(planServiceName);

        // BEGIN: store icon and planInputMessage

        RepositoryFileReference iconRef = new RepositoryFileReference(this.selfServiceMetaId, iconFileName);
        RestUtils.putContentToFile(iconRef, uploadedInputStream, body.getMediaType());

        RepositoryFileReference planInputMessageRef = new RepositoryFileReference(this.selfServiceMetaId, planInputMessageFileName);
        RestUtils.putContentToFile(planInputMessageRef, planInputMessage, MediaType.TEXT_XML_TYPE);

        // END: store icon and planInputMessage

        this.list.add(option);
        return RestUtils.persist(this.res);
    }

    @Override
    @Path("{id}/")
    public OptionResource getEntityResource(@PathParam("id") String id) {
        return this.getEntityResourceFromEncodedId(id);
    }
}
