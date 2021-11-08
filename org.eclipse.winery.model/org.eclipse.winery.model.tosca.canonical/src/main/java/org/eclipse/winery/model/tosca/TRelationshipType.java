/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRelationshipType", propOrder = {
    "instanceStates",
    "interfaces",
    "sourceInterfaces",
    "targetInterfaces",
    "interfaceDefinitions",
    "validSource",
    "validTarget",
    "validTargetList"
})
public class TRelationshipType extends TEntityType {

    @XmlElement(name = "InstanceStates")
    protected List<TInstanceState> instanceStates;

    @XmlElementWrapper(name = "Interfaces", namespace = Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE)
    @XmlElement(name = "Interface", required = true)
    protected List<TInterface> interfaces;

    @XmlElementWrapper(name = "SourceInterfaces")
    @XmlElement(name = "Interface")
    protected List<TInterface> sourceInterfaces;

    @XmlElementWrapper(name = "TargetInterfaces")
    @XmlElement(name = "Interface")
    protected List<TInterface> targetInterfaces;

    @XmlElement(name = "InterfaceDefinitions")
    protected List<TInterfaceDefinition> interfaceDefinitions;

    @XmlElement(name = "ValidSource")
    protected TRelationshipType.ValidSource validSource;

    @XmlElement(name = "ValidTarget")
    protected TRelationshipType.ValidTarget validTarget;

    // related to YAML 1.3
    // https://docs.oasis-open.org/tosca/TOSCA-Simple-Profile-YAML/v1.3/os/TOSCA-Simple-Profile-YAML-v1.3-os.html#DEFN_ENTITY_RELATIONSHIP_TYPE
    @XmlElement(name = "ValidTargetList")
    protected List<QName> validTargetList;

    @Deprecated // used for XML deserialization of API request content
    public TRelationshipType() {
    }

