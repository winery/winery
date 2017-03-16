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
 *******************************************************************************/
package org.eclipse.winery.repository.resources.artifacts;

import java.util.List;
import java.util.Objects;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.IIdDetermination;

public class ImplementationArtifactResource extends GenericArtifactResource<ImplementationArtifact> {

	private ImplementationArtifact a;


	public ImplementationArtifactResource(String artifactId, List<ImplementationArtifact> implementationArtifacts, IPersistable res) {
		this(ImplementationArtifactResource.getImplementationArtifact(artifactId, implementationArtifacts), implementationArtifacts, res);
	}

	public ImplementationArtifactResource(IIdDetermination<ImplementationArtifact> idDetermination, ImplementationArtifact o, int idx, List<ImplementationArtifact> list, IPersistable res) {
		super(idDetermination, o, idx, list, res);
		this.a = o;
	}

	public ImplementationArtifactResource(ImplementationArtifact a, List<ImplementationArtifact> implementationArtifacts, IPersistable res) {
		this(new IIdDetermination<ImplementationArtifact>() {

			@Override
			public String getId(ImplementationArtifact e) {
				return e.getName();
			}
		}, a, implementationArtifacts.indexOf(a), implementationArtifacts, res);
	}

	/**
	 * Converts the given artifactId to an ImplementArtifact.
	 *
	 * <em>SIDE EFFECT</em> Adds it to the implementationArtifacts list if it
	 * does not yet exist.
	 */
	private static ImplementationArtifact getImplementationArtifact(String artifactId, List<ImplementationArtifact> implementationArtifacts) {
		Objects.requireNonNull(artifactId);
		Objects.requireNonNull(implementationArtifacts);
		for (ImplementationArtifact ia : implementationArtifacts) {
			// ia.getName() might be null as TOSCA COS01 does not forsee a name on the implementation artifact
			// Therefore, we begin the test with "artifactId"
			if (artifactId.equals(ia.getName())) {
				return ia;
			}
		}
		// IA does not exist in list
		ImplementationArtifact ia = new ImplementationArtifact();
		ia.setName(artifactId);
		implementationArtifacts.add(ia);
		return ia;
	}

	public ImplementationArtifact getImplementationArtifact() {
		return this.a;
	}

	@Override
	public void setArtifactType(ArtifactTypeId artifactTypeId) {
		this.getImplementationArtifact().setArtifactType(artifactTypeId.getQName());
		BackendUtils.persist(this.res);
	}

	@Override
	public void setArtifactTemplate(ArtifactTemplateId artifactTemplateId) {
		this.getImplementationArtifact().setArtifactRef(artifactTemplateId.getQName());
		BackendUtils.persist(this.res);
	}

	@Override
	public ImplementationArtifact getA() {
		return this.a;
	}

}
