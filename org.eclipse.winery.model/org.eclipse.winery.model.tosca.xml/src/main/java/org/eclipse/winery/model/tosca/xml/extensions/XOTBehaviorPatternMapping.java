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

package org.eclipse.winery.model.tosca.xml.extensions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otBehaviorPatternMapping")
public class XOTBehaviorPatternMapping extends XOTPrmMapping {

    @XmlAttribute(name = "behaviorPattern", required = true)
    @NonNull
    private String behaviorPattern;

    @XmlElement(name = "Property", required = true)
    @NonNull
    private XOTPropertyKV property;

    @Deprecated // used for XML deserialization of API request content
    public XOTBehaviorPatternMapping() {
    }

    public XOTBehaviorPatternMapping(Builder builder) {
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

    public XOTPropertyKV getProperty() {
        return property;
    }

    public void setProperty(XOTPropertyKV property) {
        this.property = property;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof XOTBehaviorPatternMapping
            && getId().equals(((XOTBehaviorPatternMapping) obj).getId());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends XOTPrmMapping.Builder<Builder> {

        private String behaviorPattern;
        private XOTPropertyKV property;

        public Builder(String id) {
            super(id);
        }

        public Builder setBehaviorPattern(String behaviorPattern) {
            this.behaviorPattern = behaviorPattern;
            return self();
        }

        public Builder setProperty(XOTPropertyKV property) {
            this.property = property;
            return self();
        }

        public XOTBehaviorPatternMapping build() {
            return new XOTBehaviorPatternMapping(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
