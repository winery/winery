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
package org.eclipse.winery.repository.resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.repository.Prefs;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.importing.CSARImporter;
import org.eclipse.winery.repository.resources.admin.AdminTopResource;
import org.eclipse.winery.repository.resources.entitytemplates.artifacttemplates.ArtifactTemplatesResource;
import org.eclipse.winery.repository.resources.entitytemplates.policytemplates.PolicyTemplatesResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.nodetypeimplementations.NodeTypeImplementationsResource;
import org.eclipse.winery.repository.resources.entitytypeimplementations.relationshiptypeimplementations.RelationshipTypeImplementationsResource;
import org.eclipse.winery.repository.resources.entitytypes.artifacttypes.ArtifactTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.capabilitytypes.CapabilityTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.policytypes.PolicyTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.relationshiptypes.RelationshipTypesResource;
import org.eclipse.winery.repository.resources.entitytypes.requirementtypes.RequirementTypesResource;
import org.eclipse.winery.repository.resources.imports.ImportsResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplatesResource;
import org.restdoc.annotations.RestDoc;
import org.restdoc.annotations.RestDocReturnCode;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;

/**
 * All paths listed here have to be listed in Jersey's filter configuration
 */
@Path("/")
public class MainResource {
	
	@Path("artifacttemplates/")
	public ArtifactTemplatesResource artifacttemplates() {
		return new ArtifactTemplatesResource();
	}
	
	@Path("artifacttypes/")
	public ArtifactTypesResource artifactypes() {
		return new ArtifactTypesResource();
	}
	
	@Path("admin/")
	public AdminTopResource admin() {
		return new AdminTopResource();
	}
	
	@Path("capabilitytypes/")
	public CapabilityTypesResource capabilitytypes() {
		return new CapabilityTypesResource();
	}
	
	@Path("imports/")
	public ImportsResource imports() {
		return new ImportsResource();
	}
	
	@Path("nodetypes/")
	public NodeTypesResource nodetypes() {
		return new NodeTypesResource();
	}
	
	@Path("nodetypeimplementations/")
	public NodeTypeImplementationsResource nodetypeimplementations() {
		return new NodeTypeImplementationsResource();
	}
	
	@Path("other/")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getOtherElements() {
		return new Viewable("/jsp/otherElements.jsp");
	}
	
	@Path("policytemplates/")
	public PolicyTemplatesResource policytemplates() {
		return new PolicyTemplatesResource();
	}
	
	@Path("policytypes/")
	public PolicyTypesResource policytypes() {
		return new PolicyTypesResource();
	}
	
	@Path("relationshiptypes/")
	public RelationshipTypesResource relationshiptypes() {
		return new RelationshipTypesResource();
	}
	
	@Path("requirementtypes/")
	public RequirementTypesResource requirementtypes() {
		return new RequirementTypesResource();
	}
	
	@Path("relationshiptypeimplementations/")
	public RelationshipTypeImplementationsResource relationshiptypeimplementations() {
		return new RelationshipTypeImplementationsResource();
	}
	
	@Path("servicetemplates/")
	public ServiceTemplatesResource servicetemplates() {
		
		return new ServiceTemplatesResource();
	}
	
	/**
	 * Returns the main page of winery.
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response onGet() {
		return Response.temporaryRedirect(Utils.createURI("servicetemplates/")).build();
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@RestDoc(methodDescription = "Imports the given CSAR (sent by simplesinglefileupload.jsp)")
	@RestDocReturnCode(code = "200", description = "If the CSAR could be partially imported, the points where it failed are returned in the body")
	public Response importCSAR(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
		CSARImporter importer = new CSARImporter();
		List<String> errors = new ArrayList<String>();
		try {
			importer.readCSAR(uploadedInputStream, errors);
		} catch (Exception e) {
			return Response.serverError().entity("Could not import CSAR").entity(e.getMessage()).build();
		}
		if (errors.isEmpty()) {
			return Response.noContent().build();
		} else {
			// In case there are errors, we send them as "bad request"
			return Response.status(Status.BAD_REQUEST).entity(errors).build();
		}
	}
	
	@Produces(MimeTypes.MIMETYPE_HAL)
	@GET
	public Response getHalRepresentation(@Context UriInfo uriInfo) {
		RepresentationFactory representationFactory = new JsonRepresentationFactory();
		// @formatter:off
		Representation halResource = representationFactory.newRepresentation(uriInfo.getAbsolutePath())
				.withLink("artifacttemplates", "artifacttemplates/")
				.withLink("artifacttypes", "artifacttypes/")
				.withLink("admin", "admin/")
				.withLink("capabilitytypes", "capabilitytypes/")
				.withLink("imports", "imports/")
				.withLink("nodetypes", "nodetypes/")
				.withLink("nodetypeimplementations", "nodetypeimplementations/")
				.withLink("other", "other/")
				.withLink("policytemplates", "policytemplates/")
				.withLink("policytypes", "policytypes/")
				.withLink("relationshiptypes", "relationshiptypes/")
				.withLink("requirementtypes", "requirementtypes/")
				.withLink("relationshiptypeimplementations", "relationshiptypeimplementations/")
				.withLink("servicetemplates", "servicetemplates/");
		// @formatter:on
		String json = halResource.toString(RepresentationFactory.HAL_JSON);
		
		Response res = Response.ok(json).header("Access-Control-Allow-Origin", Prefs.INSTANCE.getURLForHALAccessControlAllowOrigin()).build();
		return res;
	}
}