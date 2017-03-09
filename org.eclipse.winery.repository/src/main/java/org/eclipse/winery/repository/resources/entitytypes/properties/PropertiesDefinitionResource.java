/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Lukas Harzenetter - add JSON implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytypes.properties;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sun.jersey.api.view.Viewable;
import org.apache.xerces.xs.XSConstants;
import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.EntityTypeResource;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;
import org.eclipse.winery.repository.resources.apiData.PropertiesDefinitionResourceApiData;
import org.eclipse.winery.repository.resources.apiData.XsdDefinitionsApiData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

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

	// TODO: delete this because it's not needed for angular
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHTML() {
		return new Viewable("/jsp/entitytypes/properties/propertiesDefinition.jsp", new JSPData(this, this.wpd));
	}
	// end TODO

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public PropertiesDefinitionResourceApiData getJson() {
		PropertiesDefinition definition = this.getEntityType().getPropertiesDefinition();
		return new PropertiesDefinitionResourceApiData(definition, this.wpd);
	}

	@GET
	@Path("{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public XsdDefinitionsApiData getXsdDefinitionJson(@PathParam("type") String type) {
		ArrayNode definitions = null;

		switch (type) {
			case "element":
				definitions = Utils.getAllXSDefinitionsForTypeAheadSelectionRaw(XSConstants.ELEMENT_DECLARATION);
				break;
			case "type":
				definitions = Utils.getAllXSDefinitionsForTypeAheadSelectionRaw(XSConstants.TYPE_DEFINITION);
				break;
		}

		if (definitions == null) {
			LOGGER.error("No such parameter available in this call", type);
			throw new InvalidParameterException("No such parameter available in this call");
		}

		return new XsdDefinitionsApiData(definitions);
	}

	public TEntityType getEntityType() {
		return this.parentRes.getEntityType();
	}

	@DELETE
	public Response clearPropertiesDefinition() {
		this.getEntityType().setPropertiesDefinition(null);
		ModelUtilities.removeWinerysPropertiesDefinition(this.getEntityType());
		return BackendUtils.persist(this.parentRes);
	}

	public boolean getIsWineryKeyValueProperties() {
		return (this.wpd != null);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response onJsonPost(PropertiesDefinitionResourceApiData data) {
		// only iff there is some data set
		if (data.propertiesDefinition != null &&
				(data.propertiesDefinition.getElement() != null || data.propertiesDefinition.getType() != null)) {
			// first of all, remove Winery's Properties definition (if it exists)
			ModelUtilities.removeWinerysPropertiesDefinition(this.getEntityType());
			// replace old properties definition by new one
			PropertiesDefinition def = new PropertiesDefinition();

			if (data.propertiesDefinition.getElement() != null) {
				def.setElement(data.propertiesDefinition.getElement());
			} else {
				def.setType(data.propertiesDefinition.getType());
			}

			this.getEntityType().setPropertiesDefinition(def);
			List<String> errors = new ArrayList<>();
			BackendUtils.deriveWPD(this.getEntityType(), errors);
			// currently the errors are just logged
			for (String error : errors) {
				PropertiesDefinitionResource.LOGGER.debug(error);
			}
			return BackendUtils.persist(this.parentRes);

		} else if (data.winerysPropertiesDefinition != null) {
			TEntityType et = this.parentRes.getEntityType();

			// clear current properties definition
			et.setPropertiesDefinition(null);

			// create winery properties definition and persist it
			ModelUtilities.replaceWinerysPropertiesDefinition(et, data.winerysPropertiesDefinition);
			String namespace = data.winerysPropertiesDefinition.getNamespace();
			if (!NamespacesResource.INSTANCE.getIsPrefixKnownForNamespace(namespace)) {
				NamespacesResource.INSTANCE.addNamespace(namespace);
			}
			return BackendUtils.persist(this.parentRes);
		}

		return Response.status(Status.BAD_REQUEST).entity("Wrong data submitted!").build();
	}
}
