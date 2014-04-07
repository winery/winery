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

import java.net.URI;
import java.util.SortedSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.Prefs;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.ResourceCreationResult;
import org.restdoc.annotations.RestDocParam;

import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;

/**
 * This class does NOT inherit from TEntityTemplatesResource<ArtifactTemplate>
 * as these templates are directly nested in a TDefinitionsElement
 */
public class AbstractComponentsWithTypeReferenceResource extends AbstractComponentsResource {
	
	/**
	 * Creates the resource and sets the specified type
	 * 
	 * In contrast to the other component instances in this package, we
	 * additionally need the parameter "type" to set the type of the artifact
	 * template.
	 * 
	 * @param namespace Namespace of the template
	 * @param name name attribute of the template
	 * @param type: QName of the type, format: {namespace}localname is retrieved
	 *            from namespace manager
	 * 
	 * @return URI of the created Resource, null if resource already exists,
	 *         URI_internalServerError if an internal server error occurred
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public Response onPost(@RestDocParam(description = "Namespace of the component") @FormParam("namespace") String namespace, @RestDocParam(description = "name attribute of the component") @FormParam("name") String name, @RestDocParam(description = "QName of the type, format: {namespace}localname") @FormParam("type") String type) {
		// only check for type parameter as namespace and name are checked in super.onPost
		if (StringUtils.isEmpty(type)) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		ResourceCreationResult creationResult = super.onPost(namespace, name);
		if (!creationResult.isSuccess()) {
			return creationResult.getResponse();
		}
		if (creationResult.getStatus().equals(Status.CREATED)) {
			IHasTypeReference resource = (IHasTypeReference) AbstractComponentsResource.getComponentInstaceResource((TOSCAComponentId) creationResult.getId());
			resource.setType(type);
			// we assume that setType succeeded and just return the result of the
			// creation of the artifact template resource
			// Thus, we do NOT change res
		}
		return creationResult.getResponse();
	}
	
	@Override
	@Produces(MimeTypes.MIMETYPE_HAL)
	@GET
	public Response getHalRepresentation(@Context UriInfo uriInfo) {
		RepresentationFactory representationFactory = new JsonRepresentationFactory();
		Representation halResource = this.fillHALRepresentation(representationFactory.newRepresentation(uriInfo.getAbsolutePath()));
		String json = halResource.toString(RepresentationFactory.HAL_JSON);
		
		Response res = Response.ok(json).header("Access-Control-Allow-Origin", Prefs.INSTANCE.getURLForHALAccessControlAllowOrigin()).build();
		return res;
	}
	
	@Override
	protected Representation fillHALRepresentation(Representation res) {
		res = res.withLink("main", "../");
		
		Class<? extends TOSCAComponentId> idClass = Utils.getComponentIdClassForComponentContainer(this.getClass());
		SortedSet<? extends TOSCAComponentId> allTOSCAComponentIds = Repository.INSTANCE.getAllTOSCAComponentIds(idClass);
		
		for (TOSCAComponentId instanceId : allTOSCAComponentIds) {
			String type = Utils.getTypeForComponentContainer(this.getClass()).toLowerCase();
			URI baseURI = URI.create(Prefs.INSTANCE.getResourcePath() + "/" + type + "s/");
			String href = Utils.getRelativeURL(baseURI, instanceId);
			String title = BackendUtils.getName(instanceId);
			res = res.withLink("instance", href, null, title, null, null);
		}
		
		return res;
	}
	
}
