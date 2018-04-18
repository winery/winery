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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Objects;


/**
 * <p>Java class for tEntityType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
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
@XmlSeeAlso( {
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
        this._abstract = builder.abstractValue;
        this._final = builder.finalValue;
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
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
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
        public void setType(QName type) {
            this.setTypeRef(type);
        }

        @Override
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


    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
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
    }

    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends TExtensibleElements.Builder<T> {
        private final String name;

        private TTags tags;
        private TEntityType.DerivedFrom derivedFrom;
        private TEntityType.PropertiesDefinition propertiesDefinition;
        private TBoolean abstractValue;
        private TBoolean finalValue;
        private String targetNamespace;

        public Builder(String name) {
            this.name = name;
        }

        public Builder(TEntityType entityType) {
            super(entityType);
            this.name = entityType.getName();
            this.derivedFrom = entityType.getDerivedFrom();
            this.addTags(entityType.getTags());
            this.abstractValue = entityType.getAbstract();
            this.finalValue = entityType.getFinal();
            this.targetNamespace = entityType.getTargetNamespace();
            this.propertiesDefinition = entityType.getPropertiesDefinition();
        }

        public T setTags(TTags tags) {
            this.tags = tags;
            return self();
        }

        public T setDerivedFrom(TEntityType.DerivedFrom derivedFrom) {
            this.derivedFrom = derivedFrom;
            return self();
        }

        public T setDerivedFrom(QName derivedFrom) {
            if (derivedFrom == null) {
                return self();
            }

            if (this.derivedFrom == null) {
                this.derivedFrom = new TEntityType.DerivedFrom();
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

        public T setAbstract(TBoolean abstractValue) {
            this.abstractValue = abstractValue;
            return self();
        }

        public T setAbstract(Boolean abstractValue) {
            if (this.abstractValue == null) {
                return self();
            }

            return setAbstract(abstractValue ? TBoolean.YES : TBoolean.NO);
        }

        public T setFinal(TBoolean finalValue) {
            this.finalValue = finalValue;
            return self();
        }

        public T setFinal(Boolean finalValue) {
            if (this.finalValue == null) {
                return self();
            }

            return setFinal(finalValue ? TBoolean.YES : TBoolean.NO);
        }

        public T setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
            return self();
        }

        public T addTags(TTags tags) {
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

        public T addTags(List<TTag> tags) {
            if (tags == null) {
                return self();
            }

            TTags tmp = new TTags();
            tmp.getTag().addAll(tags);
            return addTags(tmp);
        }

        public T addTags(TTag tags) {
            if (tags == null) {
                return self();
            }

            TTags tmp = new TTags();
            tmp.getTag().add(tags);
            return addTags(tmp);
        }

        public T addTags(String key, String value) {
            if (value == null) {
                return self();
            }

            TTag tag = new TTag();
            tag.setName(key);
            tag.setValue(value);
            return addTags(tag);
        }

        public TEntityType build() {
            return new TEntityType(this);
        }
    }
}
