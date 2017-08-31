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
package org.eclipse.winery.repository.rest.resources;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.ApiOperation;

/**
 * Ensures that the AbstractComponentInstance has a getName method
 */
public interface IHasName {

	@GET
	@Path("name")
	// @formatter:off
	@ApiOperation(value = "Returns the name of the element. " +
	"Defaults to the ID of the element. " +
	"Some other ComponentInstances either carry a name or an ID. ")
	// @formatter:on
	@Produces(MediaType.TEXT_PLAIN)
	String getName();

	@PUT
	@Path("name")
	Response setName(@FormParam("value") String name);

}
