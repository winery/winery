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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicy")
public class TPolicy extends TExtensibleElements implements HasName {

    @XmlAttribute(name = "name")
    @Nullable
    protected String name;

    @XmlAttribute(name = "policyType", required = true)
    @NonNull
    protected QName policyType;

    @XmlAttribute(name = "policyRef")
    @Nullable
    protected QName policyRef;

    public TPolicy() {
    }

    public TPolicy(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.policyType = builder.policyType;
        this.policyRef = builder.policyRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPolicy)) return false;
        if (!super.equals(o)) return false;
        TPolicy tPolicy = (TPolicy) o;
        return Objects.equals(name, tPolicy.name) &&
            Objects.equals(policyType, tPolicy.policyType) &&
            Objects.equals(policyRef, tPolicy.policyRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, policyType, policyRef);
    }

    @Nullable
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@Nullable String value) {
        this.name = value;
    }

    @NonNull
    public QName getPolicyType() {
        return policyType;
    }

    public void setPolicyType(@NonNull QName value) {
        this.policyType = Objects.requireNonNull(value);
    }

    @Nullable
    public QName getPolicyRef() {
        return policyRef;
    }

    public void setPolicyRef(@Nullable QName value) {
        this.policyRef = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends TExtensibleElements.Builder<Builder> {
        private final QName policyType;
        private String name;
        private QName policyRef;

        public Builder(QName policyType) {
            this.policyType = policyType;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPolicyRef(QName policyRef) {
            this.policyRef = policyRef;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TPolicy build() {
            return new TPolicy(this);
        }
    }
}
