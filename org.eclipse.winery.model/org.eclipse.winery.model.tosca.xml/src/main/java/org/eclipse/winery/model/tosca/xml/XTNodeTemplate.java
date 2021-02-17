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
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.xml.constants.Namespaces;
import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNodeTemplate", propOrder = {
    "requirements",
    "capabilities",
    "policies",
    "deploymentArtifacts"
})
public class XTNodeTemplate extends XRelationshipSourceOrTarget implements XHasPolicies {

    @XmlElement(name = "Requirements")
    protected XTNodeTemplate.Requirements requirements;
    @XmlElement(name = "Capabilities")
    protected XTNodeTemplate.Capabilities capabilities;
    @XmlElement(name = "Policies")
    protected XTPolicies policies;
    @XmlElement(name = "DeploymentArtifacts")
    protected XTDeploymentArtifacts deploymentArtifacts;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "minInstances")
    protected Integer minInstances;
    @XmlAttribute(name = "maxInstances")
    protected String maxInstances;

    @Deprecated // required for XML deserialization
    public XTNodeTemplate() {
        super();
    }

    public XTNodeTemplate(String id) {
        super(id);
    }

    public XTNodeTemplate(Builder builder) {
        super(builder);
        this.requirements = builder.requirements;
        this.capabilities = builder.capabilities;
        this.policies = builder.policies;
        this.deploymentArtifacts = builder.deploymentArtifacts;
        this.name = builder.name;
        this.minInstances = builder.minInstances;
        this.maxInstances = builder.maxInstances;

        if (Objects.nonNull(builder.x) && Objects.nonNull(builder.y)) {
            this.setX(builder.x);
            this.setY(builder.y);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTNodeTemplate)) return false;
        if (!super.equals(o)) return false;
        XTNodeTemplate that = (XTNodeTemplate) o;
        return Objects.equals(requirements, that.requirements) &&
            Objects.equals(capabilities, that.capabilities) &&
            Objects.equals(policies, that.policies) &&
            Objects.equals(deploymentArtifacts, that.deploymentArtifacts) &&
            Objects.equals(name, that.name) &&
            Objects.equals(minInstances, that.minInstances) &&
            Objects.equals(maxInstances, that.maxInstances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requirements, capabilities, policies, deploymentArtifacts, name, minInstances, maxInstances);
    }

    @Override
    @NonNull
    public String getFakeJacksonType() {
        return "nodetemplate";
    }

    public XTNodeTemplate.@Nullable Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(XTNodeTemplate.@Nullable Requirements value) {
        this.requirements = value;
    }

    public XTNodeTemplate.@Nullable Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(XTNodeTemplate.@Nullable Capabilities value) {
        this.capabilities = value;
    }

    public @Nullable XTPolicies getPolicies() {
        return policies;
    }

    public void setPolicies(@Nullable XTPolicies value) {
        this.policies = value;
    }

    @Nullable
    public XTDeploymentArtifacts getDeploymentArtifacts() {
        return deploymentArtifacts;
    }

    public void setDeploymentArtifacts(XTDeploymentArtifacts value) {
        this.deploymentArtifacts = value;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String value) {
        this.name = value;
    }

    @NonNull
    public int getMinInstances() {
        if (minInstances == null) {
            return 1;
        } else {
            return minInstances;
        }
    }

    public void setMinInstances(Integer value) {
        this.minInstances = value;
    }

    @NonNull
    public String getMaxInstances() {
        if (maxInstances == null) {
            return "1";
        } else {
            return maxInstances;
        }
    }

    public void setMaxInstances(String value) {
        this.maxInstances = value;
    }

    /**
     * In the JSON, also output this direct child of the node template object. Therefore, no JsonIgnore annotation.
     */
    @XmlTransient
    @Nullable
    public String getX() {
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        return otherNodeTemplateAttributes.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "x"));
    }

    /**
     * Sets the top coordinate of a {@link XTNodeTemplate}. When receiving the JSON, this method ensures that (i) the "y"
     * property can be handled and (ii) the Y coordinate is written correctly in the extension namespace.
     *
     * @param x the value of the x-coordinate to be set
     */
    public void setX(@NonNull String x) {
        Objects.requireNonNull(x);
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        otherNodeTemplateAttributes.put(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "x"), x);
    }

    /**
     * In the JSON, also output this direct child of the node template object. Therefore, no JsonIgnore annotation.
     */
    @XmlTransient
    @Nullable
    public String getY() {
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        return otherNodeTemplateAttributes.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "y"));
    }

    /**
     * Sets the top coordinate of a {@link XTNodeTemplate}. When receiving the JSON, this method ensures that (i) the "y"
     * property can be handled and (ii) the Y coordinate is written correctly in the extension namespace.
     *
     * @param y the value of the coordinate to be set
     */
    public void setY(@NonNull String y) {
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        otherNodeTemplateAttributes.put(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "y"), y);
    }

    public void accept(@NonNull Visitor visitor) {
        visitor.visit(this);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "capability"
    })
    public static class Capabilities implements Serializable {

        @XmlElement(name = "Capability", required = true)
        protected List<XTCapability> capability;

        /**
         * Gets the value of the capability property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
         * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
         * method for the capability property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCapability().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list {@link XTCapability }
         */
        @NonNull
        public List<XTCapability> getCapability() {
            if (capability == null) {
                capability = new ArrayList<XTCapability>();
            }
            return this.capability;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Capabilities that = (Capabilities) o;
            return Objects.equals(capability, that.capability);
        }

        @Override
        public int hashCode() {
            return Objects.hash(capability);
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "requirement"
    })
    public static class Requirements implements Serializable {

        @XmlElement(name = "Requirement", required = true)
        protected List<XTRequirement> requirement;

        @NonNull
        public List<XTRequirement> getRequirement() {
            if (requirement == null) {
                requirement = new ArrayList<XTRequirement>();
            }
            return this.requirement;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Requirements that = (Requirements) o;
            return Objects.equals(requirement, that.requirement);
        }

        @Override
        public int hashCode() {
            return Objects.hash(requirement);
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    public static class Builder extends XRelationshipSourceOrTarget.Builder<Builder> {
        private Requirements requirements;
        private Capabilities capabilities;
        private XTPolicies policies;
        private XTDeploymentArtifacts deploymentArtifacts;
        private String name;
        private Integer minInstances;
        private String maxInstances;
        private String x;
        private String y;

        public Builder(String id, QName type) {
            super(id, type);
        }

        public Builder(XTEntityTemplate entityTemplate) {
            super(entityTemplate);
        }

        public Builder setRequirements(XTNodeTemplate.Requirements requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setCapabilities(XTNodeTemplate.Capabilities capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setPolicies(XTPolicies policies) {
            this.policies = policies;
            return this;
        }

        public Builder setDeploymentArtifacts(XTDeploymentArtifacts deploymentArtifacts) {
            this.deploymentArtifacts = deploymentArtifacts;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setMinInstances(Integer minInstances) {
            this.minInstances = minInstances;
            return this;
        }

        public Builder setMaxInstances(String maxInstances) {
            this.maxInstances = maxInstances;
            return this;
        }

        public Builder setX(String x) {
            this.x = x;
            return this;
        }

        public Builder setY(String y) {
            this.y = y;
            return this;
        }

        public Builder addRequirements(XTNodeTemplate.Requirements requirements) {
            if (requirements == null || requirements.getRequirement().isEmpty()) {
                return this;
            }

            if (this.requirements == null) {
                this.requirements = requirements;
            } else {
                this.requirements.getRequirement().addAll(requirements.getRequirement());
            }
            return this;
        }

        public Builder addRequirements(List<XTRequirement> requirements) {
            if (requirements == null) {
                return this;
            }

            XTNodeTemplate.Requirements tmp = new XTNodeTemplate.Requirements();
            tmp.getRequirement().addAll(requirements);
            return addRequirements(tmp);
        }

        public Builder addRequirements(XTRequirement requirements) {
            if (requirements == null) {
                return this;
            }

            XTNodeTemplate.Requirements tmp = new XTNodeTemplate.Requirements();
            tmp.getRequirement().add(requirements);
            return addRequirements(tmp);
        }

        public Builder addCapabilities(XTNodeTemplate.Capabilities capabilities) {
            if (capabilities == null || capabilities.getCapability().isEmpty()) {
                return this;
            }

            if (this.capabilities == null) {
                this.capabilities = capabilities;
            } else {
                this.capabilities.getCapability().addAll(capabilities.getCapability());
            }
            return this;
        }

        public Builder addCapabilities(List<XTCapability> capabilities) {
            if (capabilities == null) {
                return this;
            }

            XTNodeTemplate.Capabilities tmp = new XTNodeTemplate.Capabilities();
            tmp.getCapability().addAll(capabilities);
            return addCapabilities(tmp);
        }

        public Builder addCapabilities(XTCapability capabilities) {
            if (capabilities == null) {
                return this;
            }

            XTNodeTemplate.Capabilities tmp = new XTNodeTemplate.Capabilities();
            tmp.getCapability().add(capabilities);
            return addCapabilities(tmp);
        }

        public Builder addPolicies(XTPolicies policies) {
            if (policies == null) {
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
            return addPolicies(tmp);
        }

        public Builder addPolicies(XTPolicy policies) {
            if (policies == null) {
                return this;
            }

            XTPolicies tmp = new XTPolicies();
            tmp.getPolicy().add(policies);
            return addPolicies(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public XTNodeTemplate build() {
            return new XTNodeTemplate(this);
        }
    }
}
