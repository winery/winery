/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

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

import com.fasterxml.jackson.annotation.JsonInclude;
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


/**
 * Java class for tEntityTemplate complex type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEntityTemplate", propOrder = {
    "properties",
    "propertyConstraints"
})
@XmlSeeAlso({
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

    @XmlAttribute(name = "type", required = true)
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

    /**
     * Gets the value of the properties property.
     *
     * @return possible object is {@link TEntityTemplate.Properties }
     */
    /*@Nullable*/
    public TEntityTemplate.Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     *
     * @param value allowed object is {@link TEntityTemplate.Properties }
     */
    public void setProperties(TEntityTemplate.Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the propertyConstraints property.
     *
     * @return possible object is {@link TEntityTemplate.PropertyConstraints }
     */
    /*@Nullable*/
    public TEntityTemplate.PropertyConstraints getPropertyConstraints() {
        return propertyConstraints;
    }

    /**
     * Sets the value of the propertyConstraints property.
     *
     * @param value allowed object is {@link TEntityTemplate.PropertyConstraints }
     */
    public void setPropertyConstraints(TEntityTemplate.PropertyConstraints value) {
        this.propertyConstraints = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return possible object is {@link QName }
     */
    @NonNull
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
    public QName getTypeAsQName() {
        return this.getType();
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
     *       &lt;sequence>
     *         &lt;any processContents='lax' namespace='##other'/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Properties {

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
         * Sets the value of the any property.
         *
         * @param value allowed object is {@link Element } {@link Object }
         */
        public void setAny(Object value) {
            this.any = value;
        }

        /**
         * This is a special method for Winery. Winery allows to define a property by specifying name/value values.
         * Instead of parsing the XML contained in TNodeType, this method is a convenience method to access this
         * information assumes the properties are key/value pairs (see WinerysPropertiesDefinition), all other cases are
         * return null.
         *
         * Returns a map of key/values of this template based on the information of WinerysPropertiesDefinition. In case
         * no value is set, the empty string is used. The map is implemented as {@link LinkedHashMap} to ensure that the
         * order of the elements is the same as in the XML. We return the type {@link LinkedHashMap}, because there is
         * no appropriate Java interface for "sorted" Maps
         *
         * In case the element is not of the form k/v, null is returned
         *
         * This method assumes that the any field is always populated.
         *
         * @return null if not k/v, a map of k/v properties otherwise
         */
        @ADR(12)
        public LinkedHashMap<String, String> getKVProperties() {
            // we use the internal variable "any", because getAny() returns null, if we have KVProperties
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

                // We cannot access the wrapper definitions, because we don't have access to the type
                // Element root = doc.createElementNS(wpd.getNamespace(), wpd.getElementName());
                LOGGER.warn("Creating XML properties element without correct wrapper element. The resulting XML needs to be patched. This is currently not implemented.");
                // Therefore, we create a dummy wrapper element:

                Element root = doc.createElementNS(Namespaces.EXAMPLE_NAMESPACE_URI, "Properties");
                doc.appendChild(root);

                // No wpd - so this is not possible:
                // we produce the serialization in the same order the XSD would be generated (because of the usage of xsd:sequence)
                // for (PropertyDefinitionKV prop : wpd.getPropertyDefinitionKVList()) {

                for (String key : properties.keySet()) {
                    // wpd.getNamespace()
                    Element element = doc.createElementNS(Namespaces.EXAMPLE_NAMESPACE_URI, key);
                    root.appendChild(element);
                    String value = properties.get(key);
                    if (value != null) {
                        Text text = doc.createTextNode(value);
                        element.appendChild(text);
                    }
                }

                this.setAny(doc.getDocumentElement());
            } else {
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
     *       &lt;sequence>
     *         &lt;element name="PropertyConstraint" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tPropertyConstraint"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "propertyConstraint"
    })
    public static class PropertyConstraints {

        @XmlElement(name = "PropertyConstraint", required = true)
        protected List<TPropertyConstraint> propertyConstraint;

        /**
         * Gets the value of the propertyConstraint property.
         *
         * <p> This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
         * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
         * method for the propertyConstraint property.
         *
         * <p> For example, to add a new item, do as follows:
         * <pre>
         *    getPropertyConstraint().add(newItem);
         * </pre>
         *
         *
         * <p> Objects of the following type(s) are allowed in the list {@link TPropertyConstraint }
         */
        @NonNull
        public List<TPropertyConstraint> getPropertyConstraint() {
            if (propertyConstraint == null) {
                propertyConstraint = new ArrayList<TPropertyConstraint>();
            }
            return this.propertyConstraint;
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
