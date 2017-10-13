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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;


/**
 * <p>Java class for tRequirementRef complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tRequirementRef">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRequirementRef")
public class TRequirementRef {

    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "ref", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object ref;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRequirementRef)) return false;
        TRequirementRef that = (TRequirementRef) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(ref, that.ref);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ref);
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    /*@Nullable*/
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
     * Gets the value of the ref property.
     *
     * @return possible object is {@link Object }
     */
    @NonNull
    public Object getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     *
     * @param value allowed object is {@link Object }
     */
    public void setRef(Object value) {
        this.ref = value;
    }
}
