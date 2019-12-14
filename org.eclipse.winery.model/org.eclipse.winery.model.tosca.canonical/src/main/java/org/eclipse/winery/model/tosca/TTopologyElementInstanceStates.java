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

package org.eclipse.winery.model.tosca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;

/**
 * <p>Java class for tTopologyElementInstanceStates complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="tTopologyElementInstanceStates">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InstanceState" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="state" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tTopologyElementInstanceStates", propOrder = {
    "instanceState"
})
public class TTopologyElementInstanceStates implements Serializable {

    @XmlElement(name = "InstanceState", required = true)
    protected List<InstanceState> instanceState;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TTopologyElementInstanceStates)) return false;
        TTopologyElementInstanceStates that = (TTopologyElementInstanceStates) o;
        return Objects.equals(instanceState, that.instanceState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceState);
    }

    /**
     * Gets the value of the instanceState property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the instanceState property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInstanceState().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link TTopologyElementInstanceStates.InstanceState }
     */
    @NonNull
    public List<InstanceState> getInstanceState() {
        if (instanceState == null) {
            instanceState = new ArrayList<InstanceState>();
        }
        return this.instanceState;
    }

    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="state" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class InstanceState implements Serializable {

        @XmlAttribute(name = "state", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String state;

        /**
         * Gets the value of the state property.
         *
         * @return possible object is {@link String }
         */
        @NonNull
        public String getState() {
            return state;
        }

        /**
         * Sets the value of the state property.
         *
         * @param value allowed object is {@link String }
         */
        public void setState(String value) {
            this.state = value;
        }
    }
}
