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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPropertyDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
    "type",
    "description",
    "required",
    "defaultValue",
    "status",
    "constraints",
    "entrySchema"
})
public class TPropertyDefinition extends TPropertyAssignmentOrDefinition {

    @XmlAttribute(name = "type", required = true)
    private QName type;
    private String description;
    private Boolean required;
    @XmlElement(name = "default")
    private Object defaultValue;
    private TStatusValue status;
    @XmlElement
    private List<TConstraintClause> constraints;
    @XmlAttribute(name = "entry_schema")
    private TEntrySchema entrySchema;

    public TPropertyDefinition() {
    }

    public TPropertyDefinition(Builder builder) {
        this.setType(builder.type);
        this.setDescription(builder.description);
        this.setRequired(builder.required);
        this.setDefault(builder.defaultValue);
        this.setStatus(builder.status);
        this.setConstraints(builder.constraints);
        this.setEntrySchema(builder.entrySchema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDescription(), getRequired(), getDefault(), getStatus(), getConstraints(), getEntrySchema());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPropertyDefinition)) return false;
        TPropertyDefinition that = (TPropertyDefinition) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getRequired(), that.getRequired()) &&
            Objects.equals(getDefault(), that.getDefault()) &&
            getStatus() == that.getStatus() &&
            Objects.equals(getConstraints(), that.getConstraints()) &&
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
            "}";
    }

    @NonNull
    public QName getType() {
        return type;
    }

    public void setType(QName type) {
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
    public TStatusValue getStatus() {
        if (status == null) {
            status = TStatusValue.supported;
        }
        return status;
    }

    public void setStatus(TStatusValue status) {
        this.status = status;
    }

    @NonNull
    public List<TConstraintClause> getConstraints() {
        if (constraints == null) {
            constraints = new ArrayList<>();
        }
        return constraints;
    }

    public void setConstraints(List<TConstraintClause> constraints) {
        this.constraints = constraints;
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
        private Boolean required;
        private Object defaultValue;
        private TStatusValue status;
        private List<TConstraintClause> constraints;
        private TEntrySchema entrySchema;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder(TPropertyDefinition propertyDefinition) {
            this.type = propertyDefinition.getType();
            this.description = propertyDefinition.getDescription();
            this.required = propertyDefinition.getRequired();
            this.defaultValue = propertyDefinition.getDefault();
            this.status = propertyDefinition.getStatus();
            this.constraints = propertyDefinition.getConstraints();
            this.entrySchema = propertyDefinition.getEntrySchema();
        }

        public Builder setDescription(String description) {
            this.description = description;
            return (Builder) this;
        }

        public Builder setRequired(Boolean required) {
            this.required = required;
            return (Builder) this;
        }

        public Builder setDefault(Object defaultValue) {
            this.defaultValue = defaultValue;
            return (Builder) this;
        }

        public Builder setStatus(TStatusValue status) {
            this.status = status;
            return (Builder) this;
        }

        public Builder setStatus(String status) {
            return setStatus(TStatusValue.getStatus(status));
        }

        public Builder setEntrySchema(TEntrySchema entrySchema) {
            this.entrySchema = entrySchema;
            return (Builder) this;
        }

        public Builder setConstraints(List<TConstraintClause> constraints) {
            if (constraints == null || constraints.isEmpty()) {
                return (Builder) this;
            }
            this.constraints = constraints;
            return (Builder) this;
        }

        public Builder addConstraints(List<TConstraintClause> constraints) {
            if (constraints == null || constraints.isEmpty()) {
                return (Builder) this;
            }

            if (this.constraints == null) {
                this.constraints = new ArrayList<>(constraints);
            } else {
                this.constraints.addAll(constraints);
            }

            return (Builder) this;
        }

        public Builder addConstraints(TConstraintClause constraint) {
            if (constraint == null) {
                return (Builder) this;
            }

            return addConstraints(Collections.singletonList(constraint));
        }

        public TPropertyDefinition build() {
            return new TPropertyDefinition(this);
        }
    }
}
