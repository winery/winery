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
package org.eclipse.winery.repository.rest.resources.entitytemplates.policytemplates;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentsResource;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentsWithTypeReferenceResource;
import org.eclipse.winery.repository.rest.resources.apiData.VisualsApiData;

import io.swagger.annotations.Api;

/**
 * Manages all policy types in all available namespaces. The actual implementation is done in the
 * AbstractComponentsWithTypeReferenceResource
 */
@Api(tags = "Policy Templates")
public class PolicyTemplatesResource extends AbstractComponentsWithTypeReferenceResource<PolicyTemplateResource> {

    @Path("{namespace}/{id}/")
    public PolicyTemplateResource getComponentInstanceResource(@PathParam("namespace") String namespace, @PathParam("id") String id) {
        return this.getComponentInstanceResource(namespace, id, true);
    }

    @GET
    @Path("allvisualappearancedata")
    @Produces(MediaType.APPLICATION_JSON)
    public List<VisualsApiData> getVisualAppearanceList(@Context UriInfo uriInfo) {
        SortedSet<PolicyTemplateId> policyTemplateIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(PolicyTemplateId.class);
        return policyTemplateIds.stream()
            .map(id -> {
                PolicyTemplateResource res = (PolicyTemplateResource) AbstractComponentsResource.getComponentInstanceResource(id);
                return res.getVisualAppearanceResource().getJsonData(uriInfo);
            })
            .collect(Collectors.toList());
    }
}
