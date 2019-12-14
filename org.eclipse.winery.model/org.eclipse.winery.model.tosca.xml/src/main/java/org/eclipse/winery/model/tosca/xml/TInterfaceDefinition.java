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
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.kvproperties.ParameterDefinition;

/**
 * Class to represent an interface definition in TOSCA YAML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Artifact", propOrder = {
    "name",
    "type",
    "inputs",
    "operations",
})
public class TInterfaceDefinition implements HasName, HasType, Serializable {

    private String name;
    private QName type;
    private List<ParameterDefinition> inputs;
    private List<TOperationDefinition> operations;

    public TInterfaceDefinition() {
    }

    public TInterfaceDefinition(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public QName getType() {
        return type;
    }

    @Override
    public QName getTypeAsQName() {
        return type;
    }

    @Override
    public void setType(QName type) {
        this.type = type;
    }

    public List<ParameterDefinition> getInputs() {
        return inputs;
    }

    public void setInputs(List<ParameterDefinition> inputs) {
        this.inputs = inputs;
    }

    public List<TOperationDefinition> getOperations() {
        return operations;
    }

    public void setOperations(List<TOperationDefinition> operations) {
        this.operations = operations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TInterfaceDefinition that = (TInterfaceDefinition) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
