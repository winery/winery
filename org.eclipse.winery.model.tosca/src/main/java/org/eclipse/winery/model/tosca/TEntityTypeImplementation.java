/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implemenmtation
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({
        TNodeTypeImplementation.class,
        TRelationshipTypeImplementation.class,
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class TEntityTypeImplementation extends TExtensibleElements implements HasName, HasType, HasInheritance, HasTargetNamespace {

    @XmlElement(name = "Tags")
    protected TTags tags;

    @XmlElement(name = "RequiredContainerFeatures")
    protected TRequiredContainerFeatures requiredContainerFeatures;

    @XmlElement(name = "ImplementationArtifacts")
    protected TImplementationArtifacts implementationArtifacts;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    @XmlAttribute(name = "abstract")
    protected TBoolean _abstract;

    @XmlAttribute(name = "final")
    protected TBoolean _final;

    @XmlTransient
    protected QName implementedType;
    
    public TEntityTypeImplementation() {
        super();
    }
    
    public TEntityTypeImplementation(Builder builder) {
        super(builder);
        this.targetNamespace = builder.targetNamespace;
        this.name = builder.name;
        this.implementedType = builder.implementedType;
        this.tags = builder.tags;
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

    @Nullable
    public TTags getTags() {
        return tags;
    }

    public void setTags(TTags value) {
        this.tags = value;
    }

    @Nullable
    public TRequiredContainerFeatures getRequiredContainerFeatures() {
        return requiredContainerFeatures;
    }

    public void setRequiredContainerFeatures(TRequiredContainerFeatures value) {
        this.requiredContainerFeatures = value;
    }

    @Nullable
    public TImplementationArtifacts getImplementationArtifacts() {
        return implementationArtifacts;
    }

    public void setImplementationArtifacts(TImplementationArtifacts value) {
        this.implementationArtifacts = value;
    }

    @NonNull
    public TBoolean getAbstract() {
        if (_abstract == null) {
            return TBoolean.NO;
        } else {
            return _abstract;
        }
    }

    public void setAbstract(TBoolean value) {
        this._abstract = value;
    }

    @NonNull
    public TBoolean getFinal() {
        if (_final == null) {
            return TBoolean.NO;
        } else {
            return _final;
        }
    }

    public void setFinal(TBoolean value) {
        this._final = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TEntityTypeImplementation)) return false;
        if (!super.equals(o)) return false;
        TEntityTypeImplementation that = (TEntityTypeImplementation) o;
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
    public QName getTypeAsQName() {
        return this.implementedType;
    }

    @Override
    public void setType(QName type) {
        this.implementedType = type;
    }

    public static abstract class Builder extends TExtensibleElements.Builder {
        private String name;
        private String targetNamespace;
        private final QName implementedType;
        private TTags tags;
        private TRequiredContainerFeatures requiredContainerFeatures;
        private TImplementationArtifacts implementationArtifacts;
        private TBoolean _abstract;
        private TBoolean _final;

        public Builder(Builder builder, String name, QName implementedType) {
            super(builder);
            this.name = name;
            this.implementedType = implementedType;
        }

        public Builder(TExtensibleElements extensibleElements, String name, QName implementedType) {
            super(extensibleElements);
            this.name = name;
            this.implementedType = implementedType;
        }

        public Builder(String name, QName implementedType) {
            this(new TExtensibleElements(), name, implementedType);
        }

        public Builder setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
            return this;
        }

        public Builder setTags(TTags tags) {
            this.tags = tags;
            return this;
        }

        public Builder setRequiredContainerFeatures(TRequiredContainerFeatures requiredContainerFeatures) {
            this.requiredContainerFeatures = requiredContainerFeatures;
            return this;
        }

        public Builder setImplementationArtifacts(TImplementationArtifacts implementationArtifacts) {
            this.implementationArtifacts = implementationArtifacts;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAbstract(TBoolean _abstract) {
            this._abstract = _abstract;
            return this;
        }

        public Builder setFinal(TBoolean _final) {
            this._final = _final;
            return this;
        }

        public Builder addTags(TTags tags) {
            if (tags == null || tags.getTag().isEmpty()) {
                return this;
            }

            if (this.tags == null) {
                this.tags = tags;
            } else {
                this.tags.getTag().addAll(tags.getTag());
            }
            return this;
        }

        public Builder addTags(List<TTag> tags) {
            if (tags == null) {
                return this;
            }

            TTags tmp = new TTags();
            tmp.getTag().addAll(tags);
            return addTags(tmp);
        }

        public Builder addTags(TTag tags) {
            if (tags == null) {
                return this;
            }

            TTags tmp = new TTags();
            tmp.getTag().add(tags);
            return addTags(tmp);
        }

        public Builder addTags(String name, String value) {
            if (name == null || name.isEmpty()) {
                return this;
            }

            TTag tmp = new TTag();
            tmp.setName(name);
            tmp.setValue(value);
            return addTags(tmp);
        }

        public Builder addRequiredContainerFeatures(TRequiredContainerFeatures requiredContainerFeatures) {
            if (requiredContainerFeatures == null || requiredContainerFeatures.getRequiredContainerFeature().isEmpty()) {
                return this;
            }

            if (this.requiredContainerFeatures == null) {
                this.requiredContainerFeatures = requiredContainerFeatures;
            } else {
                this.requiredContainerFeatures.getRequiredContainerFeature().addAll(requiredContainerFeatures.getRequiredContainerFeature());
            }
            return this;
        }

        public Builder addRequiredContainerFeatures(List<TRequiredContainerFeature> requiredContainerFeatures) {
            if (requiredContainerFeatures == null) {
                return this;
            }

            TRequiredContainerFeatures tmp = new TRequiredContainerFeatures();
            tmp.getRequiredContainerFeature().addAll(requiredContainerFeatures);
            return addRequiredContainerFeatures(tmp);
        }

        public Builder addRequiredContainerFeatures(TRequiredContainerFeature requiredContainerFeatures) {
            if (requiredContainerFeatures == null) {
                return this;
            }

            TRequiredContainerFeatures tmp = new TRequiredContainerFeatures();
            tmp.getRequiredContainerFeature().add(requiredContainerFeatures);
            return addRequiredContainerFeatures(tmp);
        }

        public Builder addImplementationArtifacts(TImplementationArtifacts implementationArtifacts) {
            if (implementationArtifacts == null || implementationArtifacts.getImplementationArtifact().isEmpty()) {
                return this;
            }

            if (this.implementationArtifacts == null) {
                this.implementationArtifacts = implementationArtifacts;
            } else {
                this.implementationArtifacts.getImplementationArtifact().addAll(implementationArtifacts.getImplementationArtifact());
            }
            return this;
        }

        public Builder addImplementationArtifacts(List<TImplementationArtifacts.ImplementationArtifact> implementationArtifacts) {
            if (implementationArtifacts == null) {
                return this;
            }

            TImplementationArtifacts tmp = new TImplementationArtifacts();
            tmp.getImplementationArtifact().addAll(implementationArtifacts);
            return addImplementationArtifacts(tmp);
        }

        public Builder addImplementationArtifacts(TImplementationArtifacts.ImplementationArtifact implementationArtifacts) {
            if (implementationArtifacts == null) {
                return this;
            }

            TImplementationArtifacts tmp = new TImplementationArtifacts();
            tmp.getImplementationArtifact().add(implementationArtifacts);
            return addImplementationArtifacts(tmp);
        }
    }
}
