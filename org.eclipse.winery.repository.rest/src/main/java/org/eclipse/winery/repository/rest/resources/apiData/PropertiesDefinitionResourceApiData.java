/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData;

import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;

// FIXME unify properties exposition on the API
public class PropertiesDefinitionResourceApiData {

    public TEntityType.XmlPropertiesDefinition propertiesDefinition;
    public WinerysPropertiesDefinition winerysPropertiesDefinition;
    public PropertiesDefinitionEnum selectedValue;

    public PropertiesDefinitionResourceApiData() {
    }

    public PropertiesDefinitionResourceApiData(
        TEntityType.XmlPropertiesDefinition propertiesDefinition,
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
