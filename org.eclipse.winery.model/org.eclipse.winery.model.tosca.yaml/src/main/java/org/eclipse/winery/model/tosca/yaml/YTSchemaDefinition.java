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

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTSchemaDefinition implements VisitorNode {
    @NonNull
    private QName type;
    private String description;
    private List<YTConstraintClause> constraints;

    // type could be map
    private YTSchemaDefinition keySchema;
    // type could be list or map
    private YTSchemaDefinition entrySchema;

    protected YTSchemaDefinition(Builder builder) {
        this.type = builder.type;
        this.description = builder.description;
        this.constraints = builder.constraints;
        this.keySchema = builder.keySchema;
        this.entrySchema = builder.entrySchema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTSchemaDefinition)) return false;
        YTSchemaDefinition that = (YTSchemaDefinition) o;
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
    public List<YTConstraintClause> getConstraints() {
        if (this.constraints == null) {
            this.constraints = new ArrayList<>();
        }

        return constraints;
    }

    public void setConstraints(@Nullable List<YTConstraintClause> constraints) {
        this.constraints = constraints;
    }

    @Nullable
    public YTSchemaDefinition getKeySchema() {
        return keySchema;
    }

    public void setKeySchema(@Nullable YTSchemaDefinition keySchema) {
        this.keySchema = keySchema;
    }

    @Nullable
    public YTSchemaDefinition getEntrySchema() {
        return entrySchema;
    }

    public void setEntrySchema(@Nullable YTSchemaDefinition entrySchema) {
        this.entrySchema = entrySchema;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        @NonNull
        private final QName type;
        private String description;
        private List<YTConstraintClause> constraints;

        private YTSchemaDefinition keySchema;
        private YTSchemaDefinition entrySchema;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder setDescription(String description) {
            this.description = description;
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

        public Builder setKeySchema(YTSchemaDefinition keySchema) {
            this.keySchema = keySchema;
            return this;
        }

        public Builder setEntrySchema(YTSchemaDefinition entrySchema) {
            this.entrySchema = entrySchema;
            return this;
        }

        public YTSchemaDefinition build() {
            return new YTSchemaDefinition(this);
        }
    }
}
