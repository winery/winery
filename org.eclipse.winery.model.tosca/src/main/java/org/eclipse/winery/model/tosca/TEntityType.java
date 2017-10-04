/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial code generation using vhudson-jaxb-ri-2.1-2
 *    Christoph Kleine - hashcode, equals, builder pattern, Nullable and NonNull annotations
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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.propertydefinitionkv.WinerysPropertiesDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;


/**
 * <p>Java class for tEntityType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tEntityType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="Tags" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tTags" minOccurs="0"/>
 *         &lt;element name="DerivedFrom" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PropertiesDefinition" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="element" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="abstract" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tBoolean" default="no" />
 *       &lt;attribute name="final" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tBoolean" default="no" />
 *       &lt;attribute name="targetNamespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEntityType", propOrder = {
        "tags",
        "derivedFrom",
        "propertiesDefinition"
})
@XmlSeeAlso({
        TNodeType.class,
        TRelationshipType.class,
        TRequirementType.class,
        TCapabilityType.class,
        TArtifactType.class,
        TPolicyType.class
})
public class TEntityType extends TExtensibleElements implements HasName, HasInheritance, HasTargetNamespace {
    @XmlElement(name = "Tags")
    protected TTags tags;
    @XmlElement(name = "DerivedFrom")
    protected TEntityType.DerivedFrom derivedFrom;
    @XmlElement(name = "PropertiesDefinition")
    protected TEntityType.PropertiesDefinition propertiesDefinition;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "abstract")
    protected TBoolean _abstract;
    @XmlAttribute(name = "final")
    protected TBoolean _final;
    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    public TEntityType() {
    }

    public TEntityType(Builder builder) {
        super(builder);
        this.tags = builder.tags;
        this.derivedFrom = builder.derivedFrom;
        this.propertiesDefinition = builder.propertiesDefinition;
        this.name = builder.name;
        this._abstract = builder._abstract;
        this._final = builder._final;
        this.targetNamespace = builder.targetNamespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TEntityType)) return false;
        TEntityType that = (TEntityType) o;
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

    /**
     * Gets the value of the tags property.
     *
     * @return possible object is {@link TTags }
     */
    @Nullable
    public TTags getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     *
     * @param value allowed object is {@link TTags }
     */
    public void setTags(TTags value) {
        this.tags = value;
    }

    /**
     * Gets the value of the derivedFrom property.
     *
     * @return possible object is {@link TEntityType.DerivedFrom }
     */
    /*@Nullable*/
    public TEntityType.DerivedFrom getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * Sets the value of the derivedFrom property.
     *
     * @param value allowed object is {@link TEntityType.DerivedFrom }
     */
    public void setDerivedFrom(HasType value) {
        this.derivedFrom = (TEntityType.DerivedFrom) value;
    }

    /**
     * Gets the value of the propertiesDefinition property.
     *
     * @return possible object is {@link TEntityType.PropertiesDefinition }
     */
    /*@Nullable*/
    public TEntityType.PropertiesDefinition getPropertiesDefinition() {
        return propertiesDefinition;
    }

    /**
     * Sets the value of the propertiesDefinition property.
     *
     * @param value allowed object is {@link TEntityType.PropertiesDefinition }
     */
    public void setPropertiesDefinition(TEntityType.PropertiesDefinition value) {
        this.propertiesDefinition = value;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

    @NonNull
    public TBoolean getAbstract() {
        if (_abstract == null) {
            return TBoolean.NO;
        } else {
            return _abstract;
        }
    }

    /**
     * Sets the value of the abstract property.
     *
     * @param value allowed object is {@link TBoolean }
     */
    public void setAbstract(TBoolean value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the final property.
     *
     * @return possible object is {@link TBoolean }
     */
    @NonNull
    public TBoolean getFinal() {
        if (_final == null) {
            return TBoolean.NO;
        } else {
            return _final;
        }
    }

    /**
     * Sets the value of the final property.
     *
     * @param value allowed object is {@link TBoolean }
     */
    public void setFinal(TBoolean value) {
        this._final = value;
    }

    /**
     * Gets the value of the targetNamespace property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Sets the value of the targetNamespace property.
     *
     * @param value allowed object is {@link String }
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

    /**
     * This is a special method for Winery. Winery allows to define a property definition by specifying name/type
     * values. Instead of parsing the extensible elements returned TDefinitions, this method is a convenience method to
     * access this information
     *
     * @return a WinerysPropertiesDefinition object, which includes a map of name/type-pairs denoting the associated
     * property definitions. A default element name and namespace is added if it is not defined in the underlying XML.
     * null if no Winery specific KV properties are defined for the given entity type
     */
    @XmlTransient
    @JsonIgnore
    public WinerysPropertiesDefinition getWinerysPropertiesDefinition() {
        // similar implementation as org.eclipse.winery.repository.resources.entitytypes.properties.PropertiesDefinitionResource.getListFromEntityType(TEntityType)
        WinerysPropertiesDefinition res = null;
        for (Object o : this.getAny()) {
            if (o instanceof WinerysPropertiesDefinition) {
                res = (WinerysPropertiesDefinition) o;
            }
        }

        if (res != null) {
            // we put defaults if elementname and namespace have not been set

            if (res.getElementName() == null) {
                res.setElementName("Properties");
            }

            if (res.getNamespace() == null) {
                // we use the targetnamespace of the original element
                String ns = this.getTargetNamespace();
                if (!ns.endsWith("/")) {
                    ns += "/";
                }
                ns += "propertiesdefinition/winery";
                res.setNamespace(ns);
            }
        }

        return res;
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DerivedFrom implements HasType {

        @XmlAttribute(name = "typeRef", required = true)
        protected QName typeRef;

        /**
         * Gets the value of the typeRef property.
         *
         * @return possible object is {@link QName }
         */
        @NonNull
        public QName getTypeRef() {
            return typeRef;
        }

        /**
         * Sets the value of the typeRef property.
         *
         * @param value allowed object is {@link QName }
         */
        public void setTypeRef(QName value) {
            this.typeRef = value;
        }

        public QName getType() {
            return this.getTypeRef();
        }

        @Override
        public QName getTypeAsQName() {
            return this.getType();
        }

        @Override
        public void setType(QName type) {
            this.setTypeRef(type);
        }
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="element" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PropertiesDefinition {

        @XmlAttribute(name = "element")
        protected QName element;
        @XmlAttribute(name = "type")
        protected QName type;

        /**
         * Gets the value of the element property.
         *
         * @return possible object is {@link QName }
         */
        @Nullable
        public QName getElement() {
            return element;
        }

        /**
         * Sets the value of the element property.
         *
         * @param value allowed object is {@link QName }
         */
        public void setElement(QName value) {
            this.element = value;
        }

        /**
         * Gets the value of the type property.
         *
         * @return possible object is {@link QName }
         */
        @Nullable
        public QName getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         *
         * @param value allowed object is {@link QName }
         */
        public void setType(QName value) {
            this.type = value;
        }
    }

    public static class Builder extends TExtensibleElements.Builder {
        private final String name;

        private TTags tags;
        private TEntityType.DerivedFrom derivedFrom;
        private TEntityType.PropertiesDefinition propertiesDefinition;
        private TBoolean _abstract;
        private TBoolean _final;
        private String targetNamespace;

        public Builder(String name) {
            this.name = name;
        }

        public Builder(TEntityType entityType) {
            super(entityType);
            this.name = entityType.getName();
            this.derivedFrom = entityType.getDerivedFrom();
            this.addTags(entityType.getTags());
            this._abstract = entityType.getAbstract();
            this._final = entityType.getFinal();
            this.targetNamespace = entityType.getTargetNamespace();
            this.propertiesDefinition = entityType.getPropertiesDefinition();
        }

        public Builder setTags(TTags tags) {
            this.tags = tags;
            return this;
        }

        public Builder setDerivedFrom(TEntityType.DerivedFrom derivedFrom) {
            this.derivedFrom = derivedFrom;
            return this;
        }

        public Builder setDerivedFrom(QName derivedFrom) {
            if (derivedFrom == null) {
                return this;
            }

            if (this.derivedFrom == null) {
                this.derivedFrom = new TEntityType.DerivedFrom();
            }
            this.derivedFrom.setTypeRef(derivedFrom);
            return this;
        }

        public Builder setDerivedFrom(String derivedFrom) {
            if (derivedFrom == null || derivedFrom.length() == 0) {
                return this;
            }

            return setDerivedFrom(new QName(derivedFrom));
        }

        public Builder setPropertiesDefinition(PropertiesDefinition propertiesDefinition) {
            this.propertiesDefinition = propertiesDefinition;
            return this;
        }

        public Builder setAbstract(TBoolean _abstract) {
            this._abstract = _abstract;
            return this;
        }

        public Builder setAbstract(Boolean _abstract) {
            if (this._abstract == null) {
                return this;
            }

            return setAbstract(_abstract ? TBoolean.YES : TBoolean.NO);
        }

        public Builder setFinal(TBoolean _final) {
            this._final = _final;
            return this;
        }

        public Builder setFinal(Boolean _final) {
            if (this._final == null) {
                return this;
            }

            return setFinal(_final ? TBoolean.YES : TBoolean.NO);
        }

        public Builder setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
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

        public Builder addTags(String key, String value) {
            if (value == null) {
                return this;
            }

            TTag tag = new TTag();
            tag.setName(key);
            tag.setValue(value);
            return this.addTags(tag);
        }

        public TEntityType build() {
            return new TEntityType(this);
        }
    }
}
