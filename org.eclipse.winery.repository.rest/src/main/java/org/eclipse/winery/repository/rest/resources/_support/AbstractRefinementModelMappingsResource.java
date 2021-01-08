/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources._support;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.tosca.extensions.OTPrmMapping;
import org.eclipse.winery.repository.rest.RestUtils;

public abstract class AbstractRefinementModelMappingsResource<T extends OTPrmMapping> {

    protected final AbstractRefinementModelResource res;
    protected List<T> mappings;

    public AbstractRefinementModelMappingsResource(AbstractRefinementModelResource res) {
        this.res = res;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<T> get() {
        return this.mappings;
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<T> removePatternRefinement(@PathParam("id") String id) {
        this.mappings.removeIf(mapping -> mapping.getId().equals(id));
        RestUtils.persist(this.res);
        return this.mappings;
    }

    public List<T> addMapping(T mapping) {
        // to update an element, just remove the old one from the list and add it again
        this.mappings.remove(mapping);
        this.mappings.add(mapping);
        RestUtils.persist(this.res);
        return mappings;
    }
}
