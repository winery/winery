/*******************************************************************************
<<<<<<< HEAD:org.eclipse.winery.model/org.eclipse.winery.model.tosca.xml/src/main/java/org/eclipse/winery/model/tosca/OTAttributeMappingType.java
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
=======
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
>>>>>>> 4a8adcd16... Push xml-model into an xml subpackage to avoid naming collisions with canonical model:org.eclipse.winery.model/org.eclipse.winery.model.tosca.xml/src/main/java/org/eclipse/winery/model/tosca/xml/TAttributeMappingType.java
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

public enum OTAttributeMappingType implements Serializable {

    @XmlEnumValue("all")
    ALL("all"),
    @XmlEnumValue("selective")
    SELECTIVE("selective");

    private final String value;

    OTAttributeMappingType(String value) {
        this.value = value;
    }

    @NonNull
    public static OTAttributeMappingType fromValue(String v) {
        for (OTAttributeMappingType c : OTAttributeMappingType.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    @Nullable
    public String value() {
        return this.value;
    }
}
