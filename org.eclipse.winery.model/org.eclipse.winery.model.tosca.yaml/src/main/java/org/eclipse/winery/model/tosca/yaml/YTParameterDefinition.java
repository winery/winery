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
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.Nullable;

public class YTParameterDefinition implements VisitorNode {
    private QName type;
    private String description;
    private Boolean required;
    @Annotations.FieldName("default")
    private Object defaultValue;
    private YTStatusValue status;
    private List<YTConstraintClause> constraints;
    private YTSchemaDefinition keySchema;
    private YTSchemaDefinition entrySchema;
    private Object value;

    protected YTParameterDefinition(Builder builder) {
        this.type = builder.type;
        this.description = builder.description;
        this.required = builder.required;
        this.defaultValue = builder.defaultValue;
        this.status = builder.status;
        this.constraints = builder.constraints;
        this.keySchema = builder.keySchema;
        this.entrySchema = builder.entrySchema;
        this.value = builder.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDescription(), getRequired(), getDefault(),
            getStatus(), getConstraints(), getKeySchema(), getEntrySchema(), getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTParameterDefinition)) return false;
        YTParameterDefinition that = (YTParameterDefinition) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getRequired(), that.getRequired()) &&
            Objects.equals(getDefault(), that.getDefault()) &&
            getStatus() == that.getStatus() &&
            Objects.equals(getConstraints(), that.getConstraints()) &&
            Objects.equals(getKeySchema(), that.getKeySchema()) &&
            Objects.equals(getEntrySchema(), that.getEntrySchema()) &&
            Objects.equals(getValue(), that.getValue());
    }

    @Override
    public String toString() {
        return "TParameterDefinition{" +
            "type=" + getType() +
            ", description='" + getDescription() + '\'' +
            ", required=" + getRequired() +
            ", defaultValue=" + getDefault() +
            ", status=" + getStatus() +
            ", constraints=" + getConstraints() +
            ", keySchema=" + getKeySchema() +
            ", entrySchema=" + getEntrySchema() +
            ", value=" + getValue() +
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

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Object getDefault() {
        return defaultValue;
    }

    public void setDefault(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public YTStatusValue getStatus() {
        return status;
    }

    public void setStatus(YTStatusValue status) {
        this.status = status;
    }

    public List<YTConstraintClause> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<YTConstraintClause> constraints) {
        this.constraints = constraints;
    }

    public YTSchemaDefinition getEntrySchema() {
        return entrySchema;
    }

    public void setEntrySchema(YTSchemaDefinition entrySchema) {
        this.entrySchema = entrySchema;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Nullable
    public YTSchemaDefinition getKeySchema() {
        return keySchema;
    }

    public void setKeySchema(YTSchemaDefinition keySchema) {
        this.keySchema = keySchema;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {

        private QName type;
        private String description;
        private Boolean required;
        private Object defaultValue;
        private YTStatusValue status;
        private List<YTConstraintClause> constraints;
        private YTSchemaDefinition keySchema;
        private YTSchemaDefinition entrySchema;
        private Object value;

        public Builder setType(QName type) {
            this.type = type;
            return this;
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

        public YTStatusValue getStatus() {
            if (Objects.isNull(status)) {
                status = YTStatusValue.supported;
            }
            return status;
        }

        public Builder setStatus(YTStatusValue status) {
            this.status = status;
            return this;
        }

        public Builder setStatus(String status) {
            return setStatus(YTStatusValue.getStatus(status));
        }

        public Builder setConstraints(List<YTConstraintClause> constraints) {
            if (Objects.isNull(constraints)) {
                constraints = new ArrayList<>();
            }
            this.constraints = constraints;
            return this;
        }

        public Builder setEntrySchema(YTSchemaDefinition entrySchema) {
            this.entrySchema = entrySchema;
            return this;
        }

        public Object getValue() {
            return value;
        }

        public Builder setValue(Object value) {
            this.value = value;
            return this;
        }

        public Builder addConstraints(List<YTConstraintClause> constraints) {
            if (Objects.isNull(constraints) || constraints.isEmpty()) {
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
            if (Objects.isNull(constraint)) {
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

        public YTParameterDefinition build() {
            return new YTParameterDefinition(this);
        }
    }
}
