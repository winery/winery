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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicyType", propOrder = {
    "appliesTo"
})
public class TPolicyType extends TEntityType {
    @XmlElement(name = "AppliesTo")
    protected TAppliesTo appliesTo;
    @XmlAttribute(name = "policyLanguage")
    @XmlSchemaType(name = "anyURI")
    protected String policyLanguage;

    @Deprecated // used for XML deserialization of API request content
    public TPolicyType() { }

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

    @Nullable
    public TAppliesTo getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(@Nullable TAppliesTo value) {
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

    public static class Builder extends TEntityType.Builder<Builder> {
        private TAppliesTo appliesTo;
        private String policyLanguage;

        public Builder(String name) {
            super(name);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setAppliesTo(TAppliesTo appliesTo) {
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
