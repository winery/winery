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

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

public interface HasType {

    /**
     * This method is necessary, because the XSD schema sometimes uses "type", sometimes "implementedType", ... This
     * here tries to bring in some consistency.
     *
     * @return the QName of the type with full namespace, never null (according to spec)
     */
    @XmlTransient
    QName getTypeAsQName();

    /**
     * Sets the type and directly persists the resource
     */
    void setType(QName type);

    /**
     * Calls setType(QName) with QName.valueOf(typeStr)
     * <p>
     * Directly persists the resource
     *
     * @param typeStr a textual representation of a QName
     */
    default void setType(String typeStr) {
        this.setType(QName.valueOf(typeStr));
    }
}
