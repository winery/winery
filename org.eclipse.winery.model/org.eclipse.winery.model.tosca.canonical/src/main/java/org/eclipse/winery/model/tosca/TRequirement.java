/*******************************************************************************
 * Copyright (c) 2013-2019 Contributors to the Eclipse Foundation
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

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.jdt.annotation.NonNull;

@JsonTypeInfo(
    defaultImpl = TRequirement.class,
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "fakeJacksonType")
public class TRequirement extends RelationshipSourceOrTarget {

    protected String name;
    
    protected String capability;
    
    protected String node;
    
    protected String relationship;

    public TRequirement() {
    }

    public TRequirement(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.capability = builder.capability;
        this.node = builder.node;
        this.relationship = builder.relationship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TRequirement that = (TRequirement) o;
        return name.equals(that.name) &&
            Objects.equals(capability, that.capability) &&
            Objects.equals(node, that.node) &&
            Objects.equals(relationship, that.relationship);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, capability, node, relationship);
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

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    @Override
    @NonNull
    public String getFakeJacksonType() {
        return "requirement";
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends RelationshipSourceOrTarget.Builder<Builder> {
        private final String name;
        private String capability;
        private String relationship;
        private String node;

        public Builder(String id, QName type) {
            super(id, type);
            this.name = id;
        }

        public Builder(String id, String name, QName type) {
            super(id, type);
            this.name = name;
        }

        public Builder setCapability(String capability) {
            this.capability = capability;
            return self();
        }
        
        public Builder setRelationship(String relationship) {
            this.relationship = relationship;
            return self();
        }
        
        public Builder setNode(String node) {
            this.node = node;
            return self();
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
