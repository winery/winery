/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.servicetemplates.plans;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParametersResource extends EntityWithIdCollectionResource<ParameterResource, TParameter> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParametersResource.class);


	public ParametersResource(List<TParameter> parameters, IPersistable typeResource) {
		super(ParameterResource.class, TParameter.class, parameters, typeResource);
	}

	/**
	 * TODO: This method possibly is never called from the Angular UI
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	// @formatter:off
	public Response createParamter(
			@FormParam("name") String name,
			@FormParam("type") String type,
			//@ApiParam(value = "type tYesNo, not Boolean. For convenience, on/off is also supported. In case this parameter is not provided, 'off' is assumed. This is in contrast to the specification, but it eases implementing the UI")
			@FormParam("required") String required) {
		// @formatter:on
		if (StringUtils.isEmpty(name)) {
			return Response.status(Status.BAD_REQUEST).entity("name must not be null").build();
		}
		if (StringUtils.isEmpty(type)) {
			return Response.status(Status.BAD_REQUEST).entity("type must not be null").build();
		}

		TParameter param = new TParameter();
		param.setName(name);
		param.setType(type);
		TBoolean tb;
		if (required == null) {
			// The specification states that the default value is "yes"
			// We assume "no", because Chrome does not send the checkbox data if a checkbox is not checked
			tb = TBoolean.NO;
		} else {
			if (required.equalsIgnoreCase("on")) {
				tb = TBoolean.YES;
			} else if (required.equalsIgnoreCase("off")) {
				tb = TBoolean.NO;
			} else {
				try {
					tb = TBoolean.valueOf(required);
				} catch (java.lang.IllegalArgumentException e) {
					return Response.status(Status.BAD_REQUEST).entity("Wrong format of required").build();
				}
			}
		}
		param.setRequired(tb);

		this.list.add(param);

		return RestUtils.persist(this.res);
	}

	@Override
	public String getId(TParameter entity) {
		return entity.getName();
	}

	@Override
	@Path("{id}/")
	public ParameterResource getEntityResource(@PathParam("id") String id) {
		return this.getEntityResourceFromEncodedId(id);
	}
}
