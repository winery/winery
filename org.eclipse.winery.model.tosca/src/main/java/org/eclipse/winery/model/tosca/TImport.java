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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;


/**
 * <p>Java class for tImport complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tImport">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="location" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="importType" use="required" type="{http://docs.oasis-open.org/tosca/ns/2011/12}importedURI"
 * />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tImport")
public class TImport extends TExtensibleElements {
    @XmlAttribute(name = "namespace")
    @XmlSchemaType(name = "anyURI")
    protected String namespace;
    @XmlAttribute(name = "location")
    @XmlSchemaType(name = "anyURI")
    protected String location;
    @XmlAttribute(name = "importType", required = true)
    protected String importType;

    public TImport() {

    }

    public TImport(Builder builder) {
        super(builder);
        this.namespace = builder.namespace;
        this.location = builder.location;
        this.importType = builder.importType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TImport)) return false;
        if (!super.equals(o)) return false;
        TImport tImport = (TImport) o;
        return Objects.equals(namespace, tImport.namespace) &&
                Objects.equals(location, tImport.location) &&
                Objects.equals(importType, tImport.importType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), namespace, location, importType);
    }

    /**
     * Gets the value of the namespace property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the value of the namespace property.
     *
     * @param value allowed object is {@link String }
     */
    public void setNamespace(String value) {
        this.namespace = value;
    }

    /**
     * Gets the value of the location property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     *
     * @param value allowed object is {@link String }
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the importType property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getImportType() {
        return importType;
    }

    /**
     * Sets the value of the importType property.
     *
     * @param value allowed object is {@link String }
     */
    public void setImportType(String value) {
        this.importType = value;
    }

    public static class Builder extends TExtensibleElements.Builder<Builder> {
        private final String importType;
        private String namespace;
        private String location;

        public Builder(String importType) {
            this.importType = importType;
        }

        public Builder(TExtensibleElements extensibleElements, String importType) {
            super(extensibleElements);
            this.importType = importType;
        }

        public Builder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TImport build() {
            return new TImport(this);
        }
    }
}
