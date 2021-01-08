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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCondition", propOrder = {
    "any"
})
public class XTCondition implements Serializable {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "expressionLanguage", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String expressionLanguage;

    @Deprecated // required for XML deserialization
    public XTCondition() { }

    public XTCondition(Builder builder) {
        this.any = builder.any;
        this.expressionLanguage = builder.expressionLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTCondition)) return false;
        XTCondition that = (XTCondition) o;
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
            any = new ArrayList<>();
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

    public static class Builder {
        private List<Object> any;
        private String expressionLanguage;

        public Builder setAny(List<Object> any) {
            this.any = any;
            return this;
        }

        public Builder setExpressionLanguage(String expressionLanguage) {
            this.expressionLanguage = expressionLanguage;
            return this;
        }

        public XTCondition build() {
            return new XTCondition(this);
        }
    }

    public void accept(Visitor visitor) {
        visitor.accept(this);
    }
}
