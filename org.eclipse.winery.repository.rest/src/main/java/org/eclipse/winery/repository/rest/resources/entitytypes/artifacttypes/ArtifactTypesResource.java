/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.artifacttypes;

import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.AbstractComponentsWithoutTypeReferenceResource;

public class ArtifactTypesResource extends AbstractComponentsWithoutTypeReferenceResource<ArtifactTypeResource> {

	// This cannot be used as the INSTANCE is per startup of the whole
	// application
	// We could do some checking for the number of ArtifactTypeResources or
	// timestamp,
	//
	// private final HashMap<String, ArtifactTypeResource> fileExtensionMapping
	// = new ArtifactTypesResource().getFileExtensionMapping();

	/**
	 * @return a mapping from file extension to artifact type resources
	 */
	// public HashMap<String, ArtifactTypeResource> getFileExtensionMapping() {
	// HashMap<String, ArtifactTypeResource> res = new HashMap<String,
	// ArtifactTypeResource>();
	// for (ArtifactTypeResource at : this.getArtifactTypeResources()) {
	// if (at.getAssociatedFileExtension() != null) {
	// res.put(at.getAssociatedFileExtension(), at);
	// }
	// }
	// return res;
	// }

	@GET
	// should be "QName", but that MIME type is not available. XLink is too
	// complicated for our setup
	@Produces(MediaType.TEXT_PLAIN)
	public Response getArtifactTypeQNameForExtension(@QueryParam("extension") String extension) {
		if (extension == null) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		ArtifactTypeResource artifactType = this.getArtifactTypeForExtension(extension);
		Response res;
		if (artifactType == null) {
			res = Response.status(Status.NOT_FOUND).build();
		} else {
			res = Response.ok().entity(artifactType.getId().getQName().toString()).build();
		}
		return res;
	}

	/**
	 * Returns the first matching ArtifactTypeResource for the given file
	 * extension. Returns null if no such ArtifactType can be found
	 *
	 * The case of the extension is ignored.
	 *
	 * This is more a DAO method
	 */
	public ArtifactTypeResource getArtifactTypeForExtension(String extension) {
		SortedSet<ArtifactTypeId> allArtifactTypeIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(ArtifactTypeId.class);
		ArtifactTypeResource res = null;
		for (ArtifactTypeId id : allArtifactTypeIds) {
			ArtifactTypeResource r = new ArtifactTypeResource(id);
			if (extension.equalsIgnoreCase(r.getAssociatedFileExtension())) {
				res = r;
				break;
			}
		}
		return res;
	}

}
