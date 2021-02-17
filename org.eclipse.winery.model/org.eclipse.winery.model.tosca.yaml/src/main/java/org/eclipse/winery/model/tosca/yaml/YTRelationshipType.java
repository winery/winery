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

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import org.eclipse.jdt.annotation.NonNull;

public class YTRelationshipType extends YTEntityType {
    private List<QName> validTargetTypes;
    private Map<String, YTInterfaceDefinition> interfaces;

    protected YTRelationshipType(Builder builder) {
        super(builder);
        this.setValidTargetTypes(builder.validTargetTypes);
        this.setInterfaces(builder.interfaces);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YTRelationshipType)) return false;
        if (!super.equals(o)) return false;
        YTRelationshipType that = (YTRelationshipType) o;
        return Objects.equals(getValidTargetTypes(), that.getValidTargetTypes()) &&
            Objects.equals(getInterfaces(), that.getInterfaces());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getValidTargetTypes(), getInterfaces());
    }

    @Override
    public String toString() {
        return "TRelationshipType{" +
            "validTargetTypes=" + getValidTargetTypes() +
            ", interfaces=" + getInterfaces() +
            "} " + super.toString();
    }

    @NonNull
    public List<QName> getValidTargetTypes() {
        if (this.validTargetTypes == null) {
            this.validTargetTypes = new ArrayList<>();
        }

        return validTargetTypes;
    }

    public void setValidTargetTypes(List<QName> validTargetTypes) {
        this.validTargetTypes = validTargetTypes;
    }

    @NonNull
    public Map<String, YTInterfaceDefinition> getInterfaces() {
        if (this.interfaces == null) {
            this.interfaces = new LinkedHashMap<>();
        }

        return interfaces;
    }

    public void setInterfaces(Map<String, YTInterfaceDefinition> interfaces) {
        this.interfaces = interfaces;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        R ir1 = super.accept(visitor, parameter);
        R ir2 = visitor.visit(this, parameter);
        if (ir1 == null) {
            return ir2;
        } else {
            return ir1.add(ir2);
        }
    }

    public static class Builder extends YTEntityType.Builder<Builder> {
        private List<QName> validTargetTypes;
        private Map<String, YTInterfaceDefinition> interfaces;

        public Builder() {

        }

        public Builder(YTEntityType entityType) {
            super(entityType);
        }

        @Override
        public Builder self() {
            return this;
        }

        public Builder setValidTargetTypes(List<QName> validTargetTypes) {
            this.validTargetTypes = validTargetTypes;
            return this;
        }

        public Builder setInterfaces(Map<String, YTInterfaceDefinition> interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public Builder addValidTargetTypes(List<QName> validTargetTypes) {
            if (validTargetTypes == null || validTargetTypes.isEmpty()) {
                return this;
            }

            if (this.validTargetTypes == null) {
                this.validTargetTypes = new ArrayList<>();
            }
            this.validTargetTypes.addAll(validTargetTypes);

            return this;
        }

        public Builder addValidTargetTypes(QName validTargetType) {
            if (validTargetType == null) {
                return this;
            }

            return addValidTargetTypes(Collections.singletonList(validTargetType));
        }

        public Builder addInterfaces(Map<String, YTInterfaceDefinition> interfaces) {
            if (Objects.isNull(interfaces) || interfaces.isEmpty()) return this;
            if (Objects.isNull(this.interfaces)) {
                this.interfaces = new LinkedHashMap<>(interfaces);
            } else {
                this.interfaces.putAll(interfaces);
            }
            return this;
        }

        public Builder addInterfaces(String key, YTInterfaceDefinition value) {
            if (Objects.isNull(key) || Objects.isNull(value)) return this;
            return addInterfaces(Collections.singletonMap(key, value));
        }

        public YTRelationshipType build() {
            return new YTRelationshipType(this);
        }
    }
}
