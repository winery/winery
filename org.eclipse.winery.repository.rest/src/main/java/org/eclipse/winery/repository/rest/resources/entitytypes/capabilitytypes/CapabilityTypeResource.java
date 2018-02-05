/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes.capabilitytypes;

import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.rest.resources.entitytypes.EntityTypeResource;

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
