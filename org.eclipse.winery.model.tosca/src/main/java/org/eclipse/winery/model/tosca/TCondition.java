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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;
import org.w3c.dom.Element;


/**
 * <p>Java class for tCondition complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tCondition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="expressionLanguage" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCondition", propOrder = {
        "any"
})
public class TCondition {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "expressionLanguage", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String expressionLanguage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TCondition)) return false;
        TCondition that = (TCondition) o;
        return Objects.equals(any, that.any) &&
                Objects.equals(expressionLanguage, that.expressionLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(any, expressionLanguage);
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     */
    @NonNull
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the expressionLanguage property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    /**
     * Sets the value of the expressionLanguage property.
     *
     * @param value allowed object is {@link String }
     */
    public void setExpressionLanguage(String value) {
        this.expressionLanguage = value;
    }
}
