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

import org.eclipse.winery.model.tosca.xml.constants.Namespaces;
import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
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

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class Properties implements Serializable {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Returns the XML element from the other namespace. In case the properties are of the form key/value, null is
         * returned.
         */
        @Nullable
        public Object getAny() {
            if (this.getKVProperties() == null) {
                return any;
            } else {
                return null;
            }
        }

        /**
         * Returns the internal any object without any K/V treatment. Required to patch JSON data received by clients
         */
        @Nullable
        @JsonIgnore
        public Object getInternalAny() {
            return any;
        }

        public void setAny(Object value) {
            this.any = value;
        }

        /**
         * This is a special method for Winery. Winery allows to define a property by specifying name/value values.
         * Instead of parsing the XML contained in TNodeType, this method is a convenience method to access this
         * information assumes the properties are key/value pairs (see WinerysPropertiesDefinition), all other cases are
         * return null.
         * <p>
         * Returns a map of key/values of this template based on the information of WinerysPropertiesDefinition. In case
         * no value is set, the empty string is used. The map is implemented as {@link LinkedHashMap} to ensure that the
         * order of the elements is the same as in the XML. We return the type {@link LinkedHashMap}, because there is
         * no appropriate Java interface for "sorted" Maps
         * <p>
         * In case the element is not of the form k/v, null is returned
         * <p>
         * This method assumes that the any field is always populated.
         *
         * @return null if not k/v, a map of k/v properties otherwise
         */
        @ADR(12)
        @Nullable
        public LinkedHashMap<String, String> getKVProperties() {
            // we use the internal variable "any", because getAny() returns null, if we have KVProperties
            if (any == null) {
                return null;
            }

            if (!(any instanceof Element)) {
                LOGGER.error("Corrupt storage - any should be null or instanceof Element");
                return null;
            }

            Element el = (Element) any;
            if (el == null) {
                return null;
            }

            // we have no type information in this place
            // we could inject a repository, but if Winery is used with multiple repositories, this could cause race conditions
            // therefore, we guess at the instance of the properties definition (i.e., here) if it is key/value or not.

            boolean isKv = true;

            LinkedHashMap<String, String> properties = new LinkedHashMap<>();

            NodeList childNodes = el.getChildNodes();

            if (childNodes.getLength() == 0) {
                // somehow invalid XML - do not treat it as k/v
                return null;
            }

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                if (item instanceof Element) {
                    String key = item.getLocalName();
                    String value;

                    Element kvElement = (Element) item;
                    NodeList kvElementChildNodes = kvElement.getChildNodes();
                    if (kvElementChildNodes.getLength() == 0) {
                        value = "";
                    } else if (kvElementChildNodes.getLength() > 1) {
                        // This is a wrong guess if comments are used, but this is prototype
                        isKv = false;
                        break;
                    } else {
                        // one child - just get the text.
                        value = item.getTextContent();
                    }
                    properties.put(key, value);
                } else if (item instanceof Text || item instanceof Comment) {
                    // these kinds of nodes are OK
                } else {
                    LOGGER.error("Trying to set k/v property on a template which does not follow the k/v scheme.");
                }
            }

            if (isKv) {
                return properties;
            } else {
                return null;
            }
        }

        @ADR(12)
        public void setKVProperties(Map<String, String> properties) {
            setKVProperties(Namespaces.EXAMPLE_NAMESPACE_URI, "Properties", properties);
        }

        @ADR(12)
        public void setKVProperties(String namespace, String elementName, Map<String, String> properties) {
            Objects.requireNonNull(properties);
            Element el = (Element) any;

            if (el == null) {
                // special case if JSON is parsed without updating an existing element
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db;
                try {
                    db = dbf.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    LOGGER.debug(e.getMessage(), e);
                    throw new IllegalStateException("Could not instantiate document builder", e);
                }
                Document doc = db.newDocument();

                Element root = doc.createElementNS(namespace, elementName);
                doc.appendChild(root);

                // No wpd - so this is not possible:
                // we produce the serialization in the same order the XSD would be generated (because of the usage of xsd:sequence)
                // for (PropertyDefinitionKV prop : wpd.getPropertyDefinitionKVList()) {

                for (String key : properties.keySet()) {
                    Element element = doc.createElementNS(namespace, key);
                    root.appendChild(element);
                    String value = properties.get(key);
                    if (value != null) {
                        Text text = doc.createTextNode(value);
                        element.appendChild(text);
                    }
                }

                this.setAny(doc.getDocumentElement());
            } else {
                //TODO: this implementation does not support adding a new property. However, I don't understand it yet so we need to fix it in future.

                // straight-forward copy over to existing property structure
                NodeList childNodes = el.getChildNodes();

                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node item = childNodes.item(i);
                    if (item instanceof Element) {
                        final Element element = (Element) item;
                        final String key = element.getLocalName();
                        final String value = properties.get(key);
                        if (value != null) {
                            element.setTextContent(value);
                        }
                    } else if (item instanceof Text || item instanceof Comment) {
                        // these kinds of nodes are OK
                    } else {
                        LOGGER.warn("Trying to read k/v property from a template which does not follow the k/v scheme.");
                    }
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Properties that = (Properties) o;
            return Objects.equals(any, that.any);
        }

        @Override
        public int hashCode() {
            return Objects.hash(any);
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
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
