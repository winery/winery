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
package org.eclipse.winery.repository.rest.resources.imports.xsdimports;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.AbstractComponentsWithoutTypeReferenceResource;

import io.swagger.annotations.ApiOperation;

/**
 * Manages all imports of type XML Schema Definition <br />
 * The actual implementation is done in the AbstractComponentsResource
 *
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

}
