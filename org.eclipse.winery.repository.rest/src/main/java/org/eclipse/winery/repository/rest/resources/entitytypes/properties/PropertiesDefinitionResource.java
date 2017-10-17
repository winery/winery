/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Lukas Harzenetter - add JSON implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.properties;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.xsd.NamespaceAndDefinedLocalNames;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.datatypes.NamespaceAndDefinedLocalNamesForAngular;
import org.eclipse.winery.repository.rest.resources.apiData.PropertiesDefinitionEnum;
import org.eclipse.winery.repository.rest.resources.apiData.PropertiesDefinitionResourceApiData;
import org.eclipse.winery.repository.rest.resources.entitytypes.EntityTypeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Models
 * <ol>
 * <li>TOSCA conforming properties definition (XML element / XML schema / none)</li>
 * <li>Winery's KV properties (in the subresource "winery")</li>
 * </ol>
 * <p>
 * This class does not have "KV" in its name, because it models
 * {@link TEntityType.PropertiesDefinition}
 */
public class PropertiesDefinitionResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesDefinitionResource.class);

	// We hold a copy of super.res as we work on the type EntityTypeResource instead of AbstractComponentInstanceResource
	private final EntityTypeResource parentRes;

	// we assume that this class is created at each request
	// therefore, we can have "wpd" final
	private final WinerysPropertiesDefinition wpd;


	public PropertiesDefinitionResource(EntityTypeResource res) {
		this.parentRes = res;
		this.wpd = ModelUtilities.getWinerysPropertiesDefinition(res.getEntityType());
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public PropertiesDefinitionResourceApiData getJson() {
		PropertiesDefinition definition = this.getEntityType().getPropertiesDefinition();
		return new PropertiesDefinitionResourceApiData(definition, this.wpd);
	}

	@GET
	@Path("{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<NamespaceAndDefinedLocalNamesForAngular> getXsdDefinitionJson(@PathParam("type") String type) {
		List<NamespaceAndDefinedLocalNames> allDeclaredElementsLocalNames = null;
		switch (type) {
			case "element":
				allDeclaredElementsLocalNames = RepositoryFactory.getRepository().getXsdImportManager().getAllDeclaredElementsLocalNames();
				break;
			case "type":
				allDeclaredElementsLocalNames = RepositoryFactory.getRepository().getXsdImportManager().getAllDefinedTypesLocalNames();
				break;
		}

		if (allDeclaredElementsLocalNames == null) {
			LOGGER.error("No such parameter available in this call", type);
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		return RestUtils.convert(allDeclaredElementsLocalNames);
	}

	public TEntityType getEntityType() {
		return this.parentRes.getEntityType();
	}

	@DELETE
	public Response clearPropertiesDefinition() {
		this.getEntityType().setPropertiesDefinition(null);
		ModelUtilities.removeWinerysPropertiesDefinition(this.getEntityType());
		return RestUtils.persist(this.parentRes);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response onJsonPost(PropertiesDefinitionResourceApiData data) {
		if (data.selectedValue == PropertiesDefinitionEnum.Element || data.selectedValue == PropertiesDefinitionEnum.Type) {
			// first of all, remove Winery's Properties definition (if it exists)
			ModelUtilities.removeWinerysPropertiesDefinition(this.getEntityType());
			// replace old properties definition by new one
			PropertiesDefinition def = new PropertiesDefinition();

			if (data.propertiesDefinition.getElement() != null) {
				def.setElement(data.propertiesDefinition.getElement());
			} else if (data.propertiesDefinition.getType() != null) {
				def.setType(data.propertiesDefinition.getType());
			} else {
				return Response.status(Status.BAD_REQUEST).entity("Wrong data submitted!").build();
			}

			this.getEntityType().setPropertiesDefinition(def);
			List<String> errors = new ArrayList<>();
			BackendUtils.deriveWPD(this.getEntityType(), errors);
			// currently the errors are just logged
			for (String error : errors) {
				PropertiesDefinitionResource.LOGGER.debug(error);
			}
			return RestUtils.persist(this.parentRes);
		} else if (data.selectedValue == PropertiesDefinitionEnum.Custom) {
			TEntityType et = this.parentRes.getEntityType();

			// clear current properties definition
			et.setPropertiesDefinition(null);

			// create winery properties definition and persist it
			ModelUtilities.replaceWinerysPropertiesDefinition(et, data.winerysPropertiesDefinition);
			String namespace = data.winerysPropertiesDefinition.getNamespace();
			NamespaceManager namespaceManager = RepositoryFactory.getRepository().getNamespaceManager();
			if (!namespaceManager.hasPrefix(namespace)) {
				namespaceManager.addNamespace(namespace);
			}
			return RestUtils.persist(this.parentRes);
		}

		return Response.status(Status.BAD_REQUEST).entity("Wrong data submitted!").build();
	}
}
