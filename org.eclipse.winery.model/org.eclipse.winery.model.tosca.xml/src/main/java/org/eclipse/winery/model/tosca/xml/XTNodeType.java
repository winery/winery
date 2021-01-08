/*******************************************************************************
 * Copyright (c) 2013-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNodeType", propOrder = {
    "requirementDefinitions",
    "capabilityDefinitions",
    "instanceStates",
    "interfaces"
})
public class XTNodeType extends XTEntityType {
    @XmlElement(name = "RequirementDefinitions")
    protected XTNodeType.RequirementDefinitions requirementDefinitions;
    @XmlElement(name = "CapabilityDefinitions")
    protected XTNodeType.CapabilityDefinitions capabilityDefinitions;
    @XmlElement(name = "InstanceStates")
    protected XTTopologyElementInstanceStates instanceStates;
    @XmlElement(name = "Interfaces")
    protected XTInterfaces interfaces;

    @Deprecated // required for XML deserialization
    public XTNodeType() { }

    public XTNodeType(Builder builder) {
        super(builder);
        this.requirementDefinitions = builder.requirementDefinitions;
        this.capabilityDefinitions = builder.capabilityDefinitions;
        this.instanceStates = builder.instanceStates;
        this.interfaces = builder.interfaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTNodeType)) return false;
        if (!super.equals(o)) return false;
        XTNodeType tNodeType = (XTNodeType) o;
        return Objects.equals(requirementDefinitions, tNodeType.requirementDefinitions) &&
            Objects.equals(capabilityDefinitions, tNodeType.capabilityDefinitions) &&
            Objects.equals(instanceStates, tNodeType.instanceStates) &&
            Objects.equals(interfaces, tNodeType.interfaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requirementDefinitions, capabilityDefinitions, instanceStates, interfaces);
    }

    public XTNodeType.@Nullable RequirementDefinitions getRequirementDefinitions() {
        return requirementDefinitions;
    }

    public void setRequirementDefinitions(XTNodeType.@Nullable RequirementDefinitions value) {
        this.requirementDefinitions = value;
    }

    public XTNodeType.@Nullable CapabilityDefinitions getCapabilityDefinitions() {
        return capabilityDefinitions;
    }

    public void setCapabilityDefinitions(XTNodeType.@Nullable CapabilityDefinitions value) {
        this.capabilityDefinitions = value;
    }

    @Nullable
    public XTTopologyElementInstanceStates getInstanceStates() {
        return instanceStates;
    }

    public void setInstanceStates(@Nullable XTTopologyElementInstanceStates value) {
        this.instanceStates = value;
    }

    public @Nullable XTInterfaces getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(@Nullable XTInterfaces value) {
        this.interfaces = value;
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
        protected List<XTCapabilityDefinition> capabilityDefinition;

        @NonNull
        public List<XTCapabilityDefinition> getCapabilityDefinition() {
            if (capabilityDefinition == null) {
                capabilityDefinition = new ArrayList<XTCapabilityDefinition>();
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
        protected List<XTRequirementDefinition> requirementDefinition;

        @NonNull
        public List<XTRequirementDefinition> getRequirementDefinition() {
            if (requirementDefinition == null) {
                requirementDefinition = new ArrayList<XTRequirementDefinition>();
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

    public static class Builder extends XTEntityType.Builder<Builder> {
        private RequirementDefinitions requirementDefinitions;
        private CapabilityDefinitions capabilityDefinitions;
        private XTTopologyElementInstanceStates instanceStates;
        private XTInterfaces interfaces;

        public Builder(String name) {
            super(name);
        }

        public Builder(XTEntityType entityType) {
            super(entityType);
        }

        public Builder setRequirementDefinitions(XTNodeType.RequirementDefinitions requirementDefinitions) {
            this.requirementDefinitions = requirementDefinitions;
            return this;
        }

        public Builder setCapabilityDefinitions(XTNodeType.CapabilityDefinitions capabilityDefinitions) {
            this.capabilityDefinitions = capabilityDefinitions;
            return this;
        }

        public Builder setInstanceStates(XTTopologyElementInstanceStates instanceStates) {
            this.instanceStates = instanceStates;
            return this;
        }

        public Builder setInterfaces(XTInterfaces interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder addRequirementDefinitions(XTNodeType.RequirementDefinitions requirementDefinitions) {
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

        public Builder addRequirementDefinitions(List<XTRequirementDefinition> requirementDefinitions) {
            if (requirementDefinitions == null) {
                return this;
            }

            XTNodeType.RequirementDefinitions tmp = new XTNodeType.RequirementDefinitions();
            tmp.getRequirementDefinition().addAll(requirementDefinitions);
            return addRequirementDefinitions(tmp);
        }

        public Builder addRequirementDefinitions(XTRequirementDefinition requirementDefinition) {
            if (requirementDefinition == null) {
                return this;
            }

            XTNodeType.RequirementDefinitions tmp = new XTNodeType.RequirementDefinitions();
            tmp.getRequirementDefinition().add(requirementDefinition);
            return addRequirementDefinitions(tmp);
        }

        public Builder addCapabilityDefinitions(XTNodeType.CapabilityDefinitions capabilityDefinitions) {
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

        public Builder addCapabilityDefinitions(List<XTCapabilityDefinition> capabilityDefinitions) {
            if (capabilityDefinitions == null) {
                return this;
            }

            XTNodeType.CapabilityDefinitions tmp = new XTNodeType.CapabilityDefinitions();
            tmp.getCapabilityDefinition().addAll(capabilityDefinitions);
            return addCapabilityDefinitions(tmp);
        }

        public Builder addCapabilityDefinitions(XTCapabilityDefinition capabilityDefinitions) {
            if (capabilityDefinitions == null) {
                return this;
            }

            XTNodeType.CapabilityDefinitions tmp = new XTNodeType.CapabilityDefinitions();
            tmp.getCapabilityDefinition().add(capabilityDefinitions);
            return addCapabilityDefinitions(tmp);
        }

        public Builder addInterfaces(XTInterfaces interfaces) {
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

        public Builder addInterfaces(List<XTInterface> interfaces) {
            if (interfaces == null) {
                return this;
            }

            XTInterfaces tmp = new XTInterfaces();
            tmp.getInterface().addAll(interfaces);
            return addInterfaces(tmp);
        }

        public Builder addInterfaces(XTInterface interfaces) {
            if (interfaces == null) {
                return this;
            }

            XTInterfaces tmp = new XTInterfaces();
            tmp.getInterface().add(interfaces);
            return addInterfaces(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public XTNodeType build() {
            return new XTNodeType(this);
        }
    }
}
