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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;


/**
 * <p>Java class for tAppliesTo complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tAppliesTo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NodeTypeReference" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
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
@XmlType(name = "tAppliesTo", propOrder = {
        "nodeTypeReference"
})
public class TAppliesTo {

    @XmlElement(name = "NodeTypeReference", required = true)
    protected List<TAppliesTo.NodeTypeReference> nodeTypeReference;

    public TAppliesTo() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TAppliesTo)) return false;
        TAppliesTo appliesTo = (TAppliesTo) o;
        return Objects.equals(nodeTypeReference, appliesTo.nodeTypeReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeTypeReference);
    }

    /**
     * Gets the value of the nodeTypeReference property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeTypeReference property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeTypeReference().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TAppliesTo.NodeTypeReference }
     */
    @NonNull
    public List<TAppliesTo.NodeTypeReference> getNodeTypeReference() {
        if (nodeTypeReference == null) {
            nodeTypeReference = new ArrayList<TAppliesTo.NodeTypeReference>();
        }
        return this.nodeTypeReference;
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
     *       &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NodeTypeReference {

        @XmlAttribute(name = "typeRef", required = true)
        protected QName typeRef;

        /**
         * Gets the value of the typeRef property.
         *
         * @return possible object is {@link QName }
         */
        @NonNull
        public QName getTypeRef() {
            return typeRef;
        }

        /**
         * Sets the value of the typeRef property.
         *
         * @param value allowed object is {@link QName }
         */
        public void setTypeRef(QName value) {
            this.typeRef = value;
        }
    }
}
