/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.converter.support;

public class ValueConverter {

    public static ValueConverter INSTANCE = new ValueConverter();

    public String convert(String value) {
        if (value.matches("get_input:.*")) return "{ ".concat(value.trim()).concat(" }");
        return value;
    }

    public String convert(Object value) {
        return convert(value.toString());
    }
}
