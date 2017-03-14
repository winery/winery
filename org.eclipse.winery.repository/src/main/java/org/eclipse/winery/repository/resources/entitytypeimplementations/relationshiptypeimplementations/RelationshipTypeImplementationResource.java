/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Tino Stadelmaier, Philipp Meyer - rename for id/namespace
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytypeimplementations.relationshiptypeimplementations;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.INodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources.artifacts.ImplementationArtifactsResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.EntityTypeImplementationResource;

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

	@Override
	public void copyIdToFields(TOSCAComponentId id) {
		this.getRTI().setTargetNamespace(id.getNamespace().getDecoded());
		this.getRTI().setName(id.getXmlId().getDecoded());
	}

	@Override
	public QName getType() {
		return this.getRTI().getRelationshipType();
	}

	@Override
	public Response setType(QName type) {
		this.getRTI().setRelationshipType(type);
		return BackendUtils.persist(this);
	}

	@Override
	public Response setType(String typeStr) {
		QName qname = QName.valueOf(typeStr);
		return this.setType(qname);
	}
}
