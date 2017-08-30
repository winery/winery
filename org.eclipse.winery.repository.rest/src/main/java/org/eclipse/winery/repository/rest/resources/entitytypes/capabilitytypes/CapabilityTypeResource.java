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
package org.eclipse.winery.repository.rest.resources.entitytypes.capabilitytypes;

import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.rest.resources.EntityTypeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CapabilityTypeResource extends EntityTypeResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(CapabilityTypeResource.class);


	/**
	 * Constructor has to be public because of test cases
	 */
	public CapabilityTypeResource(CapabilityTypeId id) {
		super(id);
	}

	/**
	 * Convenience method to avoid casting at the caller's side.
	 *
	 * @return the CapabilityType object this resource is representing
	 */
	public TCapabilityType getCapabilityType() {
		return (TCapabilityType) this.getElement();
	}

	@Override
	protected TExtensibleElements createNewElement() {
		return new TCapabilityType();
	}

}
