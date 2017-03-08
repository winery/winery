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
package org.eclipse.winery.repository.resources.interfaces;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.entitytypes.TopologyGraphElementEntityTypeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InterfacesResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(InterfacesResource.class);

	private TopologyGraphElementEntityTypeResource res;
	private List<TInterface> interfaces;

	public InterfacesResource(TopologyGraphElementEntityTypeResource res, List<TInterface> interfaces) {
		this.res = res;
		this.interfaces = interfaces;
	}

	/**
	 * Implementation base: <br />
	 * {@link AbstractComponentsResource#onPost(java.lang.String, java.lang.String)}
	 *
	 * @return entity: id of the stored interface
	 */
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.TEXT_PLAIN)
//	public Response onPost(InterfaceApiData interfaceApiData) {
//		return BackendUtils.persist(this.res);
//	}

	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public List<TInterface> onGet() {
		return this.interfaces;
	}

}
