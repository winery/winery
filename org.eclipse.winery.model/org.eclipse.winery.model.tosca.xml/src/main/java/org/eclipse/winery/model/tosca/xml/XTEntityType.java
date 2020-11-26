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

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEntityType", propOrder = {
    "tags",
    "derivedFrom",
    "propertiesDefinition"
})
@XmlSeeAlso({
    XTNodeType.class,
    XTRelationshipType.class,
    XTRequirementType.class,
    XTCapabilityType.class,
    XTArtifactType.class,
    XTPolicyType.class
})
public abstract class XTEntityType extends XTExtensibleElements implements XHasName, XHasInheritance, XHasTargetNamespace, XHasTags {
    public static final String NS_SUFFIX_PROPERTIESDEFINITION_WINERY = "propertiesdefinition/winery";

    @XmlElement(name = "Tags")
    protected XTTags tags;
    @XmlElement(name = "DerivedFrom")
    protected XTEntityType.DerivedFrom derivedFrom;
    @XmlElement(name = "PropertiesDefinition")
    protected XTEntityType.PropertiesDefinition propertiesDefinition;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "abstract")
    protected XTBoolean _abstract;
    @XmlAttribute(name = "final")
    protected XTBoolean _final;
    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @Deprecated // required for XML deserialization
    public XTEntityType() { }

    public XTEntityType(Builder builder) {
        super(builder);
        this.tags = builder.tags;
        this.derivedFrom = builder.derivedFrom;
        this.propertiesDefinition = builder.propertiesDefinition;
        this.name = builder.name;
        this._abstract = builder.abstractValue;
        this._final = builder.finalValue;
        this.targetNamespace = builder.targetNamespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTEntityType)) return false;
        XTEntityType that = (XTEntityType) o;
        return Objects.equals(tags, that.tags) &&
            Objects.equals(derivedFrom, that.derivedFrom) &&
            Objects.equals(propertiesDefinition, that.propertiesDefinition) &&
            Objects.equals(name, that.name) &&
            _abstract == that._abstract &&
            _final == that._final &&
            Objects.equals(targetNamespace, that.targetNamespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tags, derivedFrom, propertiesDefinition, name, _abstract, _final, targetNamespace);
    }

    @Nullable
    public XTTags getTags() {
        return tags;
    }

    public void setTags(@Nullable XTTags value) {
        this.tags = value;
    }

    public XTEntityType.@Nullable DerivedFrom getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(@Nullable XHasType value) {
        this.derivedFrom = (XTEntityType.DerivedFrom) value;
    }

    public XTEntityType.@Nullable PropertiesDefinition getPropertiesDefinition() {
        return propertiesDefinition;
    }

    public void setPropertiesDefinition(XTEntityType.@Nullable PropertiesDefinition value) {
        this.propertiesDefinition = value;
    }

    @ADR(22)
    @Nullable
    @Override
    public String getName() {
        return name;
    }

    @ADR(22)
    @Override
    public void setName(@Nullable String value) {
        this.name = value;
    }

    @NonNull
    public QName getQName() {
        return QName.valueOf("{" + this.targetNamespace + "}" + this.name);
    }

    @NonNull
    public XTBoolean getAbstract() {
        if (_abstract == null) {
            return XTBoolean.NO;
        } else {
            return _abstract;
        }
    }

    public void setAbstract(@Nullable XTBoolean value) {
        this._abstract = value;
    }

    @NonNull
    public XTBoolean getFinal() {
        if (_final == null) {
            return XTBoolean.NO;
        } else {
            return _final;
        }
    }

    public void setFinal(@Nullable XTBoolean value) {
        this._final = value;
    }

    @Nullable
    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(@Nullable String value) {
        this.targetNamespace = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DerivedFrom implements XHasType {

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

        @NonNull
        public QName getType() {
            return this.getTypeRef();
        }

        @Override
        public void setType(@NonNull QName type) {
            Objects.requireNonNull(type);
            this.setTypeRef(type);
        }

        @Override
        @NonNull
        public QName getTypeAsQName() {
            return this.getType();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DerivedFrom that = (DerivedFrom) o;
            return Objects.equals(typeRef, that.typeRef);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeRef);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PropertiesDefinition {

        @XmlAttribute(name = "element")
        protected QName element;
        @XmlAttribute(name = "type")
        protected QName type;

        @Nullable
        public QName getElement() {
            return element;
        }

        public void setElement(@Nullable QName value) {
            this.element = value;
        }

        @Nullable
        public QName getType() {
            return type;
        }

        public void setType(@Nullable QName value) {
            this.type = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PropertiesDefinition that = (PropertiesDefinition) o;
            return Objects.equals(element, that.element) &&
                Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element, type);
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends XTExtensibleElements.Builder<T> {
        private final String name;

        private XTTags tags;
        private XTEntityType.DerivedFrom derivedFrom;
        private XTEntityType.PropertiesDefinition propertiesDefinition;
        private XTBoolean abstractValue;
        private XTBoolean finalValue;
        private String targetNamespace;

        public Builder(String name) {
            this.name = name;
        }

        public Builder(XTEntityType entityType) {
            super(entityType);
            this.name = entityType.getName();
            this.derivedFrom = entityType.getDerivedFrom();
            this.addTags(entityType.getTags());
            this.abstractValue = entityType.getAbstract();
            this.finalValue = entityType.getFinal();
            this.targetNamespace = entityType.getTargetNamespace();
            this.propertiesDefinition = entityType.getPropertiesDefinition();
        }

        public T setTags(XTTags tags) {
            this.tags = tags;
            return self();
        }

        public T setDerivedFrom(XTEntityType.DerivedFrom derivedFrom) {
            this.derivedFrom = derivedFrom;
            return self();
        }

        public T setDerivedFrom(QName derivedFrom) {
            if (derivedFrom == null) {
                return self();
            }

            if (this.derivedFrom == null) {
                this.derivedFrom = new XTEntityType.DerivedFrom();
            }
            this.derivedFrom.setTypeRef(derivedFrom);
            return self();
        }

        public T setDerivedFrom(String derivedFrom) {
            if (derivedFrom == null || derivedFrom.length() == 0) {
                return self();
            }

            return setDerivedFrom(new QName(derivedFrom));
        }

        public T setPropertiesDefinition(PropertiesDefinition propertiesDefinition) {
            this.propertiesDefinition = propertiesDefinition;
            return self();
        }

        public T setAbstract(XTBoolean abstractValue) {
            this.abstractValue = abstractValue;
            return self();
        }

        public T setAbstract(Boolean abstractValue) {
            return setAbstract(abstractValue ? XTBoolean.YES : XTBoolean.NO);
        }

        public T setFinal(XTBoolean finalValue) {
            this.finalValue = finalValue;
            return self();
        }

        public T setFinal(Boolean finalValue) {
            return setFinal(finalValue ? XTBoolean.YES : XTBoolean.NO);
        }

        public T setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
            return self();
        }

        public T addTags(XTTags tags) {
            if (tags == null || tags.getTag().isEmpty()) {
                return self();
            }

            if (this.tags == null) {
                this.tags = tags;
            } else {
                this.tags.getTag().addAll(tags.getTag());
            }
            return self();
        }

        public T addTags(List<XTTag> tags) {
            if (tags == null) {
                return self();
            }

            XTTags tmp = new XTTags();
            tmp.getTag().addAll(tags);
            return addTags(tmp);
        }

        public T addTags(XTTag tags) {
            if (tags == null) {
                return self();
            }

            XTTags tmp = new XTTags();
            tmp.getTag().add(tags);
            return addTags(tmp);
        }

        public T addTags(String key, String value) {
            if (value == null) {
                return self();
            }

            XTTag tag = new XTTag();
            tag.setName(key);
            tag.setValue(value);
            return addTags(tag);
        }

        public XTEntityType build() {
            throw new IllegalStateException("Abstract types must never be build.");
        }
    }
}
