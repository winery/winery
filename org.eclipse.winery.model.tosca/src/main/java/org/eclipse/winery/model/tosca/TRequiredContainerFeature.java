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
 * <p>Java class for tRequiredContainerFeature complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tRequiredContainerFeature">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="feature" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRequiredContainerFeature")
public class TRequiredContainerFeature {

    @XmlAttribute(name = "feature", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String feature;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRequiredContainerFeature)) return false;
        TRequiredContainerFeature that = (TRequiredContainerFeature) o;
        return Objects.equals(feature, that.feature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feature);
    }

    /**
     * Gets the value of the feature property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getFeature() {
        return feature;
    }

    /**
     * Sets the value of the feature property.
     *
     * @param value allowed object is {@link String }
     */
    public void setFeature(String value) {
        this.feature = value;
    }
}
