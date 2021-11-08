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
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otRelationMapping")
public class XOTRelationMapping extends XOTPrmMapping {

    @XmlAttribute(name = "relationType")
    private QName relationType;

    @XmlAttribute(name = "direction")
    private XOTRelationDirection direction;

    @XmlAttribute(name = "validSourceOrTarget")
    private QName validSourceOrTarget;

    @Deprecated // required for XML deserialization
    public XOTRelationMapping() { }

    public XOTRelationMapping(Builder builder) {
        super(builder);
        this.relationType = builder.relationType;
        this.direction = builder.direction;
        this.validSourceOrTarget = builder.validSourceOrTarget;
    }

    public QName getRelationType() {
        return relationType;
    }

    public void setRelationType(QName relationType) {
        this.relationType = relationType;
    }

    public XOTRelationDirection getDirection() {
        return direction;
    }

    public void setDirection(XOTRelationDirection direction) {
        this.direction = direction;
    }

    public QName getValidSourceOrTarget() {
        return validSourceOrTarget;
    }

    public void setValidSourceOrTarget(QName validSourceOrTarget) {
        this.validSourceOrTarget = validSourceOrTarget;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof XOTRelationMapping
            && getId().equals(((XOTRelationMapping) obj).getId());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends XOTPrmMapping.Builder<Builder> {

        private QName relationType;
        private XOTRelationDirection direction;
        private QName validSourceOrTarget;

        public Builder(String id) {
            super(id);
        }

        public Builder setRelationType(QName relationType) {
            this.relationType = relationType;
            return self();
        }

        public Builder setDirection(XOTRelationDirection direction) {
            this.direction = direction;
            return self();
        }

        public Builder setValidSourceOrTarget(QName validSourceOrTarget) {
            this.validSourceOrTarget = validSourceOrTarget;
            return self();
        }

        public XOTRelationMapping build() {
            return new XOTRelationMapping(this);
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
