/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tInterfaceType", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "operations",
    "inputs"
})
public class TInterfaceType extends TEntityType {
    private Map<String, TOperationDefinition> operations;
    private Map<String, TPropertyDefinition> inputs;

    public TInterfaceType() {
    }

    public TInterfaceType(Builder builder) {
        super(builder);
        this.setOperations(builder.operations);
        this.setInputs(builder.inputs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TInterfaceType)) return false;
        if (!super.equals(o)) return false;
        TInterfaceType that = (TInterfaceType) o;
        return Objects.equals(getOperations(), that.getOperations()) &&
            Objects.equals(getInputs(), that.getInputs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getOperations(), getInputs());
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

    @NonNull
    public Map<String, TPropertyDefinition> getInputs() {
        if (this.inputs == null) {
            this.inputs = new LinkedHashMap<>();
        }

        return inputs;
    }

    public void setInputs(Map<String, TPropertyDefinition> inputs) {
        this.inputs = inputs;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        R ir1 = super.accept(visitor, parameter);
        R ir2 = visitor.visit(this, parameter);
        if (ir1 == null) {
            return ir2;
        } else {
            return ir1.add(ir2);
        }
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        private Map<String, TOperationDefinition> operations;
        private Map<String, TPropertyDefinition> inputs;

        public Builder() {

        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        @Override
        public Builder self() {
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

        public TInterfaceType build() {
            return new TInterfaceType(this);
        }
    }
}
