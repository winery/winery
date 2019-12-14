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

package org.eclipse.winery.model.tosca.xml;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.kvproperties.ParameterDefinition;

/**
 * Class to represent an property definition in TOSCA YAML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Operation", propOrder = {
    "name",
    "type",
    "description",
    "inputs",
    "outputs"
})
public class TPropertyDefinition implements HasName, Serializable {
    private String name;
    private String type;
    private String description;
    private List<ParameterDefinition> inputs;
    private List<ParameterDefinition> outputs;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String value) {

    }

    @Override
    public String getIdFromIdOrNameField() {
        return null;
    }

    @Override
    public void setId(String id) {

    }
}
