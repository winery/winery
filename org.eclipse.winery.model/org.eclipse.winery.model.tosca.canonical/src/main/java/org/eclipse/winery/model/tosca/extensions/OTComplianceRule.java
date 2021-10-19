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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.HasIdAndTags;
import org.eclipse.winery.model.tosca.HasName;
import org.eclipse.winery.model.tosca.HasTargetNamespace;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "otComplianceRule")
public class OTComplianceRule extends HasIdAndTags implements HasName, HasTargetNamespace {

    @XmlAttribute
    protected String name;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlElement(name = "Identifier")
    protected TTopologyTemplate identifier;

    @XmlElement(name = "RequiredStructure")
    protected TTopologyTemplate requiredStructure;

    @Deprecated // used for XML deserialization of API request content
    public OTComplianceRule() {
    }

    public OTComplianceRule(Builder builder) {
        super(builder);
        this.name = builder.name;
        this.identifier = builder.identifier;
        this.requiredStructure = builder.requiredStructure;
        this.targetNamespace = builder.targetNamespace;
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

    public TTopologyTemplate getIdentifier() {
        if (identifier == null) {
            identifier = new TTopologyTemplate();
        }
        return identifier;
    }

    public void setIdentifier(@Nullable TTopologyTemplate identifier) {
        this.identifier = identifier;
    }

    public TTopologyTemplate getRequiredStructure() {
        if (requiredStructure == null) {
            requiredStructure = new TTopologyTemplate();
        }
        return requiredStructure;
    }

    public void setRequiredStructure(@Nullable TTopologyTemplate requiredStructure) {
        this.requiredStructure = requiredStructure;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends HasIdAndTags.Builder<Builder> {

        private String name;
        private TTopologyTemplate identifier;
        private TTopologyTemplate requiredStructure;
        private String targetNamespace;

        public Builder(String id) {
            super(id);
        }

        public Builder setName(String name) {
            this.name = name;
            return self();
        }

        public Builder setIdentifier(TTopologyTemplate identifier) {
            this.identifier = identifier;
            return self();
        }

        public Builder setRequiredStructure(TTopologyTemplate requiredStructure) {
            this.requiredStructure = requiredStructure;
            return self();
        }

        public Builder setTargetNamespace(String targetNamespace) {
            this.targetNamespace = targetNamespace;
            return self();
        }

        public Builder self() {
            return this;
        }

        public OTComplianceRule build() {
            return new OTComplianceRule(this);
        }
    }
}
