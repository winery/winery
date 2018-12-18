/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.model.tosca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPatternRefinementModel")
public class TPatternRefinementModel extends TExtensibleElements implements HasName, HasTargetNamespace {

    @XmlAttribute
    protected String name;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlElement(name = "Detector")
    private TTopologyTemplate detector;

    @XmlElement(name = "RefinementStructure")
    private TTopologyTemplate refinementStructure;

    @XmlElement(name = "RelationMappings")
    private TRelationMappings relationMappings;

    @XmlElement(name = "PrmPropertyMappings")
    private TPrmPropertyMappings propertyMappings;

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

    @NonNull
    public TTopologyTemplate getRefinementStructure() {
        if (refinementStructure == null) {
            refinementStructure = new TTopologyTemplate();
        }
        return refinementStructure;
    }

    public void setRefinementStructure(TTopologyTemplate refinementStructure) {
        this.refinementStructure = refinementStructure;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "relationMapping"
    })
    public static class TRelationMappings implements Serializable {

        @XmlElement(name = "RelationMapping")
        protected List<TRelationMapping> relationMapping;

        @NonNull
        public List<TRelationMapping> getRelationMapping() {
            if (Objects.isNull(this.relationMapping)) {
                this.relationMapping = new ArrayList<>();
            }
            return this.relationMapping;
        }
    }

    @Nullable
    public TRelationMappings getRelationMappings() {
        return relationMappings;
    }

    public void setRelationMappings(TRelationMappings relationMappings) {
        this.relationMappings = relationMappings;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "propertyMapping"
    })
    public static class TPrmPropertyMappings implements Serializable {

        @XmlElement(name = "PropertyMapping")
        protected List<TPrmPropertyMapping> propertyMapping;

        @NonNull
        public List<TPrmPropertyMapping> getPropertyMapping() {
            if (Objects.isNull(this.propertyMapping)) {
                this.propertyMapping = new ArrayList<>();
            }
            return this.propertyMapping;
        }
    }

    @Nullable
    public TPrmPropertyMappings getPropertyMappings() {
        return propertyMappings;
    }

    public void setPropertyMappings(TPrmPropertyMappings propertyMappings) {
        this.propertyMappings = propertyMappings;
    }
}
