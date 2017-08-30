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
package org.eclipse.winery.repository.rest.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TConstraint;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.collections.withoutid.EntityWithoutIdResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;

public class ConstraintResource extends EntityWithoutIdResource<TConstraint> {

	/**
	 * @param constraint the current constraint value
	 * @param list       the list this constraint belongs to
	 * @param res        the node type resource this constraint belongs to. Required for saving
	 */
	public ConstraintResource(TConstraint constraint, int idx, List<TConstraint> list, NodeTypeResource res) {
		super(constraint, idx, list, res);
	}

	/**
	 * Required for collectionResource
	 *
	 * @throws ClassCastException of !(res instanceof NodeTypeResource)
	 */
	public ConstraintResource(TConstraint constraint, int idx, List<TConstraint> list, AbstractComponentInstanceResource res) {
		this(constraint, idx, list, (NodeTypeResource) res);
	}

	private TConstraint getConstraint() {
		return this.o;
	}

	@GET
	@Path("type")
	@Produces(MediaType.TEXT_PLAIN)
	public String getConstraintType() {
		return this.getConstraint().getConstraintType();
	}

	@PUT
	@Path("type")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response putConstraintType(String constraintType) {
		this.getConstraint().setConstraintType(constraintType);
		return RestUtils.persist(this.res);
	}
}
