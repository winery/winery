/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.policytypes;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.repository.rest.resources.AbstractComponentsWithoutTypeReferenceResource;

import io.swagger.annotations.Api;

/**
 * Manages all policy types in all available namespaces <br />
 * The actual implementation is done in the AbstractComponentsResource
 */
@Api(tags = "Policy Types")
public class PolicyTypesResource extends AbstractComponentsWithoutTypeReferenceResource<PolicyTypeResource> {
	@Path("{namespace}/{id}/")
	public PolicyTypeResource getComponentInstaceResource(@PathParam("namespace") String namespace, @PathParam("id") String id) {
		return this.getComponentInstaceResource(namespace, id, true);
	}

}
