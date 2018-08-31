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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRelationshipType", propOrder = {
    "instanceStates",
    "sourceInterfaces",
    "targetInterfaces",
    "validSource",
    "validTarget"
})
public class TRelationshipType extends TEntityType {
    @XmlElement(name = "InstanceStates")
    protected TTopologyElementInstanceStates instanceStates;
    @XmlElement(name = "SourceInterfaces")
    protected TRelationshipType.SourceInterfaces sourceInterfaces;
    @XmlElement(name = "TargetInterfaces")
    protected TRelationshipType.TargetInterfaces targetInterfaces;
    @XmlElement(name = "ValidSource")
    protected TRelationshipType.ValidSource validSource;
    @XmlElement(name = "ValidTarget")
    protected TRelationshipType.ValidTarget validTarget;

    public TRelationshipType() {
    }

    public TRelationshipType(Builder builder) {
        super(builder);
        this.instanceStates = builder.instanceStates;
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
            Objects.equals(sourceInterfaces, that.sourceInterfaces) &&
            Objects.equals(targetInterfaces, that.targetInterfaces) &&
            Objects.equals(validSource, that.validSource) &&
            Objects.equals(validTarget, that.validTarget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), instanceStates, sourceInterfaces, targetInterfaces, validSource, validTarget);
    }

    @Nullable
    public TTopologyElementInstanceStates getInstanceStates() {
        return instanceStates;
    }

    public void setInstanceStates(@Nullable TTopologyElementInstanceStates value) {
        this.instanceStates = value;
    }

    public TRelationshipType.@Nullable SourceInterfaces getSourceInterfaces() {
        return sourceInterfaces;
    }

    public void setSourceInterfaces(TRelationshipType.@Nullable SourceInterfaces value) {
        this.sourceInterfaces = value;
    }

    public TRelationshipType.@Nullable TargetInterfaces getTargetInterfaces() {
        return targetInterfaces;
    }

    public void setTargetInterfaces(TRelationshipType.@Nullable TargetInterfaces value) {
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
    @XmlType(name = "", propOrder = {
        "_interface"
    })
    public static class SourceInterfaces implements Serializable {

        @XmlElement(name = "Interface", required = true)
        protected List<TInterface> _interface;

        @NonNull
        public List<TInterface> getInterface() {
            if (_interface == null) {
                _interface = new ArrayList<TInterface>();
            }
            return this._interface;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SourceInterfaces that = (SourceInterfaces) o;
            return Objects.equals(_interface, that._interface);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_interface);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "_interface"
    })
    public static class TargetInterfaces implements Serializable {

        @XmlElement(name = "Interface", required = true)
        protected List<TInterface> _interface;

        @NonNull
        public List<TInterface> getInterface() {
            if (_interface == null) {
                _interface = new ArrayList<TInterface>();
            }
            return this._interface;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TargetInterfaces that = (TargetInterfaces) o;
            return Objects.equals(_interface, that._interface);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_interface);
        }
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
        private SourceInterfaces sourceInterfaces;
        private TargetInterfaces targetInterfaces;
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

        public Builder setSourceInterfaces(TRelationshipType.SourceInterfaces sourceInterfaces) {
            this.sourceInterfaces = sourceInterfaces;
            return this;
        }

        public Builder setTargetInterfaces(TRelationshipType.TargetInterfaces targetInterfaces) {
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

        public Builder addSourceInterfaces(TRelationshipType.SourceInterfaces sourceInterfaces) {
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

            TRelationshipType.SourceInterfaces tmp = new TRelationshipType.SourceInterfaces();
            tmp.getInterface().addAll(sourceInterfaces);
            return addSourceInterfaces(tmp);
        }

        public Builder addSourceInterfaces(TInterface sourceInterfaces) {
            if (sourceInterfaces == null) {
                return this;
            }

            TRelationshipType.SourceInterfaces tmp = new TRelationshipType.SourceInterfaces();
            tmp.getInterface().add(sourceInterfaces);
            return addSourceInterfaces(tmp);
        }

        public Builder addTargetInterfaces(TRelationshipType.TargetInterfaces targetInterfaces) {
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

            TRelationshipType.TargetInterfaces tmp = new TRelationshipType.TargetInterfaces();
            tmp.getInterface().addAll(targetInterfaces);
            return addTargetInterfaces(tmp);
        }

        public Builder addTargetInterfaces(TInterface targetInterfaces) {
            if (targetInterfaces == null) {
                return this;
            }

            TRelationshipType.TargetInterfaces tmp = new TRelationshipType.TargetInterfaces();
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
