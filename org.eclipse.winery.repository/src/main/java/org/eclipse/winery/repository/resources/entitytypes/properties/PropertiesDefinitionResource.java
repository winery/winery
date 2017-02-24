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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.xerces.xs.XSConstants;
import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.EntityTypeResource;
import org.eclipse.winery.repository.resources.apiData.PropertiesDefinitionResourceApiData;
import org.eclipse.winery.repository.resources.apiData.XsdDefinitionsApiData;
import org.eclipse.winery.repository.resources.entitytypes.properties.winery.WinerysPropertiesDefinitionResource;

import com.sun.jersey.api.view.Viewable;
import org.apache.commons.lang3.StringUtils;
import org.restdoc.annotations.RestDoc;
import org.restdoc.annotations.RestDocParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Models
 * <ol>
 * <li>TOSCA conforming properties definition (XML element / XML schema / none)</li>
 * <li>Winery's KV properties (in the subresource "winery")</li>
 * </ol>
 *
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

	//TODO: delete this because it's not needed for angular
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHTML() {
		return new Viewable("/jsp/entitytypes/properties/propertiesDefinition.jsp", new JSPData(this, this.wpd));
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

	@Path("winery/")
	public WinerysPropertiesDefinitionResource getWinerysPropertiesDefinitionResource() {
		// this.wpd is null if there is no winery definition exisitin. The subresource handles that case, too
		return new WinerysPropertiesDefinitionResource(this.parentRes, this.wpd);
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

	@GET
	@Produces(MimeTypes.MIMETYPE_XSD)
	public Response getXSD() {
		if (this.getIsWineryKeyValueProperties()) {
			return Response.ok().entity(ModelUtilities.getWinerysPropertiesDefinitionXSDAsDocument(this.wpd)).build();
		} else {
			// not yet implemented
			// We would have to check the imports in the repo for the defined property
			// This also has to be similarly done at the export to determine the right imports
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@GET
	@RestDoc(methodDescription = "We provide the XSD at . and at ./xsd/ to enable simple quering in the browser without the hazzle of setting the correct mime type.")
	@Path("xsd/")
	@Produces(MimeTypes.MIMETYPE_XSD)
	public Response getXSDAtSubResource() {
		return this.getXSD();
	}

	// @formatter:off
	@POST
	@RestDoc(methodDescription="Updates/creates a property based on XSD element or XML schema.")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response onPost(
		@FormParam("name") @RestDocParam(description="Either xsdelement or xsdtype. 'name' comes from x-editable, which uses that as field name") String name,
		@FormParam("value") @RestDocParam(description="The qname") String value) {
	// @formatter:on
		if (StringUtils.isEmpty(name)) {
			return Response.status(Status.BAD_REQUEST).entity("You have to provide a key/type or a name/value pair").build();
		}
		if (StringUtils.isEmpty(value)) {
			return Response.status(Status.BAD_REQUEST).entity("If a name is provided, a value has also to be provided").build();
		}

		// first of all, remove Winery's Properties definition (if it exists)
		ModelUtilities.removeWinerysPropertiesDefinition(this.getEntityType());

		QName qname = QName.valueOf(value);

		// replace old properties definition by new one
		PropertiesDefinition def = new PropertiesDefinition();
		switch (name) {
			case "xsdtype":
				def.setType(qname);
				break;
			case "xsdelement":
				def.setElement(qname);
				break;
			default:
				return Response.status(Status.BAD_REQUEST).entity("Invalid name. Choose xsdelement or xsdtype").build();
		}
		this.getEntityType().setPropertiesDefinition(def);
		List<String> errors = new ArrayList<>();
		BackendUtils.deriveWPD(this.getEntityType(), errors);
		// currently the errors are just logged
		for (String error : errors) {
			PropertiesDefinitionResource.LOGGER.debug(error);
		}
		return BackendUtils.persist(this.parentRes);

	}

}
