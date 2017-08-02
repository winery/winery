/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Tino Stadelmaier, Philipp Meyer - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.apiData;

import java.util.List;

import org.eclipse.winery.model.tosca.TCapabilityDefinition;

public class CapabilityDefinitionsResourceApiData {

	public List<TCapabilityDefinition> capabilityDefinitionsList;

	public CapabilityDefinitionsResourceApiData() {
	}

	public CapabilityDefinitionsResourceApiData(List<TCapabilityDefinition> list) {
		this.capabilityDefinitionsList = list;
	}
}

