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
package org.eclipse.winery.repository.rest.resources._support;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.common.Util;
import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.model.ids.elements.ToscaElementId;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.VisualsApiData;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
    protected final AbstractComponentInstanceResource res;
    protected final ToscaElementId id;

    /**
     * @param otherAttributes the other attributes of the node/relationship type
     * @param id              the id of this subresource required for storing the images
     */
    public GenericVisualAppearanceResource(AbstractComponentInstanceResource res, Map<QName, String> otherAttributes, ToscaElementId id) {
        this.id = id;
        this.res = res;
        this.otherAttributes = otherAttributes;
    }

    @DELETE
    public Response onDelete() {
        return RestUtils.delete(this.id);
    }

    /**
     * Used for GUI when accessing the resource as data E.g., for topology template
     */
    public URI getAbsoluteURL() {
        String uri = Environments.getInstance().getUiConfig().getEndpoints().get("repositoryApiUrl");
        uri = uri + "/" + Util.getUrlPath(this.id);
        return URI.create(uri);
    }

    public URI getAbsoluteURL(UriInfo uriInfo) {
        String uri = uriInfo.getBaseUri().toString();
        uri = uri + Util.getUrlPath(this.id);
        return URI.create(uri);
    }

    public ToscaElementId getId() {
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
     * Arbitrary images are supported. There currently is no check for valid image media types
     */
    protected Response putImage(String name, InputStream uploadedInputStream, MediaType mediaType) {
        RepositoryFileReference target = this.getRepoFileRef(name);
        return RestUtils.putContentToFile(target, uploadedInputStream, mediaType);
    }

    public abstract VisualsApiData getJsonData(@Context UriInfo uriInfo);

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

    public abstract String getColor();
}
