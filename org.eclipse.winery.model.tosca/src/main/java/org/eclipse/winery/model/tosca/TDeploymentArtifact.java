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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;


/**
 * <p>Java class for tDeploymentArtifact complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tDeploymentArtifact">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="artifactType" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="artifactRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDeploymentArtifact")
public class TDeploymentArtifact extends TExtensibleElements {
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "artifactType", required = true)
    protected QName artifactType;
    @XmlAttribute(name = "artifactRef")
    protected QName artifactRef;

    public TDeploymentArtifact() {
    }

    public TDeploymentArtifact(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.artifactType = builder.artifactType;
        this.artifactRef = builder.artifactRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TDeploymentArtifact)) return false;
        TDeploymentArtifact that = (TDeploymentArtifact) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(artifactType, that.artifactType) &&
                Objects.equals(artifactRef, that.artifactRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artifactType, artifactRef);
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
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

    public static class Builder extends TExtensibleElements.Builder<Builder> {
        private final String name;
        private final QName artifactType;
        private QName artifactRef;

        public Builder(String name, QName artifactType) {
            this.name = name;
            this.artifactType = artifactType;
        }

        public Builder setArtifactRef(QName artifactRef) {
            this.artifactRef = artifactRef;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TDeploymentArtifact build() {
            return new TDeploymentArtifact(this);
        }
    }
}
