/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes.properties.winery;

import io.swagger.annotations.ApiOperation;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdResource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Models a definition of one property
 * <p>
 * This is NOT in line with CSPRD01, which forces one element of one type
 */
public class PropertyDefinitionKVResource extends EntityWithIdResource<PropertyDefinitionKV> {

    public PropertyDefinitionKVResource(IIdDetermination<PropertyDefinitionKV> idDetermination, PropertyDefinitionKV o, int idx, List<PropertyDefinitionKV> list, AbstractComponentInstanceResource res) {
        super(idDetermination, o, idx, list, res);
    }

    public PropertyDefinitionKVResource(IIdDetermination<PropertyDefinitionKV> idDetermination, PropertyDefinitionKV o, int idx, List<PropertyDefinitionKV> list, IPersistable res) {
        super(idDetermination, o, idx, list, res);
    }

    @GET
    @ApiOperation(value = "@return type is the 'id' of the type ('shortType'), not the full type name")
    @Path("type")
    public String getType() {
        return this.o.getType();
    }

    @PUT
    @ApiOperation(value = "@return type is the 'id' of the type ('shortType'), not the full type name")
    @Path("type")
    public Response setType(@FormParam("type") String type) {
        this.o.setType(type);
        return RestUtils.persist(this.res);
    }
}
