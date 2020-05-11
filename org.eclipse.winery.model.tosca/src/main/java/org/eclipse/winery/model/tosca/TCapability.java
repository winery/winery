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

package org.eclipse.winery.model.tosca;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCapability")
@JsonTypeInfo(
    defaultImpl = TCapability.class,
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "fakeJacksonType")
public class TCapability extends RelationshipSourceOrTarget {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "validSourceTypes")
    protected List<QName> validSourceTypes;

    public TCapability() {
    }

    public TCapability(Builder builder) {
        super(builder);
        this.name = builder.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TCapability)) return false;
        if (!super.equals(o)) return false;
        TCapability that = (TCapability) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    @NonNull
    public List<QName> getValidSourceTypes() {
        return validSourceTypes;
    }

    public void setValidSourceTypes(List<QName> value) {
        this.validSourceTypes = value;
    }

    @Override
    @NonNull
    public String getFakeJacksonType() {
        return "capability";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends RelationshipSourceOrTarget.Builder<Builder> {
        private final String name;

        public Builder(String id, QName type, String name) {
            super(id, type);
            this.name = name;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TCapability build() {
            return new TCapability(this);
        }
    }
}
