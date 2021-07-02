/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.refinementmodels;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.extensions.OTBehaviorPatternMapping;
import org.eclipse.winery.repository.rest.resources._support.AbstractRefinementModelMappingsResource;
import org.eclipse.winery.repository.rest.resources._support.AbstractRefinementModelResource;
import org.eclipse.winery.repository.rest.resources.apiData.PrmBehaviorPatternMappingApiData;

public class BehaviorPatternMappingsResource extends AbstractRefinementModelMappingsResource<OTBehaviorPatternMapping> {

    public BehaviorPatternMappingsResource(AbstractRefinementModelResource res, List<OTBehaviorPatternMapping> behaviorPatternMappings) {
        super(res);
        this.mappings = behaviorPatternMappings;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<OTBehaviorPatternMapping> addPropertyMappingFromApi(PrmBehaviorPatternMappingApiData mapping) {
        TEntityTemplate detectorElement = this.res.getDetectorResource().getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
            .filter(entityTemplate -> entityTemplate.getId().equals(mapping.detectorElement))
            .findFirst()
            .orElse(null);
        TEntityTemplate refinementElement = this.res.getRefinementTopologyResource().getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
            .filter(entityTemplate -> entityTemplate.getId().equals(mapping.refinementElement))
            .findFirst()
            .orElse(null);
        return this.addMapping(mapping.createBehaviorPatternMapping(
            detectorElement,
            mapping.behaviorPattern,
            refinementElement
        ));
    }
}
