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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.Nullable;


/**
 * <p>Java class for tPolicyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tPolicyType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tEntityType">
 *       &lt;sequence>
 *         &lt;element name="AppliesTo" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tAppliesTo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="policyLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicyType", propOrder = {
        "appliesTo"
})
public class TPolicyType extends TEntityType {
    @XmlElement(name = "AppliesTo")
    protected TAppliesTo appliesTo;
    @XmlAttribute(name = "policyLanguage")
    @XmlSchemaType(name = "anyURI")
    protected String policyLanguage;

    public TPolicyType() {

    }

    public TPolicyType(Builder builder) {
        super(builder);
        this.appliesTo = builder.appliesTo;
        this.policyLanguage = builder.policyLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPolicyType)) return false;
        if (!super.equals(o)) return false;
        TPolicyType that = (TPolicyType) o;
        return Objects.equals(appliesTo, that.appliesTo) &&
                Objects.equals(policyLanguage, that.policyLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), appliesTo, policyLanguage);
    }

    /**
     * Gets the value of the appliesTo property.
     *
     * @return possible object is {@link TAppliesTo }
     */
    @Nullable
    public TAppliesTo getAppliesTo() {
        return appliesTo;
    }

    /**
     * Sets the value of the appliesTo property.
     *
     * @param value allowed object is {@link TAppliesTo }
     */
    public void setAppliesTo(TAppliesTo value) {
        this.appliesTo = value;
    }

    /**
     * Gets the value of the policyLanguage property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getPolicyLanguage() {
        return policyLanguage;
    }

    /**
     * Sets the value of the policyLanguage property.
     *
     * @param value allowed object is {@link String }
     */
    public void setPolicyLanguage(String value) {
        this.policyLanguage = value;
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        private TAppliesTo appliesTo;
        private String policyLanguage;

        public Builder(String name) {
            super(name);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setAppliesTo(TAppliesTo appliesTo) {
            this.appliesTo = appliesTo;
            return this;
        }

        public Builder setPolicyLanguage(String policyLanguage) {
            this.policyLanguage = policyLanguage;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TPolicyType build() {
            return new TPolicyType(this);
        }
    }
}
