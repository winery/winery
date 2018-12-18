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
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TRelationMapping;
import org.eclipse.winery.repository.rest.resources.apiData.RelationMappingApiData;

public class RelationMappingsResource extends AbstractPrmMappingsResource {

    public RelationMappingsResource(PatternRefinementModelResource res, TPatternRefinementModel.TRelationMappings relationMappings) {
        super(res);
        this.mappings = relationMappings.getRelationMapping();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    /*
      We need to use another Class since the JSON representation cannot resolve the ids to the <code>detectorNode</code> and <code>refinementNode</code>.
      Therefore, we do it manually.
     */
    public List<TRelationMapping> addRelationMappingFromApi(RelationMappingApiData mapping) {
        TNodeTemplate detectorNode = this.getDetectorNodeTemplate(mapping.detectorNode);
        TNodeTemplate refinementNode = this.getRefinementNodeTemplate(mapping.refinementNode);
        return (List<TRelationMapping>) this.addMapping(mapping.createTRelationMapping(detectorNode, refinementNode));
    }
}
