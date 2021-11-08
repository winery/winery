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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.winery.model.jaxbsupport.map.BooleanToYesNo;
import org.eclipse.winery.model.jsonsupport.YesNo;
import org.eclipse.winery.model.tosca.constants.Namespaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otPatternRefinementModel", namespace = Namespaces.TOSCA_WINERY_EXTENSIONS_NAMESPACE)
public class OTPatternRefinementModel extends OTTopologyFragmentRefinementModel {

    @XmlAttribute(name = "isPdrm")
    @XmlJavaTypeAdapter(type = boolean.class, value = BooleanToYesNo.class)
    @JsonProperty("isPdrm")
    @JsonSerialize(using = YesNo.Serializer.class)
    @JsonDeserialize(using = YesNo.Deserializer.class)
    private boolean isPdrm;

    @XmlElementWrapper(name = "BehaviorPatternMappings")
    @XmlElement(name = "BehaviorPatternMapping")
    private List<OTBehaviorPatternMapping> behaviorPatternMappings;

    @Deprecated // used for XML deserialization of API request content
    public OTPatternRefinementModel() {
    }

    public OTPatternRefinementModel(Builder builder) {
        super(builder);
        this.isPdrm = builder.isPdrm;
        this.behaviorPatternMappings = builder.behaviorPatternMappings;
    }

    @Override
    public void resetAllMappings() {
        super.resetAllMappings();
        setBehaviorPatternMappings(new ArrayList<>());
    }

    public boolean isPdrm() {
        return isPdrm;
    }

    public void setIsPdrm(boolean isPdrm) {
        this.isPdrm = isPdrm;
    }

    public List<OTBehaviorPatternMapping> getBehaviorPatternMappings() {
        return behaviorPatternMappings;
    }

    public void setBehaviorPatternMappings(List<OTBehaviorPatternMapping> behaviorPatternMappings) {
        this.behaviorPatternMappings = behaviorPatternMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OTPatternRefinementModel that = (OTPatternRefinementModel) o;
        return Objects.equals(isPdrm, that.isPdrm)
            && Objects.equals(behaviorPatternMappings, that.behaviorPatternMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isPdrm, behaviorPatternMappings);
    }

    public static class Builder extends RefinementBuilder<Builder> {

        private boolean isPdrm;
        private List<OTBehaviorPatternMapping> behaviorPatternMappings;

        public OTPatternRefinementModel.Builder setIsPdrm(boolean isPdrm) {
            this.isPdrm = isPdrm;
            return self();
        }

        public OTPatternRefinementModel.Builder setBehaviorPatternMappings(List<OTBehaviorPatternMapping> behaviorPatternMappings) {
            this.behaviorPatternMappings = behaviorPatternMappings;
            return self();
        }

        public OTPatternRefinementModel build() {
            return new OTPatternRefinementModel(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
