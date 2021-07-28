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
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

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
public class TBoundaryDefinitions implements Serializable {

    @XmlElement(name = "Properties")
    protected TBoundaryDefinitions.Properties properties;

    @XmlElementWrapper(name = "PropertyConstraints")
    @XmlElement(name = "PropertyConstraint", required = true)
    protected List<TPropertyConstraint> propertyConstraints;

    @XmlElementWrapper(name = "Requirements")
    @XmlElement(name = "Requirement", required = true)
    protected List<TRequirementRef> requirements;

    @XmlElementWrapper(name = "Capabilities")
    @XmlElement(name = "Capability", required = true)
    protected List<TCapabilityRef> capabilities;

    @XmlElement(name = "Policies")
    protected TPolicies policies;

    @XmlElementWrapper(name = "Interfaces")
    @XmlElement(name = "Interface", required = true)
    protected List<TExportedInterface> interfaces;

    @Deprecated // used for XML deserialization of API request content
    public TBoundaryDefinitions() {
    }

    public TBoundaryDefinitions(Builder builder) {
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
        if (!(o instanceof TBoundaryDefinitions)) return false;
        TBoundaryDefinitions that = (TBoundaryDefinitions) o;
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

    public TBoundaryDefinitions.@Nullable Properties getProperties() {
        return properties;
    }

    public void setProperties(TBoundaryDefinitions.@Nullable Properties value) {
        this.properties = value;
    }

    public List<TPropertyConstraint> getPropertyConstraints() {
        return propertyConstraints;
    }

    public void setPropertyConstraints(List<TPropertyConstraint> value) {
        this.propertyConstraints = value;
    }

    public List<TRequirementRef> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<TRequirementRef> value) {
        this.requirements = value;
    }

    public List<TCapabilityRef> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<TCapabilityRef> value) {
        this.capabilities = value;
    }

    public @Nullable TPolicies getPolicies() {
        return policies;
    }

    public void setPolicies(@Nullable TPolicies value) {
        this.policies = value;
    }

    public List<TExportedInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<TExportedInterface> value) {
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
        protected List<TPropertyMapping> propertyMappings;

        @Nullable
        public Object getAny() {
            return any;
        }

        public void setAny(@Nullable Object value) {
            this.any = value;
        }

        public List<TPropertyMapping> getPropertyMappings() {
            return propertyMappings;
        }

        public void setPropertyMappings(List<TPropertyMapping> value) {
            this.propertyMappings = value;
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    public static class Builder {

        private Properties properties;
        private List<TPropertyConstraint> propertyConstraints;
        private List<TRequirementRef> requirements;
        private List<TCapabilityRef> capabilities;
        private TPolicies policies;
        private List<TExportedInterface> interfaces;

        public Builder() {

        }

        public Builder setProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public Builder setPropertyConstraints(List<TPropertyConstraint> propertyConstraints) {
            this.propertyConstraints = propertyConstraints;
            return this;
        }

        public Builder setRequirements(List<TRequirementRef> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setCapabilities(List<TCapabilityRef> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setPolicies(TPolicies policies) {
            this.policies = policies;
            return this;
        }

        public Builder setInterfaces(List<TExportedInterface> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder addPolicies(TPolicies policies) {
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

        public Builder addPolicies(List<TPolicy> policies) {
            if (policies == null) {
                return this;
            }

            TPolicies tmp = new TPolicies();
            tmp.getPolicy().addAll(policies);
            return this.addPolicies(tmp);
        }

        public Builder addPolicies(TPolicy policies) {
            if (policies == null) {
                return this;
            }

            TPolicies tmp = new TPolicies();
            tmp.getPolicy().add(policies);
            return this.addPolicies(tmp);
        }

        public TBoundaryDefinitions build() {
            return new TBoundaryDefinitions(this);
        }
    }
}
