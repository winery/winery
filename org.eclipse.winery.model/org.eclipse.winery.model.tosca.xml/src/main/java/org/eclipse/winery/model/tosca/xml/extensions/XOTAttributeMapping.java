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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otAttributeMapping")
public class XOTAttributeMapping extends XOTPrmMapping {

    @XmlAttribute(name = "type")
    private XOTAttributeMappingType type;

    @XmlAttribute(name = "detectorProperty")
    @Nullable
    private String detectorProperty;

    @XmlAttribute(name = "refinementProperty")
    @Nullable
    private String refinementProperty;

    @Deprecated // required for XML deserialization
    public XOTAttributeMapping() { }

    public XOTAttributeMapping(Builder builder) {
        super(builder);
        this.type = builder.type;
        this.detectorProperty = builder.detectorProperty;
        this.refinementProperty = builder.refinementProperty;
    }
    
    public XOTAttributeMappingType getType() {
        return type;
    }

    public void setType(XOTAttributeMappingType type) {
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
        return obj instanceof XOTAttributeMapping
            && getId().equals(((XOTAttributeMapping) obj).getId());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends XOTPrmMapping.Builder<Builder> {

        private XOTAttributeMappingType type;
        private String detectorProperty;
        private String refinementProperty;

        public Builder(String id) {
            super(id);
        }

        public Builder setType(XOTAttributeMappingType type) {
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

        public XOTAttributeMapping build() {
            return new XOTAttributeMapping(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
