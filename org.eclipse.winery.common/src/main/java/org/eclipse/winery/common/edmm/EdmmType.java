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

public enum EdmmType implements Serializable {

    // component types
    compute("compute"),
    database("database"),
    dbms("dbms"),
    mysql_database("mysql_database"),
    mysql_dbms("mysql_dmbs"),
    software_component("software_component"),
    tomcat("tomcat"),
    web_application("web_application"),
    web_server("web_server"),

    // relation types
    connects_to("connects_to"),
    depends_on("depends_on"),
    hosted_on("hosted_on");

    private final String value;

    EdmmType(String value) {
        this.value = value;
    }

    public static EdmmType fromValue(String v) {
        for (EdmmType c : EdmmType.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String getValue() {
        return this.value;
    }
}
