/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial code generation using vhudson-jaxb-ri-2.1-2
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRequirement")
@JsonTypeInfo(
        defaultImpl = TRequirement.class,
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "fakeJacksonType")
public class TRequirement extends RelationshipSourceOrTarget {

    @XmlAttribute(name = "name", required = true)
    protected String name;

    public TRequirement() {
    }

    public TRequirement(Builder builder) {
        super(builder);
        this.name = builder.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRequirement)) return false;
        if (!super.equals(o)) return false;
        TRequirement that = (TRequirement) o;
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

    @Override
    @NonNull
    public String getFakeJacksonType() {
        return "requirement";
    }

    public static class Builder extends RelationshipSourceOrTarget.Builder<Builder> {
        private final String name;

        public Builder(String id, QName type) {
            super(id, type);
            this.name = id;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TRequirement build() {
            return new TRequirement(this);
        }
    }
}
