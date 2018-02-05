/*******************************************************************************
 * Copyright (c) 2012-2014 Contributors to the Eclipse Foundation
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
