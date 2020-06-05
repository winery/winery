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
@XmlType(name = "tSchemaDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "type",
    "description",
    "constraints",
    "keySchema",
    "entrySchema"
})
public class TSchemaDefinition implements VisitorNode {
    @NonNull
    private QName type;
    private String description;
    private List<TConstraintClause> constraints;

    // type could be map
    private TSchemaDefinition keySchema;
    // type could be list or map
    private TSchemaDefinition entrySchema;

    public TSchemaDefinition() {
    }

    public TSchemaDefinition(Builder builder) {
        this.type = builder.type;
        this.description = builder.description;
        this.constraints = builder.constraints;
        this.keySchema = builder.keySchema;
        this.entrySchema = builder.entrySchema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TSchemaDefinition)) return false;
        TSchemaDefinition that = (TSchemaDefinition) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getConstraints(), that.getConstraints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDescription(), getConstraints());
    }

    @Override
    public String toString() {
        return "TEntrySchema{" +
            "type=" + getType() +
            ", description='" + getDescription() + '\'' +
            ", constraints=" + getConstraints() +
            '}';
    }

    @NonNull
    public QName getType() {
        return type;
    }

    public void setType(@NonNull QName type) {
        this.type = type;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public List<TConstraintClause> getConstraints() {
        if (this.constraints == null) {
            this.constraints = new ArrayList<>();
        }

        return constraints;
    }

    public void setConstraints(@Nullable List<TConstraintClause> constraints) {
        this.constraints = constraints;
    }

    @Nullable
    public TSchemaDefinition getKeySchema() {
        return keySchema;
    }

    public void setKeySchema(@Nullable TSchemaDefinition keySchema) {
        this.keySchema = keySchema;
    }

    @Nullable
    public TSchemaDefinition getEntrySchema() {
        return entrySchema;
    }

    public void setEntrySchema(@Nullable TSchemaDefinition entrySchema) {
        this.entrySchema = entrySchema;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        @NonNull
        private final QName type;
        private String description;
        private List<TConstraintClause> constraints;

        private TSchemaDefinition keySchema;
        private TSchemaDefinition entrySchema;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder setDescription(String description) {
            this.description = description;
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

        public Builder setKeySchema(TSchemaDefinition keySchema) {
            this.keySchema = keySchema;
            return this;
        }

        public Builder setEntrySchema(TSchemaDefinition entrySchema) {
            this.entrySchema = entrySchema;
            return this;
        }

        public TSchemaDefinition build() {
            return new TSchemaDefinition(this);
        }
    }
}
