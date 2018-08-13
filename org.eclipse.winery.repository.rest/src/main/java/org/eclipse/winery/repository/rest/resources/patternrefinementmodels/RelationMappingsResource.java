/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.patternrefinementmodels;

import java.util.List;
import java.util.Objects;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TRelationMapping;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;

public class RelationMappingsResource {

    private final AbstractComponentInstanceResource res;
    private List<TRelationMapping> relationMappings;

    public RelationMappingsResource(AbstractComponentInstanceResource res, TPatternRefinementModel.TRelationMappings relationMappings) {
        this.res = res;

        TPatternRefinementModel.TRelationMappings relMaps = Objects.isNull(relationMappings) ? new TPatternRefinementModel.TRelationMappings() : relationMappings;
        this.relationMappings = relMaps.getRelationMapping();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TRelationMapping> get() {
        return this.relationMappings;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<TRelationMapping> addPatternRefinement(TRelationMapping mapping) {
        // to update an element, just remove the old one from the list and add it again
        this.relationMappings.remove(mapping);
        this.relationMappings.add(mapping);
        RestUtils.persist(this.res);
        return this.relationMappings;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<TRelationMapping> removePatternRefinement(TRelationMapping mapping) {
        this.relationMappings.remove(mapping);
        RestUtils.persist(this.res);
        return this.relationMappings;
    }
}
