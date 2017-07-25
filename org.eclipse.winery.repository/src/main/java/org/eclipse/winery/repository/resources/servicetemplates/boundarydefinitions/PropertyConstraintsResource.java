/*******************************************************************************
 * Copyright (c) 2013-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.apiData.boundarydefinitions.PropertyConstraintsApiData;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import org.apache.commons.lang3.StringUtils;

public class PropertyConstraintsResource {

	private final TBoundaryDefinitions.PropertyConstraints propertyConstraints;
	private final ServiceTemplateResource res;


	public PropertyConstraintsResource(TBoundaryDefinitions.PropertyConstraints propertyConstraints, ServiceTemplateResource res) {
		this.propertyConstraints = propertyConstraints;
		this.res = res;
	}

	@Path("{id}")
	@DELETE
	public Response onDelete(@PathParam("id") String id) {
		id = Util.URLdecode(id);
		Iterator<TPropertyConstraint> iterator = this.propertyConstraints.getPropertyConstraint().iterator();
		while (iterator.hasNext()) {
			TPropertyConstraint propertyConstraint = iterator.next();
			if (propertyConstraint.getProperty().equals(id)) {
				iterator.remove();
				return BackendUtils.persist(this.res);
			}
		}
		// if the property mapping was not found, we reach this point
		// otherwise "iterator.remove()" has called and the resource persisted
		return Response.status(Status.NOT_FOUND).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response onPost(PropertyConstraintsApiData constraintsApiData) {

		if (StringUtils.isEmpty(constraintsApiData.getProperty())) {
			return Response.status(Status.BAD_REQUEST).entity("Property must not be empty").build();
		}
		if (StringUtils.isEmpty(constraintsApiData.getConstraintType())) {
			return Response.status(Status.BAD_REQUEST).entity("Constraint Type must not be empty").build();
		}

		TPropertyConstraint propertyConstraint = new TPropertyConstraint();
		propertyConstraint.setProperty(constraintsApiData.getProperty());
		propertyConstraint.setAny(constraintsApiData.getFragments());
		propertyConstraint.setConstraintType(constraintsApiData.getConstraintType());
		this.propertyConstraints.getPropertyConstraint().add(propertyConstraint);
		return BackendUtils.persist(this.res);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<PropertyConstraintsApiData> onGet() {
		List<PropertyConstraintsApiData> apiDatas = new ArrayList<>();
		Iterator<TPropertyConstraint> iterator = this.propertyConstraints.getPropertyConstraint().iterator();
		for (TPropertyConstraint propertyConstraint : this.propertyConstraints.getPropertyConstraint()) {
			apiDatas.add(new PropertyConstraintsApiData(propertyConstraint));
		}
		return apiDatas;
	}

}
