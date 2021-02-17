/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.constants.Defaults;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.constants.QNames;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.GenericVisualAppearanceResource;
import org.eclipse.winery.repository.rest.resources.apiData.RelationshipTypesVisualsApiData;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public RelationshipTypesVisualsApiData getJsonData(@Context UriInfo uriInfo) {
        return new RelationshipTypesVisualsApiData(this);
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
        return RestUtils.getColor(this.getId().getParent().getXmlId().getDecoded(), QNames.QNAME_COLOR, this.otherAttributes);
    }

    public String getHoverColor() {
        return this.getOtherAttributeWithDefault(VisualAppearanceResource.QNAME_HOVER_COLOR, Defaults.DEFAULT_RT_HOVER_COLOR);
    }
}
