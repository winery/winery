/*******************************************************************************
 * Copyright (c) 2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.API;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.repository.datatypes.select2.Select2DataWithOptGroups;

public class APIResource {
	
	@GET
	@Path("getallartifacttemplates")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllArtifactsTemplates(@QueryParam("servicetemplate") String serviceTemplateQName) {
		if (StringUtils.isEmpty(serviceTemplateQName)) {
			return Response.status(Status.BAD_REQUEST).entity("servicetemplate has be given as query parameter").build();
		}
		Select2DataWithOptGroups res = new Select2DataWithOptGroups();
		res.add("http://www.example.org/test/artifacts/artifacttemplates", "{http://www.example.org/test/artifacts/artifacttemplates}TestArtifacts-DA-ArtifactTemplate", "TestArtifacts-DA-ArtifactTemplate");
		res.add("http://www.example.org/test/artifacts/artifacttemplates", "{http://www.example.org/test/artifacts/artifacttemplates}TestArtifacts-IA-ArtifactTemplate", "TestArtifacts-IA-ArtifactTemplate");
		return Response.ok().entity(res.asSortedSet()).build();
	}
	
	@GET
	@Path("getallartifacttemplatesofcontaineddeploymentartifacts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllArtifactTemplatesOfContainedDeploymentArtifacts(@QueryParam("servicetemplate") String serviceTemplateQName) {
		if (StringUtils.isEmpty(serviceTemplateQName)) {
			return Response.status(Status.BAD_REQUEST).entity("servicetemplate has be given as query parameter").build();
		}
		Select2DataWithOptGroups res = new Select2DataWithOptGroups();
		res.add("http://www.example.org/test/artifacts/artifacttemplates", "{http://www.example.org/test/artifacts/artifacttemplates}TestArtifacts-DA-ArtifactTemplate", "TestArtifacts-DA-ArtifactTemplate");
		return Response.ok().entity(res.asSortedSet()).build();
	}
	
	@GET
	@Path("getallartifacttemplatesofcontainedimplementationartifacts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllArtifactTemplatesOfContainedImplementationArtifacts(@QueryParam("servicetemplate") String serviceTemplateQName) {
		if (StringUtils.isEmpty(serviceTemplateQName)) {
			return Response.status(Status.BAD_REQUEST).entity("servicetemplate has be given as query parameter").build();
		}
		Select2DataWithOptGroups res = new Select2DataWithOptGroups();
		res.add("http://www.example.org/test/artifacts/artifacttemplates", "{http://www.example.org/test/artifacts/artifacttemplates}TestArtifacts-IA-ArtifactTemplate", "TestArtifacts-IA-ArtifactTemplate");
		return Response.ok().entity(res.asSortedSet()).build();
	}
}
