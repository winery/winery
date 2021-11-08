/*******************************************************************************
 * Copyright (c) 2013-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.xml;

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

import org.eclipse.winery.model.tosca.xml.constants.Namespaces;
import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

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
public class XTRelationshipType extends XTEntityType {

    @XmlElementWrapper(name = "InstanceStates")
    @XmlElement(name = "InstanceState", required = true)
    protected List<XTInstanceState> instanceStates;

    @XmlElementWrapper(name = "Interfaces", namespace = Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE)
    @XmlElement(name = "Interface", required = true)
    protected List<XTInterface> interfaces;

    @XmlElementWrapper(name = "SourceInterfaces")
    @XmlElement(name = "Interface", required = true)
    protected List<XTInterface> sourceInterfaces;

    @XmlElementWrapper(name = "TargetInterfaces")
    @XmlElement(name = "Interface", required = true)
    protected List<XTInterface> targetInterfaces;

    @XmlElement(name = "ValidSource")
    protected XTRelationshipType.ValidSource validSource;

    @XmlElement(name = "ValidTarget")
    protected XTRelationshipType.ValidTarget validTarget;

    @Deprecated // required for XML deserialization
    public XTRelationshipType() {
    }

    public XTRelationshipType(Builder builder) {
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
        if (!(o instanceof XTRelationshipType)) return false;
        if (!super.equals(o)) return false;
        XTRelationshipType that = (XTRelationshipType) o;
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
    public List<XTInstanceState> getInstanceStates() {
        return instanceStates;
    }

    public void setInstanceStates(List<XTInstanceState> value) {
        this.instanceStates = value;
    }

    @Nullable
    public List<XTInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(@Nullable List<XTInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public @Nullable List<XTInterface> getSourceInterfaces() {
        return sourceInterfaces;
    }

    public void setSourceInterfaces(@Nullable List<XTInterface> value) {
        this.sourceInterfaces = value;
    }

    public @Nullable List<XTInterface> getTargetInterfaces() {
        return targetInterfaces;
    }

    public void setTargetInterfaces(@Nullable List<XTInterface> value) {
        this.targetInterfaces = value;
    }

    public XTRelationshipType.@Nullable ValidSource getValidSource() {
        return validSource;
    }

    public void setValidSource(XTRelationshipType.@Nullable ValidSource value) {
        this.validSource = value;
    }

    public XTRelationshipType.@Nullable ValidTarget getValidTarget() {
        return validTarget;
    }

    public void setValidTarget(XTRelationshipType.@Nullable ValidTarget value) {
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

    public static class Builder extends XTEntityType.Builder<Builder> {

        private List<XTInstanceState> instanceStates;
        private List<XTInterface> interfaces;
        private List<XTInterface> sourceInterfaces;
        private List<XTInterface> targetInterfaces;
        private ValidSource validSource;
        private ValidTarget validTarget;

        public Builder(String name) {
            super(name);
        }

        public Builder(XTEntityType entityType) {
            super(entityType);
        }

        public Builder setInstanceStates(List<XTInstanceState> instanceStates) {
            this.instanceStates = instanceStates;
            return this;
        }

        public Builder setSourceInterfaces(List<XTInterface> sourceInterfaces) {
            this.sourceInterfaces = sourceInterfaces;
            return this;
        }

        public Builder setTargetInterfaces(List<XTInterface> targetInterfaces) {
            this.targetInterfaces = targetInterfaces;
            return this;
        }

        public Builder setValidSource(XTRelationshipType.ValidSource validSource) {
            this.validSource = validSource;
            return this;
        }

        public Builder setValidSource(QName validSource) {
            if (validSource == null) {
                return this;
            }

            XTRelationshipType.ValidSource tmp = new XTRelationshipType.ValidSource();
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

            XTRelationshipType.ValidTarget tmp = new XTRelationshipType.ValidTarget();
            tmp.setTypeRef(validTarget);
            return setValidTarget(tmp);
        }

        public Builder addInterfaces(List<XTInterface> interfaces) {
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

        public Builder addInterfaces(XTInterface interfaces) {
            if (interfaces == null) {
                return this;
            }

            List<XTInterface> tmp = new ArrayList<>();
            tmp.add(interfaces);
            return addInterfaces(tmp);
        }

        public Builder addSourceInterfaces(List<XTInterface> sourceInterfaces) {
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

        public Builder addTargetInterfaces(List<XTInterface> targetInterfaces) {
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

        public Builder addTargetInterfaces(XTInterface targetInterfaces) {
            if (targetInterfaces == null) {
                return this;
            }

            List<XTInterface> tmp = new ArrayList<>();
            tmp.add(targetInterfaces);
            return addTargetInterfaces(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public XTRelationshipType build() {
            return new XTRelationshipType(this);
        }
    }
}
