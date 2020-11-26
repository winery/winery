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

package org.eclipse.winery.model.tosca;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPropertyConstraint")
public class TPropertyConstraint extends TConstraint {

    @XmlAttribute(name = "property", required = true)
    protected String property;

    @Deprecated // used for XML deserialization of API request content
    public TPropertyConstraint() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPropertyConstraint)) return false;
        if (!super.equals(o)) return false;
        TPropertyConstraint that = (TPropertyConstraint) o;
        return Objects.equals(property, that.property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), property);
    }

    @NonNull
    public String getProperty() {
        return property;
    }

    public void setProperty(@NonNull String value) {
        this.property = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
