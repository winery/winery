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
import org.eclipse.winery.model.tosca.TConstraintClause;
import org.eclipse.winery.model.tosca.TDataType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.kvproperties.ConstraintClauseKV;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;

public class DataTypeResource extends AbstractComponentInstanceResource {

    public DataTypeResource(DataTypeId id) {
        super(id);
    }
    
    public TDataType getDataType() {
        // Because DataTypes are serialized into their own ServiceTemplate, but mapped to a Definitions child directly
        return getDefinitions().getDataTypes().get(0);
    }
    
    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TDataType element() {
        return getDataType();
    }
    
    @GET
    @Path("constraints/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConstraintClauseKV> constraints() {
        return getDataType().getConstraints();
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TDataType();
    }
}
