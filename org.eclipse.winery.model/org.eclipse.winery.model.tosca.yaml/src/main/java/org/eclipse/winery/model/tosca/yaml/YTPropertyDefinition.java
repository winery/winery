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

import org.eclipse.winery.model.tosca.yaml.support.Annotations;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTPropertyDefinition extends YTPropertyAssignmentOrDefinition {

    // TODO while this is required, we can not guarantee it's set while working with the model
    @Nullable
    private QName type;
    private String description;
    private Boolean required;
    @Annotations.FieldName("default")
    private Object defaultValue;
    private YTStatusValue status;
    private List<YTConstraintClause> constraints;
    private YTSchemaDefinition entrySchema;
    private YTSchemaDefinition keySchema;

    protected YTPropertyDefinition(Builder builder) {
        this.type = builder.type;
        this.description = builder.description;
        this.required = builder.required;
        this.defaultValue = builder.defaultValue;
        this.status = builder.status;
        this.constraints = builder.constraints;
        this.keySchema = builder.keySchema;
        this.entrySchema = builder.entrySchema;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDescription(), getRequired(), getDefault(),
            getStatus(), getConstraints(), getKeySchema(), getEntrySchema());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTPropertyDefinition)) return false;
        YTPropertyDefinition that = (YTPropertyDefinition) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getRequired(), that.getRequired()) &&
            Objects.equals(getDefault(), that.getDefault()) &&
            getStatus() == that.getStatus() &&
            Objects.equals(getConstraints(), that.getConstraints()) &&
            Objects.equals(getKeySchema(), that.getKeySchema()) &&
            Objects.equals(getEntrySchema(), that.getEntrySchema());
    }

    @Override
    public String toString() {
        return "TPropertyDefinition{" +
            "type=" + getType() +
            ", description='" + getDescription() + '\'' +
            ", required=" + getRequired() +
            ", defaultValue=" + getDefault() +
            ", status=" + getStatus() +
            ", constraints=" + getConstraints() +
            ", entrySchema=" + getEntrySchema() +
            ", keySchema=" + getKeySchema() +
            "}";
    }

    @Nullable
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
    public Boolean getRequired() {
        if (required == null) {
            required = true;
        }
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Nullable
    public Object getDefault() {
        return defaultValue;
    }

    public void setDefault(String defaultValue) {
        setDefault((Object) defaultValue);
    }

    public void setDefault(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @NonNull
    public YTStatusValue getStatus() {
        if (status == null) {
            status = YTStatusValue.supported;
        }
        return status;
    }

    public void setStatus(YTStatusValue status) {
        this.status = status;
    }

    @NonNull
    public List<YTConstraintClause> getConstraints() {
        if (constraints == null) {
            constraints = new ArrayList<>();
        }
        return constraints;
    }

    public void setConstraints(List<YTConstraintClause> constraints) {
        this.constraints = constraints;
    }

    @Nullable
    public YTSchemaDefinition getEntrySchema() {
        return entrySchema;
    }

    public void setEntrySchema(@Nullable YTSchemaDefinition entrySchema) {
        this.entrySchema = entrySchema;
    }

    @Nullable
    public YTSchemaDefinition getKeySchema() {
        return keySchema;
    }

    public void setKeySchema(@Nullable YTSchemaDefinition keySchema) {
        this.keySchema = keySchema;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private String description;
        private Boolean required;
        private Object defaultValue;
        private YTStatusValue status;
        private List<YTConstraintClause> constraints;
        private YTSchemaDefinition entrySchema;
        private YTSchemaDefinition keySchema;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder(YTPropertyDefinition propertyDefinition) {
            this.type = propertyDefinition.getType();
            this.description = propertyDefinition.getDescription();
            this.required = propertyDefinition.getRequired();
            this.defaultValue = propertyDefinition.getDefault();
            this.status = propertyDefinition.getStatus();
            this.constraints = propertyDefinition.getConstraints();
            this.entrySchema = propertyDefinition.getEntrySchema();
            this.keySchema = propertyDefinition.getKeySchema();
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setRequired(Boolean required) {
            this.required = required;
            return this;
        }

        public Builder setDefault(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder setStatus(YTStatusValue status) {
            this.status = status;
            return this;
        }

        public Builder setStatus(String status) {
            return setStatus(YTStatusValue.getStatus(status));
        }

        public Builder setEntrySchema(YTSchemaDefinition entrySchema) {
            this.entrySchema = entrySchema;
            return this;
        }

        public Builder setConstraints(List<YTConstraintClause> constraints) {
            if (constraints == null || constraints.isEmpty()) {
                return this;
            }
            this.constraints = constraints;
            return this;
        }

        public Builder addConstraints(List<YTConstraintClause> constraints) {
            if (constraints == null || constraints.isEmpty()) {
                return this;
            }

            if (this.constraints == null) {
                this.constraints = new ArrayList<>();
            } 
            this.constraints.addAll(constraints);
            return this;
        }

        public Builder addConstraints(YTConstraintClause constraint) {
            if (constraint == null) {
                return this;
            }

            return addConstraints(Collections.singletonList(constraint));
        }

        public Builder setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder setKeySchema(YTSchemaDefinition keySchema) {
            this.keySchema = keySchema;
            return this;
        }

        public YTPropertyDefinition build() {
            return new YTPropertyDefinition(this);
        }
    }
}
