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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tRequirementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tRequirementType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityType">
 *       &lt;attribute name="requiredCapabilityType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRequirementType")
public class TRequirementType
    extends TEntityType
{

    @XmlAttribute(name = "requiredCapabilityType")
    protected QName requiredCapabilityType;

    /**
     * Gets the value of the requiredCapabilityType property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getRequiredCapabilityType() {
        return requiredCapabilityType;
    }

    /**
     * Sets the value of the requiredCapabilityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setRequiredCapabilityType(QName value) {
        this.requiredCapabilityType = value;
    }

}
