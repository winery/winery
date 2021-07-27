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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.XHasIdAndTags;
import org.eclipse.winery.model.tosca.xml.XHasName;
import org.eclipse.winery.model.tosca.xml.XHasTargetNamespace;
import org.eclipse.winery.model.tosca.xml.XTTopologyTemplate;
import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otComplianceRule")
public class XOTComplianceRule extends XHasIdAndTags implements XHasName, XHasTargetNamespace {

    @XmlAttribute
    protected String name;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlElement(name = "Identifier")
    protected XTTopologyTemplate identifier;

    @XmlElement(name = "RequiredStructure")
    protected XTTopologyTemplate requiredStructure;

    @Deprecated // required for XML deserialization
    public XOTComplianceRule() {
    }

    private XOTComplianceRule(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.identifier = builder.identifier;
        this.requiredStructure = builder.requiredStructure;
    }

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

    public XTTopologyTemplate getIdentifier() {
        if (identifier == null) {
            identifier = new XTTopologyTemplate.Builder()
                .build();
        }
        return identifier;
    }

    public void setIdentifier(@Nullable XTTopologyTemplate identifier) {
        this.identifier = identifier;
    }

    public XTTopologyTemplate getRequiredStructure() {
        if (requiredStructure == null) {
            requiredStructure = new XTTopologyTemplate();
        }
        return requiredStructure;
    }

    public void setRequiredStructure(@Nullable XTTopologyTemplate requiredStructure) {
        this.requiredStructure = requiredStructure;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends XHasIdAndTags.Builder<Builder> {

        private String name;
        private XTTopologyTemplate identifier;
        private XTTopologyTemplate requiredStructure;

        public Builder(String id) {
            super(id);
        }

        public Builder setName(String name) {
            this.name = name;
            return self();
        }

        public Builder setIdentifier(XTTopologyTemplate identifier) {
            this.identifier = identifier;
            return self();
        }

        public Builder setRequiredStructure(XTTopologyTemplate requiredStructure) {
            this.requiredStructure = requiredStructure;
            return self();
        }

        public Builder self() {
            return this;
        }

        public XOTComplianceRule build() {
            return new XOTComplianceRule(this);
        }
    }
}
