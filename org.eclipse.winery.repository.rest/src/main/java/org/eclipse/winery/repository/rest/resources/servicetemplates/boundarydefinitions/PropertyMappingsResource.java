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

import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties.PropertyMappings;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.boundarydefinitions.PropertyMapping;
import org.eclipse.winery.repository.rest.resources.apiData.boundarydefinitions.PropertyMappingsApi;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;

public class PropertyMappingsResource {

    private final PropertyMappings propertyMappings;
    private final ServiceTemplateResource res;


    public PropertyMappingsResource(PropertyMappings propertyMappings, ServiceTemplateResource res) {
        this.propertyMappings = propertyMappings;
        this.res = res;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyMappingsApi getJSON(@Context UriInfo uriInfo) {
        return new PropertyMappingsApi(this.res.getServiceTemplate());
    }

    @Path("{serviceTemplatePropertyRef}")
    @DELETE
    public Response onDelete(@PathParam("serviceTemplatePropertyRef") String serviceTemplatePropertyRef) {
        serviceTemplatePropertyRef = EncodingUtil.URLdecode(serviceTemplatePropertyRef);
        Iterator<TPropertyMapping> iterator = this.propertyMappings.getPropertyMapping().iterator();
        while (iterator.hasNext()) {
            TPropertyMapping propertyMapping = iterator.next();
            if (propertyMapping.getServiceTemplatePropertyRef().equals(serviceTemplatePropertyRef)) {
                iterator.remove();
                return RestUtils.persist(this.res);
            }
        }
        // if the property mapping was not found, we reach this point
        // otherwise "iterator.remove()" has called and the resource persisted
        return Response.status(Status.NOT_FOUND).build();
    }

    private void updatePropertyMapping(TPropertyMapping propertyMapping, String serviceTemplatePropertyRef, TEntityTemplate template, String targetPropertyRef) {
        propertyMapping.setServiceTemplatePropertyRef(serviceTemplatePropertyRef);
        propertyMapping.setTargetObjectRef(template);
        propertyMapping.setTargetPropertyRef(targetPropertyRef);
    }

    @ApiOperation(value = "Creates or updates a property mapping with the given fields")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    // @formatter:off
    public Response onPost(PropertyMapping apiPropertyMapping) {
        // @formatter:on
        if (StringUtils.isEmpty(apiPropertyMapping.serviceTemplatePropertyRef)) {
            return Response.status(Status.BAD_REQUEST).entity("serviceTemplatePropertyRef must not be empty").build();
        }
        if (StringUtils.isEmpty(apiPropertyMapping.targetObjectRef)) {
            return Response.status(Status.BAD_REQUEST).entity("targetObjectRef must not be empty").build();
        }
        if (StringUtils.isEmpty(apiPropertyMapping.targetPropertyRef)) {
            return Response.status(Status.BAD_REQUEST).entity("targetPropertyRef must not be empty").build();
        }

        TEntityTemplate template = ModelUtilities.findNodeTemplateOrRequirementOfNodeTemplateOrCapabilityOfNodeTemplateOrRelationshipTemplate(this.res.getServiceTemplate().getTopologyTemplate(), apiPropertyMapping.targetObjectRef);
        if (template == null) {
            return Response.status(Status.BAD_REQUEST).entity("targetObjectRef " + apiPropertyMapping.targetObjectRef + " could not be resolved.").build();
        }

        // replace propertyMapping if it exists
        for (TPropertyMapping propertyMapping : this.propertyMappings.getPropertyMapping()) {
            if (propertyMapping.getServiceTemplatePropertyRef().equals(apiPropertyMapping.serviceTemplatePropertyRef)) {
                // we found a property with the same mapping
                // just update it ...
                this.updatePropertyMapping(propertyMapping, apiPropertyMapping.serviceTemplatePropertyRef, template, apiPropertyMapping.targetPropertyRef);
                // ... and finish processing
                return RestUtils.persist(this.res);
            }
        }

        // the property mapping didn't exist,
        // we create a new one
        TPropertyMapping newPropertyMapping = new TPropertyMapping();
        this.updatePropertyMapping(newPropertyMapping, apiPropertyMapping.serviceTemplatePropertyRef, template, apiPropertyMapping.targetPropertyRef);
        this.propertyMappings.getPropertyMapping().add(newPropertyMapping);
        return RestUtils.persist(this.res);
    }
}
