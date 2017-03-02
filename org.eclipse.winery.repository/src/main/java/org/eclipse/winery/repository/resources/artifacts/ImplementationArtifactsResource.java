/*******************************************************************************
 * Copyright (c) 2012-2013,2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.artifacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact;
import org.eclipse.winery.repository.resources.INodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationResource;

/**
 * ImplementationArtifact instead of TImplementationArtifact has to be used
 * because of difference in the XSD at tImplementationArtifacts vs.
 * tDeploymentArtifacts
 */
public class ImplementationArtifactsResource extends GenericArtifactsResource<ImplementationArtifactResource, ImplementationArtifact> {

	private List<ImplementationArtifact> implementationArtifacts;


	public ImplementationArtifactsResource(List<ImplementationArtifact> implementationArtifact, INodeTypeImplementationResourceOrRelationshipTypeImplementationResource res) {
		super(ImplementationArtifactResource.class, ImplementationArtifact.class, implementationArtifact, res);
		this.implementationArtifacts = implementationArtifact;
	}

	/**
	 * @return a cast to TNodeTypeImplementationResource of the parent of this
	 *         resource.
	 */
	protected NodeTypeImplementationResource getNTI() {
		return (NodeTypeImplementationResource) this.res;
	}

	/**
	 * @return a cast to TNodeTypeImplementationResource of the parent of this
	 *         resource.
	 */
	protected RelationshipTypeImplementationResource getRTI() {
		return (RelationshipTypeImplementationResource) this.res;
	}

	@Override
	public Collection<ImplementationArtifactResource> getAllArtifactResources() {
		Collection<ImplementationArtifactResource> res = new ArrayList<>(this.implementationArtifacts.size());
		for (ImplementationArtifact da : this.implementationArtifacts) {
			ImplementationArtifactResource r = new ImplementationArtifactResource(da, this.implementationArtifacts, this.res);
			res.add(r);
		}
		return res;
	}

	@Override
	public String getId(ImplementationArtifact entity) {
		return entity.getName();
	}
}
