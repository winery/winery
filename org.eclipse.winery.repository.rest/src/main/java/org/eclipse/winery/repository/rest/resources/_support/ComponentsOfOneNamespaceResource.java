/********************************************************************************
 * Copyright (c) 2016-2017 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.rest.RestUtils;

import javax.ws.rs.DELETE;
import javax.ws.rs.core.Response;

public class ComponentsOfOneNamespaceResource {

    private final Class<? extends AbstractComponentsResource> containerClass;
    private final String namespace;

    public ComponentsOfOneNamespaceResource(Class<? extends AbstractComponentsResource> containerClass, String namespace) {
        this.containerClass = containerClass;
        this.namespace = namespace;
    }

    @DELETE
    public Response delete() {
        Class<? extends DefinitionsChildId> idClass = RestUtils.getComponentIdClassForComponentContainer(this.containerClass);
        return RestUtils.delete(idClass, namespace);
    }
}
