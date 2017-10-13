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
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;


/**
 * <p>Java class for tBoundaryDefinitions complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tBoundaryDefinitions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Properties" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any namespace='##other'/>
 *                   &lt;element name="PropertyMappings" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="PropertyMapping" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyMapping"
 * maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PropertyConstraints" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PropertyConstraint" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyConstraint"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Requirements" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Requirement" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirementRef"
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
 *                   &lt;element name="Capability" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapabilityRef"
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
 *         &lt;element name="Interfaces" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tExportedInterface"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tBoundaryDefinitions", propOrder = {
        "properties",
        "propertyConstraints",
        "requirements",
        "capabilities",
        "policies",
        "interfaces"
})
public class TBoundaryDefinitions {
    @XmlElement(name = "Properties")
    protected TBoundaryDefinitions.Properties properties;
    @XmlElement(name = "PropertyConstraints")
    protected TBoundaryDefinitions.PropertyConstraints propertyConstraints;
    @XmlElement(name = "Requirements")
    protected TBoundaryDefinitions.Requirements requirements;
    @XmlElement(name = "Capabilities")
    protected TBoundaryDefinitions.Capabilities capabilities;
    @XmlElement(name = "Policies")
    protected TBoundaryDefinitions.Policies policies;
    @XmlElement(name = "Interfaces")
    protected TBoundaryDefinitions.Interfaces interfaces;

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

    /**
     * Gets the value of the properties property.
     *
     * @return possible object is {@link TBoundaryDefinitions.Properties }
     */
    /*@Nullable*/
    public TBoundaryDefinitions.Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     *
     * @param value allowed object is {@link TBoundaryDefinitions.Properties }
     */
    public void setProperties(TBoundaryDefinitions.Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the propertyConstraints property.
     *
     * @return possible object is {@link TBoundaryDefinitions.PropertyConstraints }
     */
    /*@Nullable*/
    public TBoundaryDefinitions.PropertyConstraints getPropertyConstraints() {
        return propertyConstraints;
    }

    /**
     * Sets the value of the propertyConstraints property.
     *
     * @param value allowed object is {@link TBoundaryDefinitions.PropertyConstraints }
     */
    public void setPropertyConstraints(TBoundaryDefinitions.PropertyConstraints value) {
        this.propertyConstraints = value;
    }

    /**
     * Gets the value of the requirements property.
     *
     * @return possible object is {@link TBoundaryDefinitions.Requirements }
     */
    /*@Nullable*/
    public TBoundaryDefinitions.Requirements getRequirements() {
        return requirements;
    }

    /**
     * Sets the value of the requirements property.
     *
     * @param value allowed object is {@link TBoundaryDefinitions.Requirements }
     */
    public void setRequirements(TBoundaryDefinitions.Requirements value) {
        this.requirements = value;
    }

    /**
     * Gets the value of the capabilities property.
     *
     * @return possible object is {@link TBoundaryDefinitions.Capabilities }
     */
    /*@Nullable*/
    public TBoundaryDefinitions.Capabilities getCapabilities() {
        return capabilities;
    }

    /**
     * Sets the value of the capabilities property.
     *
     * @param value allowed object is {@link TBoundaryDefinitions.Capabilities }
     */
    public void setCapabilities(TBoundaryDefinitions.Capabilities value) {
        this.capabilities = value;
    }

    /**
     * Gets the value of the policies property.
     *
     * @return possible object is {@link TBoundaryDefinitions.Policies }
     */
    /*@Nullable*/
    public TBoundaryDefinitions.Policies getPolicies() {
        return policies;
    }

    /**
     * Sets the value of the policies property.
     *
     * @param value allowed object is {@link TBoundaryDefinitions.Policies }
     */
    public void setPolicies(TBoundaryDefinitions.Policies value) {
        this.policies = value;
    }

    /**
     * Gets the value of the interfaces property.
     *
     * @return possible object is {@link TBoundaryDefinitions.Interfaces }
     */
    /*@Nullable*/
    public TBoundaryDefinitions.Interfaces getInterfaces() {
        return interfaces;
    }

    /**
     * Sets the value of the interfaces property.
     *
     * @param value allowed object is {@link TBoundaryDefinitions.Interfaces }
     */
    public void setInterfaces(TBoundaryDefinitions.Interfaces value) {
        this.interfaces = value;
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
     *         &lt;element name="Capability" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapabilityRef"
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
        protected List<TCapabilityRef> capability;

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
         * {@link TCapabilityRef }
         */
        @NonNull
        public List<TCapabilityRef> getCapability() {
            if (capability == null) {
                capability = new ArrayList<TCapabilityRef>();
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
     *         &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tExportedInterface"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "_interface"
    })
    public static class Interfaces {

        @XmlElement(name = "Interface", required = true)
        protected List<TExportedInterface> _interface;

        /**
         * Gets the value of the interface property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the interface property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInterface().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TExportedInterface }
         */
        @NonNull
        public List<TExportedInterface> getInterface() {
            if (_interface == null) {
                _interface = new ArrayList<TExportedInterface>();
            }
            return this._interface;
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
     *         &lt;any namespace='##other'/>
     *         &lt;element name="PropertyMappings" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="PropertyMapping" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyMapping"
     * maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "any",
            "propertyMappings"
    })
    public static class Properties {

        @XmlAnyElement(lax = true)
        protected Object any;
        @XmlElement(name = "PropertyMappings")
        protected TBoundaryDefinitions.Properties.PropertyMappings propertyMappings;

        /**
         * Gets the value of the any property.
         *
         * @return possible object is {@link Object }
         */
        @Nullable
        public Object getAny() {
            return any;
        }

        /**
         * Sets the value of the any property.
         *
         * @param value allowed object is {@link Object }
         */
        public void setAny(Object value) {
            this.any = value;
        }

        /**
         * Gets the value of the propertyMappings property.
         *
         * @return possible object is {@link TBoundaryDefinitions.Properties.PropertyMappings }
         */
        /*@Nullable*/
        public TBoundaryDefinitions.Properties.PropertyMappings getPropertyMappings() {
            return propertyMappings;
        }

        /**
         * Sets the value of the propertyMappings property.
         *
         * @param value allowed object is {@link TBoundaryDefinitions.Properties.PropertyMappings }
         */
        public void setPropertyMappings(TBoundaryDefinitions.Properties.PropertyMappings value) {
            this.propertyMappings = value;
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
         *         &lt;element name="PropertyMapping" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyMapping"
         * maxOccurs="unbounded"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "propertyMapping"
        })
        public static class PropertyMappings {

            @XmlElement(name = "PropertyMapping", required = true)
            protected List<TPropertyMapping> propertyMapping;

            /**
             * Gets the value of the propertyMapping property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the propertyMapping property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPropertyMapping().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link TPropertyMapping }
             */
            /*@Nullable*/
            public List<TPropertyMapping> getPropertyMapping() {
                if (propertyMapping == null) {
                    propertyMapping = new ArrayList<TPropertyMapping>();
                }
                return this.propertyMapping;
            }
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
     *         &lt;element name="PropertyConstraint" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyConstraint"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "propertyConstraint"
    })
    public static class PropertyConstraints {

        @XmlElement(name = "PropertyConstraint", required = true)
        protected List<TPropertyConstraint> propertyConstraint;

        /**
         * Gets the value of the propertyConstraint property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the propertyConstraint property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPropertyConstraint().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TPropertyConstraint }
         */
        @NonNull
        public List<TPropertyConstraint> getPropertyConstraint() {
            if (propertyConstraint == null) {
                propertyConstraint = new ArrayList<TPropertyConstraint>();
            }
            return this.propertyConstraint;
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
     *         &lt;element name="Requirement" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirementRef"
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
        protected List<TRequirementRef> requirement;

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
         * {@link TRequirementRef }
         */
        @NonNull
        public List<TRequirementRef> getRequirement() {
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
        private Policies policies;
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

        public Builder setPolicies(Policies policies) {
            this.policies = policies;
            return this;
        }

        public Builder setInterfaces(Interfaces interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder addPolicies(TBoundaryDefinitions.Policies policies) {
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

            TBoundaryDefinitions.Policies tmp = new TBoundaryDefinitions.Policies();
            tmp.getPolicy().addAll(policies);
            return this.addPolicies(tmp);
        }

        public Builder addPolicies(TPolicy policies) {
            if (policies == null) {
                return this;
            }

            TBoundaryDefinitions.Policies tmp = new TBoundaryDefinitions.Policies();
            tmp.getPolicy().add(policies);
            return this.addPolicies(tmp);
        }

        public TBoundaryDefinitions build() {
            return new TBoundaryDefinitions(this);
        }
    }
}
