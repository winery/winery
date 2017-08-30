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
package org.eclipse.winery.repository.rest.resources._support.collections;

import javax.ws.rs.core.Response;

import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;

public class CollectionsHelper {

	private CollectionsHelper() {
	}

	/**
	 * @param resource        the resource to be persisted
	 * @param idDetermination the object to use to determine the id of the entity
	 * @param entity          the entity that was persisted. Used to determine the id
	 * @param isPost          true if post, false if put
	 * @return the new id id of the resource
	 */
	public static <X> Response persist(IPersistable resource, IIdDetermination<X> idDetermination, X entity, boolean isPost) {
		Response.ResponseBuilder res = RestUtils.persistWithResponseBuilder(resource);
		if (isPost) {
			res = res.status(201);
			String id = idDetermination.getId(entity);
			res = res.entity(id);
		} else {
			res = res.status(204);
		}
		return res.build();
	}
}
