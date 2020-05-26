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

package org.eclipse.winery.model.tosca;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.kvproperties.AttributeDefinitionList;
import org.eclipse.winery.model.tosca.kvproperties.ConstraintClauseKVList;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEntityType", propOrder = {
    "tags",
    "derivedFrom",
    "properties",
    "propertiesDefinition",
    "attributeDefinitions"
})
@XmlSeeAlso( {
    TNodeType.class,
    TRelationshipType.class,
    TRequirementType.class,
    TCapabilityType.class,
    TArtifactType.class,
    TPolicyType.class,
    TDataType.class,
})
public abstract class TEntityType extends TExtensibleElements implements HasName, HasInheritance, HasTargetNamespace {
    public static final String NS_SUFFIX_PROPERTIESDEFINITION_WINERY = "propertiesdefinition/winery";

    @XmlElement(name = "Tags")
    protected TTags tags;
    @XmlElement(name = "DerivedFrom")
    protected TEntityType.DerivedFrom derivedFrom;
    @XmlElement(name = "Properties")
    // FIXME unify this type of properties definition and the XML properties definition as well as the special 
    //  support for WineryKVPropertiesDefinition nonsense
    protected List<YamlPropertyDefinition> properties;
    @XmlElement(name = "PropertiesDefinition")
    protected XmlPropertiesDefinition propertiesDefinition;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "abstract")
    protected boolean _abstract;
    @XmlAttribute(name = "final")
    protected boolean _final;
    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    // added to support conversion from/to YAML
    protected AttributeDefinitionList attributeDefinitions;

    public TEntityType() {
    }

    public TEntityType(Builder builder) {
        super(builder);
        this.tags = builder.tags;
        this.derivedFrom = builder.derivedFrom;
        this.properties = builder.properties;
        this.name = builder.name;
        this._abstract = builder.abstractValue;
        this._final = builder.finalValue;
        this.targetNamespace = builder.targetNamespace;
        this.attributeDefinitions = builder.attributeDefinitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TEntityType)) return false;
        TEntityType that = (TEntityType) o;
        return Objects.equals(tags, that.tags) &&
            Objects.equals(derivedFrom, that.derivedFrom) &&
            Objects.equals(properties, that.properties) &&
            Objects.equals(name, that.name) &&
            _abstract == that._abstract &&
            _final == that._final &&
            Objects.equals(targetNamespace, that.targetNamespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tags, derivedFrom, properties, name, _abstract, _final, targetNamespace);
    }

    @Nullable
    public AttributeDefinitionList getAttributeDefinitions() {
        return attributeDefinitions;
    }

    public void setAttributeDefinitions(AttributeDefinitionList attributeDefinitions) {
        this.attributeDefinitions = attributeDefinitions;
    }

    @Nullable
    public TTags getTags() {
        return tags;
    }

    public void setTags(@Nullable TTags value) {
        this.tags = value;
    }

    public TEntityType.@Nullable DerivedFrom getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(@Nullable HasType value) {
        this.derivedFrom = (TEntityType.DerivedFrom) value;
    }

    // FIXME this is a bit of a mess, because we also have {@link #getProperties}
    @Nullable
    public XmlPropertiesDefinition getPropertiesDefinition() {
        return propertiesDefinition;
    }
    
    public void setPropertiesDefinition(@Nullable XmlPropertiesDefinition propertiesDefinition) {
        this.propertiesDefinition = propertiesDefinition;
    }
                                        
    // Must be nullable, because types are not required to define any properties if they inherit
    @Nullable
    public List<YamlPropertyDefinition> getProperties() {
        return properties;
    }
    
    public void setProperties(@Nullable List<YamlPropertyDefinition> properties) {
        this.properties = properties;
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

    @JsonIgnore
    @NonNull
    public QName getQName() {
        return QName.valueOf("{" + this.targetNamespace + "}" + this.name);
    }

    public boolean getAbstract() {
        return _abstract;
    }

    public void setAbstract(boolean value) {
        this._abstract = value;
    }

    public void setAbstract(@Nullable Boolean value) {
        this._abstract = value == null ? false : value;
    }

    public boolean getFinal() {
        return _final;
    }

    public void setFinal(boolean value) {
        this._final = value;
    }

    public void setFinal(@Nullable Boolean value) {
        this._final = value == null ? false : value;
    }

    @Nullable
    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(@Nullable String value) {
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
                ns += NS_SUFFIX_PROPERTIESDEFINITION_WINERY;
                res.setNamespace(ns);
            }
        }

        return res;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DerivedFrom implements HasType {

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
    public static class XmlPropertiesDefinition {
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
            XmlPropertiesDefinition that = (XmlPropertiesDefinition) o;
            return Objects.equals(element, that.element) &&
                Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element, type);
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class YamlPropertyDefinition {
        private String name;
        @XmlAttribute(name = "type", required = true)
        private QName type;
        private String description;
        private Boolean required;
        @XmlElement(name = "default")
        private Object defaultValue;
        private YamlPropertyDefinition.Status status;
        @XmlElement
        private ConstraintClauseKVList constraints;

        public YamlPropertyDefinition() {
            // added for xml serialization!
        }
        
        private YamlPropertyDefinition(Builder builder) {
            this.name = builder.name;
            this.type = builder.type;
            this.description = builder.description;
            this.required = builder.required;
            this.defaultValue = builder.defaultValue;
            this.status = builder.status;
            this.constraints = builder.constraints;
        }
        
        @XmlEnum(String.class)
        public enum Status {
            supported,
            unsupported,
            experimental,
            deprecated;
            
            @Nullable
            public static Status getStatus(String status) {
                // Could possibly be replaced by wrapping with Status.getValue(status)?
                return Arrays.stream(Status.values())
                    .filter(v -> status.equalsIgnoreCase(v.name()))
                    .findFirst()
                    .orElse(null);
            }
        }
        // FIXME introduce this
//        @XmlAttribute(name = "entry_schema")
//        private TEntrySchema entrySchema;

        public static class Builder {
            private String name;
            private QName type;
            private String description;
            private Boolean required;
            private Object defaultValue;
            private YamlPropertyDefinition.Status status;
            private ConstraintClauseKVList constraints;
            
            public Builder(String name) {
                this.name = name;
            }

            public Builder setName(String name) {
                this.name = name;
                return this;
            }

            public Builder setType(QName type) {
                this.type = type;
                return this;
            }

            public Builder setDescription(String description) {
                this.description = description;
                return this;
            }

            public Builder setRequired(Boolean required) {
                this.required = required;
                return this;
            }

            public Builder setDefaultValue(Object defaultValue) {
                this.defaultValue = defaultValue;
                return this;
            }

            public Builder setStatus(Status status) {
                this.status = status;
                return this;
            }

            public Builder setConstraints(ConstraintClauseKVList constraints) {
                this.constraints = constraints;
                return this;
            }
            
            public YamlPropertyDefinition build() {
                return new YamlPropertyDefinition(this);
            }
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public QName getType() {
            return type;
        }

        public void setType(QName type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public ConstraintClauseKVList getConstraints() {
            return constraints;
        }

        public void setConstraints(ConstraintClauseKVList constraints) {
            this.constraints = constraints;
        }
    }
    
    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends TExtensibleElements.Builder<T> {
        private final String name;

        private TTags tags;
        private TEntityType.DerivedFrom derivedFrom;
        private List<YamlPropertyDefinition> properties;
        private XmlPropertiesDefinition propertiesDefinition;
        private boolean abstractValue;
        private boolean finalValue;
        private String targetNamespace;
        private AttributeDefinitionList attributeDefinitions;

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
            this.properties = entityType.getProperties();
            this.attributeDefinitions = entityType.getAttributeDefinitions();
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

        public T setProperties(List<YamlPropertyDefinition> properties) {
            if (properties == null || properties.isEmpty()) {
                return self();
            }
            
            if (this.properties == null) {
                this.properties = properties;
                return self();
            }
            this.properties.addAll(properties);
            return self();
        }
        
        public T setProperties(YamlPropertyDefinition property) {
            if (property == null) {
                return self();
            }
            List<YamlPropertyDefinition> tmp = new ArrayList<>();
            tmp.add(property);
            return setProperties(tmp);
        }
        
        public T setPropertiesDefinition(XmlPropertiesDefinition propertiesDefinition) {
            this.propertiesDefinition = propertiesDefinition;
            return self();
        }

        public T setAbstract(boolean abstractValue) {
            this.abstractValue = abstractValue;
            return self();
        }

        public T setFinal(boolean finalValue) {
            this.finalValue = finalValue;
            return self();
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

        public T setAttributeDefinitions(AttributeDefinitionList attributeDefinitions) {
            this.attributeDefinitions = attributeDefinitions;
            return self();
        }

        public TEntityType build() {
            throw new IllegalStateException("Abstract types must never be build.");
        }
    }
}
