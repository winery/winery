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

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.YTMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTNodeTemplate implements VisitorNode {

    private QName type;
    private String description;
    private Metadata metadata;
    private List<String> directives;
    private Map<String, YTPropertyAssignment> properties;
    private Map<String, YTAttributeAssignment> attributes;
    private List<YTMapRequirementAssignment> requirements;
    private Map<String, YTCapabilityAssignment> capabilities;
    private Map<String, YTInterfaceAssignment> interfaces;
    private Map<String, YTArtifactDefinition> artifacts;
    private YTNodeFilterDefinition nodeFilter;
    private QName copy;

    protected YTNodeTemplate(Builder builder) {
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
        if (!(o instanceof YTNodeTemplate)) return false;
        YTNodeTemplate that = (YTNodeTemplate) o;
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

    @Override
    public String toString() {
        return "TNodeTemplate{" +
            "type=" + getType() +
            ", description='" + getDescription() + '\'' +
            ", metadata=" + getMetadata() +
            ", directives=" + getDirectives() +
            ", properties=" + getProperties() +
            ", attributes=" + getAttributes() +
            ", requirements=" + getRequirements() +
            ", capabilities=" + getCapabilities() +
            ", interfaces=" + getInterfaces() +
            ", artifacts=" + getArtifacts() +
            ", nodeFilter=" + getNodeFilter() +
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
    public Map<String, YTAttributeAssignment> getAttributes() {

        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>();
        }

        return attributes;
    }

    public void setAttributes(Map<String, YTAttributeAssignment> attributes) {
        this.attributes = attributes;
    }

    @NonNull
    public List<YTMapRequirementAssignment> getRequirements() {
        if (this.requirements == null) {
            this.requirements = new ArrayList<>();
        }

        return requirements;
    }

    public void setRequirements(List<YTMapRequirementAssignment> requirements) {
        this.requirements = requirements;
    }

    @NonNull
    public Map<String, YTCapabilityAssignment> getCapabilities() {
        if (this.capabilities == null) {
            this.capabilities = new LinkedHashMap<>();
        }

        return capabilities;
    }

    public void setCapabilities(Map<String, YTCapabilityAssignment> capabilities) {
        this.capabilities = capabilities;
    }

    @NonNull
    public Map<String, YTInterfaceAssignment> getInterfaces() {
        if (this.interfaces == null) {
            this.interfaces = new LinkedHashMap<>();
        }

        return interfaces;
    }

    public void setInterfaces(Map<String, YTInterfaceAssignment> interfaces) {
        this.interfaces = interfaces;
    }

    @NonNull
    public Map<String, YTArtifactDefinition> getArtifacts() {
        if (this.artifacts == null) {
            this.artifacts = new LinkedHashMap<>();
        }

        return artifacts;
    }

    public void setArtifacts(Map<String, YTArtifactDefinition> artifacts) {
        this.artifacts = artifacts;
    }

    @Nullable
    public YTNodeFilterDefinition getNodeFilter() {
        return nodeFilter;
    }

    public void setNodeFilter(YTNodeFilterDefinition nodeFilter) {
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
        private Map<String, YTPropertyAssignment> properties;
        private Map<String, YTAttributeAssignment> attributes;
        private List<YTMapRequirementAssignment> requirements;
        private Map<String, YTCapabilityAssignment> capabilities;
        private Map<String, YTInterfaceAssignment> interfaces;
        private Map<String, YTArtifactDefinition> artifacts;
        private YTNodeFilterDefinition nodeFilter;
        private QName copy;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setMetadata(Metadata metadata) {
            if (Objects.nonNull(metadata) && !metadata.isEmpty()) {
                this.metadata = metadata;
            }
            return this;
        }

        public Builder setDirectives(List<String> directives) {
            this.directives = directives;
            return this;
        }

        public Builder setProperties(Map<String, YTPropertyAssignment> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setAttributes(Map<String, YTAttributeAssignment> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setRequirements(List<YTMapRequirementAssignment> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setCapabilities(Map<String, YTCapabilityAssignment> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setInterfaces(Map<String, YTInterfaceAssignment> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder setArtifacts(Map<String, YTArtifactDefinition> artifacts) {
            this.artifacts = artifacts;
            return this;
        }

        public Builder setNodeFilter(YTNodeFilterDefinition nodeFilter) {
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

        public Builder addRequirements(List<YTMapRequirementAssignment> requirements) {
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

        public Builder addRequirements(YTMapRequirementAssignment requirement) {
            if (requirement == null || requirement.isEmpty()) {
                return this;
            }

            return addRequirements(Collections.singletonList(requirement));
        }

        public Builder addRequirements(Map<String, YTRequirementAssignment> requirements) {
            if (requirements == null || requirements.isEmpty()) {
                return this;
            }

            requirements.forEach((key, value) -> {
                YTMapRequirementAssignment tmp = new YTMapRequirementAssignment();
                tmp.put(key, value);
                addRequirements(tmp);
            });

            return this;
        }

        public Builder addRequirements(String name, YTRequirementAssignment requirement) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRequirements(Collections.singletonMap(name, requirement));
        }

        public Builder addCapabilities(Map<String, YTCapabilityAssignment> capabilities) {
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

        public Builder addCapabilities(String name, YTCapabilityAssignment capability) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addCapabilities(Collections.singletonMap(name, capability));
        }

        public Builder addInterfaces(Map<String, YTInterfaceAssignment> interfaces) {
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

        public Builder addInterface(String name, YTInterfaceAssignment interfaceAssignment) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addInterfaces(Collections.singletonMap(name, interfaceAssignment));
        }

        public Builder addArtifacts(Map<String, YTArtifactDefinition> artifacts) {
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

        public Builder addArtifacts(String name, YTArtifactDefinition artifact) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addArtifacts(Collections.singletonMap(name, artifact));
        }

        public YTNodeTemplate build() {
            return new YTNodeTemplate(this);
        }
    }
}
