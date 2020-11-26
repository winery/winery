/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.apiData.converter.QNameConverter;

/**
 * Gets all templates/implementations of a type.
 * Returns the templates/implementations in JSON format see: getJSON method
 */
public abstract class TemplatesOfOneType {

    /**
     * returns a collection of a templates/implementations of a type
     * has to be implemented in the concrete class for examples see {@link org.eclipse.winery.repository.rest.resources.entitytypes.policytypes.TemplatesOfOnePolicyTypeResource} and
     * {@link org.eclipse.winery.repository.rest.resources.entitytypes.artifacttypes.TemplatesOfOneArtifactTypeResource}
     */
    protected abstract Collection<? extends DefinitionsChildId> getAllImplementations();

    /**
     * returns a list of all implementations of a type using the getAllImplementations method
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSON() {
        Collection<? extends DefinitionsChildId> allImplementations = this.getAllImplementations();
        List<QNameApiData> res = new ArrayList<>(allImplementations.size());
        QNameConverter adapter = new QNameConverter();
        for (DefinitionsChildId id : allImplementations) {
            res.add(adapter.marshal(id.getQName()));
        }
        return Response.ok().entity(res).build();
    }
}
