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
package org.eclipse.winery.repository.resources.entitytypes.properties.winery;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.EntityTypeResource;
import org.restdoc.annotations.RestDoc;

import com.sun.jersey.api.NotFoundException;

public class WinerysPropertiesDefinitionResource {

	private final EntityTypeResource res;
	private final WinerysPropertiesDefinition wpd;


	/**
	 * @param res the resource where winery's k/v properties are defined
	 * @param wpd winery's properties definition object, MAY be null
	 */
	public WinerysPropertiesDefinitionResource(EntityTypeResource res, WinerysPropertiesDefinition wpd) {
		this.res = res;
		this.wpd = wpd;
	}

	@POST
	@RestDoc(methodDescription = "switches the mode to winery properties instead of element/type properties")
	public Response onPost() {
		TEntityType et = this.res.getEntityType();

		// clear current properties definition
		et.setPropertiesDefinition(null);

		// create empty winery properties definition and persist it
		WinerysPropertiesDefinition wpd = new WinerysPropertiesDefinition();
		ModelUtilities.replaceWinerysPropertiesDefinition(et, wpd);
		return BackendUtils.persist(this.res);
	}

	@Path("namespace")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getNamespace() {
		if (this.wpd == null) {
			throw new NotFoundException();
		}
		return this.wpd.getNamespace();
	}

	@Path("namespace")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public Response setNamespace(String namespace) {
		if (this.wpd == null) {
			throw new NotFoundException();
		}
		this.wpd.setNamespace(namespace);
		return BackendUtils.persist(this.res);
	}

	@Path("elementname")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getElementName() {
		if (this.wpd == null) {
			throw new NotFoundException();
		}
		return this.wpd.getElementName();
	}

	@Path("elementname")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public Response setLocalname(String elementName) {
		if (this.wpd == null) {
			throw new NotFoundException();
		}
		this.wpd.setElementName(elementName);
		return BackendUtils.persist(this.res);
	}

	@Path("elementname")
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response setLocalnameViaWebUI(@FormParam(value = "name") String elementName) {
		if (this.wpd == null) {
			throw new NotFoundException();
		}
		this.wpd.setElementName(elementName);
		return BackendUtils.persist(this.res);
	}

	/**
	 * Here, also the addition of k/v properties is handled.
	 */
	@Path("list/")
	public PropertyDefinitionKVListResource getListResource() {
		if (this.wpd == null) {
			throw new NotFoundException();
		}
		PropertyDefinitionKVList list = this.wpd.getPropertyDefinitionKVList();
		if (list == null) {
			list = new PropertyDefinitionKVList();
			this.wpd.setPropertyDefinitionKVList(list);
		}
		return new PropertyDefinitionKVListResource(this.res, list);
	}

}
