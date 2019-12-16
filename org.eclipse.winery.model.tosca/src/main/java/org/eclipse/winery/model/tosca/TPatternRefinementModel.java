/********************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPatternRefinementModel")
public class TPatternRefinementModel extends TRefinementModel {

    @XmlElement(name = "RefinementStructure")
    private TTopologyTemplate refinementStructure;

    @XmlElementWrapper(name = "AttributeMappings")
    @XmlElement(name = "AttributeMapping")
    private List<AttributeMapping> attributeMappings;

    @XmlElementWrapper(name = "StayMappings")
    @XmlElement(name = "StayMapping")
    private List<TStayMapping> stayMappings;

    @NonNull
    @JsonIgnore
    @XmlTransient
    public TTopologyTemplate getRefinementTopology() {
        if (refinementStructure == null) {
            refinementStructure = new TTopologyTemplate();
        }
        return refinementStructure;
    }

    public TTopologyTemplate getRefinementStructure() {
        return getRefinementTopology();
    }

    public void setRefinementTopology(TTopologyTemplate refinementStructure) {
        this.refinementStructure = refinementStructure;
    }

    @Nullable
    public List<AttributeMapping> getAttributeMappings() {
        return attributeMappings;
    }

    public void setAttributeMappings(List<AttributeMapping> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }

    @Nullable
    public List<TStayMapping> getStayMappings() {
        return stayMappings;
    }

    public void setStayMappings(List<TStayMapping> stayMappings) {
        this.stayMappings = stayMappings;
    }
}
