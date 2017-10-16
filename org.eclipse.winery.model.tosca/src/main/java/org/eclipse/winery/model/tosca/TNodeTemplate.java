/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial code generation using vhudson-jaxb-ri-2.1-2
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;


/**
 * <p>Java class for tNodeTemplate complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tNodeTemplate">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityTemplate">
 *       &lt;sequence>
 *         &lt;element name="Requirements" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Requirement" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirement"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Capabilities" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Capability" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapability"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Policies" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Policy" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPolicy"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DeploymentArtifacts" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tDeploymentArtifacts"
 * minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="minInstances" type="{http://www.w3.org/2001/XMLSchema}int" default="1" />
 *       &lt;attribute name="maxInstances" default="1">
 *         &lt;simpleType>
 *           &lt;union>
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *                 &lt;pattern value="([1-9]+[0-9]*)"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;enumeration value="unbounded"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/union>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
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

    /**
     * Gets the value of the requirements property.
     *
     * @return possible object is {@link TNodeTemplate.Requirements }
     */
    public TNodeTemplate.Requirements getRequirements() {
        return requirements;
    }

    /**
     * Sets the value of the requirements property.
     *
     * @param value allowed object is {@link TNodeTemplate.Requirements }
     */
    public void setRequirements(TNodeTemplate.Requirements value) {
        this.requirements = value;
    }

    /**
     * Gets the value of the capabilities property.
     *
     * @return possible object is {@link TNodeTemplate.Capabilities }
     */
    /*@Nullable*/
    public TNodeTemplate.Capabilities getCapabilities() {
        return capabilities;
    }

    /**
     * Sets the value of the capabilities property.
     *
     * @param value allowed object is {@link TNodeTemplate.Capabilities }
     */
    public void setCapabilities(TNodeTemplate.Capabilities value) {
        this.capabilities = value;
    }

    /**
     * Gets the value of the policies property.
     *
     * @return possible object is {@link TNodeTemplate.Policies }
     */
    /*@Nullable*/
    public TNodeTemplate.Policies getPolicies() {
        return policies;
    }

    /**
     * Sets the value of the policies property.
     *
     * @param value allowed object is {@link TNodeTemplate.Policies }
     */
    public void setPolicies(TNodeTemplate.Policies value) {
        this.policies = value;
    }

    /**
     * Gets the value of the deploymentArtifacts property.
     *
     * @return possible object is {@link TDeploymentArtifacts }
     */
    @Nullable
    public TDeploymentArtifacts getDeploymentArtifacts() {
        return deploymentArtifacts;
    }

    /**
     * Sets the value of the deploymentArtifacts property.
     *
     * @param value allowed object is {@link TDeploymentArtifacts }
     */
    public void setDeploymentArtifacts(TDeploymentArtifacts value) {
        this.deploymentArtifacts = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the minInstances property.
     *
     * @return possible object is {@link Integer }
     */
    @NonNull
    public int getMinInstances() {
        if (minInstances == null) {
            return 1;
        } else {
            return minInstances;
        }
    }

    /**
     * Sets the value of the minInstances property.
     *
     * @param value allowed object is {@link Integer }
     */
    public void setMinInstances(Integer value) {
        this.minInstances = value;
    }

    /**
     * Gets the value of the maxInstances property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getMaxInstances() {
        if (maxInstances == null) {
            return "1";
        } else {
            return maxInstances;
        }
    }

    /**
     * Sets the value of the maxInstances property.
     *
     * @param value allowed object is {@link String }
     */
    public void setMaxInstances(String value) {
        this.maxInstances = value;
    }

    @XmlTransient
    public String getX() {
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        return otherNodeTemplateAttributes.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "x"));
    }
    
    /**
     * Sets the left coordinate of a {@link TNodeTemplate}.
     *
     * @param x   the value of the x-coordinate to be set
     */
    public void setX(String x) {
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        otherNodeTemplateAttributes.put(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "x"), x);
    }

    @XmlTransient
    public String getY() {
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        return otherNodeTemplateAttributes.get(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "y"));
    }

    /**
     * Sets the top coordinate of a {@link TNodeTemplate}.
     *
     * @param y   the value of the coordinate to be set
     */
    public void setY(String y) {
        Map<QName, String> otherNodeTemplateAttributes = this.getOtherAttributes();
        otherNodeTemplateAttributes.put(new QName(Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE, "y"), y);
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Capability" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapability"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "capability"
    })
    public static class Capabilities {

        @XmlElement(name = "Capability", required = true)
        protected List<TCapability> capability;

        /**
         * Gets the value of the capability property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the capability property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCapability().add(newItem);
         * </pre>
         *
         *
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
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Policy" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPolicy"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "policy"
    })
    public static class Policies {

        @XmlElement(name = "Policy", required = true)
        protected List<TPolicy> policy;

        /**
         * Gets the value of the policy property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the policy property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPolicy().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TPolicy }
         */
        @NonNull
        public List<TPolicy> getPolicy() {
            if (policy == null) {
                policy = new ArrayList<TPolicy>();
            }
            return this.policy;
        }
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Requirement" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirement"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "requirement"
    })
    public static class Requirements {

        @XmlElement(name = "Requirement", required = true)
        protected List<TRequirement> requirement;

        /**
         * Gets the value of the requirement property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the requirement property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRequirement().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TRequirement }
         */
        @NonNull
        public List<TRequirement> getRequirement() {
            if (requirement == null) {
                requirement = new ArrayList<TRequirement>();
            }
            return this.requirement;
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
