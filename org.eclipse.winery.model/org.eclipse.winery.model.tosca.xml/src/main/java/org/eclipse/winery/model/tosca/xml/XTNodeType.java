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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNodeType", propOrder = {
    "requirementDefinitions",
    "capabilityDefinitions",
    "instanceStates",
    "interfaces"
})
public class XTNodeType extends XTEntityType {

    @XmlElementWrapper(name = "RequirementDefinitions")
    @XmlElement(name = "RequirementDefinition", required = true)
    protected List<XTRequirementDefinition> requirementDefinitions;

    @XmlElementWrapper(name = "CapabilityDefinitions")
    @XmlElement(name = "CapabilityDefinition")
    protected List<XTCapabilityDefinition> capabilityDefinitions;

    @XmlElement(name = "InstanceStates")
    protected List<XTInstanceState> instanceStates;

    @XmlElementWrapper(name = "Interfaces")
    @XmlElement(name = "Interface", required = true)
    protected List<XTInterface> interfaces;

    @Deprecated // required for XML deserialization
    public XTNodeType() {
    }

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

    public List<XTRequirementDefinition> getRequirementDefinitions() {
        return requirementDefinitions;
    }

    public void setRequirementDefinitions(List<XTRequirementDefinition> value) {
        this.requirementDefinitions = value;
    }

    public List<XTCapabilityDefinition> getCapabilityDefinitions() {
        return capabilityDefinitions;
    }

    public void setCapabilityDefinitions(List<XTCapabilityDefinition> value) {
        this.capabilityDefinitions = value;
    }

    @Nullable
    public List<XTInstanceState> getInstanceStates() {
        return instanceStates;
    }

    public void setInstanceStates(List<XTInstanceState> value) {
        this.instanceStates = value;
    }

    @Nullable
    public List<XTInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(@Nullable List<XTInterface> value) {
        this.interfaces = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends XTEntityType.Builder<Builder> {

        private List<XTRequirementDefinition> requirementDefinitions;
        private List<XTCapabilityDefinition> capabilityDefinitions;
        private List<XTInstanceState> instanceStates;
        private List<XTInterface> interfaces;

        public Builder(String name) {
            super(name);
        }

        public Builder(XTEntityType entityType) {
            super(entityType);
        }

        public Builder setRequirementDefinitions(List<XTRequirementDefinition> requirementDefinitions) {
            this.requirementDefinitions = requirementDefinitions;
            return this;
        }

        public Builder setCapabilityDefinitions(List<XTCapabilityDefinition> capabilityDefinitions) {
            this.capabilityDefinitions = capabilityDefinitions;
            return this;
        }

        public Builder setInstanceStates(List<XTInstanceState> instanceStates) {
            this.instanceStates = instanceStates;
            return this;
        }

        public Builder setInterfaces(List<XTInterface> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder addInterfaces(List<XTInterface> interfaces) {
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

        public Builder addInterface(XTInterface interfaces) {
            if (interfaces == null) {
                return this;
            }

            List<XTInterface> tmp = new ArrayList<>();
            tmp.add(interfaces);
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
