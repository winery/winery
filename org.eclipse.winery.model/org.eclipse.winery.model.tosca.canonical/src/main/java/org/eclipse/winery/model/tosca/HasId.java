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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;

public abstract class HasId extends TExtensibleElements implements HasIdInIdOrNameField {

    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    public HasId() {
    }

    public HasId(String id) {
        this.setId(id);
    }

    public HasId(Builder builder) {
        super(builder);
        this.id = builder.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HasId)) return false;
        HasId hasId = (HasId) o;
        return Objects.equals(id, hasId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends TExtensibleElements.Builder<T> {
        private final String id;

        public Builder(String id) {
            this.id = id;
        }

        public Builder(HasId hasId) {
            super(hasId);
            this.id = hasId.id;
        }
    }
}
