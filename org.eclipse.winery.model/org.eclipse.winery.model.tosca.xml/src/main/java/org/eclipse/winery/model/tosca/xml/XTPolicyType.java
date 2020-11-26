/*******************************************************************************
 * Copyright (c) 2013-2020 Contributors to the Eclipse Foundation
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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicyType", propOrder = {
    "appliesTo"
})
public class XTPolicyType extends XTEntityType {
    
    @XmlElement(name = "AppliesTo")
    protected XTAppliesTo appliesTo;
    @XmlAttribute(name = "policyLanguage")
    @XmlSchemaType(name = "anyURI")
    protected String policyLanguage;

    @Deprecated // required for XML deserialization
    public XTPolicyType() { }

    public XTPolicyType(Builder builder) {
        super(builder);
        this.appliesTo = builder.appliesTo;
        this.policyLanguage = builder.policyLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTPolicyType)) return false;
        if (!super.equals(o)) return false;
        XTPolicyType that = (XTPolicyType) o;
        return Objects.equals(appliesTo, that.appliesTo) &&
            Objects.equals(policyLanguage, that.policyLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), appliesTo, policyLanguage);
    }

    @Nullable
    public XTAppliesTo getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(@Nullable XTAppliesTo value) {
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

    public static class Builder extends XTEntityType.Builder<Builder> {
        private XTAppliesTo appliesTo;
        private String policyLanguage;

        public Builder(String name) {
            super(name);
        }

        public Builder(XTEntityType entityType) {
            super(entityType);
        }

        public Builder setAppliesTo(XTAppliesTo appliesTo) {
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

        public XTPolicyType build() {
            return new XTPolicyType(this);
        }
    }
}
