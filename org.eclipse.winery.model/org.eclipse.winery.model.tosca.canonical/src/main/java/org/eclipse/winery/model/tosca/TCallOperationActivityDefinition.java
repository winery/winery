/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.extensions.kvproperties.ParameterDefinition;

/**
 * Class to represent a call operation activity definition in TOSCA YAML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CallOperationActivityDefinition")
public class TCallOperationActivityDefinition extends TActivityDefinition {
    @XmlElement
    private String operation;
    @XmlTransient
    private List<ParameterDefinition> inputs;

    @Deprecated // used for XML deserialization of API request content
    public TCallOperationActivityDefinition() {
    }

    public TCallOperationActivityDefinition(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public List<ParameterDefinition> getInputs() {
        return inputs;
    }

    public void setInputs(List<ParameterDefinition> inputs) {
        this.inputs = inputs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TCallOperationActivityDefinition that = (TCallOperationActivityDefinition) o;
        return Objects.equals(operation, that.operation) && Objects.equals(inputs, that.inputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation);
    }
}
