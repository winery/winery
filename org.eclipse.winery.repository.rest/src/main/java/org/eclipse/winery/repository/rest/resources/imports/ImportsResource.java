/**
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.rest.resources.imports;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.imports.WsdlImportId;
import org.eclipse.winery.repository.rest.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.rest.resources.imports.genericimports.GenericImportsResource;
import org.eclipse.winery.repository.rest.resources.imports.wsdlimports.WsdlImportsResource;
import org.eclipse.winery.repository.rest.resources.imports.xsdimports.XSDImportsResource;

import io.swagger.annotations.Api;

/**
 * The specification does not nest the sequence of import elements in an imports container. We introduce such a
 * container to be consistent with the other resource naming
 */
@Api(tags = "Imports")
public class ImportsResource {

	@Path("{id}/")
	public AbstractComponentsResource getXSDsResource(@PathParam("id") String id) {
		// once: decoding for browser locations
		id = Util.URLdecode(id);
		// once again: real URI
		id = Util.URLdecode(id);
		if (id.equals("http://www.w3.org/2001/XMLSchema")) {
			// Models http://www.w3.org/2001/XMLSchema. We do not use xsd instead of the
			// encoded namespace, because this induces special cases at many places
			return new XSDImportsResource();
		} else if (id.equalsIgnoreCase(WsdlImportId.WSDL_URI)) {
			return new WsdlImportsResource();
		} else {
			return new GenericImportsResource(id);
		}
	}

	@GET
	public List<?> getAllImportTypes() {
		// TODO use backend to determine a complete list
		return Arrays.asList("http://www.w3.org/2001/XMLSchema");
	}
}
