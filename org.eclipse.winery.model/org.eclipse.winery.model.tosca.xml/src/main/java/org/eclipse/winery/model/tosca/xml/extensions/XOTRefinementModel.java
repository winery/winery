/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.xml.extensions;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.XHasName;
import org.eclipse.winery.model.tosca.xml.XHasTargetNamespace;
import org.eclipse.winery.model.tosca.xml.XTExtensibleElements;
import org.eclipse.winery.model.tosca.xml.XTTopologyTemplate;
import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlType(name = "")
@XmlSeeAlso( {
    XOTTopologyFragmentRefinementModel.class,
    XOTTestRefinementModel.class,
})
public abstract class XOTRefinementModel extends XTExtensibleElements implements XHasName, XHasTargetNamespace {

    @XmlAttribute
    protected String name;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlElement(name = "Detector")
    protected XTTopologyTemplate detector;

    @XmlElementWrapper(name = "RelationMappings")
    @XmlElement(name = "RelationMapping")
    protected List<XOTRelationMapping> relationMappings;

    @XmlElementWrapper(name = "PermutationMappings")
    @XmlElement(name = "PermutationMapping")
    protected List<XOTPermutationMapping> permutationMappings;

    @Deprecated // required for XML deserialization
    public XOTRefinementModel() { }

    public XOTRefinementModel(Builder<? extends Builder<?>> builder) {
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

    public List<XOTPermutationMapping> getPermutationMappings() {
        return permutationMappings;
    }

    public void setPermutationMappings(List<XOTPermutationMapping> permutationMappings) {
        this.permutationMappings = permutationMappings;
    }

    @NonNull
    public XTTopologyTemplate getDetector() {
        if (detector == null) {
            detector = new XTTopologyTemplate();
        }
        return detector;
    }

    public void setDetector(XTTopologyTemplate detector) {
        this.detector = detector;
    }

    public abstract XTTopologyTemplate getRefinementTopology();

    public abstract void setRefinementTopology(XTTopologyTemplate topology);

    public List<XOTRelationMapping> getRelationMappings() {
        return relationMappings;
    }

    public void setRelationMappings(List<XOTRelationMapping> relationMappings) {
        this.relationMappings = relationMappings;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static abstract class Builder<T extends Builder<T>> extends XTExtensibleElements.Builder<T> {

        private List<XOTPermutationMapping> permutationMappings;
        private String name;
        private String targetNamespace;
        private XTTopologyTemplate detector;
        private List<XOTRelationMapping> relationMappings;

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

        public T setDetector(XTTopologyTemplate detector) {
            this.detector = detector;
            return self();
        }

        public T setRelationMappings(List<XOTRelationMapping> relationMappings) {
            this.relationMappings = relationMappings;
            return self();
        }

        public T setPermutationMappings(List<XOTPermutationMapping> permutationMappings) {
            this.permutationMappings = permutationMappings;
            return self();
        }
    }
}
