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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;


/**
 * <p>Java class for tExtension complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tExtension">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;attribute name="namespace" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="mustUnderstand" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tBoolean" default="yes"
 * />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tExtension")
public class TExtension extends TExtensibleElements {
    @XmlAttribute(name = "namespace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String namespace;
    @XmlAttribute(name = "mustUnderstand")
    protected TBoolean mustUnderstand;

    public TExtension() {
    }

    public TExtension(Builder builder) {
        super(builder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TExtension)) return false;
        if (!super.equals(o)) return false;
        TExtension that = (TExtension) o;
        return Objects.equals(namespace, that.namespace) &&
            mustUnderstand == that.mustUnderstand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), namespace, mustUnderstand);
    }

    /**
     * Gets the value of the namespace property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the value of the namespace property.
     *
     * @param value allowed object is {@link String }
     */
    public void setNamespace(String value) {
        this.namespace = value;
    }

    /**
     * Gets the value of the mustUnderstand property.
     *
     * @return possible object is {@link TBoolean }
     */
    @NonNull
    public TBoolean getMustUnderstand() {
        if (mustUnderstand == null) {
            return TBoolean.YES;
        } else {
            return mustUnderstand;
        }
    }

    /**
     * Sets the value of the mustUnderstand property.
     *
     * @param value allowed object is {@link TBoolean }
     */
    public void setMustUnderstand(TBoolean value) {
        this.mustUnderstand = value;
    }

    public static class Builder extends TExtensibleElements.Builder<Builder> {
        private final String namespace;
        private TBoolean mustUnderstand;

        public Builder(String namespace) {
            this.namespace = namespace;
        }

        public Builder setMustUnderstand(TBoolean mustUnderstand) {
            this.mustUnderstand = mustUnderstand;
            return this;
        }

        public Builder setMustUnderstand(Boolean mustUnderstand) {
            if (mustUnderstand == null) {
                return this;
            }

            this.mustUnderstand = mustUnderstand ? TBoolean.YES : TBoolean.NO;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TExtension build() {
            return new TExtension(this);
        }
    }
}
