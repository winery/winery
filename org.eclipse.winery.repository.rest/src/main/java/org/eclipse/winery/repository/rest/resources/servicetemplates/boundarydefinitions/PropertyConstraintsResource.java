/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources.apiData.boundarydefinitions.PropertyConstraintsApiData;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyConstraintsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyConstraintsResource.class);

    private final List<TPropertyConstraint> propertyConstraints;
    private final AbstractComponentInstanceResource res;

    public PropertyConstraintsResource(List<TPropertyConstraint> propertyConstraints, AbstractComponentInstanceResource res) {
        this.propertyConstraints = propertyConstraints;
        this.res = res;
    }

    @Path("{id}")
    @DELETE
    public Response onDelete(@PathParam("id") String id) {
        id = EncodingUtil.URLdecode(id);
        Iterator<TPropertyConstraint> iterator = this.propertyConstraints.iterator();

        while (iterator.hasNext()) {
            TPropertyConstraint propertyConstraint = iterator.next();
            if (propertyConstraint.getProperty().equals(id)) {
                iterator.remove();
                return RestUtils.persist(this.res);
            }
        }
        // if the property mapping was not found, we reach this point
        // otherwise "iterator.remove()" has called and the resource persisted
        return Response.status(Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onPost(PropertyConstraintsApiData constraintsApiData) {
        if (StringUtils.isEmpty(constraintsApiData.getProperty())) {
            return Response.status(Status.BAD_REQUEST).entity("Property must not be empty").build();
        }
        if (StringUtils.isEmpty(constraintsApiData.getConstraintType())) {
            return Response.status(Status.BAD_REQUEST).entity("Constraint Type must not be empty").build();
        }

        TPropertyConstraint propertyConstraint = new TPropertyConstraint();
        propertyConstraint.setProperty(constraintsApiData.getProperty());
        // Patching Any from String to XML
        try {
            propertyConstraint.setAny(ModelUtilities.patchAnyItem(constraintsApiData.getFragments()));
        } catch (IOException e) {
            LOGGER.info("Could not parse the given Fragments as XML elements");
            return Response.notAcceptable(null).build();
        }
        propertyConstraint.setConstraintType(constraintsApiData.getConstraintType());
        this.propertyConstraints.add(propertyConstraint);

        return RestUtils.persist(this.res);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyConstraintsApiData> onGet() {
        return this.propertyConstraints.stream()
            .map(PropertyConstraintsApiData::new)
            .collect(Collectors.toList());
    }
}
