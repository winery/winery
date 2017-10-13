/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial code contribution
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
