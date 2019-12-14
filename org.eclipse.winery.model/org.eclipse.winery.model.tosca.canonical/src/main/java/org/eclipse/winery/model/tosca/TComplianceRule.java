/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tComplianceRule")
public class TComplianceRule extends HasId implements HasName, HasTargetNamespace {

    @XmlAttribute
    protected String name;

    @XmlAttribute(name = "targetNamespace")
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlElement(name = "Identifier")
    protected TTopologyTemplate identifier;

    @XmlElement(name = "RequiredStructure")
    protected TTopologyTemplate requiredStructure;

    @XmlElement(name = "Tags")
    protected TTags tags;

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

    public TTags getTags() {
        return tags;
    }

    public void setTags(TTags tags) {
        this.tags = tags;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
