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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import org.eclipse.jdt.annotation.NonNull;

public class YTDataType extends YTEntityType {
    private List<YTConstraintClause> constraints;
    
    // metadata were added to all TEntityTypes, so no need to add these explicitly
    // FIXME add key_schema, entry_schema

    protected YTDataType(Builder builder) {
        super(builder);
        this.setConstraints(builder.constraints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTDataType)) return false;
        if (!super.equals(o)) return false;
        YTDataType dataType = (YTDataType) o;
        return Objects.equals(getConstraints(), dataType.getConstraints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getConstraints());
    }

    @Override
    public String toString() {
        return "TDataType{" +
            "constraints=" + getConstraints() +
            "} " + super.toString();
    }

    @NonNull
    public List<YTConstraintClause> getConstraints() {
        if (this.constraints == null) {
            this.constraints = new ArrayList<>();
        }

        return constraints;
    }

    public void setConstraints(List<YTConstraintClause> constraints) {
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

    public static class Builder extends YTEntityType.Builder<Builder> {
        private List<YTConstraintClause> constraints;

        public Builder() {

        }

        public Builder(YTEntityType entityType) {
            super(entityType);
        }

        @Override
        public Builder self() {
            return this;
        }

        public Builder setConstraints(List<YTConstraintClause> constraints) {
            this.constraints = constraints;
            return this;
        }

        public Builder addConstraints(List<YTConstraintClause> constraints) {
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

        public Builder addConstraints(YTConstraintClause contraint) {
            if (contraint == null) {
                return this;
            }

            return addConstraints(Collections.singletonList(contraint));
        }

        public YTDataType build() {
            return new YTDataType(this);
        }
    }
}
