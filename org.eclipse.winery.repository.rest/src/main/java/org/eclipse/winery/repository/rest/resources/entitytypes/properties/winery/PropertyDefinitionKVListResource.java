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
package org.eclipse.winery.repository.rest.resources.entitytypes.properties.winery;

import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitions;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.EntityTypeResource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Supports Winery's k/v properties introducing sub resources
 * "PropertyDefinition", which defines <em>one</em> property
 */
public class PropertyDefinitionKVListResource extends EntityWithIdCollectionResource<PropertyDefinitionKVResource, PropertyDefinitionKV> {

    public PropertyDefinitionKVListResource(EntityTypeResource res, PropertyDefinitions list) {
        super(PropertyDefinitionKVResource.class, PropertyDefinitionKV.class, list.getPropertyDefinitionKVs(), res);
    }

    @Override
    public String getId(PropertyDefinitionKV entity) {
        return entity.getKey();
    }

    @Override
    @Path("{id}/")
    public PropertyDefinitionKVResource getEntityResource(@PathParam("id") String id) {
        return this.getEntityResourceFromEncodedId(id);
    }

}
