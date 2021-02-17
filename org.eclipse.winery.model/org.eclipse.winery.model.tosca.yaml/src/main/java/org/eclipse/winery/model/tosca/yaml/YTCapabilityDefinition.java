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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTCapabilityDefinition implements VisitorNode {
    private String description;
    private List<String> occurrences;
    private List<QName> validSourceTypes;
    private QName type;
    private Map<String, YTPropertyDefinition> properties;
    private Map<String, YTAttributeDefinition> attributes;

    protected YTCapabilityDefinition(Builder builder) {
        this.setType(builder.type);
        this.setDescription(builder.description);
        this.setOccurrences(builder.occurrences);
        this.setValidSourceTypes(builder.validSourceTypes);
        this.setProperties(builder.properties);
        this.setAttributes(builder.attributes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTCapabilityDefinition)) return false;
        YTCapabilityDefinition that = (YTCapabilityDefinition) o;
        return Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getOccurrences(), that.getOccurrences()) &&
            Objects.equals(getValidSourceTypes(), that.getValidSourceTypes()) &&
            Objects.equals(getType(), that.getType()) &&
            Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getAttributes(), that.getAttributes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getOccurrences(), getValidSourceTypes(), getType(), getProperties(), getAttributes());
    }

    @Override
    public String toString() {
        return "TCapabilityDefinition{" +
            "description='" + getDescription() + '\'' +
            ", occurrences=" + getOccurrences() +
            ", validSourceTypes=" + getValidSourceTypes() +
            ", type=" + getType() +
            ", properties=" + getProperties() +
            ", attributes=" + getAttributes() +
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
    public List<String> getOccurrences() {
        if (occurrences == null) {
            occurrences = new ArrayList<>();
        }
        if (occurrences.size() < 1) {
            occurrences.add("1");
        }
        if (occurrences.size() < 2) {
            occurrences.add("UNBOUNDED");
        }

        return occurrences;
    }

    public void setOccurrences(List<String> occurrences) {
        this.occurrences = occurrences;
    }

    public List<QName> getValidSourceTypes() {
        return validSourceTypes;
    }

    public void setValidSourceTypes(List<QName> validSourceTypes) {
        this.validSourceTypes = validSourceTypes;
    }

    @NonNull
    public QName getType() {
        return type;
    }

    public void setType(QName type) {
        this.type = type;
    }

    @NonNull
    public Map<String, YTPropertyDefinition> getProperties() {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<>();
        }

        return properties;
    }

    public void setProperties(Map<String, YTPropertyDefinition> properties) {
        this.properties = properties;
    }

    @NonNull
    public Map<String, YTAttributeDefinition> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>();
        }

        return attributes;
    }

    public void setAttributes(Map<String, YTAttributeDefinition> attributes) {
        this.attributes = attributes;
    }

    @NonNull
    public String getUpperBound() {
        if (getOccurrences() == null || getOccurrences().size() <= 1) {
            return "1";
        } else {
            return getOccurrences().get(1);
        }
    }

    @NonNull
    public Integer getLowerBound() {
        if (getOccurrences() == null || getOccurrences().isEmpty()) {
            return 1;
        } else {
            return Integer.valueOf(getOccurrences().get(0));
        }
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private String description;
        private List<String> occurrences;
        private List<QName> validSourceTypes;
        private Map<String, YTPropertyDefinition> properties;
        private Map<String, YTAttributeDefinition> attributes;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setOccurrences(List<String> occurrences) {
            this.occurrences = occurrences;
            return this;
        }

        public Builder setOccurrences(int lowerBound, String upperBound) {
            return setOccurrences(Stream.of(String.valueOf(lowerBound), upperBound)
                .collect(Collectors.toList()));
        }

        public Builder setValidSourceTypes(List<QName> validSourceTypes) {
            this.validSourceTypes = validSourceTypes;
            return this;
        }

        public Builder setProperties(Map<String, YTPropertyDefinition> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setAttributes(Map<String, YTAttributeDefinition> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder addValidSourceTypes(List<QName> validSourceTypes) {
            if (validSourceTypes == null || validSourceTypes.isEmpty()) {
                return this;
            }

            if (this.validSourceTypes == null) {
                this.validSourceTypes = new ArrayList<>(validSourceTypes);
            } else {
                this.validSourceTypes.addAll(validSourceTypes);
            }

            return this;
        }

        public Builder addValidSourceTypes(QName validSourceType) {
            if (validSourceType == null) {
                return this;
            }

            return addValidSourceTypes(Collections.singletonList(validSourceType));
        }

        public Builder addProperties(Map<String, YTPropertyDefinition> properties) {
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

        public Builder addProperties(String name, YTPropertyDefinition property) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonMap(name, property));
        }

        public Builder addAttributes(Map<String, YTAttributeDefinition> attributes) {
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

        public Builder addAttributes(String name, YTAttributeDefinition attribute) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addAttributes(Collections.singletonMap(name, attribute));
        }

        public YTCapabilityDefinition build() {
            return new YTCapabilityDefinition(this);
        }
    }
}
