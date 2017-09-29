/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml;

import java.util.Collections;
import java.util.LinkedHashMap;
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
@XmlType(name = "tEntityType", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "description",
    "version",
    "derivedFrom",
    "properties",
    "attributes",
    "metadata"
})
public class TEntityType implements VisitorNode {
    private String description;
    private TVersion version;
    @XmlAttribute(name = "derived_from")
    private QName derivedFrom;
    private Map<String, TPropertyDefinition> properties;
    private Map<String, TAttributeDefinition> attributes;
    private Metadata metadata;

    public TEntityType() {
    }

    public TEntityType(Builder builder) {
        this.setDescription(builder.description);
        this.setVersion(builder.version);
        this.setDerivedFrom(builder.derivedFrom);
        this.setProperties(builder.properties);
        this.setAttributes(builder.attributes);
        this.setMetadata(builder.metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TEntityType)) return false;
        TEntityType that = (TEntityType) o;
        return Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getVersion(), that.getVersion()) &&
            Objects.equals(getDerivedFrom(), that.getDerivedFrom()) &&
            Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getAttributes(), that.getAttributes()) &&
            Objects.equals(getMetadata(), that.getMetadata());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getVersion(), getDerivedFrom(), getProperties(), getAttributes(), getMetadata());
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public TVersion getVersion() {
        return version;
    }

    public void setVersion(TVersion version) {
        this.version = version;
    }

    public void setVersion(String version) {
        TVersion tmp = new TVersion(version);
        setVersion(tmp);
    }

    @Nullable
    public QName getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(QName derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    @NonNull
    public Map<String, TPropertyDefinition> getProperties() {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<>();
        }

        return properties;
    }

    public void setProperties(Map<String, TPropertyDefinition> properties) {
        this.properties = properties;
    }

    @NonNull
    public Map<String, TAttributeDefinition> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>();
        }

        return attributes;
    }

    public void setAttributes(Map<String, TAttributeDefinition> attributes) {
        this.attributes = attributes;
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

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public abstract static class Builder<T extends Builder<T>> {
        private String description;
        private TVersion version;
        private QName derivedFrom;
        private Map<String, TPropertyDefinition> properties;
        private Map<String, TAttributeDefinition> attributes;
        private Metadata metadata;

        public Builder() {

        }

        public Builder(TEntityType entityType) {
            this.description = entityType.getDescription();
            this.version = entityType.getVersion();
            this.derivedFrom = entityType.getDerivedFrom();
            this.properties = entityType.getProperties();
            this.attributes = entityType.getAttributes();
            this.metadata = entityType.getMetadata();
        }

        public T setDescription(String description) {
            this.description = description;
            return self();
        }

        public T setVersion(TVersion version) {
            this.version = version;
            return self();
        }

        public T setDerivedFrom(QName derivedFrom) {
            this.derivedFrom = derivedFrom;
            return self();
        }

        public T setProperties(Map<String, TPropertyDefinition> properties) {
            this.properties = properties;
            return self();
        }

        public T setAttributes(Map<String, TAttributeDefinition> attributes) {
            this.attributes = attributes;
            return self();
        }

        public T setMetadata(Metadata metadata) {
            if (this.metadata == null) {
                this.metadata = metadata;
            } else {
                this.metadata.putAll(metadata);
            }
            return self();
        }

        public T setMetadata(String key, String value) {
            Metadata metadata = new Metadata();
            metadata.put(key, value);
            return self().setMetadata(metadata);
        }

        public T addProperties(Map<String, TPropertyDefinition> properties) {
            if (properties == null || properties.isEmpty()) {
                return self();
            }

            if (this.properties == null) {
                this.properties = new LinkedHashMap<>(properties);
            } else {
                this.properties.putAll(properties);
            }

            return self();
        }

        public T addProperties(String name, TPropertyDefinition property) {
            if (name == null || name.isEmpty()) {
                return self();
            }

            return addProperties(Collections.singletonMap(name, property));
        }

        public T addAttributes(Map<String, TAttributeDefinition> attributes) {
            if (attributes == null || attributes.isEmpty()) {
                return self();
            }

            if (this.attributes == null) {
                this.attributes = new LinkedHashMap<>(attributes);
            } else {
                this.attributes.putAll(attributes);
            }

            return self();
        }

        public T addAttributes(String name, TAttributeDefinition attribute) {
            if (name == null || name.isEmpty()) {
                return self();
            }

            return addAttributes(Collections.singletonMap(name, attribute));
        }

        public abstract T self();

        public TEntityType build() {
            return new TEntityType(this);
        }
    }
}
