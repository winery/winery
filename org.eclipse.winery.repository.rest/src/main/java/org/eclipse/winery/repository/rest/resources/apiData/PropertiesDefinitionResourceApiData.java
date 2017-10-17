/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.rest.resources.apiData;

import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;

public class PropertiesDefinitionResourceApiData {

	public TEntityType.PropertiesDefinition propertiesDefinition;
	public WinerysPropertiesDefinition winerysPropertiesDefinition;
	public PropertiesDefinitionEnum selectedValue;

	public PropertiesDefinitionResourceApiData() {
	}

	public PropertiesDefinitionResourceApiData(
		TEntityType.PropertiesDefinition propertiesDefinition,
		WinerysPropertiesDefinition winerysPropertiesDefinition
	) {
		this.propertiesDefinition = propertiesDefinition;
		this.winerysPropertiesDefinition = winerysPropertiesDefinition;

		if ((winerysPropertiesDefinition != null) && (winerysPropertiesDefinition.getIsDerivedFromXSD() == null)) {
			this.selectedValue = PropertiesDefinitionEnum.Custom;
		} else if ((this.propertiesDefinition != null) && (this.propertiesDefinition.getElement() != null)) {
			this.selectedValue = PropertiesDefinitionEnum.Element;
		} else if ((this.propertiesDefinition != null) && (this.propertiesDefinition.getType() != null)) {
			this.selectedValue = PropertiesDefinitionEnum.Type;
		} else {
			this.selectedValue = PropertiesDefinitionEnum.None;
		}
	}
}
