/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.resources.apiData.boundarydefinitions;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;

public class PropertyMappingsApi {

	public TBoundaryDefinitions.Properties.PropertyMappings propertyMappings;

	public PropertyMappingsApi() { }

	public PropertyMappingsApi(TServiceTemplate ste) {
		this.propertyMappings = ste.getBoundaryDefinitions().getProperties().getPropertyMappings();
	}
}
