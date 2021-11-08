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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.support.YTListString;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Used in Topology Template Definition
 */
public class YTSubstitutionMappings implements VisitorNode {
    private QName nodeType;
    private Map<String, YTListString> capabilities;
    private Map<String, YTListString> requirements;

    protected YTSubstitutionMappings(Builder builder) {
        this.setNodeType(builder.nodeType);
        this.setCapabilities(builder.capabilities);
        this.setRequirements(builder.requirements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTSubstitutionMappings)) return false;
        YTSubstitutionMappings that = (YTSubstitutionMappings) o;
        return Objects.equals(getNodeType(), that.getNodeType()) &&
            Objects.equals(getCapabilities(), that.getCapabilities()) &&
            Objects.equals(getRequirements(), that.getRequirements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeType(), getCapabilities(), getRequirements());
    }

    @Override
    public String toString() {
        return "TSubstitutionMappings{" +
            "nodeType=" + getNodeType() +
            ", capabilities=" + getCapabilities() +
            ", requirements=" + getRequirements() +
            '}';
    }

    @Nullable
    public QName getNodeType() {
        return nodeType;
    }

    public void setNodeType(QName nodeType) {
        this.nodeType = nodeType;
    }

    @NonNull
    public Map<String, YTListString> getCapabilities() {
        if (this.capabilities == null) {
            this.capabilities = new LinkedHashMap<>();
        }

        return capabilities;
    }

    public void setCapabilities(Map<String, YTListString> capabilities) {
        this.capabilities = capabilities;
    }

    @NonNull
    public Map<String, YTListString> getRequirements() {
        if (this.requirements == null) {
            this.requirements = new LinkedHashMap<>();
        }

        return requirements;
    }

    public void setRequirements(Map<String, YTListString> requirements) {
        this.requirements = requirements;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public static class Builder {
        private QName nodeType;
        private Map<String, YTListString> capabilities;
        private Map<String, YTListString> requirements;

        public Builder setNodeType(QName nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        public Builder setCapabilities(Map<String, YTListString> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setRequirements(Map<String, YTListString> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder addCapabilities(Map<String, YTListString> capabilities) {
            if (capabilities == null || capabilities.isEmpty()) {
                return this;
            }

            if (this.capabilities == null) {
                this.capabilities = new LinkedHashMap<>(capabilities);
            } else {
                this.capabilities.putAll(capabilities);
            }

            return this;
        }

        public Builder addCapabilities(String name, YTListString capability) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addCapabilities(Collections.singletonMap(name, capability));
        }

        public Builder addRequirements(Map<String, YTListString> requirements) {
            if (requirements == null || requirements.isEmpty()) {
                return this;
            }

            if (this.requirements == null) {
                this.requirements = new LinkedHashMap<>(requirements);
            } else {
                this.requirements.putAll(requirements);
            }

            return this;
        }

        public Builder addRequirements(String name, YTListString requirement) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRequirements(Collections.singletonMap(name, requirement));
        }

        public YTSubstitutionMappings build() {
            return new YTSubstitutionMappings(this);
        }
    }
}
