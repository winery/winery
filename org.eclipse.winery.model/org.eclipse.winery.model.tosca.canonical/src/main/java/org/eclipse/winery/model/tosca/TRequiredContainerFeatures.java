/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;

/**
 * <p>Java class for tRequiredContainerFeatures complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="tRequiredContainerFeatures">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequiredContainerFeature" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tRequiredContainerFeature"
 * maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRequiredContainerFeatures", propOrder = {
    "requiredContainerFeature"
})
public class TRequiredContainerFeatures implements Serializable {

    @XmlElement(name = "RequiredContainerFeature", required = true)
    protected List<TRequiredContainerFeature> requiredContainerFeature;

    @Deprecated // used for XML deserialization of API request content
    public TRequiredContainerFeatures() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRequiredContainerFeatures)) return false;
        TRequiredContainerFeatures that = (TRequiredContainerFeatures) o;
        return Objects.equals(requiredContainerFeature, that.requiredContainerFeature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredContainerFeature);
    }

    /**
     * Gets the value of the requiredContainerFeature property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requiredContainerFeature property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequiredContainerFeature().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRequiredContainerFeature }
     */
    @NonNull
    public List<TRequiredContainerFeature> getRequiredContainerFeature() {
        if (requiredContainerFeature == null) {
            requiredContainerFeature = new ArrayList<TRequiredContainerFeature>();
        }
        return this.requiredContainerFeature;
    }
}
