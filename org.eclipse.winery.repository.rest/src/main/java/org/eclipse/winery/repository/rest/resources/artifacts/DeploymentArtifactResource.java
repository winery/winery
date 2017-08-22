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
package org.eclipse.winery.repository.rest.resources.artifacts;

import java.util.List;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;

public class DeploymentArtifactResource extends GenericArtifactResource<TDeploymentArtifact> {

	private final TDeploymentArtifact a;


	public DeploymentArtifactResource(String artifactId, List<TDeploymentArtifact> deploymentArtifacts, IPersistable res) {
		this(DeploymentArtifactResource.getDeploymentArtifact(artifactId, deploymentArtifacts), deploymentArtifacts, res);
	}

	public DeploymentArtifactResource(IIdDetermination<TDeploymentArtifact> idDetermination, TDeploymentArtifact o, int idx, List<TDeploymentArtifact> list, IPersistable res) {
		super(idDetermination, o, idx, list, res);
		this.a = o;
	}

	public DeploymentArtifactResource(TDeploymentArtifact deploymentArtifact, List<TDeploymentArtifact> deploymentArtifacts, IPersistable res) {
		this(new IIdDetermination<TDeploymentArtifact>() {

			@Override
			public String getId(TDeploymentArtifact e) {
				return e.getName();
			}
		}, deploymentArtifact, deploymentArtifacts.indexOf(deploymentArtifact), deploymentArtifacts, res);
	}

	/**
	 * Converts the given artifactId to an DeploymentArtifact.
	 *
	 * <em>SIDE EFFECT</em> Adds it to the DeploymentArtifacts list if it does
	 * not yet exist.
	 */
	private static TDeploymentArtifact getDeploymentArtifact(String artifactId, List<TDeploymentArtifact> deploymentArtifacts) {
		for (TDeploymentArtifact ia : deploymentArtifacts) {
			if (ia.getName().equals(artifactId)) {
				return ia;
			}
		}
		// DA does not exist in list
		TDeploymentArtifact ia = new TDeploymentArtifact();
		ia.setName(artifactId);
		deploymentArtifacts.add(ia);
		return ia;
	}

	public TDeploymentArtifact getDeploymentArtifact() {
		return this.a;
	}

	@Override
	public void setArtifactType(ArtifactTypeId artifactTypeId) {
		this.getDeploymentArtifact().setArtifactType(artifactTypeId.getQName());
		RestUtils.persist(this.res);
	}

	@Override
	public void setArtifactTemplate(ArtifactTemplateId artifactTemplateId) {
		this.getDeploymentArtifact().setArtifactRef(artifactTemplateId.getQName());
		RestUtils.persist(this.res);
	}

	@Override
	public TDeploymentArtifact getA() {
		return this.a;
	}

}
