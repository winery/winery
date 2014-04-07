/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Nico Rusam and Alexander Stifel - HAL support
 *******************************************************************************/
package org.eclipse.winery.repository.resources.admin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.repository.Prefs;
import org.eclipse.winery.repository.resources.admin.types.ConstraintTypesManager;
import org.eclipse.winery.repository.resources.admin.types.PlanLanguagesManager;
import org.eclipse.winery.repository.resources.admin.types.PlanTypesManager;

import com.sun.jersey.api.view.Viewable;
import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;

public class AdminTopResource {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHTML() {
		return new Viewable("/jsp/admin/adminindex.jsp", this);
	}
	
	@Path("namespaces/")
	public NamespacesResource getNamespacesResource() {
		return NamespacesResource.INSTANCE;
	}
	
	@Path("repository/")
	public RepositoryAdminResource getRepositoryAdminResource() {
		return new RepositoryAdminResource();
	}
	
	@Path("planlanguages/")
	public PlanLanguagesManager getPlanLanguagesResource() {
		return PlanLanguagesManager.INSTANCE;
	}
	
	@Path("plantypes/")
	public PlanTypesManager getPlanTypesResource() {
		return PlanTypesManager.INSTANCE;
	}
	
	@Path("constrainttypes/")
	public ConstraintTypesManager getConstraintTypesManager() {
		return ConstraintTypesManager.INSTANCE;
	}
	
	@Produces(MimeTypes.MIMETYPE_HAL)
	@GET
	public Response getHalRepresentation(@Context UriInfo uriInfo) {
		RepresentationFactory representationFactory = new JsonRepresentationFactory();
		// @formatter:off
		Representation halResource = representationFactory.newRepresentation(uriInfo.getAbsolutePath())
				.withLink("namespaces", "namespaces/")
				.withLink("repository", "repository/")
				.withLink("planlanguages", "planlanguages/")
				.withLink("plantypes", "plantypes/")
				.withLink("constrainttypes", "constrainttypes/");
		// @formatter:on
		String json = halResource.toString(RepresentationFactory.HAL_JSON);
		
		Response res = Response.ok(json).header("Access-Control-Allow-Origin", Prefs.INSTANCE.getURLForHALAccessControlAllowOrigin()).build();
		
		return res;
	}
}