    public TRelationshipType(Builder builder) {
        super(builder);
        this.instanceStates = builder.instanceStates;
        this.interfaces = builder.interfaces;
        this.sourceInterfaces = builder.sourceInterfaces;
        this.targetInterfaces = builder.targetInterfaces;
        this.interfaceDefinitions = builder.interfaceDefinitions;
        this.validSource = builder.validSource;
        this.validTarget = builder.validTarget;
        this.validTargetList = builder.validTargetList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRelationshipType)) return false;
        if (!super.equals(o)) return false;
        TRelationshipType that = (TRelationshipType) o;
        return Objects.equals(instanceStates, that.instanceStates) &&
            Objects.equals(interfaces, that.interfaces) &&
            Objects.equals(sourceInterfaces, that.sourceInterfaces) &&
            Objects.equals(targetInterfaces, that.targetInterfaces) &&
            Objects.equals(interfaceDefinitions, that.interfaceDefinitions) &&
            Objects.equals(validSource, that.validSource) &&
            Objects.equals(validTarget, that.validTarget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), instanceStates, interfaces, sourceInterfaces, targetInterfaces, interfaceDefinitions, validSource, validTarget);
    }

    @Nullable
    public List<TInstanceState> getInstanceStates() {
        return instanceStates;
    }

    public void setInstanceStates(List<TInstanceState> value) {
        this.instanceStates = value;
    }

    @Nullable
    public List<TInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<TInterface> interfaces) {
        this.interfaces = interfaces;
    }

    @Nullable
    public List<TInterface> getSourceInterfaces() {
        return sourceInterfaces;
    }

    public void setSourceInterfaces(List<TInterface> value) {
        this.sourceInterfaces = value;
    }

    @Nullable
    public List<TInterface> getTargetInterfaces() {
        return targetInterfaces;
    }

    public void setTargetInterfaces(List<TInterface> value) {
        this.targetInterfaces = value;
    }

    @Nullable
    public List<TInterfaceDefinition> getInterfaceDefinitions() {
        return interfaceDefinitions;
    }

    public void setInterfaceDefinitions(List<TInterfaceDefinition> interfaceDefinitions) {
        this.interfaceDefinitions = interfaceDefinitions;
    }

    public TRelationshipType.@Nullable ValidSource getValidSource() {
        return validSource;
    }

    public void setValidSource(TRelationshipType.@Nullable ValidSource value) {
        this.validSource = value;
    }

    public TRelationshipType.@Nullable ValidTarget getValidTarget() {
        return validTarget;
    }

    public void setValidTarget(TRelationshipType.@Nullable ValidTarget value) {
        this.validTarget = value;
    }

    @Nullable
    public List<QName> getValidTargetList() {
        return validTargetList;
    }

    public void setValidTargetList(List<QName> validTargetList) {
        this.validTargetList = validTargetList;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ValidSource implements Serializable {

        @XmlAttribute(name = "typeRef", required = true)
        protected QName typeRef;

        @NonNull
        public QName getTypeRef() {
            return typeRef;
        }

        public void setTypeRef(@NonNull QName value) {
            Objects.requireNonNull(value);
            this.typeRef = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValidSource that = (ValidSource) o;
            return Objects.equals(typeRef, that.typeRef);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeRef);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ValidTarget implements Serializable {

        @XmlAttribute(name = "typeRef", required = true)
        protected QName typeRef;

        @NonNull
        public QName getTypeRef() {
            return typeRef;
        }

        public void setTypeRef(@NonNull QName value) {
            Objects.requireNonNull(value);
            this.typeRef = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValidTarget that = (ValidTarget) o;
            return Objects.equals(typeRef, that.typeRef);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeRef);
        }
    }

    public static class Builder extends TEntityType.Builder<Builder> {
        private List<TInstanceState> instanceStates;
        private List<TInterface> interfaces;
        private List<TInterface> sourceInterfaces;
        private List<TInterface> targetInterfaces;
        private List<TInterfaceDefinition> interfaceDefinitions;
        private ValidSource validSource;
        private ValidTarget validTarget;
        private List<QName> validTargetList;

        public Builder(String name) {
            super(name);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setInstanceStates(List<TInstanceState> instanceStates) {
            this.instanceStates = instanceStates;
            return this;
        }

        public Builder setSourceInterfaces(List<TInterface> sourceInterfaces) {
            this.sourceInterfaces = sourceInterfaces;
            return this;
        }

        public Builder setTargetInterfaces(List<TInterface> targetInterfaces) {
            this.targetInterfaces = targetInterfaces;
            return this;
        }

        public Builder setValidSource(TRelationshipType.ValidSource validSource) {
            this.validSource = validSource;
            return this;
        }

        public Builder setValidSource(QName validSource) {
            if (validSource == null) {
                return this;
            }

            TRelationshipType.ValidSource tmp = new TRelationshipType.ValidSource();
            tmp.setTypeRef(validSource);
            return setValidSource(tmp);
        }

        public Builder setValidTarget(ValidTarget validTarget) {
            this.validTarget = validTarget;
            return this;
        }

        public Builder setValidTarget(QName validTarget) {
            if (validTarget == null) {
                return this;
            }

            TRelationshipType.ValidTarget tmp = new TRelationshipType.ValidTarget();
            tmp.setTypeRef(validTarget);
            return setValidTarget(tmp);
        }

        public Builder setValidTargetList(List<QName> validTargetList) {
            this.validTargetList = validTargetList;
            return this;
        }

        public Builder addInterfaces(List<TInterface> interfaces) {
            if (interfaces == null || interfaces.isEmpty()) {
                return this;
            }

            if (this.interfaces == null) {
                this.interfaces = interfaces;
            } else {
                this.interfaces.addAll(interfaces);
            }
            return this;
        }

        public Builder addInterfaces(TInterface interfaces) {
            if (interfaces == null) {
                return this;
            }

            List<TInterface> tmp = new ArrayList<>();
            tmp.add(interfaces);
            return addInterfaces(tmp);
        }

        public Builder addSourceInterfaces(List<TInterface> sourceInterfaces) {
            if (sourceInterfaces == null || sourceInterfaces.isEmpty()) {
                return this;
            }

            if (this.sourceInterfaces == null) {
                this.sourceInterfaces = sourceInterfaces;
            } else {
                this.sourceInterfaces.addAll(sourceInterfaces);
            }
            return this;
        }

        public Builder addTargetInterfaces(List<TInterface> targetInterfaces) {
            if (targetInterfaces == null || targetInterfaces.isEmpty()) {
                return this;
            }

            if (this.targetInterfaces == null) {
                this.targetInterfaces = targetInterfaces;
            } else {
                this.targetInterfaces.addAll(targetInterfaces);
            }
            return this;
        }

        public Builder setInterfaceDefinitions(List<TInterfaceDefinition> interfaceDefinitions) {
            this.interfaceDefinitions = interfaceDefinitions;
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        public TRelationshipType build() {
            return new TRelationshipType(this);
        }
    }
}
