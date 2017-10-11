/**
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.rest.resources.entitytypeimplementations.nodetypeimplementations;

import javax.ws.rs.Path;

import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.repository.rest.resources.INodeTemplateResourceOrNodeTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.INodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
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
