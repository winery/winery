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
@XmlType(name = "tRelationshipTemplate", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "type",
    "description",
    "metadata",
    "properties",
    "attributes",
    "interfaces",
    "copy"
})
public class TRelationshipTemplate implements VisitorNode {
    @XmlAttribute(name = "type", required = true)
    private QName type;
    private String description;
    private Metadata metadata;
    private Map<String, TPropertyAssignment> properties;
    private Map<String, TAttributeAssignment> attributes;
    private Map<String, TInterfaceDefinition> interfaces;
    private QName copy;

    public TRelationshipTemplate() {
    }

    public TRelationshipTemplate(Builder builder) {
        this.setType(builder.type);
        this.setDescription(builder.description);
        this.setMetadata(builder.metadata);
        this.setAttributes(builder.attributes);
        this.setProperties(builder.properties);
        this.setInterfaces(builder.interfaces);
        this.setCopy(builder.copy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRelationshipTemplate)) return false;
        TRelationshipTemplate that = (TRelationshipTemplate) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getMetadata(), that.getMetadata()) &&
            Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getAttributes(), that.getAttributes()) &&
            Objects.equals(getInterfaces(), that.getInterfaces()) &&
            Objects.equals(getCopy(), that.getCopy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDescription(), getMetadata(), getProperties(), getAttributes(), getInterfaces(), getCopy());
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
    public Map<String, TInterfaceDefinition> getInterfaces() {
        if (this.interfaces == null) {
            this.interfaces = new LinkedHashMap<>();
        }

        return interfaces;
    }

    public void setInterfaces(Map<String, TInterfaceDefinition> interfaces) {
        this.interfaces = interfaces;
    }

    @Nullable
    public QName getCopy() {
        return copy;
    }

    public void setCopy(QName copy) {
        this.copy = copy;
    }

    @NonNull
    public Map<String, TAttributeAssignment> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>();
        }

        return attributes;
    }

    public void setAttributes(Map<String, TAttributeAssignment> attributes) {
        this.attributes = attributes;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private String description;
        private Metadata metadata;
        private Map<String, TAttributeAssignment> attributes;
        private Map<String, TPropertyAssignment> properties;
        private Map<String, TInterfaceDefinition> interfaces;
        private QName copy;

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

        public Builder setAttributes(Map<String, TAttributeAssignment> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setProperties(Map<String, TPropertyAssignment> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setInterfaces(Map<String, TInterfaceDefinition> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder setCopy(QName copy) {
            this.copy = copy;
            return this;
        }

        public Builder addAttributes(Map<String, TAttributeAssignment> attributes) {
            if (attributes == null || attributes.isEmpty()) {
                return this;
            }

            if (this.attributes == null) {
                this.attributes = new LinkedHashMap<>(attributes);
            } else {
                this.attributes.putAll(attributes);
            }

            return this;
        }

        public Builder addAttribtues(String name, TAttributeAssignment attribute) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return this.addAttributes(Collections.singletonMap(name, attribute));
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

        public Builder addInterfaces(Map<String, TInterfaceDefinition> interfaces) {
            if (interfaces == null || interfaces.isEmpty()) {
                return this;
            }

            if (this.interfaces == null) {
                this.interfaces = new LinkedHashMap<>(interfaces);
            } else {
                this.interfaces.putAll(interfaces);
            }

            return this;
        }

        public Builder addInterfaces(String name, TInterfaceDefinition interfaceDefinition) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addInterfaces(Collections.singletonMap(name, interfaceDefinition));
        }

        public TRelationshipTemplate build() {
            return new TRelationshipTemplate(this);
        }
    }
}
