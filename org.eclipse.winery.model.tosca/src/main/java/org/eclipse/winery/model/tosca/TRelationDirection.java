/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

import javax.xml.bind.annotation.XmlEnumValue;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public enum TRelationDirection implements Serializable {

    @XmlEnumValue("ingoing")
    INGOING("ingoing"),
    @XmlEnumValue("outgoing")
    OUTGOING("outgoing");
    private final String value;

    TRelationDirection(String v) {
        value = v;
    }

    @NonNull
    public static TRelationDirection fromValue(String v) {
        for (TRelationDirection c : TRelationDirection.values()) {
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

