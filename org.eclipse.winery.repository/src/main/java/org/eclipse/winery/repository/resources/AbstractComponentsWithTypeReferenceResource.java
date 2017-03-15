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
package org.eclipse.winery.repository.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.backend.ResourceCreationResult;

import org.apache.commons.lang3.StringUtils;
import org.restdoc.annotations.RestDocParam;

/**
 * This class does NOT inherit from TEntityTemplatesResource<ArtifactTemplate>
 * as these templates are directly nested in a TDefinitionsElement
 */
public abstract class AbstractComponentsWithTypeReferenceResource<T extends AbstractComponentInstanceResource> extends AbstractComponentsResource<T> {

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

}
