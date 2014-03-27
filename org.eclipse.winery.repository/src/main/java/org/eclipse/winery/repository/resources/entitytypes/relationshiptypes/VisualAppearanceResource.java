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
 *     Jerome Tagliaferri - support for setting the color
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytypes.relationshiptypes;

import java.io.StringWriter;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.constants.Defaults;
import org.eclipse.winery.common.constants.Namespaces;
import org.eclipse.winery.common.constants.QNames;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.resources.GenericVisualAppearanceResource;
import org.restdoc.annotations.RestDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.sun.jersey.api.view.Viewable;

public class VisualAppearanceResource extends GenericVisualAppearanceResource {
	
	private static final Logger logger = LoggerFactory.getLogger(VisualAppearanceResource.class);
	
	private static final QName QNAME_ARROWHEAD_SOURCE = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "sourceArrowHead");
	private static final QName QNAME_ARROWHEAD_TARGET = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "targetArrowHead");
	private static final QName QNAME_DASH = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "dash");
	private static final QName QNAME_LINEWIDTH = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "linewidth");
	private static final QName QNAME_HOVER_COLOR = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "hovercolor");
	
	
	public VisualAppearanceResource(RelationshipTypeResource res, Map<QName, String> map, RelationshipTypeId parentId) {
		super(res, map, new VisualAppearanceId(parentId));
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getHTML() {
		Viewable viewable = new Viewable("/jsp/entitytypes/relationshiptypes/visualappearance.jsp", this);
		return Response.ok().entity(viewable).build();
	}
	
	@GET
	@RestDoc(methodDescription = "@return JSON object to be used at jsPlumb.registerConnectionType('NAME', <data>)")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConnectionTypeForJsPlumbData() {
		JsonFactory jsonFactory = new JsonFactory();
		StringWriter sw = new StringWriter();
		try {
			JsonGenerator jg = jsonFactory.createGenerator(sw);
			jg.writeStartObject();
			
			jg.writeFieldName("connector");
			jg.writeString("Flowchart");
			
			jg.writeFieldName("paintStyle");
			jg.writeStartObject();
			jg.writeFieldName("lineWidth");
			jg.writeNumber(this.getLineWidth());
			jg.writeFieldName("strokeStyle");
			jg.writeObject(this.getColor());
			String dash = this.getDash();
			if (!StringUtils.isEmpty(dash)) {
				String dashStyle = null;
				switch (dash) {
				case "dotted":
					dashStyle = "1 5";
					break;
				case "dotted2":
					dashStyle = "3 4";
					break;
				case "plain":
					// default works
					// otherwise, "1 0" can be used
					break;
				}
				if (dashStyle != null) {
					jg.writeStringField("dashstyle", dashStyle);
				}
			}
			jg.writeEndObject();
			
			jg.writeFieldName("hoverPaintStyle");
			jg.writeStartObject();
			jg.writeFieldName("strokeStyle");
			jg.writeObject(this.getHoverColor());
			jg.writeEndObject();
			
			// BEGIN: Overlays
			
			jg.writeFieldName("overlays");
			jg.writeStartArray();
			
			// source arrow head
			String head = this.getSourceArrowHead();
			if (!head.equals("none")) {
				jg.writeStartArray();
				jg.writeString(head);
				
				jg.writeStartObject();
				
				jg.writeFieldName("location");
				jg.writeNumber(0);
				
				// arrow should point towards the node and not away from it
				jg.writeFieldName("direction");
				jg.writeNumber(-1);
				
				jg.writeFieldName("width");
				jg.writeNumber(20);
				
				jg.writeFieldName("length");
				jg.writeNumber(12);
				
				jg.writeEndObject();
				jg.writeEndArray();
			}
			
			// target arrow head
			head = this.getTargetArrowHead();
			if (!head.equals("none")) {
				jg.writeStartArray();
				jg.writeString(head);
				jg.writeStartObject();
				jg.writeFieldName("location");
				jg.writeNumber(1);
				jg.writeFieldName("width");
				jg.writeNumber(20);
				jg.writeFieldName("length");
				jg.writeNumber(12);
				jg.writeEndObject();
				jg.writeEndArray();
			}
			
			// Type in brackets on the arrow
			jg.writeStartArray();
			jg.writeString("Label");
			jg.writeStartObject();
			jg.writeStringField("id", "label");
			jg.writeStringField("label", "(" + ((RelationshipTypeResource) this.res).getName() + ")");
			jg.writeStringField("cssClass", "relationshipTypeLabel");
			jg.writeFieldName("location");
			jg.writeNumber(0.5);
			jg.writeEndObject();
			jg.writeEndArray();
			
			jg.writeEndArray();
			
			// END: Overlays
			
			jg.writeEndObject();
			
			jg.close();
		} catch (Exception e) {
			VisualAppearanceResource.logger.error(e.getMessage(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
		String res = sw.toString();
		return Response.ok(res).build();
	}
	
	private String getOtherAttributeWithDefault(QName qname, String def) {
		String res = this.otherAttributes.get(qname);
		if (StringUtils.isEmpty(res)) {
			return def;
		} else {
			return res;
		}
	}
	
	/* * * source arrow head * * */
	
	public String getSourceArrowHead() {
		return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_ARROWHEAD_SOURCE, Defaults.DEFAULT_RT_ARROWHEAD_SOURCE);
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("sourcearrowhead")
	public Response onPutSourceHead(String config) {
		if (StringUtils.isEmpty(config)) {
			return Response.status(Status.BAD_REQUEST).entity("config must not be empty").build();
		}
		this.otherAttributes.put(VisualAppearanceResource.QNAME_ARROWHEAD_SOURCE, config);
		return BackendUtils.persist(this.res);
	}
	
	/* * * target arrow head * * */
	
	public String getTargetArrowHead() {
		return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_ARROWHEAD_TARGET, Defaults.DEFAULT_RT_ARROWHEAD_TARGET);
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("targetarrowhead")
	public Response onPutTargetHead(String config) {
		if (StringUtils.isEmpty(config)) {
			return Response.status(Status.BAD_REQUEST).entity("config must not be empty").build();
		}
		this.otherAttributes.put(VisualAppearanceResource.QNAME_ARROWHEAD_TARGET, config);
		return BackendUtils.persist(this.res);
	}
	
	/* * *
	 *
	 * stroke dash array / represents the line
	 *
	 * Attention: if a linewidth != 1 is chosen, the dash has to be multiplied somehow by the line width
	 * See: http://jsplumbtoolkit.com/doc/paint-styles:
	 * "The dashstyle attribute is specified as an array of strokes and spaces, where each value is some multiple of the width of the Connector"
	 *
	 * * * */
	
	public String getDash() {
		return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_DASH, Defaults.DEFAULT_RT_DASH);
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("dash")
	public Response onPutDash(String config) {
		if (StringUtils.isEmpty(config)) {
			return Response.status(Status.BAD_REQUEST).entity("config must not be empty").build();
		}
		this.otherAttributes.put(VisualAppearanceResource.QNAME_DASH, config);
		return BackendUtils.persist(this.res);
	}
	
	/* * * stroke/line width * * */
	
	public String getLineWidth() {
		return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_LINEWIDTH, Defaults.DEFAULT_RT_LINEWIDTH);
	}
	
	/* * * color * * */
	
	/**
	 * read by topologytemplateeditor.jsp via ${it.color}
	 */
	public String getColor() {
		return BackendUtils.getColorAndSetDefaultIfNotExisting(this.getId().getParent().getXmlId().getDecoded(), QNames.QNAME_COLOR, this.otherAttributes, this.res);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("color")
	public Response onPutColor(@FormParam("color") String color) {
		this.otherAttributes.put(QNames.QNAME_COLOR, color);
		return BackendUtils.persist(this.res);
	}
	
	/**
	 * read by topologytemplateeditor.jsp via ${it.hoverColor}
	 */
	public String getHoverColor() {
		return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_HOVER_COLOR, Defaults.DEFAULT_RT_HOVER_COLOR);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("hovercolor")
	public Response onPutHoverColor(@FormParam("color") String color) {
		this.otherAttributes.put(VisualAppearanceResource.QNAME_HOVER_COLOR, color);
		return BackendUtils.persist(this.res);
	}
}
