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

package org.eclipse.winery.common.edmm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EdmmType implements Serializable {

    // component types
    COMPUTE("compute"),
    DATABASE("database"),
    DBMS("dbms"),
    MYSQL_DATABASE("mysql_database"),
    MYSQL_DBMS("mysql_dbms"),
    SOFTWARE_COMPONENT("software_component"),
    TOMCAT("tomcat"),
    WEB_APPLICATION("web_application"),
    WEB_SERVER("web_server"),

    // relation types
    CONNECTS_TO("connects_to"),
    DEPENDS_ON("depends_on"),
    HOSTED_ON("hosted_on");

    private final String value;

    EdmmType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EdmmType fromValue(String v) {
        for (EdmmType c : EdmmType.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
