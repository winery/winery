/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.reqandcapdefs;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;

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
}
