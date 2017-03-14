/*******************************************************************************
 * Copyright (c) 2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;

import com.sun.jersey.api.view.Viewable;

public class ComponentsOfOneNamespaceResource {

    private final Class<? extends AbstractComponentsResource> containerClass;
    private final String namespace;

    public ComponentsOfOneNamespaceResource(Class<? extends AbstractComponentsResource> containerClass, String namespace) {
        this.containerClass = containerClass;
        this.namespace = namespace;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getAllResourcesInNamespace() {
        return Response.ok().entity(new Viewable("/jsp/genericcomponentpage.jsp", new GenericComponentPageData(this.containerClass, Util.URLdecode(namespace)))).build();
    }

    @DELETE
    public Response delete() {
        Class<? extends TOSCAComponentId> idClass = Utils.getComponentIdClassForComponentContainer(this.containerClass);
        return BackendUtils.delete(idClass, namespace);
    }

}
