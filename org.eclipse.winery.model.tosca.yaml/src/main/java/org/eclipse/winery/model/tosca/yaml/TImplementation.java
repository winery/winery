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
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Part of Operation Definition
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tImplementation", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "primary",
    "dependencies"
})
public class TImplementation implements VisitorNode {
    @XmlAttribute(name = "primary", required = true)
    private QName primary;
    private List<QName> dependencies;

    public TImplementation() {

    }

    public TImplementation(QName primary) {
        this.primary = primary;
    }

    public TImplementation(Builder builder) {
        this.setPrimary(builder.primary);
        this.setDependencies(builder.dependencies);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TImplementation)) return false;
        TImplementation that = (TImplementation) o;
        return Objects.equals(getPrimary(), that.getPrimary()) &&
            Objects.equals(getDependencies(), that.getDependencies());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrimary(), getDependencies());
    }

    @Override
    public String toString() {
        return "TImplementation{" +
            "primary=" + getPrimary() +
            ", dependencies=" + getDependencies() +
            '}';
    }

    @NonNull
    public QName getPrimary() {
        return primary;
    }

    public void setPrimary(QName primary) {
        this.primary = primary;
    }

    @NonNull
    public List<QName> getDependencies() {
        if (this.dependencies == null) {
            this.dependencies = new ArrayList<>();
        }

        return dependencies;
    }

    public void setDependencies(List<QName> dependencies) {
        this.dependencies = dependencies;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private final QName primary;
        private List<QName> dependencies;

        public Builder(QName primary) {
            this.primary = primary;
        }

        public Builder setDependencies(List<QName> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public Builder addDependencies(List<QName> dependencies) {
            if (dependencies == null || dependencies.isEmpty()) {
                return this;
            }

            if (this.dependencies == null) {
                this.dependencies = new ArrayList<>(dependencies);
            } else {
                this.dependencies.addAll(dependencies);
            }

            return this;
        }

        public Builder addDependencies(QName dependency) {
            if (dependency == null) {
                return this;
            }

            return addDependencies(Collections.singletonList(dependency));
        }

        public TImplementation build() {
            return new TImplementation(this);
        }
    }
}
