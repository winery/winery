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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AttributeDefinitions")
public class AttributeDefinitions implements Serializable {

    private List<AttributeDefinition> attributeDefinitions;
    
    public AttributeDefinitions() {
    }

    public AttributeDefinitions(Collection<? extends AttributeDefinition> c) {
        this.attributeDefinitions = new ArrayList<>(c);
    }

    @XmlElement(name = "AttributeDefinition")
    public List<AttributeDefinition> getAttributeDefinitions() {
        if (this.attributeDefinitions == null) {
            this.attributeDefinitions = new ArrayList<>();
        }
        return this.attributeDefinitions;
    }
}
