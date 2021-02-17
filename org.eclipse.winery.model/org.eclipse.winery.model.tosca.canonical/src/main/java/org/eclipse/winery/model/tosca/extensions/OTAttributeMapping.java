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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otAttributeMapping")
public class OTAttributeMapping extends OTPrmMapping {

    @XmlAttribute(name = "type")
    private OTAttributeMappingType type;

    @XmlAttribute(name = "detectorProperty")
    @Nullable
    private String detectorProperty;

    @XmlAttribute(name = "refinementProperty")
    @Nullable
    private String refinementProperty;

    @Deprecated // used for XML deserialization of API request content
    public OTAttributeMapping() { }

    public OTAttributeMapping(Builder builder) {
        super(builder);
        this.type = builder.type;
        this.detectorProperty = builder.detectorProperty;
        this.refinementProperty = builder.refinementProperty;
    }

    public OTAttributeMappingType getType() {
        return type;
    }

    public void setType(OTAttributeMappingType type) {
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
    public boolean equals(Object obj) {
        return obj instanceof OTAttributeMapping
            && getId().equals(((OTAttributeMapping) obj).getId());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends OTPrmMapping.Builder<Builder> {

        private OTAttributeMappingType type;
        private String detectorProperty;
        private String refinementProperty;

        public Builder() {
            super();
        }

        public Builder(String id) {
            super(id);
        }

        public Builder setType(OTAttributeMappingType type) {
            this.type = type;
            return self();
        }

        public Builder setDetectorProperty(String detectorProperty) {
            this.detectorProperty = detectorProperty;
            return self();
        }

        public Builder setRefinementProperty(String refinementProperty) {
            this.refinementProperty = refinementProperty;
            return self();
        }

        public OTAttributeMapping build() {
            return new OTAttributeMapping(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
