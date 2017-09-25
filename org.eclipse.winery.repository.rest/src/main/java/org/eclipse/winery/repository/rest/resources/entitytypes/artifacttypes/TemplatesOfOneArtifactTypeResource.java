/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.artifacttypes;

import java.util.Collection;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.entitytypes.TemplatesOfOneType;

public class TemplatesOfOneArtifactTypeResource extends TemplatesOfOneType {

	private ArtifactTypeId artifactTypeId;

	/**
	 * Resource returns all templates/implementations of the given artifact type
	 * @param artifactTypeId the Id of the artifact type
	 */
	public TemplatesOfOneArtifactTypeResource(ArtifactTypeId artifactTypeId) {
		this.artifactTypeId = artifactTypeId;
	}

	@Override
	public Collection<ArtifactTemplateId> getAllImplementations() {
		return RepositoryFactory.getRepository().getAllElementsReferencingGivenType(ArtifactTemplateId.class, this.artifactTypeId.getQName());
	}
}
