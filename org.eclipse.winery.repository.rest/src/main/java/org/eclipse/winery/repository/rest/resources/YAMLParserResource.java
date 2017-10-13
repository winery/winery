/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.yaml.converter.Converter;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YAMLParserResource {
	public final static Logger LOGGER = LoggerFactory.getLogger(YAMLParserResource.class);

	@POST
	@ApiOperation(value = "Imports the given zipped YAML files")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response importYAML(
		@FormDataParam("file") InputStream uploadInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("overwrite") @ApiParam(value = "true/false both not used") Boolean overwrite,
		@Context UriInfo uriInfo
	) {
		LOGGER.debug("File {}", fileDetail);
		Converter converter = new Converter();
		converter.convertY2X(uploadInputStream);
		return Response.noContent().build();
	}
}
