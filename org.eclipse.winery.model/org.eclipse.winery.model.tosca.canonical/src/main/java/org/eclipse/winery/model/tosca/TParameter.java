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

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tParameter")
public class TParameter implements Serializable {

    @XmlAttribute(name = "name", required = true)
    @NonNull
    protected String name;

    @XmlAttribute(name = "type", required = true)
    @NonNull
    protected String type;

    @XmlAttribute(name = "required")
    protected TBoolean required;

    public TParameter() {
    }

    public TParameter(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.required = builder.required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TParameter)) return false;
        TParameter that = (TParameter) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            required == that.required;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, required);
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
    public String getType() {
        return type;
    }

    public void setType(@NonNull String value) {
        Objects.requireNonNull(value);
        this.type = value;
    }

    /**
     * @return In case the internal model stores <code>null</code>, the default value <code>TBoolean.YES</code> is returned
     */
    @NonNull
    public TBoolean getRequired() {
        if (required == null) {
            return TBoolean.YES;
        } else {
            return required;
        }
    }

    public void setRequired(@NonNull TBoolean value) {
        Objects.requireNonNull(value);
        this.required = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder {
        private final String name;
        private final String type;
        private final TBoolean required;

        public Builder(String name, String type, TBoolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }

        public Builder(String name, String type, Boolean required) {
            this(name, type, required == null ? TBoolean.YES : required ? TBoolean.YES : TBoolean.NO);
        }

        public TParameter build() {
            return new TParameter(this);
        }
    }
}
