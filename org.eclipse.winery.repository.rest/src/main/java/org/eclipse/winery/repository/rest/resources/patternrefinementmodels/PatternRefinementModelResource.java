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

import java.io.IOException;
import java.util.Objects;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TPatternRefinementModel;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResourceContainingATopology;
import org.eclipse.winery.repository.rest.resources._support.IHasName;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.TopologyTemplateResource;

public class PatternRefinementModelResource extends AbstractComponentInstanceResourceContainingATopology implements IHasName {

    private static final String REFINEMENT_STRUCTURE = "refinement-structure";
    private static final String DETECTOR = "detector";

    public PatternRefinementModelResource(DefinitionsChildId id) {
        super(id);
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TPatternRefinementModel();
    }

    public TPatternRefinementModel getTPatternRefinementModel() {
        return (TPatternRefinementModel) this.getElement();
    }

    @Path("detector")
    public TopologyTemplateResource getDetector() {
        return new TopologyTemplateResource(this, this.getTPatternRefinementModel().getDetector(), DETECTOR);
    }

    @Path("refinementstructure")
    public TopologyTemplateResource getRefinementStructure() {
        return new TopologyTemplateResource(this, this.getTPatternRefinementModel().getRefinementStructure(), REFINEMENT_STRUCTURE);
    }

    @Path("relationmappings")
    public RelationMappingsResource getRelationMappings() {
        TPatternRefinementModel.TRelationMappings relationMappings = this.getTPatternRefinementModel().getRelationMappings();

        if (Objects.isNull(relationMappings)) {
            relationMappings = new TPatternRefinementModel.TRelationMappings();
            this.getTPatternRefinementModel().setRelationMappings(relationMappings);
        }

        return new RelationMappingsResource(this, relationMappings);
    }

    @Override
    public String getName() {
        String name = this.getTPatternRefinementModel().getName();
        if (name == null) {
            // place default
            name = this.getId().getXmlId().getDecoded();
        }
        return name;
    }

    @Override
    public Response setName(String name) {
        this.getTPatternRefinementModel().setName(name);
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
                this.getTPatternRefinementModel().setDetector(topologyTemplate);
                break;
            case REFINEMENT_STRUCTURE:
                this.getTPatternRefinementModel().setRefinementStructure(topologyTemplate);
                break;
            default:
                break;
        }
    }
}
