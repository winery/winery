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
package org.eclipse.winery.repository.resources.entitytypes;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.TopologyGraphElementEntityTypeId;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;

import com.sun.jersey.api.view.Viewable;

/**
 * specifies the methods required by implementations.jsp
 */
public abstract class ImplementationsOfOneType {
	
	private final TopologyGraphElementEntityTypeId typeId;
	
	
	public ImplementationsOfOneType(TopologyGraphElementEntityTypeId typeId) {
		this.typeId = typeId;
	}
	
	public TopologyGraphElementEntityTypeId getTypeId() {
		return this.typeId;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getHTML() {
		Viewable viewable = new Viewable("/jsp/entitytypes/implementations.jsp", this);
		return Response.ok().entity(viewable).build();
	}
	
	public Collection<Namespace> getNamespaceAutocompletionList() {
		return NamespacesResource.getNamespaces();
	}
	
	/**
	 * @return a list of type implementations implementing the associated node
	 *         type
	 */
	public abstract String getImplementationsTableData();
	
	/**
	 * The string used as URL part
	 */
	public abstract String getType();
	
	/**
	 * The string displayed to the user
	 */
	public abstract String getTypeStr();
	
}
