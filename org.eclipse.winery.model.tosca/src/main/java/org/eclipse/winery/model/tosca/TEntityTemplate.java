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
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;


/**
 * Java class for tEntityTemplate complex type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEntityTemplate", propOrder = {
        "properties",
        "propertyConstraints"
})
@XmlSeeAlso({
        TArtifactTemplate.class,
        TPolicyTemplate.class,
        TCapability.class,
        TRequirement.class,
        TRelationshipTemplate.class,
        TNodeTemplate.class
})
public abstract class TEntityTemplate extends HasId {

    @XmlElement(name = "Properties")
    protected TEntityTemplate.Properties properties;

    @XmlElement(name = "PropertyConstraints")
    protected TEntityTemplate.PropertyConstraints propertyConstraints;

    @XmlAttribute(name = "type", required = true)
    protected QName type;

    public TEntityTemplate() {
        super();
    }

    public TEntityTemplate(String id) {
        super(id);
    }

    /**
     * Gets the value of the properties property.
     *
     * @return possible object is {@link TEntityTemplate.Properties }
     */
    public TEntityTemplate.Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     *
     * @param value allowed object is {@link TEntityTemplate.Properties }
     */
    public void setProperties(TEntityTemplate.Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the propertyConstraints property.
     *
     * @return possible object is {@link TEntityTemplate.PropertyConstraints }
     */
    public TEntityTemplate.PropertyConstraints getPropertyConstraints() {
        return propertyConstraints;
    }

    /**
     * Sets the value of the propertyConstraints property.
     *
     * @param value allowed object is {@link TEntityTemplate.PropertyConstraints }
     */
    public void setPropertyConstraints(TEntityTemplate.PropertyConstraints value) {
        this.propertyConstraints = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return possible object is {@link QName }
     */
    public QName getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value allowed object is {@link QName }
     */
    public void setType(QName value) {
        this.type = value;
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
     *         &lt;any processContents='lax' namespace='##other'/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "any"
    })
    public static class Properties {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Gets the value of the any property.
         *
         * @return possible object is {@link Element } {@link Object }
         */
        public Object getAny() {
            return any;
        }

        /**
         * Sets the value of the any property.
         *
         * @param value allowed object is {@link Element } {@link Object }
         */
        public void setAny(Object value) {
            this.any = value;
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
        public List<TPropertyConstraint> getPropertyConstraint() {
            if (propertyConstraint == null) {
                propertyConstraint = new ArrayList<TPropertyConstraint>();
            }
            return this.propertyConstraint;
        }
    }
}
