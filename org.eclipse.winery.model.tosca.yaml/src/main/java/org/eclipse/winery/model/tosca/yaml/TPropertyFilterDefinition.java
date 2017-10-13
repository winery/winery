/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPropertyFilterDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "constraints"
})
public class TPropertyFilterDefinition implements VisitorNode {
    private List<TConstraintClause> constraints;

    public TPropertyFilterDefinition() {
    }

    public TPropertyFilterDefinition(Builder builder) {
        this.setConstraints(builder.constraints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPropertyFilterDefinition)) return false;
        TPropertyFilterDefinition that = (TPropertyFilterDefinition) o;
        return Objects.equals(getConstraints(), that.getConstraints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConstraints());
    }

    @NonNull
    public List<TConstraintClause> getConstraints() {
        if (this.constraints == null) {
            this.constraints = new ArrayList<>();
        }

        return constraints;
    }

    public void setConstraints(List<TConstraintClause> constraints) {
        this.constraints = constraints;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private List<TConstraintClause> constraints;

        public Builder setConstraints(List<TConstraintClause> constraints) {
            this.constraints = constraints;
            return this;
        }

        public Builder addConstraints(List<TConstraintClause> constraints) {
            if (constraints == null || constraints.isEmpty()) {
                return this;
            }

            if (this.constraints == null) {
                this.constraints = new ArrayList<>(constraints);
            } else {
                this.constraints.addAll(constraints);
            }

            return this;
        }

        public Builder addConstraints(TConstraintClause constraint) {
            if (constraint == null) {
                return this;
            }

            return addConstraints(Collections.singletonList(constraint));
        }

        public TPropertyFilterDefinition build() {
            return new TPropertyFilterDefinition(this);
        }
    }
}
