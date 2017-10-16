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
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;


/**
 * <p>Java class for tParameter complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tParameter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="required" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tBoolean" default="yes" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tParameter")
public class TParameter {
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "required")
    protected TBoolean required;

    public TParameter() {
    }

    public TParameter(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.required = builder.required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TParameter)) return false;
        TParameter that = (TParameter) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                required == that.required;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, required);
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
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
     * Gets the value of the type property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value allowed object is {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the required property.
     *
     * @return possible object is {@link TBoolean }
     */
    @NonNull
    public TBoolean getRequired() {
        if (required == null) {
            return TBoolean.YES;
        } else {
            return required;
        }
    }

    /**
     * Sets the value of the required property.
     *
     * @param value allowed object is {@link TBoolean }
     */
    public void setRequired(TBoolean value) {
        this.required = value;
    }

    public static class Builder {
        private final String name;
        private final String type;
        private final TBoolean required;

        public Builder(String name, String type, TBoolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }

        public Builder(String name, String type, Boolean required) {
            this(name, type, required == null ? TBoolean.YES : required ? TBoolean.YES : TBoolean.NO);
        }

        public TParameter build() {
            return new TParameter(this);
        }
    }
}
