/*******************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.lsp.Server.ServerCore.TOSCAFunctions;

import java.util.regex.Pattern;
import java.util.List;

public class BooleanLogicFunctions {
    
    public static boolean and(List<Boolean> args) {
        for (Boolean arg : args) {
            if (!arg) {
                return false;
            }
        }
        return true;
    }

    public static boolean or(List<Boolean> args) {
        for (Boolean arg : args) {
            if (arg) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean not(boolean arg) {
        return !arg;
    }

    public static boolean xor(boolean arg1, boolean arg2) {
        return arg1 != arg2;
    }

    // Comparison Functions

    public static <T> boolean equal(T arg1, T arg2) {
        return arg1.equals(arg2);
    }

    public static <T extends Comparable<T>> boolean greater_than(T arg1, T arg2) {
        return arg1.compareTo(arg2) > 0;
    }

    public static <T extends Comparable<T>> boolean greater_or_equal(T arg1, T arg2) {
        return arg1.compareTo(arg2) >= 0;
    }

    public static <T extends Comparable<T>> boolean less_than(T arg1, T arg2) {
        return arg1.compareTo(arg2) < 0;
    }

    public static <T extends Comparable<T>> boolean less_or_equal(T arg1, T arg2) {
        return arg1.compareTo(arg2) <= 0;
    }

    public static <T> boolean valid_values(Object arg1, List<?> arg2) {
        
        return arg2.contains(arg1);
    }

    public static boolean matches(String arg1, String arg2) {
        return Pattern.matches(arg2, arg1);
    }
    
}
