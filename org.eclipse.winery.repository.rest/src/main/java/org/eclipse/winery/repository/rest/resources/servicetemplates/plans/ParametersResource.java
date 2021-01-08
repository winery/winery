/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.servicetemplates.plans;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

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
        boolean tb;
        if (required == null) {
            // The specification states that the default value is "yes"
            // We assume "no", because Chrome does not send the checkbox data if a checkbox is not checked
            tb = false;
        } else {
            if (required.equalsIgnoreCase("on")) {
                tb = true;
            } else if (required.equalsIgnoreCase("off")) {
                tb = false;
            } else {
                try {
                    tb = required.equalsIgnoreCase("yes");
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
