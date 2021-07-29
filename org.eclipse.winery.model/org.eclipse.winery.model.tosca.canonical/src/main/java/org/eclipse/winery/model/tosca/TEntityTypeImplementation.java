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

import java.util.ArrayList;
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

import org.eclipse.winery.model.jaxbsupport.map.BooleanToYesNo;
import org.eclipse.winery.model.jsonsupport.YesNo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso( {
    TNodeTypeImplementation.class,
    TRelationshipTypeImplementation.class,
    TRequiredContainerFeature.class,
    TImplementationArtifact.class
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class TEntityTypeImplementation extends TExtensibleElementWithTags implements HasName, HasType, HasInheritance, HasTargetNamespace {

    @XmlElementWrapper(name = "RequiredContainerFeatures")
    @XmlElement(name = "RequiredContainerFeature", required = true)
    protected List<TRequiredContainerFeature> requiredContainerFeatures;

    @XmlElementWrapper(name = "ImplementationArtifacts")
    @XmlElement(name = "ImplementationArtifact", required = true)
    protected List<TImplementationArtifact> implementationArtifacts;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    @XmlAttribute(name = "abstract")
    @XmlJavaTypeAdapter(type = boolean.class, value = BooleanToYesNo.class)
    @JsonProperty("abstract")
    @JsonSerialize(using = YesNo.Serializer.class)
    @JsonDeserialize(using = YesNo.Deserializer.class)
    protected boolean _abstract;

    @XmlAttribute(name = "final")
    @XmlJavaTypeAdapter(type = boolean.class, value = BooleanToYesNo.class)
    @JsonProperty("final")
    @JsonSerialize(using = YesNo.Serializer.class)
    @JsonDeserialize(using = YesNo.Deserializer.class)
    protected boolean _final;

    @XmlTransient
    protected QName implementedType;

    @Deprecated // used for XML deserialization of API request content
    public TEntityTypeImplementation() {
        super();
    }

    public TEntityTypeImplementation(Builder<?> builder) {
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

    @JsonIgnore
    public QName getQName() {
        return QName.valueOf("{" + this.targetNamespace + "}" + this.name);
    }

    @Nullable
    public List<TRequiredContainerFeature> getRequiredContainerFeatures() {
        return requiredContainerFeatures;
    }

    public void setRequiredContainerFeatures(List<TRequiredContainerFeature> value) {
        this.requiredContainerFeatures = value;
    }

    @Nullable
    public List<TImplementationArtifact> getImplementationArtifacts() {
        return implementationArtifacts;
    }

    public void setImplementationArtifacts(List<TImplementationArtifact> value) {
        this.implementationArtifacts = value;
    }

    public boolean getAbstract() {
        return _abstract;
    }

    public void setAbstract(boolean value) {
        this._abstract = value;
    }

    public void setAbstract(@Nullable Boolean value) {
        this._abstract = value != null && value;
    }

    public boolean getFinal() {
        return _final;
    }

    public void setFinal(boolean value) {
        this._final = value;
    }

    public void setFinal(@Nullable Boolean value) {
        this._final = value != null && value;
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

    public static abstract class Builder<T extends Builder<T>> extends TExtensibleElementWithTags.Builder<T> {

        private final QName implementedType;
        private String name;
        private String targetNamespace;
        private List<TRequiredContainerFeature> requiredContainerFeatures;
        private List<TImplementationArtifact> implementationArtifacts;
        private boolean _abstract;
        private boolean _final;

        public Builder(Builder<T> builder, String name, QName implementedType) {
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
            this.name = name;
            this.implementedType = implementedType;
        }

        public T setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
            return self();
        }

        public T setImplementationArtifacts(List<TImplementationArtifact> implementationArtifacts) {
            this.implementationArtifacts = implementationArtifacts;
            return self();
        }

        public T setName(String name) {
            this.name = name;
            return self();
        }

        public T setAbstract(boolean _abstract) {
            this._abstract = _abstract;
            return self();
        }

        public T setFinal(boolean _final) {
            this._final = _final;
            return self();
        }

        public T addRequiredContainerFeatures(List<TRequiredContainerFeature> requiredContainerFeatures) {
            if (requiredContainerFeatures == null || requiredContainerFeatures.isEmpty()) {
                return self();
            }

            if (this.requiredContainerFeatures == null) {
                this.requiredContainerFeatures = requiredContainerFeatures;
            } else {
                this.requiredContainerFeatures.addAll(requiredContainerFeatures);
            }
            return self();
        }

        public T addRequiredContainerFeature(TRequiredContainerFeature requiredContainerFeature) {
            if (requiredContainerFeature == null) {
                return self();
            }

            List<TRequiredContainerFeature> tmp = new ArrayList<>();
            tmp.add(requiredContainerFeature);
            return addRequiredContainerFeatures(tmp);
        }

        public T addImplementationArtifacts(List<TImplementationArtifact> implementationArtifacts) {
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

        public T addImplementationArtifact(TImplementationArtifact implementationArtifact) {
            if (implementationArtifact == null) {
                return self();
            }

            ArrayList<TImplementationArtifact> tmp = new ArrayList<>();
            tmp.add(implementationArtifact);
            return addImplementationArtifacts(tmp);
        }
    }
}
