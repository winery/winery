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

package org.eclipse.winery.model.tosca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tArtifactTemplate", propOrder = {
    "artifactReferences"
})
public class TArtifactTemplate extends TEntityTemplate {

    @XmlElement(name = "ArtifactReferences")
    protected TArtifactTemplate.ArtifactReferences artifactReferences;
    @XmlAttribute(name = "name")
    protected String name;

    public TArtifactTemplate() {
    }

    public TArtifactTemplate(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.artifactReferences = builder.artifactReferences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TArtifactTemplate)) return false;
        if (!super.equals(o)) return false;
        TArtifactTemplate that = (TArtifactTemplate) o;
        return Objects.equals(artifactReferences, that.artifactReferences) &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), artifactReferences, name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public TArtifactTemplate.@Nullable ArtifactReferences getArtifactReferences() {
        return artifactReferences;
    }

    public void setArtifactReferences(TArtifactTemplate.@Nullable ArtifactReferences value) {
        this.artifactReferences = value;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String value) {
        this.name = value;
    }

    @Override
    public String toString() {
        return "TArtifactTemplate{" +
            "artifactReferences=" + artifactReferences +
            ", name='" + name + '\'' +
            ", properties=" + properties +
            ", propertyConstraints=" + propertyConstraints +
            ", type=" + type +
            '}';
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "artifactReference"
    })
    public static class ArtifactReferences implements Serializable {

        @XmlElement(name = "ArtifactReference", required = true)
        protected List<TArtifactReference> artifactReference;

        @Override
        public String toString() {
            return "ArtifactReferences{" +
                "artifactReference=" + artifactReference +
                '}';
        }

        @NonNull
        public List<TArtifactReference> getArtifactReference() {
            if (artifactReference == null) {
                artifactReference = new ArrayList<TArtifactReference>();
            }
            return this.artifactReference;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArtifactReferences that = (ArtifactReferences) o;
            return Objects.equals(artifactReference, that.artifactReference);
        }

        @Override
        public int hashCode() {
            return Objects.hash(artifactReference);
        }
    }

    public static class Builder extends TEntityTemplate.Builder<Builder> {
        private String name;
        private TArtifactTemplate.ArtifactReferences artifactReferences;

        public Builder(String id, QName type) {
            super(id, type);
        }

        public Builder(TEntityTemplate entityTemplate) {
            super(entityTemplate);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setArtifactReferences(TArtifactTemplate.ArtifactReferences artifactReferences) {
            this.artifactReferences = artifactReferences;
            return this;
        }

        public Builder addArtifactReferences(TArtifactTemplate.ArtifactReferences artifactReferences) {
            if (artifactReferences == null || artifactReferences.getArtifactReference().isEmpty()) {
                return this;
            }

            if (this.artifactReferences == null) {
                this.artifactReferences = artifactReferences;
            } else {
                this.artifactReferences.getArtifactReference().addAll(artifactReferences.artifactReference);
            }
            return this;
        }

        public Builder addArtifactReferences(List<TArtifactReference> artifactReferences) {
            if (artifactReferences == null) {
                return this;
            }

            TArtifactTemplate.ArtifactReferences tmp = new TArtifactTemplate.ArtifactReferences();
            tmp.getArtifactReference().addAll(artifactReferences);
            return addArtifactReferences(tmp);
        }

        public Builder addArtifactReferences(TArtifactReference artifactReferences) {
            if (artifactReferences == null) {
                return this;
            }

            TArtifactTemplate.ArtifactReferences tmp = new TArtifactTemplate.ArtifactReferences();
            tmp.getArtifactReference().add(artifactReferences);
            return addArtifactReferences(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public TArtifactTemplate build() {
            return new TArtifactTemplate(this);
        }
    }
}
