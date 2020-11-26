/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.yaml;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import org.eclipse.jdt.annotation.NonNull;

public class YTInterfaceType extends YTEntityType {
    private Map<String, YTOperationDefinition> operations;
    private Map<String, YTPropertyDefinition> inputs;

    protected YTInterfaceType(Builder builder) {
        super(builder);
        this.setOperations(builder.operations);
        this.setInputs(builder.inputs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTInterfaceType)) return false;
        if (!super.equals(o)) return false;
        YTInterfaceType that = (YTInterfaceType) o;
        return Objects.equals(getOperations(), that.getOperations()) &&
            Objects.equals(getInputs(), that.getInputs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getOperations(), getInputs());
    }

    @Override
    public String toString() {
        return "TInterfaceType{" +
            "operations=" + getOperations() +
            ", inputs=" + getInputs() +
            "} " + super.toString();
    }

    @NonNull
    public Map<String, YTOperationDefinition> getOperations() {
        if (this.operations == null) {
            this.operations = new LinkedHashMap<>();
        }

        return operations;
    }

    public void setOperations(Map<String, YTOperationDefinition> operations) {
        this.operations = operations;
    }

    @NonNull
    public Map<String, YTPropertyDefinition> getInputs() {
        if (this.inputs == null) {
            this.inputs = new LinkedHashMap<>();
        }

        return inputs;
    }

    public void setInputs(Map<String, YTPropertyDefinition> inputs) {
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

    public static class Builder extends YTEntityType.Builder<Builder> {
        private Map<String, YTOperationDefinition> operations;
        private Map<String, YTPropertyDefinition> inputs;

        public Builder() {

        }

        public Builder(YTEntityType entityType) {
            super(entityType);
        }

        @Override
        public Builder self() {
            return this;
        }

        public Builder setOperations(Map<String, YTOperationDefinition> operations) {
            this.operations = operations;
            return this;
        }

        public Builder setInputs(Map<String, YTPropertyDefinition> inputs) {
            this.inputs = inputs;
            return this;
        }

        public Builder addOperations(Map<String, YTOperationDefinition> operations) {
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

        public Builder addOperations(String name, YTOperationDefinition operation) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addOperations(Collections.singletonMap(name, operation));
        }

        public Builder addInputs(Map<String, YTPropertyDefinition> inputs) {
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

        public Builder addInputs(String name, YTPropertyDefinition input) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addInputs(Collections.singletonMap(name, input));
        }

        public YTInterfaceType build() {
            return new YTInterfaceType(this);
        }
    }
}
