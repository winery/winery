/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.xml;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import javax.xml.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>Java class for tPlans complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="tPlans">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Plan" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPlan" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="targetNamespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPlans", propOrder = {
    "plan"
})
public class XTPlans implements Serializable {

    @XmlElement(name = "Plan", required = true)
    protected List<XTPlan> plan;
    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @Deprecated // required for XML deserialization
    public XTPlans() { }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTPlans)) return false;
        XTPlans tPlans = (XTPlans) o;
        return Objects.equals(plan, tPlans.plan) &&
            Objects.equals(targetNamespace, tPlans.targetNamespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plan, targetNamespace);
    }

    /**
     * Gets the value of the plan property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the plan property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlan().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XTPlan }
     */
    @NonNull
    public List<XTPlan> getPlan() {
        if (plan == null) {
            plan = new ArrayList<XTPlan>();
        }
        return this.plan;
    }

    /**
     * Gets the value of the targetNamespace property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Sets the value of the targetNamespace property.
     *
     * @param value allowed object is {@link String }
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }
}
