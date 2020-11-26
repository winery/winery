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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;

import org.eclipse.winery.model.tosca.xml.XHasId;
import org.eclipse.winery.model.tosca.xml.XTEntityTemplate;
import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

public abstract class XOTPrmMapping extends XHasId implements Serializable {

    @XmlAttribute(name = "detectorNode", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @NonNull
    private XTEntityTemplate detectorElement;

    @XmlAttribute(name = "refinementNode", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @NonNull
    private XTEntityTemplate refinementElement;

    @Deprecated // required for XML deserialization
    public XOTPrmMapping() { }

    public XOTPrmMapping(Builder builder) {
        super(builder);
        this.detectorElement = builder.detectorElement;
        this.refinementElement = builder.refinementElement;
    }

    public XTEntityTemplate getDetectorElement() {
        return detectorElement;
    }

    public void setDetectorElement(XTEntityTemplate detectorElement) {
        this.detectorElement = detectorElement;
    }

    public XTEntityTemplate getRefinementElement() {
        return refinementElement;
    }

    public void setRefinementElement(XTEntityTemplate refinementElement) {
        this.refinementElement = refinementElement;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static abstract class Builder<T extends Builder<T>> extends XHasId.Builder<T> {
        private XTEntityTemplate detectorElement;
        private XTEntityTemplate refinementElement;

        public Builder() {
            super();
        }
        
        public Builder(String id) {
            super(id);
        }

        public Builder<T> setDetectorElement(XTEntityTemplate detectorElement) {
            this.detectorElement = detectorElement;
            return self();
        }

        public Builder<T> setRefinementElement(XTEntityTemplate refinementElement) {
            this.refinementElement = refinementElement;
            return self();
        }
    }
}
