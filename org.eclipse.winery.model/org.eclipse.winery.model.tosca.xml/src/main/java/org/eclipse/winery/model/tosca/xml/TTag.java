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
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tTag")
public class TTag implements Serializable {

    @XmlAttribute(name = "name", required = true)
    @NonNull
    protected String name;

    @XmlAttribute(name = "value", required = true)
    protected String value;

    public TTag() {

    }

    public TTag(Builder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.value = Objects.requireNonNull(builder.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TTag)) return false;
        TTag tTag = (TTag) o;
        return Objects.equals(name, tTag.name) &&
            Objects.equals(value, tTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String value) {
        Objects.requireNonNull(value);
        this.name = value;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    public void setValue(@NonNull String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public void accept(Visitor visitor) {
        visitor.accept(this);
    }

    public static class Builder {
        private String name;
        private String value;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public TTag build() {
            return new TTag(this);
        }
    }
}
