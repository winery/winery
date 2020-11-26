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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPolicyTemplate")
public class TPolicyTemplate extends TEntityTemplate {
    @XmlAttribute(name = "name")
    protected String name;

    @Deprecated // used for XML deserialization of API request content
    public TPolicyTemplate() { }

    public TPolicyTemplate(Builder builder) {
        super(builder);
        this.name = builder.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPolicyTemplate)) return false;
        if (!super.equals(o)) return false;
        TPolicyTemplate that = (TPolicyTemplate) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String value) {
        this.name = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends TEntityTemplate.Builder<Builder> {
        private String name;

        public Builder(String id, QName type) {
            super(id, type);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TPolicyTemplate build() {
            return new TPolicyTemplate(this);
        }
    }
}
