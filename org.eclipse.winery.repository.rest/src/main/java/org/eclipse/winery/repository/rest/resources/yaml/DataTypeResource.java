/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.ids.definitions.DataTypeId;
import org.eclipse.winery.model.tosca.TDataType;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.extensions.kvproperties.ConstraintClauseKV;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;
import org.eclipse.winery.repository.rest.resources._support.VisualAppearanceResource;
import org.eclipse.winery.repository.rest.resources.apiData.PropertiesDefinitionEnum;
import org.eclipse.winery.repository.rest.resources.apiData.PropertiesDefinitionResourceApiData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataTypeResource extends AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTypeResource.class);

    public DataTypeResource(DataTypeId id) {
        super(id);
        // readjust the cached "element" property to account for deserialization of 
        //  YAML data into TServiceTemplate regardless of the actual type
        element = getDataType();
    }
    
    public TDataType getDataType() {
        // Because DataTypes are serialized into their own ServiceTemplate, but mapped to a Definitions child directly
        return getDefinitions().getDataTypes().get(0);
    }
    
    @PUT
    @Path("constraints")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateConstraints(List<ConstraintClauseKV> data) {
        this.getDataType().setConstraints(data);
        try {
            BackendUtils.persist(requestRepository, id, getDataType());
        } catch (IOException e) {
            LOGGER.error("Failed to persist updated constraint definitions", e);
            return Response.serverError().build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("constraints/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConstraintClauseKV> constraints() {
        return getDataType().getConstraints();
    }

    @GET
    @Path("properties/")
    @Produces(MediaType.APPLICATION_JSON) 
    public PropertiesDefinitionResourceApiData properties() {
        // this cast SHOULD be safe, since DataTypes are a YAML-only feature
        return new PropertiesDefinitionResourceApiData(getDataType().getProperties(), null);
    }

    @POST
    @Path("properties/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setProperties(PropertiesDefinitionResourceApiData data) {
        if (data.selectedValue != PropertiesDefinitionEnum.Yaml
            || !(data.propertiesDefinition instanceof TEntityType.YamlPropertiesDefinition)) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("DataTypes are always specified in YAML mode. No other property definitions types are accepted")
                .build();
        }
        getDataType().setProperties(data.propertiesDefinition);
        return RestUtils.persist(this);
    }
    
    @Override
    protected TExtensibleElements createNewElement() {
        return new TDataType();
    }

    @Path("appearance")
    public VisualAppearanceResource getVisualAppearanceResource() {
        return new VisualAppearanceResource(this, this.getElement().getOtherAttributes(), this.id);
    }
}
