/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.imports.genericimports;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImport;
import org.eclipse.winery.repository.backend.ImportUtils;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Objects;
import java.util.Optional;

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
        final Optional<TImport> theImport = ImportUtils.getTheImport(RepositoryFactory.getRepository(), (GenericImportId) id);
        if (!theImport.isPresent()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        @Nullable final String location = theImport.get().getLocation();
        @NonNull String fileName = EncodingUtil.URLdecode(encodedFileName);
        if (!fileName.equals(location)) {
            LOGGER.debug("Filename mismatch %s vs %s", fileName, location);
            return Response.status(Status.NOT_FOUND).build();
        }
        RepositoryFileReference ref = new RepositoryFileReference(this.id, location);
        return RestUtils.returnRepoPath(ref, null);
    }

}
