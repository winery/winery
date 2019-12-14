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

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRequirementType")
public class TRequirementType extends TEntityType {
    @XmlAttribute(name = "requiredCapabilityType")
    protected QName requiredCapabilityType;

    public TRequirementType() {
    }

    public TRequirementType(Builder builder) {
        super(builder);
        this.requiredCapabilityType = builder.requiredCapabilityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRequirementType)) return false;
        if (!super.equals(o)) return false;
        TRequirementType that = (TRequirementType) o;
        return Objects.equals(requiredCapabilityType, that.requiredCapabilityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requiredCapabilityType);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    public QName getRequiredCapabilityType() {
        return requiredCapabilityType;
    }

    public void setRequiredCapabilityType(@Nullable QName value) {
        this.requiredCapabilityType = value;
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        private QName requiredCapabilityType;

        public Builder(String name) {
            super(name);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setRequiredCapabilityType(QName requiredCapabilityType) {
            this.requiredCapabilityType = requiredCapabilityType;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TRequirementType build() {
            return new TRequirementType(this);
        }
    }
}
