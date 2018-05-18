/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.imports.xsdimports;

import io.swagger.annotations.ApiOperation;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentsWithoutTypeReferenceResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Manages all imports of type XML Schema Definition.
 * The actual implementation is done in the AbstractComponentsResource
 * <p>
 * FIXME: This class should be generalized to handle ImportId
 */
public class XSDImportsResource extends AbstractComponentsWithoutTypeReferenceResource<XSDImportResource> {

    @Path("{namespace}/")
    @GET
    @ApiOperation(value = "Returns all available local names of defined elements in this namespace")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAllElementLocalNames(
        @PathParam("namespace") String nsString,
        @QueryParam(value = "elements") String returnElements,
        @QueryParam(value = "types") String returnTypes) {
        // returnElements is not read as either types or elements may be read
        return RepositoryFactory.getRepository().getXsdImportManager()
            .getAllDefinedLocalNames(new Namespace(nsString, true), (returnTypes != null));
    }

    @Path("{namespace}/{id}/")
    public XSDImportResource getComponentInstanceResource(@PathParam("namespace") String namespace, @PathParam("id") String id) {
        return this.getComponentInstanceResource(namespace, id, true);
    }

}
