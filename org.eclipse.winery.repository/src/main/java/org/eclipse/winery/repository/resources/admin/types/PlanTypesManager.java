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
package org.eclipse.winery.repository.resources.admin.types;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.repository.Prefs;
import org.eclipse.winery.repository.datatypes.TypeWithShortName;
import org.eclipse.winery.repository.datatypes.ids.admin.PlanTypesId;

import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;

public class PlanTypesManager extends AbstractTypesManager {
	
	public final static PlanTypesManager INSTANCE = new PlanTypesManager();
	
	
	private PlanTypesManager() {
		super(new PlanTypesId());
		// add data without rendering in the plan types file
		this.addData(org.eclipse.winery.repository.Constants.TOSCA_PLANTYPE_BUILD_PLAN, "Build Plan");
		this.addData(org.eclipse.winery.repository.Constants.TOSCA_PLANTYPE_TERMINATION_PLAN, "Termination Plan");
	}
	
	/**
	 * Returns HAL Representation.
	 */
	@GET
	@Produces(MimeTypes.MIMETYPE_HAL)
	public Response getHalRepresentation(@Context UriInfo uriInfo) {
		
		RepresentationFactory representationFactory = new JsonRepresentationFactory();
		
		Representation halResource = representationFactory.newRepresentation(uriInfo.getAbsolutePath()).withLink("main", "../../");
		
		PlanTypesManager ptmInstance = PlanTypesManager.INSTANCE;
		Collection<TypeWithShortName> typesEntries = ptmInstance.getTypes();
		
		for (TypeWithShortName s : typesEntries) {
			
			if (!s.getType().equals(s.getShortName())) {
				
				halResource.withProperty(s.getShortName(), s.getType());
			}
			
		}
		String json = halResource.toString(RepresentationFactory.HAL_JSON);
		Response res = Response.ok(json).header("Access-Control-Allow-Origin", Prefs.INSTANCE.getURLForHALAccessControlAllowOrigin()).build();
		
		return res;
	}
}
