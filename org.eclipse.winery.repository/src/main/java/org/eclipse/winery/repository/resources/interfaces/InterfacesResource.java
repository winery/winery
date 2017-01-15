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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.relationshiptypes.RelationshipTypeResource;
import org.restdoc.annotations.RestDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.Viewable;

public class InterfacesResource extends EntityWithIdCollectionResource<InterfaceResource, TInterface> {

	private static final Logger LOGGER = LoggerFactory.getLogger(InterfacesResource.class);

	private TopologyGraphElementEntityTypeResource typeResource;

	private String urlPrefix;


	public InterfacesResource(IPersistable res, List<TInterface> list) {
		super(InterfaceResource.class, TInterface.class, list, res);
	}

	/**
	 * @param urlPrefix prefix to be prepended to the URL.
	 *            "source"|"target"|null. E.g., "source" for "sourceinterfaces"
	 */
	public InterfacesResource(String urlPrefix, List<TInterface> list, IPersistable typeResource) {
		super(InterfaceResource.class, TInterface.class, list, typeResource);
		this.urlPrefix = urlPrefix;
		this.typeResource = (TopologyGraphElementEntityTypeResource) typeResource;
	}

	@Override
	public Viewable getHTML() {
		return new Viewable("/jsp/interfaces/interfaces.jsp", this);
	}

	/**
	 * Implementation base: <br />
	 * {@link org.eclipse.winery.repository.resources.AbstractComponentResource.
	 * onPost(String)}
	 *
	 * @return entity: id of the stored interface
	 */
	@POST
	@RestDoc(methodDescription = "Creates a new interface. Returns conflict if interface already exists")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response onPost(@FormParam("interfaceName") String interfaceName) {
		if (StringUtils.isEmpty(interfaceName)) {
			return Response.status(Status.BAD_REQUEST).entity("null interfaceName").build();
		}

		TInterface iface = new TInterface();
		iface.setName(interfaceName);

		// check for duplicates
		// return "conflict" if interface already exists
		if (this.alreadyContains(iface)) {
			return Response.status(Status.CONFLICT).build();
		}

		this.list.add(iface);
		return BackendUtils.persist(this.res);
	}

	/**
	 * Required by interfaces.jsp
	 */
	public String getUrlPrefix() {
		return this.urlPrefix;
	}

	@Override
	public String getId(TInterface entity) {
		return entity.getName();
	}

	/**
	 * @return the namespace of the node/relationship type
	 */
	public String getNamespace() {
		return this.typeResource.getId().getNamespace().getDecoded();
	}

	/**
	 * @return the name of the node/relationship type
	 */
	public String getName() {
		return this.typeResource.getName();
	}

	public String getRelationshipTypeOrNodeTypeURLFragment() {
		if (this.typeResource instanceof RelationshipTypeResource) {
			return "relationshiptype";
		} else {
			return "nodetype";
		}
	}

	public String getRelationshipTypeOrNodeType() {
		if (this.typeResource instanceof RelationshipTypeResource) {
			return "Relationship Type";
		} else {
			return "Node Type";
		}
	}

	public String getTypeQName() {
		return this.typeResource.getId().getQName().toString();
	}
}
