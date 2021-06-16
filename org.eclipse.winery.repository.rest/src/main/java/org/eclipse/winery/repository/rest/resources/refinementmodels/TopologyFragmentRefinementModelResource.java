/*******************************************************************************
 * Copyright (c) 2018-2021 Contributors to the Eclipse Foundation
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.substitution.refinement.PermutationGenerator;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMappingType;
import org.eclipse.winery.model.tosca.extensions.OTBehaviorPatternMapping;
import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.model.tosca.extensions.kvproperties.OTPropertyKV;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractRefinementModelResource;
import org.eclipse.winery.repository.rest.resources.apiData.PermutationsResponse;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.RefinementTopologyTemplateResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.TopologyTemplateResource;

public class TopologyFragmentRefinementModelResource extends AbstractRefinementModelResource {

    public TopologyFragmentRefinementModelResource(DefinitionsChildId id) {
        super(id);
        this.mappingTypes = new
            ArrayList<>(Arrays.asList("PermutationMapping", "RelationshipMapping", "DeploymentArtifactMapping",
            "AttributeMapping", "StayMapping", "BehaviorPatternMapping"));
    }

    @Override
    protected OTPatternRefinementModel createNewElement() {
        return new OTPatternRefinementModel(new OTPatternRefinementModel.Builder());
    }

    public OTTopologyFragmentRefinementModel getTRefinementModel() {
        return (OTTopologyFragmentRefinementModel) this.getElement();
    }

    @Path("refinementstructure")
    public TopologyTemplateResource getRefinementTopologyResource() {
        return new TopologyTemplateResource(this, this.getTRefinementModel().getRefinementTopology(), REFINEMENT_TOPOLOGY);
    }

    @PUT
    @Path("graphicPrmTopology")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
            if (relTemplate.getType().getLocalPart().startsWith("PermutationMapping")) {
                this.getPermutationMappings().addMapping(new OTPermutationMapping(new OTPermutationMapping.Builder(mappingId)
                    .setDetectorElement(relTemplate.getSourceElement().getRef())
                    .setRefinementElement(relTemplate.getTargetElement().getRef())
                ));
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
            if (relTemplate.getType().getLocalPart().startsWith("StayMapping")) {
                this.getStayMappings().addMapping(new OTStayMapping(new OTStayMapping.Builder(mappingId)
                    .setDetectorElement(relTemplate.getSourceElement().getRef())
                    .setRefinementElement(relTemplate.getTargetElement().getRef())
                ));
            }
            if (relTemplate.getType().getLocalPart().startsWith("AttributeMapping")) {
                Map<String, String> propertiesMap = ((TEntityTemplate.WineryKVProperties) relTemplate.getProperties()).getKVProperties();
                this.getAttributeMappings().addMapping(new OTAttributeMapping(new OTAttributeMapping.Builder(mappingId)
                    .setDetectorElement(relTemplate.getSourceElement().getRef())
                    .setRefinementElement(relTemplate.getTargetElement().getRef())
                    .setType(OTAttributeMappingType.fromValue(propertiesMap.get("type")))
                    .setDetectorProperty(propertiesMap.get("detectorProperty"))
                    .setRefinementProperty(propertiesMap.get("refinementProperty"))
                ));
            }
            if (relTemplate.getType().getLocalPart().startsWith("DeploymentArtifactMapping")) {
                Map<String, String> propertiesMap = ((TEntityTemplate.WineryKVProperties) relTemplate.getProperties()).getKVProperties();
                this.getDeploymentArtifactMappings().addMapping(new OTDeploymentArtifactMapping(new OTDeploymentArtifactMapping.Builder(mappingId)
                    .setDetectorElement(relTemplate.getSourceElement().getRef())
                    .setRefinementElement(relTemplate.getTargetElement().getRef())
                    .setArtifactType(QName.valueOf(propertiesMap.get("requiredDeploymentArtifactType")))
                ));
            }
            if (relTemplate.getType().getLocalPart().startsWith("BehaviorPatternMapping")) {
                Map<String, String> propertiesMap = ((TEntityTemplate.WineryKVProperties) relTemplate.getProperties()).getKVProperties();
                ((PatternRefinementModelResource) this).getBehaviorPatternMappings()
                    .addMapping(new OTBehaviorPatternMapping(new OTBehaviorPatternMapping.Builder(mappingId)
                        .setDetectorElement(relTemplate.getSourceElement().getRef())
                        .setRefinementElement(relTemplate.getTargetElement().getRef())
                        .setProperty(new OTPropertyKV(propertiesMap.get("refinementProperty"), propertiesMap.get("refinementPropertyValue")))
                        .setBehaviorPattern(propertiesMap.get("behaviorPattern"))
                    ));
            }
        }
        return new RefinementTopologyTemplateResource(this, this.getTRefinementModel(), GRAFIC_PRM_MODEL);
    }

    @Path("attributemappings")
    public AttributeMappingsResource getAttributeMappings() {
        List<OTAttributeMapping> propertyMappings = this.getTRefinementModel().getAttributeMappings();

        if (Objects.isNull(propertyMappings)) {
            propertyMappings = new ArrayList<>();
            this.getTRefinementModel().setAttributeMappings(propertyMappings);
        }

        return new AttributeMappingsResource(this, propertyMappings);
    }

    @Path("staymappings")
    public StayMappingsResource getStayMappings() {
        List<OTStayMapping> stayMappings = this.getTRefinementModel().getStayMappings();

        if (Objects.isNull(stayMappings)) {
            stayMappings = new ArrayList<>();
            this.getTRefinementModel().setStayMappings(stayMappings);
        }

        return new StayMappingsResource(this, stayMappings);
    }

    @Path("deploymentartifactmappings")
    public DeploymentArtifactMappingsResource getDeploymentArtifactMappings() {
        List<OTDeploymentArtifactMapping> artifactMappings = this.getTRefinementModel().getDeploymentArtifactMappings();

        if (Objects.isNull(artifactMappings)) {
            artifactMappings = new ArrayList<>();
            this.getTRefinementModel().setDeploymentArtifactMappings(artifactMappings);
        }

        return new DeploymentArtifactMappingsResource(this, artifactMappings);
    }

    @Path("permutationmappings")
    public PermutationMappingsResource getPermutationMappings() {
        List<OTPermutationMapping> permutationMappings = this.getTRefinementModel().getPermutationMappings();

        if (Objects.isNull(permutationMappings)) {
            permutationMappings = new ArrayList<>();
            this.getTRefinementModel().setPermutationMappings(permutationMappings);
        }
        return new PermutationMappingsResource(this, permutationMappings);
    }

    @Path("generatePermutations")
    @POST
    public PermutationsResponse generatePermutations() {
        PermutationsResponse permutationsResponse = new PermutationsResponse();

        PermutationGenerator permutationGenerator = new PermutationGenerator();
        try {
            permutationsResponse.setPermutations(permutationGenerator.generatePermutations(this.getTRefinementModel()));
            permutationsResponse.setMutable(true);
        } catch (Exception e) {
            permutationsResponse.setError(permutationGenerator.getMutabilityErrorReason());
        }

        return permutationsResponse;
    }

    @Path("checkMutability")
    @GET
    public PermutationsResponse getMutability() {
        PermutationsResponse permutationsResponse = new PermutationsResponse();

        PermutationGenerator permutationGenerator = new PermutationGenerator();
        permutationsResponse.setMutable(permutationGenerator.checkMutability(this.getTRefinementModel()));
        permutationsResponse.setError(permutationGenerator.getMutabilityErrorReason());

        return permutationsResponse;
    }
}
