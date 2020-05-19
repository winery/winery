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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Class to represent an interface type in TOSCA YAML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tInterfaceType", propOrder = {
    "description",
    "operations"
//    "inputs"
})
public class TInterfaceType extends TEntityType {
    private String description;
    //private Map<String, TPropertyDefinition> inputs;
    private Map<String, TOperationDefinition> operations;

    public TInterfaceType() {
    }

    public TInterfaceType(Builder builder) {
        super(builder);
        this.setOperations(builder.operations);
        //this.setInputs(builder.inputs);
        this.setDescription(builder.description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TInterfaceType)) return false;
        if (!super.equals(o)) return false;
        TInterfaceType that = (TInterfaceType) o;
        return Objects.equals(getOperations(), that.getOperations()) /*&&
            Objects.equals(getInputs(), that.getInputs())*/;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getOperations()/*, getInputs()*/);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "TInterfaceType{" +
            "operations=" + getOperations() +
            //", inputs=" + getInputs() +
            "} " + super.toString();
    }

    @NonNull
    public Map<String, TOperationDefinition> getOperations() {
        if (this.operations == null) {
            this.operations = new LinkedHashMap<>();
        }

        return operations;
    }

    public void setOperations(Map<String, TOperationDefinition> operations) {
        this.operations = operations;
    }

    /*@NonNull
    public Map<String, TPropertyDefinition> getInputs() {
        if (this.inputs == null) {
            this.inputs = new LinkedHashMap<>();
        }

        return inputs;
    }

    public void setInputs(Map<String, TPropertyDefinition> inputs) {
        this.inputs = inputs;
    }*/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        public String description;
        private Map<String, TOperationDefinition> operations;
        private Map<String, TPropertyDefinition> inputs;

        public Builder(String name) {
            super(name);
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setOperations(Map<String, TOperationDefinition> operations) {
            this.operations = operations;
            return this;
        }

        public Builder setInputs(Map<String, TPropertyDefinition> inputs) {
            this.inputs = inputs;
            return this;
        }

        public Builder addOperations(Map<String, TOperationDefinition> operations) {
            if (operations == null || operations.isEmpty()) {
                return this;
            }

            if (this.operations == null) {
                this.operations = new LinkedHashMap<>(operations);
            } else {
                this.operations.putAll(operations);
            }

            return this;
        }

        public Builder addOperations(String name, TOperationDefinition operation) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addOperations(Collections.singletonMap(name, operation));
        }

        public Builder addInputs(Map<String, TPropertyDefinition> inputs) {
            if (inputs == null || inputs.isEmpty()) {
                return this;
            }

            if (this.inputs == null) {
                this.inputs = new LinkedHashMap<>(inputs);
            } else {
                this.inputs.putAll(inputs);
            }

            return this;
        }

        public Builder addInputs(String name, TPropertyDefinition input) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addInputs(Collections.singletonMap(name, input));
        }
        
        @Override
        public Builder self() {
            return this;
        }

        public TInterfaceType build() {
            return new TInterfaceType(this);
        }
    }
}
