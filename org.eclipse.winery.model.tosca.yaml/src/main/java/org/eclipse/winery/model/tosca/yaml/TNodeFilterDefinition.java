/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.support.TMapObject;
import org.eclipse.winery.model.tosca.yaml.support.TMapPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNodeFilter", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "properties",
    "capabilities"
})
public class TNodeFilterDefinition implements VisitorNode {
    private List<TMapPropertyFilterDefinition> properties;
    private List<TMapObject> capabilities;

    public TNodeFilterDefinition() {
    }

    public TNodeFilterDefinition(Builder builder) {
        this.setProperties(builder.properties);
        this.setCapabilities(builder.capabilities);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TNodeFilterDefinition)) return false;
        TNodeFilterDefinition that = (TNodeFilterDefinition) o;
        return Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getCapabilities(), that.getCapabilities());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProperties(), getCapabilities());
    }

    @NonNull
    public List<TMapPropertyFilterDefinition> getProperties() {
        if (this.properties == null) {
            this.properties = new ArrayList<>();
        }

        return properties;
    }

    public void setProperties(List<TMapPropertyFilterDefinition> properties) {
        this.properties = properties;
    }

    @NonNull
    public List<TMapObject> getCapabilities() {
        if (this.capabilities == null) {
            this.capabilities = new ArrayList<>();
        }

        return capabilities;
    }

    public void setCapabilities(List<TMapObject> capabilities) {
        this.capabilities = capabilities;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private List<TMapPropertyFilterDefinition> properties;
        private List<TMapObject> capabilities;

        public Builder setProperties(List<TMapPropertyFilterDefinition> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setCapabilities(List<TMapObject> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder addProperties(List<TMapPropertyFilterDefinition> properties) {
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

        public Builder addProperties(TMapPropertyFilterDefinition property) {
            if (property == null || property.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonList(property));
        }

        public Builder addProperties(Map<String, TPropertyFilterDefinition> properties) {
            if (properties == null || properties.isEmpty()) {
                return this;
            }

            properties.forEach((key, value) -> {
                TMapPropertyFilterDefinition tmp = new TMapPropertyFilterDefinition();
                tmp.put(key, value);
                addProperties(tmp);
            });

            return this;
        }

        public Builder addProperties(String name, TPropertyFilterDefinition property) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonMap(name, property));
        }

        public Builder addCapabilities(List<TMapObject> capabilities) {
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

        public Builder addCapabilities(TMapObject capability) {
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
                TMapObject tmp = new TMapObject();
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

        public TNodeFilterDefinition build() {
            return new TNodeFilterDefinition(this);
        }
    }
}
