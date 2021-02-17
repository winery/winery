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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tExtensions", propOrder = {
    "extension"
})
public class XTExtensions extends XTExtensibleElements {

    @XmlElement(name = "Extension", required = true)
    protected List<XTExtension> extension;

    @Deprecated // required for XML deserialization
    public XTExtensions() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTExtensions)) return false;
        if (!super.equals(o)) return false;
        XTExtensions that = (XTExtensions) o;
        return Objects.equals(extension, that.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), extension);
    }

    @NonNull
    public List<XTExtension> getExtension() {
        if (extension == null) {
            extension = new ArrayList<XTExtension>();
        }
        return this.extension;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
