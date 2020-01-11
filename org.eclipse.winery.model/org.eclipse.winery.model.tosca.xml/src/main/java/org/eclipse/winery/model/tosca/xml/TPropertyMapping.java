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

package org.eclipse.winery.model.tosca.xml;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPropertyMapping")
public class TPropertyMapping implements Serializable {

    @XmlAttribute(name = "serviceTemplatePropertyRef", required = true)
    @NonNull
    protected String serviceTemplatePropertyRef;

    @XmlAttribute(name = "targetObjectRef", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @NonNull
    protected Object targetObjectRef;

    @XmlAttribute(name = "targetPropertyRef", required = true)
    @NonNull
    protected String targetPropertyRef;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPropertyMapping)) return false;
        TPropertyMapping that = (TPropertyMapping) o;
        return Objects.equals(serviceTemplatePropertyRef, that.serviceTemplatePropertyRef) &&
            Objects.equals(targetObjectRef, that.targetObjectRef) &&
            Objects.equals(targetPropertyRef, that.targetPropertyRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTemplatePropertyRef, targetObjectRef, targetPropertyRef);
    }

    @NonNull
    public String getServiceTemplatePropertyRef() {
        return serviceTemplatePropertyRef;
    }

    public void setServiceTemplatePropertyRef(@NonNull String value) {
        this.serviceTemplatePropertyRef = value;
    }

    @NonNull
    public Object getTargetObjectRef() {
        return targetObjectRef;
    }

    public void setTargetObjectRef(@NonNull Object value) {
        Objects.requireNonNull(value);
        this.targetObjectRef = value;
    }

    @NonNull
    public String getTargetPropertyRef() {
        return targetPropertyRef;
    }

    public void setTargetPropertyRef(@NonNull String value) {
        Objects.requireNonNull(value);
        this.targetPropertyRef = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
