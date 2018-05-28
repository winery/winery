/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Used in Requirement Assignments
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRelationshipAssignment", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "type",
    "properties",
    "interfaces"
})
public class TRelationshipAssignment implements VisitorNode {
    private QName type;
    private Map<String, TPropertyAssignment> properties;
    private Map<String, TInterfaceAssignment> interfaces;

    public TRelationshipAssignment() {
    }

    public TRelationshipAssignment(QName type) {
        this.type = type;
    }

    public TRelationshipAssignment(Builder builder) {
        this.setType(builder.type);
        this.setProperties(builder.properties);
        this.setInterfaces(builder.interfaces);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRelationshipAssignment)) return false;
        TRelationshipAssignment that = (TRelationshipAssignment) o;
        return Objects.equals(getType(), that.getType()) &&
            Objects.equals(getProperties(), that.getProperties()) &&
            Objects.equals(getInterfaces(), that.getInterfaces());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getProperties(), getInterfaces());
    }

    @Override
    public String toString() {
        return "TRelationshipAssignment{" +
            "type=" + getType() +
            ", properties=" + getProperties() +
            ", interfaces=" + getInterfaces() +
            '}';
    }

    @Nullable
    public QName getType() {
        return type;
    }

    public void setType(QName type) {
        this.type = type;
    }

    @NonNull
    public Map<String, TPropertyAssignment> getProperties() {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<>();
        }

        return properties;
    }

    public void setProperties(Map<String, TPropertyAssignment> properties) {
        this.properties = properties;
    }

    @NonNull
    public Map<String, TInterfaceAssignment> getInterfaces() {
        if (this.interfaces == null) {
            this.interfaces = new LinkedHashMap<>();
        }

        return interfaces;
    }

    public void setInterfaces(Map<String, TInterfaceAssignment> interfaces) {
        this.interfaces = interfaces;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName type;
        private Map<String, TPropertyAssignment> properties;
        private Map<String, TInterfaceAssignment> interfaces;

        public Builder(QName type) {
            this.type = type;
        }

        public Builder setProperties(Map<String, TPropertyAssignment> properties) {
            this.properties = properties;
            return this;
        }

        public Builder setInterfaces(Map<String, TInterfaceAssignment> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder addProperties(Map<String, TPropertyAssignment> properties) {
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

        public Builder addProperties(String name, TPropertyAssignment property) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addProperties(Collections.singletonMap(name, property));
        }

        public Builder addInterfaces(Map<String, TInterfaceAssignment> interfaces) {
            if (interfaces == null || interfaces.isEmpty()) {
                return this;
            }

            if (this.interfaces == null) {
                this.interfaces = new LinkedHashMap<>(interfaces);
            } else {
                this.interfaces.putAll(interfaces);
            }

            return this;
        }

        public Builder addInterfaces(String name, TInterfaceAssignment interfaceAssignment) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addInterfaces(Collections.singletonMap(name, interfaceAssignment));
        }

        public TRelationshipAssignment build() {
            return new TRelationshipAssignment(this);
        }
    }
}
