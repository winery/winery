/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.Objects;

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
    public int hashCode() {
        return Objects.hash(getDescription(), getType(), getDefault(), getStatus(), getEntrySchema());
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
            Objects.equals(getEntrySchema(), that.getEntrySchema());
    }

    @Override
    public String toString() {
        return "TAttributeDefinition{" +
            "description='" + getDescription() + '\'' +
            ", type=" + getType() +
            ", defaultValue=" + getDefault() +
            ", status=" + getStatus() +
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

