/********************************************************************************
 * Copyright (c) 2014 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.policies;

import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.withoutid.EntityWithoutIdCollectionResource;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;

public class PoliciesResource extends EntityWithoutIdCollectionResource<PolicyResource, TPolicy> {

    public PoliciesResource(List<TPolicy> list, IPersistable res) {
        super(PolicyResource.class, TPolicy.class, list, res);
    }

    @PUT
    public Response replaceAll(List<TPolicy> newList) {
        this.list.clear();
        for (TPolicy policy : newList) {
            this.list.add(policy);
        }
        return RestUtils.persist(this.res);
    }

    @Override
    @Path("{id}/")
    public PolicyResource getEntityResource(@PathParam("id") String id) {
        return this.getEntityResourceFromEncodedId(id);
    }

}
