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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TRelationMapping;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.RelationMappingApiData;

import com.sun.jersey.api.NotFoundException;

public class RelationMappingsResource {

    private final PatternRefinementModelResource res;
    private List<TRelationMapping> relationMappings;

    public RelationMappingsResource(PatternRefinementModelResource res, TPatternRefinementModel.TRelationMappings relationMappings) {
        this.res = res;
        this.relationMappings = relationMappings.getRelationMapping();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TRelationMapping> get() {
        return this.relationMappings;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    /*
      We need to use another Class since the JSON representation cannot resolve the ids to the <code>detectorNode</code> and <code>refinementNode</code>.
      Therefore, we do it manually.
     */
    public List<TRelationMapping> addRelationMappingFromApi(RelationMappingApiData mapping) {
        TNodeTemplate detectorNode = this.res.getDetector().getComponentInstanceJSON().getNodeTemplates()
            .stream()
            .filter(nodeTemplate -> nodeTemplate.getId().equals(mapping.detectorNode))
            .findFirst()
            .orElseThrow(NotFoundException::new);
        TNodeTemplate refinementNode = this.res.getRefinementStructure().getComponentInstanceJSON().getNodeTemplates()
            .stream()
            .filter(nodeTemplate -> nodeTemplate.getId().equals(mapping.refinementNode))
            .findFirst()
            .orElseThrow(NotFoundException::new);

        return this.addRelationMapping(mapping.createTRelationMapping(detectorNode, refinementNode));
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TRelationMapping> removePatternRefinement(@PathParam("id") String id) {
        this.relationMappings.removeIf(mapping -> mapping.getId().equals(id));
        RestUtils.persist(this.res);
        return this.relationMappings;
    }

    public List<TRelationMapping> addRelationMapping(TRelationMapping mapping) {
        // to update an element, just remove the old one from the list and add it again
        this.relationMappings.remove(mapping);
        this.relationMappings.add(mapping);
        RestUtils.persist(this.res);
        return this.relationMappings;
    }
}
