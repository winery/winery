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
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCapabilityRef")
public class XTCapabilityRef implements Serializable {

    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "ref", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected XTCapability ref;

    @Deprecated // required for XML deserialization
    public XTCapabilityRef() { }

    public XTCapabilityRef(Builder builder) {
        this.name = builder.name;
        this.ref = builder.ref;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTCapabilityRef)) return false;
        XTCapabilityRef that = (XTCapabilityRef) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(ref, that.ref);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ref);
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @NonNull
    public XTCapability getRef() {
        return ref;
    }

    public void setRef(@NonNull XTCapability value) {
        Objects.requireNonNull(value);
        this.ref = value;
    }

    public static class Builder {
        private String name;
        private XTCapability ref;

        public void setName(String name) {
            this.name = name;
        }

        public void setRef(XTCapability ref) {
            this.ref = ref;
        }

        public XTCapabilityRef build() {
            return new XTCapabilityRef(this);
        }
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
