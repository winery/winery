/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Nico Rusam and Alexander Stifel - HAL support
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.INodeTemplateResourceOrNodeTypeImplementationResource;
import org.eclipse.winery.repository.resources.INodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources.artifacts.DeploymentArtifactsResource;
import org.eclipse.winery.repository.resources.artifacts.ImplementationArtifactsResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.EntityTypeImplementationResource;

import com.theoryinpractise.halbuilder.api.Representation;

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
	 * Even if both node type implementations and relationship type
	 * implementations have implementation artifacts, there is no common
	 * supertype. To avoid endless casts, we just implement the method here
	 * 
	 * @return
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
	 * Only NodeTypes have deployment artifacts, not RelationshipType.
	 * Therefore, this method is declared in
	 * {@link NodeTypeImplementationResource} and not in
	 * {@link EntityTypeImplementationResource}
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
	
	@Override
	protected void copyIdToFields() {
		this.getNTI().setTargetNamespace(this.getId().getNamespace().getDecoded());
		this.getNTI().setName(this.getId().getXmlId().getDecoded());
	}
	
	@Override
	public QName getType() {
		return this.getNTI().getNodeType();
	}
	
	@Override
	public Response setType(QName type) {
		this.getNTI().setNodeType(type);
		return BackendUtils.persist(this);
	}
	
	@Override
	public Response setType(String typeStr) {
		QName type = QName.valueOf(typeStr);
		return this.setType(type);
	}
	
	@Override
	protected Representation fillHALRepresentation(Representation res) {
		//@formatter:off

		res = res.withLink("implementationartifacts", "implementationartifacts")
				.withLink("deploymentartifacts", "deploymentartifacts");

		//@formatter:on
		
		return res;
	}
}
