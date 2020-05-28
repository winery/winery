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
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.reqandcapdefs;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.RepositoryUtils;
import org.eclipse.winery.repository.rest.resources.apiData.RequirementOrCapabilityDefinitionPostData;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;

import org.apache.commons.lang3.StringUtils;

public class RequirementDefinitionsResource extends RequirementOrCapabilityDefinitionsResource<RequirementDefinitionResource, TRequirementDefinition> {

    public RequirementDefinitionsResource(NodeTypeResource res, List<TRequirementDefinition> defs) {
        super(RequirementDefinitionResource.class, TRequirementDefinition.class, defs, res);
    }

    @Override
    public Collection<QName> getAllTypes() {
        SortedSet<RequirementTypeId> allDefinitionsChildIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(RequirementTypeId.class);
        return BackendUtils.convertDefinitionsChildIdCollectionToQNameCollection(allDefinitionsChildIds);
    }

    @Override
    @Path("{id}/")
    public RequirementDefinitionResource getEntityResource(@PathParam("id") String id) {
        return this.getEntityResourceFromEncodedId(id);
    }

    @Override
    public Response performPost(RequirementOrCapabilityDefinitionPostData postData) {
        // if we are in XML mode, we delegate to the parent
        if (!RepositoryUtils.isYamlRepository()) {
            return super.performPost(postData);
        }

        // otherwise, we do it the YAML way!!
        if (StringUtils.isEmpty(postData.name)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Name has to be provided").build();
        }

        if (StringUtils.isEmpty(postData.capability)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Capability Type has to be provided").build();
        }

        TRequirementDefinition def = super.createBasicReqOrCapDef(postData);
        def.setCapability(QName.valueOf(postData.capability));

        if (!StringUtils.isEmpty(postData.node)) {
            def.setNode(QName.valueOf(postData.node));
        }

        if (!StringUtils.isEmpty(postData.relationship)) {
            def.setRelationship(QName.valueOf(postData.relationship));
        }

        return super.persistDef(def, postData);
    }
}
