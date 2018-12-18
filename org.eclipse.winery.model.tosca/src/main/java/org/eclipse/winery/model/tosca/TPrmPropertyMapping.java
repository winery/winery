/*******************************************************************************
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
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class TPrmPropertyMapping extends HasId implements Serializable {

    @JsonIdentityReference(alwaysAsId = true)
    @XmlAttribute(name = "detectorNode", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @NonNull
    private TNodeTemplate detectorNode;

    @JsonIdentityReference(alwaysAsId = true)
    @XmlAttribute(name = "refinementNode", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @NonNull
    private TNodeTemplate refinementNode;
    
    @XmlAttribute(name = "type")
    private TPrmPropertyMappingType type;
    
    @XmlAttribute(name = "detectorProperty")
    @Nullable
    private String detectorProperty;
    
    @XmlAttribute(name = "refinementProperty")
    @Nullable
    private String refinementProperty;

    public TNodeTemplate getDetectorNode() {
        return detectorNode;
    }

    public void setDetectorNode(TNodeTemplate detectorNode) {
        this.detectorNode = detectorNode;
    }

    public TNodeTemplate getRefinementNode() {
        return refinementNode;
    }

    public void setRefinementNode(TNodeTemplate refinementNode) {
        this.refinementNode = refinementNode;
    }

    public TPrmPropertyMappingType getType() {
        return type;
    }

    public void setType(TPrmPropertyMappingType type) {
        this.type = type;
    }

    public String getDetectorProperty() {
        return detectorProperty;
    }

    public void setDetectorProperty(String detectorProperty) {
        this.detectorProperty = detectorProperty;
    }

    public String getRefinementProperty() {
        return refinementProperty;
    }

    public void setRefinementProperty(String refinementProperty) {
        this.refinementProperty = refinementProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TPrmPropertyMapping that = (TPrmPropertyMapping) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
