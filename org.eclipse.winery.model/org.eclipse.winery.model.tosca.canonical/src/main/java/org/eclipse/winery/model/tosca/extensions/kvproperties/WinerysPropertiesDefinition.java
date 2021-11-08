/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.extensions.kvproperties;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.constants.Namespaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@XmlRootElement(name = "PropertiesDefinition")
@XmlAccessorType(XmlAccessType.PROPERTY)
/**
 * This is Winery's main extension element for a key/value based properties definition.
 * To be representable in the canonical model it directly implements the marker interface used for storing
 * PropertiesDefinitions {@link org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition}
 */
@JsonDeserialize(as = WinerysPropertiesDefinition.class)
public class WinerysPropertiesDefinition extends TEntityType.PropertiesDefinition implements Serializable {

    @JsonProperty
    private String namespace;
    @JsonProperty
    private String elementName;
    @JsonProperty("propertyDefinitionKVList")
    private List<PropertyDefinitionKV> propertyDefinitions;
    @JsonProperty
    private Boolean isDerivedFromXSD = Boolean.FALSE;

    @XmlAttribute(name = "namespace")
    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @XmlAttribute(name = "elementname")
    public String getElementName() {
        return this.elementName;
    }

    public void setElementName(String localName) {
        this.elementName = localName;
    }

    @XmlElement(name = "properties")
    public List<PropertyDefinitionKV> getPropertyDefinitions() {
        return propertyDefinitions;
    }

    public void setPropertyDefinitions(List<PropertyDefinitionKV> propertyDefinitions) {
        this.propertyDefinitions = propertyDefinitions;
    }

    /**
     * @return null if not derived from XSD, "Boolean.TRUE" otherwise. This leads JAXB to write the attribute only if
     * derivedFromXSD is true
     */
    @XmlAttribute(name = "derivedFromXSD", namespace = Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE)
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
