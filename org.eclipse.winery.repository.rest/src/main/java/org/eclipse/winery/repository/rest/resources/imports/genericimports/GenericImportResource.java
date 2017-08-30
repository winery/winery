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
package org.eclipse.winery.repository.rest.resources.imports.genericimports;

import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.repository.backend.ImportUtils;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.AbstractComponentInstanceResource;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericImportResource extends AbstractComponentInstanceResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentInstanceResource.class);

	public GenericImportResource(GenericImportId id) {
		super(id);
	}

	@Override
	protected TExtensibleElements createNewElement() {
		throw new IllegalStateException("This should not never happen.");
	}

	@GET
	@Path("{filename}")
	public Response getFile(@PathParam("filename") @NonNull final String encodedFileName) {
		Objects.requireNonNull(encodedFileName);
		final Optional<TImport> theImport = ImportUtils.getTheImport((GenericImportId) id);
		if (!theImport.isPresent()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		@Nullable final String location = theImport.get().getLocation();
		@NonNull String fileName = Util.URLdecode(encodedFileName);
		if (!fileName.equals(location)) {
			LOGGER.debug("Filename mismatch %s vs %s", fileName, location);
			return Response.status(Status.NOT_FOUND).build();
		}
		RepositoryFileReference ref = new RepositoryFileReference(this.id, location);
		return RestUtils.returnRepoPath(ref, null);
	}

}
