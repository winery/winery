/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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

import javax.xml.bind.annotation.XmlEnumValue;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public enum XOTPrmModelElementType implements Serializable {

    @XmlEnumValue("node")
    NODE("node"),
    @XmlEnumValue("relation")
    RELATION("relation");

    private final String value;

    XOTPrmModelElementType(String v) {
        value = v;
    }

    @NonNull
    public static XOTPrmModelElementType fromValue(String v) {
        for (XOTPrmModelElementType c : XOTPrmModelElementType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    @Nullable
    public String value() {
        return value;
    }
}
