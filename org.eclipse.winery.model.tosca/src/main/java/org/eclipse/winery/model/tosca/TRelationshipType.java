/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial code generation using vhudson-jaxb-ri-2.1-2
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tRelationshipType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tRelationshipType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityType">
 *       &lt;sequence>
 *         &lt;element name="InstanceStates" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tTopologyElementInstanceStates" minOccurs="0"/>
 *         &lt;element name="SourceInterfaces" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TargetInterfaces" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ValidSource" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ValidTarget" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRelationshipType", propOrder = {
    "instanceStates",
    "sourceInterfaces",
    "targetInterfaces",
    "validSource",
    "validTarget"
})
public class TRelationshipType
    extends TEntityType
{

    @XmlElement(name = "InstanceStates")
    protected TTopologyElementInstanceStates instanceStates;
    @XmlElement(name = "SourceInterfaces")
    protected TRelationshipType.SourceInterfaces sourceInterfaces;
    @XmlElement(name = "TargetInterfaces")
    protected TRelationshipType.TargetInterfaces targetInterfaces;
    @XmlElement(name = "ValidSource")
    protected TRelationshipType.ValidSource validSource;
    @XmlElement(name = "ValidTarget")
    protected TRelationshipType.ValidTarget validTarget;

    /**
     * Gets the value of the instanceStates property.
     * 
     * @return
     *     possible object is
     *     {@link TTopologyElementInstanceStates }
     *     
     */
    public TTopologyElementInstanceStates getInstanceStates() {
        return instanceStates;
    }

    /**
     * Sets the value of the instanceStates property.
     * 
     * @param value
     *     allowed object is
     *     {@link TTopologyElementInstanceStates }
     *     
     */
    public void setInstanceStates(TTopologyElementInstanceStates value) {
        this.instanceStates = value;
    }

    /**
     * Gets the value of the sourceInterfaces property.
     * 
     * @return
     *     possible object is
     *     {@link TRelationshipType.SourceInterfaces }
     *     
     */
    public TRelationshipType.SourceInterfaces getSourceInterfaces() {
        return sourceInterfaces;
    }

    /**
     * Sets the value of the sourceInterfaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRelationshipType.SourceInterfaces }
     *     
     */
    public void setSourceInterfaces(TRelationshipType.SourceInterfaces value) {
        this.sourceInterfaces = value;
    }

    /**
     * Gets the value of the targetInterfaces property.
     * 
     * @return
     *     possible object is
     *     {@link TRelationshipType.TargetInterfaces }
     *     
     */
    public TRelationshipType.TargetInterfaces getTargetInterfaces() {
        return targetInterfaces;
    }

    /**
     * Sets the value of the targetInterfaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRelationshipType.TargetInterfaces }
     *     
     */
    public void setTargetInterfaces(TRelationshipType.TargetInterfaces value) {
        this.targetInterfaces = value;
    }

    /**
     * Gets the value of the validSource property.
     * 
     * @return
     *     possible object is
     *     {@link TRelationshipType.ValidSource }
     *     
     */
    public TRelationshipType.ValidSource getValidSource() {
        return validSource;
    }

    /**
     * Sets the value of the validSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRelationshipType.ValidSource }
     *     
     */
    public void setValidSource(TRelationshipType.ValidSource value) {
        this.validSource = value;
    }

    /**
     * Gets the value of the validTarget property.
     * 
     * @return
     *     possible object is
     *     {@link TRelationshipType.ValidTarget }
     *     
     */
    public TRelationshipType.ValidTarget getValidTarget() {
        return validTarget;
    }

    /**
     * Sets the value of the validTarget property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRelationshipType.ValidTarget }
     *     
     */
    public void setValidTarget(TRelationshipType.ValidTarget value) {
        this.validTarget = value;
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
     *         &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "_interface"
    })
    public static class SourceInterfaces {

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
         * 
         * 
         */
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
     *         &lt;element name="Interface" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tInterface" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "_interface"
    })
    public static class TargetInterfaces {

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
         * 
         * 
         */
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
     *       &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ValidSource {

        @XmlAttribute(name = "typeRef", required = true)
        protected QName typeRef;

        /**
         * Gets the value of the typeRef property.
         * 
         * @return
         *     possible object is
         *     {@link QName }
         *     
         */
        public QName getTypeRef() {
            return typeRef;
        }

        /**
         * Sets the value of the typeRef property.
         * 
         * @param value
         *     allowed object is
         *     {@link QName }
         *     
         */
        public void setTypeRef(QName value) {
            this.typeRef = value;
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
     *       &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ValidTarget {

        @XmlAttribute(name = "typeRef", required = true)
        protected QName typeRef;

        /**
         * Gets the value of the typeRef property.
         * 
         * @return
         *     possible object is
         *     {@link QName }
         *     
         */
        public QName getTypeRef() {
            return typeRef;
        }

        /**
         * Sets the value of the typeRef property.
         * 
         * @param value
         *     allowed object is
         *     {@link QName }
         *     
         */
        public void setTypeRef(QName value) {
            this.typeRef = value;
        }

    }

}
