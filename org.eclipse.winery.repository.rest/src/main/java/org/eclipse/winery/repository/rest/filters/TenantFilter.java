/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.filebased.TenantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@PreMatching
public class TenantFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantFilter.class);

    private static final String WINERY_TENANT = "xTenant";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();
        MultivaluedMap<String, String> queryParameters = containerRequestContext.getUriInfo().getQueryParameters();

        String tenantName = null;
        if (headers.containsKey(WINERY_TENANT)) {
            tenantName = headers.getFirst(WINERY_TENANT);
        } else if (queryParameters.containsKey(WINERY_TENANT)) {
            tenantName = queryParameters.getFirst(WINERY_TENANT);
        }

        if (tenantName != null) {
            LOGGER.debug("Got new request for tenant: " + tenantName);
            IRepository repo = RepositoryFactory.getRepository();
            if (repo instanceof TenantRepository) {
                ((TenantRepository) repo).useTenant(tenantName);
            }
        }
    }
}
