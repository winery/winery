/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.reqandcapdefs;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilityDefinitionsResource extends RequirementOrCapabilityDefinitionsResource<CapabilityDefinitionResource, TCapabilityDefinition> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CapabilityDefinitionsResource.class);


	public CapabilityDefinitionsResource(NodeTypeResource res, List<TCapabilityDefinition> defs) {
		super(CapabilityDefinitionResource.class, TCapabilityDefinition.class, defs, res);
	}

	@Override
	public Collection<QName> getAllTypes() {
		SortedSet<CapabilityTypeId> allDefinitionsChildIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(CapabilityTypeId.class);
		return BackendUtils.convertDefinitionsChildIdCollectionToQNameCollection(allDefinitionsChildIds);
	}
}
