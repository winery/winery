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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.ids.definitions.DataTypeId;
import org.eclipse.winery.model.tosca.TDataType;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.kvproperties.ConstraintClauseKV;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;

public class DataTypeResource extends AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal {

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
    
    @GET
    @Path("constraints/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConstraintClauseKV> constraints() {
        return getDataType().getConstraints();
    }

    @GET
    @Path("properties/")
    @Produces(MediaType.APPLICATION_JSON) 
    public List<TEntityType.PropertyDefinition> properties() {
        return getDataType().getProperties();
    }
    
    @Override
    protected TExtensibleElements createNewElement() {
        return new TDataType();
    }
}
