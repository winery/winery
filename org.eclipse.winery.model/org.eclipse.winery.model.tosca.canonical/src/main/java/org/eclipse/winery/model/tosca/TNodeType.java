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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
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
    @XmlElement(name = "RequirementDefinitions")
    protected TNodeType.RequirementDefinitions requirementDefinitions;
    @XmlElement(name = "CapabilityDefinitions")
    protected TNodeType.CapabilityDefinitions capabilityDefinitions;
    @XmlElement(name = "InstanceStates")
    protected TTopologyElementInstanceStates instanceStates;
    @XmlElement(name = "Interfaces")
    protected TInterfaces interfaces;
    @XmlElement(name = "Artifacts")
    protected TArtifacts artifacts;

    // added to support TOSCA YAML
    protected List<TInterfaceDefinition> interfaceDefinitions;

    @Deprecated // used for XML deserialization of API request content
    public TNodeType() { }

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

    public TNodeType.@Nullable RequirementDefinitions getRequirementDefinitions() {
        return requirementDefinitions;
    }

    public void setRequirementDefinitions(TNodeType.@Nullable RequirementDefinitions value) {
        this.requirementDefinitions = value;
    }

    public TNodeType.@Nullable CapabilityDefinitions getCapabilityDefinitions() {
        return capabilityDefinitions;
    }

    public void setCapabilityDefinitions(TNodeType.@Nullable CapabilityDefinitions value) {
        this.capabilityDefinitions = value;
    }

    @Nullable
    public TTopologyElementInstanceStates getInstanceStates() {
        return instanceStates;
    }

    public void setInstanceStates(@Nullable TTopologyElementInstanceStates value) {
        this.instanceStates = value;
    }

    public @Nullable TInterfaces getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(@Nullable TInterfaces value) {
        this.interfaces = value;
    }
    
    @Nullable
    public List<TInterfaceDefinition> getInterfaceDefinitions() {
        return interfaceDefinitions;
    }

    public void setInterfaceDefinitions(List<TInterfaceDefinition> interfaceDefinitions) {
        this.interfaceDefinitions = interfaceDefinitions;
    }

    public @Nullable TArtifacts getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(@Nullable TArtifacts value) {
        this.artifacts = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "capabilityDefinition"
    })
    public static class CapabilityDefinitions implements Serializable {

        @XmlElement(name = "CapabilityDefinition", required = true)
        protected List<TCapabilityDefinition> capabilityDefinition;

        @NonNull
        public List<TCapabilityDefinition> getCapabilityDefinition() {
            if (capabilityDefinition == null) {
                capabilityDefinition = new ArrayList<TCapabilityDefinition>();
            }
            return this.capabilityDefinition;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CapabilityDefinitions that = (CapabilityDefinitions) o;
            return Objects.equals(capabilityDefinition, that.capabilityDefinition);
        }

        @Override
        public int hashCode() {
            return Objects.hash(capabilityDefinition);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "requirementDefinition"
    })
    public static class RequirementDefinitions implements Serializable {

        @XmlElement(name = "RequirementDefinition", required = true)
        protected List<TRequirementDefinition> requirementDefinition;

        @NonNull
        public List<TRequirementDefinition> getRequirementDefinition() {
            if (requirementDefinition == null) {
                requirementDefinition = new ArrayList<TRequirementDefinition>();
            }
            return this.requirementDefinition;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RequirementDefinitions that = (RequirementDefinitions) o;
            return Objects.equals(requirementDefinition, that.requirementDefinition);
        }

        @Override
        public int hashCode() {
            return Objects.hash(requirementDefinition);
        }
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        private RequirementDefinitions requirementDefinitions;
        private CapabilityDefinitions capabilityDefinitions;
        private TTopologyElementInstanceStates instanceStates;
        private TInterfaces interfaces;
        private List<TInterfaceDefinition> interfaceDefinitions;
        private TArtifacts artifacts;

        public Builder(String name) {
            super(name);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setRequirementDefinitions(TNodeType.RequirementDefinitions requirementDefinitions) {
            this.requirementDefinitions = requirementDefinitions;
            return this;
        }

        public Builder setCapabilityDefinitions(TNodeType.CapabilityDefinitions capabilityDefinitions) {
            this.capabilityDefinitions = capabilityDefinitions;
            return this;
        }

        public Builder setInstanceStates(TTopologyElementInstanceStates instanceStates) {
            this.instanceStates = instanceStates;
            return this;
        }

        public Builder setInterfaces(TInterfaces interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder setArtifacts(TArtifacts artifacts) {
            this.artifacts = artifacts;
            return this;
        }

        public Builder addRequirementDefinitions(TNodeType.RequirementDefinitions requirementDefinitions) {
            if (requirementDefinitions == null || requirementDefinitions.getRequirementDefinition().isEmpty()) {
                return this;
            }

            if (this.requirementDefinitions == null) {
                this.requirementDefinitions = requirementDefinitions;
            } else {
                this.requirementDefinitions.getRequirementDefinition().addAll(requirementDefinitions.getRequirementDefinition());
            }
            return this;
        }

        public Builder addRequirementDefinitions(List<TRequirementDefinition> requirementDefinitions) {
            if (requirementDefinitions == null) {
                return this;
            }

            TNodeType.RequirementDefinitions tmp = new TNodeType.RequirementDefinitions();
            tmp.getRequirementDefinition().addAll(requirementDefinitions);
            return addRequirementDefinitions(tmp);
        }

        public Builder addRequirementDefinitions(TRequirementDefinition requirementDefinition) {
            if (requirementDefinition == null) {
                return this;
            }

            TNodeType.RequirementDefinitions tmp = new TNodeType.RequirementDefinitions();
            tmp.getRequirementDefinition().add(requirementDefinition);
            return addRequirementDefinitions(tmp);
        }

        public Builder addCapabilityDefinitions(TNodeType.CapabilityDefinitions capabilityDefinitions) {
            if (capabilityDefinitions == null || capabilityDefinitions.getCapabilityDefinition().isEmpty()) {
                return this;
            }

            if (this.capabilityDefinitions == null) {
                this.capabilityDefinitions = capabilityDefinitions;
            } else {
                this.capabilityDefinitions.getCapabilityDefinition().addAll(capabilityDefinitions.getCapabilityDefinition());
            }
            return this;
        }

        public Builder addCapabilityDefinitions(List<TCapabilityDefinition> capabilityDefinitions) {
            if (capabilityDefinitions == null) {
                return this;
            }

            TNodeType.CapabilityDefinitions tmp = new TNodeType.CapabilityDefinitions();
            tmp.getCapabilityDefinition().addAll(capabilityDefinitions);
            return addCapabilityDefinitions(tmp);
        }

        public Builder addCapabilityDefinitions(TCapabilityDefinition capabilityDefinitions) {
            if (capabilityDefinitions == null) {
                return this;
            }

            TNodeType.CapabilityDefinitions tmp = new TNodeType.CapabilityDefinitions();
            tmp.getCapabilityDefinition().add(capabilityDefinitions);
            return addCapabilityDefinitions(tmp);
        }

        public Builder addInterfaces(TInterfaces interfaces) {
            if (interfaces == null || interfaces.getInterface().isEmpty()) {
                return this;
            }

            if (this.interfaces == null) {
                this.interfaces = interfaces;
            } else {
                this.interfaces.getInterface().addAll(interfaces.getInterface());
            }
            return this;
        }

        public Builder addInterfaces(List<TInterface> interfaces) {
            if (interfaces == null) {
                return this;
            }

            TInterfaces tmp = new TInterfaces();
            tmp.getInterface().addAll(interfaces);
            return addInterfaces(tmp);
        }

        public Builder addInterfaces(TInterface interfaces) {
            if (interfaces == null) {
                return this;
            }

            TInterfaces tmp = new TInterfaces();
            tmp.getInterface().add(interfaces);
            return addInterfaces(tmp);
        }

        public Builder setInterfaceDefinitions(List<TInterfaceDefinition> interfaceDefinitions) {
            this.interfaceDefinitions = interfaceDefinitions;
            return self();
        }

        public Builder addArtifacts(TArtifacts artifacts) {
            if (artifacts == null || artifacts.getArtifact().isEmpty()) {
                return this;
            }

            if (this.artifacts == null) {
                this.artifacts = artifacts;
            } else {
                this.artifacts.getArtifact().addAll(artifacts.getArtifact());
            }
            return this;
        }

        public Builder addArtifacts(List<TArtifact> artifacts) {
            if (artifacts == null) {
                return this;
            }

            TArtifacts tmp = new TArtifacts();
            tmp.getArtifact().addAll(artifacts);
            return addArtifacts(tmp);
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
