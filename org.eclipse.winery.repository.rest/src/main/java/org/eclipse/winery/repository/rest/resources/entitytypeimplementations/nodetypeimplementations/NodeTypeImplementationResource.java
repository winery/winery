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

import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.repository.rest.resources._support.INodeTemplateResourceOrNodeTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources._support.INodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.artifacts.DeploymentArtifactsResource;
import org.eclipse.winery.repository.rest.resources.artifacts.ImplementationArtifactsResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.EntityTypeImplementationResource;

import javax.ws.rs.Path;

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
        TImplementationArtifacts implementationArtifacts;
        implementationArtifacts = this.getNTI().getImplementationArtifacts();
        if (implementationArtifacts == null) {
            implementationArtifacts = new TImplementationArtifacts();
            this.getNTI().setImplementationArtifacts(implementationArtifacts);
        }
        return new ImplementationArtifactsResource(implementationArtifacts.getImplementationArtifact(), this);
    }

    /**
     * Only NodeTypes have deployment artifacts, not RelationshipType. Therefore, this method is declared in {@link
     * NodeTypeImplementationResource} and not in {@link EntityTypeImplementationResource}
     */
    @Path("deploymentartifacts/")
    public DeploymentArtifactsResource getDeploymentArtifacts() {
        TDeploymentArtifacts deploymentArtifacts;
        deploymentArtifacts = this.getNTI().getDeploymentArtifacts();
        if (deploymentArtifacts == null) {
            deploymentArtifacts = new TDeploymentArtifacts();
            this.getNTI().setDeploymentArtifacts(deploymentArtifacts);
        }
        return new DeploymentArtifactsResource(deploymentArtifacts.getDeploymentArtifact(), this);
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TNodeTypeImplementation();
    }
}
