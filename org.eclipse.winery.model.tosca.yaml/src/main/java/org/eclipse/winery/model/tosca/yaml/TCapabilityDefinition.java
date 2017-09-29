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

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCapabilityDefinition", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "description",
    "occurrences",
    "validSourceTypes",
    "type",
    "properties",
    "attributes"
})
public class TCapabilityDefinition implements VisitorNode {
    private String description;
    private List<String> occurrences;
    @XmlAttribute(name = "valid_source_types")
    private List<QName> validSourceTypes;
    @XmlAttribute(name = "type", required = true)
    private QName type;
    private Map<String, TPropertyDefinition> properties;
    private Map<String, TAttributeDefinition> attributes;

    public TCapabilityDefinition() {

    }

    public TCapabilityDefinition(Builder builder) {
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
        if (!(o instanceof TCapabilityDefinition)) return false;
        TCapabilityDefinition that = (TCapabilityDefinition) o;
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
    public String getUpperBound() {
        if (occurrences == null || occurrences.size() <= 1) {
            return "1";
        } else {
            return occurrences.get(1);
        }
    }

    @NonNull
    public Integer getLowerBound() {
        if (occurrences == null || occurrences.isEmpty()) {
            return 1;
        } else {
            return Integer.valueOf(occurrences.get(0));
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
        private Map<String, TPropertyDefinition> properties;
        private Map<String, TAttributeDefinition> attributes;

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

        public Builder setValidSourceTypes(List<QName> validSourceTypes) {
            this.validSourceTypes = validSourceTypes;
            return this;
        }

        public Builder setProperties(Map<String, TPropertyDefinition> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setAttributes(Map<String, TAttributeDefinition> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder addOccurrences(List<String> occurrences) {
            if (occurrences == null || occurrences.isEmpty()) {
                return this;
            }

            if (this.occurrences == null) {
                this.occurrences = new ArrayList<>(occurrences);
            } else {
                this.occurrences.addAll(occurrences);
            }

            return this;
        }

        public Builder addOccurrences(String occurrence) {
            if (occurrence == null || occurrence.isEmpty()) {
                return this;
            }

            return addOccurrences(Collections.singletonList(occurrence));
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

        public Builder addProperties(Map<String, TPropertyDefinition> properties) {
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

        public Builder addProperties(String name, TPropertyDefinition property) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonMap(name, property));
        }

        public Builder addAttributes(Map<String, TAttributeDefinition> attributes) {
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

        public Builder addAttributes(String name, TAttributeDefinition attribute) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addAttributes(Collections.singletonMap(name, attribute));
        }

        public TCapabilityDefinition build() {
            return new TCapabilityDefinition(this);
        }
    }
}
