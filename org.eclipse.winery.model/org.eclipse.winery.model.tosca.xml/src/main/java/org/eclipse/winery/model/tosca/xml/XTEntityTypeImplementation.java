/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso( {
    XTNodeTypeImplementation.class,
    XTRelationshipTypeImplementation.class,
})
public abstract class XTEntityTypeImplementation extends XTExtensibleElementWithTags implements XHasName, XHasType, XHasInheritance, XHasTargetNamespace {

    @XmlElement(name = "RequiredContainerFeatures")
    protected XTRequiredContainerFeatures requiredContainerFeatures;

    @XmlElementWrapper(name = "ImplementationArtifacts") 
    @XmlElement(name = "ImplementationArtifact", required = true)
    protected List<XTImplementationArtifact> implementationArtifacts;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    @XmlAttribute(name = "abstract")
    protected XTBoolean _abstract;

    @XmlAttribute(name = "final")
    protected XTBoolean _final;

    @XmlTransient
    protected QName implementedType;

    @Deprecated // required for XML deserialization
    public XTEntityTypeImplementation() {
        super();
    }

    public XTEntityTypeImplementation(Builder<?> builder) {
        super(builder);
        this.targetNamespace = builder.targetNamespace;
        this.name = builder.name;
        this.implementedType = builder.implementedType;
        this.requiredContainerFeatures = builder.requiredContainerFeatures;
        this.implementationArtifacts = builder.implementationArtifacts;
        this._abstract = builder._abstract;
        this._final = builder._final;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

    @Nullable
    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

    public QName getQName() {
        return QName.valueOf("{" + this.targetNamespace + "}" + this.name);
    }

    @Nullable
    public XTRequiredContainerFeatures getRequiredContainerFeatures() {
        return requiredContainerFeatures;
    }

    public void setRequiredContainerFeatures(XTRequiredContainerFeatures value) {
        this.requiredContainerFeatures = value;
    }

    @Nullable
    public List<XTImplementationArtifact> getImplementationArtifacts() {
        return implementationArtifacts;
    }

    public void setImplementationArtifacts(List<XTImplementationArtifact> value) {
        this.implementationArtifacts = value;
    }

    @NonNull
    public XTBoolean getAbstract() {
        if (_abstract == null) {
            return XTBoolean.NO;
        } else {
            return _abstract;
        }
    }

    public void setAbstract(XTBoolean value) {
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

    public void setFinal(XTBoolean value) {
        this._final = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTEntityTypeImplementation)) return false;
        if (!super.equals(o)) return false;
        XTEntityTypeImplementation that = (XTEntityTypeImplementation) o;
        return Objects.equals(tags, that.tags) &&
            Objects.equals(requiredContainerFeatures, that.requiredContainerFeatures) &&
            Objects.equals(implementationArtifacts, that.implementationArtifacts) &&
            Objects.equals(targetNamespace, that.targetNamespace) &&
            Objects.equals(name, that.name) &&
            _abstract == that._abstract &&
            _final == that._final &&
            Objects.equals(implementedType, that.implementedType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tags, requiredContainerFeatures, implementationArtifacts, targetNamespace, name, _abstract, _final, implementedType);
    }

    @Override
    @NonNull
    public QName getTypeAsQName() {
        return this.implementedType;
    }

    @Override
    public void setType(@NonNull QName type) {
        this.implementedType = type;
    }

    public static abstract class Builder<T extends Builder<T>> extends XTExtensibleElementWithTags.Builder<T> {
        private final QName implementedType;
        private String name;
        private String targetNamespace;
        private XTRequiredContainerFeatures requiredContainerFeatures;
        private List<XTImplementationArtifact> implementationArtifacts;
        private XTBoolean _abstract;
        private XTBoolean _final;

        public Builder(Builder<T> builder, String name, QName implementedType) {
            super(builder);
            this.name = name;
            this.implementedType = implementedType;
        }

        public Builder(XTExtensibleElements extensibleElements, String name, QName implementedType) {
            super(extensibleElements);
            this.name = name;
            this.implementedType = implementedType;
        }

        public Builder(String name, QName implementedType) {
            this.name = name;
            this.implementedType = implementedType;
        }

        public T setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
            return self();
        }

        public T setRequiredContainerFeatures(XTRequiredContainerFeatures requiredContainerFeatures) {
            this.requiredContainerFeatures = requiredContainerFeatures;
            return self();
        }

        public T setImplementationArtifacts(List<XTImplementationArtifact> implementationArtifacts) {
            this.implementationArtifacts = implementationArtifacts;
            return self();
        }

        public T setName(String name) {
            this.name = name;
            return self();
        }

        public T setAbstract(XTBoolean _abstract) {
            this._abstract = _abstract;
            return self();
        }

        public T setFinal(XTBoolean _final) {
            this._final = _final;
            return self();
        }

        public T addRequiredContainerFeatures(XTRequiredContainerFeatures requiredContainerFeatures) {
            if (requiredContainerFeatures == null || requiredContainerFeatures.getRequiredContainerFeature().isEmpty()) {
                return self();
            }

            if (this.requiredContainerFeatures == null) {
                this.requiredContainerFeatures = requiredContainerFeatures;
            } else {
                this.requiredContainerFeatures.getRequiredContainerFeature().addAll(requiredContainerFeatures.getRequiredContainerFeature());
            }
            return self();
        }

        public T addRequiredContainerFeatures(List<XTRequiredContainerFeature> requiredContainerFeatures) {
            if (requiredContainerFeatures == null) {
                return self();
            }

            XTRequiredContainerFeatures tmp = new XTRequiredContainerFeatures();
            tmp.getRequiredContainerFeature().addAll(requiredContainerFeatures);
            return addRequiredContainerFeatures(tmp);
        }

        public T addRequiredContainerFeatures(XTRequiredContainerFeature requiredContainerFeatures) {
            if (requiredContainerFeatures == null) {
                return self();
            }

            XTRequiredContainerFeatures tmp = new XTRequiredContainerFeatures();
            tmp.getRequiredContainerFeature().add(requiredContainerFeatures);
            return addRequiredContainerFeatures(tmp);
        }

        public T addImplementationArtifacts(List<XTImplementationArtifact> implementationArtifacts) {
            if (implementationArtifacts == null || implementationArtifacts.isEmpty()) {
                return self();
            }

            if (this.implementationArtifacts == null) {
                this.implementationArtifacts = implementationArtifacts;
            } else {
                this.implementationArtifacts.addAll(implementationArtifacts);
            }
            return self();
        }
    }
}
