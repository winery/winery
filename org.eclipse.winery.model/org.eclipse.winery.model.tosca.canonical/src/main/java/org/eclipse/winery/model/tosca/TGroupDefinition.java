/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class TGroupDefinition extends TEntityTemplate {

    private String name;
    private String description;
    private List<QName> members;

    @Deprecated // used for XML deserialization of API request content
    public TGroupDefinition() {
    }

    protected TGroupDefinition(Builder builder) {
        super(builder);
        this.setName(builder.name);
        this.setDescription(builder.description);
        this.setMembers(builder.members);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public List<QName> getMembers() {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }

        return members;
    }

    public void setMembers(List<QName> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TGroupDefinition that = (TGroupDefinition) o;
        return Objects.equals(getName(), that.getName()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getMembers(), that.getMembers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getDescription(), getMembers());
    }

    @Override
    public String toString() {
        return "TGroupDefinition{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", members=" + members +
            ", properties=" + properties +
            ", type=" + type +
            '}';
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends TEntityTemplate.Builder<Builder> {

        private String name;
        private String description;
        private List<QName> members;

        public Builder(String name, QName type) {
            super(name, type);
            this.name = name;
        }

        public Builder(TEntityTemplate entityTemplate) {
            super(entityTemplate);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setMembers(List<QName> members) {
            this.members = members;
            return this;
        }

        public Builder addMembers(List<QName> members) {
            if (members == null || members.isEmpty()) {
                return this;
            }

            if (this.members == null) {
                this.members = new ArrayList<>(members);
            } else {
                this.members.addAll(members);
            }

            return this;
        }

        public Builder addMembers(QName member) {
            if (member == null) {
                return this;
            }

            return addMembers(Collections.singletonList(member));
        }

        @Override
        public TGroupDefinition.Builder self() {
            return this;
        }

        public TGroupDefinition build() {
            return new TGroupDefinition(this);
        }
    }
}
