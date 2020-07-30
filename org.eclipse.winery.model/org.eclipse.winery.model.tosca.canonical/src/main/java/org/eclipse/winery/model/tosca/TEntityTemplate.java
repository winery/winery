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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(TEntityTemplate.class);

    @XmlElement(name = "Properties")
    protected TEntityTemplate.Properties properties;

    @XmlElement(name = "PropertyConstraints")
    protected TEntityTemplate.PropertyConstraints propertyConstraints;

    // allow empty types to support YAML capability assignments
    @XmlAttribute(name = "type")
    protected QName type;

    public TEntityTemplate() {
        super();
    }

    public TEntityTemplate(String id) {
        super(id);
    }

    public TEntityTemplate(Builder builder) {
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

    public TEntityTemplate.@Nullable PropertyConstraints getPropertyConstraints() {
        return propertyConstraints;
    }

    public void setPropertyConstraints(TEntityTemplate.@Nullable PropertyConstraints value) {
        this.propertyConstraints = value;
    }

    @Nullable
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
    @JsonSubTypes({
        @JsonSubTypes.Type(value = XmlProperties.class, name = "XML"),
        @JsonSubTypes.Type(value = WineryKVProperties.class, name = "KV"),
        @JsonSubTypes.Type(value = YamlProperties.class, name = "YAML")
    })
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlType(name = "")
    @XmlSeeAlso({
        XmlProperties.class,
        WineryKVProperties.class,
        YamlProperties.class
    })
    public static abstract class Properties implements Serializable {
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
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
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "KVProperties"
    })
    @NonNullByDefault
    public static class WineryKVProperties extends Properties {
        @XmlElement(name = "kvproperties")
        private LinkedHashMap<String, String> KVProperties = new LinkedHashMap<>();

        public LinkedHashMap<String, String> getKVProperties() {
            return KVProperties;
        }

        public void setKVProperties(LinkedHashMap<String, String> KVProperties) {
            this.KVProperties = KVProperties;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "properties"
    })
    @NonNullByDefault
    public static class YamlProperties extends Properties {
        
        @XmlElement(name = "yamlproperties")
        @JsonInclude()// defaults to always include to override ObjectMapper's NON_NULL specification
        private LinkedHashMap<String, Object> properties = new LinkedHashMap<>();

        public LinkedHashMap<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(LinkedHashMap<String, Object> properties) {
            this.properties = properties;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "propertyConstraint"
    })
    public static class PropertyConstraints implements Serializable {

        @XmlElement(name = "PropertyConstraint", required = true)
        protected List<TPropertyConstraint> propertyConstraint;

        /**
         * Gets the value of the propertyConstraint property.
         * <p>
         * <p> This accessor method returns a reference to the live list, not a snapshot. Therefore any modification
         * you make to the returned list will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the propertyConstraint property.
         * <p>
         * <p> For example, to add a new item, do as follows:
         * <pre>
         *    getPropertyConstraint().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p> Objects of the following type(s) are allowed in the list {@link TPropertyConstraint }
         */
        @NonNull
        public List<TPropertyConstraint> getPropertyConstraint() {
            if (propertyConstraint == null) {
                propertyConstraint = new ArrayList<TPropertyConstraint>();
            }
            return this.propertyConstraint;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PropertyConstraints that = (PropertyConstraints) o;
            return Objects.equals(propertyConstraint, that.propertyConstraint);
        }

        @Override
        public int hashCode() {
            return Objects.hash(propertyConstraint);
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends HasId.Builder<T> {
        private final QName type;
        private TEntityTemplate.Properties properties;
        private TEntityTemplate.PropertyConstraints propertyConstraints;

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

        public T setPropertyConstraints(TEntityTemplate.PropertyConstraints propertyConstraints) {
            this.propertyConstraints = propertyConstraints;
            return self();
        }

        public T addPropertyConstraints(TEntityTemplate.PropertyConstraints propertyConstraints) {
            if (propertyConstraints == null || propertyConstraints.getPropertyConstraint().isEmpty()) {
                return self();
            }

            if (this.propertyConstraints == null) {
                this.propertyConstraints = propertyConstraints;
            } else {
                this.propertyConstraints.getPropertyConstraint().addAll(propertyConstraints.getPropertyConstraint());
            }
            return self();
        }

        public T addPropertyConstraints(List<TPropertyConstraint> propertyConstraints) {
            if (propertyConstraints == null) {
                return self();
            }

            TEntityTemplate.PropertyConstraints tmp = new TEntityTemplate.PropertyConstraints();
            tmp.getPropertyConstraint().addAll(propertyConstraints);
            return addPropertyConstraints(tmp);
        }

        public T addPropertyConstraints(TPropertyConstraint propertyConstraints) {
            if (propertyConstraints == null) {
                return self();
            }

            TEntityTemplate.PropertyConstraints tmp = new TEntityTemplate.PropertyConstraints();
            tmp.getPropertyConstraint().add(propertyConstraints);
            return addPropertyConstraints(tmp);
        }
    }
}
