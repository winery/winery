/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.artifacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.repository.rest.resources._support.INodeTemplateResourceOrNodeTypeImplementationResource;

import org.eclipse.jdt.annotation.NonNull;

public class DeploymentArtifactsResource extends GenericArtifactsResource<DeploymentArtifactResource, TDeploymentArtifact> {

    private final List<TDeploymentArtifact> deploymentArtifacts;

    public DeploymentArtifactsResource(TNodeTemplate nodeTemplate, INodeTemplateResourceOrNodeTypeImplementationResource res) {
        this(DeploymentArtifactsResource.getDeploymentArtifacts(nodeTemplate), res);
    }

    public DeploymentArtifactsResource(List<TDeploymentArtifact> deploymentArtifact, INodeTemplateResourceOrNodeTypeImplementationResource res) {
        super(DeploymentArtifactResource.class, TDeploymentArtifact.class, deploymentArtifact, res);
        this.deploymentArtifacts = deploymentArtifact;
    }

    /**
     * Determines the list of DAs belonging to the given node template.
     * <p>
     * If no DAs are existing, an empty list is created in the model for the node template
     */
    private static List<TDeploymentArtifact> getDeploymentArtifacts(TNodeTemplate nodeTemplate) {
        List<TDeploymentArtifact> deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
        final List<TDeploymentArtifact> res;
        if (deploymentArtifacts == null) {
            deploymentArtifacts = new ArrayList<>();
            nodeTemplate.setDeploymentArtifacts(deploymentArtifacts);
        }
        res = deploymentArtifacts;
        return res;
    }

    @Override
    public Collection<DeploymentArtifactResource> getAllArtifactResources() {
        Collection<DeploymentArtifactResource> res = new ArrayList<>(this.deploymentArtifacts.size());
        for (TDeploymentArtifact da : this.deploymentArtifacts) {
            DeploymentArtifactResource r = new DeploymentArtifactResource(da, this.deploymentArtifacts, this.res);
            res.add(r);
        }
        return res;
    }

    @NonNull
    public List<TDeploymentArtifact> getDeploymentArtifacts() {
        return Objects.nonNull(this.deploymentArtifacts) ? this.deploymentArtifacts : new ArrayList<>();
    }

    @Override
    public String getId(TDeploymentArtifact entity) {
        return entity.getName();
    }

    @Override
    @Path("{id}/")
    public DeploymentArtifactResource getEntityResource(@PathParam("id") String id) {
        return this.getEntityResourceFromEncodedId(id);
    }
}
