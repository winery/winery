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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tGroupType", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "members",
    "requirements",
    "capabilities",
    "interfaces"
})
public class TGroupType extends TNodeOrGroupType {
    private List<QName> members;
    private List<TMapRequirementDefinition> requirements;
    private Map<String, TCapabilityDefinition> capabilities;
    private Map<String, TInterfaceDefinition> interfaces;

    public TGroupType() {
    }

    public TGroupType(Builder builder) {
        super(builder);
        this.setMembers(builder.members);
        this.setRequirements(builder.requirements);
        this.setCapabilities(builder.capabilities);
        this.setInterfaces(builder.interfaces);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TGroupType)) return false;
        if (!super.equals(o)) return false;
        TGroupType that = (TGroupType) o;
        return Objects.equals(getMembers(), that.getMembers()) &&
            Objects.equals(getRequirements(), that.getRequirements()) &&
            Objects.equals(getCapabilities(), that.getCapabilities()) &&
            Objects.equals(getInterfaces(), that.getInterfaces());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMembers(), getRequirements(), getCapabilities(), getInterfaces());
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

    @NonNull
    public List<TMapRequirementDefinition> getRequirements() {
        if (this.requirements == null) {
            this.requirements = new ArrayList<>();
        }

        return requirements;
    }

    public void setRequirements(List<TMapRequirementDefinition> requirements) {
        this.requirements = requirements;
    }

    @NonNull
    public Map<String, TCapabilityDefinition> getCapabilities() {
        if (this.capabilities == null) {
            this.capabilities = new LinkedHashMap<>();
        }

        return capabilities;
    }

    public void setCapabilities(Map<String, TCapabilityDefinition> capabilities) {
        this.capabilities = capabilities;
    }

    @NonNull
    public Map<String, TInterfaceDefinition> getInterfaces() {
        if (this.interfaces == null) {
            this.interfaces = new LinkedHashMap<>();
        }

        return interfaces;
    }

    public void setInterfaces(Map<String, TInterfaceDefinition> interfaces) {
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

    public static class Builder extends TEntityType.Builder<Builder> {
        private List<QName> members;
        private List<TMapRequirementDefinition> requirements;
        private Map<String, TCapabilityDefinition> capabilities;
        private Map<String, TInterfaceDefinition> interfaces;

        public Builder() {

        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        @Override
        public Builder self() {
            return this;
        }

        public Builder setMembers(List<QName> members) {
            this.members = members;
            return this;
        }

        public Builder setRequirements(List<TMapRequirementDefinition> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder setCapabilities(Map<String, TCapabilityDefinition> capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public Builder setInterfaces(Map<String, TInterfaceDefinition> interfaces) {
            this.interfaces = interfaces;
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

        public Builder addRequirements(List<TMapRequirementDefinition> requirements) {
            if (requirements == null || requirements.isEmpty()) {
                return this;
            }

            if (this.requirements == null) {
                this.requirements = new ArrayList<>(requirements);
            } else {
                this.requirements.addAll(requirements);
            }

            return this;
        }

        public Builder addRequirements(TMapRequirementDefinition requirement) {
            if (requirement == null || requirement.isEmpty()) {
                return this;
            }

            return addRequirements(Collections.singletonList(requirement));
        }

        public Builder addRequirements(Map<String, TRequirementDefinition> requirements) {
            if (requirements == null || requirements.isEmpty()) {
                return this;
            }

            // TOSCA YAML syntax: one RequirementDefinition per MapRequirementDefinition
            requirements.forEach((key, value) -> {
                TMapRequirementDefinition tmp = new TMapRequirementDefinition();
                tmp.put(key, value);
                addRequirements(tmp);
            });

            return this;
        }

        public Builder addRequirements(String name, TRequirementDefinition requirement) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            return addRequirements(Collections.singletonMap(name, requirement));
        }

        public Builder addCapabilities(Map<String, TCapabilityDefinition> capabilities) {
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

        public Builder addCapabilities(String key, TCapabilityDefinition capability) {
            if (key == null || key.isEmpty()) {
                return this;
            }

            return addCapabilities(Collections.singletonMap(key, capability));
        }

        public Builder addInterfaces(Map<String, TInterfaceDefinition> interfaces) {
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

        public Builder addInterfaces(String name, TInterfaceDefinition interfaceDefinition) {
            if (name == null) {
                return this;
            }

            return addInterfaces(Collections.singletonMap(name, interfaceDefinition));
        }

        public TGroupType build() {
            return new TGroupType(this);
        }
    }
}
