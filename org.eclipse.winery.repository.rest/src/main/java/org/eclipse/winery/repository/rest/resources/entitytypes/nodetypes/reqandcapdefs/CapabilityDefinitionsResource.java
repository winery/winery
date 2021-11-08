/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.model.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

public class CapabilityDefinitionsResource extends RequirementOrCapabilityDefinitionsResource<CapabilityDefinitionResource, TCapabilityDefinition> {

    public CapabilityDefinitionsResource(NodeTypeResource res, List<TCapabilityDefinition> defs) {
        super(CapabilityDefinitionResource.class, TCapabilityDefinition.class, defs, res);
    }

    @Override
    public Collection<QName> getAllTypes() {
        SortedSet<CapabilityTypeId> allDefinitionsChildIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(CapabilityTypeId.class);
        return BackendUtils.convertDefinitionsChildIdCollectionToQNameCollection(allDefinitionsChildIds);
    }

    @Override
    @Path("{id}/")
    public CapabilityDefinitionResource getEntityResource(@PathParam("id") String id) {
        return this.getEntityResourceFromEncodedId(id);
    }
}
