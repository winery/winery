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

import java.io.Serializable;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;

import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import org.eclipse.jdt.annotation.NonNull;

public abstract class OTPrmMapping extends HasId implements Serializable {

    @JsonIdentityReference(alwaysAsId = true)
    @XmlAttribute(name = "detectorNode", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @NonNull
    private TEntityTemplate detectorElement;

    @JsonIdentityReference(alwaysAsId = true)
    @XmlAttribute(name = "refinementNode", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @NonNull
    private TEntityTemplate refinementElement;

    @Deprecated // used for XML deserialization of API request content
    public OTPrmMapping() { }

    public OTPrmMapping(Builder builder) {
        super(builder);
        this.detectorElement = builder.detectorElement;
        this.refinementElement = builder.refinementElement;
    }

    public OTPrmMapping(TEntityTemplate detectorElement, TEntityTemplate refinementElement) {
        this.detectorElement = detectorElement;
        this.refinementElement = refinementElement;
        this.setId(UUID.randomUUID().toString());
    }

    public TEntityTemplate getDetectorElement() {
        return detectorElement;
    }

    public void setDetectorElement(TEntityTemplate detectorElement) {
        this.detectorElement = detectorElement;
    }

    public TEntityTemplate getRefinementElement() {
        return refinementElement;
    }

    public void setRefinementElement(TEntityTemplate refinementElement) {
        this.refinementElement = refinementElement;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static abstract class Builder<T extends Builder<T>> extends HasId.Builder<T> {
        private TEntityTemplate detectorElement;
        private TEntityTemplate refinementElement;

        public Builder() {
            super();
        }

        public Builder(String id) {
            super(id);
        }

        public T setDetectorElement(TEntityTemplate detectorElement) {
            this.detectorElement = detectorElement;
            return self();
        }

        public T setRefinementElement(TEntityTemplate refinementElement) {
            this.refinementElement = refinementElement;
            return self();
        }
    }
}
