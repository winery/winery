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
 *     Lukas Harzenetter - JSON API
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.requirementtypes;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.AvailableSuperclassesApiData;
import org.eclipse.winery.repository.rest.resources.apiData.RequiredCapabilityTypeApiData;

import com.sun.jersey.api.NotFoundException;
import org.apache.commons.lang3.StringUtils;

public class RequiredCapabilityTypeResource {

	private RequirementTypeResource requirementTypeResource;

	public RequiredCapabilityTypeResource(RequirementTypeResource requirementTypeResource) {
		this.requirementTypeResource = requirementTypeResource;
	}

	public TRequirementType getRequirementType() {
		return this.requirementTypeResource.getRequirementType();
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public Response putRequiredCapabilityType(String type) {
		if (StringUtils.isEmpty(type)) {
			return Response.status(Status.BAD_REQUEST).entity("type must not be empty").build();
		}
		QName qname = QName.valueOf(type);
		CapabilityTypeId id = new CapabilityTypeId(qname);
		if (RepositoryFactory.getRepository().exists(id)) {
			// everything allright. Store new reference
			this.getRequirementType().setRequiredCapabilityType(qname);
			return RestUtils.persist(this.requirementTypeResource);
		} else {
			throw new NotFoundException("Given QName could not be resolved to an existing capability type");
		}
	}

	@DELETE
	public Response deleteRequiredCapabilityType() {
		this.getRequirementType().setRequiredCapabilityType(null);
		return RestUtils.persist(this.requirementTypeResource);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RequiredCapabilityTypeApiData getAllCapabilityTypes() {
		return new RequiredCapabilityTypeApiData(new AvailableSuperclassesApiData(CapabilityTypeId.class), this.getRequirementType().getRequiredCapabilityType());
	}
}
