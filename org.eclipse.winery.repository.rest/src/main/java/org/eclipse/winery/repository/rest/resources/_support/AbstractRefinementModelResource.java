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

package org.eclipse.winery.repository.rest.resources._support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TRefinementModel;
import org.eclipse.winery.model.tosca.TRelationMapping;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.refinementmodels.RelationMappingsResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.TopologyTemplateResource;

public abstract class AbstractRefinementModelResource extends AbstractComponentInstanceResourceContainingATopology implements IHasName {

    protected static final String REFINEMENT_TOPOLOGY = "refinement-structure";
    protected static final String DETECTOR = "detector";

    protected AbstractRefinementModelResource(DefinitionsChildId id) {
        super(id);
    }

    public abstract TRefinementModel getTRefinementModel();

    @Path("detector")
    public TopologyTemplateResource getDetector() {
        return new TopologyTemplateResource(this, this.getTRefinementModel().getDetector(), DETECTOR);
    }

    public abstract TopologyTemplateResource getRefinementTopology();

    @Path("relationmappings")
    public RelationMappingsResource getRelationMappings() {
        List<TRelationMapping> relationMappings = this.getTRefinementModel().getRelationMappings();

        if (Objects.isNull(relationMappings)) {
            relationMappings = new ArrayList<>();
            this.getTRefinementModel().setRelationMappings(relationMappings);
        }

        return new RelationMappingsResource(this, relationMappings);
    }

    @Override
    public String getName() {
        String name = this.getTRefinementModel().getName();
        if (name == null) {
            // place default
            name = this.getId().getXmlId().getDecoded();
        }
        return name;
    }

    @Override
    public Response setName(String name) {
        this.getTRefinementModel().setName(name);
        return RestUtils.persist(this);
    }

    @Override
    protected void synchronizeReferences() throws IOException {
        // no synchronization needed
    }

    @Override
    public void setTopology(TTopologyTemplate topologyTemplate, String type) {
        switch (type) {
            case DETECTOR:
                this.getTRefinementModel().setDetector(topologyTemplate);
                break;
            case REFINEMENT_TOPOLOGY:
                this.getTRefinementModel().setRefinementTopology(topologyTemplate);
                break;
            default:
                break;
        }
    }
}
