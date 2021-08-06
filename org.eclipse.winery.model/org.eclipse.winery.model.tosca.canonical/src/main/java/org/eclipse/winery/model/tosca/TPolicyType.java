/*******************************************************************************
 * Copyright (c) 2020-2021 Contributors to the Eclipse Foundation
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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicyType", propOrder = {
    "appliesTo"
})
public class TPolicyType extends TEntityType {

    @XmlElementWrapper(name = "AppliesTo")
    @XmlElement(name = "NodeTypeReference", required = true)
    protected List<NodeTypeReference> appliesTo;

    @XmlAttribute(name = "policyLanguage")
    @XmlSchemaType(name = "anyURI")
    protected String policyLanguage;

    @Deprecated // used for XML deserialization of API request content
    public TPolicyType() {
    }

    public TPolicyType(Builder builder) {
        super(builder);
        this.appliesTo = builder.appliesTo;
        this.policyLanguage = builder.policyLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPolicyType)) return false;
        if (!super.equals(o)) return false;
        TPolicyType that = (TPolicyType) o;
        return Objects.equals(appliesTo, that.appliesTo) &&
            Objects.equals(policyLanguage, that.policyLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), appliesTo, policyLanguage);
    }

    @NonNull
    public List<NodeTypeReference> getAppliesTo() {
        if (appliesTo == null) {
            this.appliesTo = new ArrayList<>();
        }
        return appliesTo;
    }

    public void setAppliesTo(@Nullable List<NodeTypeReference> value) {
        this.appliesTo = value;
    }

    @Nullable
    public String getPolicyLanguage() {
        return policyLanguage;
    }

    public void setPolicyLanguage(String value) {
        this.policyLanguage = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NodeTypeReference implements Serializable {

        @XmlAttribute(name = "typeRef", required = true)
        protected QName typeRef;

        @SuppressWarnings("unused")
        public NodeTypeReference() {
        }

        public NodeTypeReference(QName typeRef) {
            this.typeRef = typeRef;
        }

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

    public static class Builder extends TEntityType.Builder<Builder> {
        private List<NodeTypeReference> appliesTo;
        private String policyLanguage;

        public Builder(String name) {
            super(name);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setAppliesTo(List<NodeTypeReference> appliesTo) {
            this.appliesTo = appliesTo;
            return this;
        }

        public Builder setPolicyLanguage(String policyLanguage) {
            this.policyLanguage = policyLanguage;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TPolicyType build() {
            return new TPolicyType(this);
        }
    }
}
