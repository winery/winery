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
package org.eclipse.winery.repository.rest.resources.admin;

import javax.ws.rs.DELETE;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.ids.admin.AdminId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;

import org.apache.commons.configuration.Configuration;

/**
 * Instance of one admin resource
 *
 * Offers a configuration object to store data
 */
public abstract class AbstractAdminResource {

	protected final Configuration configuration;

	private final AdminId id;

	/**
	 * @param id the id of the element rendered by this resource
	 */
	protected AbstractAdminResource(AdminId id) {
		this.id = id;
		this.configuration = RepositoryFactory.getRepository().getConfiguration(id);
	}

	@DELETE
	public Response onDelete() {
		return RestUtils.delete(this.id);
	}
}
