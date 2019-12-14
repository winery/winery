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
package org.eclipse.winery.repository.rest.resources.admin;

import org.apache.commons.configuration2.Configuration;
import org.eclipse.winery.model.ids.admin.AdminId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;

import javax.ws.rs.DELETE;
import javax.ws.rs.core.Response;

/**
 * Instance of one admin resource
 * <p>
 * Offers a configuration object to store data
 */
public abstract class AbstractAdminResource {

    protected final Configuration configuration;

    private final AdminId id;

    /**
     * Constructor used by child classes.
     *
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
