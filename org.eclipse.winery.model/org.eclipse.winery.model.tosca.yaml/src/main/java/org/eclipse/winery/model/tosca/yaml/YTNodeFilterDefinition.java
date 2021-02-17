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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.tosca.yaml.support.YTMapObject;
import org.eclipse.winery.model.tosca.yaml.support.YTMapPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;

public class YTNodeFilterDefinition implements VisitorNode {
    private List<YTMapPropertyFilterDefinition> properties;
    private List<YTMapObject> capabilities;

    protected YTNodeFilterDefinition(Builder builder) {
        this.setProperties(builder.properties);
        this.setCapabilities(builder.capabilities);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTNodeFilterDefinition)) return false;
        YTNodeFilterDefinition that = (YTNodeFilterDefinition) o;
        return Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getCapabilities(), that.getCapabilities());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProperties(), getCapabilities());
    }

    @Override
    public String toString() {
        return "TNodeFilterDefinition{" +
            "properties=" + getProperties() +
            ", capabilities=" + getCapabilities() +
            '}';
    }

    @NonNull
    public List<YTMapPropertyFilterDefinition> getProperties() {
        if (this.properties == null) {
            this.properties = new ArrayList<>();
        }

        return properties;
    }

    public void setProperties(List<YTMapPropertyFilterDefinition> properties) {
        this.properties = properties;
    }

    @NonNull
    public List<YTMapObject> getCapabilities() {
        if (this.capabilities == null) {
            this.capabilities = new ArrayList<>();
        }

        return capabilities;
    }

    public void setCapabilities(List<YTMapObject> capabilities) {
        this.capabilities = capabilities;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private List<YTMapPropertyFilterDefinition> properties;
        private List<YTMapObject> capabilities;

        public Builder setProperties(List<YTMapPropertyFilterDefinition> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setCapabilities(List<YTMapObject> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder addProperties(List<YTMapPropertyFilterDefinition> properties) {
            if (properties == null || properties.isEmpty()) {
                return this;
            }

            if (this.properties == null) {
                this.properties = new ArrayList<>(properties);
            } else {
                this.properties.addAll(properties);
            }

            return this;
        }

        public Builder addProperties(YTMapPropertyFilterDefinition property) {
            if (property == null || property.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonList(property));
        }

        public Builder addProperties(Map<String, YTPropertyFilterDefinition> properties) {
            if (properties == null || properties.isEmpty()) {
                return this;
            }

            properties.forEach((key, value) -> {
                YTMapPropertyFilterDefinition tmp = new YTMapPropertyFilterDefinition();
                tmp.put(key, value);
                addProperties(tmp);
            });

            return this;
        }

        public Builder addProperties(String name, YTPropertyFilterDefinition property) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonMap(name, property));
        }

        public Builder addCapabilities(List<YTMapObject> capabilities) {
            if (capabilities == null || capabilities.isEmpty()) {
                return this;
            }

            if (this.capabilities == null) {
                this.capabilities = new ArrayList<>(capabilities);
            } else {
                this.capabilities.addAll(capabilities);
            }

            return this;
        }

        public Builder addCapabilities(YTMapObject capability) {
            if (capability == null || capability.isEmpty()) {
                return this;
            }

            return addCapabilities(Collections.singletonList(capability));
        }

        public Builder addCapabilities(Map<String, Object> capabilities) {
            if (capabilities == null || capabilities.isEmpty()) {
                return this;
            }

            capabilities.forEach((key, value) -> {
                YTMapObject tmp = new YTMapObject();
                tmp.put(key, value);
                addCapabilities(tmp);
            });

            return this;
        }

        public Builder addCapabilities(String name, Object capability) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addCapabilities(Collections.singletonMap(name, capability));
        }

        public YTNodeFilterDefinition build() {
            return new YTNodeFilterDefinition(this);
        }
    }
}
