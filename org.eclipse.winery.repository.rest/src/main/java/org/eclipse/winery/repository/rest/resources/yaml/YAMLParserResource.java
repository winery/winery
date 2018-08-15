/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.yaml;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.yaml.converter.Converter;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YAMLParserResource {
    public final static Logger LOGGER = LoggerFactory.getLogger(YAMLParserResource.class);

    @POST
    @ApiOperation(value = "Imports the given zipped YAML files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response importYAML(
        @FormDataParam("file") InputStream uploadInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail,
        @FormDataParam("overwrite") @ApiParam(value = "true/false both not used") Boolean overwrite,
        @Context UriInfo uriInfo
    ) {
        LOGGER.debug("File {}", fileDetail);
        Converter converter = new Converter();
        try {
            converter.convertY2X(uploadInputStream);
        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                StringEscapeUtils.escapeHtml(e.getMessage().trim())
            ).type("text/plain").build();
        }
        return Response.noContent().build();
    }
}
