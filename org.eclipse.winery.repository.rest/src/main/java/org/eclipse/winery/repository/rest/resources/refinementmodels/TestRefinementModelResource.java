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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.model.tosca.extensions.OTTestRefinementModel;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractRefinementModelResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.RefinementTopologyTemplateResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.TopologyTemplateResource;

public class TestRefinementModelResource extends AbstractRefinementModelResource {

    public TestRefinementModelResource(DefinitionsChildId id) {
        super(id);
        this.mappingTypes = new
            ArrayList<>(Arrays.asList("RelationshipMapping"));
    }

    @Override
    public OTTestRefinementModel getTRefinementModel() {
        return (OTTestRefinementModel) this.getElement();
    }

    @Override
    protected OTTestRefinementModel createNewElement() {
        return new OTTestRefinementModel(new OTTestRefinementModel.Builder());
    }

    @Path("testfragment")
    public TopologyTemplateResource getRefinementTopologyResource() {
        return new TopologyTemplateResource(this, this.getTRefinementModel().getRefinementTopology(), REFINEMENT_TOPOLOGY);
    }

    @PUT
    @Path("graphicPrmTopology")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public TopologyTemplateResource savePrmMappingTopology(TTopologyTemplate topologyTemplate) {
        this.getTRefinementModel().resetAllMappings();
        RestUtils.persist(this);
        for (TRelationshipTemplate relTemplate : topologyTemplate.getRelationshipTemplates()) {
            String mappingId;
            // necessary for topologymodeler to create the IDs
            if (relTemplate.getId().startsWith("con")) {
                mappingId = relTemplate.getId().substring(relTemplate.getId().indexOf("_") + 1);
            } else {
                mappingId = relTemplate.getId();
            }
            if (relTemplate.getType().getLocalPart().startsWith("RelationshipMapping")) {
                Map<String, String> propertiesMap = ((TEntityTemplate.WineryKVProperties) relTemplate.getProperties()).getKVProperties();
                this.getRelationMappings().addMapping(new OTRelationMapping(new OTRelationMapping.Builder(mappingId)
                    .setDetectorElement(relTemplate.getSourceElement().getRef())
                    .setRefinementElement(relTemplate.getTargetElement().getRef())
                    .setDirection(OTRelationDirection.fromValue(propertiesMap.get("direction")))
                    .setRelationType(QName.valueOf(propertiesMap.get("applicableRelationshipType")))
                    .setValidSourceOrTarget(QName.valueOf(propertiesMap.get("validEndpointType")))));
            }
        }
        return new RefinementTopologyTemplateResource(this, this.getTRefinementModel(), GRAFIC_PRM_MODEL);
    }
}
