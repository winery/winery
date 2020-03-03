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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCondition", propOrder = {
    "any"
})
public class TCondition implements Serializable {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "expressionLanguage", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String expressionLanguage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TCondition)) return false;
        TCondition that = (TCondition) o;
        return Objects.equals(any, that.any) &&
            Objects.equals(expressionLanguage, that.expressionLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(any, expressionLanguage);
    }

    @NonNull
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    @NonNull
    public String getExpressionLanguage() {
        return expressionLanguage;
    }

    public void setExpressionLanguage(@NonNull String value) {
        Objects.requireNonNull(value);
        this.expressionLanguage = value;
    }

    public void accept(Visitor visitor) {
        visitor.accept(this);
    }
}
