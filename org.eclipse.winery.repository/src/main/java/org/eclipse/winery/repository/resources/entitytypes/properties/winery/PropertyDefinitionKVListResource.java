/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytypes.properties.winery;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.repository.resources.EntityTypeResource;
import org.eclipse.winery.repository.resources._support.collections.withid.EntityWithIdCollectionResource;

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
}
