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
package org.eclipse.winery.repository.resources.entitytypes.nodetypes;

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

import org.eclipse.winery.common.constants.QNames;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.resources.GenericVisualAppearanceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

public class VisualAppearanceResource extends GenericVisualAppearanceResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisualAppearanceResource.class);


	public VisualAppearanceResource(NodeTypeResource res, Map<QName, String> map, NodeTypeId parentId) {
		super(res, map, new VisualAppearanceId(parentId));
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getHTML() {
		Viewable viewable = new Viewable("/jsp/entitytypes/nodetypes/visualappearance.jsp", this);
		return Response.ok().entity(viewable).build();
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
		return BackendUtils.getColorAndSetDefaultIfNotExisting(this.getId().getParent().getXmlId().getDecoded(), QNames.QNAME_BORDER_COLOR, this.otherAttributes, this.res);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("bordercolor")
	public Response putBorderColor(@FormParam("color") String color) {
		this.otherAttributes.put(QNames.QNAME_BORDER_COLOR, color);
		return BackendUtils.persist(this.res);
	}

}
