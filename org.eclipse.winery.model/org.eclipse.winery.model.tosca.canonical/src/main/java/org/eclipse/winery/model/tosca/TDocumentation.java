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
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.constants.Namespaces;
import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDocumentation", propOrder = {
    "content"
})
public class TDocumentation implements Serializable {

    @XmlMixed
    @XmlAnyElement(lax = true)
    protected List<Object> content;

    @XmlAttribute(name = "source")
    @XmlSchemaType(name = "anyURI")
    protected String source;

    @Nullable
    @XmlAttribute(name = "lang", namespace = Namespaces.W3C_NAMESPACE_URI)
    protected String lang;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TDocumentation)) return false;
        TDocumentation that = (TDocumentation) o;
        return Objects.equals(content, that.content) &&
            Objects.equals(source, that.source) &&
            Objects.equals(lang, that.lang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, source, lang);
    }

    @NonNull
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

    @Nullable
    public String getSource() {
        return source;
    }

    public void setSource(@Nullable String value) {
        this.source = value;
    }

    @Nullable
    public String getLang() {
        return lang;
    }

    public void setLang(@Nullable String value) {
        this.lang = value;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
