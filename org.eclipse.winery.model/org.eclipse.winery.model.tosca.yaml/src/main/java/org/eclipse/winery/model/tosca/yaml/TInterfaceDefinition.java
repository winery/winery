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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tInterfaceDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "type",
    "inputs",
    "operations"
})
public class TInterfaceDefinition implements VisitorNode {

    private QName type;
    private Map<String, TParameterDefinition> inputs;
    private Map<String, TOperationDefinition> operations;

    public TInterfaceDefinition() {
    }

    public TInterfaceDefinition(Builder<?> builder) {
        this.setType(builder.type);
        this.setInputs(builder.inputs);
        this.setOperations(builder.operations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TInterfaceDefinition)) return false;
        TInterfaceDefinition that = (TInterfaceDefinition) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getInputs(), that.getInputs()) &&
            Objects.equals(getOperations(), that.getOperations());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getInputs(), getOperations());
    }

    @Nullable
    public QName getType() {
        return type;
    }

    public void setType(QName type) {
        this.type = type;
    }

    @NonNull
    public Map<String, TParameterDefinition> getInputs() {
        if (this.inputs == null) {
            this.inputs = new LinkedHashMap<>();
        }
        return inputs;
    }

    public void setInputs(Map<String, TParameterDefinition> inputs) {
        this.inputs = inputs;
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

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder<T extends Builder<T>> {

        private QName type;
        private Map<String, TParameterDefinition> inputs;
        private Map<String, TOperationDefinition> operations;

        @ADR(11)
        @SuppressWarnings("unchecked")
        public T self() {
            return (T) this;
        }

        public T setType(QName type) {
            this.type = type;
            return self();
        }

        public T setInputs(Map<String, TParameterDefinition> inputs) {
            this.inputs = inputs;
            return self();
        }

        public T setOperations(Map<String, TOperationDefinition> operations) {
            this.operations = operations;
            return self();
        }

        public T addInputs(Map<String, TParameterDefinition> inputs) {
            if (inputs == null || inputs.isEmpty()) {
                return self();
            }

            if (this.inputs == null) {
                this.inputs = new LinkedHashMap<>(inputs);
            } else {
                this.inputs.putAll(inputs);
            }

            return self();
        }

        public T addInput(String name, TParameterDefinition input) {
            if (name == null || name.isEmpty()) {
                return self();
            }

            return addInputs(Collections.singletonMap(name, input));
        }

        public T addOperations(Map<String, TOperationDefinition> operations) {
            if (operations == null || operations.isEmpty()) {
                return self();
            }

            if (this.operations == null) {
                this.operations = new LinkedHashMap<>(operations);
            } else {
                this.operations.putAll(operations);
            }

            return self();
        }

        public T addOperation(String name, TOperationDefinition operation) {
            if (name == null || name.isEmpty()) {
                return self();
            }

            return addOperations(Collections.singletonMap(name, operation));
        }

        public TInterfaceDefinition build() {
            return new TInterfaceDefinition(this);
        }
    }
}
