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
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;

public class YTPropertyFilterDefinition implements VisitorNode {
    private List<YTConstraintClause> constraints;

    protected YTPropertyFilterDefinition(Builder builder) {
        this.setConstraints(builder.constraints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTPropertyFilterDefinition)) return false;
        YTPropertyFilterDefinition that = (YTPropertyFilterDefinition) o;
        return Objects.equals(getConstraints(), that.getConstraints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConstraints());
    }

    @Override
    public String toString() {
        return "TPropertyFilterDefinition{" +
            "constraints=" + getConstraints() +
            '}';
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
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private List<YTConstraintClause> constraints;

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

        public Builder addConstraints(YTConstraintClause constraint) {
            if (constraint == null) {
                return this;
            }

            return addConstraints(Collections.singletonList(constraint));
        }

        public YTPropertyFilterDefinition build() {
            return new YTPropertyFilterDefinition(this);
        }
    }
}
