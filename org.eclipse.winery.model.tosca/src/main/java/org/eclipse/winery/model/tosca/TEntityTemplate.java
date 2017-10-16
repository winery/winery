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
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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
public abstract class TEntityTemplate extends HasId implements HasType, HasName {

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
    public static class Properties {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Gets the value of the any property.
         *
         * @return possible object is {@link Element } {@link Object }
         */
        @Nullable
        public Object getAny() {
            return any;
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
         * not implemented yet.
         *
         * The return type "Properties" is used because of the key/value properties.
         */
        @XmlTransient
        @JsonIgnore
        public java.util.Properties getKVProperties() {
            java.util.Properties properties = new java.util.Properties();
            org.eclipse.winery.model.tosca.TEntityTemplate.Properties tprops = this;
            if (tprops != null) {
                // no checking for validity, just reading
                Element el = (Element) tprops.getAny();
                if (el == null) {
                    // somehow invalid .tosca. We return empty properties instead of throwing a NPE
                    return properties;
                }
                NodeList childNodes = el.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node item = childNodes.item(i);
                    if (item instanceof Element) {
                        String key = item.getLocalName();
                        String value = item.getTextContent();
                        properties.put(key, value);
                    }
                }
            }
            return properties;
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
