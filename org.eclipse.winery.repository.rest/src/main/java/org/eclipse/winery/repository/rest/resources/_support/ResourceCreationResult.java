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
package org.eclipse.winery.repository.rest.resources._support;

import org.eclipse.winery.common.ids.GenericId;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;

public class ResourceCreationResult {

    private Status status = null;
    private URI uri = null;
    private GenericId id = null;


    public ResourceCreationResult() {
    }

    public ResourceCreationResult(Status status) {
        this.setStatus(status);
    }

    public ResourceCreationResult(Status status, URI uri, GenericId id) {
        this.setStatus(status);
        this.setId(id);
        this.setUri(uri);
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public URI getUri() {
        return this.uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public GenericId getId() {
        return this.id;
    }

    public void setId(GenericId id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return this.getStatus() == Status.CREATED;
    }

    /**
     * The possibly existing URI is used as location in Response.created
     *
     * @return a Response created based on the contained data
     */
    public Response getResponse() {
        Response res;
        if (this.getUri() == null) {
            res = Response.status(this.getStatus()).build();
        } else {
            assert (this.getStatus().equals(Status.CREATED));
            res = Response.created(this.getUri()).build();
        }
        return res;
    }
}
