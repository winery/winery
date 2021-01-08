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

import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.support.Annotations;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTAttributeDefinition implements VisitorNode {

    private String description;
    @NonNull
    private QName type;
    @Annotations.FieldName("default")
    private Object defaultValue;
    private YTStatusValue status;
    private YTSchemaDefinition keySchema;
    private YTSchemaDefinition entrySchema;

    protected YTAttributeDefinition(Builder builder) {
        this.type = builder.type;
        this.description = builder.description;
        this.defaultValue = builder.defaultValue;
        this.status = builder.status;
        this.entrySchema = builder.entrySchema;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getType(), getDefault(), getStatus(), getKeySchema(), getEntrySchema());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTAttributeDefinition)) return false;
        YTAttributeDefinition that = (YTAttributeDefinition) o;
        return Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDefault(), that.getDefault()) &&
            getStatus() == that.getStatus() &&
            Objects.equals(getKeySchema(), that.getKeySchema()) &&
            Objects.equals(getEntrySchema(), that.getEntrySchema());
    }

    @Override
    public String toString() {
        return "TAttributeDefinition{" +
            "description='" + getDescription() + '\'' +
            ", type=" + getType() +
            ", defaultValue=" + getDefault() +
            ", status=" + getStatus() +
            ", keySchema=" + getKeySchema() +
            ", entrySchema=" + getEntrySchema() +
            '}';
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public QName getType() {
        return type;
    }

    public void setType(QName type) {
        this.type = type;
    }

    @Nullable
    public Object getDefault() {
        return defaultValue;
    }

    public void setDefault(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Nullable
    public YTStatusValue getStatus() {
        return status;
    }

    public void setStatus(YTStatusValue status) {
        this.status = status;
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
    public YTSchemaDefinition getEntrySchema() {
        return entrySchema;
    }

    public void setEntrySchema(YTSchemaDefinition entrySchema) {
        this.entrySchema = entrySchema;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private String description;
        private Object defaultValue;
        private YTStatusValue status;
        private YTSchemaDefinition keySchema;
        private YTSchemaDefinition entrySchema;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder setDescription(String description) {
            this.description = description;
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

        public Builder setEntrySchema(YTSchemaDefinition entrySchema) {
            this.entrySchema = entrySchema;
            return this;
        }

        public Builder setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder setKeySchema(YTSchemaDefinition keySchema) {
            this.keySchema = keySchema;
            return this;
        }

        public YTAttributeDefinition build() {
            return new YTAttributeDefinition(this);
        }
    }
}

