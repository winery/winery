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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tParameterDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "type",
    "description",
    "required",
    "defaultValue",
    "status",
    "constraints",
    "keySchema",
    "entrySchema",
    "value"
})
public class TParameterDefinition implements VisitorNode {
    @XmlAttribute(name = "type")
    private QName type;
    private String description;
    private Boolean required;
    @XmlElement(name = "default")
    private Object defaultValue;
    private TStatusValue status;
    @XmlElement
    private List<TConstraintClause> constraints;
    @XmlAttribute(name = "key_schema")
    private TSchemaDefinition keySchema;
    @XmlAttribute(name = "entry_schema")
    private TSchemaDefinition entrySchema;
    private Object value;

    public TParameterDefinition() {
    }

    public TParameterDefinition(Builder builder) {
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
        if (!(o instanceof TParameterDefinition)) return false;
        TParameterDefinition that = (TParameterDefinition) o;
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

    public TStatusValue getStatus() {
        return status;
    }

    public void setStatus(TStatusValue status) {
        this.status = status;
    }

    public List<TConstraintClause> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<TConstraintClause> constraints) {
        this.constraints = constraints;
    }

    public TSchemaDefinition getEntrySchema() {
        return entrySchema;
    }

    public void setEntrySchema(TSchemaDefinition entrySchema) {
        this.entrySchema = entrySchema;
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
        private TStatusValue status;
        private List<TConstraintClause> constraints;
        private TSchemaDefinition keySchema;
        private TSchemaDefinition entrySchema;
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

        public TStatusValue getStatus() {
            if (Objects.isNull(status)) {
                status = TStatusValue.supported;
            }
            return status;
        }

        public Builder setStatus(TStatusValue status) {
            this.status = status;
            return this;
        }

        public Builder setStatus(String status) {
            return setStatus(TStatusValue.getStatus(status));
        }

        public Builder setConstraints(List<TConstraintClause> constraints) {
            if (Objects.isNull(constraints)) {
                constraints = new ArrayList<>();
            }
            this.constraints = constraints;
            return this;
        }

        public Builder setEntrySchema(TSchemaDefinition entrySchema) {
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

        public Builder addConstraints(List<TConstraintClause> constraints) {
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

        public Builder addConstraints(TConstraintClause constraint) {
            if (Objects.isNull(constraint)) {
                return this;
            }

            return addConstraints(Collections.singletonList(constraint));
        }

        public Builder setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder setKeySchema(TSchemaDefinition keySchema) {
            this.keySchema = keySchema;
            return this;
        }

        public TParameterDefinition build() {
            return new TParameterDefinition(this);
        }
    }
}
