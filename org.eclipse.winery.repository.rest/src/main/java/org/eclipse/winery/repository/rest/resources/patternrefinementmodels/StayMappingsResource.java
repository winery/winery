/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TPrmModelElementType;
import org.eclipse.winery.model.tosca.TStayMapping;
import org.eclipse.winery.repository.rest.resources._support.AbstractRefinementModelMappingsResource;
import org.eclipse.winery.repository.rest.resources.apiData.PrmStayMappingApiData;

public class StayMappingsResource extends AbstractRefinementModelMappingsResource {

    public StayMappingsResource(PatternRefinementModelResource res, List<TStayMapping> stayMappings) {
        super(res);
        this.mappings = stayMappings;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<TStayMapping> addPropertyMappingFromApi(PrmStayMappingApiData mapping) {
        TEntityTemplate detectorElement, refinementNode;

        if (mapping.modelElementType == TPrmModelElementType.NODE) {
            detectorElement = this.res.getDetector().getComponentInstanceJSON().getNodeTemplate(mapping.detectorNode);
            refinementNode = this.res.getRefinementTopology().getComponentInstanceJSON().getNodeTemplate(mapping.refinementNode);
        } else {
            detectorElement = this.res.getDetector().getComponentInstanceJSON().getRelationshipTemplate(mapping.detectorNode);
            refinementNode = this.res.getRefinementTopology().getComponentInstanceJSON().getRelationshipTemplate(mapping.refinementNode);
        }

        return (List<TStayMapping>) this.addMapping(mapping.createTPrmStayMapping(detectorElement, refinementNode));
    }
}
