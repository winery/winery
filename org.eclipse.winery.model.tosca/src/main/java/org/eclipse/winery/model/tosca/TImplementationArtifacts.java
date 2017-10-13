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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;


/**
 * <p>Java class for tImplementationArtifacts complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tImplementationArtifacts">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ImplementationArtifact" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tImplementationArtifact">
 *                 &lt;anyAttribute processContents='lax' namespace='##other'/>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tImplementationArtifacts", propOrder = {
    "implementationArtifact"
})
public class TImplementationArtifacts {

    @XmlElement(name = "ImplementationArtifact", required = true)
    protected List<TImplementationArtifacts.ImplementationArtifact> implementationArtifact;

    public TImplementationArtifacts() {

    }

    public TImplementationArtifacts(Builder builder) {
        this.implementationArtifact = builder.implementationArtifact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TImplementationArtifacts)) return false;
        TImplementationArtifacts that = (TImplementationArtifacts) o;
        return Objects.equals(implementationArtifact, that.implementationArtifact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(implementationArtifact);
    }

    /**
     * Gets the value of the implementationArtifact property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the implementationArtifact property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImplementationArtifact().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TImplementationArtifacts.ImplementationArtifact }
     */
    @NonNull
    public List<TImplementationArtifacts.ImplementationArtifact> getImplementationArtifact() {
        if (implementationArtifact == null) {
            implementationArtifact = new ArrayList<TImplementationArtifacts.ImplementationArtifact>();
        }
        return this.implementationArtifact;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tImplementationArtifact">
     *       &lt;anyAttribute processContents='lax' namespace='##other'/>
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ImplementationArtifact extends TImplementationArtifact {
        public ImplementationArtifact() {

        }

        public ImplementationArtifact(Builder builder) {
            super(builder);
        }

        public static class Builder extends TImplementationArtifact.Builder<Builder> {
            public Builder(QName artifactType) {
                super(artifactType);
            }

            public Builder self() {
                return this;
            }

            public ImplementationArtifact build() {
                return new ImplementationArtifact(this);
            }
        }
    }

    public static class Builder {
        public final List<ImplementationArtifact> implementationArtifact;

        public Builder(List<ImplementationArtifact> implementationArtifact) {
            this.implementationArtifact = implementationArtifact;
        }

        public TImplementationArtifacts build() {
            return new TImplementationArtifacts(this);
        }
    }
}
