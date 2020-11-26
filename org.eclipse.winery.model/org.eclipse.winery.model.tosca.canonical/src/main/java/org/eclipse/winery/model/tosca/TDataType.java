/********************************************************************************
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.extensions.kvproperties.ConstraintClauseKV;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDataType", namespace = "http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "constraints"
})
public class TDataType extends TEntityType {
    private List<ConstraintClauseKV> constraints;
    
    // metadata were added to all TEntityTypes, so no need to add these explicitly
    // FIXME add key_schema, entry_schema

    @Deprecated // used for XML deserialization of API request content
    public TDataType() { }

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

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "TDataType{" +
            "constraints=" + getConstraints() +
            "} " + super.toString();
    }

    @NonNull
    public List<ConstraintClauseKV> getConstraints() {
        if (this.constraints == null) {
            this.constraints = new ArrayList<>();
        }
        return constraints;
    }

    public void setConstraints(List<ConstraintClauseKV> constraints) {
        this.constraints = constraints;
    }
    
    public static class Builder extends TEntityType.Builder<Builder> {
        private List<ConstraintClauseKV> constraints;

        public Builder(String name) {
            super(name);
        }
        
        public Builder(TEntityType entityType) {
            super(entityType);
        }

        @Override
        public Builder self() {
            return this;
        }

        public Builder setConstraints(List<ConstraintClauseKV> constraints) {
            this.constraints = constraints;
            return this;
        }

        public Builder addConstraints(List<ConstraintClauseKV> constraints) {
            if (constraints == null || constraints.isEmpty()) {
                return this;
            }

            if (this.constraints == null) {
                this.constraints = new ArrayList<>();
            } 
            this.constraints.addAll(constraints);

            return this;
        }

        public Builder addConstraints(ConstraintClauseKV constraint) {
            if (constraint == null) {
                return this;
            }
            if (this.constraints == null) {
                this.constraints = new ArrayList<>();
            }
            this.constraints.add(constraint);
            return this;
        }

        public TDataType build() {
            return new TDataType(this);
        }
    }
}
