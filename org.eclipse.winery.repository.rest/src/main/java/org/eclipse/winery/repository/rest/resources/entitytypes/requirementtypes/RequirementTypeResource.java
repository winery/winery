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
package org.eclipse.winery.repository.rest.resources.entitytypes.requirementtypes;

import javax.ws.rs.Path;

import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.repository.rest.resources.EntityTypeResource;

public class RequirementTypeResource extends EntityTypeResource {

	public RequirementTypeResource(RequirementTypeId id) {
		super(id);
	}

	/**
	 * Convenience method to avoid casting at the caller's side.
	 */
	public TRequirementType getRequirementType() {
		return (TRequirementType) this.getElement();
	}

	@Override
	protected TExtensibleElements createNewElement() {
		return new TRequirementType();
	}

	@Path("requiredcapabilitytype/")
	public RequiredCapabilityTypeResource getRequiredCapabilityTypeResource() {
		return new RequiredCapabilityTypeResource(this);
	}
}
