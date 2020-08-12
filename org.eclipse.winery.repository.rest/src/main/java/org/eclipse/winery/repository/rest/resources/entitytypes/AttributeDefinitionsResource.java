/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.extensions.kvproperties.AttributeDefinition;
import org.eclipse.winery.repository.rest.RestUtils;

public class AttributeDefinitionsResource {

    private final EntityTypeResource parent;

    public AttributeDefinitionsResource(EntityTypeResource parent) {
        this.parent = parent;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AttributeDefinition> get() {
        return this.parent.getEntityType().getAttributeDefinitions();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(List<AttributeDefinition> attributes) {
        this.parent.getEntityType().setAttributeDefinitions(attributes);
        return RestUtils.persist(this.parent);
    }
}
