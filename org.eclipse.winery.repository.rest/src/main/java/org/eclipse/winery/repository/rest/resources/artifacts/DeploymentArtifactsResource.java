/*******************************************************************************
 * Copyright (c) 2012-2013,2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.artifacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.repository.rest.resources.INodeTemplateResourceOrNodeTypeImplementationResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentArtifactsResource extends GenericArtifactsResource<DeploymentArtifactResource, TDeploymentArtifact> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentArtifactsResource.class);

	private List<TDeploymentArtifact> deploymentArtifacts;


	public DeploymentArtifactsResource(TNodeTemplate nodeTemplate, INodeTemplateResourceOrNodeTypeImplementationResource res) {
		this(DeploymentArtifactsResource.getDeploymentArtifacts(nodeTemplate), res);
	}

	public DeploymentArtifactsResource(List<TDeploymentArtifact> deploymentArtifact, INodeTemplateResourceOrNodeTypeImplementationResource res) {
		super(DeploymentArtifactResource.class, TDeploymentArtifact.class, deploymentArtifact, res);
		this.deploymentArtifacts = deploymentArtifact;
	}

	/**
	 * Determines the list of DAs belonging to the given node template.
	 *
	 * If no DAs are existing, an empty list is created in the model for the
	 * node template
	 */
	private static List<TDeploymentArtifact> getDeploymentArtifacts(TNodeTemplate nodeTemplate) {
		TDeploymentArtifacts deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
		final List<TDeploymentArtifact> res;
		if (deploymentArtifacts == null) {
			deploymentArtifacts = new TDeploymentArtifacts();
			nodeTemplate.setDeploymentArtifacts(deploymentArtifacts);
		}
		res = deploymentArtifacts.getDeploymentArtifact();
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

	@Override
	public String getId(TDeploymentArtifact entity) {
		return entity.getName();
	}

}
