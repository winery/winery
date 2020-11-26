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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTRelationshipTemplate implements VisitorNode {
    private QName type;
    private String description;
    private Metadata metadata;
    private Map<String, YTPropertyAssignment> properties;
    private Map<String, YTAttributeAssignment> attributes;
    private Map<String, YTInterfaceDefinition> interfaces;
    private QName copy;

    protected YTRelationshipTemplate(Builder builder) {
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
        if (!(o instanceof YTRelationshipTemplate)) return false;
        YTRelationshipTemplate that = (YTRelationshipTemplate) o;
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

    @Override
    public String toString() {
        return "TRelationshipTemplate{" +
            "type=" + getType() +
            ", description='" + getDescription() + '\'' +
            ", metadata=" + getMetadata() +
            ", properties=" + getProperties() +
            ", attributes=" + getAttributes() +
            ", interfaces=" + getInterfaces() +
            ", copy=" + getCopy() +
            '}';
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
    public Map<String, YTPropertyAssignment> getProperties() {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<>();
        }

        return properties;
    }

    public void setProperties(Map<String, YTPropertyAssignment> properties) {
        this.properties = properties;
    }

    @NonNull
    public Map<String, YTInterfaceDefinition> getInterfaces() {
        if (this.interfaces == null) {
            this.interfaces = new LinkedHashMap<>();
        }

        return interfaces;
    }

    public void setInterfaces(Map<String, YTInterfaceDefinition> interfaces) {
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
    public Map<String, YTAttributeAssignment> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>();
        }

        return attributes;
    }

    public void setAttributes(Map<String, YTAttributeAssignment> attributes) {
        this.attributes = attributes;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private String description;
        private Metadata metadata;
        private Map<String, YTAttributeAssignment> attributes;
        private Map<String, YTPropertyAssignment> properties;
        private Map<String, YTInterfaceDefinition> interfaces;
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

        public Builder setAttributes(Map<String, YTAttributeAssignment> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setProperties(Map<String, YTPropertyAssignment> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setInterfaces(Map<String, YTInterfaceDefinition> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder setCopy(QName copy) {
            this.copy = copy;
            return this;
        }

        public Builder addAttributes(Map<String, YTAttributeAssignment> attributes) {
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

        public Builder addAttribtues(String name, YTAttributeAssignment attribute) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return this.addAttributes(Collections.singletonMap(name, attribute));
        }

        public Builder addProperties(Map<String, YTPropertyAssignment> properties) {
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

        public Builder addProperties(String name, YTPropertyAssignment property) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonMap(name, property));
        }

        public Builder addInterfaces(Map<String, YTInterfaceDefinition> interfaces) {
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

        public Builder addInterfaces(String name, YTInterfaceDefinition interfaceDefinition) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addInterfaces(Collections.singletonMap(name, interfaceDefinition));
        }

        public YTRelationshipTemplate build() {
            return new YTRelationshipTemplate(this);
        }
    }
}
