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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;


/**
 * <p>Java class for tArtifactReference complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tArtifactReference">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="Include">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="pattern" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Exclude">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="pattern" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *       &lt;attribute name="reference" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tArtifactReference", propOrder = {
    "includeOrExclude"
})
public class TArtifactReference {

    @XmlElements({
        @XmlElement(name = "Exclude", type = TArtifactReference.Exclude.class),
        @XmlElement(name = "Include", type = TArtifactReference.Include.class)
    })
    protected List<Object> includeOrExclude;
    @XmlAttribute(name = "reference", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String reference;

    public TArtifactReference() {

    }

    public TArtifactReference(Builder builder) {
        this.includeOrExclude = builder.includeOrExclude;
        this.reference = builder.reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TArtifactReference)) return false;
        TArtifactReference that = (TArtifactReference) o;
        return Objects.equals(includeOrExclude, that.includeOrExclude) &&
            Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(includeOrExclude, reference);
    }

    /**
     * Gets the value of the includeOrExclude property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includeOrExclude property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludeOrExclude().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TArtifactReference.Exclude }
     * {@link TArtifactReference.Include }
     */
    @NonNull
    public List<Object> getIncludeOrExclude() {
        if (includeOrExclude == null) {
            includeOrExclude = new ArrayList<Object>();
        }
        return this.includeOrExclude;
    }

    /**
     * Gets the value of the reference property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     *
     * @param value allowed object is {@link String }
     */
    public void setReference(String value) {
        this.reference = value;
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
     *       &lt;attribute name="pattern" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Exclude {

        @XmlAttribute(name = "pattern", required = true)
        protected String pattern;

        /**
         * Gets the value of the pattern property.
         *
         * @return possible object is {@link String }
         */
        @NonNull
        public String getPattern() {
            return pattern;
        }

        /**
         * Sets the value of the pattern property.
         *
         * @param value allowed object is {@link String }
         */
        public void setPattern(String value) {
            this.pattern = value;
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
     *       &lt;attribute name="pattern" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Include {

        @XmlAttribute(name = "pattern", required = true)
        protected String pattern;

        /**
         * Gets the value of the pattern property.
         *
         * @return possible object is {@link String }
         */
        @NonNull
        public String getPattern() {
            return pattern;
        }

        /**
         * Sets the value of the pattern property.
         *
         * @param value allowed object is {@link String }
         */
        public void setPattern(String value) {
            this.pattern = value;
        }
    }

    public static class Builder {
        private final String reference;
        private List<Object> includeOrExclude;

        public Builder(String reference) {
            this.reference = reference;
        }

        public Builder setIncludeOrExclude(List<Object> includeOrExclude) {
            this.includeOrExclude = includeOrExclude;
            return this;
        }

        public TArtifactReference build() {
            return new TArtifactReference(this);
        }
    }
}
