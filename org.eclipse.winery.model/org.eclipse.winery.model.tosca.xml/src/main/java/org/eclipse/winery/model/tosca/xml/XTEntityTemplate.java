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
    XTArtifactTemplate.class,
    XTPolicyTemplate.class,
    XTCapability.class,
    XTRequirement.class,
    XTRelationshipTemplate.class,
    XTNodeTemplate.class
})
public abstract class XTEntityTemplate extends XHasId implements XHasType, XHasName {

    private static final Logger LOGGER = LoggerFactory.getLogger(XTEntityTemplate.class);

    @XmlElement(name = "Properties")
    protected XTEntityTemplate.Properties properties;

    @XmlElement(name = "PropertyConstraints")
    protected XTEntityTemplate.PropertyConstraints propertyConstraints;
    
    @XmlAttribute(name = "type", required = true)
    @NonNull
    protected QName type;

    @Deprecated // required for XML deserialization
    public XTEntityTemplate() {
        super();
    }

    public XTEntityTemplate(String id) {
        super(id);
    }

    public XTEntityTemplate(Builder builder) {
        super(builder);
        this.properties = builder.properties;
        this.propertyConstraints = builder.propertyConstraints;
        this.type = builder.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTEntityTemplate)) return false;
        if (!super.equals(o)) return false;
        XTEntityTemplate that = (XTEntityTemplate) o;
        return Objects.equals(properties, that.properties) &&
            Objects.equals(propertyConstraints, that.propertyConstraints) &&
            Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), properties, propertyConstraints, type);
    }

    public XTEntityTemplate.@Nullable Properties getProperties() {
        return properties;
    }

    public void setProperties(XTEntityTemplate.Properties value) {
        this.properties = value;
    }

    public XTEntityTemplate.@Nullable PropertyConstraints getPropertyConstraints() {
        return propertyConstraints;
    }

    public void setPropertyConstraints(XTEntityTemplate.@Nullable PropertyConstraints value) {
        this.propertyConstraints = value;
    }

    @NonNull
    public QName getType() {
        return type;
    }

    public void setType(@NonNull QName value) {
        this.type = value;
    }

    @Override
    @NonNull
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

        @Nullable
        public Object getAny() {
            return any;
        }

        public void setAny(Object value) {
            this.any = value;
        }

        @ADR(12)
        public void setKVProperties(Map<String, String> properties) {
            setKVProperties(Namespaces.EXAMPLE_NAMESPACE_URI, "Properties", properties);
        }

        @ADR(12)
        public void setKVProperties(@Nullable String namespace, @Nullable String elementName, Map<String, String> properties) {
            if (elementName == null || elementName.equals("")) {
                elementName = "Properties";
            }
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
        protected List<XTPropertyConstraint> propertyConstraint;

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
         * <p> Objects of the following type(s) are allowed in the list {@link XTPropertyConstraint }
         */
        @NonNull
        public List<XTPropertyConstraint> getPropertyConstraint() {
            if (propertyConstraint == null) {
                propertyConstraint = new ArrayList<XTPropertyConstraint>();
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
    public abstract static class Builder<T extends Builder<T>> extends XHasId.Builder<T> {
        private final QName type;
        private XTEntityTemplate.Properties properties;
        private XTEntityTemplate.PropertyConstraints propertyConstraints;

        public Builder(String id, QName type) {
            super(id);
            this.type = type;
        }

        public Builder(XTEntityTemplate entityTemplate) {
            super(entityTemplate);
            this.type = entityTemplate.getType();
            this.properties = entityTemplate.getProperties();
            this.addPropertyConstraints(entityTemplate.getPropertyConstraints());
        }

        public T setProperties(XTEntityTemplate.Properties properties) {
            this.properties = properties;
            return self();
        }

        public T setPropertyConstraints(XTEntityTemplate.PropertyConstraints propertyConstraints) {
            this.propertyConstraints = propertyConstraints;
            return self();
        }

        public T addPropertyConstraints(XTEntityTemplate.PropertyConstraints propertyConstraints) {
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

        public T addPropertyConstraints(List<XTPropertyConstraint> propertyConstraints) {
            if (propertyConstraints == null) {
                return self();
            }

            XTEntityTemplate.PropertyConstraints tmp = new XTEntityTemplate.PropertyConstraints();
            tmp.getPropertyConstraint().addAll(propertyConstraints);
            return addPropertyConstraints(tmp);
        }

        public T addPropertyConstraints(XTPropertyConstraint propertyConstraints) {
            if (propertyConstraints == null) {
                return self();
            }

            XTEntityTemplate.PropertyConstraints tmp = new XTEntityTemplate.PropertyConstraints();
            tmp.getPropertyConstraint().add(propertyConstraints);
            return addPropertyConstraints(tmp);
        }
    }
}
