/********************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.Path;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.AttributeMapping;
import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TStayMapping;
import org.eclipse.winery.repository.rest.resources._support.AbstractRefinementModelResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.TopologyTemplateResource;

public class PatternRefinementModelResource extends AbstractRefinementModelResource {

    public PatternRefinementModelResource(DefinitionsChildId id) {
        super(id);
    }

    @Override
    protected TPatternRefinementModel createNewElement() {
        return new TPatternRefinementModel();
    }

    public TPatternRefinementModel getTRefinementModel() {
        return (TPatternRefinementModel) this.getElement();
    }

    @Path("refinementstructure")
    public TopologyTemplateResource getRefinementTopology() {
        return new TopologyTemplateResource(this, this.getTRefinementModel().getRefinementTopology(), REFINEMENT_TOPOLOGY);
    }

    @Path("attributemappings")
    public AttributeMappingsResource getPropertyMappings() {
        List<AttributeMapping> propertyMappings = this.getTRefinementModel().getAttributeMappings();

        if (Objects.isNull(propertyMappings)) {
            propertyMappings = new ArrayList<>();
            this.getTRefinementModel().setAttributeMappings(propertyMappings);
        }

        return new AttributeMappingsResource(this, propertyMappings);
    }

    @Path("staymappings")
    public StayMappingsResource getStayMappings() {
        List<TStayMapping> stayMappings = this.getTRefinementModel().getStayMappings();

        if (Objects.isNull(stayMappings)) {
            stayMappings = new ArrayList<>();
            this.getTRefinementModel().setStayMappings(stayMappings);
        }

        return new StayMappingsResource(this, stayMappings);
    }
}
