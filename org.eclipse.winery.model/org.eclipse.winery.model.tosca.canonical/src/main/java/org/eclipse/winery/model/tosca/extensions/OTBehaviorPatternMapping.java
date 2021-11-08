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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.extensions.kvproperties.OTPropertyKV;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otBehaviorPatternMapping")
public class OTBehaviorPatternMapping extends OTPrmMapping {

    @XmlAttribute(name = "behaviorPattern", required = true)
    @NonNull
    private String behaviorPattern;

    @XmlElement(name = "Property", required = true)
    @NonNull
    private OTPropertyKV property;

    @Deprecated // used for XML deserialization of API request content
    public OTBehaviorPatternMapping() {
    }

    public OTBehaviorPatternMapping(Builder builder) {
        super(builder);
        this.behaviorPattern = builder.behaviorPattern;
        this.property = builder.property;
    }

    public String getBehaviorPattern() {
        return behaviorPattern;
    }

    public void setBehaviorPattern(String behaviorPattern) {
        this.behaviorPattern = behaviorPattern;
    }

    public OTPropertyKV getProperty() {
        return property;
    }

    public void setProperty(OTPropertyKV property) {
        this.property = property;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof OTBehaviorPatternMapping
            && getId().equals(((OTBehaviorPatternMapping) obj).getId());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends OTPrmMapping.Builder<Builder> {

        private String behaviorPattern;
        private OTPropertyKV property;

        public Builder(String id) {
            super(id);
        }

        public OTBehaviorPatternMapping.Builder setBehaviorPattern(String behaviorPattern) {
            this.behaviorPattern = behaviorPattern;
            return self();
        }

        public OTBehaviorPatternMapping.Builder setProperty(OTPropertyKV property) {
            this.property = property;
            return self();
        }

        public OTBehaviorPatternMapping build() {
            return new OTBehaviorPatternMapping(this);
        }

        @Override
        public OTBehaviorPatternMapping.Builder self() {
            return this;
        }
    }
}
