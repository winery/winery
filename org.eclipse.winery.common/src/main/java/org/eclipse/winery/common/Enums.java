/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.common;

public abstract class Enums {

    /**
     * A utility method for all enums for string to enum conversion
     *
     * @param c     the Enum type
     * @param value value as case insensitive string
     * @return The corresponding enum, or null
     */
    public static <T extends Enum<T>> T valueOf(final Class<T> c, final String value) {
        return valueOf(c, value, null);
    }

    /**
     * A utility method for all enums for string-to-enum conversion
     *
     * @param c            the Enum type
     * @param value        value as case insensitive string
     * @param defaultValue a public value
     * @return The corresponding enum, or null
     */
    public static <T extends Enum<T>> T valueOf(final Class<T> c, final String value, final T defaultValue) {
        if (c != null && value != null) {
            try {
                return Enum.valueOf(c, value.trim().toUpperCase());
            } catch (final IllegalArgumentException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
