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
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNodeTemplate", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "type",
    "description",
    "directives",
    "properties",
    "attributes",
    "requirements",
    "capabilities",
    "interfaces",
    "artifacts",
    "nodeFilter",
    "copy",
    "metadata"
})
public class TNodeTemplate implements VisitorNode {
    @XmlAttribute(name = "type", required = true)
    private QName type;
    private String description;
    private Metadata metadata;
    private List<String> directives;
    private Map<String, TPropertyAssignment> properties;
    private Map<String, TAttributeAssignment> attributes;
    private List<TMapRequirementAssignment> requirements;
    private Map<String, TCapabilityAssignment> capabilities;
    private Map<String, TInterfaceDefinition> interfaces;
    private Map<String, TArtifactDefinition> artifacts;
    @XmlAttribute(name = "node_filter")
    private TNodeFilterDefinition nodeFilter;
    private QName copy;

    public TNodeTemplate() {
    }

    public TNodeTemplate(Builder builder) {
        this.setType(builder.type);
        this.setDescription(builder.description);
        this.setMetadata(builder.metadata);
        this.setDirectives(builder.directives);
        this.setProperties(builder.properties);
        this.setAttributes(builder.attributes);
        this.setRequirements(builder.requirements);
        this.setCapabilities(builder.capabilities);
        this.setInterfaces(builder.interfaces);
        this.setArtifacts(builder.artifacts);
        this.setNodeFilter(builder.nodeFilter);
        this.setCopy(builder.copy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TNodeTemplate)) return false;
        TNodeTemplate that = (TNodeTemplate) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getMetadata(), that.getMetadata()) &&
            Objects.equals(getDirectives(), that.getDirectives()) &&
            Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getAttributes(), that.getAttributes()) &&
            Objects.equals(getRequirements(), that.getRequirements()) &&
            Objects.equals(getCapabilities(), that.getCapabilities()) &&
            Objects.equals(getInterfaces(), that.getInterfaces()) &&
            Objects.equals(getArtifacts(), that.getArtifacts()) &&
            Objects.equals(getNodeFilter(), that.getNodeFilter()) &&
            Objects.equals(getCopy(), that.getCopy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDescription(), getMetadata(), getDirectives(), getProperties(), getAttributes(), getRequirements(), getCapabilities(), getInterfaces(), getArtifacts(), getNodeFilter(), getCopy());
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
        if (this.metadata == null) {
            this.metadata = new Metadata();
        }

        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @NonNull
    public List<String> getDirectives() {
        if (this.directives == null) {
            this.directives = new ArrayList<>();
        }

        return directives;
    }

    public void setDirectives(List<String> directives) {
        this.directives = directives;
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
    public Map<String, TAttributeAssignment> getAttributes() {

        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>();
        }

        return attributes;
    }

    public void setAttributes(Map<String, TAttributeAssignment> attributes) {
        this.attributes = attributes;
    }

    @NonNull
    public List<TMapRequirementAssignment> getRequirements() {
        if (this.requirements == null) {
            this.requirements = new ArrayList<>();
        }

        return requirements;
    }

    public void setRequirements(List<TMapRequirementAssignment> requirements) {
        this.requirements = requirements;
    }

    @NonNull
    public Map<String, TCapabilityAssignment> getCapabilities() {
        if (this.capabilities == null) {
            this.capabilities = new LinkedHashMap<>();
        }

        return capabilities;
    }

    public void setCapabilities(Map<String, TCapabilityAssignment> capabilities) {
        this.capabilities = capabilities;
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

    @NonNull
    public Map<String, TArtifactDefinition> getArtifacts() {
        if (this.artifacts == null) {
            this.artifacts = new LinkedHashMap<>();
        }

        return artifacts;
    }

    public void setArtifacts(Map<String, TArtifactDefinition> artifacts) {
        this.artifacts = artifacts;
    }

    @Nullable
    public TNodeFilterDefinition getNodeFilter() {
        return nodeFilter;
    }

    public void setNodeFilter(TNodeFilterDefinition nodeFilter) {
        this.nodeFilter = nodeFilter;
    }

    @Nullable
    public QName getCopy() {
        return copy;
    }

    public void setCopy(QName copy) {
        this.copy = copy;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private String description;
        private Metadata metadata;
        private List<String> directives;
        private Map<String, TPropertyAssignment> properties;
        private Map<String, TAttributeAssignment> attributes;
        private List<TMapRequirementAssignment> requirements;
        private Map<String, TCapabilityAssignment> capabilities;
        private Map<String, TInterfaceDefinition> interfaces;
        private Map<String, TArtifactDefinition> artifacts;
        private TNodeFilterDefinition nodeFilter;
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

        public Builder setDirectives(List<String> directives) {
            this.directives = directives;
            return this;
        }

        public Builder setProperties(Map<String, TPropertyAssignment> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setAttributes(Map<String, TAttributeAssignment> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setRequirements(List<TMapRequirementAssignment> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setCapabilities(Map<String, TCapabilityAssignment> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setInterfaces(Map<String, TInterfaceDefinition> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder setArtifacts(Map<String, TArtifactDefinition> artifacts) {
            this.artifacts = artifacts;
            return this;
        }

        public Builder setNodeFilter(TNodeFilterDefinition nodeFilter) {
            this.nodeFilter = nodeFilter;
            return this;
        }

        public Builder setCopy(QName copy) {
            this.copy = copy;
            return this;
        }

        public Builder addDirectives(List<String> directives) {
            if (directives == null || directives.isEmpty()) {
                return this;
            }

            if (this.directives == null) {
                this.directives = new ArrayList<>(directives);
            } else {
                this.directives.addAll(directives);
            }

            return this;
        }

        public Builder addDirectives(String directive) {
            if (directive == null || directive.isEmpty()) {
                return this;
            }

            return addDirectives(Collections.singletonList(directive));
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

        public Builder addRequirements(List<TMapRequirementAssignment> requirements) {
            if (requirements == null || requirements.isEmpty()) {
                return this;
            }

            if (this.requirements == null) {
                this.requirements = new ArrayList<>(requirements);
            } else {
                this.requirements.addAll(requirements);
            }

            return this;
        }

        public Builder addRequirements(TMapRequirementAssignment requirement) {
            if (requirement == null || requirement.isEmpty()) {
                return this;
            }

            return addRequirements(Collections.singletonList(requirement));
        }

        public Builder addRequirements(Map<String, TRequirementAssignment> requirements) {
            if (requirements == null || requirements.isEmpty()) {
                return this;
            }

            requirements.forEach((key, value) -> {
                TMapRequirementAssignment tmp = new TMapRequirementAssignment();
                tmp.put(key, value);
                addRequirements(tmp);
            });

            return this;
        }

        public Builder addRequirements(String name, TRequirementAssignment requirement) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRequirements(Collections.singletonMap(name, requirement));
        }

        public Builder addCapabilities(Map<String, TCapabilityAssignment> capabilities) {
            if (capabilities == null || capabilities.isEmpty()) {
                return this;
            }

            if (this.capabilities == null) {
                this.capabilities = new LinkedHashMap<>(capabilities);
            } else {
                this.capabilities.putAll(capabilities);
            }

            return this;
        }

        public Builder addCapabilities(String name, TCapabilityAssignment capability) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addCapabilities(Collections.singletonMap(name, capability));
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

        public Builder addArtifacts(Map<String, TArtifactDefinition> artifacts) {
            if (artifacts == null || artifacts.isEmpty()) {
                return this;
            }

            if (this.artifacts == null) {
                this.artifacts = new LinkedHashMap<>(artifacts);
            } else {
                this.artifacts.putAll(artifacts);
            }

            return this;
        }

        public Builder addArtifacts(String name, TArtifactDefinition artifact) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addArtifacts(Collections.singletonMap(name, artifact));
        }

        public TNodeTemplate build() {
            return new TNodeTemplate(this);
        }
    }
}
