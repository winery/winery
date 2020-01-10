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

import java.util.List;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

public abstract class OTRefinementModel extends TExtensibleElements implements HasName, HasTargetNamespace {

    protected String name;

    protected String targetNamespace;

    protected TTopologyTemplate detector;

    @XmlElementWrapper(name = "RelationMappings")
    @XmlElement(name = "RelationMapping")
    protected List<OTRelationMapping> relationMappings;

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

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public List<OTRelationMapping> getRelationMappings() {
        return relationMappings;
    }

    public void setRelationMappings(List<OTRelationMapping> relationMappings) {
        this.relationMappings = relationMappings;
    }
}
