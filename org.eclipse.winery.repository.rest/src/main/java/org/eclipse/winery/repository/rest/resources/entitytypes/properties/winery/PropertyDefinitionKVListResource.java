/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.properties.winery;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.model.tosca.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.repository.rest.resources.EntityTypeResource;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;

/**
 * Supports Winery's k/v properties introducing sub resources
 * "PropertyDefinition", which defines <em>one</em> property
 */
public class PropertyDefinitionKVListResource extends EntityWithIdCollectionResource<PropertyDefinitionKVResource, PropertyDefinitionKV> {

	public PropertyDefinitionKVListResource(EntityTypeResource res, PropertyDefinitionKVList list) {
		super(PropertyDefinitionKVResource.class, PropertyDefinitionKV.class, list, res);
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
