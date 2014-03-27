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
package org.eclipse.winery.repository.resources.entitytypes.properties;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.EntityTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.properties.winery.WinerysPropertiesDefinitionResource;
import org.restdoc.annotations.RestDoc;
import org.restdoc.annotations.RestDocParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.Viewable;

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
	
	private static final Logger logger = LoggerFactory.getLogger(PropertiesDefinitionResource.class);
	
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
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHTML() {
		return new Viewable("/jsp/entitytypes/properties/propertiesDefinition.jsp", new JSPData(this, this.wpd));
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
		if (name.equals("xsdtype")) {
			def.setType(qname);
		} else if (name.equals("xsdelement")) {
			def.setElement(qname);
		} else {
			return Response.status(Status.BAD_REQUEST).entity("Invalid name. Choose xsdelement or xsdtype").build();
		}
		this.getEntityType().setPropertiesDefinition(def);
		List<String> errors = new ArrayList<>();
		BackendUtils.deriveWPD(this.getEntityType(), errors);
		// currently the errors are just logged
		for (String error : errors) {
			PropertiesDefinitionResource.logger.debug(error);
		}
		return BackendUtils.persist(this.parentRes);
		
	}
	
}