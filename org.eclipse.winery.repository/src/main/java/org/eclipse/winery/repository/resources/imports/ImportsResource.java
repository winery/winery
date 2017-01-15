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
package org.eclipse.winery.repository.resources.imports;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.imports.genericimports.GenericImportsResource;
import org.eclipse.winery.repository.resources.imports.xsdimports.XSDImportsResource;

/**
 * The specification does not nest the sequence of import elements in an imports
 * container. We introduce such a container to be consistent with the other
 * resource naming
 */
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
		} else {
			return new GenericImportsResource(id);
		}
	}

}
