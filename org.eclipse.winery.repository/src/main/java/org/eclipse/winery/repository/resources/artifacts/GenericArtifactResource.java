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

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.repository.resources.INodeTemplateResourceOrNodeTypeImplementationResourceOrRelationshipTypeImplementationResource;
import org.eclipse.winery.repository.resources._support.collections.withoutid.EntityWithoutIdResource;

/**
 * Currently no common things for deployment artifacts and implementation
 * artifacts as the data model also has no common ancestor (besides
 * TExensibleElement)
 */
public abstract class GenericArtifactResource<ArtifactT> extends EntityWithoutIdResource<ArtifactT> {
	
	public GenericArtifactResource(ArtifactT o, int idx, List<ArtifactT> list, INodeTemplateResourceOrNodeTypeImplementationResourceOrRelationshipTypeImplementationResource res) {
		super(o, idx, list, GenericArtifactsResource.getAbstractComponentInstanceResource(res));
	}
	
	public abstract void setArtifactType(ArtifactTypeId artifactTypeId);
	
	public abstract void setArtifactTemplate(ArtifactTemplateId artifactTemplateId);
	
	/**
	 * required by artifacts.jsp
	 */
	public abstract ArtifactT getA();
	
}
