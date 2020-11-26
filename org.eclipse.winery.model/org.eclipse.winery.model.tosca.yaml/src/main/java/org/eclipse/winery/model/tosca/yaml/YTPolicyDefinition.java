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

public class YTPolicyDefinition implements VisitorNode {
    private QName type;
    private String description;
    private Metadata metadata;
    private Map<String, YTPropertyAssignment> properties;
    private List<QName> targets;

    protected YTPolicyDefinition(Builder builder) {
        this.setType(builder.type);
        this.setDescription(builder.description);
        this.setMetadata(builder.metadata);
        this.setProperties(builder.properties);
        this.setTargets(builder.targets);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTPolicyDefinition)) return false;
        YTPolicyDefinition that = (YTPolicyDefinition) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getDescription(), that.getDescription()) &&
            Objects.equals(getMetadata(), that.getMetadata()) &&
            Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getTargets(), that.getTargets());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDescription(), getMetadata(), getProperties(), getTargets());
    }

    @Override
    public String toString() {
        return "TPolicyDefinition{" +
            "type=" + getType() +
            ", description='" + getDescription() + '\'' +
            ", metadata=" + getMetadata() +
            ", properties=" + getProperties() +
            ", targets=" + getTargets() +
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
        if (!Objects.nonNull(metadata)) {
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
    public List<QName> getTargets() {
        if (this.targets == null) {
            this.targets = new ArrayList<>();
        }

        return targets;
    }

    public void setTargets(List<QName> targets) {
        this.targets = targets;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private String description;
        private Metadata metadata;
        private Map<String, YTPropertyAssignment> properties;
        private List<QName> targets;

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

        public Builder setTargets(List<QName> targets) {
            this.targets = targets;
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

        public Builder addTargets(List<QName> targets) {
            if (targets == null || targets.isEmpty()) {
                return this;
            }

            if (this.targets == null) {
                this.targets = new ArrayList<>(targets);
            } else {
                this.targets.addAll(targets);
            }

            return this;
        }

        public Builder addTargets(QName target) {
            if (target == null) {
                return this;
            }

            return addTargets(Collections.singletonList(target));
        }

        public YTPolicyDefinition build() {
            return new YTPolicyDefinition(this);
        }
    }
}
