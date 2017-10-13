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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Element;


/**
 * <p>Java class for tExtensibleElements complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tExtensibleElements">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/tosca/ns/2011/12}documentation" maxOccurs="unbounded"
 * minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tExtensibleElements", propOrder = {
    "documentation",
    "any"
})
@XmlSeeAlso({
    TImport.class,
    TServiceTemplate.class,
    TEntityTypeImplementation.class,
    TOperation.class,
    TRequirementDefinition.class,
    TExtension.class,
    TCapabilityDefinition.class,
    TExtensions.class,
    TDeploymentArtifact.class,
    TPlan.class,
    TEntityTemplate.class,
    TEntityType.class,
    TPolicy.class,
    TImplementationArtifact.class,
    TTopologyTemplate.class,
    TDefinitions.class
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TExtensibleElements {

    protected List<TDocumentation> documentation;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public TExtensibleElements() {
    }

    public TExtensibleElements(Builder builder) {
        this.documentation = builder.documentation;
        this.any = builder.any;
        this.otherAttributes = builder.otherAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TExtensibleElements)) return false;
        TExtensibleElements that = (TExtensibleElements) o;
        return Objects.equals(documentation, that.documentation) &&
            Objects.equals(any, that.any) &&
            Objects.equals(otherAttributes, that.otherAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentation, any, otherAttributes);
    }

    /**
     * Gets the value of the documentation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documentation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocumentation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TDocumentation }
     */
    @NonNull
    public List<TDocumentation> getDocumentation() {
        if (documentation == null) {
            documentation = new ArrayList<TDocumentation>();
        }
        return this.documentation;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     */
    @NonNull
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    @Nullable
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    /**
     * Generic abstract Builder
     *
     * @param <T> the Builder which extends this abstract Builder
     */
    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> {
        private List<TDocumentation> documentation;
        private List<Object> any;
        private Map<QName, String> otherAttributes;

        public Builder() {

        }

        public Builder(Builder builder) {
            this.documentation = builder.documentation;
            this.any = builder.any;
            this.otherAttributes = builder.otherAttributes;
        }

        public Builder(TExtensibleElements extensibleElements) {
            this.addDocumentation(extensibleElements.getDocumentation());
            this.addAny(extensibleElements.getAny());
            this.addOtherAttributes(extensibleElements.getOtherAttributes());
        }

        public T setDocumentation(List<TDocumentation> documentation) {
            this.documentation = documentation;
            return self();
        }

        public T setAny(List<Object> any) {
            this.any = any;
            return self();
        }

        public T setOtherAttributes(Map<QName, String> otherAttributes) {
            this.otherAttributes = otherAttributes;
            return self();
        }

        public T addDocumentation(List<TDocumentation> documentation) {
            if (documentation == null) {
                return self();
            }

            if (this.documentation == null) {
                this.documentation = documentation;
            } else {
                this.documentation.addAll(documentation);
            }
            return self();
        }

        public T addDocumentation(TDocumentation documentation) {
            if (documentation == null) {
                return self();
            }

            List<TDocumentation> tmp = new ArrayList<>();
            tmp.add(documentation);
            return addDocumentation(tmp);
        }

        public T addDocumentation(String documentation) {
            if (documentation == null || documentation.length() == 0) {
                return self();
            }

            TDocumentation tmp = new TDocumentation();
            tmp.getContent().add(documentation);
            return self().addDocumentation(tmp);
        }

        public T addDocumentation(Map<String, String> documentation) {
            if (documentation == null) {
                return self();
            }

            for (Map.Entry<String, String> entry : documentation.entrySet()) {
                this.addDocumentation(entry.getKey() + ": " + entry.getValue());
            }
            return self();
        }

        public T addAny(List<Object> any) {
            if (any == null || any.isEmpty()) {
                return self();
            }

            if (this.any == null) {
                this.any = any;
            } else {
                this.any.addAll(any);
            }
            return self();
        }

        public T addAny(Object any) {
            if (any == null) {
                return self();
            }

            List<Object> tmp = new ArrayList<>();
            tmp.add(any);
            return addAny(tmp);
        }

        public T addOtherAttributes(Map<QName, String> otherAttributes) {
            if (otherAttributes == null || otherAttributes.isEmpty()) {
                return self();
            }

            if (this.otherAttributes == null) {
                this.otherAttributes = otherAttributes;
            } else {
                this.otherAttributes.putAll(otherAttributes);
            }
            return self();
        }

        public T addOtherAttributes(QName key, String value) {
            if (key == null) {
                return self();
            }

            LinkedHashMap<QName, String> map = new LinkedHashMap<>();
            map.put(key, value);
            return addOtherAttributes(map);
        }

        @ADR(11)
        public abstract T self();

        public TExtensibleElements build() {
            return new TExtensibleElements(this);
        }
    }
}
