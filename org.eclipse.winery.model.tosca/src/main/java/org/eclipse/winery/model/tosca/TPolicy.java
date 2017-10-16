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
import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;


/**
 * <p>Java class for tPolicy complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tPolicy">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="policyType" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="policyRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicy")
public class TPolicy extends TExtensibleElements {
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "policyType", required = true)
    protected QName policyType;
    @XmlAttribute(name = "policyRef")
    protected QName policyRef;

    public TPolicy() {
    }

    public TPolicy(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.policyType = builder.policyType;
        this.policyRef = builder.policyRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPolicy)) return false;
        if (!super.equals(o)) return false;
        TPolicy tPolicy = (TPolicy) o;
        return Objects.equals(name, tPolicy.name) &&
                Objects.equals(policyType, tPolicy.policyType) &&
                Objects.equals(policyRef, tPolicy.policyRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, policyType, policyRef);
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
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
     * Gets the value of the policyType property.
     *
     * @return possible object is {@link QName }
     */
    @NonNull
    public QName getPolicyType() {
        return policyType;
    }

    /**
     * Sets the value of the policyType property.
     *
     * @param value allowed object is {@link QName }
     */
    public void setPolicyType(QName value) {
        this.policyType = value;
    }

    /**
     * Gets the value of the policyRef property.
     *
     * @return possible object is {@link QName }
     */
    @Nullable
    public QName getPolicyRef() {
        return policyRef;
    }

    /**
     * Sets the value of the policyRef property.
     *
     * @param value allowed object is {@link QName }
     */
    public void setPolicyRef(QName value) {
        this.policyRef = value;
    }

    public static class Builder extends TExtensibleElements.Builder<Builder> {
        private final QName policyType;
        private String name;
        private QName policyRef;

        public Builder(QName policyType) {
            this.policyType = policyType;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPolicyRef(QName policyRef) {
            this.policyRef = policyRef;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TPolicy build() {
            return new TPolicy(this);
        }
    }
}
