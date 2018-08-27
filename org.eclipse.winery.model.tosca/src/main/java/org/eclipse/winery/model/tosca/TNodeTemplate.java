/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
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
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNodeTemplate", propOrder = {
    "requirements",
    "capabilities",
    "policies",
    "deploymentArtifacts"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
    defaultImpl = TNodeTemplate.class,
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "fakeJacksonType")
public class TNodeTemplate extends RelationshipSourceOrTarget {
    @XmlElement(name = "Requirements")
    protected TNodeTemplate.Requirements requirements;
    @XmlElement(name = "Capabilities")
    protected TNodeTemplate.Capabilities capabilities;
    @XmlElement(name = "Policies")
    protected TNodeTemplate.Policies policies;
    @XmlElement(name = "DeploymentArtifacts")
    protected TDeploymentArtifacts deploymentArtifacts;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "minInstances")
    protected Integer minInstances;
    @XmlAttribute(name = "maxInstances")
    protected String maxInstances;

    public TNodeTemplate() {
        super();
    }

    public TNodeTemplate(String id) {
        super(id);
    }

    public TNodeTemplate(Builder builder) {
        super(builder);
        this.requirements = builder.requirements;
        this.capabilities = builder.capabilities;
        this.policies = builder.policies;
        this.deploymentArtifacts = builder.deploymentArtifacts;
        this.name = builder.name;
        this.minInstances = builder.minInstances;
        this.maxInstances = builder.maxInstances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TNodeTemplate)) return false;
        if (!super.equals(o)) return false;
        TNodeTemplate that = (TNodeTemplate) o;
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

    public TNodeTemplate.@Nullable Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(TNodeTemplate.@Nullable Requirements value) {
        this.requirements = value;
    }

    public TNodeTemplate.@Nullable Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(TNodeTemplate.@Nullable Capabilities value) {
        this.capabilities = value;
    }

    public TNodeTemplate.@Nullable Policies getPolicies() {
        return policies;
    }

    public void setPolicies(TNodeTemplate.@Nullable Policies value) {
        this.policies = value;
    }

    @Nullable
    public TDeploymentArtifacts getDeploymentArtifacts() {
        return deploymentArtifacts;
    }

    public void setDeploymentArtifacts(@Nullable TDeploymentArtifacts value) {
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
     * In the JSON, also output this direct child of the node template object.
     * Therefore, no JsonIgnore annotation.
     */
    @XmlTransient
    @Nullable
    public String getX() {
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        return otherNodeTemplateAttributes.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "x"));
    }

    /**
     * Sets the top coordinate of a {@link TNodeTemplate}. When receiving the JSON, this method ensures that (i) the "y" property can be handled and (ii) the Y coordinate is written correctly in the extension namespace.
     *
     * @param x the value of the x-coordinate to be set
     */
    public void setX(@NonNull String x) {
        Objects.requireNonNull(x);
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        otherNodeTemplateAttributes.put(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "x"), x);
    }

    /**
     * In the JSON, also output this direct child of the node template object.
     * Therefore, no JsonIgnore annotation.
     */
    @XmlTransient
    @Nullable
    public String getY() {
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        return otherNodeTemplateAttributes.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "y"));
    }

    /**
     * Sets the top coordinate of a {@link TNodeTemplate}. When receiving the JSON, this method ensures that (i) the "y" property can be handled and (ii) the Y coordinate is written correctly in the extension namespace.
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
        protected List<TCapability> capability;

        /**
         * Gets the value of the capability property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the capability property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCapability().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TCapability }
         */
        @NonNull
        public List<TCapability> getCapability() {
            if (capability == null) {
                capability = new ArrayList<TCapability>();
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
        "policy"
    })
    public static class Policies implements Serializable {

        @XmlElement(name = "Policy", required = true)
        protected List<TPolicy> policy;

        @NonNull
        public List<TPolicy> getPolicy() {
            if (policy == null) {
                policy = new ArrayList<>();
            }
            return this.policy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Policies policies = (Policies) o;
            return Objects.equals(policy, policies.policy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(policy);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "requirement"
    })
    public static class Requirements implements Serializable {

        @XmlElement(name = "Requirement", required = true)
        protected List<TRequirement> requirement;

        @NonNull
        public List<TRequirement> getRequirement() {
            if (requirement == null) {
                requirement = new ArrayList<TRequirement>();
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

    public static class Builder extends RelationshipSourceOrTarget.Builder<Builder> {
        private Requirements requirements;
        private Capabilities capabilities;
        private Policies policies;
        private TDeploymentArtifacts deploymentArtifacts;
        private String name;
        private Integer minInstances;
        private String maxInstances;

        public Builder(String id, QName type) {
            super(id, type);
        }

        public Builder(TEntityTemplate entityTemplate) {
            super(entityTemplate);
        }

        public Builder setRequirements(TNodeTemplate.Requirements requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setCapabilities(TNodeTemplate.Capabilities capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setPolicies(TNodeTemplate.Policies policies) {
            this.policies = policies;
            return this;
        }

        public Builder setDeploymentArtifacts(TDeploymentArtifacts deploymentArtifacts) {
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

        public Builder addRequirements(TNodeTemplate.Requirements requirements) {
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

        public Builder addRequirements(List<TRequirement> requirements) {
            if (requirements == null) {
                return this;
            }

            TNodeTemplate.Requirements tmp = new TNodeTemplate.Requirements();
            tmp.getRequirement().addAll(requirements);
            return addRequirements(tmp);
        }

        public Builder addRequirements(TRequirement requirements) {
            if (requirements == null) {
                return this;
            }

            TNodeTemplate.Requirements tmp = new TNodeTemplate.Requirements();
            tmp.getRequirement().add(requirements);
            return addRequirements(tmp);
        }

        public Builder addCapabilities(TNodeTemplate.Capabilities capabilities) {
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

        public Builder addCapabilities(List<TCapability> capabilities) {
            if (capabilities == null) {
                return this;
            }

            TNodeTemplate.Capabilities tmp = new TNodeTemplate.Capabilities();
            tmp.getCapability().addAll(capabilities);
            return addCapabilities(tmp);
        }

        public Builder addCapabilities(TCapability capabilities) {
            if (capabilities == null) {
                return this;
            }

            TNodeTemplate.Capabilities tmp = new TNodeTemplate.Capabilities();
            tmp.getCapability().add(capabilities);
            return addCapabilities(tmp);
        }

        public Builder addPolicies(TNodeTemplate.Policies policies) {
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

        public Builder addPolicies(List<TPolicy> policies) {
            if (policies == null) {
                return this;
            }

            TNodeTemplate.Policies tmp = new TNodeTemplate.Policies();
            tmp.getPolicy().addAll(policies);
            return addPolicies(tmp);
        }

        public Builder addPolicies(TPolicy policies) {
            if (policies == null) {
                return this;
            }

            TNodeTemplate.Policies tmp = new TNodeTemplate.Policies();
            tmp.getPolicy().add(policies);
            return addPolicies(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public TNodeTemplate build() {
            return new TNodeTemplate(this);
        }
    }
}
