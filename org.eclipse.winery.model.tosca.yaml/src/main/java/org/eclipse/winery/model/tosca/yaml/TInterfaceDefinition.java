/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tInterfaceDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "type",
    "inputs",
    "operations"
})
public class TInterfaceDefinition implements VisitorNode {
    private QName type;
    private Map<String, TPropertyAssignmentOrDefinition> inputs;
    private Map<String, TOperationDefinition> operations;

    public TInterfaceDefinition() {
    }

    public TInterfaceDefinition(Builder builder) {
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
    public Map<String, TPropertyAssignmentOrDefinition> getInputs() {
        if (this.inputs == null) {
            this.inputs = new LinkedHashMap<>();
        }

        return inputs;
    }

    public void setInputs(Map<String, TPropertyAssignmentOrDefinition> inputs) {
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
        private Map<String, TPropertyAssignmentOrDefinition> inputs;
        private Map<String, TOperationDefinition> operations;

        public T self() {
            return (T) this;
        }

        public T setType(QName type) {
            this.type = type;
            return self();
        }

        public T setInputs(Map<String, TPropertyAssignmentOrDefinition> inputs) {
            this.inputs = inputs;
            return self();
        }

        public T setOperations(Map<String, TOperationDefinition> operations) {
            this.operations = operations;
            return self();
        }

        public T addInputs(Map<String, TPropertyAssignmentOrDefinition> inputs) {
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

        public T addInputs(String name, TPropertyAssignmentOrDefinition input) {
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

        public T addOperations(String name, TOperationDefinition operation) {
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
