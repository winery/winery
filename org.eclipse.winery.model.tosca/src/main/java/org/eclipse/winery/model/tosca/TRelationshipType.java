/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
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
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
    "validSource",
    "validTarget"
})
public class TRelationshipType extends TEntityType {

    @XmlElement(name = "InstanceStates")
    protected TTopologyElementInstanceStates instanceStates;
    @XmlElement(name = "Interfaces", namespace = Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE)
    protected TInterfaces interfaces;
    @XmlElement(name = "SourceInterfaces")
    protected TInterfaces sourceInterfaces;
    @XmlElement(name = "TargetInterfaces")
    protected TInterfaces targetInterfaces;
    @XmlElement(name = "ValidSource")
    protected TRelationshipType.ValidSource validSource;
    @XmlElement(name = "ValidTarget")
    protected TRelationshipType.ValidTarget validTarget;

    public TRelationshipType() {
    }

    public TRelationshipType(Builder builder) {
        super(builder);
        this.instanceStates = builder.instanceStates;
        this.interfaces = builder.interfaces;
        this.sourceInterfaces = builder.sourceInterfaces;
        this.targetInterfaces = builder.targetInterfaces;
        this.validSource = builder.validSource;
        this.validTarget = builder.validTarget;
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
            Objects.equals(validSource, that.validSource) &&
            Objects.equals(validTarget, that.validTarget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), instanceStates, interfaces, sourceInterfaces, targetInterfaces, validSource, validTarget);
    }

    @Nullable
    public TTopologyElementInstanceStates getInstanceStates() {
        return instanceStates;
    }

    public void setInstanceStates(@Nullable TTopologyElementInstanceStates value) {
        this.instanceStates = value;
    }

    @Nullable
    public TInterfaces getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(@Nullable TInterfaces interfaces) {
        this.interfaces = interfaces;
    }

    public @Nullable TInterfaces getSourceInterfaces() {
        return sourceInterfaces;
    }

    public void setSourceInterfaces(@Nullable TInterfaces value) {
        this.sourceInterfaces = value;
    }

    public @Nullable TInterfaces getTargetInterfaces() {
        return targetInterfaces;
    }

    public void setTargetInterfaces(@Nullable TInterfaces value) {
        this.targetInterfaces = value;
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
        private TTopologyElementInstanceStates instanceStates;
        private TInterfaces interfaces;
        private TInterfaces sourceInterfaces;
        private TInterfaces targetInterfaces;
        private ValidSource validSource;
        private ValidTarget validTarget;

        public Builder(String name) {
            super(name);
        }

        public Builder(TEntityType entityType) {
            super(entityType);
        }

        public Builder setInstanceStates(TTopologyElementInstanceStates instanceStates) {
            this.instanceStates = instanceStates;
            return this;
        }

        public Builder setSourceInterfaces(TInterfaces sourceInterfaces) {
            this.sourceInterfaces = sourceInterfaces;
            return this;
        }

        public Builder setTargetInterfaces(TInterfaces targetInterfaces) {
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

        public Builder addInterfaces(TInterfaces interfaces) {
            if (interfaces == null || interfaces.getInterface().isEmpty()) {
                return this;
            }

            if (this.interfaces == null) {
                this.interfaces = interfaces;
            } else {
                this.interfaces.getInterface().addAll(interfaces.getInterface());
            }
            return this;
        }

        public Builder addInterfaces(List<TInterface> interfaces) {
            if (interfaces == null) {
                return this;
            }

            TInterfaces tmp = new TInterfaces();
            tmp.getInterface().addAll(interfaces);
            return addInterfaces(tmp);
        }

        public Builder addInterfaces(TInterface interfaces) {
            if (interfaces == null) {
                return this;
            }

            TInterfaces tmp = new TInterfaces();
            tmp.getInterface().add(interfaces);
            return addInterfaces(tmp);
        }

        public Builder addSourceInterfaces(TInterfaces sourceInterfaces) {
            if (sourceInterfaces == null || sourceInterfaces.getInterface().isEmpty()) {
                return this;
            }

            if (this.sourceInterfaces == null) {
                this.sourceInterfaces = sourceInterfaces;
            } else {
                this.sourceInterfaces.getInterface().addAll(sourceInterfaces.getInterface());
            }
            return this;
        }

        public Builder addSourceInterfaces(List<TInterface> sourceInterfaces) {
            if (sourceInterfaces == null) {
                return this;
            }

            TInterfaces tmp = new TInterfaces();
            tmp.getInterface().addAll(sourceInterfaces);
            return addSourceInterfaces(tmp);
        }

        public Builder addSourceInterfaces(TInterface sourceInterfaces) {
            if (sourceInterfaces == null) {
                return this;
            }

            TInterfaces tmp = new TInterfaces();
            tmp.getInterface().add(sourceInterfaces);
            return addSourceInterfaces(tmp);
        }

        public Builder addTargetInterfaces(TInterfaces targetInterfaces) {
            if (targetInterfaces == null || targetInterfaces.getInterface().isEmpty()) {
                return this;
            }

            if (this.targetInterfaces == null) {
                this.targetInterfaces = targetInterfaces;
            } else {
                this.targetInterfaces.getInterface().addAll(targetInterfaces.getInterface());
            }
            return this;
        }

        public Builder addTargetInterfaces(List<TInterface> targetInterfaces) {
            if (targetInterfaces == null) {
                return this;
            }

            TInterfaces tmp = new TInterfaces();
            tmp.getInterface().addAll(targetInterfaces);
            return addTargetInterfaces(tmp);
        }

        public Builder addTargetInterfaces(TInterface targetInterfaces) {
            if (targetInterfaces == null) {
                return this;
            }

            TInterfaces tmp = new TInterfaces();
            tmp.getInterface().add(targetInterfaces);
            return addTargetInterfaces(tmp);
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
