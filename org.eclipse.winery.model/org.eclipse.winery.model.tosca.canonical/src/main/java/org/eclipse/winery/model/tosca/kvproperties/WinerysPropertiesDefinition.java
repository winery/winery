/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.kvproperties;

import java.io.Serializable;

/**
 * This is Winery's main extension element for a key/value based properties definition
 */
public class WinerysPropertiesDefinition implements Serializable {

    private String namespace;
    private String elementName;
    private PropertyDefinitionKVList propertyDefinitionKVList;
    private Boolean isDerivedFromXSD = Boolean.FALSE;

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getElementName() {
        return this.elementName;
    }

    public void setElementName(String localName) {
        this.elementName = localName;
    }

    public PropertyDefinitionKVList getPropertyDefinitionKVList() {
        return this.propertyDefinitionKVList;
    }

    public void setPropertyDefinitionKVList(PropertyDefinitionKVList propertyDefinitionKVList) {
        this.propertyDefinitionKVList = propertyDefinitionKVList;
    }

    /**
     * @return null if not derived from XSD, "Boolean.TRUE" otherwise. This leads JAXB to write the attribute only if
     * derivedFromXSD is true
     */
    public Boolean getIsDerivedFromXSD() {
        if ((this.isDerivedFromXSD != null) && (this.isDerivedFromXSD)) {
            return Boolean.TRUE;
        } else {
            return null;
        }
    }

    public void setIsDerivedFromXSD(Boolean isDerivedFromXSD) {
        this.isDerivedFromXSD = isDerivedFromXSD;
    }
}
