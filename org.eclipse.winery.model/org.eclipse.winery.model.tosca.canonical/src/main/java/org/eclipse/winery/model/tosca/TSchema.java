/*******************************************************************************
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
//import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.extensions.kvproperties.ConstraintClauseKV;

import org.eclipse.jdt.annotation.NonNull;

@XmlRootElement
public class TSchema {

    @NonNull
    private QName type;
    private String description;
    private List<ConstraintClauseKV> constraints;
    private TSchema keySchema;
    private TSchema entrySchema;

    @Deprecated // used for XML deserialization of API request content
    public TSchema() { }

    public TSchema(Builder builder) {
        this.type = builder.type;
        this.description = builder.description;
        this.constraints = builder.constraints;
        this.keySchema = builder.keySchema;
        this.entrySchema = builder.entrySchema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TSchema tSchema = (TSchema) o;
        return type.equals(tSchema.type) &&
            Objects.equals(description, tSchema.description) &&
            Objects.equals(constraints, tSchema.constraints) &&
            Objects.equals(keySchema, tSchema.keySchema) &&
            Objects.equals(entrySchema, tSchema.entrySchema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, description, constraints, keySchema, entrySchema);
    }

    @Override
    public String toString() {
        return "TSchema{" +
            "type=" + type +
            ", description='" + description + '\'' +
            ", constraints=" + constraints +
            ", keySchema=" + keySchema +
            ", entrySchema=" + entrySchema +
            '}';
    }

    public QName getType() {
        return type;
    }

    public void setType(QName type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ConstraintClauseKV> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ConstraintClauseKV> constraints) {
        this.constraints = constraints;
    }

    public TSchema getKeySchema() {
        return keySchema;
    }

    public void setKeySchema(TSchema keySchema) {
        this.keySchema = keySchema;
    }

    public TSchema getEntrySchema() {
        return entrySchema;
    }

    public void setEntrySchema(TSchema entrySchema) {
        this.entrySchema = entrySchema;
    }

    public static class Builder {

        @NonNull
        private final QName type;
        private String description;
        private List<ConstraintClauseKV> constraints;
        private TSchema keySchema;
        private TSchema entrySchema;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder setDescription(String description) {
            this.description = description;
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

        public Builder addConstraints(ConstraintClauseKV contraint) {
            if (contraint == null) {
                return this;
            }

            return addConstraints(Collections.singletonList(contraint));
        }

        public Builder setKeySchema(TSchema keySchema) {
            this.keySchema = keySchema;
            return this;
        }

        public Builder setEntrySchema(TSchema entrySchema) {
            this.entrySchema = entrySchema;
            return this;
        }

        public TSchema build() {
            return new TSchema(this);
        }
    }
}
