/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
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
@XmlType(name = "tAttributeDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "description",
    "type",
    "defaultValue",
    "status",
    "entrySchema"
})
public class TAttributeDefinition implements VisitorNode {
    private String description;
    @XmlAttribute(name = "type", required = true)
    private QName type;
    @XmlElement(name = "default")
    private Object defaultValue;
    private TStatusValue status;
    @XmlAttribute(name = "entry_schema")
    private TEntrySchema entrySchema;

    public TAttributeDefinition() {
    }

    public TAttributeDefinition(Builder builder) {
        this.setType(builder.type);
        this.setDescription(builder.description);
        this.setDefault(builder.defaultValue);
        this.setStatus(builder.status);
        this.setEntrySchema(builder.entrySchema);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TAttributeDefinition)) return false;
        TAttributeDefinition that = (TAttributeDefinition) o;
        return Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getType(), that.getType()) &&
            Objects.equals(defaultValue, that.defaultValue) &&
            getStatus() == that.getStatus() &&
            Objects.equals(getEntrySchema(), that.getEntrySchema());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getType(), defaultValue, getStatus(), getEntrySchema());
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

    @Nullable
    public TEntrySchema getEntrySchema() {
        return entrySchema;
    }

    public void setEntrySchema(TEntrySchema entrySchema) {
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
        private TEntrySchema entrySchema;

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

        public Builder setEntrySchema(TEntrySchema entrySchema) {
            this.entrySchema = entrySchema;
            return this;
        }

        public TAttributeDefinition build() {
            return new TAttributeDefinition(this);
        }
    }
}

