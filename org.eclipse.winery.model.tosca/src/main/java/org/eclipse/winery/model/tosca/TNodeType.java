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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;


/**
 * <p>Java class for tNodeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tNodeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityType">
 *       &lt;sequence>
 *         &lt;element name="RequirementDefinitions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="RequirementDefinition" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirementDefinition"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CapabilityDefinitions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CapabilityDefinition" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapabilityDefinition"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="InstanceStates" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tTopologyElementInstanceStates"
 * minOccurs="0"/>
 *         &lt;element name="Interfaces" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNodeType", propOrder = {
    "requirementDefinitions",
    "capabilityDefinitions",
    "instanceStates",
    "interfaces"
})
public class TNodeType extends TEntityType {
    @XmlElement(name = "RequirementDefinitions")
    protected TNodeType.RequirementDefinitions requirementDefinitions;
    @XmlElement(name = "CapabilityDefinitions")
    protected TNodeType.CapabilityDefinitions capabilityDefinitions;
    @XmlElement(name = "InstanceStates")
    protected TTopologyElementInstanceStates instanceStates;
    @XmlElement(name = "Interfaces")
    protected TNodeType.Interfaces interfaces;

    public TNodeType() {
    }

    public TNodeType(Builder builder) {
        super(builder);
        this.requirementDefinitions = builder.requirementDefinitions;
        this.capabilityDefinitions = builder.capabilityDefinitions;
        this.instanceStates = builder.instanceStates;
        this.interfaces = builder.interfaces;
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
            Objects.equals(interfaces, tNodeType.interfaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requirementDefinitions, capabilityDefinitions, instanceStates, interfaces);
    }

    /**
     * Gets the value of the requirementDefinitions property.
     *
     * @return possible object is {@link TNodeType.RequirementDefinitions }
     */
    /*@Nullable*/
    public TNodeType.RequirementDefinitions getRequirementDefinitions() {
        return requirementDefinitions;
    }

    /**
     * Sets the value of the requirementDefinitions property.
     *
     * @param value allowed object is {@link TNodeType.RequirementDefinitions }
     */
    public void setRequirementDefinitions(TNodeType.RequirementDefinitions value) {
        this.requirementDefinitions = value;
    }

    /**
     * Gets the value of the capabilityDefinitions property.
     *
     * @return possible object is {@link TNodeType.CapabilityDefinitions }
     */
    /*@Nullable*/
    public TNodeType.CapabilityDefinitions getCapabilityDefinitions() {
        return capabilityDefinitions;
    }

    /**
     * Sets the value of the capabilityDefinitions property.
     *
     * @param value allowed object is {@link TNodeType.CapabilityDefinitions }
     */
    public void setCapabilityDefinitions(TNodeType.CapabilityDefinitions value) {
        this.capabilityDefinitions = value;
    }

    /**
     * Gets the value of the instanceStates property.
     *
     * @return possible object is {@link TTopologyElementInstanceStates }
     */
    /*@Nullable*/
    public TTopologyElementInstanceStates getInstanceStates() {
        return instanceStates;
    }

    /**
     * Sets the value of the instanceStates property.
     *
     * @param value allowed object is {@link TTopologyElementInstanceStates }
     */
    public void setInstanceStates(TTopologyElementInstanceStates value) {
        this.instanceStates = value;
    }

    /**
     * Gets the value of the interfaces property.
     *
     * @return possible object is {@link TNodeType.Interfaces }
     */
    /*@Nullable*/
    public TNodeType.Interfaces getInterfaces() {
        return interfaces;
    }

    /**
     * Sets the value of the interfaces property.
     *
     * @param value allowed object is {@link TNodeType.Interfaces }
     */
    public void setInterfaces(TNodeType.Interfaces value) {
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
     *         &lt;element name="CapabilityDefinition" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tCapabilityDefinition"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "capabilityDefinition"
    })
    public static class CapabilityDefinitions {

        @XmlElement(name = "CapabilityDefinition", required = true)
        protected List<TCapabilityDefinition> capabilityDefinition;

        /**
         * Gets the value of the capabilityDefinition property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the capabilityDefinition property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCapabilityDefinition().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TCapabilityDefinition }
         */
        @NonNull
        public List<TCapabilityDefinition> getCapabilityDefinition() {
            if (capabilityDefinition == null) {
                capabilityDefinition = new ArrayList<TCapabilityDefinition>();
            }
            return this.capabilityDefinition;
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
     *         &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface"
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
        protected List<TInterface> _interface;

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
         * {@link TInterface }
         */
        @NonNull
        public List<TInterface> getInterface() {
            if (_interface == null) {
                _interface = new ArrayList<TInterface>();
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
     *         &lt;element name="RequirementDefinition" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequirementDefinition"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "requirementDefinition"
    })
    public static class RequirementDefinitions {

        @XmlElement(name = "RequirementDefinition", required = true)
        protected List<TRequirementDefinition> requirementDefinition;

        /**
         * Gets the value of the requirementDefinition property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the requirementDefinition property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRequirementDefinition().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TRequirementDefinition }
         */
        @NonNull
        public List<TRequirementDefinition> getRequirementDefinition() {
            if (requirementDefinition == null) {
                requirementDefinition = new ArrayList<TRequirementDefinition>();
            }
            return this.requirementDefinition;
        }
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        private RequirementDefinitions requirementDefinitions;
        private CapabilityDefinitions capabilityDefinitions;
        private TTopologyElementInstanceStates instanceStates;
        private Interfaces interfaces;

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

        public Builder setInterfaces(TNodeType.Interfaces interfaces) {
            this.interfaces = interfaces;
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

        public Builder addInterfaces(TNodeType.Interfaces interfaces) {
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

            TNodeType.Interfaces tmp = new TNodeType.Interfaces();
            tmp.getInterface().addAll(interfaces);
            return addInterfaces(tmp);
        }

        public Builder addInterfaces(TInterface interfaces) {
            if (interfaces == null) {
                return this;
            }

            TNodeType.Interfaces tmp = new TNodeType.Interfaces();
            tmp.getInterface().add(interfaces);
            return addInterfaces(tmp);
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
