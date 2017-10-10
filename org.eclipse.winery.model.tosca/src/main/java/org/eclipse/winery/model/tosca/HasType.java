/**
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.model.tosca;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface HasType {

    /**
     * This method is necessary, because the XSD schema sometimes uses "type", sometimes "implementedType", ... This
     * here tries to bring in some consistency.
     *
     * @return the QName of the type with full namespace, never null (according to spec)
     */
    @XmlTransient
    @JsonIgnore
    QName getTypeAsQName();

    /**
     * Sets the type and directly persists the resource
     */
    void setType(QName type);

    /**
     * Calls setType(QName) with QName.valueOf(typeStr)
     *
     * Directly persists the resource
     *
     * @param typeStr a textual representation of a QName
     */
    default void setType(String typeStr) {
        this.setType(QName.valueOf(typeStr));
    }
}
