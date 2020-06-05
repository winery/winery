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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tAttributeDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "description",
    "type",
    "defaultValue",
    "status",
    "keySchema",
    "entrySchema"
})
public class TAttributeDefinition implements VisitorNode {

    private String description;
    @XmlAttribute(name = "type", required = true)
    @NonNull
    private QName type;
    @XmlElement(name = "default")
    private Object defaultValue;
    private TStatusValue status;
    @XmlAttribute(name = "key_schema")
    private TSchemaDefinition keySchema;
    @XmlAttribute(name = "entry_schema")
    private TSchemaDefinition entrySchema;

    public TAttributeDefinition() {
    }

    public TAttributeDefinition(Builder builder) {
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
        if (!(o instanceof TAttributeDefinition)) return false;
        TAttributeDefinition that = (TAttributeDefinition) o;
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
    public TStatusValue getStatus() {
        return status;
    }

    public void setStatus(TStatusValue status) {
        this.status = status;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Nullable
    public TSchemaDefinition getKeySchema() {
        return keySchema;
    }

    public void setKeySchema(TSchemaDefinition keySchema) {
        this.keySchema = keySchema;
    }

    @Nullable
    public TSchemaDefinition getEntrySchema() {
        return entrySchema;
    }

    public void setEntrySchema(TSchemaDefinition entrySchema) {
        this.entrySchema = entrySchema;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private String description;
        private Object defaultValue;
        private TStatusValue status;
        private TSchemaDefinition keySchema;
        private TSchemaDefinition entrySchema;

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

        public Builder setStatus(TStatusValue status) {
            this.status = status;
            return this;
        }

        public Builder setEntrySchema(TSchemaDefinition entrySchema) {
            this.entrySchema = entrySchema;
            return this;
        }

        public Builder setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder setKeySchema(TSchemaDefinition keySchema) {
            this.keySchema = keySchema;
            return this;
        }

        public TAttributeDefinition build() {
            return new TAttributeDefinition(this);
        }
    }
}

