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

import org.eclipse.winery.model.jsonsupport.PropertiesDefinitionDeserializer;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class PropertiesDefinitionResourceApiData {

    public TEntityType.PropertiesDefinition propertiesDefinition;
    @JsonDeserialize(using = PropertiesDefinitionDeserializer.class)
    public WinerysPropertiesDefinition winerysPropertiesDefinition;
    public PropertiesDefinitionEnum selectedValue;

    @SuppressWarnings("unused") // required for JSON deserialization
    public PropertiesDefinitionResourceApiData() { }

    public PropertiesDefinitionResourceApiData(
        TEntityType.PropertiesDefinition propertiesDefinition,
        WinerysPropertiesDefinition winerysPropertiesDefinition
    ) {
        this.propertiesDefinition = propertiesDefinition;
        this.winerysPropertiesDefinition = winerysPropertiesDefinition;

        if (propertiesDefinition == null) {
            this.selectedValue = PropertiesDefinitionEnum.None;
        } else if (propertiesDefinition instanceof TEntityType.XmlElementDefinition) {
            this.selectedValue = PropertiesDefinitionEnum.Element;
        } else if (propertiesDefinition instanceof TEntityType.XmlTypeDefinition) {
            this.selectedValue = PropertiesDefinitionEnum.Type;
        } else if (propertiesDefinition instanceof WinerysPropertiesDefinition) {
            this.selectedValue = PropertiesDefinitionEnum.Custom;
        } else if (propertiesDefinition instanceof TEntityType.YamlPropertiesDefinition) {
            this.selectedValue = PropertiesDefinitionEnum.Yaml;
        } else {
            throw new IllegalStateException("Properties Definition Type was unknown");
        }
    }
}
