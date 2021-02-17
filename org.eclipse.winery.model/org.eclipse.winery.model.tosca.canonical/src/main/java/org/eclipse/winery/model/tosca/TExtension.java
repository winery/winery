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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tExtension")
public class TExtension extends TExtensibleElements {
    @XmlAttribute(name = "namespace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String namespace;
    @XmlAttribute(name = "mustUnderstand")
    protected boolean mustUnderstand;

    @Deprecated // used for XML deserialization of API request content
    public TExtension() { }

    public TExtension(Builder builder) {
        super(builder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TExtension)) return false;
        if (!super.equals(o)) return false;
        TExtension that = (TExtension) o;
        return Objects.equals(namespace, that.namespace) &&
            mustUnderstand == that.mustUnderstand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), namespace, mustUnderstand);
    }

    @NonNull
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(@NonNull String value) {
        Objects.requireNonNull(value);
        this.namespace = value;
    }

    public boolean getMustUnderstand() {
        return mustUnderstand;
    }

    public void setMustUnderstand(@Nullable Boolean value) {
        this.mustUnderstand = value == null ? true : value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends TExtensibleElements.Builder<Builder> {
        private final String namespace;
        private boolean mustUnderstand = true;

        public Builder(String namespace) {
            this.namespace = namespace;
        }

        public Builder setMustUnderstand(boolean mustUnderstand) {
            this.mustUnderstand = mustUnderstand;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TExtension build() {
            return new TExtension(this);
        }
    }
}
