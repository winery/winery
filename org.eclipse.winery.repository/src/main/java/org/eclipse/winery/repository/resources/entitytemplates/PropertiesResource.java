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
package org.eclipse.winery.repository.resources.entitytemplates;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;

import com.sun.jersey.api.view.Viewable;

public class PropertiesResource {

	private AbstractComponentInstanceResource res;
	private TEntityTemplate template;


	/**
	 * @param template the template to store the definitions at
	 * @param res the resource to save after modifications
	 */
	public PropertiesResource(TEntityTemplate template, AbstractComponentInstanceResource res) {
		this.template = template;
		this.res = res;
	}

	/**
	 * Currently, properties can only be updated as a whole XML fragment
	 *
	 * Getting/setting a fragment of properties is not possible yet
	 */
	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
	public Response setProperties(TEntityTemplate.Properties properties) {
		this.getTemplate().setProperties(properties);
		return BackendUtils.persist(this.res);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHTML() {
		return new Viewable("/jsp/entitytemplates/properties.jsp", this);
	}

	/** data for the JSP **/

	public TEntityTemplate getTemplate() {
		return this.template;
	}

}
