/**
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.rest.resources.entitytypeimplementations.relationshiptypeimplementations;

import javax.ws.rs.Path;

import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.repository.rest.resources.INodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.rest.resources.artifacts.ImplementationArtifactsResource;
import org.eclipse.winery.repository.rest.resources.entitytypeimplementations.EntityTypeImplementationResource;

public class RelationshipTypeImplementationResource extends EntityTypeImplementationResource implements INodeTypeImplementationResourceOrRelationshipTypeImplementationResource {

	public RelationshipTypeImplementationResource(RelationshipTypeImplementationId id) {
		super(id);
	}

	public TRelationshipTypeImplementation getRTI() {
		return (TRelationshipTypeImplementation) this.getElement();
	}

	/**
	 * Even if both node type implementations and relationship type
	 * implementations have implementation artifacts, there is no common
	 * supertype. To avoid endless casts, we just implement the method here
	 */
	@Path("implementationartifacts/")
	public ImplementationArtifactsResource getImplementationArtifacts() {
		TImplementationArtifacts implementationArtifacts;
		implementationArtifacts = this.getRTI().getImplementationArtifacts();
		if (implementationArtifacts == null) {
			implementationArtifacts = new TImplementationArtifacts();
			this.getRTI().setImplementationArtifacts(implementationArtifacts);
		}
		return new ImplementationArtifactsResource(implementationArtifacts.getImplementationArtifact(), this);
	}

	@Override
	protected TExtensibleElements createNewElement() {
		return new TRelationshipTypeImplementation();
	}
}
