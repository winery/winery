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
package org.eclipse.winery.model.tosca;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.extensions.kvproperties.ParameterDefinition;

/**
 * Class to represent an operations definition in TOSCA YAML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Operation", propOrder = {
    "name",
    "description",
    "inputs",
    "outputs",
    "implementation",
})
public class TOperationDefinition implements HasName, Serializable {

    private String name;
    private String description;
    private List<ParameterDefinition> inputs;
    private List<ParameterDefinition> outputs;
    private TImplementation implementation;

    @Deprecated // used for XML deserialization of API request content
    public TOperationDefinition() { }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ParameterDefinition> getInputs() {
        return inputs;
    }

    public void setInputs(List<ParameterDefinition> inputs) {
        this.inputs = inputs;
    }

    public List<ParameterDefinition> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ParameterDefinition> outputs) {
        this.outputs = outputs;
    }

    public TImplementation getImplementation() {
        return implementation;
    }

    public void setImplementation(TImplementation implementation) {
        this.implementation = implementation;
    }
}
