/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.imports.genericimports;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.common.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.repository.rest.resources.AbstractComponentsWithoutTypeReferenceResource;

/**
 * Manages a certain kind of imports without special treatments
 */
public class GenericImportsResource extends AbstractComponentsWithoutTypeReferenceResource<GenericImportResource> {

	private String type;

	/**
	 * @param id the (decoded) id, e.g., http://schemas.xmlsoap.org/wsdl/
	 */
	public GenericImportsResource(String id) {
		this.type = id;
	}

	@Override
	public GenericImportResource getComponentInstaceResource(String namespace, String id, boolean encoded) {
		GenericImportId iId = new GenericImportId(namespace, id, encoded, this.type);
		return new GenericImportResource(iId);
	}

	@Path("{namespace}/{id}/")
	public GenericImportResource getComponentInstaceResource(@PathParam("namespace") String namespace, @PathParam("id") String id) {
		return this.getComponentInstaceResource(namespace, id, true);
	}
}
