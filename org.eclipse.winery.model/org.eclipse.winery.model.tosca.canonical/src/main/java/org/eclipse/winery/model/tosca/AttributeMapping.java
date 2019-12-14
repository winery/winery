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

import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.Nullable;

public class AttributeMapping extends TPrmMapping {

    @XmlAttribute(name = "type")
    private TAttributeMappingType type;

    @XmlAttribute(name = "detectorProperty")
    @Nullable
    private String detectorProperty;

    @XmlAttribute(name = "refinementProperty")
    @Nullable
    private String refinementProperty;

    public TAttributeMappingType getType() {
        return type;
    }

    public void setType(TAttributeMappingType type) {
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
        return obj instanceof AttributeMapping
            && getId().equals(((AttributeMapping) obj).getId());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
