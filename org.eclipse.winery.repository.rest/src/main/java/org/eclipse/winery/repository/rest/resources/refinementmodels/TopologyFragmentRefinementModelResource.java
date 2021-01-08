/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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
import java.util.List;
import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.winery.model.adaptation.substitution.refinement.PermutationGenerator;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.rest.resources._support.AbstractRefinementModelResource;
import org.eclipse.winery.repository.rest.resources.apiData.PermutationsResponse;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.TopologyTemplateResource;

public class TopologyFragmentRefinementModelResource extends AbstractRefinementModelResource {

    public TopologyFragmentRefinementModelResource(DefinitionsChildId id) {
        super(id);
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

    @Path("attributemappings")
    public AttributeMappingsResource getPropertyMappings() {
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
