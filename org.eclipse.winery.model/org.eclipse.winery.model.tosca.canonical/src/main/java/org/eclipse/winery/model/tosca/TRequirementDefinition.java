/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class TRequirementDefinition extends TExtensibleElements {
    protected TRequirementDefinition.Constraints constraints;
    protected String name;
    protected QName requirementType;
    protected Integer lowerBound;
    protected String upperBound;
    // the following attributes are introduced to support the YAML specs
    private QName capability;
    private QName node;
    private QName relationship;

    public TRequirementDefinition() {

    }

    public TRequirementDefinition(Builder builder) {
        super(builder);
        this.constraints = builder.constraints;
        this.name = builder.name;
        this.requirementType = builder.requirementType;
        this.lowerBound = builder.lowerBound;
        this.upperBound = builder.upperBound;
        this.capability = builder.capability;
        this.node = builder.node;
        this.relationship = builder.relationship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRequirementDefinition)) return false;
        if (!super.equals(o)) return false;
        TRequirementDefinition that = (TRequirementDefinition) o;
        return Objects.equals(constraints, that.constraints) &&
            Objects.equals(name, that.name) &&
            Objects.equals(requirementType, that.requirementType) &&
            Objects.equals(lowerBound, that.lowerBound) &&
            Objects.equals(upperBound, that.upperBound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), constraints, name, requirementType, lowerBound, upperBound);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public TRequirementDefinition.@Nullable Constraints getConstraints() {
        return constraints;
    }

    public void setConstraints(TRequirementDefinition.@Nullable Constraints value) {
        this.constraints = value;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String value) {
        Objects.requireNonNull(value);
        this.name = value;
    }

    // removed the @NonNull annotation since this field is not present in YAML mode
    public QName getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(@NonNull QName value) {
        Objects.requireNonNull(value);
        this.requirementType = value;
    }

    @NonNull
    public int getLowerBound() {
        if (lowerBound == null) {
            return 1;
        } else {
            return lowerBound;
        }
    }

    public void setLowerBound(@Nullable Integer value) {
        this.lowerBound = value;
    }

    @NonNull
    public String getUpperBound() {
        if (upperBound == null) {
            return "1";
        } else {
            return upperBound;
        }
    }

    public void setUpperBound(@Nullable String value) {
        this.upperBound = value;
    }

    public QName getCapability() {
        return capability;
    }

    public void setCapability(QName capability) {
        this.capability = capability;
    }

    public QName getNode() {
        return node;
    }

    public void setNode(QName node) {
        this.node = node;
    }

    public QName getRelationship() {
        return relationship;
    }

    public void setRelationship(QName relationship) {
        this.relationship = relationship;
    }

    public static class Constraints implements Serializable {

        protected List<TConstraint> constraint;

        @NonNull
        public List<TConstraint> getConstraint() {
            if (constraint == null) {
                constraint = new ArrayList<TConstraint>();
            }
            return this.constraint;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Constraints that = (Constraints) o;
            return Objects.equals(constraint, that.constraint);
        }

        @Override
        public int hashCode() {
            return Objects.hash(constraint);
        }
    }

    public static class Builder extends TExtensibleElements.Builder<Builder> {
        private final String name;
        private final QName requirementType;
        private Constraints constraints;
        private Integer lowerBound;
        private String upperBound;
        private QName capability;
        private QName node;
        private QName relationship;

        /**
         * This constructor is used when in YAML mode.
         */
        public Builder(String name) {
            this(name, null);
        }

        public Builder(String name, QName requirementType) {
            this.name = name;
            this.requirementType = requirementType;
        }

        public Builder setConstraints(TRequirementDefinition.Constraints constraints) {
            this.constraints = constraints;
            return this;
        }

        public Builder setLowerBound(Integer lowerBound) {
            this.lowerBound = lowerBound;
            return this;
        }

        public Builder setUpperBound(String upperBound) {
            this.upperBound = upperBound;
            return this;
        }

        public Builder setCapability(QName capability) {
            this.capability = capability;
            return self();
        }

        public Builder setNode(QName node) {
            this.node = node;
            return self();
        }

        public Builder setRelationship(QName relationship) {
            this.relationship = relationship;
            return self();
        }

        public Builder addConstraints(TRequirementDefinition.Constraints constraints) {
            if (constraints == null || constraints.getConstraint().isEmpty()) {
                return this;
            }

            if (this.constraints == null) {
                this.constraints = constraints;
            } else {
                this.constraints.getConstraint().addAll(constraints.getConstraint());
            }
            return this;
        }

        public Builder addConstraints(List<TConstraint> constraints) {
            if (constraints == null) {
                return this;
            }

            TRequirementDefinition.Constraints tmp = new TRequirementDefinition.Constraints();
            tmp.getConstraint().addAll(constraints);
            return addConstraints(tmp);
        }

        public Builder addConstraints(TConstraint constraints) {
            if (constraints == null) {
                return this;
            }

            TRequirementDefinition.Constraints tmp = new TRequirementDefinition.Constraints();
            tmp.getConstraint().add(constraints);
            return addConstraints(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public TRequirementDefinition build() {
            return new TRequirementDefinition(this);
        }
    }
}
