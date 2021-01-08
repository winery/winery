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

import java.io.Serializable;
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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tAppliesTo", propOrder = {
    "nodeTypeReference"
})
public class XTAppliesTo implements Serializable {

    @XmlElement(name = "NodeTypeReference", required = true)
    protected List<XTAppliesTo.NodeTypeReference> nodeTypeReference;

    @Deprecated // required for XML deserialization
    public XTAppliesTo() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTAppliesTo)) return false;
        XTAppliesTo appliesTo = (XTAppliesTo) o;
        return Objects.equals(nodeTypeReference, appliesTo.nodeTypeReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeTypeReference);
    }

    /**
     * Gets the value of the nodeTypeReference property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the nodeTypeReference property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeTypeReference().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link XTAppliesTo.NodeTypeReference }
     */
    @NonNull
    public List<XTAppliesTo.NodeTypeReference> getNodeTypeReference() {
        if (nodeTypeReference == null) {
            nodeTypeReference = new ArrayList<>();
        }
        return this.nodeTypeReference;
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
     *       &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NodeTypeReference implements Serializable {

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeTypeReference that = (NodeTypeReference) o;
            return Objects.equals(typeRef, that.typeRef);
        }

        @Override
        public int hashCode() {

            return Objects.hash(typeRef);
        }
    }
}
