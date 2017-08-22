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
package org.eclipse.winery.repository.rest.resources;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.elements.TOSCAElementId;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.entitytypes.TopologyGraphElementEntityTypeResource;

import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

//import com.fasterxml.jackson.annotation.JsonIgnore; // currently not required

/**
 * Contains methods for both visual appearance for
 * <ul>
 * <li>node types</li>
 * <li>relationship types</li>
 * </ul>
 */
public abstract class GenericVisualAppearanceResource {

	protected final Map<QName, String> otherAttributes;
	protected final TopologyGraphElementEntityTypeResource res;
	protected final TOSCAElementId id;


	/**
	 * @param otherAttributes the other attributes of the node/relationship type
	 * @param id              the id of this subresource required for storing the images
	 */
	public GenericVisualAppearanceResource(TopologyGraphElementEntityTypeResource res, Map<QName, String> otherAttributes, VisualAppearanceId id) {
		this.id = id;
		this.res = res;
		this.otherAttributes = otherAttributes;
	}

	@DELETE
	public Response onDelete() {
		return RestUtils.delete(this.id);
	}

	/**
	 * Used for GUI when accessing the resource as data E.g., for topology
	 * template
	 */
	//@JsonIgnore
	public URI getAbsoluteURL() {
		String URI = Environment.getUrlConfiguration().getRepositoryApiUrl();
		URI = URI + "/" + Util.getUrlPath(this.id);
		return RestUtils.createURI(URI);
	}

	//@JsonIgnore
	public TOSCAElementId getId() {
		return this.id;
	}

	/**
	 * Determines repository reference to file in repo
	 */
	protected RepositoryFileReference getRepoFileRef(String name) {
		return new RepositoryFileReference(this.id, name);
	}

	protected Response getImage(String name, String modified) {
		RepositoryFileReference target = this.getRepoFileRef(name);
		return RestUtils.returnRepoPath(target, modified);
	}

	/**
	 * Arbitrary images are supported. There currently is no check for valid
	 * image media types
	 */
	protected Response putImage(String name, InputStream uploadedInputStream, MediaType mediaType) {
		RepositoryFileReference target = this.getRepoFileRef(name);
		return RestUtils.putContentToFile(target, uploadedInputStream, mediaType);
	}

	@GET
	@Path("16x16")
	public Response get16x16Image(@HeaderParam("If-Modified-Since") String modified) {
		// Even if the extension is "png", it might contain a jpg, too
		// We keep the file extension as the windows explorer can display previews even if the content is not a png
		return this.getImage(Filename.FILENAME_SMALL_ICON, modified);
	}

	@PUT
	@Path("16x16")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response post16x16Image(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart body) {
		return this.putImage(Filename.FILENAME_SMALL_ICON, uploadedInputStream, body.getMediaType());
	}
}
