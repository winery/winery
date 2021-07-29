/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tArtifactTemplate", propOrder = {
    "artifactReferences"
})
public class TArtifactTemplate extends TEntityTemplate {

    @XmlElementWrapper(name = "ArtifactReferences")
    @XmlElement(name = "ArtifactReference", required = true)
    protected List<TArtifactReference> artifactReferences;

    @XmlAttribute(name = "name")
    protected String name;

    @Deprecated // used for XML deserialization of API request content
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

    public List<TArtifactReference> getArtifactReferences() {
        return artifactReferences;
    }

    public void setArtifactReferences(List<TArtifactReference> value) {
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

    public static class Builder extends TEntityTemplate.Builder<Builder> {

        private String name;
        private List<TArtifactReference> artifactReferences;

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

        public Builder addArtifactReferences(List<TArtifactReference> artifactReferences) {
            if (artifactReferences == null || artifactReferences.isEmpty()) {
                return this;
            }

            if (this.artifactReferences == null) {
                this.artifactReferences = artifactReferences;
            } else {
                this.artifactReferences.addAll(artifactReferences);
            }
            return this;
        }

        public Builder addArtifactReference(TArtifactReference artifactReference) {
            if (artifactReference == null) {
                return this;
            }

            List<TArtifactReference> tmp = new ArrayList<>();
            tmp.add(artifactReference);
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
