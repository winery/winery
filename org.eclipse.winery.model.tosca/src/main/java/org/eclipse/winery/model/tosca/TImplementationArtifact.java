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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;


/**
 * <p>Java class for tImplementationArtifact complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tImplementationArtifact">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="interfaceName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="operationName" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="artifactType" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="artifactRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tImplementationArtifact")
@XmlSeeAlso({
    org.eclipse.winery.model.tosca.TImplementationArtifacts.ImplementationArtifact.class
})
public class TImplementationArtifact extends TExtensibleElements {
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "interfaceName")
    @XmlSchemaType(name = "anyURI")
    protected String interfaceName;
    @XmlAttribute(name = "operationName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String operationName;
    @XmlAttribute(name = "artifactType", required = true)
    protected QName artifactType;
    @XmlAttribute(name = "artifactRef")
    protected QName artifactRef;

    public TImplementationArtifact() {

    }

    public TImplementationArtifact(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.interfaceName = builder.interfaceName;
        this.operationName = builder.operationName;
        this.artifactType = builder.artifactType;
        this.artifactRef = builder.artifactRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TImplementationArtifact)) return false;
        if (!super.equals(o)) return false;
        TImplementationArtifact that = (TImplementationArtifact) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(interfaceName, that.interfaceName) &&
            Objects.equals(operationName, that.operationName) &&
            Objects.equals(artifactType, that.artifactType) &&
            Objects.equals(artifactRef, that.artifactRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, interfaceName, operationName, artifactType, artifactRef);
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the interfaceName property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * Sets the value of the interfaceName property.
     *
     * @param value allowed object is {@link String }
     */
    public void setInterfaceName(String value) {
        this.interfaceName = value;
    }

    /**
     * Gets the value of the operationName property.
     *
     * @return possible object is {@link String }
     */
    @Nullable
    public String getOperationName() {
        return operationName;
    }

    /**
     * Sets the value of the operationName property.
     *
     * @param value allowed object is {@link String }
     */
    public void setOperationName(String value) {
        this.operationName = value;
    }

    /**
     * Gets the value of the artifactType property.
     *
     * @return possible object is {@link QName }
     */
    @NonNull
    public QName getArtifactType() {
        return artifactType;
    }

    /**
     * Sets the value of the artifactType property.
     *
     * @param value allowed object is {@link QName }
     */
    public void setArtifactType(QName value) {
        this.artifactType = value;
    }

    /**
     * Gets the value of the artifactRef property.
     *
     * @return possible object is {@link QName }
     */
    @Nullable
    public QName getArtifactRef() {
        return artifactRef;
    }

    /**
     * Sets the value of the artifactRef property.
     *
     * @param value allowed object is {@link QName }
     */
    public void setArtifactRef(QName value) {
        this.artifactRef = value;
    }

    public static class Builder<T extends Builder<T>> extends TExtensibleElements.Builder<Builder<T>> {
        private final QName artifactType;

        private String name;
        private String interfaceName;
        private String operationName;
        private QName artifactRef;

        public Builder(QName artifactType) {
            this.artifactType = artifactType;
        }

        public T setName(String name) {
            this.name = name;
            return self();
        }

        public T setInterfaceName(String interfaceName) {
            this.interfaceName = interfaceName;
            return self();
        }

        public T setOperationName(String operationName) {
            this.operationName = operationName;
            return self();
        }

        public T setArtifactRef(QName artifactRef) {
            this.artifactRef = artifactRef;
            return self();
        }

        @ADR(11)
        @Override
        public T self() {
            return (T) this;
        }

        public TImplementationArtifact build() {
            return new TImplementationArtifact(this);
        }
    }
}
