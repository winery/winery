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
package org.eclipse.winery.repository.rest.resources._support.collections;

import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;

import javax.ws.rs.core.Response;

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
