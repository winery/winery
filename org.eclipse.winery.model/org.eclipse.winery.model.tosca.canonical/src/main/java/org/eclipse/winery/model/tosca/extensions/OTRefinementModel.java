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

package org.eclipse.winery.model.tosca.extensions;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;

import org.eclipse.winery.model.tosca.HasName;
import org.eclipse.winery.model.tosca.HasTargetNamespace;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

public abstract class OTRefinementModel extends TExtensibleElements implements HasName, HasTargetNamespace {

    @XmlAttribute
    protected String name;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlElement(name = "Detector")
    protected TTopologyTemplate detector;

    @XmlElementWrapper(name = "RelationMappings")
    @XmlElement(name = "RelationMapping")
    protected List<OTRelationMapping> relationMappings;

    @XmlElementWrapper(name = "PermutationMappings")
    @XmlElement(name = "PermutationMapping")
    protected List<OTPermutationMapping> permutationMappings;

    @Deprecated // used for XML deserialization of API request content
    public OTRefinementModel() {
    }

    @SuppressWarnings("unchecked")
    public OTRefinementModel(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.targetNamespace = builder.targetNamespace;
        this.detector = builder.detector;
        this.relationMappings = builder.relationMappings;
        this.permutationMappings = builder.permutationMappings;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

    @Override
    public String getIdFromIdOrNameField() {
        return getName();
    }

    @Override
    public String getTargetNamespace() {
        return targetNamespace;
    }

    @Override
    public void setTargetNamespace(String value) {
        targetNamespace = value;
    }

    @NonNull
    public TTopologyTemplate getDetector() {
        if (detector == null) {
            detector = new TTopologyTemplate();
        }
        return detector;
    }

    public void setDetector(TTopologyTemplate detector) {
        this.detector = detector;
    }

    public abstract TTopologyTemplate getRefinementTopology();

    public abstract void setRefinementTopology(TTopologyTemplate topology);

    public abstract void resetAllMappings();

    public List<OTRelationMapping> getRelationMappings() {
        return relationMappings;
    }

    public void setRelationMappings(List<OTRelationMapping> relationMappings) {
        this.relationMappings = relationMappings;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public List<OTPermutationMapping> getPermutationMappings() {
        return permutationMappings;
    }

    public void setPermutationMappings(List<OTPermutationMapping> permutationMappings) {
        this.permutationMappings = permutationMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OTRefinementModel that = (OTRefinementModel) o;
        return Objects.equals(name, that.name)
            && Objects.equals(targetNamespace, that.targetNamespace)
            && Objects.equals(detector, that.detector)
            && Objects.equals(relationMappings, that.relationMappings)
            && Objects.equals(permutationMappings, that.permutationMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, targetNamespace, detector, relationMappings, permutationMappings);
    }

    public static abstract class Builder<T extends Builder<T>> extends TExtensibleElements.Builder<T> {

        private String name;
        private String targetNamespace;
        private TTopologyTemplate detector;
        private List<OTRelationMapping> relationMappings;
        private List<OTPermutationMapping> permutationMappings;

        public Builder() {
        }

        public T setName(String name) {
            this.name = name;
            return self();
        }

        public T setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
            return self();
        }

        public T setDetector(TTopologyTemplate detector) {
            this.detector = detector;
            return self();
        }

        public T setRelationMappings(List<OTRelationMapping> relationMappings) {
            this.relationMappings = relationMappings;
            return self();
        }

        public T setPermutationMappings(List<OTPermutationMapping> permutationMappings) {
            this.permutationMappings = permutationMappings;
            return self();
        }
    }
}
