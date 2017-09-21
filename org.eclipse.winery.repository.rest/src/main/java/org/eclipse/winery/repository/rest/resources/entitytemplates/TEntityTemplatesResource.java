/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytemplates;

import java.util.List;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdResource;

/**
 * This resource models the list of TEntityTemplates
 */
public abstract class TEntityTemplatesResource<R extends EntityWithIdResource<T>, T extends TEntityTemplate> extends EntityWithIdCollectionResource<R, T> {

	public TEntityTemplatesResource(Class<R> entityResourceTClazz, Class<T> entityTClazz, List<T> list, IPersistable res) {
		super(entityResourceTClazz, entityTClazz, list, res);
	}

}
