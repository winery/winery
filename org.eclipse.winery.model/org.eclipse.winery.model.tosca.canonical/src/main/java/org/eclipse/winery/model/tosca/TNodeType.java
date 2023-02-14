/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNodeType", propOrder = {
    "requirementDefinitions",
    "capabilityDefinitions",
    "instanceStates",
    "interfaces",
    "interfaceDefinitions",
    "artifacts"
})
public class TNodeType extends TEntityType {

    @XmlElementWrapper(name = "RequirementDefinitions")
    @XmlElement(name = "RequirementDefinition", required = true)
    protected List<TRequirementDefinition> requirementDefinitions;

    @XmlElementWrapper(name = "CapabilityDefinitions")
    @XmlElement(name = "CapabilityDefinition", required = true)
    protected List<TCapabilityDefinition> capabilityDefinitions;

    @XmlElementWrapper(name = "InstanceStates")
    @XmlElement(name = "InstanceState", required = true)
    protected List<TInstanceState> instanceStates;

    @XmlElementWrapper(name = "Interfaces")
    @XmlElement(name = "Interface", required = true)
    protected List<TInterface> interfaces;

    @XmlElementWrapper(name = "Artifacts")
    @XmlElement(name = "Artifact")
    protected List<TArtifact> artifacts;

    // added to support TOSCA YAML
    protected List<TInterfaceDefinition> interfaceDefinitions;

    @Deprecated // used for XML deserialization of API request content
    public TNodeType() {
    }

    public TNodeType(Builder builder) {
        super(builder);
        this.requirementDefinitions = builder.requirementDefinitions;
        this.capabilityDefinitions = builder.capabilityDefinitions;
        this.instanceStates = builder.instanceStates;
        this.interfaces = builder.interfaces;
        this.interfaceDefinitions = builder.interfaceDefinitions;
        this.artifacts = builder.artifacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TNodeType)) return false;
        if (!super.equals(o)) return false;
        TNodeType tNodeType = (TNodeType) o;
        return Objects.equals(requirementDefinitions, tNodeType.requirementDefinitions) &&
            Objects.equals(capabilityDefinitions, tNodeType.capabilityDefinitions) &&
            Objects.equals(instanceStates, tNodeType.instanceStates) &&
            Objects.equals(interfaces, tNodeType.interfaces) &&
            Objects.equals(artifacts, tNodeType.artifacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requirementDefinitions, capabilityDefinitions, instanceStates, interfaces);
    }

    public List<TRequirementDefinition> getRequirementDefinitions() {
        return requirementDefinitions;
    }

    public void setRequirementDefinitions(List<TRequirementDefinition> value) {
        this.requirementDefinitions = value;
    }

    public List<TCapabilityDefinition> getCapabilityDefinitions() {
        return capabilityDefinitions;
    }

    public void setCapabilityDefinitions(List<TCapabilityDefinition> value) {
        this.capabilityDefinitions = value;
    }

    @Nullable
    public List<TInstanceState> getInstanceStates() {
        return instanceStates;
    }

    public void setInstanceStates(List<TInstanceState> value) {
        this.instanceStates = value;
    }

    public List<TInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<TInterface> value) {
        this.interfaces = value;
    }

    @Nullable
    public List<TInterfaceDefinition> getInterfaceDefinitions() {
        return interfaceDefinitions;
    }

    public void setInterfaceDefinitions(List<TInterfaceDefinition> interfaceDefinitions) {
        this.interfaceDefinitions = interfaceDefinitions;
    }

    @Nullable
    public List<TArtifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<TArtifact> value) {
        this.artifacts = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        
        private List<TRequirementDefinition> requirementDefinitions;
        private List<TCapabilityDefinition> capabilityDefinitions;
        private List<TInstanceState> instanceStates;
        private List<TInterface> interfaces;
        private List<TInterfaceDefinition> interfaceDefinitions;
        private List<TArtifact> artifacts;

        @Deprecated
        public Builder(String name) {
            super(name);
        }

        public Builder(QName id) {
            super(id);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setRequirementDefinitions(List<TRequirementDefinition> requirementDefinitions) {
            this.requirementDefinitions = requirementDefinitions;
            return this;
        }

        public Builder setCapabilityDefinitions(List<TCapabilityDefinition> capabilityDefinitions) {
            this.capabilityDefinitions = capabilityDefinitions;
            return this;
        }

        public Builder setInstanceStates(List<TInstanceState> instanceStates) {
            this.instanceStates = instanceStates;
            return this;
        }

        public Builder setInterfaces(List<TInterface> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder setArtifacts(List<TArtifact> artifacts) {
            this.artifacts = artifacts;
            return this;
        }

        public Builder addRequirementDefinitions(List<TRequirementDefinition> requirementDefinitions) {
            if (requirementDefinitions == null || requirementDefinitions.isEmpty()) {
                return this;
            }

            if (this.requirementDefinitions == null) {
                this.requirementDefinitions = requirementDefinitions;
            } else {
                this.requirementDefinitions.addAll(requirementDefinitions);
            }
            return this;
        }

        public Builder addRequirementDefinitions(TRequirementDefinition requirementDefinition) {
            if (requirementDefinition == null) {
                return this;
            }

            List<TRequirementDefinition> requirements = new ArrayList<>();
            requirements.add(requirementDefinition);
            return addRequirementDefinitions(requirements);
        }

        public Builder addCapabilityDefinitions(List<TCapabilityDefinition> capabilityDefinitions) {
            if (capabilityDefinitions == null || capabilityDefinitions.isEmpty()) {
                return this;
            }

            if (this.capabilityDefinitions == null) {
                this.capabilityDefinitions = capabilityDefinitions;
            } else {
                this.capabilityDefinitions.addAll(capabilityDefinitions);
            }
            return this;
        }

        public Builder addCapabilityDefinitions(TCapabilityDefinition capabilityDefinitions) {
            if (capabilityDefinitions == null) {
                return this;
            }

            List<TCapabilityDefinition> tmp = new ArrayList<>();
            tmp.add(capabilityDefinitions);
            return addCapabilityDefinitions(tmp);
        }

        public Builder addInterfaces(List<TInterface> interfaces) {
            if (interfaces == null || interfaces.isEmpty()) {
                return this;
            }

            if (this.interfaces == null) {
                this.interfaces = interfaces;
            } else {
                this.interfaces.addAll(interfaces);
            }
            return this;
        }

        public Builder addInterfaces(TInterface interfaces) {
            if (interfaces == null) {
                return this;
            }

            List<TInterface> tmp = new ArrayList<>();
            tmp.add(interfaces);
            return addInterfaces(tmp);
        }

        public Builder setInterfaceDefinitions(List<TInterfaceDefinition> interfaceDefinitions) {
            this.interfaceDefinitions = interfaceDefinitions;
            return self();
        }

        public Builder addArtifacts(List<TArtifact> artifacts) {
            if (artifacts == null || artifacts.isEmpty()) {
                return this;
            }

            if (this.artifacts == null) {
                this.artifacts = artifacts;
            } else {
                this.artifacts.addAll(artifacts);
            }
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TNodeType build() {
            return new TNodeType(this);
        }
    }
}
