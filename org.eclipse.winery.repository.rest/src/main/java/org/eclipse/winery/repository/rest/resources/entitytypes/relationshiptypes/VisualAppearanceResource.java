/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.relationshiptypes;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.common.constants.Defaults;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.constants.QNames;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.GenericVisualAppearanceResource;
import org.eclipse.winery.repository.rest.resources.apiData.RelationshipTypesVisualsApiData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.Map;

public class VisualAppearanceResource extends GenericVisualAppearanceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisualAppearanceResource.class);

    private static final QName QNAME_ARROWHEAD_SOURCE = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "sourceArrowHead");
    private static final QName QNAME_ARROWHEAD_TARGET = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "targetArrowHead");
    private static final QName QNAME_DASH = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "dash");
    private static final QName QNAME_LINEWIDTH = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "linewidth");
    private static final QName QNAME_HOVER_COLOR = new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "hoverColor");


    public VisualAppearanceResource(RelationshipTypeResource res, Map<QName, String> map, RelationshipTypeId parentId) {
        super(res, map, new VisualAppearanceId(parentId));
    }

    @GET
    @ApiOperation(value = "@return JSON object to be used at jsPlumb.registerConnectionType('NAME', <data>)")
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

            jg.writeStringField("dash", getDash());
            jg.writeStringField("sourceArrowHead", this.getSourceArrowHead());
            jg.writeStringField("targetArrowHead", this.getTargetArrowHead());
            jg.writeStringField("color", this.getColor());
            jg.writeStringField("hoverColor", this.getHoverColor());
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
            jg.writeStringField("label", "(" + this.res.getName() + ")");
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
            VisualAppearanceResource.LOGGER.error(e.getMessage(), e);
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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putJsonData(RelationshipTypesVisualsApiData data) {
        if (data == null) {
            return Response.status(Status.BAD_REQUEST).entity("config must not be empty").build();
        }

        this.otherAttributes.put(VisualAppearanceResource.QNAME_ARROWHEAD_TARGET, data.targetArrowHead);
        this.otherAttributes.put(VisualAppearanceResource.QNAME_ARROWHEAD_SOURCE, data.sourceArrowHead);
        this.otherAttributes.put(VisualAppearanceResource.QNAME_DASH, data.dash);
        this.otherAttributes.put(VisualAppearanceResource.QNAME_HOVER_COLOR, data.hoverColor);
        this.otherAttributes.put(QNames.QNAME_COLOR, data.color);
        return RestUtils.persist(this.res);
    }

    /* * * source arrow head * * */
    public String getSourceArrowHead() {
        return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_ARROWHEAD_SOURCE, Defaults.DEFAULT_RT_ARROWHEAD_SOURCE);
    }

    /* * * target arrow head * * */
    public String getTargetArrowHead() {
        return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_ARROWHEAD_TARGET, Defaults.DEFAULT_RT_ARROWHEAD_TARGET);
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

    /* * * stroke/line width * * */

    public String getLineWidth() {
        return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_LINEWIDTH, Defaults.DEFAULT_RT_LINEWIDTH);
    }

    /* * * color * * */

    public String getColor() {
        return RestUtils.getColor(this.getId().getParent().getXmlId().getDecoded(), QNames.QNAME_COLOR, this.otherAttributes, this.res);
    }

    public String getHoverColor() {
        return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_HOVER_COLOR, Defaults.DEFAULT_RT_HOVER_COLOR);
    }
}
