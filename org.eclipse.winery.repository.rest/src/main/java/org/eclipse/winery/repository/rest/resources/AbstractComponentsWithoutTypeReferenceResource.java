/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Nicole Keppler, Lukas Balzer - changes for angular frontend
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.repository.rest.resources._support.ResourceCreationResult;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;

/**
 * This class does NOT inherit from TEntityTemplatesResource<ArtifactTemplate>
 * as these templates are directly nested in a TDefinitionsElement
 */
public abstract class AbstractComponentsWithoutTypeReferenceResource<T extends AbstractComponentInstanceResource> extends AbstractComponentsResource<T> {

	/**
	 * Creates a new component instance in the given namespace
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response onJsonPost(QNameApiData jsonData) {
		ResourceCreationResult creationResult = super.onPost(jsonData.namespace, jsonData.localname);
		return creationResult.getResponse();
	}

	/**
	 * Creates a new component instance in the given namespace
	 *
	 * @param namespace plain namespace
	 * @param name      plain id
	 * @param ignored   this parameter is ignored, but necessary for {@link ArtifactTemplatesResource} to be able to
	 *                  accept the artifact type at a post
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response onPost(@FormParam("namespace") String namespace, @FormParam("name") String name, String ignored) {
		ResourceCreationResult res = this.onPost(namespace, name);
		return res.getResponse();
	}
}
