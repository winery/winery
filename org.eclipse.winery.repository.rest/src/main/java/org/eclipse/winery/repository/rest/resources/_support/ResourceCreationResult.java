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
package org.eclipse.winery.repository.rest.resources._support;

import java.net.URI;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.ids.GenericId;

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
