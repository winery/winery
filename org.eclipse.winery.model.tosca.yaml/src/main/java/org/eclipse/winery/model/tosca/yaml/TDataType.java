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

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDataType", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "constraints"
})
public class TDataType extends TEntityType {
    private List<TConstraintClause> constraints;

    public TDataType() {
    }

    public TDataType(Builder builder) {
        super(builder);
        this.setConstraints(builder.constraints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TDataType)) return false;
        if (!super.equals(o)) return false;
        TDataType dataType = (TDataType) o;
        return Objects.equals(getConstraints(), dataType.getConstraints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getConstraints());
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
        R ir1 = super.accept(visitor, parameter);
        R ir2 = visitor.visit(this, parameter);
        if (ir1 == null) {
            return ir2;
        } else {
            return ir1.add(ir2);
        }
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        private List<TConstraintClause> constraints;

        public Builder() {

        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        @Override
        public Builder self() {
            return this;
        }

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

        public Builder addConstraints(TConstraintClause contraint) {
            if (contraint == null) {
                return this;
            }

            return addConstraints(Collections.singletonList(contraint));
        }

        public TDataType build() {
            return new TDataType(this);
        }
    }
}
