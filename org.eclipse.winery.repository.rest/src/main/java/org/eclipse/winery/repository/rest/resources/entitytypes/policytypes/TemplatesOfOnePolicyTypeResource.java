/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.policytypes;

import java.util.Collection;

import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.entitytypes.TemplatesOfOneType;

public class TemplatesOfOnePolicyTypeResource extends TemplatesOfOneType {

	private PolicyTypeId policyTypeId;
	/**
	 * Resource returns all templates/implementations of the given policy type
	 * @param policyTypeId the Id of the policy type
	 */
	public TemplatesOfOnePolicyTypeResource(PolicyTypeId policyTypeId) {
		this.policyTypeId = policyTypeId;
	}

	@Override
	public Collection<PolicyTemplateId> getAllImplementations() {
		return RepositoryFactory.getRepository().getAllElementsReferencingGivenType(PolicyTemplateId.class, this.policyTypeId.getQName());
	}
}
