/*******************************************************************************
 * Copyright (c) 2013-2014 Contributors to the Eclipse Foundation
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources.apiData.boundarydefinitions.PropertyConstraintsApiData;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class PropertyConstraintsResource {

    private final TBoundaryDefinitions.PropertyConstraints propertyConstraints;
    private final TEntityTemplate.PropertyConstraints entityPropertyConstraints;
    private final ServiceTemplateResource res;
    private final AbstractComponentInstanceResource abstractRes;
    private final boolean isEntitytemplate;

    public PropertyConstraintsResource(TBoundaryDefinitions.PropertyConstraints propertyConstraints, ServiceTemplateResource res) {
        this.propertyConstraints = propertyConstraints;
        this.res = res;
        this.entityPropertyConstraints = null;
        this.abstractRes = null;
        this.isEntitytemplate = false;
    }

    public PropertyConstraintsResource(TEntityTemplate.PropertyConstraints propertyConstraints, AbstractComponentInstanceResource res) {
        this.res = null;
        this.propertyConstraints = null;
        this.entityPropertyConstraints = propertyConstraints;
        this.abstractRes = res;
        this.isEntitytemplate = true;
    }

    @Path("{id}")
    @DELETE
    public Response onDelete(@PathParam("id") String id) {
        id = Util.URLdecode(id);
        Iterator<TPropertyConstraint> iterator;
        if (!isEntitytemplate) {
            iterator = this.propertyConstraints.getPropertyConstraint().iterator();
        } else {
            iterator = this.entityPropertyConstraints.getPropertyConstraint().iterator();
        }
        while (iterator.hasNext()) {
            TPropertyConstraint propertyConstraint = iterator.next();
            if (propertyConstraint.getProperty().equals(id)) {
                iterator.remove();
                if (!isEntitytemplate) {
                    return RestUtils.persist(this.res);
                } else {
                    return RestUtils.persist(this.abstractRes);
                }
            }
        }
        // if the property mapping was not found, we reach this point
        // otherwise "iterator.remove()" has called and the resource persisted
        return Response.status(Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onPost(PropertyConstraintsApiData constraintsApiData) {

        Document doc = null;
        DocumentBuilder xmlBuilder;

        if (StringUtils.isEmpty(constraintsApiData.getProperty())) {
            return Response.status(Status.BAD_REQUEST).entity("Property must not be empty").build();
        }
        if (StringUtils.isEmpty(constraintsApiData.getConstraintType())) {
            return Response.status(Status.BAD_REQUEST).entity("Constraint Type must not be empty").build();
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            xmlBuilder = factory.newDocumentBuilder();
            doc = xmlBuilder.parse(new InputSource(new StringReader(constraintsApiData.getFragments().toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        TPropertyConstraint propertyConstraint = new TPropertyConstraint();
        propertyConstraint.setProperty(constraintsApiData.getProperty());
//        propertyConstraint.setAny(constraintsApiData.getFragments());
        propertyConstraint.setAny(doc.getDocumentElement());
        propertyConstraint.setConstraintType(constraintsApiData.getConstraintType());
        if (!isEntitytemplate) {
            this.propertyConstraints.getPropertyConstraint().add(propertyConstraint);
            return RestUtils.persist(this.res);
        } else {
            this.entityPropertyConstraints.getPropertyConstraint().add(propertyConstraint);
            return RestUtils.persist(this.abstractRes);
        }
//        return RestUtils.persist(this.res);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyConstraintsApiData> onGet() {
        List<PropertyConstraintsApiData> apiDatas = new ArrayList<>();
//        Iterator<TPropertyConstraint> iterator = this.propertyConstraints.getPropertyConstraint().iterator();
        if (!isEntitytemplate) {
            for (TPropertyConstraint propertyConstraint : this.propertyConstraints.getPropertyConstraint()) {
                apiDatas.add(new PropertyConstraintsApiData(propertyConstraint));
            }
        } else {
            for (TPropertyConstraint propertyConstraint : this.entityPropertyConstraints.getPropertyConstraint()) {
                apiDatas.add(new PropertyConstraintsApiData(propertyConstraint));
            }
        }
        return apiDatas;
    }
}
