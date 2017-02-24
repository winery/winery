/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.resources.apiData;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TEntityType;

public class PropertiesDefinitionResourceApiData {

	public TEntityType.PropertiesDefinition propertiesDefinition;
	public WinerysPropertiesDefinition winerysPropertiesDefinition;

	public PropertiesDefinitionResourceApiData() {}

	public PropertiesDefinitionResourceApiData(
			TEntityType.PropertiesDefinition propertiesDefinition,
			WinerysPropertiesDefinition winerysPropertiesDefinition
	) {
		this.propertiesDefinition = propertiesDefinition;
		this.winerysPropertiesDefinition = winerysPropertiesDefinition;
	}
}
