/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicyDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "type",
    "description",
    "metadata",
    "properties",
    "targets"
})
public class TPolicyDefinition implements VisitorNode {
    @XmlAttribute(name = "type", required = true)
    private QName type;
    private String description;
    private Metadata metadata;
    private Map<String, TPropertyAssignment> properties;
    private List<QName> targets;

    public TPolicyDefinition() {
    }

    public TPolicyDefinition(Builder builder) {
        this.setType(builder.type);
        this.setDescription(builder.description);
        this.setMetadata(builder.metadata);
        this.setProperties(builder.properties);
        this.setTargets(builder.targets);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPolicyDefinition)) return false;
        TPolicyDefinition that = (TPolicyDefinition) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getMetadata(), that.getMetadata()) &&
            Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getTargets(), that.getTargets());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDescription(), getMetadata(), getProperties(), getTargets());
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
    public Metadata getMetadata() {
        if (!Objects.nonNull(metadata)) {
            this.metadata = new Metadata();
        }

        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @NonNull
    public Map<String, TPropertyAssignment> getProperties() {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<>();
        }

        return properties;
    }

    public void setProperties(Map<String, TPropertyAssignment> properties) {
        this.properties = properties;
    }

    @NonNull
    public List<QName> getTargets() {
        if (this.targets == null) {
            this.targets = new ArrayList<>();
        }

        return targets;
    }

    public void setTargets(List<QName> targets) {
        this.targets = targets;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private String description;
        private Metadata metadata;
        private Map<String, TPropertyAssignment> properties;
        private List<QName> targets;


        public Builder(QName type) {
            this.type = type;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder setProperties(Map<String, TPropertyAssignment> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setTargets(List<QName> targets) {
            this.targets = targets;
            return this;
        }

        public Builder addProperties(Map<String, TPropertyAssignment> properties) {
            if (properties == null || properties.isEmpty()) {
                return this;
            }

            if (this.properties == null) {
                this.properties = new LinkedHashMap<>(properties);
            } else {
                this.properties.putAll(properties);
            }

            return this;
        }

        public Builder addProperties(String name, TPropertyAssignment property) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonMap(name, property));
        }

        public Builder addTargets(List<QName> targets) {
            if (targets == null || targets.isEmpty()) {
                return this;
            }

            if (this.targets == null) {
                this.targets = new ArrayList<>(targets);
            } else {
                this.targets.addAll(targets);
            }

            return this;
        }

        public Builder addTargets(QName target) {
            if (target == null) {
                return this;
            }

            return addTargets(Collections.singletonList(target));
        }

        public TPolicyDefinition build() {
            return new TPolicyDefinition(this);
        }
    }
}
