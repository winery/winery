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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.XTBoolean;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otPatternRefinementModel")
public class XOTPatternRefinementModel extends XOTTopologyFragmentRefinementModel {

    @XmlAttribute(name = "isPdrm")
    private XTBoolean isPdrm;

    @XmlElementWrapper(name = "BehaviorPatternMappings")
    @XmlElement(name = "BehaviorPatternMapping")
    private List<XOTBehaviorPatternMapping> behaviorPatternMappings;

    @Deprecated // required for XML deserialization
    public XOTPatternRefinementModel() {
    }

    public XOTPatternRefinementModel(Builder builder) {
        super(builder);
        this.isPdrm = builder.isPdrm;
        this.behaviorPatternMappings = builder.behaviorPatternMappings;
    }

    public XTBoolean isPdrm() {
        return isPdrm;
    }

    public void setIsPdrm(XTBoolean isPdrm) {
        this.isPdrm = isPdrm;
    }

    public List<XOTBehaviorPatternMapping> getBehaviorPatternMappings() {
        return behaviorPatternMappings;
    }

    public void setBehaviorPatternMappings(List<XOTBehaviorPatternMapping> behaviorPatternMappings) {
        this.behaviorPatternMappings = behaviorPatternMappings;
    }

    public static class Builder extends XOTTopologyFragmentRefinementModel.Builder {

        private XTBoolean isPdrm;
        private List<XOTBehaviorPatternMapping> behaviorPatternMappings;

        public XOTPatternRefinementModel.Builder setIsPdrm(XTBoolean isPdrm) {
            this.isPdrm = isPdrm;
            return self();
        }

        public XOTTopologyFragmentRefinementModel.Builder setBehaviorPatternMappings(List<XOTBehaviorPatternMapping> behaviorPatternMappings) {
            this.behaviorPatternMappings = behaviorPatternMappings;
            return self();
        }

        public XOTPatternRefinementModel build() {
            return new XOTPatternRefinementModel(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
