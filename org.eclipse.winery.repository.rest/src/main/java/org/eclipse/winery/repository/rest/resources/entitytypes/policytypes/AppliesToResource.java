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
package org.eclipse.winery.repository.rest.resources.entitytypes.policytypes;

import org.eclipse.winery.model.tosca.TPolicyType;

public class AppliesToResource {

	private final PolicyTypeResource policyTypeResource;


	public AppliesToResource(PolicyTypeResource policyTypeResource) {
		this.policyTypeResource = policyTypeResource;
	}

	public TPolicyType getPolicyType() {
		return this.policyTypeResource.getPolicyType();
	}
}
