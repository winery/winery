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

package org.eclipse.winery.model.tosca.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tArtifactReference", propOrder = {
    "includeOrExclude"
})
public class TArtifactReference implements Serializable {

    @XmlElements( {
        @XmlElement(name = "Exclude", type = TArtifactReference.Exclude.class),
        @XmlElement(name = "Include", type = TArtifactReference.Include.class)
    })
    protected List<Object> includeOrExclude;

    @XmlAttribute(name = "reference", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String reference;

    public TArtifactReference() {

    }

    public TArtifactReference(Builder builder) {
        this.includeOrExclude = builder.includeOrExclude;
        this.reference = builder.reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TArtifactReference)) return false;
        TArtifactReference that = (TArtifactReference) o;
        return Objects.equals(includeOrExclude, that.includeOrExclude) &&
            Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(includeOrExclude, reference);
    }

    @Override
    public String toString() {
        return "TArtifactReference{" +
            "includeOrExclude=" + includeOrExclude +
            ", reference='" + reference + '\'' +
            '}';
    }

    /**
     * Gets the value of the includeOrExclude property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includeOrExclude property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludeOrExclude().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TArtifactReference.Exclude }
     * {@link TArtifactReference.Include }
     */
    @NonNull
    public List<Object> getIncludeOrExclude() {
        if (includeOrExclude == null) {
            includeOrExclude = new ArrayList<Object>();
        }
        return this.includeOrExclude;
    }

    @NonNull
    public String getReference() {
        return reference;
    }

    public void setReference(String value) {
        this.reference = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Exclude {

        @XmlAttribute(name = "pattern", required = true)
        protected String pattern;

        @NonNull
        public String getPattern() {
            return pattern;
        }

        public void setPattern(String value) {
            this.pattern = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Include implements Serializable {

        @XmlAttribute(name = "pattern", required = true)
        protected String pattern;

        @NonNull
        public String getPattern() {
            return pattern;
        }

        public void setPattern(String value) {
            this.pattern = value;
        }
    }

    public static class Builder {
        private final String reference;
        private List<Object> includeOrExclude;

        public Builder(String reference) {
            this.reference = reference;
        }

        public Builder setIncludeOrExclude(List<Object> includeOrExclude) {
            this.includeOrExclude = includeOrExclude;
            return this;
        }

        public TArtifactReference build() {
            return new TArtifactReference(this);
        }
    }
}
