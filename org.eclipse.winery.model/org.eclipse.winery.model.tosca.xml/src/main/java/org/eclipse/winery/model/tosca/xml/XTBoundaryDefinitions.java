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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
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
    @XmlElement(name = "PropertyConstraints")
    protected XTBoundaryDefinitions.PropertyConstraints propertyConstraints;
    @XmlElement(name = "Requirements")
    protected XTBoundaryDefinitions.Requirements requirements;
    @XmlElement(name = "Capabilities")
    protected XTBoundaryDefinitions.Capabilities capabilities;
    @XmlElement(name = "Policies")
    protected XTPolicies policies;
    @XmlElement(name = "Interfaces")
    protected XTBoundaryDefinitions.Interfaces interfaces;

    @Deprecated // required for XML deserialization
    public XTBoundaryDefinitions() { }

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

    public XTBoundaryDefinitions.@Nullable PropertyConstraints getPropertyConstraints() {
        return propertyConstraints;
    }

    public void setPropertyConstraints(XTBoundaryDefinitions.@Nullable PropertyConstraints value) {
        this.propertyConstraints = value;
    }

    public XTBoundaryDefinitions.@Nullable Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(XTBoundaryDefinitions.@Nullable Requirements value) {
        this.requirements = value;
    }

    public XTBoundaryDefinitions.@Nullable Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(XTBoundaryDefinitions.@Nullable Capabilities value) {
        this.capabilities = value;
    }

    public @Nullable XTPolicies getPolicies() {
        return policies;
    }

    public void setPolicies(@Nullable XTPolicies value) {
        this.policies = value;
    }

    public XTBoundaryDefinitions.@Nullable Interfaces getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(XTBoundaryDefinitions.@Nullable Interfaces value) {
        this.interfaces = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "capability"
    })
    public static class Capabilities implements Serializable {

        @XmlElement(name = "Capability", required = true)
        protected List<XTCapabilityRef> capability;

        @NonNull
        public List<XTCapabilityRef> getCapability() {
            if (capability == null) {
                capability = new ArrayList<XTCapabilityRef>();
            }
            return this.capability;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "_interface"
    })
    public static class Interfaces implements Serializable {

        @XmlElement(name = "Interface", required = true)
        protected List<XTExportedInterface> _interface;

        @NonNull
        public List<XTExportedInterface> getInterface() {
            if (_interface == null) {
                _interface = new ArrayList<XTExportedInterface>();
            }
            return this._interface;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any",
        "propertyMappings"
    })
    public static class Properties implements Serializable {

        @XmlAnyElement(lax = true)
        protected Object any;
        @XmlElement(name = "PropertyMappings")
        protected XTBoundaryDefinitions.Properties.PropertyMappings propertyMappings;

        @Nullable
        public Object getAny() {
            return any;
        }

        public void setAny(@Nullable Object value) {
            this.any = value;
        }

        public XTBoundaryDefinitions.Properties.@Nullable PropertyMappings getPropertyMappings() {
            return propertyMappings;
        }

        public void setPropertyMappings(XTBoundaryDefinitions.Properties.@Nullable PropertyMappings value) {
            this.propertyMappings = value;
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "propertyMapping"
        })
        public static class PropertyMappings implements Serializable {

            @XmlElement(name = "PropertyMapping", required = true)
            protected List<XTPropertyMapping> propertyMapping;

            @NonNull
            public List<XTPropertyMapping> getPropertyMapping() {
                if (propertyMapping == null) {
                    propertyMapping = new ArrayList<XTPropertyMapping>();
                }
                return this.propertyMapping;
            }
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "propertyConstraint"
    })
    public static class PropertyConstraints implements Serializable {

        @XmlElement(name = "PropertyConstraint", required = true)
        protected List<XTPropertyConstraint> propertyConstraint;

        @NonNull
        public List<XTPropertyConstraint> getPropertyConstraint() {
            if (propertyConstraint == null) {
                propertyConstraint = new ArrayList<>();
            }
            return this.propertyConstraint;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "requirement"
    })
    public static class Requirements implements Serializable {

        @XmlElement(name = "Requirement", required = true)
        protected List<XTRequirementRef> requirement;

        @NonNull
        public List<XTRequirementRef> getRequirement() {
            if (requirement == null) {
                requirement = new ArrayList<>();
            }
            return this.requirement;
        }
    }

    public static class Builder {
        private Properties properties;
        private PropertyConstraints propertyConstraints;
        private Requirements requirements;
        private Capabilities capabilities;
        private XTPolicies policies;
        private Interfaces interfaces;

        public Builder() {

        }

        public Builder setProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public Builder setPropertyConstraints(PropertyConstraints propertyConstraints) {
            this.propertyConstraints = propertyConstraints;
            return this;
        }

        public Builder setRequirements(Requirements requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setCapabilities(Capabilities capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setPolicies(XTPolicies policies) {
            this.policies = policies;
            return this;
        }

        public Builder setInterfaces(Interfaces interfaces) {
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
