/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.repository.rest.resources._support.AbstractRefinementModelMappingsResource;
import org.eclipse.winery.repository.rest.resources.apiData.PrmStayMappingApiData;

public class StayMappingsResource extends AbstractRefinementModelMappingsResource<OTStayMapping> {

    public StayMappingsResource(TopologyFragmentRefinementModelResource res, List<OTStayMapping> stayMappings) {
        super(res);
        this.mappings = stayMappings;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<OTStayMapping> addPropertyMappingFromApi(PrmStayMappingApiData mapping) {
        TEntityTemplate detectorElement = this.res.getDetectorResource().getTopologyTempalte()
            .getNodeTemplateOrRelationshipTemplate().stream()
            .filter(element -> element.getId().equals(mapping.detectorElement))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
        TEntityTemplate refinementElement = this.res.getRefinementTopologyResource().getTopologyTempalte()
            .getNodeTemplateOrRelationshipTemplate().stream()
            .filter(element -> element.getId().equals(mapping.refinementElement))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);

        return this.addMapping(mapping.createOTPrmStayMapping(detectorElement, refinementElement));
    }
}
