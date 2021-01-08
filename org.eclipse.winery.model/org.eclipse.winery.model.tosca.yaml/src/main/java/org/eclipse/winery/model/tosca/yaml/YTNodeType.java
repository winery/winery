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

import org.eclipse.winery.model.tosca.yaml.support.YTMapRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import org.eclipse.jdt.annotation.NonNull;

public class YTNodeType extends YTNodeOrGroupType {
    private Map<String, YTAttributeDefinition> attributes;
    private List<YTMapRequirementDefinition> requirements;
    private Map<String, YTCapabilityDefinition> capabilities;
    private Map<String, YTInterfaceDefinition> interfaces;
    private Map<String, YTArtifactDefinition> artifacts;

    protected YTNodeType(Builder builder) {
        super(builder);
        this.setAttributes(builder.attributes);
        this.setRequirements(builder.requirements);
        this.setCapabilities(builder.capabilities);
        this.setInterfaces(builder.interfaces);
        this.setArtifacts(builder.artifacts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTNodeType)) return false;
        if (!super.equals(o)) return false;
        YTNodeType tNodeType = (YTNodeType) o;
        return Objects.equals(getAttributes(), tNodeType.getAttributes()) &&
            Objects.equals(getRequirements(), tNodeType.getRequirements()) &&
            Objects.equals(getCapabilities(), tNodeType.getCapabilities()) &&
            Objects.equals(getInterfaces(), tNodeType.getInterfaces()) &&
            Objects.equals(getArtifacts(), tNodeType.getArtifacts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAttributes(), getRequirements(), getCapabilities(), getInterfaces(), getArtifacts());
    }

    @Override
    public String toString() {
        return "TNodeType{" +
            "attributes=" + getAttributes() +
            ", requirements=" + getRequirements() +
            ", capabilities=" + getCapabilities() +
            ", interfaces=" + getInterfaces() +
            ", artifacts=" + getArtifacts() +
            "} " + super.toString();
    }

    @NonNull
    public Map<String, YTAttributeDefinition> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        }
        return attributes;
    }

    public void setAttributes(Map<String, YTAttributeDefinition> attributes) {
        this.attributes = attributes;
    }

    @NonNull
    public List<YTMapRequirementDefinition> getRequirements() {
        if (requirements == null) {
            requirements = new ArrayList<>();
        }

        return requirements;
    }

    public void setRequirements(List<YTMapRequirementDefinition> requirements) {
        this.requirements = requirements;
    }

    @NonNull
    public Map<String, YTCapabilityDefinition> getCapabilities() {
        if (this.capabilities == null) {
            this.capabilities = new LinkedHashMap<>();
        }

        return capabilities;
    }

    public void setCapabilities(Map<String, YTCapabilityDefinition> capabilities) {
        this.capabilities = capabilities;
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

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        R ir1 = super.accept(visitor, parameter);
        R ir2 = visitor.visit(this, parameter);
        if (ir1 == null) {
            return ir2;
        } else {
            return ir1.add(ir2);
        }
    }

    public static class Builder extends YTEntityType.Builder<Builder> {
        private Map<String, YTAttributeDefinition> attributes;
        private List<YTMapRequirementDefinition> requirements;
        private Map<String, YTCapabilityDefinition> capabilities;
        private Map<String, YTInterfaceDefinition> interfaces;
        private Map<String, YTArtifactDefinition> artifacts;

        public Builder() {

        }

        public Builder(YTEntityType entityType) {
            super(entityType);
        }

        public Builder setAttributes(Map<String, YTAttributeDefinition> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setRequirements(List<YTMapRequirementDefinition> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setCapabilities(Map<String, YTCapabilityDefinition> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setInterfaces(Map<String, YTInterfaceDefinition> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder setArtifacts(Map<String, YTArtifactDefinition> artifacts) {
            this.artifacts = artifacts;
            return this;
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

        @Override
        public Builder self() {
            return this;
        }

        public Builder addRequirements(List<YTMapRequirementDefinition> requirements) {
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

        public Builder addRequirements(YTMapRequirementDefinition requirement) {
            if (requirement == null || requirement.isEmpty()) {
                return this;
            }

            return addRequirements(Collections.singletonList(requirement));
        }

        public Builder addRequirements(Map<String, YTRequirementDefinition> requirements) {
            if (requirements == null || requirements.isEmpty()) {
                return this;
            }

            requirements.forEach((key, value) -> {
                YTMapRequirementDefinition tmp = new YTMapRequirementDefinition();
                tmp.put(key, value);
                addRequirements(tmp);
            });

            return this;
        }

        public Builder addRequirements(String name, YTRequirementDefinition requirement) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRequirements(Collections.singletonMap(name, requirement));
        }

        public Builder addCapabilities(Map<String, YTCapabilityDefinition> capabilities) {
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

        public Builder addCapabilities(String name, YTCapabilityDefinition capability) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addCapabilities(Collections.singletonMap(name, capability));
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

        public YTNodeType build() {
            return new YTNodeType(this);
        }
    }
}
