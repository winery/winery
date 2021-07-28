/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tBoundaryDefinitions", propOrder = {
    "properties",
    "propertyConstraints",
    "requirements",
    "capabilities",
    "policies",
    "interfaces"
})
public class XTBoundaryDefinitions implements Serializable {

    @XmlElement(name = "Properties")
    protected XTBoundaryDefinitions.Properties properties;

    @XmlElementWrapper(name = "PropertyConstraints")
    @XmlElement(name = "PropertyConstraint", required = true)
    protected List<XTPropertyConstraint> propertyConstraints;

    @XmlElementWrapper(name = "Requirements")
    @XmlElement(name = "Requirement", required = true)
    protected List<XTRequirementRef> requirements;

    @XmlElementWrapper(name = "Capabilities")
    @XmlElement(name = "Capability", required = true)
    protected List<XTCapabilityRef> capabilities;

    @XmlElement(name = "Policies")
    protected XTPolicies policies;

    @XmlElementWrapper(name = "Interfaces")
    @XmlElement(name = "Interface", required = true)
    protected List<XTExportedInterface> interfaces;

    @Deprecated // required for XML deserialization
    public XTBoundaryDefinitions() {
    }

    public XTBoundaryDefinitions(Builder builder) {
        this.properties = builder.properties;
        this.propertyConstraints = builder.propertyConstraints;
        this.requirements = builder.requirements;
        this.capabilities = builder.capabilities;
        this.policies = builder.policies;
        this.interfaces = builder.interfaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTBoundaryDefinitions)) return false;
        XTBoundaryDefinitions that = (XTBoundaryDefinitions) o;
        return Objects.equals(properties, that.properties) &&
            Objects.equals(propertyConstraints, that.propertyConstraints) &&
            Objects.equals(requirements, that.requirements) &&
            Objects.equals(capabilities, that.capabilities) &&
            Objects.equals(policies, that.policies) &&
            Objects.equals(interfaces, that.interfaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, propertyConstraints, requirements, capabilities, policies, interfaces);
    }

    public XTBoundaryDefinitions.@Nullable Properties getProperties() {
        return properties;
    }

    public void setProperties(XTBoundaryDefinitions.@Nullable Properties value) {
        this.properties = value;
    }

    public List<XTPropertyConstraint> getPropertyConstraints() {
        return propertyConstraints;
    }

    public void setPropertyConstraints(List<XTPropertyConstraint> value) {
        this.propertyConstraints = value;
    }

    public List<XTRequirementRef> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<XTRequirementRef> value) {
        this.requirements = value;
    }

    public List<XTCapabilityRef> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<XTCapabilityRef> value) {
        this.capabilities = value;
    }

    public @Nullable XTPolicies getPolicies() {
        return policies;
    }

    public void setPolicies(@Nullable XTPolicies value) {
        this.policies = value;
    }

    public List<XTExportedInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<XTExportedInterface> value) {
        this.interfaces = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any",
        "propertyMappings"
    })
    public static class Properties implements Serializable {

        @XmlAnyElement(lax = true)
        protected Object any;
        @XmlElementWrapper(name = "PropertyMappings")
        @XmlElement(name = "PropertyMapping", required = true)
        protected List<XTPropertyMapping> propertyMappings;

        @Nullable
        public Object getAny() {
            return any;
        }

        public void setAny(@Nullable Object value) {
            this.any = value;
        }

        public List<XTPropertyMapping> getPropertyMappings() {
            return propertyMappings;
        }

        public void setPropertyMappings(List<XTPropertyMapping> value) {
            this.propertyMappings = value;
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    public static class Builder {
        private Properties properties;
        private List<XTPropertyConstraint> propertyConstraints;
        private List<XTRequirementRef> requirements;
        private List<XTCapabilityRef> capabilities;
        private XTPolicies policies;
        private List<XTExportedInterface> interfaces;

        public Builder() {

        }

        public Builder setProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public Builder setPropertyConstraints(List<XTPropertyConstraint> propertyConstraints) {
            this.propertyConstraints = propertyConstraints;
            return this;
        }

        public Builder setRequirements(List<XTRequirementRef> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setCapabilities(List<XTCapabilityRef> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setPolicies(XTPolicies policies) {
            this.policies = policies;
            return this;
        }

        public Builder setInterfaces(List<XTExportedInterface> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder addPolicies(XTPolicies policies) {
            if (policies == null || policies.getPolicy().isEmpty()) {
                return this;
            }

            if (this.policies == null) {
                this.policies = policies;
            } else {
                this.policies.getPolicy().addAll(policies.getPolicy());
            }
            return this;
        }

        public Builder addPolicies(List<XTPolicy> policies) {
            if (policies == null) {
                return this;
            }

            XTPolicies tmp = new XTPolicies();
            tmp.getPolicy().addAll(policies);
            return this.addPolicies(tmp);
        }

        public Builder addPolicies(XTPolicy policies) {
            if (policies == null) {
                return this;
            }

            XTPolicies tmp = new XTPolicies();
            tmp.getPolicy().add(policies);
            return this.addPolicies(tmp);
        }

        public XTBoundaryDefinitions build() {
            return new XTBoundaryDefinitions(this);
        }
    }
}
