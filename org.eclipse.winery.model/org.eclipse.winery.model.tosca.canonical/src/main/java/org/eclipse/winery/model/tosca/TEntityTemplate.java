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
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

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

    @NonNull
    public QName getType() {
        return type;
    }

    public void setType(QName value) {
        this.type = value;
    }

    @Override
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
    public static abstract class Properties implements Serializable {
//        @ADR(12)
//        public void setKVProperties(String namespace, String elementName, Map<String, Object> properties) {
//            Objects.requireNonNull(properties);
//            
//            // properties are a complex map, so we must be reading a YAML assignment
//            if (properties.values().stream().anyMatch(v -> !(v instanceof String))) {
//                this.any = properties;
//                return;
//            }
//            
//            if (any == null) {
//                // special case if JSON is parsed without updating an existing element
//                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//                DocumentBuilder db;
//                try {
//                    db = dbf.newDocumentBuilder();
//                } catch (ParserConfigurationException e) {
//                    LOGGER.debug(e.getMessage(), e);
//                    throw new IllegalStateException("Could not instantiate document builder", e);
//                }
//                Document doc = db.newDocument();
//
//                Element root = doc.createElementNS(namespace, elementName);
//                doc.appendChild(root);
//
//                // No wpd - so this is not possible:
//                // we produce the serialization in the same order the XSD would be generated (because of the usage of xsd:sequence)
//                // for (PropertyDefinitionKV prop : wpd.getPropertyDefinitionKVList()) {
//
//                for (String key : properties.keySet()) {
//                    Element element = doc.createElementNS(namespace, key);
//                    root.appendChild(element);
//                    
//                    Object value = properties.get(key);
//                    if (value == null) { continue; }
//                    if (value instanceof String) {
//                        Text text = doc.createTextNode((String)value);
//                        element.appendChild(text);
//                    } else {
//                        // FIXME value is complex type
//                    }
//                }
//
//                this.setAny(doc.getDocumentElement());
//            } else if (any instanceof Element){
//                // TODO: this implementation does not support adding a new property.
//                //  However, I don't understand it yet so we need to fix it in future.
//
//                Element el = (Element) any;
//                // straight-forward copy over to existing property structure
//                NodeList childNodes = el.getChildNodes();
//
//                for (int i = 0; i < childNodes.getLength(); i++) {
//                    Node item = childNodes.item(i);
//                    if (item instanceof Element) {
//                        final Element element = (Element) item;
//                        final String key = element.getLocalName();
//                        final Object value = properties.get(key);
//                        if (value instanceof String) {
//                            element.setTextContent((String)value);
//                        } else {
//                            // FIXME Value is complex type
//                        }
//                    } else if (item instanceof Text || item instanceof Comment) {
//                        // these kinds of nodes are OK
//                    } else {
//                        LOGGER.warn("Trying to read k/v property from a template which does not follow the k/v scheme.");
//                    }
//                }
//            } else {
//                LOGGER.warn("Failed to set properties on stored yaml properties??");
//            }
//        }
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
        "kvProperties"
    })
    @NonNullByDefault
    public static class WineryKVProperties extends Properties {
        @XmlElement(name = "kvproperties")
        private LinkedHashMap<String, String> kvProperties = new LinkedHashMap<>();

        public LinkedHashMap<String, String> getKvProperties() {
            return kvProperties;
        }

        public void setKvProperties(LinkedHashMap<String, String> kvProperties) {
            this.kvProperties = kvProperties;
        }
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "properties"
    })
    @NonNullByDefault
    public static class YamlProperties extends Properties {
        
        @XmlElement(name = "yamlproperties")
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
