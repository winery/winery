/*******************************************************************************
 * Copyright (c) 2013-2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.Objects;


/**
 * <p>Java class for tConstraint complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="tConstraint">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other'/>
 *       &lt;/sequence>
 *       &lt;attribute name="constraintType" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tConstraint", propOrder = {
    "any"
})
@XmlSeeAlso( {
    TPropertyConstraint.class
})
public class TConstraint {

    @XmlAnyElement(lax = true)
    protected Object any;
    @XmlAttribute(name = "constraintType", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String constraintType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TConstraint)) return false;
        TConstraint that = (TConstraint) o;
        return Objects.equals(any, that.any) &&
            Objects.equals(constraintType, that.constraintType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(any, constraintType);
    }

    /**
     * Gets the value of the any property.
     *
     * @return possible object is {@link Element } {@link Object }
     */
    @Nullable
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

    /**
     * Gets the value of the constraintType property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getConstraintType() {
        return constraintType;
    }

    /**
     * Sets the value of the constraintType property.
     *
     * @param value allowed object is {@link String }
     */
    public void setConstraintType(String value) {
        this.constraintType = value;
    }
}
