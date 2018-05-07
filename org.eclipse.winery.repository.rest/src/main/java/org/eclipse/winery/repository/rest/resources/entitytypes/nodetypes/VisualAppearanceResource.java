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
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.constants.QNames;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.GenericVisualAppearanceResource;
import org.eclipse.winery.repository.rest.resources.apiData.NodeTypesVisualsApiData;

import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualAppearanceResource extends GenericVisualAppearanceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisualAppearanceResource.class);


    public VisualAppearanceResource(NodeTypeResource res, Map<QName, String> map, NodeTypeId parentId) {
        super(res, map, new VisualAppearanceId(parentId));
    }

    @GET
    @Path("50x50")
    public Response get50x50Image(@HeaderParam("If-Modified-Since") String modified) {
        return this.getImage(Filename.FILENAME_BIG_ICON, modified);
    }

    @PUT
    @Path("50x50")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response post50x50Image(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body) {
        return this.putImage(Filename.FILENAME_BIG_ICON, uploadedInputStream, body.getMediaType());
    }

    @GET
    @Path("bordercolor")
    public String getBorderColor() {
        return RestUtils.getColor(this.getId().getParent().getXmlId().getDecoded(), QNames.QNAME_BORDER_COLOR, this.otherAttributes, this.res);
    }

    @PUT
    @Path("bordercolor")
    public Response putBorderColor(@FormParam("color") String color) {
        this.otherAttributes.put(QNames.QNAME_BORDER_COLOR, color);
        return RestUtils.persist(this.res);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public NodeTypesVisualsApiData getJsonData() {
        return new NodeTypesVisualsApiData(this);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putJsonData(NodeTypesVisualsApiData data) {
        this.otherAttributes.put(QNames.QNAME_BORDER_COLOR, data.color);
        return RestUtils.persist(this.res);
    }
}
