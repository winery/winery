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
package org.eclipse.winery.model.tosca.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class YTGroupDefinition implements VisitorNode {

    private QName type;
    private String description;
    private Metadata metadata;
    private Map<String, YTPropertyAssignment> properties;
    private List<QName> members;

    protected YTGroupDefinition(Builder builder) {
        this.setType(builder.type);
        this.setDescription(builder.description);
        this.setMetadata(builder.metadata);
        this.setProperties(builder.properties);
        this.setMembers(builder.members);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTGroupDefinition)) return false;
        YTGroupDefinition that = (YTGroupDefinition) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getMetadata(), that.getMetadata()) &&
            Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getMembers(), that.getMembers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDescription(), getMetadata(), getProperties(), getMembers());
    }

    @Override
    public String toString() {
        return "TGroupDefinition{" +
            "type=" + getType() +
            ", description='" + getDescription() + '\'' +
            ", metadata=" + getMetadata() +
            ", properties=" + getProperties() +
            ", members=" + getMembers() +
            '}';
    }

    @NonNull
    public QName getType() {
        return type;
    }

    public void setType(QName type) {
        this.type = type;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public Metadata getMetadata() {
        if (Objects.isNull(metadata)) {
            this.metadata = new Metadata();
        }

        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @NonNull
    public Map<String, YTPropertyAssignment> getProperties() {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<>();
        }

        return properties;
    }

    public void setProperties(Map<String, YTPropertyAssignment> properties) {
        this.properties = properties;
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

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {

        private final QName type;
        private String description;
        private Metadata metadata;
        private Map<String, YTPropertyAssignment> properties;
        private List<QName> members;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder setProperties(Map<String, YTPropertyAssignment> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setMembers(List<QName> members) {
            this.members = members;
            return this;
        }

        public Builder addProperties(Map<String, YTPropertyAssignment> properties) {
            if (properties == null || properties.isEmpty()) {
                return this;
            }

            if (this.properties == null) {
                this.properties = new LinkedHashMap<>(properties);
            } else {
                this.properties.putAll(properties);
            }

            return this;
        }

        public Builder addProperties(String name, YTPropertyAssignment property) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonMap(name, property));
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

        public YTGroupDefinition build() {
            return new YTGroupDefinition(this);
        }
    }
}
