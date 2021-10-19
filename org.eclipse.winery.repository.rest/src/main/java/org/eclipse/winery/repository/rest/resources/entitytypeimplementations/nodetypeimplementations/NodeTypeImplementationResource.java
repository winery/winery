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
package org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;

import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.repository.rest.resources._support.INodeTemplateResourceOrNodeTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources._support.INodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.artifacts.DeploymentArtifactsResource;
import org.eclipse.winery.repository.rest.resources.artifacts.ImplementationArtifactsResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.EntityTypeImplementationResource;

public class NodeTypeImplementationResource extends EntityTypeImplementationResource implements INodeTemplateResourceOrNodeTypeImplementationResource, INodeTypeImplementationResourceOrRelationshipTypeImplementationResource {

    public NodeTypeImplementationResource(NodeTypeImplementationId id) {
        super(id);
    }

    /**
     * public because of exporter
     */
    public TNodeTypeImplementation getNTI() {
        return (TNodeTypeImplementation) this.getElement();
    }

    /**
     * Even if both node type implementations and relationship type implementations have implementation artifacts, there
     * is no common supertype. To avoid endless casts, we just implement the method here
     */
    @Path("implementationartifacts/")
    public ImplementationArtifactsResource getImplementationArtifacts() {
        List<TImplementationArtifact> implementationArtifacts = this.getNTI().getImplementationArtifacts();
        if (implementationArtifacts == null) {
            implementationArtifacts = new ArrayList<>();
            this.getNTI().setImplementationArtifacts(implementationArtifacts);
        }
        return new ImplementationArtifactsResource(implementationArtifacts, this);
    }

    /**
     * Only NodeTypes have deployment artifacts, not RelationshipType. Therefore, this method is declared in {@link
     * NodeTypeImplementationResource} and not in {@link EntityTypeImplementationResource}
     */
    @Path("deploymentartifacts/")
    public DeploymentArtifactsResource getDeploymentArtifacts() {
        List<TDeploymentArtifact> deploymentArtifacts = this.getNTI().getDeploymentArtifacts();
        if (deploymentArtifacts == null) {
            deploymentArtifacts = new ArrayList<>();
            this.getNTI().setDeploymentArtifacts(deploymentArtifacts);
        }
        return new DeploymentArtifactsResource(deploymentArtifacts, this);
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TNodeTypeImplementation();
    }
}
