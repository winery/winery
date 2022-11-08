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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.jaxbsupport.map.PropertiesAdapter;
import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEntityTemplate", propOrder = {
    "properties",
    "propertyConstraints"
})
@XmlSeeAlso( {
    TArtifactTemplate.class,
    TPolicyTemplate.class,
    TCapability.class,
    TRequirement.class,
    TRelationshipTemplate.class,
    TNodeTemplate.class
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class TEntityTemplate extends HasId implements HasType, HasName {

    @XmlElement(name = "Properties")
    protected TEntityTemplate.Properties properties;

    @XmlElementWrapper(name = "PropertyConstraints")
    @XmlElement(name = "PropertyConstraint", required = true)
    protected List<TPropertyConstraint> propertyConstraints;

    // allow empty types to support YAML capability assignments
    @XmlAttribute(name = "type")
    protected QName type;

    @Deprecated // used for XML deserialization of API request content
    public TEntityTemplate() {
        super();
    }

    public TEntityTemplate(String id) {
        super(id);
    }

    public TEntityTemplate(Builder<?> builder) {
        super(builder);
        this.properties = builder.properties;
        this.propertyConstraints = builder.propertyConstraints;
        this.type = builder.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TEntityTemplate)) return false;
        if (!super.equals(o)) return false;
        TEntityTemplate that = (TEntityTemplate) o;
        return Objects.equals(properties, that.properties) &&
            Objects.equals(propertyConstraints, that.propertyConstraints) &&
            Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), properties, propertyConstraints, type);
    }

    public TEntityTemplate.@Nullable Properties getProperties() {
        return properties;
    }

    public void setProperties(TEntityTemplate.Properties value) {
        this.properties = value;
    }

    public List<TPropertyConstraint> getPropertyConstraints() {
        return propertyConstraints;
    }

    public void setPropertyConstraints(List<TPropertyConstraint> value) {
        this.propertyConstraints = value;
    }

    public QName getType() {
        return type;
    }

    public void setType(QName value) {
        this.type = value;
    }

    @Override
    @Nullable
    public QName getTypeAsQName() {
        return this.getType();
    }

    public abstract void accept(Visitor visitor);

    @JsonTypeInfo(defaultImpl = XmlProperties.class
        , include = JsonTypeInfo.As.EXTERNAL_PROPERTY
        , property = "propertyType"
        , use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes( {
        @JsonSubTypes.Type(value = XmlProperties.class, name = "XML"),
        @JsonSubTypes.Type(value = WineryKVProperties.class, name = "KV"),
        @JsonSubTypes.Type(value = YamlProperties.class, name = "YAML")
    })
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlType(name = "", namespace = Namespaces.TOSCA_NAMESPACE)
    // no XmlSeeAlso because types are remapped using XmlTypeAdapter and we don't want to expose LinkedHashMap to the
    //  JAXBContext This avoids introducing the namespaceURI "" into the JAXBContext
    // The adapter deals with the fact that two of the Properties implementations have an xml schema that depends on the
    //  runtime values stored inside the Maps they encapsulate.
    @XmlJavaTypeAdapter(PropertiesAdapter.class)
    @XmlRootElement
    public static abstract class Properties implements Serializable {

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", namespace = Namespaces.TOSCA_NAMESPACE)
    @XmlRootElement(name = "Properties", namespace = Namespaces.TOSCA_NAMESPACE)
    @XmlJavaTypeAdapter(value = PropertiesAdapter.class, type = XmlProperties.class)
    // Xml transformation is done by XmlJavaTypeAdapter, thus no XML configuration whatsoever
    public static class XmlProperties extends Properties {

        @XmlAnyElement(lax = true)
        protected Object any;

        @Nullable
        public Object getAny() {
            return any;
        }

        public void setAny(Object any) {
            this.any = any;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            XmlProperties that = (XmlProperties) o;
            return Objects.equals(any, that.any);
        }

        @Override
        public int hashCode() {
            return Objects.hash(any);
        }
    }

    @XmlJavaTypeAdapter(value = PropertiesAdapter.class, type = Properties.class)
    @XmlType(name = "", namespace = Namespaces.TOSCA_NAMESPACE)
    // Xml transformation is done by XmlJavaTypeAdapter, thus no XML configuration whatsoever
    public static class WineryKVProperties extends Properties {

        @Nullable
        private String namespace = null;

        @Nullable
        private String elementName = null;

        @NonNull
        private LinkedHashMap<String, String> KVProperties = new LinkedHashMap<>();

        @NonNull
        public LinkedHashMap<String, String> getKVProperties() {
            return KVProperties;
        }

        public void setKVProperties(@NonNull LinkedHashMap<String, String> KVProperties) {
            this.KVProperties = KVProperties;
        }

        @Nullable
        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(@Nullable String namespace) {
            this.namespace = namespace;
        }

        @Nullable
        public String getElementName() {
            return elementName;
        }

        public void setElementName(@Nullable String elementName) {
            this.elementName = elementName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WineryKVProperties that = (WineryKVProperties) o;
            return Objects.equals(namespace, that.namespace) &&
                Objects.equals(elementName, that.elementName) &&
                Objects.equals(KVProperties, that.KVProperties);
        }

        @Override
        public int hashCode() {
            return Objects.hash(namespace, elementName, KVProperties);
        }
    }

    @XmlType(name = "", namespace = Namespaces.TOSCA_NAMESPACE)
    @XmlJavaTypeAdapter(value = PropertiesAdapter.class, type = Properties.class)
    // Xml transformation is done by XmlJavaTypeAdapter, thus no XML configuration whatsoever
    @NonNullByDefault
    public static class YamlProperties extends Properties {

        @JsonInclude()// defaults to always include to override ObjectMapper's NON_NULL specification
        private LinkedHashMap<String, Object> properties = new LinkedHashMap<>();

        public LinkedHashMap<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(LinkedHashMap<String, Object> properties) {
            this.properties = properties;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            YamlProperties that = (YamlProperties) o;
            return Objects.equals(properties, that.properties);
        }

        @Override
        public int hashCode() {
            return Objects.hash(properties);
        }
    }

    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends HasId.Builder<T> {
        private final QName type;
        private TEntityTemplate.Properties properties;
        private List<TPropertyConstraint> propertyConstraints;

        public Builder(String id, QName type) {
            super(id);
            this.type = type;
        }

        public Builder(TEntityTemplate entityTemplate) {
            super(entityTemplate);
            this.type = entityTemplate.getType();
            this.properties = entityTemplate.getProperties();
            this.addPropertyConstraints(entityTemplate.getPropertyConstraints());
        }

        public T setProperties(TEntityTemplate.Properties properties) {
            this.properties = properties;
            return self();
        }

        public T setPropertyConstraints(List<TPropertyConstraint> propertyConstraints) {
            this.propertyConstraints = propertyConstraints;
            return self();
        }

        public T addPropertyConstraints(List<TPropertyConstraint> propertyConstraints) {
            if (propertyConstraints == null || propertyConstraints.isEmpty()) {
                return self();
            }

            if (this.propertyConstraints == null) {
                this.propertyConstraints = propertyConstraints;
            } else {
                this.propertyConstraints.addAll(propertyConstraints);
            }
            return self();
        }

        public T addPropertyConstraint(TPropertyConstraint propertyConstraints) {
            if (propertyConstraints == null) {
                return self();
            }

            List<TPropertyConstraint> tmp = new ArrayList<>();
            tmp.add(propertyConstraints);
            return addPropertyConstraints(tmp);
        }
    }
}
